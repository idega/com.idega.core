/*
 * $Id: UserBusiness.java,v 1.113 2009/02/07 14:34:57 valdas Exp $
 * Created on Nov 18, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.business.IBOService;
import com.idega.core.accesscontrol.business.LoginCreateException;
import com.idega.core.accesscontrol.data.ICRole;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneHome;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOQuery;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.user.bean.GroupMemberDataBean;
import com.idega.user.data.Gender;
import com.idega.user.data.Group;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupHome;
import com.idega.user.data.Status;
import com.idega.user.data.TopNodeGroupHome;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.IWTimestamp;


/**
 *
 *  Last modified: $Date: 2009/02/07 14:34:57 $ by $Author: valdas $
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.113 $
 */
public interface UserBusiness extends IBOService {

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserHome
	 */
	public UserHome getUserHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGroupHome
	 */
	public GroupHome getGroupHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getEmailHome
	 */
	public EmailHome getEmailHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAddressHome
	 */
	public AddressHome getAddressHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getPhoneHome
	 */
	public PhoneHome getPhoneHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getTopNodeGroupHome
	 */
	public TopNodeGroupHome getTopNodeGroupHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#insertUser
	 */
	public User insertUser(String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserByPersonalIDIfDoesNotExist
	 */
	public User createUserByPersonalIDIfDoesNotExist(String fullName, String personalID, Gender gender,
			IWTimestamp dateOfBirth) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserByPersonalIDIfDoesNotExist
	 */
	public User createUserByPersonalIDIfDoesNotExist(String firstName, String middleName, String lastName,
			String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserByPersonalIdAndUUIDOrUpdate
	 */
	public void createUserByPersonalIdAndUUIDOrUpdate(String pin, String UUID, String fullName, String gender,
			String dateOfBirth) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(User user, String name, String gender, String dateOfBirth) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String fullName)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#setUserUnderDomain
	 */
	public void setUserUnderDomain(ICDomain domain, User user, GroupDomainRelationType type) throws CreateException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#generateUserLogin
	 */
	public LoginTable generateUserLogin(int userID) throws LoginCreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#generateUserLogin
	 */
	public LoginTable generateUserLogin(User user) throws LoginCreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename, String lastname) throws CreateException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName, String lastName, int primary_groupID)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName, String lastName, Group primary_group)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender,
			IWTimestamp dateOfBirth, Group primaryGroup) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender,
			IWTimestamp dateOfBirth) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserWithLogin
	 */
	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,
			String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group,
			String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity,
			Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType)
			throws CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserWithLogin
	 */
	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,
			String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group,
			String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity,
			Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType,
			String fullName) throws CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserWithLogin
	 */
	public User createUserWithLogin(String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin,
			String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires,
			Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws CreateException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#deleteUser
	 */
	public void deleteUser(int userId, User currentUser) throws RemoveException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#deleteUser
	 */
	public void deleteUser(User delUser, User currentUser) throws RemoveException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeUserFromGroup
	 */
	public void removeUserFromGroup(int userId, Group group, User currentUser) throws RemoveException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeUserFromGroup
	 */
	public void removeUserFromGroup(User user, Group group, User currentUser) throws RemoveException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#setPermissionGroup
	 */
	public void setPermissionGroup(User user, Integer primaryGroupId) throws IDOStoreException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGenderId
	 */
	public Integer getGenderId(String gender) throws Exception, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isMale
	 */
	public boolean isMale(int genderId) throws RemoteException, FinderException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isFemale
	 */
	public boolean isFemale(int genderId) throws RemoteException, FinderException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserPhones
	 */
	public Phone[] getUserPhones(int userId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserPhones
	 */
	public Phone[] getUserPhones(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserPhone
	 */
	public Phone getUserPhone(int userId, int phoneTypeId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserMail
	 */
	public Email getUserMail(int userId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserMail
	 */
	public Email getUserMail(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserHomePhone
	 */
	public void updateUserHomePhone(User user, String phoneNumber) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserWorkPhone
	 */
	public void updateUserWorkPhone(User user, String phoneNumber) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserMobilePhone
	 */
	public void updateUserMobilePhone(User user, String phoneNumber) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserPhone
	 */
	public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber) throws EJBException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserPhone
	 */
	public void updateUserPhone(User user, int phoneTypeId, String phoneNumber) throws EJBException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserMail
	 */
	public Email updateUserMail(int userId, String email) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserMail
	 */
	public Email updateUserMail(User user, String email) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserJob
	 */
	public void updateUserJob(int userId, String job) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserJob
	 */
	public String getUserJob(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserWorkPlace
	 */
	public void updateUserWorkPlace(int userId, String workPlace) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserWorkPlace
	 */
	public String getUserWorkPlace(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserAddress1
	 */
	public Address getUserAddress1(int userID) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserAddressByAddressType
	 */
	public Address getUserAddressByAddressType(int userID, AddressType type) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddress
	 */
	public Address getUsersMainAddress(int userID) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddresses
	 */
	public Collection getUsersMainAddresses(String[] userIDs) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddresses
	 */
	public Collection getUsersMainAddresses(IDOQuery query) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddress
	 */
	public Address getUsersMainAddress(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersCoAddress
	 */
	public Address getUsersCoAddress(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersCoAddress
	 */
	public Address getUsersCoAddress(int userId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsers
	 */
	public Collection<User> getUsers(String[] userIDs) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsers
	 */
	public Collection getUsers(IDOQuery query) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber,
			PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersCoAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersCoAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber,
			PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersCoAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group,
			String fullname) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group,
			String fullname) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#listOfUserEmails
	 */
	public Collection listOfUserEmails(int iUserId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserEmail
	 */
	public Email storeUserEmail(Integer userID, String emailAddress, boolean replaceExistentRecord)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserEmail
	 */
	public Email storeUserEmail(User user, String emailAddress, boolean replaceExistentRecord)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeUserEmails
	 */
	public boolean removeUserEmails(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#addNewUserEmail
	 */
	public void addNewUserEmail(int iUserId, String sNewEmailAddress) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#listOfUserGroups
	 */
	public Collection listOfUserGroups(int iUserId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection getUserGroups(int iUserId) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInGroup
	 */
	public Collection<User> getUsersInGroup(int iGroupId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInGroup
	 */
	public Collection<User> getUsersInGroup(Group aGroup) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsers
	 */
	public Collection<User> getUsers() throws FinderException, RemoteException;

	/**
	 *
	 * <p>Searches for {@link User} in database by {@link User#getPersonalID()},
	 * if nothing found, than by {@link User#getName()}.</p>
	 * @param name of {@link User};
	 * @param personalID of {@link User};
	 * @return {@link Collection} of {@link User}s, matching given criteria or
	 * {@link Collections#emptyList()} on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public Collection<User> getUsers(String name, String personalID);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUser
	 */
	public User getUser(int iUserId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUser
	 */
	public User getUser(Integer iUserId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUser
	 */
	public User getUser(String personalID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName
	 */
	public User findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(String personalID, String first_name,
			String last_name) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInNoGroup
	 */
	public Collection getUsersInNoGroup() throws SQLException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroupsDirectlyRelated
	 */
	public Collection getUserGroupsDirectlyRelated(int iUserId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInPrimaryGroup
	 */
	public Collection getUsersInPrimaryGroup(Group group) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroupsDirectlyRelated
	 */
	public Collection getUserGroupsDirectlyRelated(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getParentGroupsInDirectForUser
	 */
	public Collection getParentGroupsInDirectForUser(int iUserId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getNonParentGroups
	 */
	public Collection getNonParentGroups(int iUserId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection<Group> getUserGroups(User aUser) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection getUserGroups(User aUser, String[] groupTypes) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection getUserGroups(User aUser, String[] groupTypes, boolean returnSepcifiedGroupTypes)
			throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGroupBusiness
	 */
	public GroupBusiness getGroupBusiness() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllUsersOrderedByFirstName
	 */
	public Collection<User> getAllUsersOrderedByFirstName() throws FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainEmail
	 */
	public Email getUsersMainEmail(User user) throws NoEmailFoundException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersHomePhone
	 */
	public Phone getUsersHomePhone(User user) throws NoPhoneFoundException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersWorkPhone
	 */
	public Phone getUsersWorkPhone(User user) throws NoPhoneFoundException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMobilePhone
	 */
	public Phone getUsersMobilePhone(User user) throws NoPhoneFoundException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersFaxPhone
	 */
	public Phone getUsersFaxPhone(User user) throws NoPhoneFoundException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getNameOfGroupOrUser
	 */
	public String getNameOfGroupOrUser(Group groupOrUser) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserProperties
	 */
	public UserProperties getUserProperties(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserProperties
	 */
	public UserProperties getUserProperties(int userID) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getHomePageIDForUser
	 */
	public int getHomePageIDForUser(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getHomePageForUser
	 */
	public com.idega.core.builder.data.ICPage getHomePageForUser(User user) throws javax.ejb.FinderException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAddressBusiness
	 */
	public AddressBusiness getAddressBusiness() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#castUserGroupToUser
	 */
	public User castUserGroupToUser(Group userGroup) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#hasUserLogin
	 */
	public boolean hasUserLogin(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#hasUserLogin
	 */
	public boolean hasUserLogin(int userID) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersHighestTopGroupNode
	 */
	public Group getUsersHighestTopGroupNode(User user, List groupTypes, IWUserContext iwuc) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isGroupUnderUsersTopGroupNode
	 */
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isGroupUnderUsersTopGroupNode
	 */
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user, Collection topGroupNodes)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#hasTopNodes
	 */
	public boolean hasTopNodes(User user, IWUserContext iwuc) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersTopGroupNodesByViewAndOwnerPermissions
	 */
	public Collection getUsersTopGroupNodesByViewAndOwnerPermissions(User user, IWUserContext iwuc)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#addGroupPKsToCollectionFromICPermissionCollection
	 */
	public void addGroupPKsToCollectionFromICPermissionCollection(Collection ICPermissionSRC, Collection GroupDEST)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getStoredTopNodeGroups
	 */
	public Collection getStoredTopNodeGroups(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getStoredTopGroupNodes
	 */
	public Collection getStoredTopGroupNodes(User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeStoredTopGroupNodes
	 */
	public void removeStoredTopGroupNodes(User user) throws RemoveException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserTopGroupNodes
	 */
	public boolean storeUserTopGroupNodes(User user, Collection nodeGroups, int numberOfPermissions,
			String totalLoginTime, String comment) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllGroupsWithEditPermission
	 */
	public Collection getAllGroupsWithEditPermission(User user, IWUserContext iwuc) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllGroupsWithViewPermission
	 */
	public Collection getAllGroupsWithViewPermission(User user, IWUserContext iwuc) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#moveUsers
	 */
	public Map moveUsers(IWUserContext iwuc, Collection userIds, Group parentGroup, int targetGroupId)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#moveUsers
	 */
	public Map moveUsers(IWUserContext iwuc, Collection userIds, Group parentGroup, int targetGroupId,
			boolean leaveCopyOfUserInCurrentGroup) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#moveUsers
	 */
	public Map moveUsers(IWUserContext iwuc, Collection groups, Collection groupTypesToMoveAmong)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isMemberOfGroup
	 */
	public boolean isMemberOfGroup(int parentGroupToTest, User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isUserSuitedForGroup
	 */
	public String isUserSuitedForGroup(User user, Group targetGroup) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserApplicationStyleSheetURL
	 */
	public String getUserApplicationStyleSheetURL() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isInDefaultCommune
	 */
	public boolean isInDefaultCommune(User user) throws RemoteException, FinderException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressByFullAddressString
	 */
	public void updateUsersMainAddressByFullAddressString(User user, String fullAddressString) throws RemoteException,
			CreateException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserByUniqueId
	 */
	public User getUserByUniqueId(String uniqueID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersBySpecificGroupsUserstatusDateOfBirthAndGender
	 */
	public Collection getUsersBySpecificGroupsUserstatusDateOfBirthAndGender(Collection groups,
			Collection userStatuses, Integer yearOfBirthFrom, Integer yearOfBirthTo, String gender)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserComment
	 */
	public void storeUserComment(User user, String comment, User performer) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserComments
	 */
	public Collection getUserComments(User user) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#callAllUserGroupPluginAfterUserCreateOrUpdateMethod
	 */
	public void callAllUserGroupPluginAfterUserCreateOrUpdateMethod(User user) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#callAllUserGroupPluginAfterUserCreateOrUpdateMethod
	 */
	public void callAllUserGroupPluginAfterUserCreateOrUpdateMethod(User user, Group parentGroup)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#callAllUserGroupPluginBeforeUserRemoveMethod
	 */
	public void callAllUserGroupPluginBeforeUserRemoveMethod(User user, Group parentGroup)
			throws java.rmi.RemoteException;


	/**
	 * @see com.idega.user.business.UserBusinessBean#cleanUserEmails
	 */
	public void cleanUserEmails();


	/**
	 * @see com.idega.user.business.UserBusinessBean#setUsersPreferredLocale
	 */
	public void setUsersPreferredLocale(User user,String preferredLocale, boolean storeUser);

	/**
	 * @see com.idega.user.business.UserBusinessBean#setUsersPreferredRole
	 */
	public void setUsersPreferredRole(User user, ICRole preferredRole, boolean storeUser);


	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersPreferredLocale
	 */
	public Locale getUsersPreferredLocale(User user);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersPreferredRole
	 */
	public ICRole getUsersPreferredRole(User user);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGroupsMembersData
	 */
	public List<GroupMemberDataBean> getGroupsMembersData(List<String> uniqueIds);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserStatus
	 */
	public Status getUserStatus(IWContext iwc, User user, Group group);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserStatus
	 */
	public Status getUserStatus(IWContext iwc, int userId, int groupId);

	public String getUserPassword(User user);

	public String getUserLogin(User user);

	public List<String> getAllUserGroupsIds(User user, IWUserContext iwuc) throws RemoteException;

	public List<Group> getAllUserGroups(User user, IWUserContext iwuc) throws RemoteException;

	public Date getUserDateOfBirthFromPersonalId(String personalId);

	public Collection<User> getUsersByName(String name);

	public Collection<User> getUsersByNameOrEmailOrPhone(String nameEmailOrPhone);

	public Collection<User> getUsersByNameAndEmailAndPhone(String nameEmailOrPhone);

	public Collection<User> getUsersByEmail(String email);

	public Collection<User> getUsersByPhoneNumber(String phoneNumber);

	public String setPreferredRoleAndGetHomePageUri(String roleKey, IWUserContext iwuc);

	public String getPageUriByUserPreferredRole(User user, IWUserContext iwuc);

	public List<ICRole> getAvailableRolesForUserAsPreferredRoles(User user);

	/**
	 * returns  moderator(company and admin) of the user
	 *
	 * @param user
	 * @param iwc
	 * @return
	 * @throws RemoteException
	 */
	public User getModeratorForUser(User user);

	public void setPreferedCompany(String companyId, User user);

	public Group getPreferedCompany(User user) throws RemoteException;

	public String changeUserPassword(String newPassword);

	/**
	 * creates login for user account provided
	 * @param newUser mandatory
	 * @param userLogin mandatory
	 * @param password mandatory
	 * @param accountEnabled
	 * @param modified
	 * @param daysOfValidity
	 * @param passwordExpires
	 * @param userAllowedToChangePassw
	 * @param changeNextTime
	 * @param encryptionType
	 */
	public abstract void createUserLogin(User newUser, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity,
			Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType);

	public Image getUserImage(User user);

	public boolean validatePersonalId(User user);
	public boolean validatePersonalId(User user, Locale locale);
	public boolean validatePersonalId(String personalId);
	public boolean validatePersonalId(String personalId, Locale locale);

	public boolean hasValidPersonalId(User user);
	public boolean hasValidPersonalId(User user, Locale locale);
}
