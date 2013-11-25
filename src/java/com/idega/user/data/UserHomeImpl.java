package com.idega.user.data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.core.location.data.Commune;
import com.idega.data.IDOLookupException;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class UserHomeImpl extends com.idega.data.IDOFactory implements UserHome {

	private static final long serialVersionUID = 7639370524500772726L;

	@Override
	protected Class<User> getEntityInterfaceClass() {
		return User.class;
	}

	@Override
	public User create() throws javax.ejb.CreateException {
		return (User) super.createIDO();
	}

	@Override
	public User findByPersonalID(java.lang.String p0)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity).ejbFindByPersonalID(p0);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public User findByDateOfBirthAndName(java.sql.Date p0, java.lang.String p1)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity).ejbFindByDateOfBirthAndName(p0, p1);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection findByDateOfBirth(java.sql.Date p0)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindByDateOfBirth(p0);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public User findUserByUniqueId(String uniqueIdString)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity)
				.ejbFindUserByUniqueId(uniqueIdString);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public User findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(
			java.lang.String p0, java.lang.String p1, java.lang.String p2)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity)
				.ejbFindByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(
						p0, p1, p2);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public User findUserForUserGroup(int p0) throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity).ejbFindUserForUserGroup(p0);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection findUsersByMetaData(String key, String value)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersByMetaData(key,
				value);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUsersInQuery(com.idega.data.IDOQuery query)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersInQuery(query);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllUsers() throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindAllUsers();
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findNewestUsers(int returningNumberOfRecords,
			int startingRecord) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindNewestUsers(
				returningNumberOfRecords, startingRecord);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<User> findByNames(java.lang.String p0,
			java.lang.String p1, java.lang.String p2, boolean useLoweredValues)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Integer> ids = ((UserBMPBean) entity).ejbFindByNames(p0, p1, p2,
				useLoweredValues);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<User> ejbFindBySearchRequest(String request, int groupId,
			int maxAmount, int startingEntry) {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Integer> ids = ((UserBMPBean) entity)
				.ejbFindBySearchRequest(request, groupId, maxAmount,
						startingEntry);
		this.idoCheckInPooledEntity(entity);
		try {
			return this.getEntityCollectionForPrimaryKeys(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public Collection<User> ejbFindBySearchRequest(Collection<String> requests,
			int groupId, int maxAmount, int startingEntry) {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Integer> ids = ((UserBMPBean) entity)
				.ejbFindBySearchRequest(requests, groupId, maxAmount,
						startingEntry);
		this.idoCheckInPooledEntity(entity);
		try {
			return this.getEntityCollectionForPrimaryKeys(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public Collection<User> ejbAutocompleteRequest(String request, int groupId,
			int maxAmount, int startingEntry) {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Integer> ids = ((UserBMPBean) entity)
				.ejbAutocompleteRequest(request, groupId, maxAmount,
						startingEntry);
		this.idoCheckInPooledEntity(entity);
		try {
			return this.getEntityCollectionForPrimaryKeys(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	public Collection<User> findByNames(java.lang.String p0,
			java.lang.String p1, java.lang.String p2)
			throws javax.ejb.FinderException {
		return findByNames(p0, p1, p2, false);
	}

	@Override
	public Collection findUsersForUserRepresentativeGroups(Collection p0)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindUsersForUserRepresentativeGroups(p0);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);

	}

	@Override
	public User findUserForUserRepresentativeGroup(com.idega.user.data.Group p0)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity)
				.ejbFindUserForUserRepresentativeGroup(p0);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection findUsersInPrimaryGroup(com.idega.user.data.Group p0)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersInPrimaryGroup(p0);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUsersBySearchCondition(java.lang.String p0, boolean p1)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersBySearchCondition(
				p0, p1);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUsersBySearchConditionAndAge(java.lang.String p0,
			boolean p1, int endAge) throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersBySearchCondition(
				p0, p1, endAge);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUsersByYearOfBirth(int minYear, int maxYear)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersByYearOfBirth(
				minYear, maxYear);
		this.idoCheckInPooledEntity(entity);

		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUsersBySearchCondition(String condition,
			String[] userIds, boolean p2) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersBySearchCondition(
				condition, userIds, p2);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUsersByConditions(String userName, String personalId,
			String streetName, String groupName, int gender, int statusId,
			int startAge, int endAge, String[] allowedGroups,
			String[] allowedUsers, boolean useAnd, boolean orderLastFirst)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersByConditions(
				userName, personalId, streetName, groupName, gender, statusId,
				startAge, endAge, allowedGroups, allowedUsers, useAnd,
				orderLastFirst);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUsersByConditions(String firstName,
			String middleName, String lastName, String personalId,
			String streetName, String groupName, int gender, int statusId,
			int startAge, int endAge, String[] allowedGroups,
			String[] allowedUsers, boolean useAnd, boolean orderLastFirst)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersByConditions(
				firstName, middleName, lastName, personalId, streetName,
				groupName, gender, statusId, startAge, endAge, allowedGroups,
				allowedUsers, useAnd, orderLastFirst);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public User findUserForUserGroup(com.idega.user.data.Group p0)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity).ejbFindUserForUserGroup(p0);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public User findUserFromEmail(java.lang.String p0)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((UserBMPBean) entity).ejbFindUserFromEmail(p0);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection<User> findUsersByEmail(java.lang.String email)
			throws javax.ejb.FinderException {
		return findUsersByEmail(email, false, false);
	}

	@Override
	public Collection<User> findUsersByEmail(String email,
			boolean useLoweredValue, boolean useLikeExpression)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		@SuppressWarnings("unchecked")
		Collection<Integer> pks = ((UserBMPBean) entity).ejbFindUsersByEmail(
				email, useLoweredValue, useLikeExpression);
		this.idoCheckInPooledEntity(entity);
		@SuppressWarnings("unchecked")
		Collection<User> usrs = getEntityCollectionForPrimaryKeys(pks);
		return usrs;
	}

	@Override
	public Collection findUsers(java.lang.String[] userIDs)
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsers(userIDs);
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllUsersOrderedByFirstName()
			throws javax.ejb.FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindAllUsersOrderedByFirstName();
		this.idoCheckInPooledEntity(entity);
		// return this.getEntityCollectionForPrimaryKeys(ids);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (User) super.findByPrimaryKeyIDO(pk);
	}

	@Override
	public int getUserCount() throws com.idega.data.IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((UserBMPBean) entity).ejbHomeGetUserCount();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public int getCountByBirthYearAndCommune(int fromYear, int toYear,
			Commune commune) throws com.idega.data.IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((UserBMPBean) entity)
				.ejbHomeGetCountByBirthYearAndCommune(fromYear, toYear, commune);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public java.lang.String getGroupType() {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.lang.String theReturn = ((UserBMPBean) entity)
				.ejbHomeGetGroupType();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public Collection findUsersByCreationTime(IWTimestamp firstCreationTime,
			IWTimestamp lastCreationTime) throws FinderException,
			IDOLookupException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindUsersByCreationTime(
				firstCreationTime, lastCreationTime);
		this.idoCheckInPooledEntity(entity);

		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.user.data.UserHome#
	 * findByDateOfBirthAndGroupRelationInitiationTimeAndStatus(java.sql.Date,
	 * java.sql.Date, com.idega.user.data.Group, java.sql.Timestamp,
	 * java.sql.Timestamp, java.lang.String[])
	 */
	@Override
	public Collection findByDateOfBirthAndGroupRelationInitiationTimeAndStatus(
			Date firstBirthDateInPeriode, Date lastBirthDateInPeriode,
			Group relatedGroup, Timestamp firstInitiationDateInPeriode,
			Timestamp lastInitiationDateInPeriode, String[] relationStatus)
			throws IDOLookupException, FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindByDateOfBirthAndGroupRelationInitiationTimeAndStatus(
						firstBirthDateInPeriode, lastBirthDateInPeriode,
						relatedGroup, firstInitiationDateInPeriode,
						lastInitiationDateInPeriode, relationStatus);
		this.idoCheckInPooledEntity(entity);

		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findByGroupRelationInitiationTimeAndStatus(
			Group relatedGroup, Timestamp firstInitiationDateInPeriode,
			Timestamp lastInitiationDateInPeriode, String[] relationStatus)
			throws IDOLookupException, FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindByGroupRelationInitiationTimeAndStatus(relatedGroup,
						firstInitiationDateInPeriode,
						lastInitiationDateInPeriode, relationStatus);
		this.idoCheckInPooledEntity(entity);

		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection ejbFindUsersBySpecificGroupsUserstatusDateOfBirthAndGender(
			Collection groups, Collection userStatuses,
			Integer yearOfBirthFrom, Integer yearOfBirthTo, String gender)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindUsersBySpecificGroupsUserstatusDateOfBirthAndGender(
						groups, userStatuses, yearOfBirthFrom, yearOfBirthTo,
						gender);
		this.idoCheckInPooledEntity(entity);

		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllUsersWithDuplicatedEmails() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindAllUsersWithDuplicatedEmails();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllUsersWithDuplicatedPhones(String phoneType)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindAllUsersWithDuplicatedPhones(phoneType);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<User> findByDisplayName(String displayName,
			boolean useLoweredValue) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity).ejbFindByDisplayName(
				displayName, useLoweredValue);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<User> findByPhoneNumber(String phoneNumber)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindByPhoneNumber(phoneNumber);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<User> findAllByPersonalID(String personalId)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((UserBMPBean) entity)
				.ejbFindAllByPersonalId(personalId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.UserHome#findAllByNameAndEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<User> findAllByNameAndEmail(String name, String email) {
		if (StringUtil.isEmpty(name) || StringUtil.isEmpty(email)) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to get " + User.class.getName() + 
					" because user name or email was not provided!");
			return null;
		}

		ArrayList<User> users = null;

		/* Expecting to be unique */
		Collection<User> usersByEmail = null;
		try {
			usersByEmail = findUsersByEmail(email);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to get " + getEntityInterfaceClass().getName() + 
					"'s by email: '" + email + "'");
		}

		
		if (ListUtil.isEmpty(usersByEmail)) {
			return Collections.emptyList();
		}

		/* Filtering users by name */
		users = new ArrayList<User>(usersByEmail.size());
		for (User user : usersByEmail) {
			boolean nameMatches = Boolean.TRUE;
			
			for (String namePart : name.split(CoreConstants.SPACE)) {
				if (!user.getName().contains(namePart)) {
					nameMatches = Boolean.FALSE;
				}
			}

			if (nameMatches) {
				users.add(user);
			}
		}

		return users;
	}
}