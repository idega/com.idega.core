package com.idega.core.version.data;


public interface ICVersionTagHome extends com.idega.data.IDOHome
{
 public ICVersionTag create() throws javax.ejb.CreateException;
 public ICVersionTag findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

}