package com.idega.idegaweb;


import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.idega.data.EntityControl;
import com.idega.data.IDOContainer;
import com.idega.user.data.GroupRelationType;
import com.idega.user.data.GroupRelationTypeHome;
import com.idega.util.FileUtil;
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
	public void starPoolManDatabasePool() {
		ConnectionBroker.POOL_MANAGER_TYPE = ConnectionBroker.POOL_MANAGER_TYPE_POOLMAN;
		//ServletContext cont = this.getServletContext();
		String file = "poolman.xml";
		//String file = IWMainApplication.getIWMainApplication(cont).getPropertiesRealPath()+separator+"poolman.xml";
		this.propertiesfile = file;
		sendStartMessage("Reading Databases from file: " + file);
		sendStartMessage("Starting Datastore ConnectionPool");
		//com.codestudio.util.SQLManager.getInstance(file);
	}
	public void startIdegaDatabasePool() {
		ConnectionBroker.POOL_MANAGER_TYPE = ConnectionBroker.POOL_MANAGER_TYPE_IDEGA;
		String separator = FileUtil.getFileSeparator();
		//ServletContext cont = this.getServletContext();
		
		String file = iwma.getPropertiesRealPath() + separator + "db.properties";
		this.propertiesfile = file;
		sendStartMessage("Reading Databases from file: " + file);
		sendStartMessage("Starting Datastore ConnectionPool");
		PoolManager.getInstance(file);
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
	public void endPoolManDatabasePool() {
	}
	public void endIdegaDatabasePool() {
		PoolManager.getInstance().release();
	}
	/**
	 * Adds the .jar files in /WEB-INF/classes to the ClassPath
	 */
	protected void addToClassPath() {
		String classPathProperty = "java.class.path";
		String classPath = System.getProperty(classPathProperty);
		StringBuffer classes = new StringBuffer(classPath);
		File webINF = new File(iwma.getRealPath("/WEB-INF/classes"));
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
		application.getSettings().setProperty("last_startup", com.idega.util.IWTimestamp.RightNow().toString());
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
		String accControlType = application.getSettings().getProperty(IWMainApplication.IW_ACCESSCONTROL_TYPE_PROPERTY);
		if (accControlType != null) {
			com.idega.presentation.Block.usingNewAcessControlSystem = true;
		}
		String usingEvent = application.getSettings().getProperty(IWMainApplication._PROPERTY_USING_EVENTSYSTEM);
		if (usingEvent != null && !"false".equalsIgnoreCase(usingEvent)) {
			com.idega.presentation.text.Link.usingEventSystem = true;
		}
		String poolType = application.getSettings().getProperty(IWMainApplicationSettings.IW_POOLMANAGER_TYPE);
		if (poolType != null) {
			if (poolType.equalsIgnoreCase("poolman")) {
				this.starPoolManDatabasePool();
			}
			else if (poolType.equalsIgnoreCase("idega")) {
				this.startIdegaDatabasePool();
			}
		}
		else {
			startIdegaDatabasePool();
		}
		IWStyleManager iwStyleManager = new IWStyleManager(application);
		iwStyleManager.getStyleSheet();
		sendStartMessage("Starting IWStyleManager");
		insertStartData();
		application.startAccessController();
		application.createMediaTables(); //added by Eiki to ensure that ic_file is created before ib_page
		application.loadBundles();
		executeServices(application);
		//create ibdomain
		long end = System.currentTimeMillis();
		long time = (end - start) / 1000;
		sendStartMessage("Completed in " + time + " seconds");
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
			GroupRelationTypeHome grtHome =
				(GroupRelationTypeHome) com.idega.data.IDOLookup.getHome(GroupRelationType.class);
			GroupRelationType grType = grtHome.create();
			grType.setType(groupRelationType);
			grType.store();
			sendStartMessage("Registered Group relation type: '" + groupRelationType + "'");
		}
		catch (Exception e) {
			//sendStartMessage("Failed Registering Group relation type: '"+groupRelationType+"'");
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
