package com.idega.core.component.data;


public interface ICObjectTypeHome extends com.idega.data.IDOHome
{
 public ICObjectType create() throws javax.ejb.CreateException;
 public ICObjectType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;
 public boolean updateClassReferences(java.lang.String p0,java.lang.Class p1)throws com.idega.data.IDOException;
 public boolean updateStartData()throws com.idega.data.IDOException;

}