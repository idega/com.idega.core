package com.idega.servlet;

import com.idega.builder.data.IBDomain;
import com.idega.data.EntityControl;
import com.idega.data.IDOContainer;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWService;
import com.idega.idegaweb.IWStyleManager;
import com.idega.util.FileUtil;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.database.PoolManager;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;



/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWStarterServlet extends GenericServlet
{

  //debug
  private static String propertiesfile;


	//public PoolManager poolMgr;

	public void init() throws ServletException{
	      sendStartMessage("Initializing IdegaWebStarter");
	      startIdegaWebApplication();
	}


	public void service( ServletRequest _req, ServletResponse _res) throws IOException
	{
	    _res.getWriter().println("No Service");
	}


	public void destroy(){
		//poolMgr.release();

		sendShutdownMessage("Destroying IdegaWebStarter");
		      endIdegaWebApplication();

		//super.destroy();
	}

	public void starPoolManDatabasePool(){
		ConnectionBroker.POOL_MANAGER_TYPE=ConnectionBroker.POOL_MANAGER_TYPE_POOLMAN;
		String separator = FileUtil.getFileSeparator();
		ServletContext cont = this.getServletContext();
		String file = "poolman.xml";
		//String file = IWMainApplication.getIWMainApplication(cont).getPropertiesRealPath()+separator+"poolman.xml";
		this.propertiesfile=file;
		sendStartMessage("Reading Databases from file: "+file);
		sendStartMessage("Starting Datastore ConnectionPool");
		//com.codestudio.util.SQLManager.getInstance(file);
	}


	public void startIdegaDatabasePool(){
		ConnectionBroker.POOL_MANAGER_TYPE=ConnectionBroker.POOL_MANAGER_TYPE_IDEGA;
		String separator = FileUtil.getFileSeparator();
		ServletContext cont = this.getServletContext();
		String file = IWMainApplication.getIWMainApplication(cont).getPropertiesRealPath()+separator+"db.properties";
		this.propertiesfile=file;
		sendStartMessage("Reading Databases from file: "+file);
		PoolManager poolMgr;
		sendStartMessage("Starting Datastore ConnectionPool");
		poolMgr = PoolManager.getInstance(file);
	}


	public void endDatabasePool(){
	   sendShutdownMessage("Stopping Database Pool");
	  try{
	    endIdegaDatabasePool();
	  }
	  catch(Exception e){

	  }
	  try{
	    endPoolManDatabasePool();
	  }
	  catch(Exception e){

	  }
	}

	public void endPoolManDatabasePool(){

	}

	public void endIdegaDatabasePool(){
	  PoolManager.getInstance().release();
	}

	/**
	 * Adds the .jar files in /WEB-INF/classes to the ClassPath
	 */
	protected void addToClassPath(){
          String classPathProperty = "java.class.path";
          String classPath = System.getProperty(classPathProperty);
          StringBuffer classes = new StringBuffer(classPath);

	  File webINF = new File(this.getServletContext().getRealPath("/WEB-INF/classes"));
	  File[] subfiles = webINF.listFiles();
	  if(subfiles!=null){
	    for (int i = 0; i < subfiles.length; i++) {
	      if(subfiles[i].isFile()){
		String jarEnding = ".jar";
		String fileName = subfiles[i].getAbsolutePath();
		if(fileName.endsWith(jarEnding)){
		  classes.append(File.pathSeparator);
                  classes.append(fileName);
		}
	      }
	    }
	  }

          System.setProperty(classPathProperty,classes.toString());

	}

	private static void addToClassPath(String path){
	    String classPathProperty = "java.class.path";
	    String classPath = System.getProperty(classPathProperty);
	    classPath += File.pathSeparator;
	    classPath += path;
	    System.setProperty(classPathProperty,classPath);
	}

	public void startIdegaWebApplication(){
		long start = System.currentTimeMillis();
	    try{
	      addToClassPath();
	    }
	    catch(Exception e){
	      e.printStackTrace(System.err);
	    }
	    IWMainApplication application = new IWMainApplication(this.getServletContext());
            application.getSettings().setProperty("last_startup",com.idega.util.IWTimestamp.RightNow().toString());
	    if(application.getSettings().getIfDebug()){
	      application.getSettings().setDebug(true);
	      sendStartMessage("Debug mode is active");
	    }
	    if(application.getSettings().getIfAutoCreateStrings()){
	      application.getSettings().setAutoCreateStrings(true);
	      sendStartMessage("AutoCreateLocalizedStrings is active");
	    }
	    if(application.getSettings().getIfAutoCreateProperties()){
	      application.getSettings().setAutoCreateProperties(true);
	      sendStartMessage("AutoCreateProperties is active");
	    }
	    if(application.getSettings().getIfEntityAutoCreate()){
	      EntityControl.setAutoCreationOfEntities(true);
	      sendStartMessage("EntityAutoCreation Active");
	    }
	    if(application.getSettings().getIfEntityBeanCaching()){
	      IDOContainer.getInstance().setBeanCaching(true);
	      sendStartMessage("EntityBeanCaching Active");
	    }
	    if(application.getSettings().getIfEntityQueryCaching()){
	      IDOContainer.getInstance().setQueryCaching(true);
	      sendStartMessage("EntityQueryCaching Active");
	    }
	    else{
	      sendStartMessage("EntityAutoCreation Not Active");
	    }
	    String accControlType = application.getSettings().getProperty(IWMainApplication.IW_ACCESSCONTROL_TYPE_PROPERTY);
	    if(accControlType!=null){
		com.idega.presentation.Block.usingNewAcessControlSystem=true;
	    }
	    String usingEvent = application.getSettings().getProperty(IWMainApplication._PROPERTY_USING_EVENTSYSTEM);
	    if(usingEvent!=null && !"false".equalsIgnoreCase(usingEvent)){
		com.idega.presentation.text.Link.usingEventSystem=true;
	    }
	    String poolType = application.getSettings().getProperty(IWMainApplicationSettings.IW_POOLMANAGER_TYPE);
	    if(poolType!=null){
	      if(poolType.equalsIgnoreCase("poolman")){
		this.starPoolManDatabasePool();
	      }
	      else if(poolType.equalsIgnoreCase("idega")){
		this.startIdegaDatabasePool();
	      }
	    }
	    else{
	      startIdegaDatabasePool();
	    }
	    
	    IWStyleManager iwStyleManager = new IWStyleManager(application);
	    iwStyleManager.getStyleSheet();
	    sendStartMessage("Starting IWStyleManager");

	    application.startAccessController();
	    application.createMediaTables();//added by Eiki to ensure that ic_file is created before ib_page
	    application.loadBundles();
	    executeServices(application);


	    //create ibdomain
	    IBDomain domain = ((com.idega.builder.data.IBDomainHome)com.idega.data.IDOLookup.getHomeLegacy(IBDomain.class)).createLegacy();
		long end = System.currentTimeMillis();
		long time = (end-start)/1000;
	    sendStartMessage("Completed in "+time+" seconds");
	}

	/**
	 * Not Implemented fully
	 */
	public void executeServices(IWMainApplication application){
	    List list = application.getSettings().getServiceClasses();
	    if(list!=null){
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
		  Object item = iter.next();
		  try{
		    Class theClass = (Class)item;
		    IWService theService = (IWService)theClass.newInstance();
		    theService.startService(application);
		  }
		  catch(Exception ex){
		    ex.printStackTrace();
		  }


		}
	    }
	}

	public void endIdegaWebApplication(){
	    IWMainApplication application = IWMainApplication.getIWMainApplication(getServletContext());
            application.getSettings().setProperty("last_shutdown",com.idega.util.IWTimestamp.RightNow().toString());
	    application.unload();
	    endDatabasePool();
	    IWStyleManager iwStyleManager = new IWStyleManager(application);
	    iwStyleManager.writeStyleSheet();
	    sendShutdownMessage("Saving style sheet");
	    sendShutdownMessage("Completed");
	}


	public void sendStartMessage(String message){
	  System.out.println("[idegaWeb] : startup : "+message);
	}

	public void sendShutdownMessage(String message){
	  System.out.println("[idegaWeb] : shutdown : "+message);
	}

}

//-------------
//- End of file
//-------------
