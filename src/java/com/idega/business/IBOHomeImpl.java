package com.idega.business;

import javax.ejb.EJBMetaData;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import java.io.Serializable;

/**
 * Title:        idega Business Objects
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public abstract class IBOHomeImpl implements IBOHome,Serializable{

  protected IBOHomeImpl(){
  }

  public IBOService createIBO(Class entityInterfaceClass)throws javax.ejb.CreateException{
    Class beanClass = IBOLookup.getIBOBeanClassFor(entityInterfaceClass);
    try{
      IBOService bean = (IBOService)beanClass.newInstance();
      IBOSessionContext context = new IBOSessionContext();
      context.setEJBHome(this);
      ((IBOServiceBean)bean).setSessionContext(context);
      return bean;
      //return (IDOEntity)beanClass.newInstance();
    }
    catch(Exception e){
      e.printStackTrace();
      throw new javax.ejb.CreateException(e.getMessage());
    }
  }


  public IBOService createIBO()throws javax.ejb.CreateException{
    return createIBO(this.getBeanInterfaceClass());
  }

  /**
   * @todo: implement
   */
  public EJBMetaData getEJBMetaData(){
      /**@todo: Implement this javax.ejb.EJBHome method*/
    throw new java.lang.UnsupportedOperationException("Method getEJBMetaData() not yet implemented.");
  }

  /**
   * @todo: implement
   */
  public HomeHandle getHomeHandle(){
      /**@todo: Implement this javax.ejb.EJBHome method*/
    throw new java.lang.UnsupportedOperationException("Method getHomeHandle() not yet implemented.");
  }

  /**
   * @todo: implement
   */
  public void remove(Handle handle){}

  public void remove(Object primaryKey){
  }

  protected abstract Class getBeanInterfaceClass();

  protected Class getBeanClass(){
    return IBOLookup.getIBOBeanClassFor(getBeanInterfaceClass());
  }

  protected Class getEntityBeanClass(){
    return getBeanClass();
  }


  protected IBOService iboCheckOutPooledBean()throws Exception{
    /**
     * @todo: Change implementation
     */
    return (IBOService)this.getBeanClass().newInstance();
  }


  protected void iboCheckInPooledBean(IBOService service){
    /**
     * @todo: implement
     */
  }

}
