//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import java.sql.*;
import com.idega.util.database.*;
import javax.sql.*;
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
                String file = IWMainApplication.getIWMainApplication(this.getServletContext()).getPropertiesRealPath()+separator+"db.properties";
                sendStartMessage("Reading Databases from file: "+file);
                PoolManager poolMgr;
                sendStartMessage("Starting Datastore ConnectionPool");
                poolMgr = PoolManager.getInstance(file);
        }

        public void endDatabasePool(){
          sendShutdownMessage("Stopping Database Pool");
          PoolManager.getInstance().release();
        }

        public void startIdegaWebApplication(){
            IWMainApplication application = new IWMainApplication(this.getServletContext());
            if(application.getSettings().getIfEntityAutoCreate()){
              EntityControl.setAutoCreationOfEntities(true);
              sendStartMessage("EntityAutoCreation Active");
            }
            else{
              sendStartMessage("EntityAutoCreation Not Active");
            }
            startDatabasePool();
            application.loadBundles();

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
          System.out.println("idegaWeb : startup : "+message);
        }

        public void sendShutdownMessage(String message){
          System.out.println("idegaWeb : shutdown : "+message);
        }

}

//-------------
//- End of file
//-------------
