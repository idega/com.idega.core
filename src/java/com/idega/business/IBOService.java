package com.idega.business;

import javax.ejb.EJBObject;
import java.util.Locale;

import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import com.idega.idegaweb.IWApplicationContext;

/**
 * Title:        idega Business Objects
 * Description:  A class to be used as a base interface for IBO Service (Stateless EJB Session) beans
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */

public interface IBOService extends EJBObject {

	public void initializeBean();
	public void addActionListener(ActionListener listener); 
	public String getServiceDescription()throws RemoteException;
  public String getLocalizedServiceDescription(Locale locale)throws RemoteException;
  public IWApplicationContext getIWApplicationContext()throws RemoteException;

}