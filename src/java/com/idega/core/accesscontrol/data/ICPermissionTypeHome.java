package com.idega.core.accesscontrol.data;


public interface ICPermissionTypeHome extends com.idega.data.IDOHome
{
 public ICPermissionType create() throws javax.ejb.CreateException;
 public ICPermissionType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllPermissionsTypes()throws javax.ejb.FinderException;

}