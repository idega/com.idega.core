package com.idega.util.database;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.idega.data.DatastoreConnection;
import com.idega.util.LogWriter;

/**
 * <code>ConnectionPool</code> is the default ConnectilPool implementation for
 * idegaWeb. The connectionpool handles retrieving and putting back java.sql.
 * Connection objects. This class should in most cases not be used on its own
 * but rather access Connections through <code>ConnectionBroker</code>.<br><br>
 * 
 * The Connectionpool reads its properties through a properties file (db.
 * properties) and supports the following attributes.
 * 
 * drivers=[JDBCdriverClassName] This must be set for the pool to initialize
 * correctly with the correct Driver class.<br><br>
 * 
 * All connection properties must be prefixed with the name of the pool
 * [poolname] and the default pool name is "default".<br><br>
 * 
 * [poolname].url The fully qualified JDBC URL to the datastore<br>
 * [poolname].user The user name to log into the databstore<br>
 * [poolname].password The password to log into the databstore<br> 
 * [poolname].initconns The number of connections that the pool should hold
 * initially<br> 
 * [poolname].maxconns The number of connections that the pool should hold
 * maximally at any time<br>
 * [poolname].loglevel The level of logging for the pool. Possible values
 * are: 0 (NONE - no logging), 1 (INFO - only information messages), 2
 * (ERROR - show error messages), 3 (DEBUG - show all info,error and debug
 * messages). The default is 1 (INFO).<br> 
 * 
 * [poolname].logintimeout The timout (in
 * seconds) that the pool will wait for a connection before throwing an
 * SQLException. The default is 5.<br>
 *  [poolname]. refreshminutes The number of
 * minutes for the pool to refresh itself and load new connections. The
 * default is 20.<br>
 *<br>
 * This software is the proprietary information of Idega hf. Use is subject
 *to license terms.
 * 
 *@author Originally by <a href="http://www.wrox.com">wrox</a> modified by <a
 *href="mailto: tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.3
 */
public class ConnectionPool
{
	/**
	 * Constructor ConnectionPool.
	 * @param poolName
	 * @param url
	 * @param user
	 * @param password
	 * @param max
	 * @param init
	 * @param timeOut
	 * @param pw
	 * @param logLevel
	 * @param lRefreshIntervalMillis
	 */
	public ConnectionPool(
		String poolName,
		String url,
		String user,
		String password,
		int max,
		int init,
		int timeOut,
		PrintWriter pw,
		int logLevel) {
			this(poolName,url,user,password,max,init,timeOut,pw,logLevel,-1);			
	}
	private String name;
	private String URL;
	private String user;
	private String password;
	private int minConns;
	private int maxConns;
	private int timeOut;
	private LogWriter logWriter;
	private long lConnectionTimeOut = 10 * 60 * 1000; //10 minutes
	private Map checkedOutInfoMap;
	//private long lastRefresh;
	//private final long refreshIntervalMillis = 1 * 60 * 1000;
	private long refreshIntervalMillis = 20 * 60 * 1000;

	private ConnectionRefresher refresher;
	//private int checkedOut;
	private Vector freeConnections = new Vector();
	private static String DEBUG_RETURNED_CONNECTION = "Returned connection to correct pool";
	private static String DEBUG_REQUESTING_CONNECTION = "Requested connection";
	public ConnectionPool(
		String name,
		String URL,
		String user,
		String password,
		int maxConns,
		int initConns,
		int timeOut,
		PrintWriter pw,
		int logLevel,
		long refreshIntervalMilis)
	{
		this.name = name;
		this.URL = URL;
		this.user = user;
		this.password = password;
		if(refreshIntervalMilis!=-1){
			this.refreshIntervalMillis=refreshIntervalMilis;
		}
		//Special case if using Interbase/Firebird
		if ((URL.indexOf("interbase") != -1) || (URL.indexOf("firebird") != -1))
		{
			this.minConns = 1;
			initConns = 1;
		}
		else
		{
			this.minConns = initConns;
		}
		this.maxConns = maxConns;
		this.timeOut = timeOut > 0 ? timeOut : 5;
		this.logWriter = new LogWriter(name, logLevel, pw);
		initPool(initConns);
		this.logWriter.log("New pool created", LogWriter.INFO);
		String lf = System.getProperty("line.separator");
		this.logWriter.log(
			lf
				+ " url="
				+ URL
				+ lf
				+ " user="
				+ user
				+ lf
				+ " password="
				+ password
				+ lf
				+ " initconns="
				+ initConns
				+ lf
				+ " maxconns="
				+ maxConns
				+ lf
				+ " logintimeout="
				+ this.timeOut,
			LogWriter.DEBUG);
		this.logWriter.log(getStats(), LogWriter.DEBUG);
	}
	public void initializeRefresher(long refreshIntervalMillis)
	{
		if(this.refresher==null){
			this.refresher = new ConnectionRefresher(this, refreshIntervalMillis);
		}
	}
	protected synchronized void refresh()
	{
		this.cleanUpCheckedOut();
		int size = this.freeConnections.size();
		int conns = getCurrentConnectionCount();
		debug("[ConnectionPool.refresh()] : size=" + size + ", conns=" + conns + ", getCheckedOutCount()=" + this.getCheckedOutCount());
		if(conns==0){
			//This should only happen if the datastore has become unreachable
			initPool(this.minConns);	
			return;
		}
		for (int i = 0; i < conns; i++)
		{
			try
			{
				Connection conn = getConnection();
				/*
				*Disabled commiting of connections when refreshing
				*
				try{
				  conn.commit();
				}
				catch(Exception ex){
				  System.err.println("Commit failed for connection in ConnectionPool.refresh()");
				  ex.printStackTrace();
				}*/
				conn.close();
				this.removeFromCheckedOutList(conn);
			}
			catch (Exception ex)
			{
				System.err.println("There was an error in ConnectionPool.refresh() for i=" + i + " \n The error was: " + ex.getMessage());
				System.err.println("Error calling this.getConnection() or connecton.close()");
				ex.printStackTrace(System.err);
			}
			try
			{
				freeConnection(this.newConnection());
				this.debug("Refreshed the databaseConnections for ConnectionPool: " + this.name + " i=" + i + ",size=" + size);
			}
			catch (Exception ex)
			{
				System.err.println("There was an exception in ConnectionPool.refresh() for i=" + i + " \n The exception was: " + ex.getMessage());
				System.err.println("Error calling freeConnection(this.newConnection())");
				ex.printStackTrace(System.err);
			}
			//catch (Error ex)
			//{
			//	System.err.println("There was an Error in ConnectionPool.refresh() for i=" + i + " \n The Error was: " + ex.getMessage());
			//	System.err.println("Error calling freeConnection(this.newConnection())");
			//	ex.printStackTrace(System.err);
			//}
		}
	}
	private void initPool(int initConns)
	{
		//debug still active for now
		// lastRefresh = System.currentTimeMillis();
		initializeRefresher(this.refreshIntervalMillis);
		//
		for (int i = 0; i < initConns; i++)
		{
			try
			{
				Connection pc = newConnection();
				this.freeConnections.addElement(pc);
			}
			catch (SQLException e)
			{
			}
		}
	}
	public Connection getConnection() throws SQLException
	{
		this.logWriter.log(DEBUG_REQUESTING_CONNECTION, LogWriter.DEBUG);
		try
		{
			return getConnection(this.timeOut * 1000);
		}
		catch (SQLException e)
		{
			this.logWriter.log(e, "Exception getting connection", LogWriter.ERROR);
			throw e;
		}
	}
	private synchronized Connection getConnection(long timeout) throws SQLException
	{
		// Get a pooled Connection from the cache or a new one.
		// Wait if all are checked out and the max limit has
		// been reached.
		long startTime = System.currentTimeMillis();
		long remaining = timeout;
		Connection conn = null;
		while ((conn = getPooledConnection()) == null)
		{
			try
			{
				this.logWriter.log("Waiting for connection. Timeout=" + remaining, LogWriter.DEBUG);
				wait(remaining);
			}
			catch (InterruptedException e)
			{
			}
			remaining = timeout - (System.currentTimeMillis() - startTime);
			if (remaining <= 0)
			{
				// Timeout has expired
				this.logWriter.log("Time-out while waiting for connection", LogWriter.DEBUG);
				throw new SQLException("getConnection() timed-out");
			}
		}
		// Check if the Connection is still OK
		if (!isConnectionOK(conn))
		{
			// It was bad. Try again with the remaining timeout
			try
			{
				conn.close();
			}
			catch (SQLException ex)
			{
			}
			this.logWriter.log("Removed selected bad connection from pool", LogWriter.ERROR);
			return getConnection(remaining);
		}
		//checkedOut++;
		this.addToCheckedOutList(conn);
		//debug
		//logWriter.log("Delivered connection from pool", LogWriter.INFO);
		//logWriter.log(getStats(), LogWriter.DEBUG);
		return conn;
	}
	/*private boolean isConnectionOK(Connection conn)
	
	{
	
	   Statement testStmt = null;
	
	   try
	
	   {
	
	      if (!conn.isClosed())
	
	      {
	
	         // Try to createStatement to see if it's really alive
	
	         testStmt = conn.createStatement();
	
	         testStmt.close();
	
	      }
	
	      else
	
	      {
	
	         return false;
	
	      }
	
	   }
	
	   catch (SQLException e)
	
	   {
	
	      if (testStmt != null)
	
	      {
	
	         try
	
	         {
	
	            testStmt.close();
	
	         }
	
	         catch (SQLException se)
	
	         { }
	
	      }
	
	      logWriter.log(e, "Pooled Connection was not okay",
	
	                        LogWriter.ERROR);
	
	      return false;
	
	   }
	
	   return true;
	
	}*/
	private boolean isConnectionOK(Connection conn)
	{
		//try{
		return ((DatastoreConnection) conn).getDatastoreInterface().isConnectionOK(conn);
		//}
		//catch(SQLException ex){
		//  logWriter.log(ex,ex.getMessage(),LogWriter.DEBUG);
		//  return false;
		//}
	}
	private Connection getPooledConnection() throws SQLException
	{
		Connection conn = null;
		if (this.freeConnections.size() > 0)
		{
			// Pick the first Connection in the Vector
			// to get round-robin usage
			conn = (Connection) this.freeConnections.firstElement();
			this.freeConnections.removeElementAt(0);
		}
		else if (this.maxConns == 0 || this.getCheckedOutCount() < this.maxConns)
		{
			conn = newConnection();
		}
		return conn;
	}
	private Connection newConnection() throws SQLException
	{
		try
		{
			Connection conn = null;
			DatastoreConnection dsc = null;
			if (this.user == null)
			{
				conn = DriverManager.getConnection(this.URL);
				dsc = new DatastoreConnection(conn, this.name);
			}
			else
			{
				conn = DriverManager.getConnection(this.URL, this.user, this.password);
				dsc = new DatastoreConnection(conn, this.name);
			}
			this.logWriter.log("Opened a new connection", LogWriter.INFO);
			return dsc;
		}
		catch (SQLException e)
		{
			throw new com.idega.data.IDONoDatastoreError(e.getMessage());
		}
	}
	public synchronized void freeConnection(Connection conn)
	{
		DatastoreConnection dsconn = (DatastoreConnection) conn;
		String datasourceOfConnection = dsconn.getDatasource();
		if (datasourceOfConnection.equals(this.name))
		{
			// Put the connection at the end of the Vector
			addConnectionToPool(conn);
			//if(checkedOut!=0){
			//checkedOut--;
			//}
			removeFromCheckedOutList(conn);
			notifyAll();
			//debug
			this.logWriter.log(DEBUG_RETURNED_CONNECTION, LogWriter.DEBUG);
			//logWriter.log(getStats(), LogWriter.DEBUG);
		}
		else
		{
			System.out.println(
				"[ATTENTION!!!] Connection returned to wrong pool - was returned to "
					+ this.name
					+ " should have been returned to "
					+ datasourceOfConnection);
			PoolManager.getInstance().freeConnection(datasourceOfConnection, conn);
		}
	}
	private synchronized void addConnectionToPool(Connection conn)
	{
		// Put the connection at the end of the Vector
		this.freeConnections.addElement(conn);
	}
	public synchronized void release()
	{
		if (this.refresher != null) {
			this.refresher.stop();
		}
		this.refresher=null;
		Enumeration allConnections = this.freeConnections.elements();
		while (allConnections.hasMoreElements())
		{
			Connection con = (Connection) allConnections.nextElement();
			try
			{
				con.close();
				this.logWriter.log("Closed connection", LogWriter.INFO);
			}
			catch (SQLException e)
			{
				this.logWriter.log(e, "Couldn't close connection", LogWriter.ERROR);
			}
		}
		this.freeConnections.removeAllElements();
	}
	//  debug
	//  private String getStats() {
	public String getStats()
	{
		return "Total connections: "
			+ (this.getCurrentConnectionCount())
			+ " Available: "
			+ this.freeConnections.size()
			+ " Checked-out: "
			+ this.getCheckedOutCount();
	}
	public String getURL()
	{
		return this.URL;
	}
	public String getUserName()
	{
		return this.user;
	}
	public String getPassword()
	{
		return this.password;
	}
	public Connection recycleConnection(Connection conn)
	{
		try
		{
			conn = newConnection();
			return conn;
		}
		catch (SQLException ex)
		{
			this.logWriter.log(ex, ex.getMessage(), LogWriter.ERROR);
			return null;
		}
	}
	/**
	
	 * Trims the pool so that it has only size number of connections
	
	 */
	public synchronized void trimTo(int size, int minSize, int maxSize)
	{
		//setTimeOut(3*getTimeOut());
		if (size > maxSize)
		{
			size = maxSize;
		}
		if (size < minSize)
		{
			size = minSize;
		}
		int currentConnectionCount = this.getCurrentConnectionCount();
		if (size == 1 && minSize == 1 && maxSize == 1 && (currentConnectionCount == 1))
		{
			this.minConns = minSize;
			this.maxConns = maxSize;
		}
		else
		{
			Vector connections = new Vector();
			try
			{
				int dummy = 0;
				while (this.maxConns > dummy)
				{
					connections.add(getConnection());
					dummy++;
					//System.out.println("ConnectionPool.trimTo() : Getting Connection nr "+dummy+", maxConns="+this.maxConns);
				}
			}
			catch (Exception ex)
			{
				System.out.println("Exception in" + this.getClass().getName());
				ex.printStackTrace();
			}
			try
			{
				//this.checkedOut=size;
				this.minConns = minSize;
				this.maxConns = maxSize;
				for (int i = 0; i < size; i++)
				{
					Connection conn = (Connection) connections.get(0);
					connections.remove(0);
					connections.trimToSize();
					this.freeConnection(conn);
				}
				Iterator iter = connections.iterator();
				while (iter.hasNext())
				{
					Connection item = (Connection) iter.next();
					this.removeFromCheckedOutList(item);
					item.close();
					//System.out.print("Closing database connection");
				}
			}
			catch (Exception ex)
			{
				System.out.println("Exception in " + this.getClass().getName());
				ex.printStackTrace();
			}
			System.out.println("\n---- ConnectionPool trim ---");
			System.out.println("\tDatasource " + this.name + " contains " + getCurrentConnectionCount() + " connections");
			System.out.println("----");
		}
		//setTimeOut((int)getTimeOut()/3);
	}
	/**
	
	 * Enlarges the pool so it has size number of connections
	
	 */
	public synchronized void enlargeTo(int size, int minSize, int maxSize)
	{
		//setTimeOut(getTimeOut()*3);
		if (size > maxSize)
		{
			size = maxSize;
		}
		if (size < minSize)
		{
			size = minSize;
		}
		int currentConnectionCount = this.getCurrentConnectionCount();
		int currentMaxSize = this.getMaximumConnectionCount();
		int currentMinimumSize = this.getMinimumConnectionCount();
		if (currentConnectionCount == 1 && currentMaxSize == 1 && currentMinimumSize == 1 && (size == 1))
		{
			this.minConns = minSize;
			this.maxConns = maxSize;
		}
		else
		{
			Vector connections = new Vector();
			try
			{
				int dummy = 0;
				while (this.maxConns > dummy)
				{
					connections.add(getConnection());
					dummy++;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			int currentconns = connections.size();
			if (currentconns < size)
			{
				int difference = size - currentconns;
				for (int i = 0; i < difference; i++)
				{
					try
					{
						Connection conn = newConnection();
						connections.add(conn);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			try
			{
				this.minConns = minSize;
				this.maxConns = maxSize;
				//this.checkedOut=size;
				for (int i = 0; i < size; i++)
				{
					Connection conn = (Connection) connections.get(0);
					connections.remove(0);
					connections.trimToSize();
					this.freeConnection(conn);
				}
				Iterator iter = connections.iterator();
				while (iter.hasNext())
				{
					Connection item = (Connection) iter.next();
					item.close();
					//System.out.print("Closing database connection");
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			System.out.println("\n---- ConnectionPool enlargement ---");
			System.out.println("\tDatasource " + this.name + " contains " + getCurrentConnectionCount() + " connections");
			System.out.println("----");
		}
		//setTimeOut((int)getTimeOut()/3);
	}
	public DatastoreConnection getDatastoreConnection() throws SQLException
	{
		return (DatastoreConnection) getConnection();
	}
	public int getCurrentConnectionCount()
	{
		//return this.currentConns;
		return this.freeConnections.size() + this.getCheckedOutCount();
	}
	public int getMaximumConnectionCount()
	{
		return this.maxConns;
	}
	public int getMinimumConnectionCount()
	{
		return this.minConns;
	}
	public void setTimeOut(int timeout)
	{
		this.timeOut = timeout;
	}
	public int getTimeOut()
	{
		return this.timeOut;
	}
	private int getCheckedOutCount()
	{
		return getCheckedOutMap().size();
	}
	private Map getCheckedOutMap()
	{
		if (this.checkedOutInfoMap == null)
		{
			this.checkedOutInfoMap = new HashMap();
		}
		return this.checkedOutInfoMap;
	}
	private void addToCheckedOutList(Connection conn)
	{
		getCheckedOutMap().put(conn, new Long(System.currentTimeMillis()));
	}
	private void removeFromCheckedOutList(Connection conn)
	{
		getCheckedOutMap().remove(conn);
	}
	private void cleanUpCheckedOut()
	{
		//debug("Running cleanUpCheckedOut()");
		Iterator iter = getCheckedOutMap().keySet().iterator();
		while (iter.hasNext())
		{
			Connection conn = (Connection) iter.next();
			Long timeCheckedOut = (Long) getCheckedOutMap().get(conn);
			long lCheckedOut = timeCheckedOut.longValue();
			if (lCheckedOut + this.lConnectionTimeOut < System.currentTimeMillis())
			{
				//Throw away the reference to the connection
				//debug("Throwing away timed out connection from out-pool");
				iter.remove();
			}
		}
	}
	protected void debug(String debug)
	{
		this.logWriter.log("[ConnectionPool-Debug] : " + debug, LogWriter.DEBUG);
		//System.out.println("[ConnectionPool-Debug] : "+debug);
	}
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
