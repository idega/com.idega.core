package com.idega.business;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;

import javax.ejb.*;

import com.idega.data.IDOEntity;
import com.idega.data.IDOEntityBean;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.IDOHome;


import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWApplicationContext;

/**
 * Title:        idega Business Objects
 * Description:  IBOLookup is a class use to get instances of IBO (Service and Session) objects.<br><br>
 * <br>Instances of IBOService classes are stored in the IWApplicationContext and obtained by passing a reference to the application context and either a class representing a bean interface of implementation.
 * <br>Instances of IBOSession classes are stored in the IWUserContext and obtained by passing a reference to the user context and either a class representing a bean interface of implementation.
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */

public class IBOLookup {


  private static IBOLookup instance;
  private static IBOLookup getInstance(){
    if(instance==null){
      instance = new IBOLookup();
    }
    return instance;
  }


  protected final String FACTORY_SUFFIX = "HomeImpl";
  private final String BEAN_SUFFIX = "Bean";
  protected String getBeanSuffix(){
    return BEAN_SUFFIX;
  }


  protected static Map homes = new HashMap();
  protected static Map beanClasses = new HashMap();
  protected static Map interfaceClasses = new HashMap();

  protected Map services;

  protected IBOLookup() {
  }

  protected static EJBHome getHomeForClass(Class beanInterfaceClass)throws RemoteException{
    return getInstance().getEJBHomeInstance(beanInterfaceClass);
  }

  protected static IBOHome getIBOHomeForClass(Class beanInterfaceClass)throws RemoteException{
    return (IBOHome)getHomeForClass(beanInterfaceClass);
  }

  /**
   * Returns an instance of a IBOSession bean.
   * The instance is stored in the session for the (current) user context. After <b>this</b> method is called there should be a corresponding call to removeSessionAttribute() to clean up from the users context.
   * @param iwuc A reference to the user (context) the bean instance is working under.
   * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
   */
  public static IBOSession getSessionInstance(IWUserContext iwuc,Class beanInterfaceClass)throws RemoteException{
    return getInstance().getSessionInstanceImpl(iwuc,beanInterfaceClass);
  }

  private IBOSession getSessionInstanceImpl(IWUserContext iwuc,Class beanInterfaceClass)throws RemoteException{
    IBOSession session = (IBOSession)iwuc.getSessionAttribute(this.getSessionKeyForObject(beanInterfaceClass));
    if(session==null){
      try{
        session = instanciateSessionBean(beanInterfaceClass);
        iwuc.setSessionAttribute(getSessionKeyForObject(beanInterfaceClass),session);
        session.setUserContext(iwuc);
      }
      catch(CreateException cre){
        throw new RemoteException("[IBOLookup] : CreateException : "+cre.getMessage());
      }
    }
    return session;
  }

  /**
   * Cleans up after an an instance of a IBOSession bean has been used.
   * @param iwuc A reference to the user (context) the bean instance is working under.
   * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
   */
  public static void removeSessionInstance(IWUserContext iwuc,Class beanInterfaceClass)throws RemoteException,RemoveException{
      getInstance().removeSessionInstanceImpl(iwuc,beanInterfaceClass);
  }


  private void removeSessionInstanceImpl(IWUserContext iwuc,Class beanInterfaceClass)throws RemoteException,RemoveException{
    IBOSession session = getSessionInstance(iwuc,beanInterfaceClass);
    session.remove();
    iwuc.removeSessionAttribute(getSessionKeyForObject(beanInterfaceClass));
  }

  private IBOSession instanciateSessionBean(Class beanInterfaceClass)throws RemoteException,CreateException{
    return (IBOSession)instanciateServiceBean(beanInterfaceClass);
  }

  private IBOService instanciateServiceBean(Class beanInterfaceClass)throws RemoteException,CreateException{
    IBOService session = null;
    IBOHome home = getIBOHomeForClass(beanInterfaceClass);
    session = home.createIBO();
    return session;
  }

  /**
   * Returns an instance of a IBOService bean.
   * The instance is stored in the application context and is shared between all users.
   * @param iwac A reference to the application (context) the bean should be looked up.
   * @param beanInterfaceClass The bean (implementation or interface) class to be used. (For example UserBusiness.class or UserBusinessBean.class)
   */
  public static IBOService getServiceInstance(IWApplicationContext iwac,Class beanInterfaceClass)throws RemoteException{
    return getInstance().getServiceInstanceImpl(iwac,beanInterfaceClass);
  }

  private IBOService getServiceInstanceImpl(IWApplicationContext iwac,Class beanInterfaceClass)throws RemoteException{
    IBOService service = (IBOService)this.getServicesMap(iwac).get(beanInterfaceClass);
    if(service==null){
      try{
        service = this.instanciateServiceBean(beanInterfaceClass);
        ((IBOServiceBean)service).setIWApplicationContext(iwac);
        getServicesMap(iwac).put(beanInterfaceClass,service);
      }
      catch(CreateException cre){
        throw new RemoteException("[IBOLookup] : CreateException : "+cre.getMessage());
      }
    }
    return service;
  }

  protected Class getFactoryClassFor(Class entityInterfaceClass)throws Exception{
    String className = getInterfaceClassForNonStatic(entityInterfaceClass).getName();
    String homeClassName = className + FACTORY_SUFFIX;
    return Class.forName(homeClassName);
  }

  private String getSessionKeyForObject(Class interfaceClass){
    return "IBO."+interfaceClass.getName();
  }

  /**
   * Gets an instance of the implementation of the Home interface for the data bean.
   * <br>The object retured can then needs to be casted to the specific home interface for the bean.
   * @param entityInterfaceClass i the (Remote) interface of the data bean.
   */
  protected EJBHome getEJBHomeInstance(Class entityInterfaceClass)throws RemoteException{
    EJBHome home = (EJBHome)homes.get(entityInterfaceClass);
    if(home==null){
      try{
        Class factoryClass = getFactoryClassFor(entityInterfaceClass);
        home = (EJBHome)factoryClass.newInstance();
        homes.put(entityInterfaceClass,home);
      }
      catch(Exception e){
        e.printStackTrace();
        throw new RuntimeException("Error initializing Home for EJB Bean Interface class:"+entityInterfaceClass.getName()+" - Message: "+e.getMessage());
      }
    }
    return home;
  }


   /**
   * Gets the Class object for the (BMP) bean class of a data bean.
   * @param entityInterfaceClass i the (Remote) interface of the data bean.
   */
  static Class getBeanClassFor(Class entityInterfaceClass){
    return getInstance().getBeanClassForNonStatic(entityInterfaceClass);
  }



  /**
   * Gets the Class object for the (BMP) bean class of a data bean.
   * @param entityInterfaceClass i the (Remote) interface of the data bean.
   */
  protected Class getBeanClassForNonStatic(Class entityInterfaceClass){
    try{
      Class beanClass = (Class)beanClasses.get(entityInterfaceClass);
      if(beanClass==null){
        if(entityInterfaceClass.isInterface()){
          String className = entityInterfaceClass.getName();
          String beanClassName = className + getBeanSuffix();
          beanClass = Class.forName(beanClassName);
        }
        else{
          beanClass = entityInterfaceClass;
        }
        beanClasses.put(entityInterfaceClass,beanClass);
        interfaceClasses.put(beanClass,entityInterfaceClass);
      }
      return beanClass;
    }
    catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException(e.getClass().getName()+": "+e.getMessage());
    }
    //return null;
  }

    /**
   * Gets the Class object for the (Remote) interface of a data bean.
   * @param entityBeanOrInterfaceClass can be either the BMP bean class or the interface class itself.
   */
  protected Class getInterfaceClassForNonStatic(Class entityBeanOrInterfaceClass){
    if(entityBeanOrInterfaceClass.isInterface()){
      return entityBeanOrInterfaceClass;
    }
    else{
      Class interfaceClass = (Class)interfaceClasses.get(entityBeanOrInterfaceClass);
      try{
        if(interfaceClass==null){
          String className = entityBeanOrInterfaceClass.getName();
          int endIndex = className.indexOf(getBeanSuffix());
          if(endIndex!=-1){
            String interfaceClassName = className.substring(0,endIndex);
            interfaceClass = Class.forName(interfaceClassName);
          }
          else{
            //For legacy beans
            interfaceClass = entityBeanOrInterfaceClass;
          }
        }
        return interfaceClass;
      }
      catch(ClassNotFoundException e){
        throw new RuntimeException(e.getClass()+": "+e.getMessage());
      }
    }
  }


  private Map getServicesMap(IWApplicationContext iwac){
    /**
     * @todo: implement
     */
     return getServicesMap();
  }


  private Map getServicesMap(){
    if(services==null){
      services=new HashMap();
    }
    return services;
  }


}