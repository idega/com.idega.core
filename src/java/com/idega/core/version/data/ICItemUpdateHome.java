package com.idega.core.version.data;


public interface ICItemUpdateHome extends com.idega.data.IDOHome
{
 public ICItemUpdate create() throws javax.ejb.CreateException;
 public ICItemUpdate findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

}