package com.idega.core.data;


public interface EmailHome extends com.idega.data.IDOHome
{
 public Email create() throws javax.ejb.CreateException;
 public Email createLegacy();
 public Email findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Email findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Email findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}