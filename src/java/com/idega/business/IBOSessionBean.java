package com.idega.business;

import javax.ejb.EJBHome;
import javax.ejb.Handle;
import javax.ejb.EJBObject;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.RemoveException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import java.rmi.RemoteException;

import com.idega.core.user.data.User;

import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.presentation.IWContext;

/**
 * Title:        idega Business Objects
 * Description:  A class to be a base implementation for IBO Session (Stateful EJB Session) beans
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

  public void setUserContext(IWUserContext _iwuc) {
  	IWUserContext iwucToSet = _iwuc;
  	if(_iwuc instanceof IWContext){
		IWContext iwc = (IWContext)_iwuc;
  		HttpSession session = iwc.getSession();
  		ServletContext sc = iwc.getServletContext();
  		iwucToSet = new IWUserContextImpl(session,sc);
  	}
    this.iwuc=iwucToSet;
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

  protected IBOSession getSessionInstance(Class beanClass)throws RemoteException{
    return IBOLookup.getSessionInstance(this.getUserContext(),beanClass);
  }


  protected User getCurrentUser(){
    return this.getUserContext().getUser();
  }
}