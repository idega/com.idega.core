package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.core.accesscontrol.business.LoginCreateException;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.user.data.Gender;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

public interface UserBusiness extends com.idega.business.IBOService
{
 public java.util.Collection getUsers()throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User getUser(int p0) throws java.rmi.RemoteException;


// public java.util.Collection getUserGroupsNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getParentGroupsInDirectForUser(int p0) throws java.rmi.RemoteException;
 
 public void updateUserMail(int p0,java.lang.String p1)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUserMail(com.idega.user.data.User p0,java.lang.String p1)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUserJob(int p0,java.lang.String p1);
 public void updateUserWorkPlace(int p0,java.lang.String p1);
 public java.util.Collection listOfUserEmails(int p0) throws java.rmi.RemoteException;
 public void setUserUnderDomain(com.idega.core.builder.data.ICDomain p0,com.idega.user.data.User p1,com.idega.user.data.GroupDomainRelationType p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void deleteUser(int p0, com.idega.user.data.User p1)throws javax.ejb.RemoveException, java.rmi.RemoteException;
 public void deleteUser(com.idega.user.data.User p0, com.idega.user.data.User p1)throws javax.ejb.RemoveException, java.rmi.RemoteException; 
 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public com.idega.core.contact.data.Phone getUsersWorkPhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUserByPersonalIDIfDoesNotExist(java.lang.String p0,java.lang.String p1,com.idega.user.data.Gender p2,com.idega.util.IWTimestamp p3)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.contact.data.Email lookupEmail(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.core.location.business.AddressBusiness getAddressBusiness()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.contact.data.Email getUserMail(int p0) throws java.rmi.RemoteException;
 public java.lang.String getUserJob(com.idega.user.data.User p0);
 public java.lang.String getUserWorkPlace(com.idega.user.data.User p0);
 public java.util.Collection getUsersInPrimaryGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersInNoGroup()throws java.sql.SQLException, java.rmi.RemoteException;
 public com.idega.user.data.UserHome getUserHome() throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4,com.idega.util.IWTimestamp p5)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.AddressHome getAddressHome() throws java.rmi.RemoteException;
 public com.idega.core.contact.data.Phone getUsersFaxPhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 public com.idega.user.business.UserProperties getUserProperties(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.user.business.UserProperties getUserProperties(int p0) throws java.rmi.RemoteException;
 public com.idega.core.location.data.Address getUserAddress1(int p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9,java.lang.String p10)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9,java.lang.String p10)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.contact.data.PhoneHome getPhoneHome() throws java.rmi.RemoteException;
 public java.lang.String getNameOfGroupOrUser(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User insertUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.Integer p5,com.idega.util.IWTimestamp p6,java.lang.Integer p7)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType)throws javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.user.data.User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType)throws javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.user.data.User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType,String fullName)throws javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.core.contact.data.Email getUserMail(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public void updateUserPhone(int p0,int p1,java.lang.String p2)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public void updateUserPhone(com.idega.user.data.User p0,int p1,java.lang.String p2)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public void updateUserHomePhone(com.idega.user.data.User p0,java.lang.String p1)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public void updateUserWorkPhone(com.idega.user.data.User p0,java.lang.String p1)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public void updateUserMobilePhone(com.idega.user.data.User p0,java.lang.String p1)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.core.contact.data.EmailHome getEmailHome() throws java.rmi.RemoteException;
 public com.idega.core.location.data.Address getUsersMainAddress(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.location.data.Address getUsersCoAddress(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.contact.data.Email getUsersMainEmail(com.idega.user.data.User p0)throws com.idega.user.business.NoEmailFoundException, java.rmi.RemoteException;
 public com.idega.core.builder.data.ICPage getHomePageForUser(com.idega.user.data.User p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.core.contact.data.Phone[] getUserPhones(int p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.contact.data.Phone getUsersHomePhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersInGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(String firstname, String middlename, String lastname,String personalID)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.contact.data.Phone getUsersMobilePhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 public com.idega.user.data.User getUser(java.lang.Integer p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUserByPersonalIDIfDoesNotExist(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4,com.idega.util.IWTimestamp p5)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.accesscontrol.data.LoginTable generateUserLogin(com.idega.user.data.User p0)throws LoginCreateException, java.rmi.RemoteException;
 /**
  * Adds an email to the given user, existent email records for the user are left untouched
  * see storeUserEmail for email replacements
  * @param userId
  * @param emailAddress
  * @throws java.rmi.RemoteException
  */
 public void addNewUserEmail(int userId,java.lang.String emailAddress) throws java.rmi.RemoteException;
 public com.idega.core.accesscontrol.data.LoginTable generateUserLogin(int p0)throws java.lang.Exception, java.rmi.RemoteException;
 public int getHomePageIDForUser(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public java.util.Collection getUserGroups(com.idega.user.data.User p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.business.GroupBusiness getGroupBusiness()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setPermissionGroup(com.idega.user.data.User p0,java.lang.Integer p1)throws com.idega.data.IDOStoreException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection listOfUserGroups(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(String firstName, String middleName, String lastName, int primary_groupID)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUserGroups(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUserGroupsDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(String firstname, String middlename, String lastname,String personalID, Gender gender, IWTimestamp dateOfBirth,Group primaryGroup)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(String firstName, String middleName, String lastName, Group primary_group)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllUsersOrderedByFirstName()throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getGroupHome() throws java.rmi.RemoteException;

 public java.util.Collection getNonParentGroups(int p0) throws java.rmi.RemoteException;
/**
 * Gets all the groups that the user is in recursively up the group tree filtered with specified groupTypes
 * @param aUser a User to find parent Groups for
 * @param groupTypes the Groups a String array of group types of which the Groups to be returned must be
= * @return Collection of Groups found recursively up the tree
 * @throws EJBException If an error occured
 */
  public Collection getUserGroups(User aUser, String[] groupTypes) throws EJBException,java.rmi.RemoteException;
  
 public java.lang.Integer getGenderId(java.lang.String p0)throws java.lang.Exception, java.rmi.RemoteException;
 public com.idega.core.contact.data.Phone getUserPhone(int p0,int p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUserGroupsDirectlyRelated(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersInGroup(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,com.idega.util.IWTimestamp p7,java.lang.Integer p8)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,com.idega.util.IWTimestamp p7,java.lang.Integer p8,java.lang.String p9)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 /**
  * @deprecated Replaced by updateUsersCoAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber, PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID)
  */
 public com.idega.core.location.data.Address updateUsersMainAddressOrCreateIfDoesNotExist(java.lang.Integer p0,java.lang.String p1,java.lang.Integer p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6, Integer communeID)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 /**
  * @deprecated Replaced by updateUsersMainAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber, PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID)
  */
 public com.idega.core.location.data.Address updateUsersCoAddressOrCreateIfDoesNotExist(java.lang.Integer p0,java.lang.String p1,java.lang.Integer p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6, Integer communeID)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException; 
 public Address updateUsersCoAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber, PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID) throws CreateException,RemoteException;
 public Address updateUsersMainAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber, PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID) throws CreateException,RemoteException;
// public com.idega.core.location.data.Address updateUsersMainAddressOrCreateIfDoesNotExist(java.lang.Integer p0,java.lang.String p1,java.lang.Integer p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
// public com.idega.core.location.data.Address updateUsersCoAddressOrCreateIfDoesNotExist(java.lang.Integer p0,java.lang.String p1,java.lang.Integer p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException; 
 public java.util.Collection getUserGroups(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;


	/**
	 * Cast a Group that is a "UserReresentative" Group to a User instance.
	 * @param userGroups An instance of a Group that is really a "UserReresentative" group i.e. the Group representation of the User
	 * @param userGroup A instnance of a Group that is really a "UserReresentative" group i.e. the Group representation of the User
	 * @return User
	 * @throws EJBException If an error occurs casting
	 */
	public User castUserGroupToUser(Group userGroup)throws EJBException,RemoteException;

	public boolean hasUserLogin(User user) throws RemoteException;
	public boolean hasUserLogin(int userID) throws RemoteException;
	 public Address getUserAddressByAddressType(int userID,AddressType type) throws EJBException,RemoteException;

  public Address getUsersMainAddress(int userID) throws EJBException,RemoteException;
  public Address getUsersCoAddress(int userID) throws EJBException,RemoteException;
  public Collection getUsersMainAddresses(String[] userIDs) throws EJBException,RemoteException;
  public Collection getUsers(String[] userIDs) throws EJBException,RemoteException;
	public  Phone[] getUserPhones(User user)throws RemoteException;
	public Collection getUsers(com.idega.data.IDOQuery query) throws EJBException,RemoteException;
	public Collection getUsersMainAddresses(com.idega.data.IDOQuery query) throws EJBException,RemoteException;
	/**
	 *  Returns User from personal id returns null if not found
	 */
	public  User getUser(String personalID) throws FinderException;
	public com.idega.user.data.User getUserByPartOfPersonalIdAndFirstName(java.lang.String p0,java.lang.String p1) throws FinderException;

	
	public Collection getUsersTopGroupNodesByViewAndOwnerPermissions(User user, IWUserContext iwuc)throws RemoteException;
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user) throws RemoteException;
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user,Collection topGroupNodes) throws RemoteException;

  public void removeUserFromGroup(User user, Group group, User currentUser) throws RemoveException;
  public void removeUserFromGroup(int userId, Group group, User currentUser) throws RemoveException;
  public Collection getAllGroupsWithEditPermission(User user, IWUserContext iwuc);
	public Collection getAllGroupsWithViewPermission(User user, IWUserContext iwuc);
  public Map moveUsers(IWUserContext iwuc,Collection userIds, Group parentGroup, int targetGroupId);
  public Map moveUsers(IWUserContext iwuc, Collection groupIds, Collection groupTypesToMoveAmong);
  
	public Group getUsersHighestTopGroupNode(User user, List groupTypes, IWUserContext iwuc) throws RemoteException;
  /**
   * 
   * Description: Returns the url of the style sheet used <br>
   * @param parentPage is the parent page. iwb is the bundle containing the style sheet
   * @author <a href="mailto:birna@idega.is">Birna Iris Jonsdottir</a>
   */
  public String getUserApplicationStyleSheet(Page parentPage, IWContext iwc);

  public boolean isMemberOfGroup(int parentGroupToTest, User user) throws RemoteException;
	public boolean isInDefaultCommune(User user) throws RemoteException, FinderException;

	/**
	   * Adds email to the given user, and removes older emails if requested
	   */
	  public Email storeUserEmail(Integer userID,String emailAddress,boolean replaceExistentRecords )throws RemoteException;
	  
	  /**
	   * Adds email to the given user, and removes older emails if requested
	   */
	  public Email storeUserEmail(User user,String emailAddress,boolean replaceExistentRecords )throws RemoteException;
	  
	  /**
	   * Removes email relations to given user
	   * @param user
	   * @return true if successfull, else false
	   */
	  public boolean removeUserEmails(User user);
	  
	  public Collection getStoredTopNodeGroups(User user)throws RemoteException;
	  public Collection getStoredTopGroupNodes(User user)throws RemoteException;
	  public void removeStoredTopGroupNodes(User user)throws RemoveException,RemoteException;
	  
	  public User createOrUpdateUser(DN distinguishedName,Attributes attributes) throws CreateException,NamingException,RemoteException;
	  public User createOrUpdateUser(DN distinguishedName,Attributes attributes, Group parentGroup) throws CreateException,NamingException,RemoteException;
	  
		/**
		 * Adds all the ldap attributes as metadata-fields
		 * @param group
		 * @param distinguishedName
		 * @param attributes
		 */
		public void setMetaDataFromLDAPAttributes(User user, DN distinguishedName, Attributes attributes);
		public Collection getUsersByLDAPAttribute(String key, String value);
		public boolean hasTopNodes(User user, IWUserContext iwuc) throws RemoteException;
		
		/**
		 * Tests if the installed plugins accept the given user into the given group.
		 * Returns a message to be displayed to performing user
		 * @param user
		 * @param targetGroup
		 * @return
		 * @throws RemoteException
		 */
		 public String isUserSuitedForGroup(User user, Group targetGroup)throws RemoteException;
		 
		 public User getUserByUniqueId(String uniqueID) throws FinderException;
		 /**
			 * Looks for the user by his DN in his metadata
			 * @param identifier
			 * @return
			 */
			public User getUserByDirectoryString(String dn);
			
			/**
			 * Looks for the user by his DN in his metadata
			 * @param identifier
			 * @return
			 */
			public User getUserByDirectoryString(DirectoryString dn);
}
