package com.idega.idegaweb;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogManager;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.data.EntityControl;
import com.idega.data.IDOContainer;
import com.idega.data.IDOLookupException;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.user.data.GroupRelationType;
import com.idega.user.data.GroupRelationTypeHome;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.database.PoolManager;


/**
 * Title:        IWApplicationStarter
 * Description:  The class that starts up an initializes an idegaWeb Application
 * Copyright: Copyright (c) 2002 idega software
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IWMainApplicationStarter {
	
	IWMainApplication iwma;
	
	public IWMainApplicationStarter(IWMainApplication _iwma){
		iwma=_iwma;
	}
	//debug
	private static String propertiesfile;
	//public PoolManager poolMgr;
	public void startup() {
		sendStartMessage("Initializing IdegaWebStarter");
		startIdegaWebApplication();
	}

	public void shutdown() {
		//poolMgr.release();
		sendShutdownMessage("Destroying IdegaWebStarter");
		endIdegaWebApplication();
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
		this.propertiesfile = file;
		sendStartMessage("Reading Databases from file: " + file);
		sendStartMessage("Starting PoolMan Datastore ConnectionPool");
		//com.codestudio.util.SQLManager.getInstance(file);
	}
	protected void startIdegaDatabasePool(IWMainApplication iwma) {
		String separator = File.separator;
		ConnectionBroker.POOL_MANAGER_TYPE=ConnectionBroker.POOL_MANAGER_TYPE_IDEGA;
		String file = iwma.getPropertiesRealPath()+separator+"db.properties";
		sendStartMessage("Reading Databases from file: "+file);
		sendStartMessage("Starting idega Datastore ConnectionPool");
		PoolManager.getInstance(file);	
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
		
		//IWMainApplication application = new IWMainApplication(this.getServletContext());
		IWMainApplication application = iwma;
		setApplicationVariables(application);
		application.getSettings().setProperty("last_startup", com.idega.util.IWTimestamp.RightNow().toString());
		
		this.startDatabasePool();
		this.startLogManager();
		IWStyleManager iwStyleManager = new IWStyleManager(application);
		iwStyleManager.getStyleSheet();
		sendStartMessage("Starting IWStyleManager");
		registerSystemBeans();
		insertStartData();
		application.startAccessController();
		application.startFileSystem(); //added by Eiki to ensure that ic_file is created before ib_page
		application.loadBundles();
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
						try
						{
							IBOLookup.registerImplementationForBean("com.idega.core.user.data.User","com.idega.core.user.data.OldUserBMPBean");
						}
						catch (ClassNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
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
	}
	
	/**
	 * 
	 */
	private void registerSystemBeans()
	{
		try
		{
			sendStartMessage("Registering System Beans");
			IBOLookup.registerImplementationForBean("com.idega.core.builder.data.ICDomain","com.idega.builder.data.IBDomainBMPBean");
			IBOLookup.registerImplementationForBean("com.idega.core.builder.data.ICPage","com.idega.builder.data.IBPageBMPBean");
			
			IBOLookup.registerImplementationForBean("com.idega.core.builder.business.BuilderService","com.idega.builder.business.IBMainServiceBean");
			IBOLookup.registerImplementationForBean("com.idega.core.file.business.ICFileSystem","com.idega.block.media.business.MediaFileSystemBean");
			
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

			rfregistry.registerRefactoredClass("com.idega.core.data.ICLanaguage","com.idega.core.localisation.data.ICLanaguage");
			rfregistry.registerRefactoredClass("com.idega.core.data.ICLocale","com.idega.core.localisation.data.ICLocale");

			rfregistry.registerRefactoredClass("com.idega.core.data.Address","com.idega.core.location.data.Address");
			rfregistry.registerRefactoredClass("com.idega.core.data.AddressType","com.idega.core.location.data.AddressType");
			rfregistry.registerRefactoredClass("com.idega.core.data.Commune","com.idega.core.location.data.Commune");
			rfregistry.registerRefactoredClass("com.idega.core.data.Country","com.idega.core.location.data.Country");
			rfregistry.registerRefactoredClass("com.idega.core.data.PostalCode","com.idega.core.location.data.PostalCode");
			rfregistry.registerRefactoredClass("com.idega.core.data.Province","com.idega.core.location.data.Province");

			rfregistry.registerRefactoredClass("com.idega.core.data.ICNetwork","com.idega.core.net.data.ICNetwork");
			rfregistry.registerRefactoredClass("com.idega.core.data.ICProtocol","com.idega.core.net.data.ICProtocol");


		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void insertStartData() {
		/*
		 * @todo Move to user plugin system
		 **/
		insertGroupRelationType("GROUP_PARENT");
		insertGroupRelationType("FAM_CHILD");
		insertGroupRelationType("FAM_PARENT");
		insertGroupRelationType("FAM_SPOUSE");
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
		endDatabasePool();
		IWStyleManager iwStyleManager = new IWStyleManager(iwma);
		iwStyleManager.writeStyleSheet();
		iwma.unload();
		sendShutdownMessage("Saving style sheet");
		sendShutdownMessage("Completed");
	}
	public void sendStartMessage(String message) {
		System.out.println("[idegaWeb] : startup : " + message);
	}
	public void sendShutdownMessage(String message) {
		System.out.println("[idegaWeb] : shutdown : " + message);
	}
	
}
