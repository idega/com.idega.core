package com.idega.user.data;


public interface GroupTypeHome extends com.idega.data.IDOHome
{
 public GroupType create() throws javax.ejb.CreateException;
 public GroupType createLegacy();
 public GroupType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public GroupType findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public GroupType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}