package com.idega.core.contact.data;


public interface CountryCodeHome extends com.idega.data.IDOHome
{
 public CountryCode create() throws javax.ejb.CreateException;
 public CountryCode createLegacy();
 public CountryCode findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public CountryCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public CountryCode findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}