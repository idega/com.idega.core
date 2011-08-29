package com.idega.data;

import java.util.Collection;

import javax.ejb.FinderException;


public class MetaDataHomeImpl extends com.idega.data.IDOFactory implements MetaDataHome {
	
 /**
	 * 
	 */
	private static final long serialVersionUID = -2779913911896861198L;

@Override
 protected Class<MetaData> getEntityInterfaceClass(){
  return MetaData.class;
 }

 public MetaData create() throws javax.ejb.CreateException{
  return (MetaData) super.idoCreate(MetaData.class);
 }

 public MetaData createLegacy(){
	try {
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}
 }

 public MetaData findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (MetaData) super.idoFindByPrimaryKey(id);
 }

 public MetaData findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (MetaData) super.idoFindByPrimaryKey(MetaData.class, pk);
 }

 public MetaData findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }

 	public Collection<MetaData> findAllByMetaDataNameAndType(String name, String type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Object> ids = ((MetaDataBMPBean) entity).ejbFindAllByMetaDataNameAndType(name, type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public MetaData findByMetaDataNameAndValueAndType(String name, String value, String type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((MetaDataBMPBean) entity).ejbFindByMetaDataNameAndValueAndType(name, value, type);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

}