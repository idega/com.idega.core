package com.idega.core.data;


public interface CountryHome extends com.idega.data.IDOHome
{
 public Country create() throws javax.ejb.CreateException;
 public Country createLegacy();
 public Country findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Country findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}