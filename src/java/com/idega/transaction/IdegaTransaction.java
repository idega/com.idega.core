//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.transaction;




/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5
*UNDER CONSTRUCTION - NOT FINISHED
*/

import com.idega.util.database.ConnectionBroker;
import javax.transaction.*;
import javax.transaction.xa.*;
import java.sql.*;

public class IdegaTransaction implements Transaction{

private Synchronization syncronization;
private int status=IdegaTransactionStatus.STATUS_ACTIVE;
private boolean isRollBackOnly=false;
private Connection conn;

  protected IdegaTransaction(){
      conn = getFirstConnection();
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
        this.conn.commit();
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
      this.conn.rollback();
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
  Connection conn = ConnectionBroker.getConnection(false);
  try{
    conn.setAutoCommit(false);
  }
  catch(SQLException ex){
    ex.printStackTrace(System.err);
  }
  return conn;
}

public Connection getConnection(){
 return this.conn;
}

public void freeConnection(Connection conn){
  try{
    this.conn.setAutoCommit(true);
  }
  catch(SQLException ex){
    ex.printStackTrace(System.err);
  }
  ConnectionBroker.freeConnection(conn,false);
  this.conn=null;
}

protected void end(){
  freeConnection(this.conn);
}

}
