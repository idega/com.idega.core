package com.idega.core.accesscontrol.data;


public interface ICPermissionHome extends com.idega.data.IDOHome
{
 public ICPermission create() throws javax.ejb.CreateException;
 public ICPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByContextTypeAndContextValueAndPermissionGroupOrdered(java.lang.String p0,java.lang.String p1,com.idega.user.data.Group p2)throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByContextTypeAndContextValue(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(com.idega.user.data.Group p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(java.lang.String p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException;
 //needed for now
 public ICPermission createLegacy();
 public ICPermission findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;


}