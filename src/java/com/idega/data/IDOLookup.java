package com.idega.data;

import java.util.Map;
import java.util.Hashtable;
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

  private static Map homes = new Hashtable();


  private IDOLookup() {
  }

  public static Class getInterfaceClassFor(Class entityBeanOrInterfaceClass){
    if(entityBeanOrInterfaceClass.isInterface()){
      return entityBeanOrInterfaceClass;
    }
    else{
      String className = entityBeanOrInterfaceClass.getName();
      String interfaceClassName = className.substring(0,className.indexOf(BEAN_SUFFIX));
      try{
        return Class.forName(interfaceClassName);
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

  public static Class getBeanClassFor(Class entityInterfaceClass){
    try{
      String className = entityInterfaceClass.getName();
      String homeClassName = className + BEAN_SUFFIX;
      return Class.forName(homeClassName);
    }
    catch(Exception e){
      e.printStackTrace();
      throw new RuntimeException(e.getClass().getName()+": "+e.getMessage());
    }
    //return null;
  }

  public static IDOHome getHome(Class entityInterfaceClass)throws RemoteException{
    IDOHome home = (IDOHome)homes.get(entityInterfaceClass);
    if(home==null){
      try{
        Class factoryClass = getFactoryClassFor(entityInterfaceClass);
        home = (IDOHome)factoryClass.newInstance();
        homes.put(entityInterfaceClass,home);
      }
      catch(Exception e){
        e.printStackTrace();
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


   public static IDOLegacyEntity createLegacy(Class entityInterfaceOrBeanClass){
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

  private static IDOLegacyEntity createNew(Class entityInterfaceClass){
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).idoCreate();
    }
    catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }


  public static IDOLegacyEntity findByPrimaryKeyLegacy(Class entityInterfaceOrBeanClass,int id)throws java.sql.SQLException{
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

  private static IDOLegacyEntity findByPrimaryKeyNew(Class entityInterfaceClass,int id)throws java.sql.SQLException{
    try{
      return (IDOLegacyEntity)getHome(entityInterfaceClass).idoFindByPrimaryKey(new Integer(id));
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
