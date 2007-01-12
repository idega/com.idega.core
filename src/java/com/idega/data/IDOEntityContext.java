package com.idega.data;

import java.rmi.RemoteException;
import java.security.Identity;
import java.security.Principal;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EntityContext;
import javax.transaction.UserTransaction;


/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOEntityContext implements EntityContext {

  private boolean _rollbackOnly=false;
  private EJBObject _ejbo;

  public IDOEntityContext(EJBObject ejbo) {
    setEJBObject(ejbo);
  }

  public void setEJBObject(EJBObject ejbo){
    this._ejbo=ejbo;
  }


  public EJBObject getEJBObject() throws java.lang.IllegalStateException {
    return this._ejbo;
  }

  public EJBLocalObject getEJBLocalObject() throws java.lang.IllegalStateException {
    return (EJBLocalObject)this._ejbo;
  }

  public javax.ejb.EJBLocalHome getEJBLocalHome() throws java.lang.IllegalStateException {
    return this.getEJBLocalObject().getEJBLocalHome();
  }

  public Object getPrimaryKey() throws java.lang.IllegalStateException {
    try{
      return getEJBObject().getPrimaryKey();
    }
    catch(RemoteException e){
      e.printStackTrace();
      return null;
    }
  }
  public Identity getCallerIdentity() {
    /**@todo: Implement this javax.ejb.EJBContext method*/
    throw new java.lang.UnsupportedOperationException("Method getCallerIdentity() not yet implemented.");
  }
  public Principal getCallerPrincipal() {
    /**@todo: Implement this javax.ejb.EJBContext method*/
    throw new java.lang.UnsupportedOperationException("Method getCallerPrincipal() not yet implemented.");
  }
  public EJBHome getEJBHome() {
    try{
      return getEJBObject().getEJBHome();
    }
    catch(RemoteException e){
      e.printStackTrace();
      return null;
    }
  }
  public Properties getEnvironment() {
    /**@todo: Implement this javax.ejb.EJBContext method*/
    throw new java.lang.UnsupportedOperationException("Method getEnvironment() not yet implemented.");
  }
  public boolean getRollbackOnly() throws java.lang.IllegalStateException {
    return this._rollbackOnly;
  }
  public UserTransaction getUserTransaction() throws java.lang.IllegalStateException {
    /**@todo: Implement this javax.ejb.EJBContext method*/
    throw new java.lang.UnsupportedOperationException("Method getUserTransaction() not yet implemented.");
  }
  public boolean isCallerInRole(String parm1) {
    /**@todo: Implement this javax.ejb.EJBContext method*/
    throw new java.lang.UnsupportedOperationException("Method isCallerInRole() not yet implemented.");
  }
  public boolean isCallerInRole(Identity parm1) {
    /**@todo: Implement this javax.ejb.EJBContext method*/
    throw new java.lang.UnsupportedOperationException("Method isCallerInRole() not yet implemented.");
  }
  public void setRollbackOnly() throws java.lang.IllegalStateException {
    this._rollbackOnly=true;
  }
}
