package com.idega.user.data;


public class StatusHomeImpl extends com.idega.data.IDOFactory implements StatusHome
{
 protected Class getEntityInterfaceClass(){
  return Status.class;
 }


 public Status create() throws javax.ejb.CreateException{
  return (Status) super.createIDO();
 }


public java.util.Collection findAll()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((StatusBMPBean)entity).ejbFindAll();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public Status findByStatusKey(String statusKey)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((StatusBMPBean)entity).ejbFindByStatusKey(statusKey);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public Status findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Status) super.findByPrimaryKeyIDO(pk);
 }



}