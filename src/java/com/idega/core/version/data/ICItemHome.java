package com.idega.core.version.data;


public interface ICItemHome extends com.idega.data.IDOHome
{
 public ICItem create() throws javax.ejb.CreateException;
 public ICItem findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

}