package com.idega.core.user.data;


public interface UserGroupRepresentativeHome extends com.idega.data.IDOHome
{
 public UserGroupRepresentative create() throws javax.ejb.CreateException;
 public UserGroupRepresentative createLegacy();
 public UserGroupRepresentative findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public UserGroupRepresentative findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public UserGroupRepresentative findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}