package com.idega.data;

import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;
import javax.ejb.EJBException;

import com.idega.business.IBOLookup;


/**
 * Title:        idegaclasses
 * Title:        idega Data Objects
 * Description:  IDOLookup is a class use to look up and get a reference to instances of IDO (BMP Entity EJB Beans) objects.<br><br>
 * <br>IDOLookup should be used mainly for looking up home instances for a data bean.
 * <br>Instances of IDO home classes are obtained by passing either a Class representing a bean interface or implementation. (For example User.class or UserBMPBean.class) to the getHome() method.
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOLookup extends IBOLookup{

  private static IDOLookup idoInstance;
  private static IDOLookup getInstance(){
    return getIDOLookupInstance();
  }

  private static IDOLookup getIDOLookupInstance(){
    if(idoInstance==null){
      idoInstance = new IDOLookup();
    }
    return idoInstance;
  }

  private final String BMP_BEAN_SUFFIX = "BMPBean";
  protected String getBeanSuffix(){
    return BMP_BEAN_SUFFIX;
  }

  private IDOLookup() {
  }



  /**
   * Gets an instance of the implementation of the Home interface for the data bean.
   * <br>The object retured can then needs to be casted to the specific home interface for the bean.
   * @param entityInterfaceClass i the (Remote) interface of the data bean.
   */
  public static IDOHome getHome(Class entityInterfaceClass)throws RemoteException{
    return (IDOHome)getIDOLookupInstance().getEJBHomeInstance(entityInterfaceClass);
  }




  public static IDOHome getHomeLegacy(Class entityInterfaceClass){
    try{
      return getHome(entityInterfaceClass);
    }
    catch(RemoteException e){
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Gets the Class object for the (Remote) interface of a data bean.
   * @param entityBeanOrInterfaceClass can be either the BMP bean class or the interface class itself.
   */
  public static Class getInterfaceClassFor(Class entityBeanOrInterfaceClass){
    return getIDOLookupInstance().getInterfaceClassForNonStatic(entityBeanOrInterfaceClass);
  }

    /**
   * Gets the Class object for the (BMP) bean class of a data bean.
   * @param entityInterfaceClass i the (Remote) interface of the data bean.
   */
  public static Class getBeanClassFor(Class entityInterfaceClass){
    return getIDOLookupInstance().getBeanClassForNonStatic(entityInterfaceClass);
  }




  public static IDOEntity create(Class entityInterfaceClass)throws RemoteException,javax.ejb.CreateException{
    return getHome(entityInterfaceClass).createIDO();
  }

  public static IDOEntity findByPrimaryKey(Class entityInterfaceClass,int id)throws RemoteException,javax.ejb.FinderException{
    return getHome(entityInterfaceClass).findByPrimaryKeyIDO(new Integer(id));
  }

  public static IDOEntity findByPrimaryKey(Class entityInterfaceClass,Integer id)throws RemoteException,javax.ejb.FinderException{
    return getHome(entityInterfaceClass).findByPrimaryKeyIDO(id);
  }


   public static IDOLegacyEntity createOld(Class entityInterfaceOrBeanClass){
      //return createNew(entityInterfaceClass);
      if(entityInterfaceOrBeanClass.isInterface()){
        return createLegacy(getBeanClassFor(entityInterfaceOrBeanClass));
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
      return (IDOLegacyEntity)getHome(entityInterfaceClass).createIDO();
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
      return (IDOLegacyEntity)getHome(entityInterfaceClass).findByPrimaryKeyIDO(new Integer(id));
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
      IDOLegacyEntity entity = (IDOLegacyEntity)getHome(entityInterfaceClass).createIDO();
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
      return (IDOLegacyEntity)getHome(entityInterfaceClass).findByPrimaryKeyIDO(id);
    }
    catch(Exception e){
      throw new java.sql.SQLException(e.getMessage());
    }
  }


  public IDOEntityDefinition getEntityDefinitionForClass(Class entityInterfaceClass)throws RemoteException{
    return GenericEntity.getStaticInstance(entityInterfaceClass).getEntityDefinition();
  }

  static IDOLegacyEntity instanciateEntity(Class entityBeanOrInterfaceClass){
    try{
      Class beanClass = entityBeanOrInterfaceClass;
      if(beanClass.isInterface()){
        beanClass = getBeanClassFor(entityBeanOrInterfaceClass);
      }
      return (IDOLegacyEntity)beanClass.newInstance();
    }
    catch(Exception e){
      throw new EJBException(e.getMessage());
    }
  }

}
