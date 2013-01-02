/*
 * $Id: IWMainApplication.java,v 1.152 2005/11/16 17:59:10 gimmi Exp $
 * Created in 2002 by Tryggvi Larusson
 * 
 * Copyright (C) 2002-2005 Idega software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;


import java.beans.Introspector;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.appserver.AppServer;
import com.idega.core.appserver.AppServerDetector;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.component.business.ComponentRegistry;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectType;
import com.idega.core.component.data.ICObjectTypeHome;
import com.idega.core.contact.data.EmailType;
import com.idega.core.contact.data.EmailTypeHome;
import com.idega.core.user.data.OldUserBMPBean;
import com.idega.core.user.data.User;
import com.idega.data.DatastoreInterface;
import com.idega.data.EntityControl;
import com.idega.data.IDOContainer;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.SingletonRepository;
import com.idega.user.data.GroupRelationBMPBean;
import com.idega.user.data.GroupRelationType;
import com.idega.user.data.GroupRelationTypeHome;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.database.PoolManager;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * This class is responsible for starting up the idegaWeb application,
 * that is initializing the IWMainApplication instance
 * and reading and initializing properties and settings on startup.
 * </p>
 * Copyright: Copyright (c) 2002-2005 idega software<br/>
 * Last modified: $Date: 2005/11/16 17:59:10 $ by $Author: gimmi $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.152 $
 */
public class IWMainApplicationStarter implements ServletContextListener  {
	
	IWMainApplication iwma = null;
	private ServletContext context;
	
	private final Logger log = Logger.getLogger(IWMainApplicationStarter.class.getName());

	public IWMainApplicationStarter(){
		// empty
	}
	
	public IWMainApplicationStarter(ServletContext context){
	    initialize(context);
	}
	/**
     * @param context
     */
    private void initialize(ServletContext context) {
        AppServer appServer = AppServerDetector.setAppServerForApplication(context);
		IWMainApplication _iwma = new IWMainApplication(context,appServer);
		_iwma.setApplicationServer(appServer);
		this.iwma=_iwma;
		this.context=context;
		
		this.startLogManager();
		
		//IWMainApplication iwma = IWMainApplication.getIWMainApplication(getServletContext());
		//sendStartMessage("Initializing IWMainApplicationStarter");
		String serverInfo = context.getServerInfo();
		String appServerName = appServer.getName();
		String appServerVersion = appServer.getVersion();
		if(appServer.isOfficiallySupported()){
			if(appServerVersion!=null){
			    log.fine("Detected supported application server '"+appServerName+"' (version "+appServerVersion+")");
			}
			else{
				log.fine("Detected supported application server '"+appServerName+"' (unknown version)");
			}
		}
		else{
			log.warning("This application server ("+serverInfo+") is not officially supported by idegaWeb");
		}
		startup();
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
        initialize(event.getServletContext());
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
        log.fine("Destroying IWMainApplicationStarter");
        shutdown();
        log.fine("Destroyed IWMainApplicationStarter");
    }
    
	public void startup() {
		log.info("Initializing IdegaWeb");
		try{
			startIdegaWebApplication();
			fireAppStartedEvent();
		}
		catch(Exception re){
			re.printStackTrace();
		}	
	}
	
	protected void fireAppStartedEvent() {		
		ELUtil.getInstance().publishEvent(new IWMainApplicationStartedEvent(this));
	}

	public void shutdown() {
		ELUtil.getInstance().publishEvent(new IWMainApplicationShutdownEvent(this));
		//poolMgr.release();
		log.info("Stopping IdegaWeb");
		endIdegaWebApplication();
		this.iwma=null;
		//super.destroy();
	}
	protected void startDatabasePool(){
		String poolType = this.iwma.getSettings().getPoolManagerType();
		if (poolType != null) {
			if (poolType.equalsIgnoreCase("POOLMAN")) {
				this.startPoolManDatabasePool();
			}
			else if (poolType.equalsIgnoreCase("IDEGA")) {
				startIdegaDatabasePool();
			}
			else if (poolType.equalsIgnoreCase("JDBC_DATASOURCE")) {
				this.startJNDIDatasourcePool();
			}
		}
		else {
			if(this.startIdegaDatabasePool()){
				//db.properties Successful
			}
			else if(startJNDIDatasourcePool()){
				//JNDI Successful
			}
			else{
				log.warning("No Database found - setting to databaseless mode and setup mode");
				this.iwma.setInDatabaseLessMode(true);
				this.iwma.setInSetupMode(true);
			}
		}
		
		Connection conn=null;
		try{
			conn = ConnectionBroker.getConnection();
			DatastoreInterface.getInstance(conn).onApplicationStart(conn);
			ConnectionBroker.freeConnection(conn);
		}
		catch(Exception e){
			log.log(Level.WARNING, "Error starting database pool", e);
		}
		finally{
			if(conn!=null){
				ConnectionBroker.freeConnection(conn);
			}
		}
	}
	
	protected void startPoolManDatabasePool() {
		ConnectionBroker.POOL_MANAGER_TYPE = ConnectionBroker.POOL_MANAGER_TYPE_POOLMAN;
		//ServletContext cont = this.getServletContext();
		//String file = "poolman.xml";
		//String file = IWMainApplication.getIWMainApplication(cont).getPropertiesRealPath()+separator+"poolman.xml";
		//sendStartMessage("Reading Databases from file: " + file);
		//sendStartMessage("Starting PoolMan Datastore ConnectionPool");
		//com.codestudio.util.SQLManager.getInstance(file);
	}
	/**
	 * <p>
	 * Returns true if a db.properties file is found and old style idegaWeb PoolManager is initialized.
	 * </p>
	 */
	protected boolean startIdegaDatabasePool() {
		String separator = File.separator;
		ConnectionBroker.POOL_MANAGER_TYPE=ConnectionBroker.POOL_MANAGER_TYPE_IDEGA;
		String fileName=null;
		String sfile1 = this.iwma.getApplicationRealPath()+"/WEB-INF/idegaweb/properties/db.properties";
		String sfile2= this.iwma.getPropertiesRealPath()+separator+"db.properties";
		String dbPropsFromSystemProperty = System.getProperty(ConnectionBroker.SYSTEM_PROPERTY_DB_PROPERTIES_FILE_PATH);
		if(dbPropsFromSystemProperty!=null && !"".equals(dbPropsFromSystemProperty)){
			sfile1 = dbPropsFromSystemProperty;
			log.info("Trying to load db.properties from system property ("+ConnectionBroker.SYSTEM_PROPERTY_DB_PROPERTIES_FILE_PATH+") :"+dbPropsFromSystemProperty);
		}
		
		File file1 = new File(sfile1);
		File file2 = new File(sfile2);
		
		if(file1.exists()){
			fileName=sfile1;
		}
		else if(file2.exists()){
			fileName=sfile2;
		}
		else{
			log.fine("No db.properties found");
			return false;
		}
		
		log.info("Reading Databases from file: "+fileName);
		log.fine("Starting idega Datastore ConnectionPool");
		PoolManager.unlock();
		PoolManager.getInstance(fileName,this.iwma);	
		
		return true;
	}
	/**
	 * <p>
	 * Returns true if a JNDI DataSource is successfully initialized.
	 * </p>
	 */
	protected boolean startJNDIDatasourcePool(){
		boolean theReturn=false;
		String url = this.iwma.getSettings().getJDBCDatasourceDefaultURL();
		if(url!=null){
			ConnectionBroker.POOL_MANAGER_TYPE=ConnectionBroker.POOL_MANAGER_TYPE_JDBC_DATASOURCE;
			ConnectionBroker.setDefaultJDBCDatasourceURL(url);
		}
		theReturn =  ConnectionBroker.tryDefaultJNDIDataSource();
		if(theReturn){
			log.info("Starting JDBC Datastore ConnectionPool from url: "+ConnectionBroker.getDefaultJNDIUrl());
		}
		return theReturn;
		
	}
	
	public void endDatabasePool() {
		//sendShutdownMessage("Stopping Database Pool");
		try {
			endIdegaDatabasePool();
		}
		catch (Exception e) {
			// ignore
		}
		try {
			endPoolManDatabasePool();
		}
		catch (Exception e) {
			// ignore
		}
	}
	protected void endPoolManDatabasePool() {
		// empty
	}

	protected void endIdegaDatabasePool() {
		PoolManager.getInstance().release();
		int count = Thread.activeCount();
		Thread[] threads = new Thread[count];
		Thread.enumerate(threads);
		for (int i = 0; i < threads.length; i++) {
			Thread thread = threads[i];
			String name = (thread == null) ? null : thread.getName();
			//System.out.println(name);
			if (name != null && name.startsWith("HSQLDB")) {
				thread.interrupt();
			}
		}
	}		

	/**
	 * <p>
	 * This is a method that is called to set database specific properties bafore loading the Jdbc driver and the pool.
	 * </p>
	 */
	protected void setDatabaseProperties(){
		//Currently this only does initialization for Apache Derby:
		Properties p = System.getProperties();
		if(p.get("derby.system.home")==null){
			String dbHome = this.iwma.getApplicationRealPath()+"WEB-INF/derby/";
			p.put("derby.system.home", dbHome);
		}
		String osName = p.get("os.name").toString().toLowerCase();
		if(osName.indexOf("mac os") != -1){
			//this is a hack for the MacOS X 1.4.x VM:
			p.put("derby.storage.fileSyncTransactionLog","true");
		}
	}
	
	/**
	 * Adds the .jar files in /WEB-INF/lib to the ClassPath
	 */
	protected void addToClassPath() {
		String classPathProperty = "java.class.path";
		String classPath = System.getProperty(classPathProperty);
		log.fine("Classpath before adding WEB-INF/lib:\n" + classPath);
		StringBuffer classes = new StringBuffer(classPath);
		File webINF = new File(this.iwma.getApplicationRealPath()+"/WEB-INF/lib");
		File[] subfiles = webINF.listFiles();
		if (subfiles != null) {
			for (int i = 0; i < subfiles.length; i++) {
				if (subfiles[i].isFile()) {
					String fileName = subfiles[i].getAbsolutePath();
					if (fileName.endsWith(".jar")) {
						classes.append(File.pathSeparator);
						classes.append(fileName);
					}
				}
			}
		}
		classPath = classes.toString();
		log.fine("Classpath after adding libs:\n" + classPath);
		System.setProperty(classPathProperty, classPath);
	}
	
	
	public void startIdegaWebApplication() {
		long start = System.currentTimeMillis();

		try {
			addToClassPath();
		}
		catch (Exception e) {
			log.log(Level.WARNING, "Error adding libs to classpath", e);
		}
		// reset singletons
		IWMainApplication.shutdownApplicationServices();
		// enable singletons
		SingletonRepository.start();
		
		this.setDatabaseProperties();
		registerSystemBeans();
		this.startDatabasePool();
		
		// set application variables first before setting any properties (ICApplicationBinding table might be created first)
		setApplicationVariables();
		if (!this.iwma.getSettings().getBoolean("use_debug_mode", false)) {
			this.iwma.regData();
		}
		// now set some properties
		this.iwma.getSettings().setProperty("last_startup", com.idega.util.IWTimestamp.RightNow().toString());

		/*IWStyleManager iwStyleManager = IWStyleManager.getInstance();
		iwStyleManager.getStyleSheet(this.iwma);
		if(iwStyleManager.shouldWriteDownFile()){
			sendStartMessage("Starting IWStyleManager");
		}
		else{
			sendStartMessage("Starting IWStyleManager - writing down style.css is disabled");
		}
		*/		
		// cleaning, maintaining, updating
		if(!this.iwma.isInDatabaseLessMode()){
			log.fine("Cleaning and updating database...");
			//updateClassReferencesInDatabase();
			updateStartDataInDatabase();
			//cleanEmailData();
			log.fine("...cleaning and updating database done");
		}
		startTemporaryBundleStarters();
		
		startComponentRegistry();
		
		if(!this.iwma.isInDatabaseLessMode()){
			this.iwma.startAccessController();
		}
		
		if(!this.iwma.isInDatabaseLessMode()){
			this.iwma.startFileSystem(); //added by Eiki to ensure that ic_file is created before ib_page
		}
		//if(IWMainApplication.USE_JSF){
			try{
				log.fine("Loading the ViewManager...");
				this.iwma.loadViewManager();
				log.fine("...loading ViewManager done");
			}
			catch(Exception e){
				log.log(Level.SEVERE, "Error loading the ViewManager", e);
			}
		//}		

		if(!this.iwma.isInDatabaseLessMode()){
			this.iwma.loadBundles();
		}

		executeServices(this.iwma);
		//create ibdomain
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
		
		// test if all classes are available
		testReferencedClasses();
		log.info("Completed in " + time + " seconds");
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method startComponentRegistry
	 * </p>
	 */
	private void startComponentRegistry() {
		ComponentRegistry.loadRegistry(this.iwma,this.context);
	}

	/**
	 * 
	 */
	private void startLogManager() {
		String propertiesRealPath = this.iwma.getPropertiesRealPath();
		File propertiesFile = new File(propertiesRealPath,"logging.properties");
		boolean propertiesFileExists = propertiesFile.exists();
		if(propertiesFileExists){
			try {
				LogManager.getLogManager().readConfiguration(new FileInputStream(propertiesFile));
			}
			catch (Exception e) {
				log.log(Level.WARNING, "Error reading logging.properties", e);
			}
		}
	}

	protected void setApplicationVariables() {
		// get the factory settings for auto create entities and set EntityControl. 
		// In this way ICApplicationBinding table can be created if necessary and if it is allowed by the factory settings 
		// see call of getIfgetIfEntityAutoCreate() below
		if (this.iwma.getSettings().getFactorySettingsForAutoCreateEntities()) {
			EntityControl.setAutoCreationOfEntities(true);
			log.fine("EntityAutoCreation switched on temporarily (factory settings)");
		}
	    if (this.iwma.getSettings().getIfUsePreparedStatement()) {
			this.iwma.getSettings().setUsePreparedStatement(true);
			log.fine("Using prepared statements");
	    }
		if (this.iwma.getSettings().getIfDebug()) {
			this.iwma.getSettings().setDebug(true);
			log.fine("Debug mode is active");
		}
		if (this.iwma.getSettings().getIfAutoCreateStrings()) {
			this.iwma.getSettings().setAutoCreateStrings(true);
			log.fine("AutoCreateLocalizedStrings is active");
		}
		if (this.iwma.getSettings().getIfAutoCreateProperties()) {
			this.iwma.getSettings().setAutoCreateProperties(true);
			log.fine("AutoCreateProperties is active");
		}
		if (this.iwma.getSettings().getIfEntityBeanCaching()) {
			IDOContainer.getInstance().setBeanCachingActiveByDefault(true);
			log.fine("EntityBeanCaching Active");
		}
		if (this.iwma.getSettings().getIfEntityQueryCaching()) {
			IDOContainer.getInstance().setQueryCaching(true);
			log.fine("EntityQueryCaching Active");
		}
		if (this.iwma.getSettings().getIfEntityAutoCreate()) {
			// now set what the table ICApplicationBinding says
			EntityControl.setAutoCreationOfEntities(true);
			log.fine("EntityAutoCreation Active");
		}
		else {
			log.fine("EntityAutoCreation Not Active");
		}
		String userSystem = this.iwma.getSettings().getProperty("IW_USER_SYSTEM");
		if(userSystem!=null){
			if(userSystem.equalsIgnoreCase("OLD")){
				log.fine("Using Old idegaWeb User System");
				LoginBusinessBean.USING_OLD_USER_SYSTEM=true;
				IBOLookup.registerImplementationForBean(User.class,OldUserBMPBean.class);	
			}
		}
		String accControlType = this.iwma.getSettings().getProperty(IWMainApplication.IW_ACCESSCONTROL_TYPE_PROPERTY);
		if (accControlType == null) {
			com.idega.presentation.Block.usingNewAcessControlSystem = true;
		}
		else if(!accControlType.equals("iw")){
			com.idega.presentation.Block.usingNewAcessControlSystem = false;
		}
		String usingEvent = this.iwma.getSettings().getProperty(IWMainApplication._PROPERTY_USING_EVENTSYSTEM);
		if (usingEvent != null && !"false".equalsIgnoreCase(usingEvent)) {
			com.idega.presentation.text.Link.usingEventSystem = true;
		}
		String usingNewURLStructure = this.iwma.getSettings().getProperty(IWMainApplication.PROPERTY_NEW_URL_STRUCTURE);
		if (usingNewURLStructure != null && "false".equalsIgnoreCase(usingNewURLStructure)) {
			log.fine("NOT Using new URL Scheme");
			IWMainApplication.useNewURLScheme=false;
		}
		else{
			log.fine("Using new URL Scheme");
		}
		String usingJSFRendering = this.iwma.getSettings().getProperty(IWMainApplication.PROPERTY_JSF_RENDERING);
		if (usingJSFRendering != null && "false".equalsIgnoreCase(usingJSFRendering)) {
			log.fine("NOT Using JavaServer Faces Runtime");
			IWMainApplication.useJSF=false;
		}
		else{
			log.fine("Using JavaServer Faces Runtime");
		}
	}
	
	
	private void startTemporaryBundleStarters() {
		// start these bundle starters explicitly because some old applications have not registered these bundles and
		// therefore these bundle starters will not start automatically
		//startTemporaryBundleStarter("com.idega.block.category.IWBundleStarter");
		//startTemporaryBundleStarter("com.idega.block.media.IWBundleStarter");
		//startTemporaryBundleStarter("com.idega.builder.IWBundleStarter");
		//Temporary, should be removed:
		
		/*try {
			Class.forName("com.idega.hibernate.demo.Test").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/
		
		//startTemporaryBundleStarter("is.idega.idegaweb.member.IWBundleStarter");
		//startTemporaryBundleStarter("se.idega.idegaweb.commune.childcare.IWBundleStarter");
		//startTemporaryBundleStarter("se.idega.idegaweb.commune.school.IWBundleStarter");
		//startTemporaryBundleStarter("se.idega.idegaweb.commune.accounting.IWBundleStarter");
		//startTemporaryBundleStarter("is.idega.idegaweb.egov.musicschool.IWBundleStarter");
		//startTemporaryBundleStarter("se.idega.idegaweb.commune.adulteducation.IWBundleStarter");
	}
	
	
	/**
	 * Category bundle is not registered in many web applications (but used) because category wasn't a bundle in the past
	 * Call BundleStarter directly because the bundle is not loaded by old web applications! 
	 */
	/*private void startTemporaryBundleStarter(String starterName) {
	    IWBundleStartable starter;
        try {
            starter = (IWBundleStartable) RefactorClassRegistry.forName(starterName).newInstance();
            starter.start(null);
        }
		catch (InstantiationException e) {
			log.info(starterName + " could not be instanciated (probably corresponding bundle is not loaded)");
		}
		catch (IllegalAccessException e) {
			log.info(" Constructor of " + starterName + " could not be accessed (probably corresponding bundle is not loaded)");
		}
		catch (ClassNotFoundException e) {
			log.info(starterName + " could not be found (probably corresponding bundle is not loaded)");
		}
		catch (Exception e) {
			log.log(Level.WARNING, "Error starting " + starterName, e);
        }
	}*/
	
	
	/**
	 * This is a fix so that these bundle starters are always started
	 */
	private void registerSystemBeans()
	{
	    //TODO: TL: Move this to be registered in a property file.
		RefactorClassRegistry rfregistry = RefactorClassRegistry.getInstance();
		rfregistry.registerRefactoredClass("com.idega.builder.data.IBDomain","com.idega.core.builder.data.ICDomain");
		rfregistry.registerRefactoredClass("com.idega.builder.data.IBPage","com.idega.core.builder.data.ICPage");

		rfregistry.registerRefactoredClass("com.idega.core.data.ICFile","com.idega.core.file.data.ICFile");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICFileType","com.idega.core.file.data.ICFileType");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICFileTypeHandler","com.idega.core.file.data.ICFileTypeHandler");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICFileCategory","com.idega.core.file.data.ICFileCategory");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICMimeType","com.idega.core.file.data.ICMimeType");

		rfregistry.registerRefactoredClass("com.idega.core.data.ICCategory","com.idega.core.category.data.ICCategory");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICCategoryBMPBean","com.idega.block.category.data.ICCategoryBMPBean");
		rfregistry.registerRefactoredClass("com.idega.core.category.data.ICCategoryBMPBean","com.idega.block.category.data.ICCategoryBMPBean");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICCategoryICObjectInstance","com.idega.core.category.data.ICCategoryICObjectInstance");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICCategoryTranslation","com.idega.core.category.data.ICCategoryTranslation");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICInformationCategory","com.idega.core.category.data.ICInformationCategory");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICInformationCategoryBMPBean","com.idega.core.category.data.ICInformationCategoryBMPBean");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICInformationCategoryTranslation","com.idega.core.category.data.ICInformationCategoryTranslation");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICInformationFolder","com.idega.core.category.data.ICInformationFolder");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICInformationFolderBMPBean","com.idega.core.category.data.ICInformationFolderBMPBean");

		rfregistry.registerRefactoredClass("com.idega.core.data.ICObject","com.idega.core.component.data.ICObject");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICObjectField","com.idega.core.component.data.ICObjectField");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICObjectInstance","com.idega.core.component.data.ICObjectInstance");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICObjectType","com.idega.core.component.data.ICObjectType");

		rfregistry.registerRefactoredClass("com.idega.core.data.AreaCode","com.idega.core.contact.data.AreaCode");
		rfregistry.registerRefactoredClass("com.idega.core.data.Email","com.idega.core.contact.data.Email");
		rfregistry.registerRefactoredClass("com.idega.core.data.EmailType","com.idega.core.contact.data.EmailType");
		rfregistry.registerRefactoredClass("com.idega.core.data.Phone","com.idega.core.contact.data.Phone");
		rfregistry.registerRefactoredClass("com.idega.core.data.PhoneType","com.idega.core.contact.data.PhoneType");
		rfregistry.registerRefactoredClass("com.idega.core.data.CountryCode","com.idega.core.contact.data.CountryCode");

		rfregistry.registerRefactoredClass("com.idega.core.data.ICLanguage","com.idega.core.localisation.data.ICLanguage");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICLocale","com.idega.core.localisation.data.ICLocale");

		rfregistry.registerRefactoredClass("com.idega.core.data.Address","com.idega.core.location.data.Address");
		rfregistry.registerRefactoredClass("com.idega.core.data.AddressType","com.idega.core.location.data.AddressType");
		rfregistry.registerRefactoredClass("com.idega.core.data.Commune","com.idega.core.location.data.Commune");
		rfregistry.registerRefactoredClass("com.idega.core.data.Country","com.idega.core.location.data.Country");
		rfregistry.registerRefactoredClass("com.idega.core.data.PostalCode","com.idega.core.location.data.PostalCode");
		rfregistry.registerRefactoredClass("com.idega.core.data.Province","com.idega.core.location.data.Province");

		rfregistry.registerRefactoredClass("com.idega.core.data.ICNetwork","com.idega.core.net.data.ICNetwork");
		rfregistry.registerRefactoredClass("com.idega.core.data.ICProtocol","com.idega.core.net.data.ICProtocol");
		
		// perhaps these entries are not necessary (by thomas)
		rfregistry.registerRefactoredClass("com.idega.builder.handler.PropertyHandler", ICPropertyHandler.class.getName());
		rfregistry.registerRefactoredClass("com.idega.core.builder.data.ICPropertyHandler", ICPropertyHandler.class.getName());
	}

	/*private void updateClassReferencesInDatabase() {
		try {
			ICObjectTypeHome home = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
			home.updateClassReferences("com.idega.builder.handler.PropertyHandler", ICPropertyHandler.class);
			home.updateClassReferences("com.idega.core.builder.data.ICPropertyHandler", ICPropertyHandler.class);
		}
		catch (IDOLookupException e) {
			log.throwing(this.getClass().getName(), "updateClassReferencesInDatabase", e);
		}
		catch (IDOException e) {
			log.throwing(this.getClass().getName(), "updateClassReferencesInDatabase", e);
		}
	}*/
	
	protected void updateStartDataInDatabase() {
		IWStartDataInserter.getInstance().insertStartData();

		updateStartDataGroupRelationType();
		updateStartTypeEmailType();
		updateDomainData();
	}
		
	private void updateDomainData() {
		
		String propertyKey = "dataupdate_domain_done";
		String done = iwma.getSettings().getProperty(propertyKey);
		if(done==null){
			
			ICDomainHome domainHome = null;
			ICDomain defaultDomain = null;
			try {
				domainHome = (ICDomainHome) IDOLookup.getHome(ICDomain.class);
				defaultDomain = domainHome.findDefaultDomain();

			} catch (FinderException e) {
				if(defaultDomain==null){
					try {
						defaultDomain = domainHome.findFirstDomain();
						defaultDomain.setType(ICDomain.TYPE_DEFAULT);
						defaultDomain.store();
						iwma.getSettings().setProperty(propertyKey, "true");
					} catch (FinderException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} catch (IDOLookupException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void updateStartDataGroupRelationType() {
		/*
		 * @todo Move to user plugin system
		 **/
		//Temporary Fix to make sure GroupRelation table exists:
		new GroupRelationBMPBean();
		try {
			ICObjectTypeHome home = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
			home.updateStartData();
		}
		catch (IDOLookupException e) {
			log.throwing(this.getClass().getName(), "updateStartDataGroupRelationType", e);
		}
		catch (IDOException e) {
			log.throwing(this.getClass().getName(), "updateStartDataGroupRelationType", e);
		}
		insertGroupRelationType("GROUP_PARENT");
		insertGroupRelationType("FAM_CHILD");
		insertGroupRelationType("FAM_PARENT");
		insertGroupRelationType("FAM_SPOUSE");
		insertGroupRelationType("FAM_CUSTODIAN");
		insertGroupRelationType("FAM_SIBLING");
	}
	
	private void updateStartTypeEmailType() {
		EmailTypeHome home;
		try {
			home = (EmailTypeHome) IDOLookup.getHome(EmailType.class);
			home.updateStartData();
		}
		catch (IDOLookupException e) {
			log.throwing(this.getClass().getName(), "updateStartTypeEmailType", e);
		}
		catch (IDOException e) {
			log.throwing(this.getClass().getName(), "updateStartTypeEmailType", e);
		}
		catch (CreateException e) {
			log.throwing(this.getClass().getName(), "updateStartTypeEmailType", e);
		}

	}
	
	/*private void cleanEmailData() {
		try {
			IWApplicationContext iwac = this.iwma.getIWApplicationContext();
			UserBusiness userBusiness = (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
			userBusiness.cleanUserEmails();
		}
		catch (IBOLookupException e) {
			log.throwing(this.getClass().getName(), "cleanEmailData", e);
		}
	}*/

	
	private void insertGroupRelationType(String groupRelationType) {
		/**
		 * @todo Move this to a more appropriate place
		 **/
		try {
			GroupRelationTypeHome grtHome = (GroupRelationTypeHome) com.idega.data.IDOLookup.getHome(GroupRelationType.class);
			GroupRelationType grType;
			try {
				grType = grtHome.findByPrimaryKey(groupRelationType);
			}
			catch (FinderException fe) {
				try {
					grType = grtHome.create();
					grType.setType(groupRelationType);
					grType.store();
					log.fine("Registered Group relation type: '" + groupRelationType + "'");
				}
				catch (CreateException e) {
					log.throwing(this.getClass().getName(), "insertGroupRelationType", e);
				}
			}
		}
		catch (IDOLookupException e) {
			log.throwing(this.getClass().getName(), "insertGroupRelationType", e);
		}
	}
	
	/**
	 * Not Implemented fully
	 */
	public void executeServices(IWMainApplication application) {
		List list = application.getSettings().getServiceClasses();
		if (list != null) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				Class theClass = (Class) iter.next();
				try {
					IWService theService = (IWService) theClass.newInstance();
					theService.startService(application);
				}
				catch (Exception ex) {
					log.log(Level.WARNING, "Error starting service " + theClass.getName(), ex);
				}
			}
		}
	}
	public void endIdegaWebApplication() {
		//IWMainApplication application = IWMainApplication.getIWMainApplication(getServletContext());
		//IWMainApplication application = iwma;
		this.iwma.getSettings().setProperty("last_shutdown", com.idega.util.IWTimestamp.RightNow().toString());

		IWStyleManager iwStyleManager = IWStyleManager.getInstance();
		log.fine("Saving style sheet");
		iwStyleManager.writeStyleSheet();
		this.iwma.unloadInstanceAndClass();
		// some bundle starters have initialized threads that are using the database
		// therefore stop first and then end database pool
		endDatabasePool();
		log.fine("Completed");
		Introspector.flushCaches();
	}
	
	private void testReferencedClasses() {
		ICObjectHome home = null;
		try {
			home =(ICObjectHome) IDOLookup.getHome(ICObject.class);
		}
		catch (IDOLookupException ex) {
			log.severe("Could not find home of ICObject");
			return;
		}
		// get the current classloader (it is the same that is used for "Class.forName()" )
		ClassLoader currentClassLoader = getClass().getClassLoader();
		try {
			SortedSet classNames = new TreeSet();
			Collection allICObjects = home.findAll();
			Iterator iterator = allICObjects.iterator();
			while (iterator.hasNext()) {
				ICObject object = (ICObject) iterator.next();
				String className = object.getClassName();
				try {
					// delay initialization, we are not using the class here
					RefactorClassRegistry.forName(className, false, currentClassLoader);
				}
				catch (ClassNotFoundException ex) {
					// bad luck
					classNames.add(className);
					// go ahead
				}
			}
			Iterator classNameIterator = classNames.iterator();
			while (classNameIterator.hasNext()) {
				String className = (String) classNameIterator.next();
				log.warning("Class " + className + " could not be found but is referenced as ICObject");
			}
			
		}
		catch (FinderException ex) {
			log.fine("Could not find any ICObjects");
		}
	}
	
	public IWMainApplication getIWMainApplication() {
		return iwma;
	}
}