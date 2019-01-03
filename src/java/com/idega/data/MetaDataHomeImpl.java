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

 @Override
public MetaData create() throws javax.ejb.CreateException{
  return super.idoCreate(MetaData.class);
 }

 @Override
public MetaData createLegacy(){
	try {
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}
 }

 @Override
public MetaData findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (MetaData) super.idoFindByPrimaryKey(id);
 }

 @Override
public MetaData findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return super.idoFindByPrimaryKey(MetaData.class, pk);
 }

 @Override
public MetaData findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }

 	@Override
	public Collection<MetaData> findAllByMetaDataValue(String value) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Object> ids = ((MetaDataBMPBean) entity).ejbFindAllByMetaDataValue(value);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

 	@Override
	public Collection<MetaData> findAllByMetaDataNameAndType(String name, String type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Object> ids = ((MetaDataBMPBean) entity).ejbFindAllByMetaDataNameAndType(name, type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public MetaData findByMetaDataNameAndValueAndType(String name, String value, String type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((MetaDataBMPBean) entity).ejbFindByMetaDataNameAndValueAndType(name, value, type);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

}