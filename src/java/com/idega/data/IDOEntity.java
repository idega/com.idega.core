package com.idega.data;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

/**
 * Title:        idega Data Objects
 * Description:  A class to be used as a base interface for IDO  (BMP EJB Entity) beans
 * Copyright:    Copyright (c) 2001-2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IDOEntity extends EJBObject,javax.ejb.EJBLocalObject{

  //public Object getId() throws RemoteException;
  //public String getName() throws RemoteException;

  /*public void addTo(IDOEntity entity) throws RemoteException;
  public void removeFrom(IDOEntity entity) throws RemoteException;
  */
  public void store() throws IDOStoreException,RemoteException;
  //public void remove() throws javax.ejb.RemoveException,RemoteException;
  public IDOEntityDefinition getEntityDefinition() throws RemoteException;
}
