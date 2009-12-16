package com.idega.data;

import java.util.Collection;

import javax.ejb.FinderException;

public interface MetaDataHome extends com.idega.data.IDOHome {
	
 public MetaData create() throws javax.ejb.CreateException;
 public MetaData createLegacy();
 public MetaData findByPrimaryKey(int id) throws FinderException;
 public MetaData findByPrimaryKey(Object pk) throws FinderException;
 public MetaData findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

 public Collection<MetaData> findAllByMetaDataNameAndType(String name, String type) throws FinderException;
 public MetaData findByMetaDataNameAndValueAndType(String name, String value, String type) throws FinderException;
 
}