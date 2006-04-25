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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.LogManager;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.LogFactory;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.appserver.AppServer;
import com.idega.core.appserver.AppServerDetector;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectType;
import com.idega.core.component.data.ICObjectTypeHome;
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

/**
 * <p>
 * This class is esponsible for starting up the idegaWeb application,
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
	// not used
	//private Logger log = Logger.getLogger(IWMainApplicationStarter.class.getName());

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
		
		//IWMainApplication iwma = IWMainApplication.getIWMainApplication(getServletContext());
		//sendStartMessage("Initializing IWMainApplicationStarter");
		String serverInfo = context.getServerInfo();
		String appServerName = appServer.getName();
		String appServerVersion = appServer.getVersion();
		if(appServer.isOfficiallySupported()){
			if(appServerVersion!=null){
			    sendStartMessage("Detecting Supported Application Server '"+appServerName+"' with version '"+appServerVersion+"'");
			}
			else{
			    sendStartMessage("Detecting Supported Application Server '"+appServerName+"' with unknown version");
			}
		}
		else{
		    sendStartMessage("WARNING! This Application Server ("+serverInfo+") is not officially supported by idegaWeb");
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
        sendShutdownMessage("Destroying IWMainApplicationStarter");
        shutdown();
        sendShutdownMessage("Destroyed IWMainApplicationStarter");
    }
    
	public void startup() {
		sendStartMessage("Initializing IdegaWeb");
		startIdegaWebApplication();
	}

	public void shutdown() {
		//poolMgr.release();
		sendShutdownMessage("Stopping IdegaWeb");
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
				sendStartMessage("No Database found - setting to databaseless mode and setup mode");
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
			e.printStackTrace();
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
		String file = "poolman.xml";
		//String file = IWMainApplication.getIWMainApplication(cont).getPropertiesRealPath()+separator+"poolman.xml";
		sendStartMessage("Reading Databases from file: " + file);
		sendStartMessage("Starting PoolMan Datastore ConnectionPool");
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
		File file1 = new File(sfile1);
		File file2 = new File(sfile2);
		if(file1.exists()){
			fileName=sfile1;
			sendStartMessage("Reading Databases from file: "+fileName);
			sendStartMessage("Starting idega Datastore ConnectionPool");
			PoolManager.unlock();
			PoolManager.getInstance(fileName,this.iwma);	
			return true;
		}
		else if(file2.exists()){
			fileName=sfile2;
			sendStartMessage("Reading Databases from file: "+fileName);
			sendStartMessage("Starting idega Datastore ConnectionPool");
			PoolManager.unlock();
			PoolManager.getInstance(fileName,this.iwma);	
			return true;
		}
		else{
			//sendStartMessage("No db.properties found - setting to databaseless mode and setup mode");
			//iwma.setInDatabaseLessMode(true);
			//iwma.setInSetupMode(true);
			sendStartMessage("No db.properties found");
			return false;
		}
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
			sendStartMessage("Starting JDBC Datastore ConnectionPool from url: "+ConnectionBroker.getDefaultJNDIUrl());
		}
		return theReturn;
		
	}
	
	public void endDatabasePool() {
		//sendShutdownMessage("Stopping Database Pool");
		try {
			endIdegaDatabasePool();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			endPoolManDatabasePool();
		}
		catch (Exception e) {
			e.printStackTrace();
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
	 * Adds the .jar files in /WEB-INF/classes to the ClassPath
	 */
	protected void addToClassPath() {
		String classPathProperty = "java.class.path";
		String classPath = System.getProperty(classPathProperty);
		StringBuffer classes = new StringBuffer(classPath);
		File webINF = new File(this.iwma.getApplicationRealPath()+"/WEB-INF/lib");
		File[] subfiles = webINF.listFiles();
		if (subfiles != null) {
			for (int i = 0; i < subfiles.length; i++) {
				if (subfiles[i].isFile()) {
					String jarEnding = ".jar";
					String fileName = subfiles[i].getAbsolutePath();
					if (fileName.endsWith(jarEnding)) {
						classes.append(File.pathSeparator);
						classes.append(fileName);
					}
				}
			}
		}
		System.setProperty(classPathProperty, classes.toString());
	}
	
	
	public void startIdegaWebApplication() {
		long start = System.currentTimeMillis();
		try {
			addToClassPath();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
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
		this.iwma.regData();
		// now set some properties
		this.iwma.getSettings().setProperty("last_startup", com.idega.util.IWTimestamp.RightNow().toString());

		this.startLogManager();
		IWStyleManager iwStyleManager = IWStyleManager.getInstance();
		iwStyleManager.getStyleSheet(this.iwma);
		if(iwStyleManager.shouldWriteDownFile()){
			sendStartMessage("Starting IWStyleManager");
		}
		else{
			sendStartMessage("Starting IWStyleManager - writing down style.css is disabled");
		}
		
		
		if(!this.iwma.isInDatabaseLessMode()){
			updateClassReferencesInDatabase();
			updateStartDataInDatabase();
		}
		startTemporaryBundleStarters();
		
		if(!this.iwma.isInDatabaseLessMode()){
			this.iwma.startAccessController();
		}
		
		if(!this.iwma.isInDatabaseLessMode()){
			this.iwma.startFileSystem(); //added by Eiki to ensure that ic_file is created before ib_page
		}
		//if(IWMainApplication.USE_JSF){
			try{
				this.iwma.loadViewManager();
				sendStartMessage("Loaded the ViewManager");
			}
			catch(Exception e){
				System.err.println("Error starting the ViewManager: "+e);
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
		sendStartMessage("Completed in " + time + " seconds");
	}
	
	/**
	 * 
	 */
	private void startLogManager() {
		String propertiesRealPath = this.iwma.getPropertiesRealPath();
		File propertiesDir = new File(propertiesRealPath);
		File propertiesFile = new File(propertiesDir,"logging.properties");
		boolean propertiesFileExists = propertiesFile.exists();
		if(propertiesFileExists){
			try {
				LogManager.getLogManager().readConfiguration(new FileInputStream(propertiesFile));
			}
			catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void setApplicationVariables() {
		// get the factory settings for auto create entities and set EntityControl. 
		// In this way ICApplicationBinding table can be created if necessary and if it is allowed by the factory settings 
		// see call of getIfgetIfEntityAutoCreate() below
		if (this.iwma.getSettings().getFactorySettingsForAutoCreateEntities()) {
			EntityControl.setAutoCreationOfEntities(true);
			sendStartMessage("EntityAutoCreation switched on temporarily (factory settings)");
		}
	    if (this.iwma.getSettings().getIfUsePreparedStatement()) {
			this.iwma.getSettings().setUsePreparedStatement(true);
			sendStartMessage("Using prepared statements");
	    }
		if (this.iwma.getSettings().getIfDebug()) {
			this.iwma.getSettings().setDebug(true);
			sendStartMessage("Debug mode is active");
		}
		if (this.iwma.getSettings().getIfAutoCreateStrings()) {
			this.iwma.getSettings().setAutoCreateStrings(true);
			sendStartMessage("AutoCreateLocalizedStrings is active");
		}
		if (this.iwma.getSettings().getIfAutoCreateProperties()) {
			this.iwma.getSettings().setAutoCreateProperties(true);
			sendStartMessage("AutoCreateProperties is active");
		}
		if (this.iwma.getSettings().getIfEntityBeanCaching()) {
			IDOContainer.getInstance().setBeanCaching(true);
			sendStartMessage("EntityBeanCaching Active");
		}
		if (this.iwma.getSettings().getIfEntityQueryCaching()) {
			IDOContainer.getInstance().setQueryCaching(true);
			sendStartMessage("EntityQueryCaching Active");
		}
		if (this.iwma.getSettings().getIfEntityAutoCreate()) {
			// now set what the table ICApplicationBinding says
			EntityControl.setAutoCreationOfEntities(true);
			sendStartMessage("EntityAutoCreation Active");
		}
		else {
			sendStartMessage("EntityAutoCreation Not Active");
		}
		String userSystem = this.iwma.getSettings().getProperty("IW_USER_SYSTEM");
		if(userSystem!=null){
			if(userSystem.equalsIgnoreCase("OLD")){
				sendStartMessage("Using Old idegaWeb User System");
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
			sendStartMessage("NOT Using new URL Scheme");
			IWMainApplication.useNewURLScheme=false;
		}
		else{
			sendStartMessage("Using new URL Scheme");
		}
		String usingJSFRendering = this.iwma.getSettings().getProperty(IWMainApplication.PROPERTY_JSF_RENDERING);
		if (usingJSFRendering != null && "false".equalsIgnoreCase(usingJSFRendering)) {
			sendStartMessage("NOT Using JavaServer Faces Runtime");
			IWMainApplication.useJSF=false;
		}
		else{
			sendStartMessage("Using JavaServer Faces Runtime");
		}
	}
	
	
	private void startTemporaryBundleStarters() {
		// start these bundle starters explicitly because some old applications have not registered these bundles and
		// therefore these bundle starters will not start automatically
		startTemporaryBundleStarter("com.idega.block.category.IWBundleStarter");
		startTemporaryBundleStarter("com.idega.block.media.IWBundleStarter");
		startTemporaryBundleStarter("com.idega.builder.IWBundleStarter");
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
	private void startTemporaryBundleStarter(String starterName) {
	    IWBundleStartable starter;
        try {
            starter = (IWBundleStartable) RefactorClassRegistry.forName(starterName).newInstance();
            starter.start(null);
        } catch (InstantiationException e) {
        	sendStartMessage("Info: "+starterName + " could not be instanciated (probably corresponding bundle is not loaded)");
        } catch (IllegalAccessException e) {
        	sendStartMessage("Info: Constructor of "+starterName + " could not be accessed (probably corresponding bundle is not loaded)");
        } catch (ClassNotFoundException e) {
            sendStartMessage("Info: "+starterName + "could not be found (probably corresponding bundle is not loaded)");
        } catch (Exception e) {
        	sendStartMessage("Info: "+ starterName + "caused the following exception: "+ e.getMessage());
        }
	}
	
	
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

	private void updateClassReferencesInDatabase() {
		try {
			ICObjectTypeHome home = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
			home.updateClassReferences("com.idega.builder.handler.PropertyHandler", ICPropertyHandler.class);
			home.updateClassReferences("com.idega.core.builder.data.ICPropertyHandler", ICPropertyHandler.class);
		} 
		catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (IDOException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void updateStartDataInDatabase() {
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
			e.printStackTrace();
		}
		catch (IDOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insertGroupRelationType("GROUP_PARENT");
		insertGroupRelationType("FAM_CHILD");
		insertGroupRelationType("FAM_PARENT");
		insertGroupRelationType("FAM_SPOUSE");
		insertGroupRelationType("FAM_CUSTODIAN");
		insertGroupRelationType("FAM_SIBLING");
	}
	
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
					sendStartMessage("Registered Group relation type: '" + groupRelationType + "'");
				}
				catch (CreateException ce) {
					ce.printStackTrace();
				}
			}
		}
		catch (IDOLookupException ile) {
			ile.printStackTrace();
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
				Object item = iter.next();
				try {
					Class theClass = (Class) item;
					IWService theService = (IWService) theClass.newInstance();
					theService.startService(application);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	public void endIdegaWebApplication() {
		//IWMainApplication application = IWMainApplication.getIWMainApplication(getServletContext());
		//IWMainApplication application = iwma;
		this.iwma.getSettings().setProperty("last_shutdown", com.idega.util.IWTimestamp.RightNow().toString());

		IWStyleManager iwStyleManager = IWStyleManager.getInstance();
		sendShutdownMessage("Saving style sheet");
		iwStyleManager.writeStyleSheet();
		this.iwma.unloadInstanceAndClass();
		// some bundle starters have initialized threads that are using the database
		// therefore stop first and then end database pool
		endDatabasePool();
		sendShutdownMessage("Completed");
		Introspector.flushCaches();
		LogFactory factory = LogFactory.getFactory();
		if (factory != null) {
			String message = factory.toString();
			factory.release();
			System.out.println("[IWMainApplicationStarter] Following log factory was released: " + message);
		}
		else {
			System.out.println("[IWMainApplicationStarter] LogFactory is already null");
		}
	}
	
	public void sendStartMessage(String message) {
		System.out.println("[idegaWeb] : startup : " + message);
	}
	public void sendShutdownMessage(String message) {
		System.out.println("[idegaWeb] : shutdown : " + message);
	}
	
	private void testReferencedClasses() {
		ICObjectHome home = null;
		try {
			home =(ICObjectHome) IDOLookup.getHome(ICObject.class);
		}
		catch (IDOLookupException ex) {
			sendStartMessage("[IWMainApplicationStarter]: Could not find home of ICObject");
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
				sendStartMessage("[IWMainApplicationStarter] WARNING: Class " + className+" could not be found but is referenced as ICObject");
			}
			
		}
		catch (FinderException ex) {
			sendStartMessage("[IWMainApplicationStarter] Could not find any ICObjects");
		}
	}
}
