package com.idega.core.user.data;


public interface PrimaryUserGroupHome extends com.idega.data.IDOHome
{
 public PrimaryUserGroup create() throws javax.ejb.CreateException;
 public PrimaryUserGroup createLegacy();
 public PrimaryUserGroup findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public PrimaryUserGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public PrimaryUserGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}