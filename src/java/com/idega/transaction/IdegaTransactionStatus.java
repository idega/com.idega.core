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



public class IdegaTransactionStatus implements Status{



    public static int STATUS_ACTIVE=0;



    public static int STATUS_COMMITTED=1;



    public static int STATUS_COMMITTING=2;



    public static int STATUS_MARKED_ROLLBACK=3;



    public static int STATUS_NO_TRANSACTION=4;



    public static int STATUS_PREPARED=5;



    public static int STATUS_PREPARING=6;



    public static int STATUS_ROLLEDBACK=7;



    public static int STATUS_ROLLING_BACK=8;



    public static int STATUS_UNKNOWN=9;

}
