package com.idega.data;

import javax.ejb.*;
import java.rmi.RemoteException;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5 UNFINISHED - UNDER DEVELOPMENT
 */

public interface IDOEntityBean extends EntityBean{

  //public Object getId() throws RemoteException;
  //public String getName() throws RemoteException;

  /*public void addTo(IDOEntity entity) throws RemoteException;
  public void removeFrom(IDOEntity entity) throws RemoteException;
  */
  public Object ejbCreate() throws CreateException;
  //public Object ejbCreate(Object pk) throws CreateException;
  public Object ejbFindByPrimaryKey(Object pk) throws FinderException;
  public void setEJBHome(javax.ejb.EJBHome ejbHome);


}
