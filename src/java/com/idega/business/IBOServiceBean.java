package com.idega.business;

import java.util.Locale;
import javax.ejb.EJBHome;
import javax.ejb.Handle;
import javax.ejb.EJBObject;
import javax.ejb.SessionContext;
import javax.ejb.SessionBean;

import com.idega.idegaweb.IWApplicationContext;

/**
 * Title:        idega Business Objects
 * Description:  A class to be a base implementation for IBO Service (Stateless EJB Session) beans
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IBOServiceBean implements IBOService, SessionBean {

  private SessionContext ejbSessionContext;
  private IWApplicationContext iwac;

  public IBOServiceBean() {
  }

  /**
   * The default implementation
   */

  public void ejbCreate(){
  }
  public void ejbPostCreate(){
  }

  public String getServiceDescription() {
    /**@todo: Implement this com.idega.business.IBOService method*/
    throw new java.lang.UnsupportedOperationException("Method getServiceDescription() not yet implemented.");
  }
  public String getLocalizedServiceDescription(Locale locale) {
    /**@todo: Implement this com.idega.business.IBOService method*/
    throw new java.lang.UnsupportedOperationException("Method getLocalizedServiceDescription() not yet implemented.");
  }
  public EJBHome getEJBHome() throws java.rmi.RemoteException {
    return this.getSessionContext().getEJBHome();
  }

  protected EJBObject getEJBObject() throws java.rmi.RemoteException {
    return this.getSessionContext().getEJBObject();
  }

  public Handle getHandle() throws java.rmi.RemoteException {
    throw new java.lang.UnsupportedOperationException("Method getHandle() not yet implemented.");
  }
  public Object getPrimaryKey() throws java.rmi.RemoteException {
    throw new java.rmi.RemoteException("Method getPrimaryKey() not available for a session bean");
  }
  public boolean isIdentical(EJBObject parm1) throws java.rmi.RemoteException {
    return this.getEJBObject().equals(parm1);
  }
  public void remove() throws java.rmi.RemoteException, javax.ejb.RemoveException {
    this.ejbRemove();
  }
  public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
  }
  public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
  }
  public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    this.ejbSessionContext=null;
  }
  public void setSessionContext(SessionContext parm1) throws javax.ejb.EJBException, java.rmi.RemoteException {
    this.ejbSessionContext=parm1;
  }
  protected SessionContext getSessionContext() {
    return this.ejbSessionContext;
  }

  public void setIWApplicationContext(IWApplicationContext iwac){
    this.iwac=iwac;
  }

  public IWApplicationContext getIWApplicationContext(){
    return this.iwac;
  }

}