//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.transaction;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*UNDER CONSTRUCTION
*/
import com.idega.util.*;
import javax.transaction.*;
import javax.transaction.xa.*;

public class IdegaTransactionSynchronization implements Synchronization{

  private int status;

/**
 * This method is called by the transaction manager after the transaction is committed or rolled back.
 */
 public void afterCompletion(int status) {
  this.status=status;
 }

  /**
   * This method is called by the transaction manager prior to the start of the transaction completion process.
   */
 public void beforeCompletion(){}

}