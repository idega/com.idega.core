package com.idega.user.data;


public class UserHomeImpl extends com.idega.data.IDOFactory implements UserHome
{
 protected Class getEntityInterfaceClass(){
  return User.class;
 }


 public User create() throws javax.ejb.CreateException{
  return (User) super.createIDO();
 }


public java.util.Collection findAllUsers()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindAllUsers();
	this.idoCheckInPooledEntity(entity);
//	return this.getEntityCollectionForPrimaryKeys(ids);
        return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersForUserRepresentativeGroups(p0);
	this.idoCheckInPooledEntity(entity);
//	return this.getEntityCollectionForPrimaryKeys(ids);
        return this.getIDOEntityListForPrimaryKeys(ids);

}

public java.util.Collection findUsersInPrimaryGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersInPrimaryGroup(p0);
	this.idoCheckInPooledEntity(entity);
//	return this.getEntityCollectionForPrimaryKeys(ids);
        return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findAllUsersOrderedByFirstName()throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindAllUsersOrderedByFirstName();
	this.idoCheckInPooledEntity(entity);
//	return this.getEntityCollectionForPrimaryKeys(ids);
        return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findAllByNames(String first,String middle,String last)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindByNames(first,middle,last);
	this.idoCheckInPooledEntity(entity);
  return this.getIDOEntityListForPrimaryKeys(ids);
}


public User findUserFromEmail(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserFromEmail(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public User findUserForUserGroup(int groupID)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserForUserGroup(groupID);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public User findUserForUserGroup(Group userGroup)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserForUserGroup(userGroup);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public User findByPersonalID(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindByPersonalID(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (User) super.findByPrimaryKeyIDO(pk);
 }



}