package com.idega.core.data;


public class ICObjectHomeImpl extends com.idega.data.IDOFactory implements ICObjectHome
{
 protected Class getEntityInterfaceClass(){
  return ICObject.class;
 }


 public ICObject create() throws javax.ejb.CreateException{
  return (ICObject) super.createIDO();
 }


 public ICObject createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findAllByObjectType(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICObjectBMPBean)entity).ejbFindAllByObjectType(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public ICObject findByClassName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICObjectBMPBean)entity).ejbFindByClassName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public ICObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICObject) super.findByPrimaryKeyIDO(pk);
 }


 public ICObject findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICObject) super.findByPrimaryKeyIDO(id);
 }


 public ICObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}