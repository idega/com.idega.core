package com.idega.core.component.data;


public interface ICObjectHome extends com.idega.data.IDOHome
{
 public ICObject create() throws javax.ejb.CreateException;
 public ICObject createLegacy();
 public ICObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICObject findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAllByObjectType(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllByObjectTypeAndBundle(java.lang.String p0,String bundle)throws javax.ejb.FinderException;
 public java.util.Collection findAllByBundle(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllBlocksByBundle(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllElementsByBundle(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllElements()throws javax.ejb.FinderException;
 public java.util.Collection findAllBlocks()throws javax.ejb.FinderException;
 public ICObject findByClassName(java.lang.String p0)throws javax.ejb.FinderException;

}