//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.transaction;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;


/**
 * Title:        idegaWeb Implementation of the JTA (javax.transaction) API
 * Description:  UNIMPLEMENTED CLASS
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.0
 *UNIMPLEMENTED CLASS
*/

public class IdegaTransactionXAResource implements XAResource{



  /**

   * End a recovery scan.

   */

  public static final int TMENDRSCAN = 8388608;

  /**

   * Disassociates the caller and mark the transaction branch rollback-only.

   */

  public static final int TMFAIL = 536870912;

  /**

   * Caller is joining existing transaction branch.

   */

  public static final int TMJOIN = 2097152;

  /**

   *Use TMNOFLAGS to indicate no flags value is selected.

   */

  public static final int TMNOFLAGS = 0;

  /**

   *Caller is using one-phase optimization.

   */

  public static final int TMONEPHASE = 1073741824;

  /**

   * Caller is resuming association with with suspended transaction branch.

   */

  public static final int TMRESUME = 134217728;

  /**

   * Start a recovery scan.

   */

  public static final int TMSTARTRSCAN = 16777216;

  /**

   * Disassociate caller from transaction branch.

   */

  public static final int TMSUCCESS = 67108864;

  /**

   *Caller is suspending (not ending) association with transaction branch.

   */

  public static final int TMSUSPEND = 33554432;

  /**

   *The transaction work has been prepared normally.

   */

  public static final int XA_RDONLY = 3;

  /**

   *The transaction branch has been read-only and has been committed.

   */

  public static final int XA_OK = 0;







  public void commit(Xid p0, boolean p1) throws XAException{}



  public void end(Xid p0, int p1) throws XAException{}



  public void forget(Xid p0) throws XAException{}



  public int getTransactionTimeout() throws XAException{return(XA_OK);}



  public boolean isSameRM(XAResource p0) throws XAException{return(true);}



  public int prepare(Xid p0) throws XAException{return(0);}



  public Xid[] recover(int p0) throws XAException{return(null);}



  public void rollback(Xid p0) throws XAException{}



  public boolean setTransactionTimeout(int p0) throws XAException{return(true);}



  public void start(Xid p0, int p1) throws XAException{}

}
