package com.idega.user.data;


public class GenderHomeImpl extends com.idega.data.IDOFactory implements GenderHome
{
 protected Class getEntityInterfaceClass(){
  return Gender.class;
 }


 public Gender create() throws javax.ejb.CreateException{
  return (Gender) super.createIDO();
 }


public Gender findByGenderName(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((GenderBMPBean)entity).ejbFindByGenderName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public java.util.Collection findAllGenders()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GenderBMPBean)entity).ejbFindAllGenders();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public Gender findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Gender) super.findByPrimaryKeyIDO(pk);
 }


public com.idega.user.data.Gender getFemaleGender()throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	com.idega.user.data.Gender theReturn = ((GenderBMPBean)entity).ejbHomeGetFemaleGender();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public com.idega.user.data.Gender getMaleGender()throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	com.idega.user.data.Gender theReturn = ((GenderBMPBean)entity).ejbHomeGetMaleGender();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}