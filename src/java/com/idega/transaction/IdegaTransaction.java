//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.transaction;









/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 0.9

*/



import com.idega.util.database.ConnectionBroker;

import javax.transaction.*;

import javax.transaction.xa.*;

import java.sql.*;



public class IdegaTransaction implements Transaction{



private Synchronization syncronization;

private int status=IdegaTransactionStatus.STATUS_ACTIVE;

private boolean isRollBackOnly=false;

private Connection _conn;

private String _dataSource=ConnectionBroker.DEFAULT_POOL;



  protected IdegaTransaction(){

      _conn = getFirstConnection();

  }



  protected IdegaTransaction(String datasource){

      this.setDatasource(datasource);

      _conn = getFirstConnection();

  }







    public void commit()throws RollbackException,

                   HeuristicMixedException,

                   HeuristicRollbackException,

                   java.lang.SecurityException,

                   SystemException{

    if(this.isRollBackOnly){

      //Do nothing

      //

      this.rollback();

      this.end();

      throw new RollbackException("Transaction Rolled back");

      /**

       * @todo: Should this throw an exception?

       */

    }

    else{

      Synchronization sync = getSynchronization();

      if(sync!=null){

        sync.beforeCompletion();

      }



      setStatus(IdegaTransactionStatus.STATUS_PREPARING);



      setStatus(IdegaTransactionStatus.STATUS_PREPARED);



      setStatus(IdegaTransactionStatus.STATUS_COMMITTING);

      try{

        getConnection().commit();

      }

      catch(SQLException ex){

          SystemException exeption = new SystemException(ex.getMessage());

          throw (SystemException)exeption.fillInStackTrace();

      }

      setStatus(IdegaTransactionStatus.STATUS_COMMITTED);



      if(sync!=null){

        sync.afterCompletion(getStatus());

      }

    }



}



/**

 * UNIMPLEMENTED

 */

public boolean delistResource(XAResource xaRes, int flag)throws java.lang.IllegalStateException,

                              SystemException{

  return false;



}



/**

 * UNIMPLEMENTED

 */

public  boolean enlistResource(XAResource xaRes)throws RollbackException,

                              java.lang.IllegalStateException,

                              SystemException{

  return false;

}



public int getStatus()throws SystemException{

  return this.status;

}



public void registerSynchronization(Synchronization sync)throws RollbackException,

                                    java.lang.IllegalStateException,

                                    SystemException{

  this.syncronization=sync;

                                    }



    public void rollback()throws java.lang.IllegalStateException,

                     SystemException{

    Synchronization sync = getSynchronization();

    if(sync!=null){

      sync.beforeCompletion();

    }



    setStatus(IdegaTransactionStatus.STATUS_PREPARING);



    setStatus(IdegaTransactionStatus.STATUS_PREPARED);



    setStatus(IdegaTransactionStatus.STATUS_ROLLING_BACK);

    try{

      getConnection().rollback();

    }

    catch(SQLException ex){

        SystemException exeption = new SystemException(ex.getMessage());

        throw (SystemException)exeption.fillInStackTrace();

    }

    setStatus(IdegaTransactionStatus.STATUS_ROLLEDBACK);



    if(sync!=null){

      sync.afterCompletion(getStatus());

    }





}



/**

 * UNIMPLEMENTED

 */

public void setRollbackOnly()throws java.lang.IllegalStateException,

                            SystemException{

    setRollBackOnly(true);

}



/**

 * UNIMPLEMENTED

 */

private void setRollBackOnly(boolean rollbackOnly)throws java.lang.IllegalStateException,

                            SystemException{

  this.isRollBackOnly=rollbackOnly;

}



private Synchronization getSynchronization(){

  return syncronization;

}



private void setStatus(int status){

  this.status=status;

}



private Connection getFirstConnection(){

  //Connection conn = ConnectionBroker.getConnection(false);

  Connection conn = ConnectionBroker.getConnection(this._dataSource);

  initFirstConnection(conn);

  return conn;

}



private void initFirstConnection(Connection conn){

  try{

    //System.out.println("initFirstConnection() conn.getTransactionIsolation()="+conn.getTransactionIsolation());

    //System.out.println("initFirstConnection() conn.getMetaData().getDefaultTransactionIsolation()="+conn.getMetaData().getDefaultTransactionIsolation());

    //if(conn.getMetaData().getDefaultTransactionIsolation()!=conn.TRANSACTION_NONE){

      conn.setAutoCommit(false);

    //}

  }

  catch(SQLException ex){

    System.err.println("Error in IdegaTransaction.freeConnection() Message :"+ex.getMessage());

    //ex.printStackTrace(System.err);

  }

}



public Connection getConnection(){

 return this._conn;

}



public void freeConnection(Connection conn){

  if(conn!=null){

    try{

      //System.out.println("freeConnection() conn.getTransactionIsolation()="+conn.getTransactionIsolation());

      //System.out.println("freeConnection() conn.getMetaData().getDefaultTransactionIsolation()="+conn.getMetaData().getDefaultTransactionIsolation());

      //if(conn.getMetaData().getDefaultTransactionIsolation()!=conn.TRANSACTION_NONE){

        conn.setAutoCommit(true);

      //}

    }

    catch(SQLException ex){

      //ex.printStackTrace(System.err);

      System.err.println("Error in IdegaTransaction.freeConnection() Message :"+ex.getMessage());

    }

    //ConnectionBroker.freeConnection(conn,false);

    ConnectionBroker.freeConnection(this._dataSource,_conn,false);

    this._conn=null;

  }

}



protected void end(){

    freeConnection(this._conn);

    this._conn=null;

}



protected void setDatasource(String datasourceName){

  this._dataSource=datasourceName;

}



}

