//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.transaction;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/



import com.idega.util.database.ConnectionBroker;
import com.idega.util.ThreadContext;
import javax.transaction.*;
import javax.transaction.xa.*;
import java.sql.*;

/**
 * Title:        idegaWeb Implementation of the JTA (javax.transaction) API
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */

  public class IdegaTransaction implements Transaction, UserTransaction{

	/**
	 * 
	 * @uml.property name="syncronization"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private Synchronization syncronization;

  private int status=Status.STATUS_NO_TRANSACTION;
  private boolean isRollBackOnly=false;
  private Connection _conn;
  private String _dataSource=ConnectionBroker.DEFAULT_POOL;
  private int transactionTimeOutSeconds=60*60;
  private int transactionCount=0;
  private long transactionBegun = System.currentTimeMillis();


  public IdegaTransaction(){
  }

  protected IdegaTransaction(String datasource){
      this.setDatasource(datasource);
  }

    public void commit()throws RollbackException,
                   HeuristicMixedException,
                   HeuristicRollbackException,
                   java.lang.SecurityException,
                   SystemException{
      if(this.getStatus()==Status.STATUS_NO_TRANSACTION){
        throw new SystemException("Transaction not begun");
      }
      else{
        if(this.transactionCount==1){
          try{
            doRealCommit();
          }
          catch(RollbackException rbe){
            throw (RollbackException)rbe.fillInStackTrace();
          }
          catch(HeuristicMixedException hme){
            throw (HeuristicMixedException)hme.fillInStackTrace();
          }
          catch(HeuristicRollbackException hrbe){
            throw (HeuristicRollbackException)hrbe.fillInStackTrace();
          }
          catch(SecurityException sce){
            throw (SecurityException)sce.fillInStackTrace();
          }
          catch(SystemException se){
            throw (SystemException)se.fillInStackTrace();
          }
          finally{
            end();
          }
      }
      else{
        this.transactionCount--;
      }
      }
    }


    protected void doRealCommit()throws RollbackException,
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
      setStatus(Status.STATUS_PREPARING);
      setStatus(Status.STATUS_PREPARED);
      setStatus(Status.STATUS_COMMITTING);
      try{
        getConnection().commit();
      }
      catch(SQLException ex){
          SystemException exeption = new SystemException(ex.getMessage());
          throw (SystemException)exeption.fillInStackTrace();
      }
      setStatus(Status.STATUS_COMMITTED);

      if(sync!=null){
        sync.afterCompletion(getStatus());
      }
      this.end();
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

/**
 * 
 * @uml.property name="status"
 */
public int getStatus() throws SystemException {

	return this.status;

}




public void registerSynchronization(Synchronization sync)throws RollbackException,

                                    java.lang.IllegalStateException,

                                    SystemException{

  this.syncronization=sync;

                                    }



    public void rollback()throws java.lang.IllegalStateException,SystemException{
      if(this.getStatus()==Status.STATUS_NO_TRANSACTION){
        throw new SystemException("Transaction not begun");
      }
      else{
        if(this.transactionCount==1){
          try{
            doRealRollback();
          }
          catch(IllegalStateException ise){
            throw (IllegalStateException)ise.fillInStackTrace();
          }
          catch(SystemException se){
            throw (SystemException)se.fillInStackTrace();
          }
          finally{
            end();
          }
        }
        else{
          this.setRollbackOnly();
          this.transactionCount--;
        }
      }
    }


  public void doRealRollback()throws java.lang.IllegalStateException,
                     SystemException{
    Synchronization sync = getSynchronization();
    if(sync!=null){
      sync.beforeCompletion();
    }
    setStatus(Status.STATUS_PREPARING);
    setStatus(Status.STATUS_PREPARED);
    setStatus(Status.STATUS_ROLLING_BACK);
    try{
      getConnection().rollback();
    }
    catch(SQLException ex){
        SystemException exeption = new SystemException(ex.getMessage());
        throw (SystemException)exeption.fillInStackTrace();
    }
    setStatus(Status.STATUS_ROLLEDBACK);
    if(sync!=null){
      sync.afterCompletion(getStatus());
    }

  }

public void setRollbackOnly()throws java.lang.IllegalStateException,
                            SystemException{
    setRollBackOnly(true);
}


private void setRollBackOnly(boolean rollbackOnly)throws java.lang.IllegalStateException,
                            SystemException{
  if(rollbackOnly){
    this.setStatus(Status.STATUS_MARKED_ROLLBACK);
  }
  else{
    this.setStatus(Status.STATUS_ACTIVE);
  }
  this.isRollBackOnly=rollbackOnly;
}



private Synchronization getSynchronization(){
  return this.syncronization;
}

/**
 * 
 * @uml.property name="status"
 */
private void setStatus(int status) {
	this.status = status;
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

    ConnectionBroker.freeConnection(this._dataSource,this._conn,false);

    this._conn=null;

  }

}


protected void end(){
    freeConnection(this._conn);
    this._conn=null;
    ThreadContext.getInstance().removeAttribute(Thread.currentThread(),getTransactionAttributeName());
}



protected void setDatasource(String datasourceName){

  this._dataSource=datasourceName;

}
  public void begin() throws javax.transaction.NotSupportedException, javax.transaction.SystemException {
    if(this.transactionCount==0){
      //((IdegaTransactionManager)IdegaTransactionManager.getInstance()).begin(this);
      this._conn = getFirstConnection();
      ThreadContext.getInstance().setAttribute(Thread.currentThread(),getTransactionAttributeName(),this);
      this.transactionCount=1;
      this.transactionBegun=System.currentTimeMillis();
      setStatus(Status.STATUS_ACTIVE);
    }
    else{
      beginSubTransaction();
    }
  }
  public void setTransactionTimeout(int parm1) throws javax.transaction.SystemException {
    this.transactionTimeOutSeconds=parm1;
  }


  public void beginSubTransaction(){
    this.transactionCount++;
  }

  private String getTransactionAttributeName() {
	  return IdegaTransactionManager.transaction_attribute_name+"_"+this._dataSource;
  }
  
}

