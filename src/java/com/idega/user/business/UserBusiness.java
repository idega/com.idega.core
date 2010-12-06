package com.idega.user.business;


import com.idega.core.contact.data.PhoneHome;
import java.util.Map;
import java.rmi.RemoteException;
import com.idega.user.data.UserHome;
import java.sql.SQLException;
import com.idega.core.contact.data.EmailHome;
import com.idega.user.data.Status;
import com.idega.presentation.IWContext;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.core.accesscontrol.business.LoginCreateException;
import com.idega.util.IWTimestamp;
import javax.ejb.EJBException;
import com.idega.user.data.TopNodeGroupHome;
import com.idega.core.contact.data.Phone;
import java.util.Locale;
import com.idega.core.contact.data.EmailTypeHome;
import com.idega.core.location.data.Country;
import com.idega.data.IDOQuery;
import java.util.Collection;
import com.idega.core.location.data.AddressType;
import javax.ejb.RemoveException;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.location.data.AddressHome;
import java.util.List;
import com.idega.core.builder.data.ICPage;
import com.idega.business.IBOService;
import com.idega.core.location.data.Address;
import com.idega.data.IDOStoreException;
import com.idega.user.data.Group;
import com.idega.user.data.Gender;
import java.sql.Date;
import javax.ejb.CreateException;
import com.idega.user.data.GroupHome;
import com.idega.user.data.User;
import com.idega.core.location.data.PostalCode;
import javax.ejb.FinderException;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.contact.data.Email;
import com.idega.idegaweb.IWUserContext;

public interface UserBusiness extends IBOService {
	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserHome
	 */
	public UserHome getUserHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGroupHome
	 */
	public GroupHome getGroupHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getEmailHome
	 */
	public EmailHome getEmailHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getEmailTypeHome
	 */
	public EmailTypeHome getEmailTypeHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAddressHome
	 */
	public AddressHome getAddressHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getPhoneHome
	 */
	public PhoneHome getPhoneHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getTopNodeGroupHome
	 */
	public TopNodeGroupHome getTopNodeGroupHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#insertUser
	 */
	public User insertUser(String firstname, String middlename,
			String lastname, String displayname, String description,
			Integer gender, IWTimestamp date_of_birth, Integer primary_group)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserByPersonalIDIfDoesNotExist
	 */
	public User createUserByPersonalIDIfDoesNotExist(String fullName,
			String personalID, Gender gender, IWTimestamp dateOfBirth)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserByPersonalIDIfDoesNotExist
	 */
	public User createUserByPersonalIDIfDoesNotExist(String firstName,
			String middleName, String lastName, String personalID,
			Gender gender, IWTimestamp dateOfBirth) throws CreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserByPersonalIdAndUUIDOrUpdate
	 */
	public void createUserByPersonalIdAndUUIDOrUpdate(String pin, String UUID,
			String fullName, String gender, String dateOfBirth)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(User user, String name, String gender,
			String dateOfBirth) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName,
			String lastName, String displayname, String personalID,
			String description, Integer gender, IWTimestamp date_of_birth,
			Integer primary_group) throws CreateException, RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName,
			String lastName, String displayname, String personalID,
			String description, Integer gender, IWTimestamp date_of_birth,
			Integer primary_group, String fullName) throws CreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#setUserUnderDomain
	 */
	public void setUserUnderDomain(ICDomain domain, User user,
			GroupDomainRelationType type) throws CreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#generateUserLogin
	 */
	public LoginTable generateUserLogin(int userID)
			throws LoginCreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#generateUserLogin
	 */
	public LoginTable generateUserLogin(User user) throws LoginCreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename, String lastname)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename,
			String lastname, String personalID) throws CreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName,
			String lastName, int primary_groupID) throws CreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstName, String middleName,
			String lastName, Group primary_group) throws CreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename,
			String lastname, String personalID, Gender gender)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename,
			String lastname, String personalID, Gender gender,
			IWTimestamp dateOfBirth, Group primaryGroup)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename,
			String lastname, String personalID, Gender gender,
			IWTimestamp dateOfBirth) throws CreateException, RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUser
	 */
	public User createUser(String firstname, String middlename,
			String lastname, String displayname, String personalID,
			Gender gender, IWTimestamp dateOfBirth) throws CreateException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserWithLogin
	 */
	public User createUserWithLogin(String firstname, String middlename,
			String lastname, String SSN, String displayname,
			String description, Integer gender, IWTimestamp date_of_birth,
			Integer primary_group, String userLogin, String password,
			Boolean accountEnabled, IWTimestamp modified, int daysOfValidity,
			Boolean passwordExpires, Boolean userAllowedToChangePassw,
			Boolean changeNextTime, String encryptionType)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserWithLogin
	 */
	public User createUserWithLogin(String firstname, String middlename,
			String lastname, String SSN, String displayname,
			String description, Integer gender, IWTimestamp date_of_birth,
			Integer primary_group, String userLogin, String password,
			Boolean accountEnabled, IWTimestamp modified, int daysOfValidity,
			Boolean passwordExpires, Boolean userAllowedToChangePassw,
			Boolean changeNextTime, String encryptionType, String fullName)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#createUserWithLogin
	 */
	public User createUserWithLogin(String firstname, String middlename,
			String lastname, String displayname, String description,
			Integer gender, IWTimestamp date_of_birth, Integer primary_group,
			String userLogin, String password, Boolean accountEnabled,
			IWTimestamp modified, int daysOfValidity, Boolean passwordExpires,
			Boolean userAllowedToChangePassw, Boolean changeNextTime,
			String encryptionType) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#deleteUser
	 */
	public void deleteUser(int userId, User currentUser)
			throws RemoveException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#deleteUser
	 */
	public void deleteUser(User delUser, User currentUser)
			throws RemoveException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeUserFromGroup
	 */
	public void removeUserFromGroup(int userId, Group group, User currentUser)
			throws RemoveException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeUserFromGroup
	 */
	public void removeUserFromGroup(User user, Group group, User currentUser)
			throws RemoveException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#setPermissionGroup
	 */
	public void setPermissionGroup(User user, Integer primaryGroupId)
			throws IDOStoreException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGenderId
	 */
	public Integer getGenderId(String gender) throws Exception, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isMale
	 */
	public boolean isMale(int genderId) throws RemoteException,
			FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isFemale
	 */
	public boolean isFemale(int genderId) throws RemoteException,
			FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserPhones
	 */
	public Phone[] getUserPhones(int userId) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserPhones
	 */
	public Phone[] getUserPhones(User user) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserPhone
	 */
	public Phone getUserPhone(int userId, int phoneTypeId)
			throws RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserMail
	 */
	public Email getUserMail(int userId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserMail
	 */
	public Email getUserMail(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserHomePhone
	 */
	public void updateUserHomePhone(User user, String phoneNumber)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserWorkPhone
	 */
	public void updateUserWorkPhone(User user, String phoneNumber)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserMobilePhone
	 */
	public void updateUserMobilePhone(User user, String phoneNumber)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserPhone
	 */
	public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserPhone
	 */
	public void updateUserPhone(User user, int phoneTypeId, String phoneNumber)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserMail
	 */
	public Email updateUserMail(int userId, String email)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserMail
	 */
	public Email updateUserMail(User user, String email)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserJob
	 */
	public void updateUserJob(int userId, String job) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserJob
	 */
	public String getUserJob(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUserWorkPlace
	 */
	public void updateUserWorkPlace(int userId, String workPlace)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserWorkPlace
	 */
	public String getUserWorkPlace(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserAddress1
	 */
	public Address getUserAddress1(int userID) throws EJBException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserAddressByAddressType
	 */
	public Address getUserAddressByAddressType(int userID, AddressType type)
			throws EJBException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddress
	 */
	public Address getUsersMainAddress(int userID) throws EJBException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddresses
	 */
	public Collection getUsersMainAddresses(String[] userIDs)
			throws EJBException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddresses
	 */
	public Collection getUsersMainAddresses(IDOQuery query)
			throws EJBException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainAddress
	 */
	public Address getUsersMainAddress(User user) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersCoAddress
	 */
	public Address getUsersCoAddress(User user) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersCoAddress
	 */
	public Address getUsersCoAddress(int userId) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsers
	 */
	public Collection getUsers(String[] userIDs) throws EJBException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsers
	 */
	public Collection getUsers(IDOQuery query) throws EJBException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId,
			String streetNameAndNumber, Integer postalCodeId,
			String countryName, String city, String province, String poBox)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(User user,
			String streetNameAndNumber, PostalCode postalCode, Country country,
			String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId,
			String streetNameAndNumber, Integer postalCodeId,
			String countryName, String city, String province, String poBox,
			Integer communeID) throws CreateException, RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersCoAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId,
			String streetNameAndNumber, Integer postalCodeId,
			String countryName, String city, String province, String poBox)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersCoAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(User user,
			String streetNameAndNumber, PostalCode postalCode, Country country,
			String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersCoAddressOrCreateIfDoesNotExist
	 */
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId,
			String streetNameAndNumber, Integer postalCodeId,
			String countryName, String city, String province, String poBox,
			Integer communeID) throws CreateException, RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(int userId, String firstname, String middlename,
			String lastname, String displayname, String description,
			Integer gender, String personalID, IWTimestamp date_of_birth,
			Integer primary_group) throws EJBException, RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(int userId, String firstname, String middlename,
			String lastname, String displayname, String description,
			Integer gender, String personalID, IWTimestamp date_of_birth,
			Integer primary_group, String fullname) throws EJBException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(User userToUpdate, String firstname,
			String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID,
			IWTimestamp date_of_birth, Integer primary_group)
			throws EJBException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUser
	 */
	public void updateUser(User userToUpdate, String firstname,
			String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID,
			IWTimestamp date_of_birth, Integer primary_group, String fullname)
			throws EJBException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#listOfUserEmails
	 */
	public Collection listOfUserEmails(int iUserId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserEmail
	 */
	public Email storeUserEmail(Integer userID, String emailAddress,
			boolean replaceExistentRecord) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserEmail
	 */
	public Email storeUserEmail(User user, String emailAddress,
			boolean replaceExistentRecord) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeUserEmails
	 */
	public boolean removeUserEmails(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#addNewUserEmail
	 */
	public void addNewUserEmail(int iUserId, String sNewEmailAddress)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#listOfUserGroups
	 */
	public Collection listOfUserGroups(int iUserId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection getUserGroups(int iUserId) throws EJBException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInGroup
	 */
	public Collection getUsersInGroup(int iGroupId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInGroup
	 */
	public Collection getUsersInGroup(Group aGroup) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsers
	 */
	public Collection getUsers() throws FinderException, RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUser
	 */
	public User getUser(int iUserId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUser
	 */
	public User getUser(Integer iUserId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUser
	 */
	public User getUser(String personalID) throws FinderException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName
	 */
	public User findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(
			String personalID, String first_name, String last_name)
			throws FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInNoGroup
	 */
	public Collection getUsersInNoGroup() throws SQLException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroupsDirectlyRelated
	 */
	public Collection getUserGroupsDirectlyRelated(int iUserId)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersInPrimaryGroup
	 */
	public Collection getUsersInPrimaryGroup(Group group)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroupsDirectlyRelated
	 */
	public Collection getUserGroupsDirectlyRelated(User user)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getParentGroupsInDirectForUser
	 */
	public Collection getParentGroupsInDirectForUser(int iUserId)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getNonParentGroups
	 */
	public Collection getNonParentGroups(int iUserId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection getUserGroups(User aUser) throws EJBException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection getUserGroups(User aUser, String[] groupTypes)
			throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserGroups
	 */
	public Collection getUserGroups(User aUser, String[] groupTypes,
			boolean returnSepcifiedGroupTypes) throws EJBException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGroupBusiness
	 */
	public GroupBusiness getGroupBusiness() throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllUsersOrderedByFirstName
	 */
	public Collection getAllUsersOrderedByFirstName() throws FinderException,
			RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMainEmail
	 */
	public Email getUsersMainEmail(User user) throws NoEmailFoundException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersHomePhone
	 */
	public Phone getUsersHomePhone(User user) throws NoPhoneFoundException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersWorkPhone
	 */
	public Phone getUsersWorkPhone(User user) throws NoPhoneFoundException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersMobilePhone
	 */
	public Phone getUsersMobilePhone(User user) throws NoPhoneFoundException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersFaxPhone
	 */
	public Phone getUsersFaxPhone(User user) throws NoPhoneFoundException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getNameOfGroupOrUser
	 */
	public String getNameOfGroupOrUser(Group groupOrUser)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserProperties
	 */
	public UserProperties getUserProperties(User user) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserProperties
	 */
	public UserProperties getUserProperties(int userID) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getHomePageIDForUser
	 */
	public int getHomePageIDForUser(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getHomePageForUser
	 */
	public ICPage getHomePageForUser(User user) throws FinderException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAddressBusiness
	 */
	public AddressBusiness getAddressBusiness() throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#castUserGroupToUser
	 */
	public User castUserGroupToUser(Group userGroup) throws EJBException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#hasUserLogin
	 */
	public boolean hasUserLogin(User user) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#hasUserLogin
	 */
	public boolean hasUserLogin(int userID) throws RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserLogin
	 */
	public String getUserLogin(User user);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserPassword
	 */
	public String getUserPassword(User user);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersHighestTopGroupNode
	 */
	public Group getUsersHighestTopGroupNode(User user, List groupTypes,
			IWUserContext iwuc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isGroupUnderUsersTopGroupNode
	 */
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc,
			Group group, User user) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isGroupUnderUsersTopGroupNode
	 */
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc,
			Group group, User user, Collection topGroupNodes)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#hasTopNodes
	 */
	public boolean hasTopNodes(User user, IWUserContext iwuc)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersTopGroupNodesByViewAndOwnerPermissions
	 */
	public Collection getUsersTopGroupNodesByViewAndOwnerPermissions(User user,
			IWUserContext iwuc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#addGroupPKsToCollectionFromICPermissionCollection
	 */
	public void addGroupPKsToCollectionFromICPermissionCollection(
			Collection ICPermissionSRC, Collection GroupDEST)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getStoredTopNodeGroups
	 */
	public Collection getStoredTopNodeGroups(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getStoredTopGroupNodes
	 */
	public Collection getStoredTopGroupNodes(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#removeStoredTopGroupNodes
	 */
	public void removeStoredTopGroupNodes(User user) throws RemoveException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserTopGroupNodes
	 */
	public boolean storeUserTopGroupNodes(User user, Collection nodeGroups,
			int numberOfPermissions, String totalLoginTime, String comment)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllGroupsWithEditPermission
	 */
	public Collection getAllGroupsWithEditPermission(User user,
			IWUserContext iwuc) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllGroupsWithViewPermission
	 */
	public Collection getAllGroupsWithViewPermission(User user,
			IWUserContext iwuc) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#moveUsers
	 */
	public Map moveUsers(IWUserContext iwuc, Collection userIds,
			Group parentGroup, int targetGroupId) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#moveUsers
	 */
	public Map moveUsers(IWUserContext iwuc, Collection userIds,
			Group parentGroup, int targetGroupId,
			boolean leaveCopyOfUserInCurrentGroup, boolean copyOrMoveUserInfo)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#moveUsers
	 */
	public Map moveUsers(IWUserContext iwuc, Collection groups,
			Collection groupTypesToMoveAmong) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isMemberOfGroup
	 */
	public boolean isMemberOfGroup(int parentGroupToTest, User user)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isUserSuitedForGroup
	 */
	public String isUserSuitedForGroup(User user, Group targetGroup)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserApplicationStyleSheetURL
	 */
	public String getUserApplicationStyleSheetURL() throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#isInDefaultCommune
	 */
	public boolean isInDefaultCommune(User user) throws RemoteException,
			FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#updateUsersMainAddressByFullAddressString
	 */
	public void updateUsersMainAddressByFullAddressString(User user,
			String fullAddressString) throws RemoteException, CreateException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserByUniqueId
	 */
	public User getUserByUniqueId(String uniqueID) throws FinderException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersBySpecificGroupsUserstatusDateOfBirthAndGender
	 */
	public Collection getUsersBySpecificGroupsUserstatusDateOfBirthAndGender(
			Collection groups, Collection userStatuses,
			Integer yearOfBirthFrom, Integer yearOfBirthTo, String gender)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#storeUserComment
	 */
	public void storeUserComment(User user, String comment, User performer)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserComments
	 */
	public Collection getUserComments(User user) throws FinderException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#callAllUserGroupPluginAfterUserCreateOrUpdateMethod
	 */
	public void callAllUserGroupPluginAfterUserCreateOrUpdateMethod(User user)
			throws CreateException, RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#callAllUserGroupPluginAfterUserCreateOrUpdateMethod
	 */
	public void callAllUserGroupPluginAfterUserCreateOrUpdateMethod(User user,
			Group parentGroup) throws CreateException, RemoteException,
			RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#callAllUserGroupPluginBeforeUserRemoveMethod
	 */
	public void callAllUserGroupPluginBeforeUserRemoveMethod(User user,
			Group parentGroup) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#cleanUserEmails
	 */
	public void cleanUserEmails();

	/**
	 * @see com.idega.user.business.UserBusinessBean#setUsersPreferredLocale
	 */
	public void setUsersPreferredLocale(User user, String preferredLocale,
			boolean storeUser) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUsersPreferredLocale
	 */
	public Locale getUsersPreferredLocale(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#validateIcelandicSSN
	 */
	public boolean validateIcelandicSSN(String ssn) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#hasValidIcelandicSSN
	 */
	public boolean hasValidIcelandicSSN(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getGroupsMembersData
	 */
	public List getGroupsMembersData(List uniqueIds);

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserStatus
	 */
	public Status getUserStatus(IWContext iwc, User user, Group group)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserStatus
	 */
	public Status getUserStatus(IWContext iwc, int userId, int groupId)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getUserDateOfBirthFromPersonalId
	 */
	public Date getUserDateOfBirthFromPersonalId(String personalId)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllUserGroupsIds
	 */
	public List getAllUserGroupsIds(User user, IWUserContext iwuc)
			throws RemoteException, RemoteException;

	/**
	 * @see com.idega.user.business.UserBusinessBean#getAllUserGroups
	 */
	public List getAllUserGroups(User user, IWUserContext iwuc)
			throws RemoteException, RemoteException;
}