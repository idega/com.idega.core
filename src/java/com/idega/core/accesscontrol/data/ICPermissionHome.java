package com.idega.core.accesscontrol.data;


public interface ICPermissionHome extends com.idega.data.IDOHome
{
 public ICPermission create() throws javax.ejb.CreateException;
 public ICPermission createLegacy();
 public ICPermission findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}