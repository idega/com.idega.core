package com.idega.util.database;

import java.sql.*;
import java.util.*;
import java.io.*;
import com.idega.util.LogWriter;
import com.idega.data.*;

/**
 *
*@author <a href="http://www.wrox.com">wrox</a> modified by <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
* Class to store database connections
*/
public class ConnectionPool
{

   private String name;
   private String URL;
   private String user;
   private String password;
   private int minConns;
   private int maxConns;
   private int timeOut;
   private LogWriter logWriter;

   //private long lastRefresh;
   private final long refreshIntervalMillis=1000000;
   private ConnectionRefresher refresher;

   private int checkedOut;
   private Vector freeConnections = new Vector();

   public ConnectionPool(String name, String URL, String user,
      String password, int maxConns, int initConns, int timeOut,
      PrintWriter pw, int logLevel)
      {

      this.name = name;
      this.URL = URL;
      this.user = user;
      this.password = password;
      this.minConns = initConns;
      this.maxConns = maxConns;
      this.timeOut = timeOut > 0 ? timeOut : 5;

      logWriter = new LogWriter(name, logLevel, pw);
      initPool(initConns);

      logWriter.log("New pool created", LogWriter.INFO);
      String lf = System.getProperty("line.separator");
      logWriter.log(lf +
                    " url=" + URL + lf +
                    " user=" + user + lf +
                    " password=" + password + lf +
                    " initconns=" + initConns + lf +
                    " maxconns=" + maxConns + lf +
                    " logintimeout=" + this.timeOut, LogWriter.DEBUG);
      logWriter.log(getStats(), LogWriter.DEBUG);
   }

   public void initializeRefresher(long refreshIntervalMillis){
      refresher = new ConnectionRefresher(this,refreshIntervalMillis);
   }

   protected synchronized void refresh(){
      int size = freeConnections.size();
      int conns = this.checkedOut+this.freeConnections.size();
      for(int i=0;i < conns;i++){
          try{
            Connection conn = getConnection();
            try{
              conn.commit();
            }
            catch(Exception ex){
              System.err.println("Commit failed for connection in ConnectionPool.refresh()");
              ex.printStackTrace();
            }
            conn.close();
          }
          catch(SQLException ex){
            System.err.println("There was an error in ConnectionPool.refresh() for i="+i+" \n The error was: "+ex.getMessage());
            System.err.println("Error calling this.getConnection() or connecton.close()");
            ex.printStackTrace(System.err);
          }
          try{
            freeConnection(this.newConnection());
            System.out.println("Refreshed the databaseConnections for ConnectionPool: "+this.name+" i="+i+",size="+size);
          }
          catch(SQLException ex){
            System.err.println("There was an error in ConnectionPool.refresh() for i="+i+" \n The error was: "+ex.getMessage());
            System.err.println("Error calling freeConnection(this.newConnection())");
            ex.printStackTrace(System.err);
          }
      }
   }

   private void initPool(int initConns)
   {
    //debug still active for now
     // lastRefresh = System.currentTimeMillis();
    initializeRefresher(refreshIntervalMillis);

      //
      for (int i = 0; i < initConns; i++)
      {
         try
         {
            Connection pc = newConnection();
            freeConnections.addElement(pc);
         }
         catch (SQLException e)
         { }
      }
   }

   public Connection getConnection() throws SQLException
   {
      logWriter.log("Request for connection received", LogWriter.DEBUG);
      try
      {
         return getConnection(timeOut * 1000);
      }
       catch (SQLException e)
      {
         logWriter.log(e, "Exception getting connection",
                       LogWriter.ERROR);
         throw e;
      }
   }

   private synchronized Connection getConnection(long timeout)
                      throws SQLException
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
            logWriter.log("Waiting for connection. Timeout=" + remaining,
                          LogWriter.DEBUG);
            wait(remaining);
         }
         catch (InterruptedException e)
         { }
         remaining = timeout - (System.currentTimeMillis() - startTime);
         if (remaining <= 0)
         {
            // Timeout has expired
            logWriter.log("Time-out while waiting for connection",
                          LogWriter.DEBUG);
            throw new SQLException("getConnection() timed-out");
         }
      }

      // Check if the Connection is still OK
      if (!isConnectionOK(conn))
      {
         // It was bad. Try again with the remaining timeout
         try{
            conn.close();
         }
         catch(SQLException ex){
         }
         logWriter.log("Removed selected bad connection from pool",
                       LogWriter.ERROR);
         return getConnection(remaining);
      }
      checkedOut++;
      //debug
      //logWriter.log("Delivered connection from pool", LogWriter.INFO);
      logWriter.log(getStats(), LogWriter.DEBUG);
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
   private boolean isConnectionOK(Connection conn){
      //try{
      return ((DatastoreConnection)conn).getDatastoreInterface().isConnectionOK(conn);
      //}
      //catch(SQLException ex){
      //  logWriter.log(ex,ex.getMessage(),LogWriter.DEBUG);
      //  return false;
      //}
   }

   private Connection getPooledConnection() throws SQLException
   {
      Connection conn = null;
      if (freeConnections.size() > 0)
      {
         // Pick the first Connection in the Vector
         // to get round-robin usage
         conn = (Connection) freeConnections.firstElement();
         freeConnections.removeElementAt(0);
      }
      else if (maxConns == 0 || checkedOut < maxConns)
      {
         conn = newConnection();
      }
      return conn;
   }

   private Connection newConnection() throws SQLException
   {
      Connection conn = null;
      DatastoreConnection dsc=null;
      if (user == null) {
         conn = DriverManager.getConnection(URL);
         dsc=new DatastoreConnection(conn);
      }
      else {
         conn = DriverManager.getConnection(URL, user, password);
         dsc=new DatastoreConnection(conn);
      }
      logWriter.log("Opened a new connection", LogWriter.INFO);
      return dsc;
   }

   public synchronized void freeConnection(Connection conn)
   {
      // Put the connection at the end of the Vector
      freeConnections.addElement(conn);
      checkedOut--;
      notifyAll();
     //debug
     //logWriter.log("Returned connection to pool", LogWriter.INFO);
      logWriter.log(getStats(), LogWriter.DEBUG);
   }

   public synchronized void release()
   {
      refresher.stop();
      Enumeration allConnections = freeConnections.elements();
      while (allConnections.hasMoreElements())
      {
         Connection con = (Connection) allConnections.nextElement();
         try
         {
            con.close();
            logWriter.log("Closed connection", LogWriter.INFO);
         }
         catch (SQLException e)
         {
            logWriter.log(e, "Couldn't close connection", LogWriter.ERROR);
         }
      }
      freeConnections.removeAllElements();

   }

//	debug
//   private String getStats() {
public String getStats(){
      return "Total connections: " +
         (freeConnections.size() + checkedOut) +
         " Available: " + freeConnections.size() +
         " Checked-out: " + checkedOut;
}



public String getURL(){
  return URL;
}

public String getUserName(){
  return user;
}

public String getPassword(){
  return password;
}


 public Connection recycleConnection(Connection conn){
    try{
      conn=newConnection();
      return conn;
    }
    catch(SQLException ex){
      logWriter.log(ex,ex.getMessage(),LogWriter.ERROR);
      return null;
    }
 }


 public DatastoreConnection getDatastoreConnection()throws SQLException{
    return (DatastoreConnection) getConnection();
 }

}
