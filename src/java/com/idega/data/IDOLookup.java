package com.idega.data;

import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;


/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOLookup{

  private static final String FACTORY_SUFFIX = "HomeImpl";
  private static final String BEAN_SUFFIX = "BMPBean";

  private static Map homes = new HashMap();
  private static Map beanClasses = new HashMap();
  private static Map interfaceClasses = new HashMap();

  private IDOLookup() {
  }

  /**
   * Gets the Class object for the (Remote) interface of a data bean.
   * @param entityBeanOrInterfaceClass can be either the BMP bean class or the interface class itself.
   */
  public static Class getInterfaceClassFor(Class entityBeanOrInterfaceClass){
    if(entityBeanOrInterfaceClass.isInterface()){
      return entityBeanOrInterfaceClass;
    }
    else{
      Class interfaceClass = (Class)interfaceClasses.get(entityBeanOrInterfaceClass);
      try{
        if(interfaceClass==null){
          String className = entityBeanOrInterfaceClass.getName();
          String interfaceClassName = className.substring(0,className.indexOf(BEAN_SUFFIX));
          interfaceClass = Class.forName(interfaceClassName);
        }
        return interfaceClass;
      }
      catch(ClassNotFoundException e){
        throw new RuntimeException(e.getClass()+": "+e.getMessage());
      }
    }
  }

  private static Class getFactoryClassFor(Class entityInterfaceClass)throws Exception{
    String className = getInterfaceClassFor(entityInterfaceClass).getName();
    String homeClassName = className + FACTORY_SUFFIX;
    return Class.forName(homeClassName);
  }

  /**
   * Gets the Class object for the (BMP) bean class of a data bean.
   * @param entityInterfaceClass i the (Remote) interface of the data bean.
   */
  public static Class getBeanClassFor(Class entityInterfaceClass){
    try{
      Class beanClass = (Class)beanClasses.get(entityInterfaceClass);
      if(beanClass==null){
        String className = entityInterfaceClass.getName();
        String beanClassName = className + BEAN_SUFFIX;
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
   * Gets an instance of the implementation of the Home interface for the data bean.
   * <br>The object retured can then needs to be casted to the specific home interface for the bean.
   * @param entityInterfaceClass i the (Remote) interface of the data bean.
   */
  public static IDOHome getHome(Class entityInterfaceClass)throws RemoteException{
    IDOHome home = (IDOHome)homes.get(entityInterfaceClass);
    if(home==null){
      try{
        Class factoryClass = getFactoryClassFor(entityInterfaceClass);
        home = (IDOHome)factoryClass.newInstance();
        homes.put(entityInterfaceClass,home);
      }
      catch(Exception e){
        //e.printStackTrace();
        throw new RuntimeException("Error initializing Home for Data class:"+entityInterfaceClass.getName()+" - Message: "+e.getMessage());
      }
    }
    return home;
  }

  public static IDOHome getHomeLegacy(Class entityInterfaceClass){
    try{
      return getHome(entityInterfaceClass);
    }
    catch(RemoteException e){
      throw new RuntimeException(e.getMessage());
    }
  }



  public static IDOEntity create(Class entityInterfaceClass)throws RemoteException,javax.ejb.CreateException{
    return getHome(entityInterfaceClass).idoCreate();
  }

  public static IDOEntity findByPrimaryKey(Class entityInterfaceClass,int id)throws RemoteException,javax.ejb.FinderException{
    return getHome(entityInterfaceClass).idoFindByPrimaryKey(new Integer(id));
  }

  public static IDOEntity findByPrimaryKey(Class entityInterfaceClass,Integer id)throws RemoteException,javax.ejb.FinderException{
    return getHome(entityInterfaceClass).idoFindByPrimaryKey(id);
  }


   public static IDOLegacyEntity createOld(Class entityInterfaceOrBeanClass){
      //return createNew(entityInterfaceClass);
      if(entityInterfaceOrBeanClass.isInterface()){
        return createLegacy(IDOLookup.getBeanClassFor(entityInterfaceOrBeanClass));
      }
      else{
        try{
          return (IDOLegacyEntity)entityInterfaceOrBeanClass.newInstance();
        }
        catch(Exception e){
          e.printStackTrace();
          throw new RuntimeException(e.getMessage());
        }
      }
   }

  public static IDOLegacyEntity createLegacy(Class entityInterfaceClass){
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).idoCreate();
    }
    catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }


  public static IDOLegacyEntity findByPrimaryKeyLegacyOld(Class entityInterfaceOrBeanClass,int id)throws java.sql.SQLException{
    //return findByPrimaryKeyNew(entityInterfaceOrBeanClass,id);
    try{
      IDOLegacyEntity entity = createLegacy(entityInterfaceOrBeanClass);
      entity.findByPrimaryKey(id);
      return entity;
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,int id)throws java.sql.SQLException{
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).idoFindByPrimaryKey(new Integer(id));
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }

  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,int id,String datasource)throws java.sql.SQLException{
    return findByPrimaryKeyLegacy(entityInterfaceClass,new Integer(id),datasource);
  }

  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,Integer id,String datasource)throws java.sql.SQLException{
    try{
      IDOLegacyEntity entity = (IDOLegacyEntity)getHome(entityInterfaceClass).idoCreate();
      entity.setDatasource(datasource);
      ((IDOEntityBean)entity).ejbFindByPrimaryKey(id);
      ((IDOEntityBean)entity).ejbLoad();
      return entity;
      //return (IDOLegacyEntity)getHome(entityInterfaceClass).idoFindByPrimaryKey(new Integer(id));
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }


  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceClass,Integer id)throws java.sql.SQLException{
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).idoFindByPrimaryKey(id);
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }



}
