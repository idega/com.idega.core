package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import javax.ejb.*;

import com.idega.core.accesscontrol.business.LoginCreateException;
import com.idega.core.data.Address;
import com.idega.core.data.AddressType;
import com.idega.core.data.Phone;
import com.idega.idegaweb.IWUserContext;
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
 public void updateUserJob(int p0,java.lang.String p1);
 public void updateUserWorkPlace(int p0,java.lang.String p1);
 public java.util.Collection listOfUserEmails(int p0) throws java.rmi.RemoteException;
 public void setUserUnderDomain(com.idega.builder.data.IBDomain p0,com.idega.user.data.User p1,com.idega.user.data.GroupDomainRelationType p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void deleteUser(int p0, com.idega.user.data.User p1)throws javax.ejb.RemoveException, java.rmi.RemoteException;
 public void deleteUser(com.idega.user.data.User p0, com.idega.user.data.User p1)throws javax.ejb.RemoveException, java.rmi.RemoteException; 
 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public com.idega.core.data.Phone getUsersWorkPhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUserByPersonalIDIfDoesNotExist(java.lang.String p0,java.lang.String p1,com.idega.user.data.Gender p2,com.idega.util.IWTimestamp p3)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Email lookupEmail(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.core.business.AddressBusiness getAddressBusiness()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Email getUserMail(int p0) throws java.rmi.RemoteException;
 public java.lang.String getUserJob(com.idega.user.data.User p0);
 public java.lang.String getUserWorkPlace(com.idega.user.data.User p0);
 public java.util.Collection getUsersInPrimaryGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersInNoGroup()throws java.sql.SQLException, java.rmi.RemoteException;
 public com.idega.user.data.UserHome getUserHome() throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4,com.idega.util.IWTimestamp p5)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.AddressHome getAddressHome() throws java.rmi.RemoteException;
 public com.idega.core.data.Phone getUsersFaxPhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 public com.idega.user.business.UserProperties getUserProperties(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.user.business.UserProperties getUserProperties(int p0) throws java.rmi.RemoteException;
 public com.idega.core.data.Address getUserAddress1(int p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9,java.lang.String p10)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9,java.lang.String p10)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.PhoneHome getPhoneHome() throws java.rmi.RemoteException;
 public java.lang.String getNameOfGroupOrUser(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User insertUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.Integer p5,com.idega.util.IWTimestamp p6,java.lang.Integer p7)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType)throws javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.user.data.User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType)throws javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.user.data.User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType,String fullName)throws javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.core.data.Email getUserMail(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public void updateUserPhone(int p0,int p1,java.lang.String p2)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.core.data.EmailHome getEmailHome() throws java.rmi.RemoteException;
 public com.idega.core.data.Address getUsersMainAddress(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Address getUsersCoAddress(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Email getUsersMainEmail(com.idega.user.data.User p0)throws com.idega.user.business.NoEmailFoundException, java.rmi.RemoteException;
 public com.idega.builder.data.IBPage getHomePageForUser(com.idega.user.data.User p0)throws javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.core.data.Phone[] getUserPhones(int p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Phone getUsersHomePhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersInGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Phone getUsersMobilePhone(com.idega.user.data.User p0)throws com.idega.user.business.NoPhoneFoundException, java.rmi.RemoteException;
 public com.idega.user.data.User getUser(java.lang.Integer p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUserByPersonalIDIfDoesNotExist(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4,com.idega.util.IWTimestamp p5)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7,com.idega.util.IWTimestamp p8,java.lang.Integer p9)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.accesscontrol.data.LoginTable generateUserLogin(com.idega.user.data.User p0)throws LoginCreateException, java.rmi.RemoteException;
 public void addNewUserEmail(int p0,java.lang.String p1) throws java.rmi.RemoteException;
 public com.idega.core.accesscontrol.data.LoginTable generateUserLogin(int p0)throws java.lang.Exception, java.rmi.RemoteException;
 public int getHomePageIDForUser(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public java.util.Collection getUserGroups(com.idega.user.data.User p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.business.GroupBusiness getGroupBusiness()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setPermissionGroup(com.idega.user.data.User p0,java.lang.Integer p1)throws com.idega.data.IDOStoreException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection listOfUserGroups(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,int p3)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUserGroups(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUserGroupsDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4,com.idega.util.IWTimestamp p5,com.idega.user.data.Group p6)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,com.idega.user.data.Group p3)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
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
 public com.idega.core.data.Phone getUserPhone(int p0,int p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUserGroupsDirectlyRelated(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersInGroup(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,com.idega.util.IWTimestamp p7,java.lang.Integer p8)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,com.idega.util.IWTimestamp p7,java.lang.Integer p8,java.lang.String p9)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Address updateUsersMainAddressOrCreateIfDoesNotExist(java.lang.Integer p0,java.lang.String p1,java.lang.Integer p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Address updateUsersCoAddressOrCreateIfDoesNotExist(java.lang.Integer p0,java.lang.String p1,java.lang.Integer p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException; 
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

	/**
	 * Cast a Group that is a "UserReresentative" Group to a User instance.
	 * @param userGroupCollection A Collection with instnances of a Group that are really a "UserReresentative" groups i.e. the Group representation of the User
	 * @return Collection of user instances representing the Groups
	 * @throws EJBException If an error occurs casting
	 */
	public Collection castUserGroupsToUsers(Collection userGroupCollection)throws EJBException,RemoteException;
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
	 *  Returns User from personal id, throws EJBException if not found
	 */
	public  User getUser(String personalID) throws EJBException;

	
	public Collection getUsersTopGroupNodesByViewAndOwnerPermissions(User user, IWUserContext iwuc)throws RemoteException;

  public void removeUserFromGroup(User user, Group group, User currentUser) throws RemoveException;
  public void removeUserFromGroup(int userId, Group group, User currentUser) throws RemoveException;
  public Collection getAllGroupsWithEditPermission(User user, IWUserContext iwuc);
	public Collection getAllGroupsWithViewPermission(User user, IWUserContext iwuc);
  public Collection moveUsers(Collection userIds, Group parentGroup, int targetGroupId, User currentUser);
  public Map moveUsers(Group parentGroup, int targetGroupId, User currentUser);
}
