package com.idega.core.accesscontrol.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.user.data.Group;


public interface ICPermissionHome extends com.idega.data.IDOHome {

 public ICPermission create() throws javax.ejb.CreateException;
 public ICPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Collection<ICPermission> findAllPermissionsByContextTypeAndContextValueAndPermissionGroupOrdered(java.lang.String p0,java.lang.String p1,com.idega.user.data.Group p2)throws javax.ejb.FinderException;
 public Collection<ICPermission> findAllPermissionsByContextTypeAndContextValue(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;
 public Collection<ICPermission> findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(com.idega.user.data.Group p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public Collection<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(java.lang.String p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException;
 public Collection<ICPermission> findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection<com.idega.user.data.Group> groups,String permissionString, String contextType) throws FinderException;
 public Collection<ICPermission> findAllPermissionsByContextTypeAndContextValueAndPermissionString(java.lang.String p0,java.lang.String p1,String p3)throws javax.ejb.FinderException;
 public ICPermission findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue(Group group,String permissionString, String contextType, String contextValue) throws FinderException;
 public Collection<ICPermission> findAllGroupPermissionsToInheritByGroupCollection(Collection<com.idega.user.data.Group> groups) throws FinderException;
 public Collection<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupCollectionOrderedByContextValue(java.lang.String p0,Collection p1)throws javax.ejb.FinderException;
 public Collection<ICPermission> findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup(String contextType, String contextValue, Collection permissionStrings, Group group) throws FinderException;

 public Collection<ICPermission> findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(com.idega.user.data.Group p0,Collection p1,java.lang.String p2)throws javax.ejb.FinderException;
 public Collection<ICPermission> findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection<com.idega.user.data.Group> groups,Collection permissionString, String contextType) throws FinderException;

 public ICPermission createLegacy();
 public ICPermission findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}