package com.idega.core.data;


public interface ICObjectTypeHome extends com.idega.data.IDOHome
{
 public ICObjectType create() throws javax.ejb.CreateException;
 public ICObjectType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;

}