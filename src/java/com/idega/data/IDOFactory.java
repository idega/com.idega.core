package com.idega.data;

import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5 UNFINISHED - UNDER DEVELOPMENT
 */

public abstract class IDOFactory implements IDOHome{

  protected IDOFactory(){
  }

  public IDOEntity idoCreate(Class entityInterfaceClass)throws javax.ejb.CreateException{
    Class beanClass = IDOLookup.getBeanClassFor(entityInterfaceClass);
    try{
      return IDOContainer.getInstance().createEntity(entityInterfaceClass);
      //return (IDOEntity)beanClass.newInstance();
    }
    catch(Exception e){
      throw new javax.ejb.CreateException(e.getMessage());
    }
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,int id)throws javax.ejb.FinderException{
    try{
      IDOEntity theReturn = idoCreate(entityInterfaceClass);
      ((GenericEntity)theReturn).findByPrimaryKey(id);
      return theReturn;
    }
    catch(Exception e){
      throw new javax.ejb.FinderException(e.getMessage());
    }
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,Integer id)throws javax.ejb.FinderException{
    return idoFindByPrimaryKey(entityInterfaceClass,id.intValue());
  }

  public IDOEntity idoFindByPrimaryKey(Class entityInterfaceClass,Object pk)throws javax.ejb.FinderException{
    if(pk instanceof Integer){
      return idoFindByPrimaryKey(entityInterfaceClass,(Integer)pk);
    }
    else{
      throw new RuntimeException("[idoFactory] : Primarykey other than type Integer not supported");
    }
  }

  public IDOEntity idoCreate() throws CreateException{
    return idoCreate(getEntityInterfaceClass());
  }

  public IDOEntity idoFindByPrimaryKey(int primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  public IDOEntity idoFindByPrimaryKey(Integer primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  public IDOEntity idoFindByPrimaryKey(Object primaryKey) throws FinderException{
    return idoFindByPrimaryKey(getEntityInterfaceClass(),primaryKey);
  }

  public List findAll() throws FinderException{
    return null;
  }

  /**
   * @todo: implement
   */
  public EJBMetaData getEJBMetaData(){return null;}

  /**
   * @todo: implement
   */
  public HomeHandle getHomeHandle(){return null;}

  /**
   * @todo: implement
   */
  public void remove(Handle handle){}

  public void remove(Object primaryKey){
    try{
      if(primaryKey instanceof Integer){
        IDOEntity entity = idoFindByPrimaryKey((Integer)primaryKey);
        entity.remove();
      }
      else{
        throw new javax.ejb.EJBException("Primary Keys other than java.lang.Integer not supported");
      }
    }
    catch(Exception e){
      throw new javax.ejb.EJBException(e.getMessage());
    }
  }

  protected abstract Class getEntityInterfaceClass();

}