package com.idega.user.data;


public class UserStatusHomeImpl extends com.idega.data.IDOFactory implements UserStatusHome
{
 protected Class getEntityInterfaceClass(){
  return UserStatus.class;
 }


 public UserStatus create() throws javax.ejb.CreateException{
  return (UserStatus) super.createIDO();
 }


public java.util.Collection findAll()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserStatusBMPBean)entity).ejbFindAll();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByGroupId(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserStatusBMPBean)entity).ejbFindAllByGroupId(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByUserId(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserStatusBMPBean)entity).ejbFindAllByUserId(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByUserIdAndGroupId(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserStatusBMPBean)entity).ejbFindAllByUserIdAndGroupId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public UserStatus findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (UserStatus) super.findByPrimaryKeyIDO(pk);
 }



}