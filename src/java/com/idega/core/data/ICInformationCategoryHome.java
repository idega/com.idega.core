package com.idega.core.data;


public interface ICInformationCategoryHome extends com.idega.data.IDOHome
{
 public ICInformationCategory create() throws javax.ejb.CreateException;
 public ICInformationCategory createLegacy();
 public ICInformationCategory findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public ICInformationCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ICInformationCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}