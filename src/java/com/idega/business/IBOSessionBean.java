package com.idega.business;

import javax.ejb.SessionBean;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.idega.core.accesscontrol.business.LoginSession;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.presentation.IWContext;
import com.idega.user.data.bean.User;
import com.idega.util.expression.ELUtil;

/**
 * Title:        idega Business Objects
 * Description:  A class to be a base implementation for IBO Session (Stateful EJB Session) beans
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */

public class IBOSessionBean extends IBOServiceBean implements IBOSession,SessionBean{

	private static final long serialVersionUID = -3480014893942017616L;
	private IWUserContext iwuc;
	private final String sessionKey="IBO."+this.getClass().getName();

  public IBOSessionBean() {}

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
    return this.iwuc;
  }

  @Override
  public IWApplicationContext getIWApplicationContext() {
	  return getUserContext() == null ? IWMainApplication.getDefaultIWApplicationContext() : getUserContext().getApplicationContext();
  }

  @Override
  public void remove(){
    this.ejbRemove();
  }

  @Override
  public void ejbRemove() {
    this.getUserContext().removeSessionAttribute(this.sessionKey);
    this.iwuc = null;
  }

  @SuppressWarnings("unchecked")
  protected <T extends IBOSession> T getSessionInstance(Class<? extends IBOSession> beanClass) throws IBOLookupException {
	return (T) IBOLookup.getSessionInstance(getUserContext(), beanClass);
  }


  @Override
  protected User getCurrentUser(){
    if (getUserContext() == null) {
    	try {
    		LoginSession loginSession = ELUtil.getInstance().getBean(LoginSession.class);
    		return loginSession.getUser();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    return getUserContext().getLoggedInUser();
  }
}