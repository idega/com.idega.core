package com.idega.servlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.ThreadContext;
import com.idega.util.database.ConnectionBroker;
/**
 * This servlet is an abstract servlet and is meant to be extended by other servlets in idegaWeb.
* @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
* @version 1.3
*/
public class IWCoreServlet extends HttpServlet 
//implements SingleThreadModel 
{
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
	private boolean synchronizeFirstAccess=true;
	private boolean isFirstAccessor=true;

	/**
	 * This Service method by default synchronizes the first access to it for performance/caching reasons
	 */
	public void service(HttpServletRequest _req, HttpServletResponse _res)
	throws ServletException, IOException{
	    
	    //þetta þyrfti að skila object sem að er síðan syncað á í stað this t.d. IBPage
	    //sami hlutur og er geymdur fyrir checkið. hafa áfram impl. í þessum klasa bara með this
		if(getIfSyncronizeAccess(_req,_res)){
			synchronized(getObjectToSynchronizeOn(_req,_res)){
				unSynchronizedService(_req,_res);
				unSetSyncronizedAccess(_req,_res);
			}
		}
		else{
			unSynchronizedService(_req,_res);
		}
	}
	
	/**
	 * When loading a page for the first time it is synchonized for caching and performance reasons.
	 * Overrid this method if you do not want the whole instance of your servlet to be synchronized.
     * @param request
     * @param response
     * @return
     */
    protected Object getObjectToSynchronizeOn(HttpServletRequest request, HttpServletResponse response) {
        return this;
    }

    /**
	 * The real service method implementation besides synchronization
	 */
	protected void unSynchronizedService(HttpServletRequest _req, HttpServletResponse _res)
	throws ServletException, IOException{
				ThreadContext context = getThreadContext();
		//Thread ct = Thread.currentThread();
		context.putThread(Thread.currentThread());
		super.service(_req,_res);
		context.releaseThread(Thread.currentThread());
	}
	
	/**
	 * This method can be overrided in subclasses. Checks if the current access to service should be synchronized
	 * @param _req
	 * @param _res
	 * @return true if the current service access sould be synchronized, false otherwise.
	 */
	protected boolean getIfSyncronizeAccess(HttpServletRequest _req, HttpServletResponse _res){
		return synchronizeFirstAccess&&isFirstAccessor;
	}
	/**
	 * This method can be overrided in sublcasses. Unsets syncronization for the current access to service.
	 * @param _req 
	 * @param _res
	 * @return true if successfully unSet.
	 */
	protected boolean unSetSyncronizedAccess(HttpServletRequest _req, HttpServletResponse _res){
		return isFirstAccessor=false;
	}
	
	
	public void doGet(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		super.doGet(servReq, servRes);
	}
	public void doPost(HttpServletRequest servReq, HttpServletResponse servRes) throws ServletException, IOException {
		super.doPost(servReq, servRes);
	}
	/*protected void initializeThreadContext(){
	
		if (threadcontext == null){
	
			//threadcontext = new ThreadContext();
	
			threadcontext = ThreadContext.getInstance();
	
		}
	
	}*/
	protected static ThreadContext getThreadContext() {
		//if (threadcontext == null){
		//threadcontext = new ThreadContext();
		//threadcontext = ThreadContext.getInstance();
		//}
		//return threadcontext;
		return ThreadContext.getInstance();
	}
	public static void storeObject(String storeName, Object objectToStore) {
		getThreadContext().setAttribute(Thread.currentThread(), storeName, objectToStore);
		//objects.put(storeName,objectToStore);
	}
	public static Object retrieveObject(String storeName) {
		return getThreadContext().getAttribute(Thread.currentThread(), storeName);
		//return objects.get(storeName);
	}
	public Connection getConnection(String datasourceName) throws SQLException {
		return ConnectionBroker.getConnection(datasourceName);
	}
	public Connection getConnection() throws SQLException {
		return ConnectionBroker.getConnection();
	}
	public void freeConnection(String datasourceName, Connection connection) {
		ConnectionBroker.freeConnection(datasourceName, connection);
	}
	public void freeConnection(Connection connection) {
		ConnectionBroker.freeConnection(connection);
	}
	/**
	
	 * @deprecated replaced with setDatasource
	
	 */
	public void setDBConnectionString(String dbConnectionString) {
		storeObject("DBConnectionString", dbConnectionString);
	}
	public void setDatasource(String datasourceName) {
		storeObject("datasource", datasourceName);
	}
	/**
	
	 * @deprecated replaced with getDatasource
	
	 */
	public String getDBConnectionString() {
		storeObject("connectionRequested", "true");
		return (String) retrieveObject("DBConnectionString");
	}
	public String getDatasource() {
		storeObject("connectionRequested", "true");
		return (String) retrieveObject("datasource");
	}
	public boolean connectionRequested() {
		if (retrieveObject("connectionRequested") == null) {
			return false;
		}
		else {
			return true;
		}
	}
	/**
	
	*Return a home object for an EJB object
	
	    *Not implemented
	
	*/
	public Object getHome(String homeName) {
		return null;
		//not implemented
	}
	/**
	
	*Return the remote interface for an EJB object
	
	    *Not implemented
	
	*/
	public Object getRemoteObject(String ObjectName) {
		return null;
		//not implemented
	}
	public IWMainApplication getApplication() {
		return IWMainApplication.getIWMainApplication(getServletContext());
	}
	public IWMainApplicationSettings getApplicationSettings() {
		return this.getApplication().getSettings();
	}
}
