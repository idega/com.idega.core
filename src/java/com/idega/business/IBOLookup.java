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
 * Description:
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

  public static EJBHome getHomeForClass(Class beanInterfaceClass)throws RemoteException{
    return getInstance().getEJBHomeInstance(beanInterfaceClass);
  }

  public static IBOHome getIBOHomeForClass(Class beanInterfaceClass)throws RemoteException{
    return (IBOHome)getHomeForClass(beanInterfaceClass);
  }


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

  public static void removeSessionInstance(IWUserContext iwuc,Class beanInterfaceClass)throws RemoteException,RemoveException{
    IBOSession session = getSessionInstance(iwuc,beanInterfaceClass);
    session.remove();
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
        String className = entityInterfaceClass.getName();
        String beanClassName = className + getBeanSuffix();
        beanClass = Class.forName(beanClassName);
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
          String interfaceClassName = className.substring(0,className.indexOf(getBeanSuffix()));
          interfaceClass = Class.forName(interfaceClassName);
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