package com.idega.core.accesscontrol.data;


public interface ICObjectPermissionHome extends com.idega.data.IDOHome
{
 public ICObjectPermission create() throws javax.ejb.CreateException;
 public ICObjectPermission createLegacy();
 public ICObjectPermission findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICObjectPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICObjectPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}