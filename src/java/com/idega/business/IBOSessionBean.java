package com.idega.business;

import javax.ejb.EJBHome;
import javax.ejb.Handle;
import javax.ejb.EJBObject;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.RemoveException;

import java.rmi.RemoteException;


import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWApplicationContext;

/**
 * Title:        idega Business Objects
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */

public class IBOSessionBean extends IBOServiceBean implements IBOSession,SessionBean{

  private SessionContext sessionContext;
  private IWUserContext iwuc;
  private String sessionKey="IBO."+this.getClass().getName();


  public IBOSessionBean() {
  }

  public void ejbCreate(IWUserContext iwuc){
    this.setUserContext(iwuc);
  }

  public void ejbPostCreate(IWUserContext iwuc){
  }

  public void setUserContext(IWUserContext iwuc) {
    this.iwuc=iwuc;
  }
  public IWUserContext getUserContext() {
    return iwuc;
  }

  public IWApplicationContext getIWApplicationContext(){
    return this.getUserContext().getApplicationContext();
  }

  public void remove(){
    this.ejbRemove();
  }

  public void ejbRemove(){
    this.getUserContext().removeSessionAttribute(sessionKey);
    this.sessionContext=null;
    this.iwuc=null;
  }



}