//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.transaction;



/**
 * Title:        idegaWeb Implementation of the JTA (javax.transaction) API
 * Description:  UNIMPLEMENTED CLASS
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.0
 *UNIMPLEMENTED CLASS
*/

import com.idega.util.*;

import javax.transaction.*;

import javax.transaction.xa.*;



public class IdegaTransactionXid implements Xid{



  /**

   * Maximum number of bytes returned by getBqual.

   */

  public static final int MAXGTRIDSIZE = 64;

  /**

   * Maximum number of bytes returned by getGtrid.

   */

  public static final int MAXBQUALSIZE = 64;



/**

 * Obtain the transaction branch identifier part of XID as an array of bytes.

 */

public byte[] getBranchQualifier(){return(null);}



/**

 * Obtain the format identifier part of the XID.

 */

public int getFormatId(){return(0);}



/**

 *Obtain the global transaction identifier part of XID as an array of bytes.

 */

public byte[] getGlobalTransactionId() {return(null);}









}
