package com.idega.business;

import java.rmi.RemoteException;

import com.idega.idegaweb.IWUserContext;

/**
 * Title:        idega Business Objects
 * Description:  A class to be a base interface for IBO Session (Stateful EJB Session) beans<br><br>
 * An instance of this type is stored in session for the user and an instance is to be obtained via IBOLookup
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public interface IBOSession extends IBOService {
  public void setUserContext(IWUserContext iwuc) throws RemoteException;
  public IWUserContext getUserContext() throws RemoteException;
}