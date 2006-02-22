package com.idega.business;

import javax.ejb.SessionBean;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;

/**
 * Title:        idega Business Objects
 * Description:  A class to be a base implementation for IBO Session (Stateful EJB Session) beans
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */

public class IBOSessionBean extends IBOServiceBean implements IBOSession,SessionBean{

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
    this.iwuc=null;
  }

  protected IBOSession getSessionInstance(Class beanClass)throws IBOLookupException{
    return IBOLookup.getSessionInstance(this.getUserContext(),beanClass);
  }


  protected User getCurrentUser(){
    return this.getUserContext().getCurrentUser();
  }
}