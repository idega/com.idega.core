//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.transaction;







/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*UNIMPLEMENTED

*/

import com.idega.util.*;

import javax.transaction.*;

import javax.transaction.xa.*;



public class IdegaUserTransaction extends IdegaTransaction implements javax.transaction.UserTransaction {



  /*private boolean isRollBackOnly=false;



  public void commit(){}



  public boolean delistResource(XAResource xaRes, int flag){}



  public  boolean enlistResource(XAResource xaRes){}



  public int getStatus(){}



  public void registerSynchronization(Synchronization sync){}



  public void rollback(){}



  public void setRollbackOnly(){

    isRollBackOnly=true;

  }*/



  public void begin()throws NotSupportedException, SystemException{



  }





  public void setTransactionTimeout(int p0) throws SystemException{



  }



}

