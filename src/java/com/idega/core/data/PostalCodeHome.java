package com.idega.core.data;


public interface PostalCodeHome extends com.idega.data.IDOHome
{
 public PostalCode create() throws javax.ejb.CreateException;
 public PostalCode createLegacy();
 public PostalCode findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public PostalCode findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}