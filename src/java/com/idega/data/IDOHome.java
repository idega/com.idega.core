package com.idega.data;

import javax.ejb.*;
import java.util.List;
import java.rmi.RemoteException;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 0.5 UNFINISHED - UNDER DEVELOPMENT
 */

public interface IDOHome extends EJBHome {

  public IDOEntity idoCreate() throws CreateException, RemoteException;
  /*public IDOEntity idoFindByPrimaryKey(int primaryKey) throws RemoteException, FinderException;*/
  public IDOEntity idoFindByPrimaryKey(Object primaryKey) throws RemoteException, FinderException;
  public List findAll() throws RemoteException, FinderException;

}
