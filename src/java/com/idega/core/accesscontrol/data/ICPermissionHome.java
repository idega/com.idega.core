package com.idega.core.accesscontrol.data;

import java.util.Collection;

import javax.ejb.FinderException;


public interface ICPermissionHome extends com.idega.data.IDOHome
{
 public ICPermission create() throws javax.ejb.CreateException;
 public ICPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByTypeAndContextValueAndPermissionGroupOrdered(java.lang.String p0,java.lang.String p1,com.idega.user.data.Group p2)throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByTypeAndContextValue(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByPermissionGroupAndPermissionStringAndTypeOrderedByContextValue(com.idega.user.data.Group p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByTypeAndPermissionGroupOrderedByContextValue(java.lang.String p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndTypeOrderedByContextValue(Collection groups,String permissionString, String contextType) throws FinderException;
		
 //needed for now
 public ICPermission createLegacy();
 public ICPermission findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;


}