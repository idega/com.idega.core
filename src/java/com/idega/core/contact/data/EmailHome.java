package com.idega.core.contact.data;


public interface EmailHome extends com.idega.data.IDOHome
{
 public Email create() throws javax.ejb.CreateException;
 public Email createLegacy();
 public Email findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Email findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Email findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findEmailsForUser(com.idega.user.data.User p0)throws java.rmi.RemoteException,javax.ejb.FinderException;
 public java.util.Collection findEmailsForUser(int p0)throws javax.ejb.FinderException;
 public Email findEmailByAddress(java.lang.String p0)throws javax.ejb.FinderException;

}