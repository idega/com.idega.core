package com.idega.core.contact.data;

import java.util.Collection;


public interface PhoneHome extends com.idega.data.IDOHome
{
 public Phone create() throws javax.ejb.CreateException;
 public Phone createLegacy();
 public Phone findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Phone findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Phone findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 
public Phone findUsersHomePhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;
public Phone findUsersWorkPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;
public Phone findUsersMobilePhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;
public Phone findUsersFaxPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException;
public Collection findUsersPhones(int userId,int type);
}