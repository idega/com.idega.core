//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.transaction;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5
*UNDER CONSTRUCTION - NOT FINISHED
*/
import com.idega.util.*;
import javax.transaction.*;
import javax.transaction.xa.*;

public class IdegaTransactionManager implements javax.transaction.TransactionManager{

  private static String transaction_attribute_name = "idega_transaction";
  private static String transaction_syncronization_attribute_name = "idega_transaction_synchronization";
  private static int transaction_timeout = 1000;
  private static IdegaTransactionManager instance;

  /**
   * Only this class can construct itself
   */
  private IdegaTransactionManager(){
  }

    /**
    * The only way to get an instance of the TransactionManager
    */
  public static TransactionManager getInstance(){
    if(instance==null){
      instance=new IdegaTransactionManager();
    }
    return instance;
  }

  /**
   * Start a transaction, constructs a new Transaction and associates it with the current thread.
   */
 public void begin()throws NotSupportedException,
                  SystemException{
  boolean transactionAlreadyBegun=false;
  try{
    Transaction trans = getTransaction();
    if(trans!=null){
      transactionAlreadyBegun=true;
    }
  }
  catch(Exception ex){

  }
  if(transactionAlreadyBegun){
      throw new NotSupportedException("Transaction already begun, nested transactions not currently supported");
  }
  Transaction trans = new IdegaTransaction();
  //trans.registerSynchronization(new IdegaTransactionSynchronization());
  ThreadContext.getInstance().setAttribute(Thread.currentThread(),transaction_attribute_name,trans);
 }

  /**
   * Commits the current transaction and deassociates it with the current thread.
   */
 public void commit()throws RollbackException,
                   HeuristicMixedException,
                   HeuristicRollbackException,
                   java.lang.SecurityException,
                   java.lang.IllegalStateException,
                   SystemException{
  Transaction transaction = getTransaction();
  transaction.commit();
  endTransaction((IdegaTransaction)transaction);
 }

 public int getStatus() throws SystemException{
  return getTransaction().getStatus();
 }

 /**
  * Returns the current Transaction, must be constructed with begin() first
  */
 public Transaction getTransaction() throws SystemException{
    Transaction trans = (Transaction)ThreadContext.getInstance().getAttribute(Thread.currentThread(),transaction_attribute_name);
    if(trans==null){
      throw new SystemException("Transaction not set");
    }
    return trans;
  }

  /**
   * UNIMPLEMENTED
   */
 public void resume(Transaction tobj)throws InvalidTransactionException,
                   java.lang.IllegalStateException,
                   SystemException{
  Transaction trans = getTransaction();
 }


  /**
   * Rollbacks the current transaction and deassociates it with the current thread.
   */
 public void rollback()throws java.lang.IllegalStateException,
                     java.lang.SecurityException,
                     SystemException{
  Transaction transaction = getTransaction();
  transaction.rollback();
  endTransaction((IdegaTransaction)transaction);
 }


 public void setRollbackOnly()throws java.lang.IllegalStateException,
                            SystemException{
  Transaction trans = getTransaction();
  trans.setRollbackOnly();
 }

  /**
   * UNIMPLEMENTED
   */
 public void setTransactionTimeout(int seconds)throws SystemException{
  Transaction trans = getTransaction();
 }

  /**
   * UNIMPLEMENTED
   */
 public  Transaction suspend()throws SystemException{
  Transaction trans = getTransaction();
  return trans;
 }

 /**
 * Returns true if the TransactionManager has bound a Transaction Object to the current Thread
 */
 public boolean hasCurrentThreadBoundTransaction(){
  /*try{
    Transaction trans = getTransaction();
  }
  catch(SystemException ex){
    return false;
  }
  return true;*/
  Transaction obj=null;
  try{
    obj = (Transaction)ThreadContext.getInstance().getAttribute(Thread.currentThread(),transaction_attribute_name);
    if(obj==null){
      return false;
    }
    else{
      return true;
    }
  }
  catch(Exception ex){
    return false;
  }
  
 }


  private void endTransaction(IdegaTransaction transaction){
      transaction.end();
      ThreadContext.getInstance().removeAttribute(Thread.currentThread(),transaction_attribute_name);
  }


}
