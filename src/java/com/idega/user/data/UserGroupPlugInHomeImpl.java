package com.idega.user.data;


public class UserGroupPlugInHomeImpl extends com.idega.data.IDOFactory implements UserGroupPlugInHome
{
 protected Class getEntityInterfaceClass(){
  return UserGroupPlugIn.class;
 }


 public UserGroupPlugIn create() throws javax.ejb.CreateException{
  return (UserGroupPlugIn) super.createIDO();
 }


public java.util.Collection findRegisteredPlugInsForGroupType(com.idega.user.data.GroupType p0)throws javax.ejb.FinderException,com.idega.data.IDORelationshipException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserGroupPlugInBMPBean)entity).ejbFindRegisteredPlugInsForGroupType(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findRegisteredPlugInsForGroupType(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException,com.idega.data.IDORelationshipException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserGroupPlugInBMPBean)entity).ejbFindRegisteredPlugInsForGroupType(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPlugIns()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserGroupPlugInBMPBean)entity).ejbFindAllPlugIns();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findRegisteredPlugInsForGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserGroupPlugInBMPBean)entity).ejbFindRegisteredPlugInsForGroup(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findRegisteredPlugInsForUser(com.idega.user.data.User p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserGroupPlugInBMPBean)entity).ejbFindRegisteredPlugInsForUser(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public UserGroupPlugIn findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (UserGroupPlugIn) super.findByPrimaryKeyIDO(pk);
 }



}