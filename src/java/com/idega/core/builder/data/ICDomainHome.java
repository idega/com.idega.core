package com.idega.core.builder.data;


public interface ICDomainHome extends com.idega.data.IDOHome
{
 public ICDomain create() throws javax.ejb.CreateException;
 public ICDomain createLegacy();
 public ICDomain findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICDomain findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICDomain findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAllDomains()throws javax.ejb.FinderException;
 public java.util.Collection findAllDomainsByServerName(String serverName)throws javax.ejb.FinderException;
}