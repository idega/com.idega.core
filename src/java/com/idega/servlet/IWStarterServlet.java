package com.idega.servlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.servlet.*;

import com.idega.util.database.*;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;
import com.idega.idegaweb.IWService;
import com.idega.data.EntityControl;



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


        public void startDatabasePool(){
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
          PoolManager.getInstance().release();
        }

        /**
         * Adds the .jar files in /WEB-INF/classes to the ClassPath
         */
        protected void addToClassPath(){
          File webINF = new File(this.getServletContext().getRealPath("/WEB-INF/classes"));
          File[] subfiles = webINF.listFiles();
          if(subfiles!=null){
            for (int i = 0; i < subfiles.length; i++) {
              if(subfiles[i].isFile()){
                String jarEnding = ".jar";
                String fileName = subfiles[i].getAbsolutePath();
                if(fileName.endsWith(jarEnding)){
                  addToClassPath(fileName);
                }
              }
            }
          }
        }

        private static void addToClassPath(String path){
            String classPathProperty = "java.class.path";
            String classPath = System.getProperty(classPathProperty,".");
            classPath += File.pathSeparator;
            classPath += path;
            System.setProperty(classPathProperty,classPath);
        }

        public void startIdegaWebApplication(){
            try{
              addToClassPath();
            }
            catch(Exception e){
              e.printStackTrace();
            }
            IWMainApplication application = new IWMainApplication(this.getServletContext());
            if(application.getSettings().getIfEntityAutoCreate()){
              EntityControl.setAutoCreationOfEntities(true);
              sendStartMessage("EntityAutoCreation Active");
            }
            else{
              sendStartMessage("EntityAutoCreation Not Active");
            }
            String accControlType = application.getSettings().getProperty(IWMainApplication.IW_ACCESSCONTROL_TYPE_PROPERTY);
            if(accControlType!=null){
                com.idega.presentation.Block.usingNewAcessControlSystem=true;
            }
            startDatabasePool();
            application.loadBundles();

            application.startAccessControler();

            executeServices(application);
            sendStartMessage("Completed");
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
            application.unload();
            endDatabasePool();
            sendShutdownMessage("Completed");
        }


        public void sendStartMessage(String message){
          System.out.println("[idegaWeb] : startup : "+message);
        }

        public void sendShutdownMessage(String message){
          System.out.println("idegaWeb : shutdown : "+message);
        }

}

//-------------
//- End of file
//-------------
