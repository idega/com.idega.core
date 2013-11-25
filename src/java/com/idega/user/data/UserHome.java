package com.idega.user.data;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.FinderException;

import com.idega.core.contact.data.Email;
import com.idega.core.location.data.Commune;
import com.idega.data.IDOLookupException;
import com.idega.util.IWTimestamp;


public interface UserHome extends com.idega.data.IDOHome
{
 public User create() throws javax.ejb.CreateException;
 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public User findByPersonalID(String p0)throws javax.ejb.FinderException;
 public Collection<User> findAllByPersonalID(String p0)throws javax.ejb.FinderException;
 public User findByDateOfBirthAndName(java.sql.Date p0, String p1)throws javax.ejb.FinderException;
 public Collection<User> findByDateOfBirth(java.sql.Date p0)throws javax.ejb.FinderException;
 public User findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(String p0,String p1, String p2)throws javax.ejb.FinderException;
 public User findUserForUserGroup(int p0)throws javax.ejb.FinderException;
 public Collection<User> findAllUsersWithDuplicatedEmails()throws javax.ejb.FinderException;
 public Collection<User> findAllUsersWithDuplicatedPhones(String p0)throws javax.ejb.FinderException;
 public Collection<User> findAllUsers()throws javax.ejb.FinderException;
 public Collection<User> findNewestUsers(int returningNumberOfRecords, int startingRecord) throws FinderException;
 public Collection<User> findByNames(String p0,String p1,String p2)throws javax.ejb.FinderException;
 public Collection<User> findUsersForUserRepresentativeGroups(Collection p0)throws javax.ejb.FinderException;
 public User findUserForUserRepresentativeGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public Collection<User> findUsersInPrimaryGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public Collection<User> findUsersBySearchCondition(String p0, boolean orderLastFirst)throws javax.ejb.FinderException;
 public Collection<User> findUsersBySearchConditionAndAge(String p0, boolean orderLastFirst, int endAge)throws javax.ejb.FinderException;
 public Collection<User> findUsersBySearchCondition(String condition, String[] userIds, boolean orderLastFirst) throws FinderException;
 public User findUserForUserGroup(com.idega.user.data.Group p0)throws javax.ejb.FinderException;
 public User findUserFromEmail(String p0)throws javax.ejb.FinderException;

 public abstract Collection<User> findUsersByEmail(String email) throws javax.ejb.FinderException;
 public abstract Collection<User> findUsersByEmail(String email, boolean useLoweredValue, boolean useLikeExpression) throws javax.ejb.FinderException;

 public Collection<User> findUsers(String[] userIDs)throws javax.ejb.FinderException;
 public Collection<User> findAllUsersOrderedByFirstName()throws javax.ejb.FinderException;
 public Collection<User> findUsersByYearOfBirth (int minYear, int maxYear)  throws  FinderException;
 public int getUserCount()throws com.idega.data.IDOException;
 public int getCountByBirthYearAndCommune(int fromYear, int toYear, Commune commune) throws com.idega.data.IDOException;
 public String getGroupType();
 public Collection<User> findUsersInQuery(com.idega.data.IDOQuery query)throws javax.ejb.FinderException;
 public Collection<User> findUsersByConditions(String userName, String personalId, String streetName, String groupName, int genderId, int statusId, int startAge, int endAge, String[] allowedGroupIds, String[] allowedUserIds, boolean useAnd, boolean orderLastFirst) throws FinderException;
 public Collection<User> findUsersByConditions(String firstName, String middleName, String lastName, String personalId, String streetName, String groupName, int genderId, int statusId, int startAge, int endAge, String[] allowedGroupIds, String[] allowedUserIds, boolean useAnd, boolean orderLastFirst) throws FinderException;
 public Collection<User> findUsersByMetaData(String key, String value) throws FinderException;
 public Collection<User> findUsersByCreationTime(IWTimestamp firstCreationTime, IWTimestamp lastCreationTime) throws FinderException, IDOLookupException;
 public Collection<User> findByDateOfBirthAndGroupRelationInitiationTimeAndStatus(java.sql.Date firstBirthDateInPeriode, java.sql.Date lastBirthDateInPeriode, Group relatedGroup, java.sql.Timestamp firstInitiationDateInPeriode, java.sql.Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException;
 public Collection<User> findByGroupRelationInitiationTimeAndStatus(Group relatedGroup, java.sql.Timestamp firstInitiationDateInPeriode, java.sql.Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException;
 public User findUserByUniqueId(String uniqueIdString) throws FinderException;
 public Collection<User> ejbFindUsersBySpecificGroupsUserstatusDateOfBirthAndGender(Collection groups, Collection userStatuses, Integer yearOfBirthFrom, Integer yearOfBirthTo, String gender) throws FinderException;

 public Collection<User> findByNames(String p0,String p1,String p2, boolean useLoweredValues) throws FinderException;

 public Collection<User> findByDisplayName(String displayName, boolean useLoweredValue) throws FinderException;

 public Collection<User> findByPhoneNumber(String phoneNumber) throws FinderException;

 public Collection <User> ejbFindBySearchRequest(String request, int groupId, int maxAmount, int startingEntry);
 public Collection <User> ejbFindBySearchRequest(Collection <String> requests, int groupId, int maxAmount, int startingEntry);
 public Collection <User> ejbAutocompleteRequest(String request, int groupId, int maxAmount, int startingEntry);

	/**
	 * 
	 * @param name is {@link User#getName()}, not <code>null</code>;
	 * @param email is {@link Email#getEmailAddress()}, not <code>null</code>;
	 * @return {@link User}s in data source by given criteria or
	 * {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<User> findAllByNameAndEmail(String name, String email);
}