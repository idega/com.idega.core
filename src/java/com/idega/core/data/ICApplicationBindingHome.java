package com.idega.core.data;


public interface ICApplicationBindingHome extends com.idega.data.IDOHome
{
 public ICApplicationBinding create() throws javax.ejb.CreateException;
 public ICApplicationBinding findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findByBindingType(java.lang.String p0)throws javax.ejb.FinderException;

}