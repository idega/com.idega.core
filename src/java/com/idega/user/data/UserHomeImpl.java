package com.idega.user.data;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;


public class UserHomeImpl extends com.idega.data.IDOFactory implements UserHome
{
 protected Class getEntityInterfaceClass(){
  return User.class;
 }


 public User create() throws javax.ejb.CreateException{
  return (User) super.createIDO();
 }


public User findByPersonalID(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindByPersonalID(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public User findUserForUserGroup(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserForUserGroup(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public java.util.Collection findUsersInQuery(com.idega.data.IDOQuery query)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersInQuery(query);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findAllUsers()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindAllUsers();
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findByNames(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindByNames(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersForUserRepresentativeGroups(p0);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
	
}

public User findUserForUserRepresentativeGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserForUserRepresentativeGroup(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public java.util.Collection findUsersInPrimaryGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersInPrimaryGroup(p0);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findUsersBySearchCondition(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersBySearchCondition(p0);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findUsersByYearOfBirth (int minYear, int maxYear)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersByYearOfBirth(minYear,maxYear);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public Collection findUsersBySearchCondition(String condition, String[] userIds) throws FinderException, RemoteException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersBySearchCondition(condition, userIds);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

public User findUserForUserGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserForUserGroup(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public User findUserFromEmail(java.lang.String p0)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserFromEmail(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public java.util.Collection findUsers(java.lang.String[] userIDs)throws javax.ejb.FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsers(userIDs);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

public java.util.Collection findAllUsersOrderedByFirstName()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindAllUsersOrderedByFirstName();
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getIDOEntityListForPrimaryKeys(ids);
}

 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (User) super.findByPrimaryKeyIDO(pk);
 }


public int getUserCount()throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((UserBMPBean)entity).ejbHomeGetUserCount();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getGroupType(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((UserBMPBean)entity).ejbHomeGetGroupType();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}