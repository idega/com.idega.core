package com.idega.idegaweb;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.component.data.ICObjectType;
import com.idega.core.component.data.ICObjectTypeHome;
import com.idega.core.user.data.OldUserBMPBean;
import com.idega.core.user.data.User;
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
 * This class is responsible for starting up and initializing an idegaWeb Application
 * Copyright: Copyright (c) 2002-2004 idega software
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IWMainApplicationStarter implements ServletContextListener  {
	
	IWMainApplication iwma;
	private Logger log = Logger.getLogger(IWMainApplicationStarter.class.getName());

	public IWMainApplicationStarter(){
	    
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
		iwma=_iwma;
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
    
    //debug
	private static String propertiesfile;
	//public PoolManager poolMgr;
	public void startup() {
		sendStartMessage("Initializing IdegaWeb");
		startIdegaWebApplication();
	}

	public void shutdown() {
		//poolMgr.release();
		sendShutdownMessage("Stopping IdegaWeb");
		endIdegaWebApplication();
		iwma=null;
		//super.destroy();
	}
	protected void startDatabasePool(){
		String poolType = iwma.getSettings().getProperty(IWMainApplicationSettings.IW_POOLMANAGER_TYPE);
		if (poolType != null) {
			if (poolType.equalsIgnoreCase("POOLMAN")) {
				this.startPoolManDatabasePool(iwma);
			}
			else if (poolType.equalsIgnoreCase("IDEGA")) {
				this.startIdegaDatabasePool(iwma);
			}
			else if (poolType.equalsIgnoreCase("JDBC_DATASOURCE")) {
				this.startJDBCDatasourcePool(iwma);
			}
		}
		else {
			startIdegaDatabasePool(iwma);
		}
	}
	
	protected void startPoolManDatabasePool(IWMainApplication iwma) {
		ConnectionBroker.POOL_MANAGER_TYPE = ConnectionBroker.POOL_MANAGER_TYPE_POOLMAN;
		//ServletContext cont = this.getServletContext();
		String file = "poolman.xml";
		//String file = IWMainApplication.getIWMainApplication(cont).getPropertiesRealPath()+separator+"poolman.xml";
		propertiesfile = file;
		sendStartMessage("Reading Databases from file: " + file);
		sendStartMessage("Starting PoolMan Datastore ConnectionPool");
		//com.codestudio.util.SQLManager.getInstance(file);
	}
	protected void startIdegaDatabasePool(IWMainApplication iwma) {
		String separator = File.separator;
		ConnectionBroker.POOL_MANAGER_TYPE=ConnectionBroker.POOL_MANAGER_TYPE_IDEGA;
		String fileName=null;
		String sfile1 = iwma.getRealPath("/")+"/WEB-INF/idegaweb/properties/db.properties";
		String sfile2= iwma.getPropertiesRealPath()+separator+"db.properties";
		File file1 = new File(sfile1);
		File file2 = new File(sfile2);
		if(file1.exists()){
			fileName=sfile1;
			sendStartMessage("Reading Databases from file: "+fileName);
			sendStartMessage("Starting idega Datastore ConnectionPool");
			PoolManager.getInstance(fileName,iwma);	
		}
		else if(file2.exists()){
			fileName=sfile2;
			sendStartMessage("Reading Databases from file: "+fileName);
			sendStartMessage("Starting idega Datastore ConnectionPool");
			PoolManager.getInstance(fileName,iwma);	
		}
		else{
			sendStartMessage("No db.properties found - setting to databaseless mode and setup mode");
			iwma.setInDatabaseLessMode(true);
			iwma.setInSetupMode(true);
		}
	}
	protected void startJDBCDatasourcePool(IWMainApplication iwma){
		ConnectionBroker.POOL_MANAGER_TYPE=ConnectionBroker.POOL_MANAGER_TYPE_JDBC_DATASOURCE;
		String url = iwma.getSettings().getProperty("JDBC_DATASOURCE_DEFAULT_URL");
		if(url!=null){
			ConnectionBroker.setDefaultJDBCDatasourceURL(url);
		}
		sendStartMessage("Starting JDBC Datastore ConnectionPool from url: "+url);
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
	}
	protected void endIdegaDatabasePool() {
		PoolManager.getInstance().release();
	}
	
	/**
	 * Adds the .jar files in /WEB-INF/classes to the ClassPath
	 */
	protected void addToClassPath() {
		String classPathProperty = "java.class.path";
		String classPath = System.getProperty(classPathProperty);
		StringBuffer classes = new StringBuffer(classPath);
		File webINF = new File(iwma.getRealPath("/WEB-INF/lib"));
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
	private static void addToClassPath(String path) {
		String classPathProperty = "java.class.path";
		String classPath = System.getProperty(classPathProperty);
		classPath += File.pathSeparator;
		classPath += path;
		System.setProperty(classPathProperty, classPath);
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
		iwma.shutdownApplicationServices();
		// enable singletons
		SingletonRepository.start();
		
		//IWMainApplication application = new IWMainApplication(this.getServletContext());
		
		IWMainApplication application = iwma;
		setApplicationVariables(application);
		application.getSettings().setProperty("last_startup", com.idega.util.IWTimestamp.RightNow().toString());
		
		this.startDatabasePool();
		this.startLogManager();
		IWStyleManager iwStyleManager = IWStyleManager.getInstance();
		iwStyleManager.getStyleSheet(application);
		sendStartMessage("Starting IWStyleManager");
		registerSystemBeans();
		if(!application.isInDatabaseLessMode()){
			updateClassReferencesInDatabase();
			updateStartDataInDatabase();
		}
		startTemporaryBundleStarters();
		
		if(!application.isInDatabaseLessMode()){
			application.startAccessController();
		}
		
		if(!application.isInDatabaseLessMode()){
			application.startFileSystem(); //added by Eiki to ensure that ic_file is created before ib_page
		}
		//if(IWMainApplication.USE_JSF){
			application.loadViewManager();
			sendStartMessage("Loaded the ViewManager");
		//}		

		if(!application.isInDatabaseLessMode()){
			application.loadBundles();
		}

		executeServices(application);
		//create ibdomain
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
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

	protected void setApplicationVariables(IWMainApplication application){
	    if (application.getSettings().getIfUsePreparedStatement()) {
			application.getSettings().setUsePreparedStatement(true);
			sendStartMessage("Using prepared statements");
	    }
		if (application.getSettings().getIfDebug()) {
			application.getSettings().setDebug(true);
			sendStartMessage("Debug mode is active");
		}
		if (application.getSettings().getIfAutoCreateStrings()) {
			application.getSettings().setAutoCreateStrings(true);
			sendStartMessage("AutoCreateLocalizedStrings is active");
		}
		if (application.getSettings().getIfAutoCreateProperties()) {
			application.getSettings().setAutoCreateProperties(true);
			sendStartMessage("AutoCreateProperties is active");
		}
		if (application.getSettings().getIfEntityBeanCaching()) {
			IDOContainer.getInstance().setBeanCaching(true);
			sendStartMessage("EntityBeanCaching Active");
		}
		if (application.getSettings().getIfEntityQueryCaching()) {
			IDOContainer.getInstance().setQueryCaching(true);
			sendStartMessage("EntityQueryCaching Active");
		}
		if (application.getSettings().getIfEntityAutoCreate()) {
			EntityControl.setAutoCreationOfEntities(true);
			sendStartMessage("EntityAutoCreation Active");
		}
		else {
			sendStartMessage("EntityAutoCreation Not Active");
		}
		String userSystem = iwma.getSettings().getProperty("IW_USER_SYSTEM");
		if(userSystem!=null){
			if(userSystem.equalsIgnoreCase("OLD")){
				sendStartMessage("Using Old idegaWeb User System");
				LoginBusinessBean.USING_OLD_USER_SYSTEM=true;
				IBOLookup.registerImplementationForBean(User.class,OldUserBMPBean.class);	
			}
		}
		String accControlType = application.getSettings().getProperty(IWMainApplication.IW_ACCESSCONTROL_TYPE_PROPERTY);
		if (accControlType != null) {
			com.idega.presentation.Block.usingNewAcessControlSystem = true;
		}
		String usingEvent = application.getSettings().getProperty(IWMainApplication._PROPERTY_USING_EVENTSYSTEM);
		if (usingEvent != null && !"false".equalsIgnoreCase(usingEvent)) {
			com.idega.presentation.text.Link.usingEventSystem = true;
		}
		String usingNewURLStructure = application.getSettings().getProperty(IWMainApplication.PROPERTY_NEW_URL_STRUCTURE);
		if (usingNewURLStructure != null && !"false".equalsIgnoreCase(usingNewURLStructure)) {
			sendStartMessage("Using new URL Scheme");
			IWMainApplication.USE_NEW_URL_SCHEME=true;
		}
		String usingJSFRendering = application.getSettings().getProperty(IWMainApplication.PROPERTY_JSF_RENDERING);
		if (usingJSFRendering != null && !"false".equalsIgnoreCase(usingJSFRendering)) {
			sendStartMessage("Using JavaServer Faces Runtime");
			IWMainApplication.USE_JSF=true;
		}
	}
	
	
	private void startTemporaryBundleStarters() {
		// start these bundle starters explicitly because some old applications have not registered these bundles and
		// therefore these bundle starters will not start automatically
		startTemporaryBundleStarter("com.idega.block.category.IWBundleStarter");
		startTemporaryBundleStarter("com.idega.block.media.IWBundleStarter");
		startTemporaryBundleStarter("com.idega.builder.IWBundleStarter");
		startTemporaryBundleStarter("is.idega.idegaweb.member.IWBundleStarter");
		startTemporaryBundleStarter("se.cubecon.bun24.viewpoint.IWBundleStarter");
		startTemporaryBundleStarter("se.idega.idegaweb.commune.childcare.IWBundleStarter");
		startTemporaryBundleStarter("se.idega.idegaweb.commune.school.IWBundleStarter");
		startTemporaryBundleStarter("se.idega.idegaweb.commune.accounting.IWBundleStarter");
		startTemporaryBundleStarter("se.idega.idegaweb.commune.school.music.IWBundleStarter");
	}
	
	
	/**
	 * Category bundle is not registered in many web applications (but used) because category wasn't a bundle in the past
	 * Call BundleStarter directly because the bundle is not loaded by old web applications! 
	 */
	private void startTemporaryBundleStarter(String starterName) {
	    IWBundleStartable starter;
        try {
            starter = (IWBundleStartable) Class.forName(starterName).newInstance();
            starter.start(null);
        } catch (InstantiationException e) {
        	sendStartMessage("Info: "+starterName + " could not be instanciated (probably corresponding bundle is not loaded)");
        } catch (IllegalAccessException e) {
        	sendStartMessage("Info: Constructor of "+starterName + " could not be accessed (probably corresponding bundle is not loaded)");
        } catch (ClassNotFoundException e) {
            sendStartMessage("Info: "+starterName + "could not be found (probably corresponding bundle is not loaded)");

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
		rfregistry.registerRefactoredClass("com.idega.core.data.ICCategoryBMPBean","com.idega.core.category.data.ICCategoryBMPBean");
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
		iwma.getSettings().setProperty("last_shutdown", com.idega.util.IWTimestamp.RightNow().toString());
		IWStyleManager iwStyleManager = IWStyleManager.getInstance();
		sendShutdownMessage("Saving style sheet");
		iwStyleManager.writeStyleSheet();
		iwma.unload();
		// some bundle starters have initialized threads that are using the database
		// therefore stop first and then end database pool
		endDatabasePool();
		sendShutdownMessage("Completed");
	}
	public void sendStartMessage(String message) {
		System.out.println("[idegaWeb] : startup : " + message);
	}
	public void sendShutdownMessage(String message) {
		System.out.println("[idegaWeb] : shutdown : " + message);
	}
	
}
