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

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class IWStarterServlet extends GenericServlet
{

	//public PoolManager poolMgr;

	public void init() throws ServletException{
          System.out.println("Initializing IdegaWebStarter");
          startDatabasePool();
          startIdegaWebApplication();
	}


	public void service( ServletRequest _req, ServletResponse _res) throws IOException
	{
            _res.getWriter().println("No Service");
	}


	public void destroy(){
		//poolMgr.release();

		System.out.println("destroying IdegaWebStarter");
                      endIdegaWebApplication();
                      endDatabasePool();
		//super.destroy();
	}


        public void startDatabasePool(){
                // gimmi 06.02.2001
                String separator = System.getProperty("file.separator");
		String file = getServletContext().getRealPath("/")+separator+"db"+separator+"db.properties";
                System.out.println("DB : "+file);
                PoolManager poolMgr;
		//String file = "db/db.properties";
                poolMgr = PoolManager.getInstance(file);
    		//getServletContext().setAttribute("poolmanager",poolMgr);
  		System.out.println("Starting pool");
  		System.out.println(file);
        }

        public void endDatabasePool(){
          PoolManager.getInstance().release();
        }

        public void startIdegaWebApplication(){
            IWMainApplication application = new IWMainApplication(this.getServletContext());
        }


        public void endIdegaWebApplication(){
            IWMainApplication application = IWMainApplication.getIWMainApplication(getServletContext());
            application.unload();
        }



}

//-------------
//- End of file
//-------------
