package com.idega.core.data;


public interface PhoneHome extends com.idega.data.IDOHome
{
 public Phone create() throws javax.ejb.CreateException;
 public Phone createLegacy();
 public Phone findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Phone findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Phone findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}