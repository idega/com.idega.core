package com.idega.user.data;


public interface UserGroupPlugInHome extends com.idega.data.IDOHome
{
 public UserGroupPlugIn create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public UserGroupPlugIn findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findRegisteredPlugInsForGroupType(com.idega.user.data.GroupType p0)throws javax.ejb.FinderException,com.idega.data.IDORelationshipException, java.rmi.RemoteException;
 public java.util.Collection findRegisteredPlugInsForGroupType(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException,com.idega.data.IDORelationshipException, java.rmi.RemoteException;
 public java.util.Collection findAllPlugIns()throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findRegisteredPlugInsForGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection findRegisteredPlugInsForUser(com.idega.user.data.User p0)throws javax.ejb.FinderException, java.rmi.RemoteException;

}