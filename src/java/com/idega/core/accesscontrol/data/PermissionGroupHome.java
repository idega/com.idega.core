package com.idega.core.accesscontrol.data;


public interface PermissionGroupHome extends com.idega.data.IDOHome
//public interface PermissionGroupHome extends com.idega.user.data.GroupHome
{
 public PermissionGroup create() throws javax.ejb.CreateException;
 public PermissionGroup createLegacy();
 public PermissionGroup findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public PermissionGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public PermissionGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
}