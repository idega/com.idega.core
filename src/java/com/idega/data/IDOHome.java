package com.idega.data;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5 UNFINISHED - UNDER DEVELOPMENT
 */

public interface IDOHome extends EJBLocalHome{//EJBHome {

  public IDOEntity createIDO() throws CreateException;
  //public IDOEntity createIDO() throws CreateException, RemoteException;
    
  
  /*public IDOEntity idoFindByPrimaryKey(int primaryKey) throws RemoteException, FinderException;*/
  public IDOEntity findByPrimaryKeyIDO(Object primaryKey) throws FinderException;
  public Collection findByPrimaryKeyCollection(Collection primaryKey) throws FinderException;
  //public IDOEntity findByPrimaryKeyIDO(Object primaryKey) throws RemoteException, FinderException;
  
  /*public List findAll() throws RemoteException, FinderException;*/
  public Object decode(String pkString);
  public Collection decode(String[] primaryKeys);
}
