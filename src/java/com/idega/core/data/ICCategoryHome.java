package com.idega.core.data;


public interface ICCategoryHome extends com.idega.data.IDOHome
{
 public ICCategory create() throws javax.ejb.CreateException;
 public ICCategory createLegacy();
 public ICCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICCategory findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public boolean setOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1,int p2)throws com.idega.data.IDOException;
 public int getOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1)throws javax.ejb.FinderException;
 public java.util.List getListOfCategoryForObjectInstance(com.idega.core.data.ICObjectInstance p0,boolean p1)throws javax.ejb.FinderException;

}