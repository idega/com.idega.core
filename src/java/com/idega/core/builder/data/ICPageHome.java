package com.idega.core.builder.data;


public interface ICPageHome extends com.idega.data.IDOHome
{
 public ICPage create() throws javax.ejb.CreateException;
 public ICPage createLegacy();
 public ICPage findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
/**
 * @param integer
 * @return
 */
public java.util.Collection findByTemplate(Integer integer)throws javax.ejb.FinderException;

}