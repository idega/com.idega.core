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



   private long lConnectionTimeOut=10*60*1000;//10 minutes

   private Map checkedOutInfoMap;



   //private long lastRefresh;

   private final long refreshIntervalMillis=20*60*1000;

   private ConnectionRefresher refresher;



   //private int checkedOut;

   private Vector freeConnections = new Vector();



   private static String DEBUG_RETURNED_CONNECTION = "Returned connection to correct pool";

   private static String DEBUG_REQUESTING_CONNECTION = "Requested connection";



   public ConnectionPool(String name, String URL, String user,

      String password, int maxConns, int initConns, int timeOut,

      PrintWriter pw, int logLevel)

      {



      this.name = name;

      this.URL = URL;

      this.user = user;

      this.password = password;



      //Special case if using Interbase

      if(URL.indexOf("interbase")!=-1){

        this.minConns = 1;

        initConns=1;

      }

      else{

        this.minConns = initConns;

      }



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

      this.cleanUpCheckedOut();

      int size = freeConnections.size();

      int conns = getCurrentConnectionCount();



      System.out.println("size="+size+", conns="+conns+", getCheckedOutCount()="+this.getCheckedOutCount());



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

            this.removeFromCheckedOutList(conn);

          }

          catch(Exception ex){

            System.err.println("There was an error in ConnectionPool.refresh() for i="+i+" \n The error was: "+ex.getMessage());

            System.err.println("Error calling this.getConnection() or connecton.close()");

            ex.printStackTrace(System.err);

          }

          try{

            freeConnection(this.newConnection());

            System.out.println("Refreshed the databaseConnections for ConnectionPool: "+this.name+" i="+i+",size="+size);

          }

          catch(Exception ex){

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

      logWriter.log(DEBUG_REQUESTING_CONNECTION, LogWriter.DEBUG);

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

      else if (maxConns == 0 || this.getCheckedOutCount() < maxConns)

      {

         conn = newConnection();



      }

      return conn;

   }



   private Connection newConnection() throws SQLException

   {

    try{

      Connection conn = null;

      DatastoreConnection dsc=null;

      if (user == null) {

         conn = DriverManager.getConnection(URL);

         dsc=new DatastoreConnection(conn,this.name);

      }

      else {

         conn = DriverManager.getConnection(URL, user, password);

         dsc=new DatastoreConnection(conn,this.name);

      }

      logWriter.log("Opened a new connection", LogWriter.INFO);

      return dsc;

    }

    catch(SQLException e){

      throw new com.idega.data.IDONoDatastoreError(e.getMessage());

    }

   }



   public synchronized void freeConnection(Connection conn)

   {



      DatastoreConnection dsconn = (DatastoreConnection)conn;

      String datasourceOfConnection = dsconn.getDatasource();

      if(datasourceOfConnection.equals(this.name)){

        // Put the connection at the end of the Vector

        addConnectionToPool(conn);

        //if(checkedOut!=0){

          //checkedOut--;

        //}

        removeFromCheckedOutList(conn);

        notifyAll();

       //debug

       logWriter.log(DEBUG_RETURNED_CONNECTION, LogWriter.DEBUG);

       //logWriter.log(getStats(), LogWriter.DEBUG);

      }

      else{

        System.out.println("[ATTENTION!!!] Connection returned to wrong pool - was returned to "+this.name+" should have been returned to "+datasourceOfConnection);

        PoolManager.getInstance().freeConnection(datasourceOfConnection,conn);

      }



   }



   private synchronized void addConnectionToPool(Connection conn)

   {

      // Put the connection at the end of the Vector

      freeConnections.addElement(conn);

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



//  debug

//  private String getStats() {

public String getStats(){

      return "Total connections: " +

         (this.getCurrentConnectionCount()) +

         " Available: " + freeConnections.size() +

         " Checked-out: " + this.getCheckedOutCount();

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







 /**

  * Trims the pool so that it has only size number of connections

  */

 public synchronized void trimTo(int size,int minSize,int maxSize){



    //setTimeOut(3*getTimeOut());



    if(size>maxSize){

      size=maxSize;

    }

    if(size<minSize){

      size=minSize;

    }



    int currentConnectionCount = this.getCurrentConnectionCount();



    if(size==1 && minSize == 1 && maxSize == 1 && (currentConnectionCount==1)){

        this.minConns=minSize;

        this.maxConns=maxSize;

    }

    else{



      Vector connections = new Vector();

      try{

        int dummy=0;

        while(this.maxConns>dummy){

              connections.add(getConnection());

              dummy++;

              //System.out.println("ConnectionPool.trimTo() : Getting Connection nr "+dummy+", maxConns="+this.maxConns);

        }

      }

      catch(Exception ex){

        System.out.println("Exception in"+this.getClass().getName());

        ex.printStackTrace();

      }



      try{

        //this.checkedOut=size;

        this.minConns=minSize;

        this.maxConns=maxSize;



        for (int i = 0; i < size; i++) {

          Connection conn = (Connection)connections.get(0);

          connections.remove(0);

          connections.trimToSize();

          this.freeConnection(conn);

        }

        Iterator iter = connections.iterator();

        while (iter.hasNext()) {

          Connection item = (Connection)iter.next();

          this.removeFromCheckedOutList(item);

          item.close();

          //System.out.print("Closing database connection");

        }



      }

      catch(Exception ex){

        System.out.println("Exception in "+this.getClass().getName());

        ex.printStackTrace();

      }



      System.out.println("\n---- ConnectionPool trim ---");

      System.out.println("\tDatasource "+this.name+" contains "+getCurrentConnectionCount()+" connections");

      System.out.println("----");



    }

    //setTimeOut((int)getTimeOut()/3);



 }



 /**

  * Enlarges the pool so it has size number of connections

  */

 public synchronized void enlargeTo(int size,int minSize,int maxSize){



    //setTimeOut(getTimeOut()*3);





    if(size>maxSize){

      size=maxSize;

    }

    if(size<minSize){

      size=minSize;

    }



    int currentConnectionCount = this.getCurrentConnectionCount();

    int currentMaxSize = this.getMaximumConnectionCount();

    int currentMinimumSize = this.getMinimumConnectionCount();



    if(currentConnectionCount==1 && currentMaxSize == 1 && currentMinimumSize == 1 && (size==1)){



        this.minConns=minSize;

        this.maxConns=maxSize;



    }

    else{





      Vector connections = new Vector();



      try{

        int dummy=0;

        while(this.maxConns>dummy){

              connections.add(getConnection());

              dummy++;

        }

      }

      catch(Exception ex){

        ex.printStackTrace();

      }



      int currentconns = connections.size();

      if(currentconns<size){

        int difference = size-currentconns;

        for (int i = 0; i < difference; i++) {

          try{

            Connection conn = newConnection();

            connections.add(conn);

          }

          catch(Exception ex){

            ex.printStackTrace();

          }



        }

      }



      try{

        this.minConns=minSize;

        this.maxConns=maxSize;

        //this.checkedOut=size;

        for (int i = 0; i < size; i++) {

          Connection conn = (Connection)connections.get(0);

          connections.remove(0);

          connections.trimToSize();

          this.freeConnection(conn);

        }

        Iterator iter = connections.iterator();

        while (iter.hasNext()) {

          Connection item = (Connection)iter.next();

          item.close();

          //System.out.print("Closing database connection");

        }



      }

      catch(Exception ex){

          ex.printStackTrace();

      }



      System.out.println("\n---- ConnectionPool enlargement ---");

      System.out.println("\tDatasource "+this.name+" contains "+getCurrentConnectionCount()+" connections");

      System.out.println("----");



    }



    //setTimeOut((int)getTimeOut()/3);



 }



 public DatastoreConnection getDatastoreConnection()throws SQLException{

    return (DatastoreConnection) getConnection();

 }



 public int getCurrentConnectionCount(){

  //return this.currentConns;

  return freeConnections.size() + this.getCheckedOutCount();

 }



 public int getMaximumConnectionCount(){

  return this.maxConns;

 }



 public int getMinimumConnectionCount(){

  return this.minConns;

 }



 public void setTimeOut(int timeout){

  this.timeOut=timeout;

 }



 public int getTimeOut(){

  return this.timeOut;

 }









 private int getCheckedOutCount(){

  return getCheckedOutMap().size();

 }



 private Map getCheckedOutMap(){

  if(checkedOutInfoMap==null){

    checkedOutInfoMap= new HashMap();

  }

  return checkedOutInfoMap;

 }



 private void addToCheckedOutList(Connection conn){

    getCheckedOutMap().put(conn,new Long(System.currentTimeMillis()));

 }



 private void removeFromCheckedOutList(Connection conn){

    getCheckedOutMap().remove(conn);

 }





 private void cleanUpCheckedOut(){

  //debug("Running cleanUpCheckedOut()");

  Iterator iter = getCheckedOutMap().keySet().iterator();

  while (iter.hasNext()) {

    Connection conn = (Connection)iter.next();

    Long timeCheckedOut = (Long)getCheckedOutMap().get(conn);

    long lCheckedOut = timeCheckedOut.longValue();



    if(lCheckedOut+lConnectionTimeOut<System.currentTimeMillis()){

      //Throw away the reference to the connection

      //debug("Throwing away timed out connection from out-pool");

      iter.remove();

    }

  }

 }





 protected void debug(String debug){

    //logWriter.log("[ConnectionPool-Debug] : "+debug, LogWriter.DEBUG);

    System.out.println("[ConnectionPool-Debug] : "+debug);

 }



}

