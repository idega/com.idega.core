//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.SingleThreadModel;
import com.idega.util.ThreadContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import java.sql.Connection;
import java.lang.Object;
import java.io.IOException;
import java.sql.SQLException;
import java.lang.String;
import javax.servlet.http.HttpServletResponse;
import com.idega.util.database.ConnectionBroker;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public  class IWCoreServlet extends HttpServlet implements SingleThreadModel{

//public  class IWCoreServlet extends HttpServlet{

//protected Hashtable objects;
//private ThreadContext threadcontext;
//private String dbConnectionString;
//private boolean connectionSent=false;


	/*
	public void init(ServletConfig config)
          throws ServletException{
		super.init(config);
	}*/

	/*public void init()throws ServletException{
		//objects = new Hashtable();
		//initializeThreadContext();
	}*/

	public void service(ServletRequest _req, ServletResponse _res)
	throws ServletException, IOException{
                ThreadContext context = getThreadContext();
		context.putThread(Thread.currentThread());
		super.service(_req,_res);
		/*if (connectionRequested()){
				freeConnection();
		}*/
		context.releaseThread(Thread.currentThread());
	}


	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes)throws ServletException, IOException {
		super.doGet(servReq,servRes);

	}


	public void doPost(HttpServletRequest servReq, HttpServletResponse servRes)throws ServletException, IOException {
		super.doPost(servReq,servRes);
	}


	/*protected void initializeThreadContext(){
		if (threadcontext == null){
			//threadcontext = new ThreadContext();
			threadcontext = ThreadContext.getInstance();
		}
	}*/

	protected static ThreadContext getThreadContext(){
		//if (threadcontext == null){
			//threadcontext = new ThreadContext();
			//threadcontext = ThreadContext.getInstance();
		//}
		//return threadcontext;
	        return ThreadContext.getInstance();
        }

	public static void storeObject(String storeName , Object objectToStore){
		getThreadContext().setAttribute(Thread.currentThread(),storeName,objectToStore);
		//objects.put(storeName,objectToStore);
	}

	public static Object retrieveObject(String storeName){
		return getThreadContext().getAttribute(Thread.currentThread(),storeName);
		//return objects.get(storeName);
	}


	public Connection getConnection(String datasourceName)throws SQLException{
          return ConnectionBroker.getConnection(datasourceName);
	}



	public Connection getConnection()throws SQLException{
		return ConnectionBroker.getConnection();
	}





	public void freeConnection(String datasourceName,Connection connection){
		ConnectionBroker.freeConnection(datasourceName,connection);
	}



	public void freeConnection(Connection connection){
		ConnectionBroker.freeConnection(connection);
	}



        /**
         * @deprecated replaced with setDatasource
         */
	public void setDBConnectionString(String dbConnectionString){
		storeObject("DBConnectionString",dbConnectionString);
	}

	public void setDatasource(String datasourceName){
		storeObject("datasource",datasourceName);
	}

        /**
         * @deprecated replaced with getDatasource
         */
	public String getDBConnectionString(){
		storeObject("connectionRequested","true");
		return (String) retrieveObject("DBConnectionString");
	}

	public String getDatasource(){
		storeObject("connectionRequested","true");
		return (String) retrieveObject("datasource");
	}


	public boolean connectionRequested(){
		if (retrieveObject("connectionRequested") == null){
			return false;
		}
		else{
			return true;
		}
	}

	/**
	*Return a home object for an EJB object
        *Not implemented
	*/
	public Object getHome(String homeName){
		return null;
		//not implemented
	}

	/**
	*Return the remote interface for an EJB object
        *Not implemented
	*/
	public Object getRemoteObject(String ObjectName){
		return null;
		//not implemented
	}


        public IWMainApplication getApplication(){
            return IWMainApplication.getIWMainApplication(getServletContext());
        }

        public IWMainApplicationSettings getApplicationSettings(){
            return this.getApplication().getSettings();
        }
}
