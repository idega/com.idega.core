package com.idega.core.accesscontrol.data;


public class LoginRecordHomeImpl extends com.idega.data.IDOFactory implements LoginRecordHome
{
 protected Class getEntityInterfaceClass(){
  return LoginRecord.class;
 }

 public LoginRecord create() throws javax.ejb.CreateException{
  return (LoginRecord) super.idoCreate();
 }

 public LoginRecord createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public LoginRecord findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (LoginRecord) super.idoFindByPrimaryKey(id);
 }

 public LoginRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (LoginRecord) super.idoFindByPrimaryKey(pk);
 }

 public LoginRecord findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }

public java.util.Collection findAllLoginRecords(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((LoginRecordBMPBean)entity).ejbFindAllLoginRecords(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}


}