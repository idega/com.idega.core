//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.transaction;



/**
 * Title:        idegaWeb Implementation of the JTA (javax.transaction) API
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
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
