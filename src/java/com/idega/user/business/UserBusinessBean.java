package com.idega.user.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;

import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.LoginCreateException;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneBMPBean;
import com.idega.core.contact.data.PhoneHome;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.ldap.util.IWLDAPUtil;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOStoreException;
import com.idega.data.IDOUtil;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.TopNodeGroup;
import com.idega.user.data.TopNodeGroupHome;
import com.idega.user.data.User;
import com.idega.user.data.UserComment;
import com.idega.user.data.UserCommentHome;
import com.idega.user.data.UserHome;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.Timer;
import com.idega.util.datastructures.NestedSetsContainer;
import com.idega.util.text.Name;

/**
 * Title: UserBusinessBean <br>
 * Description: A collection of methods to create, remove, lookup and manipulate
 * a User. See also GroupBusinessBean <br>
 * Copyright: Copyright (c) 2002-2004 <br>
 * Company: Idega Software <br>
 * 
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson </a>, <a href="eiki@idega.is">Eirikur S. Hrafnsson </a> <br>
 * @version 1.6
 */
public class UserBusinessBean extends com.idega.business.IBOServiceBean implements UserBusiness, IWLDAPConstants {

	// remove use of "null" when metadata can be removed
	private static final String NULL = "null";

	private static final String JOB_META_DATA_KEY = "job";

	private static final String WORKPLACE_META_DATA_KEY = "workplace";

	private static final String SESSION_KEY_TOP_NODES = "top_nodes_for_user";

	private static final int NUMBER_OF_PERMISSIONS_CACHING_LIMIT = 800;

	private GroupHome groupHome;

	private UserHome userHome;

	private EmailHome emailHome;

	private AddressHome addressHome;

	private PhoneHome phoneHome;

	private TopNodeGroupHome topNodeGroupHome;

	private Gender male, female;

	private Map pluginsForGroupTypeCachMap = new HashMap();

	/**
	 * if stored prosedure is not available and users view and owner permissions
	 * for groups are more than <code>searchForTopNodesFromTop</code> then the
	 * top nodes are searched from the top of the tree, otherwise the collection
	 * of the groups that the user has collection for is used to find which of
	 * them are topnodes
	 */
	private int searchForTopNodesFromTop = 3000;

	public UserBusinessBean() {
	}

	public UserHome getUserHome() {
		if (userHome == null) {
			try {
				userHome = (UserHome) IDOLookup.getHome(User.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return userHome;
	}

	public GroupHome getGroupHome() {
		if (groupHome == null) {
			try {
				groupHome = (GroupHome) IDOLookup.getHome(Group.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupHome;
	}

	public EmailHome getEmailHome() {
		if (emailHome == null) {
			try {
				emailHome = (EmailHome) IDOLookup.getHome(Email.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return emailHome;
	}

	public AddressHome getAddressHome() {
		if (addressHome == null) {
			try {
				addressHome = (AddressHome) IDOLookup.getHome(Address.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return addressHome;
	}

	public PhoneHome getPhoneHome() {
		if (phoneHome == null) {
			try {
				phoneHome = (PhoneHome) IDOLookup.getHome(Phone.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return phoneHome;
	}

	public TopNodeGroupHome getTopNodeGroupHome() {
		if (topNodeGroupHome == null) {
			try {
				topNodeGroupHome = (TopNodeGroupHome) IDOLookup.getHome(TopNodeGroup.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return topNodeGroupHome;
	}

	/**
	 * @deprecated replaced with createUser
	 */
	public User insertUser(String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group)
			throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, displayname, null, description, gender, date_of_birth,
				primary_group);
	}

	/**
	 * Method createUserByPersonalIDIfDoesNotExist either created a new user or
	 * updates an old one.
	 * 
	 * @param fullName
	 * @param personalID
	 * @param gender
	 * @param dateOfBirth
	 * @return User
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public User createUserByPersonalIDIfDoesNotExist(String fullName, String personalID, Gender gender,
			IWTimestamp dateOfBirth) throws CreateException, RemoteException {
		User user = null;
		if (personalID != null && personalID.trim().length() > 0) {
			// if a user exists with same personal id, we'll use him instead of
			// creating a new one
			try {
				user = getUserHome().findByPersonalID(personalID);
			}
			catch (FinderException e) {
				// user is still null, that's ok we'll create a new one
			}
		}
		if (user != null) {
			// found user with given personal id, use that user since no two
			// users can have same personal id
			user.setFullName(fullName);
			if (user.getDisplayName() == null || "".equals(user.getDisplayName().trim())) {
				user.setDisplayName(fullName);
			}
			if (gender != null)
				user.setGender((Integer) gender.getPrimaryKey());
			if (dateOfBirth != null)
				user.setDateOfBirth(dateOfBirth.getDate());
			user.store();
		}
		else {
			Name name = new Name(fullName);
			user = createUser(name.getFirstName(), name.getMiddleName(), name.getLastName(), fullName, personalID,
					null, gender != null ? (Integer) gender.getPrimaryKey() : null, dateOfBirth, null);
			//user = createUser(name.getFirstName(), name.getMiddleName() ,
			// name.getLastName() , personalID, gender, dateOfBirth);
		}
		return user;
	}

	/**
	 * Method createUserByPersonalIDIfDoesNotExist does what is says.
	 * 
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param personalID
	 * @param gender
	 * @param dateOfBirth
	 * @return User
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public User createUserByPersonalIDIfDoesNotExist(String firstName, String middleName, String lastName,
			String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException, RemoteException {
		User user;
		Name name = new Name(firstName, middleName, lastName);
		String fullName = name.getName();
		user = createUserByPersonalIDIfDoesNotExist(fullName, personalID, gender, dateOfBirth);
		return user;
	}

	public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group)
			throws CreateException, RemoteException {
		return createUser(firstName, middleName, lastName, displayname, personalID, description, gender, date_of_birth,
				primary_group, null);
	}

	public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String fullName)
			throws CreateException, RemoteException {
		try {
			User userToAdd = getUserHome().create();
			if (fullName == null) {
				Name name = new Name(firstName, middleName, lastName);
				fullName = name.getName();
			}
			userToAdd.setFullName(fullName);
			/*
			 * userToAdd.setFirstName(firstName);
			 * userToAdd.setMiddleName(middleName);
			 * userToAdd.setLastName(lastName);
			 */
			if (displayname != null) {
				userToAdd.setDisplayName(displayname);
			}
			if (description != null) {
				userToAdd.setDescription(description);
			}
			if (personalID != null) {
				userToAdd.setPersonalID(personalID);
			}
			if (gender != null) {
				userToAdd.setGender(gender);
			}
			if (date_of_birth != null) {
				userToAdd.setDateOfBirth(date_of_birth.getSQLDate());
			}
			if (primary_group != null) {
				userToAdd.setPrimaryGroupID(primary_group);
			}
			userToAdd.store();
			setUserUnderDomain(this.getIWApplicationContext().getDomain(), userToAdd, (GroupDomainRelationType) null);
			//    UserGroupRepresentative group =
			// (UserGroupRepresentative)this.getUserGroupRepresentativeHome().create();
			//    group.setName(userToAdd.getName());
			//    group.setDescription("User representative in table ic_group");
			//    group.store();
			//    userToAdd.setGroup(group);
			//    userToAdd.store();
			if (primary_group != null) {
				Group prgr = userToAdd.getPrimaryGroup();
				prgr.addGroup(userToAdd);
			}
			return userToAdd;
		}
		catch (Exception e) {
			getLogger().warning(
					"Error creating user with personalID=" + personalID + ", firstName=" + firstName + ", lastName"
							+ lastName);
			throw new IDOCreateException(e);
		}
	}

	public void setUserUnderDomain(ICDomain domain, User user, GroupDomainRelationType type) throws CreateException,
			RemoteException {
		GroupDomainRelation relation = (GroupDomainRelation) IDOLookup.create(GroupDomainRelation.class);
		relation.setDomain(domain);
		relation.setRelatedUser(user);
		if (type != null) {
			relation.setRelationship(type);
		}
		relation.store();
	}

	/**
	 * Generates a login for a user with a random password and a login derived
	 * from the users name (or random login if all possible logins are taken)
	 * 
	 * @param userId
	 *            the id for the user.
	 * @throws LoginCreateException
	 *             If an error occurs creating login for the user.
	 */
	public LoginTable generateUserLogin(int userID) throws LoginCreateException, RemoteException {
		//return this.generateUserLogin(userID);
		return LoginDBHandler.generateUserLogin(userID);
	}

	/**
	 * Generates a login for a user with a random password and a login derived
	 * from the users name (or random login if all possible logins are taken)
	 */
	public LoginTable generateUserLogin(User user) throws LoginCreateException, RemoteException {
		//return LoginDBHandler.generateUserLogin(user);
		int userID = ((Integer) user.getPrimaryKey()).intValue();
		return this.generateUserLogin(userID);
	}

	/**
	 * Creates a user with a firstname,middlename, lastname, where middlename
	 * can be null
	 */
	public User createUser(String firstname, String middlename, String lastname) throws CreateException,
			RemoteException {
		return createUser(firstname, middlename, lastname, (String) null);
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname and personalID
	 * where middlename and personalID can be null
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID)
			throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null, null, null, null);
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname and
	 * primaryGroupID where middlename can be null
	 */
	public User createUser(String firstName, String middleName, String lastName, int primary_groupID)
			throws CreateException, RemoteException {
		return createUser(firstName, middleName, lastName, null, null, null, null, null, new Integer(primary_groupID));
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname and
	 * primaryGroupID where middlename can be null but primary_group can not be
	 * noull
	 */
	public User createUser(String firstName, String middleName, String lastName, Group primary_group)
			throws CreateException, RemoteException {
		return createUser(firstName, middleName, lastName, null, null, null, null, null,
				(Integer) primary_group.getPrimaryKey());
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname ,personalID and
	 * gender where middlename and personalID can be null
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender)
			throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null, (Integer) gender.getPrimaryKey(),
				null, null);
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname ,personalID,
	 * gender and date of birth where middlename,personalID,gender,dateofbirth
	 * can be null
	 * 
	 * @throws NullPointerException
	 *             if primaryGroup is null
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender,
			IWTimestamp dateOfBirth, Group primaryGroup) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null, (Integer) gender.getPrimaryKey(),
				dateOfBirth, (Integer) primaryGroup.getPrimaryKey());
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname ,personalID,
	 * gender and date of birth where middlename,personalID,gender,dateofbirth
	 * can be null
	 */
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender,
			IWTimestamp dateOfBirth) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null,
				gender != null ? (Integer) gender.getPrimaryKey() : null, dateOfBirth, null);
	}

	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,
			String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group,
			String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity,
			Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType)
			throws CreateException {
		return createUserWithLogin(firstname, middlename, lastname, SSN, displayname, description, gender,
				date_of_birth, primary_group, userLogin, password, accountEnabled, modified, daysOfValidity,
				passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType, null);
	}

	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN,
			String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group,
			String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity,
			Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType,
			String fullName) throws CreateException {
		UserTransaction transaction = this.getSessionContext().getUserTransaction();
		try {
			transaction.begin();
			User newUser;
			// added by Aron 07.01.2002 ( aron@idega.is )
			if (primary_group == null)
				primary_group = new Integer(GroupBMPBean.GROUP_ID_USERS);
			//newUser = insertUser(firstname,middlename,
			// lastname,null,null,null,null,primary_group);
			newUser = createUser(firstname, middlename, lastname, displayname, SSN, description, gender, date_of_birth,
					primary_group, fullName);
			if (userLogin != null && password != null && !userLogin.equals("") && !password.equals("")) {
				LoginDBHandler.createLogin(newUser, userLogin, password, accountEnabled, modified, daysOfValidity,
						passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
			}
			transaction.commit();
			return newUser;
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				transaction.rollback();
			}
			catch (SystemException se) {
			}
			throw new CreateException(e.getMessage());
		}
	}

	public User createUserWithLogin(String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin,
			String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires,
			Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws CreateException {
		return createUserWithLogin(firstname, middlename, lastname, null, displayname, description, gender,
				date_of_birth, primary_group, userLogin, password, accountEnabled, modified, daysOfValidity,
				passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
	}

	/*
	 * public User getUser(int userGroupRepresentativeID) throws SQLException {
	 * List l =
	 * EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(User.class),com.idega.user.data.UserBMPBean._COLUMNNAME_USER_GROUP_ID,userGroupRepresentativeID);
	 * if(l != null && l.size() > 0){ return ((User)l.get(0)); } return null; }
	 * 
	 * public int getUserID(int userGroupRepresentativeID) throws SQLException {
	 * User user = getUser(userGroupRepresentativeID); if(user != null){ return
	 * user.getID(); } return -1; }
	 */
	/**
	 * This methods removes this user from all groups and deletes his login.
	 */
	public void deleteUser(int userId, User currentUser) throws RemoveException {
		User delUser = getUser(userId);
		deleteUser(delUser, currentUser);
	}

	/**
	 * This methods remoces this useer from all groups and deletes his login.
	 */
	public void deleteUser(User delUser, User currentUser) throws RemoveException {
		try {
			Collection groups = getGroupBusiness().getParentGroups(delUser);
			if (groups != null && !groups.isEmpty()) {
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
					Group group = (Group) iter.next();
					removeUserFromGroup(delUser, group, currentUser);
				}
			}
			LoginDBHandler.deleteUserLogin(delUser.getID());
			//delUser.removeAllAddresses();
			//delUser.removeAllEmails();
			//delUser.removeAllPhones();
			/*
			 * try { this.getGroupBusiness().deleteGroup(groupId); }catch
			 * (FinderException fe) { System.out.println("[UserBusinessBean] :
			 * cannot find group to delete with user"); }
			 */
			delUser.delete(currentUser.getID());
			delUser.store();
		}
		catch (Exception e) {
			//e.printStackTrace(System.err);
			throw new RemoveException(e.getMessage());
		}
	}

	public void removeUserFromGroup(int userId, Group group, User currentUser) throws RemoveException {
		User user = getUser(userId);
		removeUserFromGroup(user, group, currentUser);
	}

	public void removeUserFromGroup(User user, Group group, User currentUser) throws RemoveException{
		group.removeUser(user, currentUser);
		Integer primaryGroupId = new Integer(user.getPrimaryGroupID());
		if(group.getPrimaryKey().equals(primaryGroupId)) {
			// update primary group for user, since it was the group the user was removed from
			Collection groups = getUserGroups(user, new String[] {"general"});
			Iterator iter = groups.iterator();
			Group newPG = null;
			if(groups != null && !groups.isEmpty()) {
				// no smart way to find new primary group, just set as first group in user groups collection that
				// is not same as current primary group
				newPG = (Group)iter.next();
				while(newPG != null && newPG.getPrimaryKey().equals(primaryGroupId)) {
					if(iter.hasNext()) {
						newPG = (Group) iter.next();
					} else {
						newPG = null;
					}
				}
			}
			if(newPG != null) {
				user.setPrimaryGroup(newPG);
			} else {
				user.setPrimaryGroupID(-1);
			}
			user.store();
		}
	}

	public void setPermissionGroup(User user, Integer primaryGroupId) throws IDOStoreException, RemoteException {
		if (primaryGroupId != null) {
			user.setPrimaryGroupID(primaryGroupId);
			user.store();
		}
	}

	/**
	 * Male: M, male, 0 Female: F, female, 1
	 */
	public Integer getGenderId(String gender) throws Exception {
		try {
			GenderHome home = getGenderHome();
			if (gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("0")) {
				if (male == null) {
					male = home.getMaleGender();
				}
				return (Integer) male.getPrimaryKey();
			}
			else if (gender.equalsIgnoreCase("F") || gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("1")) {
				if (female == null) {
					female = home.getFemaleGender();
				}
				return (Integer) female.getPrimaryKey();
			}
			else {
				//throw new RuntimeException("String gender must be: M, male,
				// 0, F, female or 1 ");
				return null;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returnes true if that genderid refers to the male gender
	 */
	public boolean isMale(int genderId) throws RemoteException, FinderException {
		GenderHome home = getGenderHome();
		if (male == null) {
			male = home.getMaleGender();
		}
		return (((Integer) male.getPrimaryKey()).intValue() == genderId);
	}

	/**
	 * Returnes true if that genderid refers to the female gender
	 */
	public boolean isFemale(int genderId) throws RemoteException, FinderException {
		return !isMale(genderId);
	}

	private GenderHome getGenderHome() throws RemoteException {
		GenderHome home = (GenderHome) this.getIDOHome(Gender.class);
		return home;
	}

	public Phone[] getUserPhones(int userId) throws RemoteException {
		try {
			Collection phones = this.getUser(userId).getPhones();
			//	  if(phones != null){
			return (Phone[]) phones.toArray(new Phone[phones.size()]);
			//	  }
			//return (Phone[])
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
		}
		catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Phone[] getUserPhones(User user) throws RemoteException {
		try {
			Collection phones = user.getPhones();
			//		if(phones != null){
			return (Phone[]) phones.toArray(new Phone[phones.size()]);
			//		}
			//return (Phone[])
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
		}
		catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Phone getUserPhone(int userId, int phoneTypeId) throws RemoteException {
		try {
			Phone[] result = this.getUserPhones(userId);
			//IDOLegacyEntity[] result =
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					if (((Phone) result[i]).getPhoneTypeId() == phoneTypeId) {
						return (Phone) result[i];
					}
				}
			}
			return null;
		}
		catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Email getUserMail(int userId) {
		return getUserMail(this.getUser(userId));
	}

	public Email getUserMail(User user) {
		try {
			Collection L = user.getEmails();
			if (L != null) {
				if (!L.isEmpty())
					return (Email) L.iterator().next();
			}
			return null;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void updateUserHomePhone(User user, String phoneNumber) throws EJBException {
		updateUserPhone(user, PhoneBMPBean.getHomeNumberID(), phoneNumber);
	}

	public void updateUserWorkPhone(User user, String phoneNumber) throws EJBException {
		updateUserPhone(user, PhoneBMPBean.getWorkNumberID(), phoneNumber);
	}

	public void updateUserMobilePhone(User user, String phoneNumber) throws EJBException {
		updateUserPhone(user, PhoneBMPBean.getMobileNumberID(), phoneNumber);
	}

	public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber) throws EJBException {
		updateUserPhone(getUser(userId), phoneTypeId, phoneNumber);
	}

	public void updateUserPhone(User user, int phoneTypeId, String phoneNumber) throws EJBException {
		try {
			Phone phone = getUserPhone(((Integer) user.getPrimaryKey()).intValue(), phoneTypeId);
			boolean insert = false;
			if (phone == null) {
				phone = this.getPhoneHome().create();
				phone.setPhoneTypeId(phoneTypeId);
				insert = true;
			}
			if (phoneNumber != null) {
				phone.setNumber(phoneNumber);
			}
			phone.store();
			if (insert) {
				//((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(phone);
				user.addPhone(phone);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
	}

	public void updateUserMail(int userId, String email) throws CreateException, RemoteException {
		updateUserMail(getUser(userId), email);
	}

	public void updateUserMail(User user, String email) throws CreateException, RemoteException {
		if (email != null) {
			Email mail = getUserMail(((Integer) user.getPrimaryKey()).intValue());
			boolean insert = false;
			if (mail == null) {
				mail = this.getEmailHome().create();
				insert = true;
			}
			mail.setEmailAddress(email);
			mail.store();
			if (insert) {
				//((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(mail);
				try {
					user.addEmail(mail);
				}
				catch (Exception e) {
					throw new RemoteException(e.getMessage());
				}
			}
		}
	}

	public void updateUserJob(int userId, String job) {
		if (job == null || job.length() == 0)
			job = NULL;
		User user = getUser(userId);
		user.setMetaData(JOB_META_DATA_KEY, job);
		user.store();
	}

	public String getUserJob(User user) {
		String job = (String) user.getMetaData(JOB_META_DATA_KEY);
		if (job == null || NULL.equals(job))
			return "";
		else
			return job;
	}

	public void updateUserWorkPlace(int userId, String workPlace) {
		if (workPlace == null || workPlace.length() == 0)
			workPlace = NULL;
		User user = getUser(userId);
		user.setMetaData(WORKPLACE_META_DATA_KEY, workPlace);
		user.store();
	}

	public String getUserWorkPlace(User user) {
		String workPlace = (String) user.getMetaData(WORKPLACE_META_DATA_KEY);
		if (workPlace == null || NULL.equals(workPlace))
			return "";
		else
			return workPlace;
	}

	/**
	 * @deprecated user getUsersMainAddress instead. Gets the users main address
	 *             and returns it.
	 * @returns the address if found or null if not.
	 */
	public Address getUserAddress1(int userID) throws EJBException, RemoteException {
		return getUsersMainAddress(userID);
	}

	/**
	 * Gets the user's main address by addresstype and returns it.
	 * 
	 * @returns the address if found or null if not.
	 */
	public Address getUserAddressByAddressType(int userID, AddressType type) throws EJBException, RemoteException {
		try {
			return getAddressHome().findUserAddressByAddressType(userID, type);
		}
		catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Gets the user's main address and returns it.
	 * 
	 * @returns the address if found or null if not.
	 */
	public Address getUsersMainAddress(int userID) throws EJBException, RemoteException {
		try {
			return getAddressHome().findPrimaryUserAddress(userID);
		}
		catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Gets the users main addresses and returns them.
	 * 
	 * @returns a collection of addresses if found or null if not.
	 */
	public Collection getUsersMainAddresses(String[] userIDs) throws EJBException, RemoteException {
		try {
			return getAddressHome().findPrimaryUserAddresses(userIDs);
		}
		catch (FinderException fe) {
			return null;
		}
	}

	public Collection getUsersMainAddresses(IDOQuery query) throws EJBException, RemoteException {
		try {
			return getAddressHome().findPrimaryUserAddresses(query);
		}
		catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Gets the users main address and returns it.
	 * 
	 * @returns the address if found or null if not.
	 */
	public Address getUsersMainAddress(User user) throws RemoteException {
		return getUsersMainAddress(((Integer) user.getPrimaryKey()).intValue());
	}

	/**
	 * Gets the users co address and returns it.
	 * 
	 * @returns the address if found or null if not.
	 */
	public Address getUsersCoAddress(User user) throws RemoteException {
		return getUsersCoAddress(((Integer) user.getPrimaryKey()).intValue());
	}

	/**
	 * Gets the users co address and returns it.
	 * 
	 * @returns the address if found or null if not.
	 */
	public Address getUsersCoAddress(int userId) throws RemoteException {
		AddressType coAddressType = getAddressHome().getAddressType2();
		return getUserAddressByAddressType(userId, coAddressType);
	}

	/**
	 * Gets the users and returns them.
	 * 
	 * @returns a collection of users if found or null if not.
	 */
	public Collection getUsers(String[] userIDs) throws EJBException, RemoteException {
		try {
			return getUserHome().findUsers(userIDs);
		}
		catch (FinderException fe) {
			return null;
		}
	}

	public Collection getUsers(IDOQuery query) throws EJBException, RemoteException {
		try {
			return getUserHome().findUsersInQuery(query);
		}
		catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Method updateUsersMainAddressOrCreateIfDoesNotExist. This method can both
	 * be used to update the user main address or to create one <br>
	 * if one does not exist. Only userId and StreetName(AndNumber) are required
	 * to be not null others are optional.
	 * 
	 * @param userId
	 * @param streetNameAndNumber
	 * @param postalCodeId
	 * @param countryName
	 * @param city
	 * @param province
	 * @param poBox
	 * @return Address the address that was created or updated
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox)
			throws CreateException, RemoteException {
		return updateUsersMainAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName,
				city, province, poBox, null);
	}

	public Address updateUsersMainAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber,
			PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException {
		AddressType mainAddressType = getAddressHome().getAddressType1();
		return updateUsersAddressOrCreateIfDoesNotExist(user, streetNameAndNumber, postalCode, country, city, province,
				poBox, communeID, mainAddressType);
	}

	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException {
		AddressType mainAddressType = getAddressHome().getAddressType1();
		return updateUsersAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city,
				province, poBox, communeID, mainAddressType);
	}

	/**
	 * Method updateUsersCoAddressOrCreateIfDoesNotExist. This method can both
	 * be used to update the user co address or to create one <br>
	 * if one does not exist. Only userId and StreetName(AndNumber) are required
	 * to be not null others are optional.
	 * 
	 * @param userId
	 * @param streetNameAndNumber
	 * @param postalCodeId
	 * @param countryName
	 * @param city
	 * @param province
	 * @param poBox
	 * @return Address the address that was created or updated
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox)
			throws CreateException, RemoteException {
		return updateUsersCoAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city,
				province, poBox, null);
	}

	public Address updateUsersCoAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber,
			PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException {
		AddressType coAddressType = getAddressHome().getAddressType2();
		return updateUsersAddressOrCreateIfDoesNotExist(user, streetNameAndNumber, postalCode, country, city, province,
				poBox, communeID, coAddressType);
	}

	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID)
			throws CreateException, RemoteException {
		AddressType coAddressType = getAddressHome().getAddressType2();
		return updateUsersAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city,
				province, poBox, communeID, coAddressType);
	}

	private Address updateUsersAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID,
			AddressType addressType) throws CreateException, RemoteException {
		try {
			User user = getUser(userId);
			Country country = null;
			if (countryName != null) {
				country = ((CountryHome) getIDOHome(Country.class)).findByCountryName(countryName);
			}
			PostalCode code = null;
			if (postalCodeId != null) {
				code = ((PostalCodeHome) getIDOHome(PostalCode.class)).findByPrimaryKey(postalCodeId);
			}
			return updateUsersAddressOrCreateIfDoesNotExist(user, streetNameAndNumber, code, country, city, province,
					poBox, communeID, addressType);
		}
		catch (Exception e) {
		}
		return null;
	}

	protected Address updateUsersAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber,
			PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID,
			AddressType addressType) throws CreateException, RemoteException {
		Address address = null;
		if (streetNameAndNumber != null && user != null) {
			try {
				AddressBusiness addressBiz = getAddressBusiness();
				String streetName = addressBiz.getStreetNameFromAddressString(streetNameAndNumber);
				String streetNumber = addressBiz.getStreetNumberFromAddressString(streetNameAndNumber);
				address = getUserAddressByAddressType(((Integer) user.getPrimaryKey()).intValue(), addressType);
				boolean addAddress = false;
				/** @todo is this necessary?* */
				if (address == null) {
					AddressHome addressHome = addressBiz.getAddressHome();
					address = addressHome.create();
					address.setAddressType(addressType);
					addAddress = true;
				}
				if (country != null)
					address.setCountry(country);
				if (postalCode != null)
					address.setPostalCode(postalCode);
				if (province != null)
					address.setProvince(province);
				if (city != null)
					address.setCity(city);
				if (poBox != null)
					address.setPOBox(poBox);
				address.setStreetName(streetName);
				if (streetNumber != null)
					address.setStreetNumber(streetNumber);
				else {
					// Fix when entering unnumbered addresses (Aron )
					address.setStreetNumber("");
				}
				if (communeID != null) {
					address.setCommuneID(communeID.intValue());
				}
				address.store();
				if (addAddress) {
					user.addAddress(address);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to update or create address for userid : " + user.getPrimaryKey());
			}
		}
		else
			throw new CreateException("No streetname or user is null!");
		return address;
	}

	public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group)
			throws EJBException, RemoteException {
		User userToUpdate = this.getUser(userId);
		updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID,
				date_of_birth, primary_group, null);
	}

	public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group,
			String fullname) throws EJBException, RemoteException {
		User userToUpdate = this.getUser(userId);
		updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID,
				date_of_birth, primary_group, fullname);
	}

	public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group)
			throws EJBException, RemoteException {
		updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID,
				date_of_birth, primary_group, null);
	}

	public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname,
			String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group,
			String fullname) throws EJBException, RemoteException {
		if (firstname != null) {
			userToUpdate.setFirstName(firstname);
		}
		if (middlename != null) {
			userToUpdate.setMiddleName(middlename);
		}
		if (lastname != null) {
			userToUpdate.setLastName(lastname);
		}
		if (displayname != null) {
			userToUpdate.setDisplayName(displayname);
		}
		else if (fullname != null
				&& (userToUpdate.getDisplayName() == null || "".equals(userToUpdate.getDisplayName()))) {
			//set the display name as the full name
			userToUpdate.setDisplayName(fullname);
		}
		if (description != null) {
			userToUpdate.setDescription(description);
		}
		if (gender != null) {
			userToUpdate.setGender(gender);
		}
		if (date_of_birth != null) {
			userToUpdate.setDateOfBirth(date_of_birth.getSQLDate());
		}
		if (primary_group != null) {
			userToUpdate.setPrimaryGroupID(primary_group);
		}
		if (personalID != null) {
			userToUpdate.setPersonalID(personalID);
		}
		if (fullname != null) {
			userToUpdate.setFullName(fullname);
		}
		userToUpdate.store();
	}

	/**
	 * Gets all the Emails registered to a User with id iUserId
	 * 
	 * @param iUserId
	 *            an ID of a User
	 * @return Collection of Emails for the User or Null if no emails are found.
	 */
	public Collection listOfUserEmails(int iUserId) {
		try {
			return this.getEmailHome().findEmailsForUser(iUserId);
		}
		catch (Exception ex) {
		}
		return null;
	}

	/**
	 * Adds email to the given user, and removes older emails if requested
	 */
	public Email storeUserEmail(Integer userID, String emailAddress, boolean replaceExistentRecord) {
		return storeUserEmail(getUser(userID), emailAddress, replaceExistentRecord);
	}

	/**
	 * Adds email to the given user, and removes older emails if requested if
	 * addres is null or empty the no email vill exist any more for the user
	 * 
	 * @return null if no email was stored
	 */
	public Email storeUserEmail(User user, String emailAddress, boolean replaceExistentRecord) {
		try {
			if (replaceExistentRecord)
				removeUserEmails(user);
			if (!"".equals(emailAddress)) {
				Email emailRecord = lookupEmail(emailAddress);
				if (emailRecord == null) {
					emailRecord = this.getEmailHome().create();
					emailRecord.setEmailAddress(emailAddress);
					emailRecord.store();
				}
				user.addEmail(emailRecord);
				return emailRecord;
			}
		}
		catch (IDOStoreException e) {
			e.printStackTrace();
		}
		catch (IDOAddRelationshipException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Removes email relations to given user
	 * 
	 * @param user
	 * @return true if successfull, else false
	 */
	public boolean removeUserEmails(User user) {
		try {
			user.removeAllEmails();
			return true;
		}
		catch (IDORemoveRelationshipException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void addNewUserEmail(int iUserId, String sNewEmailAddress) {
		storeUserEmail(getUser(iUserId), sNewEmailAddress, false);
	}

	public Email lookupEmail(String EmailAddress) {
		try {
			//EntityFinder.debug = true;
			//java.util.Collection c =
			// EntityFinder.getInstance().findAllByColumn(Email.class,com.idega.core.data.EmailBMPBean.getColumnNameAddress(),EmailAddress);
			Email email = getEmailHome().findEmailByAddress(EmailAddress);
			return email;
		}
		catch (Exception ex) {
		}
		return null;
	}

	/**
	 * @deprecated use getUserGroupsDirectlyRelated(int iUserId)
	 */
	public Collection listOfUserGroups(int iUserId) {
		return getUserGroupsDirectlyRelated(iUserId);
	}

	public Collection getUserGroups(int iUserId) throws EJBException {
		try {
			return getUserGroups(this.getUser(iUserId));
			//return
			// getUserGroups(((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId).getGroupID());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets all users that are directly children of the group with id iGroupId
	 * 
	 * @return Collection of User objects.
	 * @see com.idega.user.business.UserBusiness#getUsersInGroup(Group)
	 */
	public Collection getUsersInGroup(int iGroupId) {
		try {
			//EntityFinder.findRelated(group,com.idega.user.data.UserBMPBean.getStaticInstance());
			return this.getGroupBusiness().getUsers(iGroupId);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets all users that are directly children of the group aGroup
	 * 
	 * @return Collection of User objects.
	 * @see com.idega.user.business.UserBusiness#getUsersInGroup(Group)
	 */
	public Collection getUsersInGroup(Group aGroup) {
		try {
			int groupID = ((Integer) aGroup.getPrimaryKey()).intValue();
			return getUsersInGroup(groupID);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets a collection of all users in the system.
	 * 
	 * @return Collection of User entities
	 * @see com.idega.user.business.UserBusiness#getUsers()
	 */
	public Collection getUsers() throws FinderException, RemoteException {
		//Collection l =
		// EntityFinder.findAll(com.idega.user.data.UserBMPBean.getStaticInstance());
		Collection l = this.getUserHome().findAllUsers();
		return l;
	}

	/**
	 * Returns User from userid, throws an unchecked EJBException if not found
	 * 
	 * @throws EJBException
	 *             if nothing found or an error occured
	 */
	public User getUser(int iUserId) {
		return getUser(new Integer(iUserId));
	}

	/**
	 * Returns User from userid, throws EJBException if not found
	 */
	public User getUser(Integer iUserId) {
		try {
			return getUserHome().findByPrimaryKey(iUserId);
		}
		catch (Exception ex) {
			throw new EJBException("Error getting user for id: " + iUserId.toString() + " Message: " + ex.getMessage());
		}
		//return null;
	}

	/**
	 * Returns User from personal id returns null if not found
	 */
	public User getUser(String personalID) throws FinderException {
		return getUserHome().findByPersonalID(personalID);
	}

	/**
	 * Returns User from personal id returns null if not found
	 */
	public User findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(String personalID, String first_name, String last_name) throws FinderException {
		return getUserHome().findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(personalID, first_name, last_name);
	}

	public Collection getUsersInNoGroup() throws SQLException {
		//return
		// EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean.getStaticInstance());
		//Collection nonrelated =
		// EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getStaticInstance());
		//return
		// UserGroupBusiness.getUsersForUserRepresentativeGroups(nonrelated);
		throw new java.lang.UnsupportedOperationException("method getUsersInNoGroup() not implemented");
	}

	public Collection getUserGroupsDirectlyRelated(int iUserId) {
		try {
			return getUserGroupsDirectlyRelated(this.getUser(iUserId));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Collection getUsersInPrimaryGroup(Group group) {
		try {
			//return
			// EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean._COLUMNNAME_PRIMARY_GROUP_ID,group.getID());
			return this.getUserHome().findUsersInPrimaryGroup(group);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Collection getUserGroupsDirectlyRelated(User user) {
		try {
			return getGroupBusiness().getParentGroups(user); //  EntityFinder.findRelated(user,com.idega.user.data.GroupBMPBean.getStaticInstance());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets all the groups that are indirect parents (grand parents etc.) of the
	 * user with id iUserId
	 * 
	 * @param iUserId
	 *            the ID of the user to get indirect parents for
	 * @return Collection of Group entities that are not direct parents of the
	 *         specified user
	 */
	public Collection getParentGroupsInDirectForUser(int iUserId) {
		//public Collection getUserGroupsNotDirectlyRelated(int iUserId){
		try {
			User user = this.getUser(iUserId);
			/*
			 * Collection isDirectlyRelated =
			 * getUserGroupsDirectlyRelated(user); Collection AllRelatedGroups =
			 * getUserGroups(user);
			 * 
			 * if(AllRelatedGroups != null){ if(isDirectlyRelated != null){
			 * Iterator iter = isDirectlyRelated.iterator(); while
			 * (iter.hasNext()) { Object item = iter.next();
			 * AllRelatedGroups.remove(item);
			 * //while(AllRelatedGroups.remove(item)){} } } return
			 * AllRelatedGroups; }else { return null; }
			 */
			return this.getGroupBusiness().getParentGroupsInDirect(user.getGroupID());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns all the groups that are not a direct parent of the User with id
	 * iUserId. That is both groups that are indirect parents of the user or not
	 * at all parents of the user.
	 * 
	 * @see com.idega.user.business.GroupBusiness#getAllGroupsNotDirectlyRelated(int)
	 * @return Collection of non direct parent groups
	 */
	public Collection getNonParentGroups(int iUserId) {
		try {
			User user = getUser(iUserId);
			/*
			 * Collection isDirectlyRelated =
			 * getUserGroupsDirectlyRelated(user); Collection AllGroups =
			 * UserGroupBusiness.getAllGroups();
			 * //EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getStaticInstance());
			 * 
			 * if(AllGroups != null){ if(isDirectlyRelated != null){ Iterator
			 * iter = isDirectlyRelated.iterator(); while (iter.hasNext()) {
			 * Object item = iter.next(); AllGroups.remove(item);
			 * //while(AllGroups.remove(item)){} } } return AllGroups; }else{
			 * return null; }
			 */
			return getGroupBusiness().getNonParentGroups(user.getGroupID());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets all the groups that the user is in recursively up the group tree
	 * with all availble group types.
	 * 
	 * @param aUser
	 *            a User to find parent Groups for
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *             If an error occured
	 */
	public Collection getUserGroups(User aUser) throws EJBException {
		//String[] groupTypesToReturn = new String[2];
		//groupTypesToReturn[0] =
		// com.idega.user.data.GroupBMPBean.getStaticInstance().getGroupTypeValue();
		//groupTypesToReturn[1] =
		// com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
		return getUserGroups(aUser, null, false);
	}

	/**
	 * Gets all the groups that the user is in recursively up the group tree
	 * filtered with specified groupTypes
	 * 
	 * @param aUser
	 *            a User to find parent Groups for
	 * @param groupTypes
	 *            the Groups a String array of group types of which the Groups
	 *            to be returned must be = *
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *             If an error occured
	 */
	public Collection getUserGroups(User aUser, String[] groupTypes) throws EJBException {
		return getUserGroups(aUser, groupTypes, false);
	}

	/**
	 * Returns recursively up the group tree parents of User aUser with filtered
	 * out with specified groupTypes
	 * 
	 * @param aUser
	 *            a User to find parent Groups for
	 * @param groupTypes
	 *            the Groups a String array of group types to be filtered with
	 * @param returnSpecifiedGroupTypes
	 *            if true it returns the Collection with all the groups that are
	 *            of the types specified in groupTypes[], else it returns the
	 *            opposite (all the groups that are not of any of the types
	 *            specified by groupTypes[])
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *             If an error occured
	 */
	public Collection getUserGroups(User aUser, String[] groupTypes, boolean returnSepcifiedGroupTypes)
			throws EJBException {
		try {
			return getGroupBusiness().getParentGroupsRecursive(aUser.getGroup(), groupTypes, returnSepcifiedGroupTypes);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	public GroupBusiness getGroupBusiness() throws RemoteException {
		return (GroupBusiness) IBOLookup.getServiceInstance(this.getIWApplicationContext(), GroupBusiness.class);
	}

	public Collection getAllUsersOrderedByFirstName() throws FinderException, RemoteException {
		return this.getUserHome().findAllUsersOrderedByFirstName();
	}

	public Email getUsersMainEmail(User user) throws NoEmailFoundException {
		String userString = null;
		try {
			userString = user.getName();
			Collection collection = user.getEmails();
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				Email element = (Email) iterator.next();
				return element;
			}
		}
		catch (Exception e) {
		}
		throw new NoEmailFoundException(userString);
	}

	public Phone getUsersHomePhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersHomePhone(user);
		}
		catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersWorkPhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersWorkPhone(user);
		}
		catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersMobilePhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersMobilePhone(user);
		}
		catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersFaxPhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersFaxPhone(user);
		}
		catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	/**
	 * @return Correct name of the group or user or empty string if there was an
	 *         error getting the name. Gets the name of the group and explicitly
	 *         checks if the "groupOrUser" and if it is a user it returns the
	 *         correct name of the user. Else it regularely returns the name of
	 *         the group.
	 */
	public String getNameOfGroupOrUser(Group groupOrUser) {
		try {
			String userGroupType = getUserHome().getGroupType();
			if (groupOrUser.getGroupType().equals(userGroupType)) {
				int userID = ((Integer) groupOrUser.getPrimaryKey()).intValue();
				return getUser(userID).getName();
			}
			else {
				return groupOrUser.getName();
			}
		}
		catch (Exception e) {
			return "";
		}
	}

	public UserProperties getUserProperties(User user) throws RemoteException {
		return getUserProperties(((Integer) user.getPrimaryKey()).intValue());
	}

	public UserProperties getUserProperties(int userID) {
		UserProperties properties = new UserProperties(getIWApplicationContext().getIWMainApplication(), userID);
		return properties;
	}

	/**
	 * @return the id of the homepage for the user if it is set, else -1 Finds
	 *         the homepage set for the user, if none is set it checks on the
	 *         homepage set for the users primary group, else it returns -1
	 */
	public int getHomePageIDForUser(User user) {
		try {
			int homeID = user.getHomePageID();
			if (homeID == -1) {
				homeID = user.getPrimaryGroup().getHomePageID();
				return homeID;
			}
			else {
				return homeID;
			}
		}
		catch (Exception e) {
			return -1;
		}
	}

	/**
	 * @return the id of the homepage for the user if it is set, else it throws
	 *         a javax.ejb.FinderException Finds the homepage set for the user,
	 *         if none is set it checks on the homepage set for the users
	 *         primary group, else it throws a javax.ejb.FinderException
	 */
	public com.idega.core.builder.data.ICPage getHomePageForUser(User user) throws javax.ejb.FinderException {
		try {
			int homeID = getHomePageIDForUser(user);
			if (homeID != -1) {
				return getIBPageHome().findByPrimaryKey(homeID);
			}
			else {
				throw new javax.ejb.FinderException("No homepage found for user");
			}
		}
		catch (Exception e) {
			throw new javax.ejb.FinderException("Error finding homepage for user. Error was:" + e.getClass().getName()
					+ " with message: " + e.getMessage());
		}
	}

	protected ICPageHome getIBPageHome() throws java.rmi.RemoteException {
		return (ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class);
	}

	public AddressBusiness getAddressBusiness() throws RemoteException {
		return (AddressBusiness) getServiceInstance(AddressBusiness.class);
	}

	/**
	 * Cast a Group that is a "UserReresentative" Group to a User instance.
	 * 
	 * @param userGroups
	 *            An instance of a Group that is really a "UserReresentative"
	 *            group i.e. the Group representation of the User
	 * @param userGroup
	 *            A instnance of a Group that is really a "UserReresentative"
	 *            group i.e. the Group representation of the User
	 * @return User
	 * @throws EJBException
	 *             If an error occurs casting
	 */
	public User castUserGroupToUser(Group userGroup) throws EJBException {
		try {
			if (userGroup instanceof User) {
				return (User) userGroup;
			}
			else {
				//try{
				return this.getUserHome().findUserForUserGroup(userGroup);
				//}
				//catch(FinderException e){
				//if(userGroup.isUser()){
				//	return
				// this.getUserHome().findByPrimaryKey(userGroup.getPrimaryKey());
				//}
				//}
			}
		}
		catch (Exception e) {
			throw new IBORuntimeException(e);
		}
		//throw new IBORuntimeException("Error find user for group
		// "+userGroup.toString());
	}

	public boolean hasUserLogin(User user) throws RemoteException {
		LoginTable lt = LoginDBHandler.getUserLogin(((Integer) user.getPrimaryKey()).intValue());
		return lt != null;
	}

	public boolean hasUserLogin(int userID) throws RemoteException {
		LoginTable lt = LoginDBHandler.getUserLogin(userID);
		return lt != null;
	}

	public Group getUsersHighestTopGroupNode(User user, List groupTypes, IWUserContext iwuc) throws RemoteException {
		Map groupTypeGroup = new HashMap();
		Collection topNodes = getUsersTopGroupNodesByViewAndOwnerPermissions(user, iwuc);
		Iterator iterator = topNodes.iterator();
		while (iterator.hasNext()) {
			Group group = (Group) iterator.next();
			String groupType = group.getGroupType();
			groupTypeGroup.put(groupType, group);
		}
		Iterator typeIterator = groupTypes.iterator();
		while (typeIterator.hasNext()) {
			String groupType = (String) typeIterator.next();
			if (groupTypeGroup.containsKey(groupType)) {
				return (Group) groupTypeGroup.get(groupType);
			}
		}
		return null;
	}

	/**
	 * Checks if a group is under a user's top group node. This can be used to
	 * check if the user is allowed to view the group.
	 * 
	 * @param iwc
	 *            IWUserContext
	 * @param group
	 *            The group to check
	 * @param user
	 *            The user to check the group for
	 * @return returns true if any of <code>user</code> s top group nodes is
	 *         an ancestor of <code>group</code>, false otherwise.
	 */
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user) throws RemoteException {
		Collection topGroupNodes = null;
		try {
			topGroupNodes = getUsersTopGroupNodesByViewAndOwnerPermissions(user, iwc);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		if (topGroupNodes == null || topGroupNodes.isEmpty()) {
			return false;
		}
		else {
			//System.out.println("Checking if group " + group.getName() + " is
			// under a top group (" + topGroupNodes.size() + ") for user " +
			// user.getName());
			return isGroupUnderUsersTopGroupNode(iwc, group, user, topGroupNodes);
		}
	}

	/**
	 * Helper method for
	 * {@link #isGroupUnderUsersTopGroupNode(IWUserContext, Group, User)}.
	 * 
	 * @see #isGroupUnderUsersTopGroupNode(IWUserContext, Group, User)
	 */
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user, Collection topGroupNodes) {
		boolean found = false; // whether ancestry with a top group node is
		// found or not
		if (group != null && topGroupNodes.contains(group)) {
			//System.out.println("found top group ancestor " +
			// group.getName());
			found = true;
		}
		else {
			Iterator parents = group.getParentGroups().iterator();
			while (parents.hasNext() && !found) {
				Group parent = (Group) parents.next();
				//System.out.println("checking group for top group in ancestors
				// " + parent.getName());
				try {
					found = isGroupUnderUsersTopGroupNode(iwc, parent, user, topGroupNodes);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				if (found) {
					break;
				}
			}
		}
		return found;
	}

	private Collection searchForTopNodes(Collection permissions, Collection initialTopNodes, User user)
			throws EJBException, RemoteException, FinderException {
		if (permissions == null || permissions.isEmpty()) {
			return ListUtil.getEmptyList();
		}
		NestedSetsContainer groupTree = getGroupBusiness().getLastGroupTreeSnapShot();
		Map topNodesSubTrees = new HashMap();
		Iterator iter = permissions.iterator();
		if (initialTopNodes == null || initialTopNodes.isEmpty()) {
			String groupPK = ((ICPermission) iter.next()).getContextValue(); //this
			// group
			// is a
			// topnode
			// until
			// we
			// find
			// topnode
			// that
			// is
			// parent
			// of
			// it
			topNodesSubTrees.put(groupPK, groupTree.subSet(groupPK)); //get the
			// first
			// topnode's
			// subtree
		}
		else {
			for (Iterator iterator = initialTopNodes.iterator(); iterator.hasNext();) {
				String groupPK = String.valueOf(((Group) iterator.next()).getPrimaryKey());
				topNodesSubTrees.put(groupPK, groupTree.subSet(groupPK));
			}
		}
		Set topNodeSubTreesEntrySet = topNodesSubTrees.entrySet();
		while (iter.hasNext()) {
			ICPermission perm = (ICPermission) iter.next();
			if (!perm.getPermissionValue()) {
				continue;//we don't want to use this permission if is a
				// negative permission (a NOT permission)
			}
			String sGroup = perm.getContextValue();
			boolean isChild = false;
			for (Iterator iterator = topNodeSubTreesEntrySet.iterator(); iterator.hasNext();) {
				Set subTree = (Set) ((Map.Entry) iterator.next()).getValue();
				if (subTree.contains(sGroup)) {
					isChild = true;
					break;
				}
			}
			if (!isChild) {
				if (groupTree.contains(sGroup)) {
					Set newSubset = groupTree.subSet(sGroup);
					for (Iterator keyIter = topNodesSubTrees.keySet().iterator(); keyIter.hasNext();) {
						String topNode = (String) keyIter.next();
						if (newSubset.contains(topNode)) { // if the new topnode
							// is parent of
							// another "topnode"
							// then that node is
							// not topnode and is
							// removed
							keyIter.remove();
						}
					}
					topNodesSubTrees.put(sGroup, newSubset);
					topNodeSubTreesEntrySet = topNodesSubTrees.entrySet();
				}
				else {
					log("[UserBusinessBean]: Topnode " + sGroup
							+ " dose not exist in the tree but is set in the permissions for the user " + user);
				}
			}
		}
		List topNodes = new ArrayList();
		String[] groupPKs = (String[]) topNodesSubTrees.keySet().toArray(new String[topNodesSubTrees.size()]);
		topNodes.addAll(getGroupHome().findByPrimaryKeyCollection(getGroupHome().decode(groupPKs)));
		for (Iterator iterator = topNodes.iterator(); iterator.hasNext();) {
			Group group = (Group) iterator.next();
			if (group.isAlias() && topNodes.contains(group.getAlias())) {
				iterator.remove();
			}
			else if (group.isAlias()) {
				Set subTree = groupTree.subSet(String.valueOf(group.getAliasID()));
				for (Iterator topIter = topNodes.iterator(); topIter.hasNext();) {
					String topNode = String.valueOf(((Group) topIter.next()).getPrimaryKey());
					if (subTree.contains(topNode)) { // if the new topnode is
						// parent of another
						// "topnode" then that node
						// is not topnode and is
						// removed
						topIter.remove();
					}
				}
			}
		}
		return topNodes;
	}

	private Collection searchForTopNodesFromTheTop(Collection possibleGroups) throws IDORelationshipException,
			RemoteException, FinderException {
		Collection domainTopNodes = this.getIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
		Collection topNodes = new ArrayList();
		HashSet prosessedGroups = new HashSet();
		collectTopNodesForSubtree(domainTopNodes, possibleGroups, topNodes, prosessedGroups);
		return topNodes;
	}

	/**
	 * 
	 * @param childGroups
	 * @param possibleGroups
	 *            collection of prosessed groups to prevent endless recusion
	 * @param topNodes
	 * @param prosessedGroups
	 */
	private void collectTopNodesForSubtree(Collection childGroups, Collection possibleGroups, Collection topNodes,
			Set prosessedGroups) {
		for (Iterator iter = childGroups.iterator(); iter.hasNext();) {
			Group gr = (Group) iter.next();
			if (!prosessedGroups.contains(gr)) {
				prosessedGroups.add(gr);
				if (possibleGroups.contains(gr)) {
					topNodes.add(gr);
					continue;
				}
				else {
					collectTopNodesForSubtree(gr.getChildGroups(), possibleGroups, topNodes, prosessedGroups);
				}
			}
		}
	}

	public boolean hasTopNodes(User user, IWUserContext iwuc) {
		try {
			//super admin check is done first
			boolean isSuperUser = iwuc.isSuperAdmin();
			if((isSuperUser && user == null) || (isSuperUser && iwuc.getCurrentUser().equals(user))) {
				return true;
			}
			else{
				Collection topNodes = getUsersTopGroupNodesByViewAndOwnerPermissions(user, iwuc);
				return !topNodes.isEmpty();
			}
		}
		catch (RemoteException re) {
			return false;
		}
	}

	/**
	 * Returns a collection of Groups that are this users top nodes. The nodes
	 * that he has either view or owner permissions to <br>
	 * To end up with only the top nodes we do the following: <br>
	 * For each group (key) in the parents Map we check if that group is
	 * contained within any of <br>
	 * the other groups' parents. If another group has this group as a parent it
	 * is removed and its parent list <br>
	 * and we move on to the next key. This way the map we iterate through will
	 * always get smaller until only the <br>
	 * top node groups are left.
	 * 
	 * Finally we check for the special case that the remaining top nodes have a
	 * shortcut that is not a top node <br>
	 * and if so we need to remove that node unless there is only one node left
	 * or if the alias and the real group <br>
	 * are both top nodes.
	 * 
	 * @param user
	 * @return @throws
	 *         RemoteException
	 */
	public Collection getUsersTopGroupNodesByViewAndOwnerPermissions(User user, IWUserContext iwuc)
			throws RemoteException {
		Collection topNodes = new ArrayList();
		//check for the super user case first
		boolean isSuperUser = iwuc.isSuperAdmin();
		if ((isSuperUser && user == null) || (isSuperUser && iwuc.getCurrentUser().equals(user))) {
			try {
				topNodes = this.getIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
				return topNodes;
			}
			catch (Exception e1) {
				topNodes = new Vector();
				e1.printStackTrace();
			}
		}
		if (user != null) {
			topNodes = (Collection) iwuc.getSessionAttribute(SESSION_KEY_TOP_NODES + user.getPrimaryKey().toString());
			if (topNodes != null && !topNodes.isEmpty()) {
				return topNodes;
			}
			topNodes = getStoredTopNodeGroups(user);
			if (topNodes != null && !topNodes.isEmpty()) {
				iwuc.setSessionAttribute(SESSION_KEY_TOP_NODES + user.getPrimaryKey().toString(), topNodes);
				return topNodes;
			}
			else {
				log("[UserBusinessBean]: getUsersTopGroupNodesByViewAndOwnerPermissions(...) begins");
				Timer totalTime = new Timer();
				totalTime.start();
				Collection allViewAndOwnerPermissionGroups = new ArrayList();
				try {
					GroupBusiness groupBiz = getGroupBusiness();
					if (false) {//(groupBiz.userGroupTreeImageProcedureTopNodeSearch())
								// {
						log("[UserBusinessBean]: using stored procedure topnode search");
						Timer time = new Timer();
						time.start();
						Collection directlyRelatedParents = getGroupBusiness().getParentGroups(user);
						Iterator iterating = directlyRelatedParents.iterator();
						List additionalGroups = new ArrayList();
						while (iterating.hasNext()) {
							Group parent = (Group) iterating.next();
							if (parent != null && parent.getPermissionControllingGroupID() > 0) {
								additionalGroups.add(parent.getPermissionControllingGroup());
							}
						}
						directlyRelatedParents.addAll(additionalGroups);
						Collection allPermissions = new ArrayList();
						//get all view permissions for direct parent and put in
						// a list
						Collection viewPermissions = AccessControl.getAllGroupViewPermissions(directlyRelatedParents);
						allPermissions.addAll(viewPermissions);
						Collection ownedPermissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
						allPermissions.addAll(ownedPermissions);
						time.stop();
						log("[UserBusinessBean]: fetching complete " + time.getTimeString());
						time.start();
						try {
							topNodes = searchForTopNodes(allPermissions, null, user);
							//							topNodes =
							// searchForTopNodes(viewPermissions,searchForTopNodes(ownedPermissions,null));
							for (Iterator iter = topNodes.iterator(); iter.hasNext();) {
								Group gr = (Group) iter.next();
								if (gr.isAlias() && topNodes.contains(gr.getAlias())) {
									iter.remove();
								}
							}
							time.stop();
							log("[UserBusinessBean]: searchForTopNodesFromTheTop complete " + time.getTimeString());
						}
						catch (RemoteException e1) {
							throw new EJBException(e1);
						}
						catch (FinderException e1) {
							throw new EJBException(e1);
						}
					}
					else {
						log("[UserBusinessBean]: not using stored procedure topnode search");
						Timer time = new Timer();
						time.start();
						Map parents = new HashMap();
						Map groupMap = new HashMap();//we need it to be
						// synchronized so we can
						// remove items while in a
						// iterator
						Map aliasMap = new HashMap();
						IDOUtil idoUtil = IDOUtil.getInstance();
						GroupHome grHome = getGroupHome();
						Collection directlyRelatedParents = getGroupBusiness().getParentGroups(user);
						Iterator iterating = directlyRelatedParents.iterator();
						List additionalGroups = new ArrayList();
						while (iterating.hasNext()) {
							Group parent = (Group) iterating.next();
							if (parent != null && parent.getPermissionControllingGroupID() > 0) {
								additionalGroups.add(parent.getPermissionControllingGroup());
							}
						}
						directlyRelatedParents.addAll(additionalGroups);
						Collection allViewAndOwnerPermissionGroupPKs = new ArrayList();
						//get all view permissions for direct parent and put in
						// a list
						Collection viewPermissions = AccessControl.getAllGroupViewPermissions(directlyRelatedParents);
						addGroupPKsToCollectionFromICPermissionCollection(viewPermissions,
								allViewAndOwnerPermissionGroupPKs);
						Collection ownedPermissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
						//allViewAndOwnerPermissions.removeAll(ownedPermissions);//no
						// double entries thank you
						addGroupPKsToCollectionFromICPermissionCollection(ownedPermissions,
								allViewAndOwnerPermissionGroupPKs);
						try {
							allViewAndOwnerPermissionGroups = grHome.findByPrimaryKeyCollection(allViewAndOwnerPermissionGroupPKs);
						}
						catch (FinderException e) {
							log("UserBusiness: In getUsersTopGroupNodesByViewAndOwnerPermissions. groups not found");
							e.printStackTrace();
						}
						time.stop();
						log("[UserBusinessBean]: getting permission groups complete " + time.getTimeString());
						time.start();
						//searchForTopNodesFromTop=3000; //some suitable value
						if (false) {//(allViewAndOwnerPermissionGroups.size() >
									// this.searchForTopNodesFromTop) {
							log("[UserBusinessBean]: using search from the top");
							try {
								topNodes = searchForTopNodesFromTheTop(allViewAndOwnerPermissionGroups);
								for (Iterator iter = topNodes.iterator(); iter.hasNext();) {
									Group gr = (Group) iter.next();
									if (gr.isAlias() && topNodes.contains(gr.getAlias())) {
										iter.remove();
									}
								}
								time.stop();
								log("[UserBusinessBean]: searchForTopNodesFromTheTop complete " + time.getTimeString());
								time.start();
							}
							catch (IDORelationshipException e1) {
								throw new EJBException(e1);
							}
							catch (RemoteException e1) {
								throw new EJBException(e1);
							}
							catch (FinderException e1) {
								throw new EJBException(e1);
							}
						}
						else {
							log("[UserBusinessBean]: using old topnode search");
							//get all (recursively) parents for permission
							Iterator permissions = allViewAndOwnerPermissionGroups.iterator();
							Map cachedParents = new HashMap();
							Map cachedGroups = new HashMap();
							while (permissions.hasNext()) {
								Group group = (Group) permissions.next();
								Integer primaryKey = (Integer) group.getPrimaryKey();
								if (!groupMap.containsKey(primaryKey)) {
									Group permissionGroup = group;
									if (!cachedGroups.containsKey(primaryKey.toString())) {
										cachedGroups.put(primaryKey.toString(), permissionGroup);
									}
									Collection recParents = groupBiz.getParentGroupsRecursive(permissionGroup,
											cachedParents, cachedGroups);
									Map parentMap = idoUtil.convertIDOEntityCollectionToMapOfPrimaryKeysAndEntityValues(recParents);
									parents.put(primaryKey, parentMap);
									groupMap.put(primaryKey, permissionGroup);
									//if it's an alias we don't need the
									// original group and make a list of those
									// groups to filter out later
									if (permissionGroup.isAlias()) {
										Integer originalGroupID = new Integer(permissionGroup.getAliasID());
										aliasMap.put(originalGroupID, primaryKey);
									}
								}
							}
							time.stop();
							log("[UserBusinessBean]: getting all parents (recursively) complete "
									+ time.getTimeString());
							time.start();
							//Filter out the real top nodes!
							Map skipThese = new HashMap();
							Set keys = parents.keySet();
							Iterator iter = keys.iterator();
							while (iter.hasNext()) {
								Integer thePermissionGroupsId = (Integer) iter.next();
								Iterator iter2 = parents.keySet().iterator();
								while (iter2.hasNext()) {
									Integer groupToCompareTo = (Integer) iter2.next();
									//If this group was already checked or is
									// the same as the comparing group, continue
									// (skip this one)
									if (thePermissionGroupsId.equals(groupToCompareTo)
											|| skipThese.containsKey(thePermissionGroupsId)) {
										continue;//dont check for self
									}
									//Get the parents to see if
									// thePermissionGroupsId is in it. ergo it
									// is a parent of the comparing group and
									// therefor a higher node
									Map theParents = (Map) (parents.get(groupToCompareTo));
									//or the permissiongroup has a shortcut
									if (theParents != null && theParents.containsKey(thePermissionGroupsId)) {
										//it's a parent of the comparing group
										// so we don't have to check the
										// comparing group again
										skipThese.put(groupToCompareTo, null);//for
										// the
										// check
										// skip
										// check
										groupMap.remove(groupToCompareTo);//the
										// groups
										// that
										// will
										// be
										// left
										// are
										// the
										// top
										// nodes
									}//remove if this group is a child group of
									// myGroup
								}//inner while ends
							}//outer while ends
							time.stop();
							log("[UserBusinessBean]: filter out the real topnodes complete " + time.getTimeString());
							time.start();
							//Now we have to check if the remaining top nodes
							// have a shortcut
							//that is not a top node and if so we need to
							// remove that node
							//unless there is only one node left or if the
							// alias and the real group are both top nodes
							if (groupMap != null && !groupMap.isEmpty()) {
								List aliasGroupType = new ArrayList();
								aliasGroupType.add("alias");
								if (!aliasMap.isEmpty()) {
									Iterator keyIter = groupMap.keySet().iterator();
									while (keyIter.hasNext()) {
										Integer topNodeId = (Integer) keyIter.next();
										Integer aliasGroupsId = (Integer) aliasMap.get(topNodeId);
										if (aliasGroupsId != null) {
											if (!groupMap.containsKey(aliasGroupsId)) {//only
												// remove
												// if
												// they
												// are
												// not
												// both
												// top
												// nodes
												//												groupMap.remove(topNodeId);
												System.err.println("Here is the code that once returned concurrentException");
											}
										}
									}
								}
								time.stop();
								log("[UserBusinessBean]: some alias complete " + time.getTimeString());
								time.start();
								//check the children recursively
								List groupsToRemove = new ArrayList();
								Iterator keyIter = groupMap.keySet().iterator();
								while (keyIter.hasNext()) {
									Integer topNodeId = (Integer) keyIter.next();
									if (skipThese.containsKey(topNodeId)) {
										continue;//it's going to be removed
										// later
									}
									else {
										try {
											//also we need to check the
											// children of the current top nodes
											// recursively for aliases :s
											Collection aliasesRecursive = getGroupBusiness().getChildGroupsRecursiveResultFiltered(
													getGroupBusiness().getGroupByGroupID(topNodeId.intValue()),
													aliasGroupType, true);
											if (aliasesRecursive != null && !aliasesRecursive.isEmpty()) {
												Iterator aliasIter = aliasesRecursive.iterator();
												while (aliasIter.hasNext()) {
													Group alias = (Group) aliasIter.next();
													Integer aliasGroupsId = new Integer(alias.getAliasID());
													if (groupMap.containsKey(aliasGroupsId)) {//only
														// remove
														// if
														// they
														// are
														// not
														// both
														// top
														// nodes
														groupsToRemove.add(aliasGroupsId);
														skipThese.put(aliasGroupsId, null);
													}
												}
											}
										}
										catch (FinderException e1) {
											e1.printStackTrace();
										}
									}
								}
								time.stop();
								log("[UserBusinessBean]: check children (recursively) complete " + time.getTimeString());
								time.start();
								//remove the top nodes that have aliases under
								// another top node, or itself to avoid crashing
								// the server in an endless loop?
								Iterator removeIter = groupsToRemove.iterator();
								while (removeIter.hasNext()) {
									groupMap.remove(removeIter.next());
								}
								time.stop();
								log("[UserBusinessBean]: remove the aliases undr another top node complete "
										+ time.getTimeString());
							}
							//finally done! the remaining nodes are the top
							// nodes
							topNodes = groupMap.values();
						}
					}
				}
				catch (EJBException e) {
					e.printStackTrace();
				}
				totalTime.stop();
				log("[UserBusinessBean]: topnode....(...) ends " + totalTime.getTimeString());
				int numberOfPermissions = allViewAndOwnerPermissionGroups.size();
				if (numberOfPermissions > NUMBER_OF_PERMISSIONS_CACHING_LIMIT) {
					storeUserTopGroupNodes(user, topNodes, numberOfPermissions, totalTime.getTimeString(), null);
				}
			}
			iwuc.setSessionAttribute(SESSION_KEY_TOP_NODES + user.getPrimaryKey().toString(), topNodes);
		}
		return topNodes;
	}

	public void addGroupPKsToCollectionFromICPermissionCollection(Collection ICPermissionSRC, Collection GroupDEST) {
		GroupHome grHome = getGroupHome();
		for (Iterator iter = ICPermissionSRC.iterator(); iter.hasNext();) {
			ICPermission perm = (ICPermission) iter.next();
			if (!perm.getPermissionValue()) {
				continue;//we don't want to use this permission if is a
				// negative permission (a NOT permission)
			}
			try {
				String groupId = perm.getContextValue();
				Object grPK = grHome.decode(groupId);
				GroupDEST.add(grPK);
			}
			catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
		}
	}

	public Collection getStoredTopNodeGroups(User user) {
		try {
			return getTopNodeGroupHome().getTopNodeGroups(user);
		}
		catch (IDORelationshipException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection getStoredTopGroupNodes(User user) {
		try {
			return getTopNodeGroupHome().findByUser(user);
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removeStoredTopGroupNodes(User user) throws RemoveException {
		Collection oldNodes = getStoredTopGroupNodes(user);
		if (oldNodes != null && !oldNodes.isEmpty()) {
			for (Iterator iter = oldNodes.iterator(); iter.hasNext();) {
				TopNodeGroup topnode = (TopNodeGroup) iter.next();
				topnode.remove();
			}
		}
	}

	/**
	 * Stores the given group top nodes to the user, by first removing all
	 * previously stored top nodes from the user
	 * 
	 * @param user
	 * @param nodeGroupIds
	 * @param comment
	 */
	public boolean storeUserTopGroupNodes(User user, Collection nodeGroups, int numberOfPermissions, String totalLoginTime, String comment) {
		javax.transaction.TransactionManager transactionManager = com.idega.transaction.IdegaTransactionManager.getInstance();
		try {
			transactionManager.begin();
			removeStoredTopGroupNodes(user);
			Integer userID = (Integer) user.getPrimaryKey();
			for (Iterator iter = nodeGroups.iterator(); iter.hasNext();) {
				Integer groupID = (Integer) ((Group) iter.next()).getPrimaryKey();
				TopNodeGroup topNode = getTopNodeGroupHome().create(userID, groupID);
				if (comment != null)
					topNode.setComment(comment);
				topNode.setLastChanged(IWTimestamp.getTimestampRightNow());
				topNode.setNumberOfPermissions(new Integer(numberOfPermissions));
				topNode.setLoginDuration(totalLoginTime);
				topNode.store();
			}
			transactionManager.commit();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			try {
				transactionManager.rollback();
			}
			catch (javax.transaction.SystemException sy) {
				sy.printStackTrace(System.err);
			}
			// this is an unusual error therefore localization is it not
			// necessary
		}
		return false;
	}

	/**
	 * Returns a collection of Groups. The groups that he has edit permissions
	 * to <br>
	 * 
	 * @param user
	 * @return @throws
	 *         RemoteException
	 */
	public Collection getAllGroupsWithEditPermission(User user, IWUserContext iwuc) {
		Collection resultGroups = new TreeSet(); // important to use Set so
		// there will not be any
		// doubles
		GroupHome grHome = getGroupHome();
		//	GroupBusiness groupBiz = null;
		//	try {
		//	  groupBiz = getGroupBusiness();
		//	}
		//	catch (RemoteException ex) {
		//	  throw new RuntimeException(ex.getMessage());
		//	}
		Collection permissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
		List parentGroupsList = user.getParentGroups();
		Collection editPermissions = AccessControl.getAllGroupEditPermissions(parentGroupsList);
		// permissions.removeAll(editPermissions); // avoid double entries
		//      permissions.addAll(editPermissions);
		Collection allPermissions = new ArrayList();
		allPermissions.addAll(permissions);
		allPermissions.addAll(editPermissions);
		Iterator iterator = allPermissions.iterator();
		while (iterator.hasNext()) {
			ICPermission perm = (ICPermission) iterator.next();
			try {
				String groupId = perm.getContextValue();
				//				Group group =
				// groupBiz.getGroupByGroupID(Integer.parseInt(groupId));
				//				resultGroups.add(group);
				Object grPK = grHome.decode(groupId);
				resultGroups.add(grPK);
			}
			catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			//			catch (FinderException e1) {
			//				System.out.println("UserBusiness: In
			// getAllGroupsWithEditPermission. group not
			// found"+perm.getContextValue());
			//			}
			//			catch (RemoteException ex) {
			//				throw new RuntimeException(ex.getMessage());
			//			}
		}
		try {
			return grHome.findByPrimaryKeyCollection(resultGroups);
		}
		catch (FinderException e) {
			System.out.println("UserBusiness: In getAllGroupsWithEditPermission. groups not found");
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
	}

	/**
	 * Returns a collection of Groups. The groups that he has view permissions
	 * to <br>
	 * 
	 * @param user
	 * @return @throws
	 *         RemoteException
	 */
	public Collection getAllGroupsWithViewPermission(User user, IWUserContext iwuc) {
		Collection resultGroups = new TreeSet(); // important to use Set so
		// there will not be any
		// doubles
		//Group userGroup = null;
		//		GroupBusiness groupBiz = null;
		GroupHome grHome = getGroupHome();
		//		try {
		//			groupBiz = getGroupBusiness();
		//		}
		//		catch (RemoteException ex) {
		//			throw new RuntimeException(ex.getMessage());
		//		}
		Collection permissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
		List parentGroupsList = user.getParentGroups();
		Collection viewPermissions = AccessControl.getAllGroupViewPermissions(parentGroupsList);
		// permissions.removeAll(editPermissions); // avoid double entries
		// permissions.addAll(viewPermissions); //Sigtryggur: this caused an
		// error because both of the collection are now prefetched and are
		// therefore IDOPrimaryKeyLists, not normal collections
		Collection allPermissions = new ArrayList();
		allPermissions.addAll(permissions);
		allPermissions.addAll(viewPermissions);
		//		Collection allPermissions = null;
		//		try {
		//			allPermissions = IDOEntityList.merge(permissions,viewPermissions);
		//		} catch (FinderException e) {
		//			System.out.println("UserBusiness: In getAllGroupsWithEditPermission.
		// merge failed");
		//			e.printStackTrace();
		//			return ListUtil.getEmptyList();
		//		}
		Iterator iterator = allPermissions.iterator();
		while (iterator.hasNext()) {
			ICPermission perm = (ICPermission) iterator.next();
			try {
				String groupId = perm.getContextValue();
				//				Group group =
				// groupBiz.getGroupByGroupID(Integer.parseInt(groupId));
				//				resultGroups.add(group);
				Object grPK = grHome.decode(groupId);
				resultGroups.add(grPK);
			}
			catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			//			catch (FinderException e1) {
			//				System.out.println("UserBusiness: In
			// getAllGroupsWithEditPermission. group not
			// found"+perm.getContextValue());
			//			}
			//			catch (RemoteException ex) {
			//				throw new RuntimeException(ex.getMessage());
			//			}
		}
		try {
			return grHome.findByPrimaryKeyCollection(resultGroups);
		}
		catch (FinderException e) {
			System.out.println("UserBusiness: In getAllGroupsWithEditPermission. groups not found");
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
	}

	public Map moveUsers(IWUserContext iwuc, Collection userIds, Group parentGroup, int targetGroupId) {
		IWMainApplication application = getIWApplicationContext().getIWMainApplication();
		IWBundle bundle = application.getBundle("com.idega.user");
		Locale locale = application.getSettings().getDefaultLocale();
		IWResourceBundle iwrb = bundle.getResourceBundle(locale);
		Map result = new HashMap();
		// check if the source and the target are the same
		if (parentGroup != null) {
			int parentGroupId = ((Integer) parentGroup.getPrimaryKey()).intValue();
			// target and source are the same do nothing
			if (parentGroupId == targetGroupId) {
				String message = iwrb.getLocalizedString("user_source_and_target_are_the_same",
						"Source group and target group are the same");
				// fill the result map
				Iterator iterator = userIds.iterator();
				while (iterator.hasNext()) {
					String userIdAsString = (String) iterator.next();
					Integer userId = new Integer(userIdAsString);
					result.put(userId, message);
				}
				return result;
			}
		}
		GroupBusiness groupBiz = null;
		Group targetGroup = null;
		try {
			groupBiz = getGroupBusiness();
			targetGroup = groupBiz.getGroupByGroupID(targetGroupId);
			//check if we have editpermissions for the targetgroup
			if (!getAccessController().hasEditPermissionFor(targetGroup, iwuc)) {
				//       fill the result map
				Iterator iterator = userIds.iterator();
				while (iterator.hasNext()) {
					String userIdAsString = (String) iterator.next();
					Integer userId = new Integer(userIdAsString);
					result.put(userId, iwrb.getLocalizedString("no_edit_permission_for_target_group",
							"You do not have edit permission for the target group"));
				}
				return result;
			}
		}
		catch (FinderException ex) {
			throw new EJBException("Error getting group for id: " + targetGroupId + " Message: " + ex.getMessage());
		}
		catch (RemoteException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		String userIsAlreadyAMemberOfTheGroupMessage = iwrb.getLocalizedString(
				"user_already_member_of_the_target_group", "The user is already a member of the target group");
		// finally perform moving
		Iterator iterator = userIds.iterator();
		while (iterator.hasNext()) {
			String message;
			String userIdAsString = (String) iterator.next();
			Integer userId = new Integer(userIdAsString);
			User user = getUser(userId);
			// first check
			if (isMemberOfGroup(targetGroupId, user)) {
				message = userIsAlreadyAMemberOfTheGroupMessage;
			}
			// second check
			else {
				message = isUserSuitedForGroup(user, targetGroup);
			}
			// if there aren't any problems the message is null
			if (message == null) {
				message = moveUserWithoutTest(user, parentGroup, targetGroup, iwuc.getCurrentUser());
			}
			// if the user was sucessfully moved the message is null
			result.put(userId, message);
		}
		return result;
	}

	public Map moveUsers(IWUserContext iwuc, Collection groups, Collection groupTypesToMoveAmong) {
		IWMainApplication application = getIWApplicationContext().getIWMainApplication();
		IWBundle bundle = application.getBundle("com.idega.user");
		Locale locale = application.getSettings().getDefaultLocale();
		IWResourceBundle iwrb = bundle.getResourceBundle(locale);
		String noSuitableGroupMessage = iwrb.getLocalizedString("user_suitable_group_could_not_be_found",
				"A suitable group for the user could not be found.");
		String moreThanOneSuitableGroupMessage = iwrb.getLocalizedString(
				"user_more_than_one_suitable_group_was_found_prefix",
				"More than one suitable groups where found. The system could not decide where to put the user. The possible groups are: ");
		// key groups id, value group
		Map groupIdGroup = new HashMap();
		// key group id, value users
		Map groupIdUsers = new HashMap();
		// key group id, value id of users
		Map groupIdUsersId = new HashMap();
		// key user id, value user's parent group
		Map userParentGroup = new HashMap();
		// key user id, value user's target group
		Map userTargetGroup = new HashMap();
		// get all groups
		try {
			GroupBusiness groupBiz = getGroupBusiness();
			Iterator groupsIterator = groups.iterator();
			String[] usrGroupType = new String[] { User.USER_GROUP_TYPE };
			while (groupsIterator.hasNext()) {
				Group group = (Group) groupsIterator.next();
				String groupId = group.getPrimaryKey().toString();
				String groupType = group.getGroupType();
				if (groupTypesToMoveAmong == null || (groupTypesToMoveAmong.contains(groupType))) {
					fillMaps(group, groupId, groupIdGroup, groupIdUsers, groupIdUsersId);
				}
				//Iterator childIterator =
				// groupBiz.getChildGroups(group).iterator();
				Collection coll = groupBiz.getChildGroupsRecursive(group, usrGroupType, false);
				if (coll != null && !coll.isEmpty()) {
					for (Iterator childIterator = coll.iterator(); childIterator.hasNext();) {
						Group childGroup = (Group) childIterator.next();
						String childGroupType = childGroup.getGroupType();
						if (groupTypesToMoveAmong == null || (groupTypesToMoveAmong.contains(childGroupType))) {
							String childGroupId = childGroup.getPrimaryKey().toString();
							fillMaps(childGroup, childGroupId, groupIdGroup, groupIdUsers, groupIdUsersId);
						}
					}
				}
			}
		}
		// Finder and RemoteException
		catch (Exception ex) {
			throw new EJBException("Error getting group. Message: " + ex.getMessage());
		}
		// iterate over all users
		Iterator groupIdsIterator = groupIdGroup.entrySet().iterator();
		// iterate over groups
		while (groupIdsIterator.hasNext()) {
			Map.Entry entry = (Map.Entry) groupIdsIterator.next();
			String parentGroupId = (String) entry.getKey();
			Group parentGroup = (Group) entry.getValue();
			Collection userInGroup = (Collection) groupIdUsers.get(parentGroupId);
			Iterator userIterator = userInGroup.iterator();
			// iterate over users within a group
			while (userIterator.hasNext()) {
				Collection possibleTargets = null;
				User user = (User) userIterator.next();
				// test if the user is assignable to one and only one group
				Iterator targetGroupIds = groupIdGroup.entrySet().iterator();
				while (targetGroupIds.hasNext()) {
					Map.Entry targetEntry = (Map.Entry) targetGroupIds.next();
					String targetGroupId = (String) targetEntry.getKey();
					Group targetGroup = (Group) targetEntry.getValue();
					boolean result = true;
					// skip the own group
					if (!targetGroupId.equals(parentGroupId)) {
						// check if the user is already a member of the target
						// group
						Collection userIdsOfTarget = (Collection) groupIdUsersId.get(targetGroupId);
						result = !userIdsOfTarget.contains(user.getPrimaryKey().toString());
					}
					// check if the user is suited for the target group when the
					// result is still true
					if (result) {
						result = (isUserSuitedForGroup(user, targetGroup) == null);
					}
					if (result) {
						if (possibleTargets == null) {
							possibleTargets = new ArrayList();
						}
						possibleTargets.add(targetGroup);
					}
				}
				userParentGroup.put(user, parentGroup);
				userTargetGroup.put(user, possibleTargets);
			}
		}
		// perform moving
		Map result = new HashMap();
		Iterator userIterator = userTargetGroup.entrySet().iterator();
		while (userIterator.hasNext()) {
			Map.Entry entry = (Map.Entry) userIterator.next();
			User user = (User) entry.getKey();
			Collection target = (Collection) entry.getValue();
			Group source = (Group) userParentGroup.get(user);
			Integer sourceId = (Integer) source.getPrimaryKey();
			Map map = (Map) result.get(sourceId);
			if (map == null) {
				map = new HashMap();
				result.put(sourceId, map);
			}
			if (target != null) {
				if (target.size() == 1) {
					int source_id = ((Integer) source.getPrimaryKey()).intValue();
					Group targetGr = (Group) target.iterator().next();
					int target_id = ((Integer) targetGr.getPrimaryKey()).intValue();
					if (source_id != target_id) {
						String message = moveUserWithoutTest(user, source, targetGr, iwuc.getCurrentUser());
						// if there is not a transaction error the message is
						// null!
						map.put(user.getPrimaryKey(), message);
					}
					else {
						map.put(user.getPrimaryKey(), null);
					}
				}
				else {
					String message = moreThanOneSuitableGroupMessage;
					boolean first = true;
					for (Iterator iter = target.iterator(); iter.hasNext();) {
						Group gr = (Group) iter.next();
						if (first) {
							message += " ";
						}
						else {
							message += ", ";
						}
						message += gr.getName();
						first = false;
					}
					map.put(user.getPrimaryKey(), message);
				}
			}
			else {
				map.put(user.getPrimaryKey(), noSuitableGroupMessage);
			}
		}
		return result;
	}

	private void fillMaps(Group group, String groupId, Map groupIdGroup, Map groupIdUsers, Map groupIdUsersId) {
		groupIdGroup.put(groupId, group);
		Collection usersInGroup = getUsersInGroup(group);
		groupIdUsers.put(groupId, usersInGroup);
		Collection userIds = new ArrayList();
		Iterator iterator = usersInGroup.iterator();
		while (iterator.hasNext()) {
			User user = (User) iterator.next();
			userIds.add(user.getPrimaryKey().toString());
		}
		groupIdUsersId.put(groupId, userIds);
	}

	public boolean isMemberOfGroup(int parentGroupToTest, User user) {
		// first check the primary group
		/*
		 * Eiki and jonas, commented out because we could not add users from old
		 * user system to the same group as their former primary group. We need
		 * this method to return false because they don't have a record in
		 * ic_group_relation like they should. Group group =
		 * user.getPrimaryGroup(); if (group != null) { int primaryGroupId =
		 * ((Integer) group.getPrimaryKey()).intValue(); if (parentGroupToTest ==
		 * primaryGroupId) { return true; } }
		 */
		// then check the group relations
		int userId = ((Integer) user.getPrimaryKey()).intValue();
		Collection coll;
		try {
			GroupRelationHome groupRelationHome = (GroupRelationHome) IDOLookup.getHome(GroupRelation.class);
			GroupHome groupHome = (GroupHome) IDOLookup.getHome(Group.class);
			String parentRelation = groupHome.getRelationTypeGroupParent();
			coll = groupRelationHome.findGroupsRelationshipsContainingUniDirectional(parentGroupToTest, userId,
					parentRelation);
		}
		// Remote and FinderException
		catch (Exception rme) {
			throw new RuntimeException(rme.getMessage());
		}
		return !coll.isEmpty();
	}

	private String moveUserWithoutTest(User user, Group parentGroup, Group targetGroup, User currentUser) {
		int userId = ((Integer) user.getPrimaryKey()).intValue();
		int targetGroupId = ((Integer) targetGroup.getPrimaryKey()).intValue();
		int parentGroupId = -1;
		if (parentGroup != null) {
			parentGroupId = ((Integer) parentGroup.getPrimaryKey()).intValue();
			if (parentGroupId == targetGroupId) {
				// there was a previous test therefore localization is it not
				// necessary
				return "source and target are the same";
			}
		}
		// note: if the primary group id is equal to minus one the user does not
		// belong to a group
		int primaryGroupId = user.getPrimaryGroupID();
		// Transaction starts
		javax.transaction.TransactionManager transactionManager = com.idega.transaction.IdegaTransactionManager.getInstance();
		try {
			transactionManager.begin();
			// check if the primaryGroup is the parentGroup or if the user does
			// not belong to a group at all
			boolean targetIsSetAsPrimaryGroup = false;
			if ((parentGroup != null && (parentGroupId == primaryGroupId)) || (primaryGroupId == -1)) {
				user.setPrimaryGroup(targetGroup);
				user.store();
				targetIsSetAsPrimaryGroup = true;
			}
			// remove user from parent group
			// IMPORTANT
			// if the parent group was the primary group there might(!) be
			// a corresponding GroupRelation.
			// usually there should not be such a GroupRelation
			// therefore be sure that the method below does not throw an error
			// if it
			// is not able to find a group relation.
			if (parentGroup != null) {
				parentGroup.removeUser(user, currentUser);
			}
			// set target group
			if (!targetIsSetAsPrimaryGroup) {
				targetGroup.addGroup(userId);
			}
			else {
				/**
				 * TODO solve group relation primary group problem
				 */
				// this is a hack. If the target group is already the primary
				// group
				// it should not be necessary to add a corresponding group
				// relation.
				// but if it is not added the group tree does not know that this
				// user is a child
				// of the target group
				targetGroup.addGroup(userId);
			}
			transactionManager.commit();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			try {
				transactionManager.rollback();
			}
			catch (javax.transaction.SystemException sy) {
				sy.printStackTrace(System.err);
			}
			// this is an unusual error therefore localization is it not
			// necessary
			return "Transaction failed";
		}
		return null;
	}

	public String isUserSuitedForGroup(User user, Group targetGroup) {
		try {
			String grouptype = targetGroup.getGroupType();
			Collection plugins = (Collection) pluginsForGroupTypeCachMap.get(grouptype);
			if (plugins == null) {
				plugins = getGroupBusiness().getUserGroupPluginsForGroupType(grouptype);
				pluginsForGroupTypeCachMap.put(grouptype, plugins);
			}
			Iterator iter = plugins.iterator();
			while (iter.hasNext()) {
				UserGroupPlugInBusiness pluginBiz = (UserGroupPlugInBusiness) iter.next();
				String message;
				if ((message = pluginBiz.isUserSuitedForGroup(user, targetGroup)) != null) {
					return message;
				}
			}
		}
		catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return null;
	}

	public String getUserApplicationStyleSheetURL() {
		IWMainApplication application = getIWApplicationContext().getIWMainApplication();
		IWBundle bundle = application.getBundle("com.idega.user");
		String styleScript = "DefaultStyle.css";
		String styleSrc = bundle.getVirtualPathWithFileNameString(application.getBundle("com.idega.user").getProperty(
				"styleSheet_name", styleScript));
		return styleSrc;
	}

	public boolean isInDefaultCommune(User user) throws RemoteException, FinderException {
		Address address = getUsersMainAddress(user);
		Commune commune = null;
		if (address != null)
			commune = getCommuneHome().findByPrimaryKey(new Integer(address.getCommuneID()));
		else
			return false;
		if (commune != null)
			return commune.getIsDefault();
		return false;
	}

	private CommuneHome getCommuneHome() throws RemoteException {
		return (CommuneHome) IDOLookup.getHome(Commune.class);
	}

	/**
	 * Creates or updates a user from an LDAP DN and its attributes.
	 * 
	 * @throws NamingException,RemoteException
	 * @see com.idega.user.business.UserBusiness#createOrUpdateUser(DN
	 *      distinguishedName,Attributes attributes)
	 */
	public User createOrUpdateUser(DN distinguishedName, Attributes attributes) throws CreateException,
			NamingException, RemoteException {
		IWLDAPUtil ldapUtil = IWLDAPUtil.getInstance();
		String fullName = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_COMMON_NAME, attributes);
		Name name = new Name(fullName);
		String firstName = name.getFirstName();
		String middleName = name.getMiddleName();
		String lastName = name.getLastName();
		String description = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_DESCRIPTION, attributes);
		String uniqueID = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_UNIQUE_ID,attributes);
		String personalId = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_PERSONAL_ID,attributes);
		String userName = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_UID,attributes);
		if(userName==null || "".equals(userName)){
			//maybe its active directory!
			userName = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_UID_ACTIVE_DIRECTORY,attributes);	
		}
		
		String userPassword = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_USER_PASSWORD,attributes);
		String email = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_EMAIL, attributes);
		//String homePhone =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_TELEPHONE_NUMBER,attributes);
		//String fax =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_FAX_NUMBER,attributes);
		//String mobile =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_MOBILE_NUMBER,attributes);
//		TODO eiki handle addresses
		//String address =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_REGISTERED_ADDRESS,attributes);
		String gender = ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_IDEGAWEB_GENDER, attributes);
		int genderId = -1;
		if (gender != null) {
			try {
				genderId = getGenderId(gender).intValue();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//find the user
		User user = null;
		if (uniqueID != null) {
			try {
				user = getUserByUniqueId(uniqueID);
			}
			catch (FinderException e) {
				System.out.println("UserBusiness: User not found by unique id:" + uniqueID);
			}
		}
		
		if (user == null && personalId != null) {
			try {
				user = getUser(personalId);
			}
			catch (FinderException e) {
				System.out.println("UserBusiness: User not found by personal id:" + personalId);
			}
		}
		
		if (user == null) {
			user = getUserByDirectoryString(distinguishedName.toString());
		}
		
//		if (user == null && firstName != null) {
//			try {
//				Collection users = getUserHome().findUsersByConditions(firstName, middleName, lastName, null, null,
//						null, genderId, -1, -1, -1, null, null, true, false);
//				if (users != null && !users.isEmpty() && users.size() == 1) {
//					//its the only one with this name must be our guy!
//					user = (User) users.iterator().next();
//				}
//			}
//			catch (FinderException e) {
//				System.out.println("UserBusiness: last try...user not found by firstname,middlename,lastname");
//			}
//		}
		
		//could not find the person create it
		if (user == null) {
			user = createUser(firstName, middleName, lastName, personalId);
			user.store();
		}
		
		//update stuff
		//the unique id
		if (uniqueID != null) {
			user.setUniqueId(uniqueID);
		}
		
		//the gender id
		if (genderId > 0) {
			user.setGender(genderId);
		}
		
		//the description
		user.setDescription(description);
		
		user.store();
		
		//the email
		updateUserMail(user, email);
		
		//the login
		if(userName!=null){
			try {
				int userId = ((Integer)user.getPrimaryKey()).intValue();
				LoginTable login = LoginDBHandler.getUserLogin(userId);
				if(login==null){
					//no login create one
					login = ((LoginTableHome) com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).createLegacy();
					login.setUserId(userId);	
				}
				
				login.setUserLogin(userName);
				if(userPassword!=null){
//					remove the encryption e.g. {md5} prefix
					userPassword = userPassword.substring(userPassword.indexOf("}")+1);
					login.setUserPasswordInClearText(userPassword);
				}
				login.setLastChanged(IWTimestamp.getTimestampRightNow());
				login.store();
			}
			catch (IDOStoreException e) {
				e.printStackTrace();
			}
			catch (EJBException e) {
				e.printStackTrace();
			}
		}
			
		//set all the attributes as metadata also
		setMetaDataFromLDAPAttributes(user, distinguishedName, attributes);
		
			
		
//			TODO Eiki make a method updatePhones(home,fax,mobile) DO in
			// update also
			//getPhoneHome().findUsersFaxPhone();
		//	  		updateUsersMainAddressOrCreateIfDoesNotExist()
		//	  		String address =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_REGISTERED_ADDRESS,attributes);
		//			String phone =
		// ldapUtil.getSingleValueOfAttributeByAttributeKey(LDAP_ATTRIBUTE_TELEPHONE_NUMBER,attributes);  		
		
		return user;
	}

	public User getUserByUniqueId(String uniqueID) throws FinderException {
		User user;
		user = getUserHome().findUserByUniqueId(uniqueID);
		return user;
	}

	/**
	 * Creates or updates a user from an LDAP DN and its attributes and adds it
	 * under the parentGroup supplied
	 * 
	 * @throws NamingException
	 * @throws CreateException
	 * @throws NamingException
	 * @throws RemoteException
	 * @see com.idega.user.business.UserBusiness#createOrUpdateUser(DN
	 *      distinguishedName,Attributes attributes,Group parentGroup)
	 */
	public User createOrUpdateUser(DN distinguishedName, Attributes attributes, Group parentGroup)
			throws RemoteException, CreateException, NamingException {
		User user = createOrUpdateUser(distinguishedName, attributes);
		parentGroup.addGroup(user);
		return user;
	}

	/**
	 * Adds all the ldap attributes as metadata-fields
	 * 
	 * @param group
	 * @param distinguishedName
	 * @param attributes
	 */
	public void setMetaDataFromLDAPAttributes(User user, DN distinguishedName, Attributes attributes) {
		try {
			getGroupBusiness().setMetaDataFromLDAPAttributes(user, distinguishedName, attributes);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets all the users that have this ldap metadata
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Collection getUsersByLDAPAttribute(String key, String value) {
		IWLDAPUtil util = IWLDAPUtil.getInstance();
		Collection users;
		try {
			users = getUserHome().findUsersByMetaData(util.getAttributeKeyWithMetaDataNamePrefix(key), value);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
		return users;
	}

	/**
	 * Looks for the user by his DN in his metadata
	 * 
	 * @param identifier
	 * @return
	 */
	public User getUserByDirectoryString(DirectoryString dn) {
		return getUserByDirectoryString(dn.toString());
	}

	/**
	 * Looks for the user by his DN in his metadata
	 * 
	 * @param identifier
	 * @return
	 */
	public User getUserByDirectoryString(String dn) {
		User user = null;
		dn = dn.toLowerCase(); 
		Collection users = getUsersByLDAPAttribute(IWLDAPConstants.LDAP_META_DATA_KEY_DIRECTORY_STRING, dn);
		if (!users.isEmpty() && users.size() == 1) {
			user = (User) users.iterator().next();
		}
		else {
			int index = dn.indexOf(IWLDAPConstants.LDAP_USER_DIRECTORY_STRING_SEPARATOR);
			int commaIndex = dn.indexOf(",");
			if (index > 0 && index < commaIndex) {
				String pid = dn.substring(index + 1, commaIndex);
				try {
					user = getUser(pid);
				}
				catch (FinderException e) {
				}
			}
			else {
				//TODO find by his name
				//getUserHome().findUsersByConditions()
			}
		}
		return user;
	}

	public Collection getUsersBySpecificGroupsUserstatusDateOfBirthAndGender(Collection groups, Collection userStatuses, Integer yearOfBirthFrom, Integer yearOfBirthTo, String gender) {
	    try {
            return getUserHome().ejbFindUsersBySpecificGroupsUserstatusDateOfBirthAndGender(groups, userStatuses, yearOfBirthFrom, yearOfBirthTo, gender);
        } catch (FinderException e) {
            return ListUtil.getEmptyList();
        }
	}
	
	private UserCommentHome getUserCommentHome() {
		try {
			return (UserCommentHome) IDOLookup.getHome(UserComment.class);
		}
		catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
	
	public void storeUserComment(User user, String comment, User performer) {
		try {
			UserComment userComment = getUserCommentHome().create();
			userComment.setUser(user);
			userComment.setComment(comment);
			userComment.setCreatedDate(new IWTimestamp().getDate());
			userComment.setCreatedBy(performer);
			userComment.store();
		}
		catch (CreateException ce) {
			log(ce);
		}
	}
	
	public Collection getUserComments(User user) throws FinderException {
		Collection comments = getUserCommentHome().findAllByUser(user);
		if (comments == null || comments.isEmpty()) {
			throw new FinderException("No comments found for user with PK = " + user.getPrimaryKey());
		}
		return comments;
	}
} // Class UserBusiness
