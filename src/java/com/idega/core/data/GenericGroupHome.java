package com.idega.core.data;


public interface GenericGroupHome extends com.idega.data.IDOHome
{
 public GenericGroup create() throws javax.ejb.CreateException;
 public GenericGroup createLegacy();
 public GenericGroup findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public GenericGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public GenericGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}