package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;


public interface GroupHome extends com.idega.data.IDOHome
{
 public Group create() throws javax.ejb.CreateException;
 public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
// public Group createGroup()throws javax.ejb.CreateException, java.rmi.RemoteException;
 public java.util.Collection findGroups(java.lang.String[] p0)throws javax.ejb.FinderException;
// public Group findGroupByPrimaryKey(java.lang.Object p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public Group findSystemUsersGroup()throws javax.ejb.FinderException;
 public java.util.Collection findAllGroups(java.lang.String[] p0,boolean p1)throws javax.ejb.FinderException;
 public java.lang.String getGroupType();
 public java.util.Collection findGroupsContained(com.idega.user.data.Group p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException;
 public java.util.Collection findTopNodeGroupsContained(com.idega.builder.data.IBDomain p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException;
 public int getNumberOfGroupsContained(com.idega.user.data.Group p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException, com.idega.data.IDOException;
 public int getNumberOfTopNodeGroupsContained(com.idega.builder.data.IBDomain p0, java.util.Collection p1, boolean p2)throws javax.ejb.FinderException, com.idega.data.IDOException;
 public java.util.Collection findGroupsByType(java.lang.String p0)throws javax.ejb.FinderException;
 public Collection findGroupsByMetaData(String key, String value) throws FinderException;
}