package com.idega.data;

import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001-2002
 *  Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class IDOFactory implements IDOHome,java.io.Serializable{

  protected IDOFactory(){
  }

  public IDOEntity idoCreate(Class entityInterfaceClass)throws javax.ejb.CreateException{
    //Class beanClass = IDOLookup.getBeanClassFor(entityInterfaceClass);
    try{
      IDOEntityBean entity = null;
      try{
        entity = (IDOEntityBean)IDOContainer.getInstance().createEntity(entityInterfaceClass);
      }
      catch(Error e){
        System.err.println("Error creating bean for : "+this.getClass().getName());
        e.printStackTrace();
      }
      entity.setEJBLocalHome(this);
      return (IDOEntity)entity;
      //return (IDOEntity)beanClass.newInstance();
    }
    catch(Exception e){
      e.printStackTrace();
      throw new javax.ejb.CreateException(e.getMessage());
    }
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,int id)throws javax.ejb.FinderException{
    return this.idoFindByPrimaryKey(entityInterfaceClass,new Integer(id));
    /*try{
      IDOEntity theReturn = idoCreate(entityInterfaceClass);
      ((IDOLegacyEntity)theReturn).findByPrimaryKey(id);
      return theReturn;
    }
    catch(Exception e){
      throw new javax.ejb.FinderException(e.getMessage());
    }*/
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,Integer id)throws javax.ejb.FinderException{
    //return idoFindByPrimaryKey(entityInterfaceClass,id.intValue());
        return idoFindByPrimaryKey(entityInterfaceClass,(Object)id);
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,Object pk)throws javax.ejb.FinderException{
      IDOEntity theReturn = IDOContainer.getInstance().findByPrimaryKey(entityInterfaceClass,pk,this);
      return theReturn;

    /*if(pk instanceof Integer){
      return idoFindByPrimaryKey(entityInterfaceClass,(Integer)pk);
    }
    else{
      throw new IDOFinderException("[idoFactory] : Primarykey other than type Integer not supported");
    }*/
  }


  public IDOEntity createIDO() throws CreateException{
    return idoCreate(getEntityInterfaceClass());
  }

  /**
   * @deprecated
   */
  public IDOEntity idoCreate() throws CreateException{
    return createIDO();
  }

  public IDOEntity idoFindByPrimaryKey(int primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  public IDOEntity idoFindByPrimaryKey(Integer primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  /**
   * @deprecated
   */
  public IDOEntity idoFindByPrimaryKey(Object primaryKey) throws FinderException{
    return findByPrimaryKeyIDO(primaryKey);
  }

  public IDOEntity findByPrimaryKeyIDO(Object primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  public IDOEntity findByPrimaryKeyIDO(int primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
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
    try{
      IDOEntity entity = idoFindByPrimaryKey(primaryKey);
      entity.remove();
    }
    catch(Exception e){
      throw new javax.ejb.EJBException(e.getMessage());
    }
  }

  protected abstract Class getEntityInterfaceClass();

  protected Class getEntityBeanClass(){
    return IDOLookup.getBeanClassFor(getEntityInterfaceClass());
  }


  /**
   *
   * @param setOfPrimaryKeys
   * @return Set of IDOEntity objects for this Factory
   * @throws FinderException
   */
  protected Set getEntitySetForPrimaryKeys(Set setOfPrimaryKeys)throws FinderException{
    Set theReturn = new java.util.HashSet();
    Iterator iter = setOfPrimaryKeys.iterator();
    while (iter.hasNext()) {
      Object pk = iter.next();
      IDOEntity entityObject = this.idoFindByPrimaryKey(pk);
      theReturn.add(entityObject);
    }
    return theReturn;
  }

  /**
   *
   * @param collectionOfPrimaryKeys
   * @return Collection of IDOEntity objects for this Factory
   * @throws FinderException
   */
  protected Collection getEntityCollectionForPrimaryKeys(Collection collectionOfPrimaryKeys)throws FinderException{
    Collection theReturn = new Vector();
    Iterator iter = collectionOfPrimaryKeys.iterator();
    while (iter.hasNext()) {
      Object pk = iter.next();
      IDOEntity entityObject = this.idoFindByPrimaryKey(pk);
      theReturn.add(entityObject);
    }
    return theReturn;
  }

    /**
   *
   * @param collectionOfPrimaryKeys
   * @return Collection of IDOEntity objects for this Factory
   * @throws FinderException
   */
  protected Collection getIDOEntityListForPrimaryKeys(Collection collectionOfPrimaryKeys)throws FinderException{
    Collection theReturn = new IDOEntityList(this,collectionOfPrimaryKeys);
//    Iterator iter = collectionOfPrimaryKeys.iterator();
//    while (iter.hasNext()) {
//      Object pk = iter.next();
//      IDOEntity entityObject = this.idoFindByPrimaryKey(pk);
//      theReturn.add(entityObject);
//    }
    return theReturn;
  }

  protected IDOEntity idoCheckOutPooledEntity(){
    /**
     * @todo: Change implementation
     */
    return com.idega.data.GenericEntity.getStaticInstance(this.getEntityInterfaceClass());
  }


  protected void idoCheckInPooledEntity(IDOEntity entity){
    /**
     * @todo: implement
     */
  }

}
