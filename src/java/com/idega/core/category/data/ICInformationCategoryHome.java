package com.idega.core.category.data;


public interface ICInformationCategoryHome extends com.idega.data.IDOHome
{
 public ICInformationCategory create() throws javax.ejb.CreateException;
 public ICInformationCategory createLegacy();
 public ICInformationCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICInformationCategory findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICInformationCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public java.util.Collection findAvailableCategories(int p0,int p1)throws javax.ejb.FinderException;
 public java.util.Collection findAvailableTopNodeCategories(int p0,int p1)throws javax.ejb.FinderException;
 public void removeObjectInstanceRelation(com.idega.core.component.data.ICObjectInstance p0)throws com.idega.data.IDORemoveRelationshipException;

}