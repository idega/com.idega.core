package com.idega.core.accesscontrol.data;


public class LoginRecordHomeImpl extends com.idega.data.IDOFactory implements LoginRecordHome
{
 protected Class getEntityInterfaceClass(){
  return LoginRecord.class;
 }


 public LoginRecord create() throws javax.ejb.CreateException{
  return (LoginRecord) super.createIDO();
 }


public java.util.Collection findAllLoginRecords(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((LoginRecordBMPBean)entity).ejbFindAllLoginRecords(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public LoginRecord findByLoginID(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((LoginRecordBMPBean)entity).ejbFindByLoginID(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public LoginRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (LoginRecord) super.findByPrimaryKeyIDO(pk);
 }



}