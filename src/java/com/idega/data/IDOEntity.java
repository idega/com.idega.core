package com.idega.data;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5 UNFINISHED - UNDER DEVELOPMENT
 */

public interface IDOEntity extends EJBObject{

  //public Object getId() throws RemoteException;
  //public String getName() throws RemoteException;

  /*public void addTo(IDOEntity entity) throws RemoteException;
  public void removeFrom(IDOEntity entity) throws RemoteException;
  */
  public void store() throws RemoteException;
  public void remove() throws RemoteException;

}