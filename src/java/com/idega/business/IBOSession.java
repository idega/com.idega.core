package com.idega.business;

import javax.ejb.EJBObject;

import com.idega.idegaweb.IWUserContext;
import java.rmi.RemoteException;

/**
 * Title:        idega Business Objects
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public interface IBOSession extends IBOService {
  public void setUserContext(IWUserContext iwuc) throws RemoteException;
  public IWUserContext getUserContext() throws RemoteException;
}