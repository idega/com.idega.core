package com.idega.user.data;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOLookupException;
import com.idega.util.IWTimestamp;


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

	public User findByPartOfPersonalIDAndFirstName(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean)entity).ejbFindByPartOfPersonalIDAndFirstName(p0,p1);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}


public User findUserForUserGroup(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((UserBMPBean)entity).ejbFindUserForUserGroup(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public java.util.Collection findUsersByMetaData(String key, String value) throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersByMetaData(key,value);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findUsersInQuery(com.idega.data.IDOQuery query)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersInQuery(query);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllUsers()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindAllUsers();
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public Collection findNewestUsers(int returningNumberOfRecords, int startingRecord) throws FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindNewestUsers(returningNumberOfRecords, startingRecord);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findByNames(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindByNames(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersForUserRepresentativeGroups(p0);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
	
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
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findUsersBySearchCondition(java.lang.String p0, boolean p1)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersBySearchCondition(p0, p1);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findUsersBySearchConditionAndAge(java.lang.String p0, boolean p1, int endAge)throws javax.ejb.FinderException,java.rmi.RemoteException{	
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersBySearchCondition(p0, p1, endAge);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findUsersByYearOfBirth (int minYear, int maxYear)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersByYearOfBirth(minYear,maxYear);
	this.idoCheckInPooledEntity(entity);

	return this.getEntityCollectionForPrimaryKeys(ids);
}

public Collection findUsersBySearchCondition(String condition, String[] userIds, boolean p2) throws FinderException, RemoteException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersBySearchCondition(condition, userIds, p2);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public Collection findUsersByConditions(String userName, String personalId, String streetName, String groupName, int gender, int statusId, int startAge, int endAge, String[] allowedGroups, String[] allowedUsers, boolean useAnd, boolean orderLastFirst) throws FinderException, RemoteException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersByConditions(userName, personalId, streetName, groupName, gender, statusId, startAge, endAge, allowedGroups, allowedUsers, useAnd, orderLastFirst);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public Collection findUsersByConditions(String firstName, String middleName, String lastName,  String personalId, String streetName, String groupName, int gender, int statusId, int startAge, int endAge, String[] allowedGroups, String[] allowedUsers, boolean useAnd, boolean orderLastFirst) throws FinderException, RemoteException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersByConditions(firstName, middleName, lastName, personalId, streetName, groupName, gender, statusId, startAge, endAge, allowedGroups, allowedUsers, useAnd, orderLastFirst);
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
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
			return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllUsersOrderedByFirstName()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindAllUsersOrderedByFirstName();
	this.idoCheckInPooledEntity(entity);
//return this.getEntityCollectionForPrimaryKeys(ids);
			return this.getEntityCollectionForPrimaryKeys(ids);
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


public java.util.Collection findUsersByCreationTime(IWTimestamp firstCreationTime, IWTimestamp lastCreationTime) throws FinderException, IDOLookupException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindUsersByCreationTime(firstCreationTime,lastCreationTime);
	this.idoCheckInPooledEntity(entity);

	return this.getEntityCollectionForPrimaryKeys(ids);
}


/* (non-Javadoc)
 * @see com.idega.user.data.UserHome#findByDateOfBirthAndGroupRelationInitiationTimeAndStatus(java.sql.Date, java.sql.Date, com.idega.user.data.Group, java.sql.Timestamp, java.sql.Timestamp, java.lang.String[])
 */
public Collection findByDateOfBirthAndGroupRelationInitiationTimeAndStatus(Date firstBirthDateInPeriode, Date lastBirthDateInPeriode, Group relatedGroup, Timestamp firstInitiationDateInPeriode, Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindByDateOfBirthAndGroupRelationInitiationTimeAndStatus(firstBirthDateInPeriode,lastBirthDateInPeriode,relatedGroup,firstInitiationDateInPeriode,lastInitiationDateInPeriode,relationStatus);
	this.idoCheckInPooledEntity(entity);

	return this.getEntityCollectionForPrimaryKeys(ids);
}


public Collection findByGroupRelationInitiationTimeAndStatus(Group relatedGroup, Timestamp firstInitiationDateInPeriode, Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((UserBMPBean)entity).ejbFindByGroupRelationInitiationTimeAndStatus(relatedGroup,firstInitiationDateInPeriode,lastInitiationDateInPeriode,relationStatus);
	this.idoCheckInPooledEntity(entity);

	return this.getEntityCollectionForPrimaryKeys(ids);
}



}