package com.idega.core.category.data;


public interface ICInformationFolderHome extends com.idega.data.IDOHome
{
 public ICInformationFolder create() throws javax.ejb.CreateException;
 public ICInformationFolder createLegacy();
 public ICInformationFolder findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICInformationFolder findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICInformationFolder findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public void removeObjectInstanceRelation(com.idega.core.component.data.ICObjectInstance p0)throws com.idega.data.IDORemoveRelationshipException;

}