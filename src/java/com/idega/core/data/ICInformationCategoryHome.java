package com.idega.core.data;


public interface ICInformationCategoryHome extends com.idega.data.IDOHome
{
 public ICInformationCategory create() throws javax.ejb.CreateException;
 public ICInformationCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAvailableCategories(int p0,int p1)throws javax.ejb.FinderException;
 public java.util.Collection findAvailableTopNodeCategories(int p0,int p1)throws javax.ejb.FinderException;

}