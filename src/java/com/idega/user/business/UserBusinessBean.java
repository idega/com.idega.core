/*
 * $Id: UserBusinessBean.java,v 1.246 2009/05/27 11:25:19 laddi Exp $
 * Created in 2002 by gummi
 *
 * Copyright (C) 2002-2005 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.user.business;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.LoginCreateException;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.accesscontrol.dao.UserLoginDAO;
import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.accesscontrol.data.ICRole;
import com.idega.core.accesscontrol.data.ICRoleHome;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.accesscontrol.data.PasswordNotKnown;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.contact.data.EmailType;
import com.idega.core.contact.data.EmailTypeHome;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneBMPBean;
import com.idega.core.contact.data.PhoneHome;
import com.idega.core.localisation.data.ICLanguage;
import com.idega.core.localisation.data.ICLanguageHome;
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
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.user.bean.GroupMemberDataBean;
import com.idega.user.bean.GroupMemberDataBeanComparator;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.Group;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.MetadataConstants;
import com.idega.user.data.Status;
import com.idega.user.data.TopNodeGroup;
import com.idega.user.data.TopNodeGroupHome;
import com.idega.user.data.User;
import com.idega.user.data.UserComment;
import com.idega.user.data.UserCommentHome;
import com.idega.user.data.UserHome;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.LocaleUtil;
import com.idega.util.SendMail;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.Timer;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Name;
import com.idega.util.text.SocialSecurityNumber;

/**
 * <p>
 * This is the the class that holds the main business logic for creating, removing, lookups and manipulating Users.
 * </p>
 * Copyright (C) idega software 2002-2005 <br/> Last modified: $Date: 2009/05/27 11:25:19 $ by $Author: laddi $
 *
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>,<a href="eiki@idega.is">Eirikur S. Hrafnsson</a>, <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.246 $
 */
public class UserBusinessBean extends com.idega.business.IBOServiceBean implements UserBusiness {

	private static final long serialVersionUID = 5915206628531551728L;

	private static final Logger LOGGER = Logger.getLogger(UserBusinessBean.class.getName());

	// remove use of "null" when metadata can be removed
	private static final String NULL = "null";

	private static final String JOB_META_DATA_KEY = "job";
	private static final String USER_BIRTH_COUNTRY_META_DATA_KEY = "birth_country";
	private static final String USER_YEAR_WHEN_USER_CAME_TO_ICELAND_META_DATA_KEY = "year_when_came_to_iceland";
	private static final String USER_PRIMARY_LANGUAGE_META_DATA_KEY = "primary_language";
	private static final String USER_SECONDARY_LANGUAGE_META_DATA_KEY = "secondary_language";
	private static final String USER_THIRD_LANGUAGE_META_DATA_KEY = "third_language";
	private static final String USER_FOURTH_LANGUAGE_META_DATA_KEY = "fourth_language";

	private static final String WORKPLACE_META_DATA_KEY = "workplace";

	private static final String SESSION_KEY_TOP_NODES = "top_nodes_for_user";

	private static final int NUMBER_OF_PERMISSIONS_CACHING_LIMIT = 800;

	private GroupHome groupHome;

	private ICLanguageHome icLanguageHome;

	private CountryHome countryHome;

	private UserHome userHome;

	private EmailHome emailHome;

	private EmailTypeHome emailTypeHome;

	private AddressHome addressHome;

	private PhoneHome phoneHome;

	private TopNodeGroupHome topNodeGroupHome;

	private Gender male, female;

	private Map<String, Collection<UserGroupPlugInBusiness>> pluginsForGroupTypeCachMap = new HashMap<String, Collection<UserGroupPlugInBusiness>>();

	private UserStatusBusiness statusBusiness = null;

	private UserInfoColumnsBusiness userInfoBusiness = null;

	private SimpleDateFormat userDateOfBirthFormatter = new SimpleDateFormat("ddMMyy");
	private SimpleDateFormat userDateOfBirthFormatterYearsFirst = new SimpleDateFormat("yyMMdd");
	private SimpleDateFormat userDateOfBirthFormatterFullYears = new SimpleDateFormat("yyyyMMdd");

	@Autowired
	private UserDAO userDAO;

	public UserBusinessBean() {
	}

	private UserDAO getUserDAO() {
		if (userDAO == null)
			ELUtil.getInstance().autowire(this);
		return userDAO;
	}

	@Override
	public UserHome getUserHome() {
		if (this.userHome == null) {
			try {
				this.userHome = (UserHome) IDOLookup.getHome(User.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.userHome;
	}

	@Override
	public GroupHome getGroupHome() {
		if (this.groupHome == null) {
			try {
				this.groupHome = (GroupHome) IDOLookup.getHome(Group.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.groupHome;
	}

	@Override
	public ICLanguageHome getICLanguageHome() {
		if (this.icLanguageHome  == null) {
			try {
				this.icLanguageHome = (ICLanguageHome) IDOLookup.getHome(ICLanguage.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.icLanguageHome;
	}


	@Override
	public CountryHome getCountryHome() {
		if (this.countryHome  == null) {
			try {
				this.countryHome = (CountryHome) IDOLookup.getHome(Country.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.countryHome;
	}

	@Override
	public EmailHome getEmailHome() {
		if (this.emailHome == null) {
			try {
				this.emailHome = (EmailHome) IDOLookup.getHome(Email.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.emailHome;
	}

	public EmailTypeHome getEmailTypeHome() {
		if (this.emailTypeHome == null) {
			try {
				this.emailTypeHome = (EmailTypeHome) IDOLookup.getHome(EmailType.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.emailTypeHome;
	}

	@Override
	public AddressHome getAddressHome() {
		if (this.addressHome == null) {
			try {
				this.addressHome = (AddressHome) IDOLookup.getHome(Address.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.addressHome;
	}

	@Override
	public PhoneHome getPhoneHome() {
		if (this.phoneHome == null) {
			try {
				this.phoneHome = (PhoneHome) IDOLookup.getHome(Phone.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.phoneHome;
	}

	@Override
	public TopNodeGroupHome getTopNodeGroupHome() {
		if (this.topNodeGroupHome == null) {
			try {
				this.topNodeGroupHome = (TopNodeGroupHome) IDOLookup.getHome(TopNodeGroup.class);
			} catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.topNodeGroupHome;
	}

	/**
	 * @deprecated replaced with createUser
	 */
	@Override
	@Deprecated
	public User insertUser(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, displayname, null, description, gender, date_of_birth, primary_group);
	}

	/**
	 * Method createUserByPersonalIDIfDoesNotExist either created a new user or updates an old one.
	 *
	 * @param fullName
	 * @param personalID
	 * @param gender
	 * @param dateOfBirth
	 * @return User
	 * @throws CreateException
	 * @throws RemoteException
	 */
	@Override
	public User createUserByPersonalIDIfDoesNotExist(String fullName, String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException, RemoteException {
		User user = null;
		if (personalID != null && personalID.trim().length() > 0) {
			// if a user exists with same personal id, we'll use him instead of
			// creating a new one
			try {
				user = getUserHome().findByPersonalID(personalID);
			} catch (FinderException e) {
				// user is still null, that's ok we'll create a new one
			}
		}
		if (user != null) {
			// found user with given personal id, use that user since no two
			// users can have same personal id
			user.setFullName(fullName);
			if (!user.getDisplayNameSetManually()) {
				user.setDisplayName(fullName);
			}
			if (gender != null) {
				user.setGender((Integer) gender.getPrimaryKey());
			}
			if (dateOfBirth != null) {
				user.setDateOfBirth(dateOfBirth.getDate());
			}
			user.store();
		} else {
			Name name = new Name(fullName);
			user = createUser(name.getFirstName(), name.getMiddleName(), name.getLastName(), fullName, personalID, null, gender != null ? (Integer) gender.getPrimaryKey() : null, dateOfBirth, null);
			// user = createUser(name.getFirstName(), name.getMiddleName() ,
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
	@Override
	public User createUserByPersonalIDIfDoesNotExist(String firstName, String middleName, String lastName, String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException, RemoteException {
		User user;
		Name name = new Name(firstName, middleName, lastName);
		String fullName = name.getName();
		user = createUserByPersonalIDIfDoesNotExist(fullName, personalID, gender, dateOfBirth);
		return user;
	}

	/**
	 *
	 * Creates or update a user with the supplied parameters and then calls callAllUserGroupPluginAfterUserCreateOrUpdateMethod(user);
	 *
	 * @param pin
	 *            PersonalId e.g. social security number MUST NOT BE NULL
	 * @param UUID
	 *            Unique id MUST NOT BE NULL
	 * @param fullName
	 * @param gender
	 *            as a string either "f" for female or "m" for male
	 * @param dateOfBirth
	 *            as a String in the format yyyy-MM-dd
	 * @throws CreateException
	 * @throws RemoteException
	 */
	@Override
	public void createUserByPersonalIdAndUUIDOrUpdate(String pin, String UUID, String fullName, String gender, String dateOfBirth) throws CreateException, RemoteException {

		if (UUID != null && pin != null) {
			User user = null;
			try {
				user = getUserByUniqueId(pin);
			} catch (FinderException e) {
				log("User not found by UUID:" + UUID + " trying pin:" + pin);
			}
			try {
				if (user == null) {
					user = getUser(pin);
					user.setUniqueId(UUID);
				}
			} catch (FinderException e) {
				log("User not found by pin:" + pin + " creating a new user...");
			}

			if (user == null) {
				user = createUserByPersonalIDIfDoesNotExist(fullName, pin, null, null);
				user.setUniqueId(UUID);
				user.store();
			}

			updateUser(user, fullName, gender, dateOfBirth);
			callAllUserGroupPluginAfterUserCreateOrUpdateMethod(user);

		}
	}

	/**
	 * Updates the user with fullName,gender (f/m) and date of birth (yyy-MM-dd)
	 *
	 * @param user
	 * @param name
	 * @param gender
	 * @param dateOfBirth
	 */
	@Override
	public void updateUser(User user, String name, String gender, String dateOfBirth) {
		if (name != null) {
			user.setFullName(name);
		}
		if (dateOfBirth != null) {
			Date birthDate = null;
			try {
				birthDate = java.sql.Date.valueOf(dateOfBirth);
				user.setDateOfBirth(birthDate);
			} catch (IllegalArgumentException e) {
				log("UserBusiness: date of birth format is invalid.Should be yyyy-MM-dd : " + dateOfBirth);
			}
		}
		if (gender != null) {
			Integer genderId;
			try {
				genderId = getGenderId(gender);
				user.setGender(genderId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		user.store();
	}

	@Override
	public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group) throws CreateException, RemoteException {
		return createUser(firstName, middleName, lastName, displayname, personalID, description, gender, date_of_birth, primary_group, null);
	}

	@Override
	public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String fullName) throws CreateException, RemoteException {
		try {
			User userToAdd = getUserHome().create();

			if (fullName == null) {

				Name name = new Name(firstName, middleName, lastName);
				userToAdd.setFirstName(name.getFirstName());
				userToAdd.setMiddleName(name.getMiddleName());
				userToAdd.setLastName(name.getLastName());

			} else {

				userToAdd.setFullName(fullName);
			}

			/*
			 * userToAdd.setFirstName(firstName); userToAdd.setMiddleName(middleName); userToAdd.setLastName(lastName);
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
			// UserGroupRepresentative group =
			// (UserGroupRepresentative)this.getUserGroupRepresentativeHome().create();
			// group.setName(userToAdd.getName());
			// group.setDescription("User representative in table ic_group");
			// group.store();
			// userToAdd.setGroup(group);
			// userToAdd.store();
			if (primary_group != null) {
				Group prgr = userToAdd.getPrimaryGroup();
				prgr.addGroup(userToAdd);
			}
			return userToAdd;
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().warning("Error creating user with personalID=" + personalID + ", firstName=" + firstName + ", lastName" + lastName);
			throw new IDOCreateException(e);
		}
	}

	@Override
	public void setUserUnderDomain(ICDomain domain, User user, GroupDomainRelationType type) throws CreateException, RemoteException {
		GroupDomainRelation relation = IDOLookup.create(GroupDomainRelation.class);
		relation.setDomain(domain);
		relation.setRelatedUser(user);
		if (type != null) {
			relation.setRelationship(type);
		}
		relation.store();
	}

	/**
	 * Generates a login for a user with a random password and a login derived from the users name (or random login if all possible logins are taken)
	 *
	 * @param userId
	 *            the id for the user.
	 * @throws LoginCreateException
	 *             If an error occurs creating login for the user.
	 */
	@Override
	public LoginTable generateUserLogin(int userID) throws LoginCreateException, RemoteException {
		// return this.generateUserLogin(userID);
		return LoginDBHandler.generateUserLogin(userID);
	}

	/**
	 * Generates a login for a user with a random password and a login derived from the users name (or random login if all possible logins are taken)
	 */
	@Override
	public LoginTable generateUserLogin(User user) throws LoginCreateException, RemoteException {
		// return LoginDBHandler.generateUserLogin(user);
		int userID = ((Integer) user.getPrimaryKey()).intValue();
		return this.generateUserLogin(userID);
	}

	/**
	 * Creates a user with a firstname,middlename, lastname, where middlename can be null
	 */
	@Override
	public User createUser(String firstname, String middlename, String lastname) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, (String) null);
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname and personalID where middlename and personalID can be null
	 */
	@Override
	public User createUser(String firstname, String middlename, String lastname, String personalID) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null, null, null, null);
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname and primaryGroupID where middlename can be null
	 */
	@Override
	public User createUser(String firstName, String middleName, String lastName, int primary_groupID) throws CreateException, RemoteException {
		return createUser(firstName, middleName, lastName, null, null, null, null, null, new Integer(primary_groupID));
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname and primaryGroupID where middlename can be null but primary_group can not be noull
	 */
	@Override
	public User createUser(String firstName, String middleName, String lastName, Group primary_group) throws CreateException, RemoteException {
		return createUser(firstName, middleName, lastName, null, null, null, null, null, (Integer) primary_group.getPrimaryKey());
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname ,personalID and gender where middlename and personalID can be null
	 */
	@Override
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null, (Integer) gender.getPrimaryKey(), null, null);
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname ,personalID, gender and date of birth where middlename,personalID,gender,dateofbirth can be null
	 *
	 * @throws NullPointerException
	 *             if primaryGroup is null
	 */
	@Override
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender, IWTimestamp dateOfBirth, Group primaryGroup) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null, (Integer) gender.getPrimaryKey(), dateOfBirth, (Integer) primaryGroup.getPrimaryKey());
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname ,personalID, gender and date of birth where middlename,personalID,gender,dateofbirth can be null
	 */
	@Override
	public User createUser(String firstname, String middlename, String lastname, String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, null, personalID, null, gender != null ? (Integer) gender.getPrimaryKey() : null, dateOfBirth, null);
	}

	/**
	 * Creates a new user with a firstname,middlename, lastname ,personalID, gender and date of birth where middlename,personalID,gender,dateofbirth can be null
	 */
	public User createUser(String firstname, String middlename, String lastname, String displayname, String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException, RemoteException {
		return createUser(firstname, middlename, lastname, displayname, personalID, null, gender != null ? (Integer) gender.getPrimaryKey() : null, dateOfBirth, null);
	}

	@Override
	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws CreateException {
		return createUserWithLogin(firstname, middlename, lastname, SSN, displayname, description, gender, date_of_birth, primary_group, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType, null);
	}

	@Override
	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType, String fullName) throws CreateException {
		UserTransaction transaction = this.getSessionContext().getUserTransaction();
		AccessController controller = getIWMainApplication().getAccessController();
		try {
			transaction.begin();
			User newUser;
			// added by Aron 07.01.2002 ( aron@idega.is )
			if (primary_group == null) {
				primary_group = new Integer(controller.getUsersGroupID());
			}
			// newUser = insertUser(firstname,middlename,
			// lastname,null,null,null,null,primary_group);
			newUser = createUser(firstname, middlename, lastname, displayname, SSN, description, gender, date_of_birth, primary_group, fullName);
			if (userLogin != null && password != null && !userLogin.equals("") && !password.equals("")) {
				LoginDBHandler.createLogin(newUser, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
			}
			transaction.commit();
			return newUser;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				transaction.rollback();
			} catch (SystemException se) {
			}
			throw new CreateException(e.getMessage());
		}
	}

	@Override
	public void createUserLogin(User newUser, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) {

		if (newUser != null && !StringUtil.isEmpty(userLogin) && !StringUtil.isEmpty(password)) {

			try {
				LoginDBHandler.createLogin(newUser, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);

			} catch (Exception e) {
				getLogger().log(Level.SEVERE, "Exception while creating user login for userLogin="+userLogin+", user="+newUser.getPrimaryKey(), e);
			}
		} else
			throw new IllegalArgumentException("Tried to create login for user="+(newUser != null ? newUser.getPrimaryKey().toString() : null)+", but insufficient parameters provided: userLogin="+userLogin+", password="+password);
	}

	@Override
	public User createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws CreateException {
		return createUserWithLogin(firstname, middlename, lastname, null, displayname, description, gender, date_of_birth, primary_group, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
	}

	/*
	 * public User getUser(int userGroupRepresentativeID) throws SQLException { List l = EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(User.class),com.idega.user.data.UserBMPBean._COLUMNNAME_USER_GROUP_ID,userGroupRepresentativeID); if(l != null && l.size() > 0){ return ((User)l.get(0)); } return null; }
	 *
	 * public int getUserID(int userGroupRepresentativeID) throws SQLException { User user = getUser(userGroupRepresentativeID); if(user != null){ return user.getID(); } return -1; }
	 */
	/**
	 * This methods removes this user from all groups and deletes his login.
	 */
	@Override
	public void deleteUser(int userId, User currentUser) throws RemoveException {
		User delUser = getUser(userId);
		deleteUser(delUser, currentUser);
	}

	/**
	 * This methods remoces this useer from all groups and deletes his login.
	 */
	@Override
	public void deleteUser(User delUser, User currentUser) throws RemoveException {
		try {
			Collection<Group> groups = getGroupBusiness().getParentGroups(delUser);
			if (groups != null && !groups.isEmpty()) {
				for (Iterator<Group> iter = groups.iterator(); iter.hasNext();) {
					Group group = iter.next();
					removeUserFromGroup(delUser, group, currentUser);
				}
			}
			LoginDBHandler.deleteUserLogin(delUser.getID());
			// delUser.removeAllAddresses();
			// delUser.removeAllEmails();
			// delUser.removeAllPhones();
			/*
			 * try { this.getGroupBusiness().deleteGroup(groupId); }catch (FinderException fe) { System.out.println("[UserBusinessBean] : cannot find group to delete with user"); }
			 */
			delUser.delete(currentUser.getID());
			delUser.store();
		} catch (Exception e) {
			// e.printStackTrace(System.err);
			throw new RemoveException(e.getMessage());
		}
	}

	@Override
	public void removeUserFromGroup(int userId, Group group, User currentUser) throws RemoveException {
		User user = getUser(userId);
		removeUserFromGroup(user, group, currentUser);
	}

	@Override
	public void removeUserFromGroup(User user, Group group, User currentUser) throws RemoveException {
		// call plugin methods first
		callAllUserGroupPluginBeforeUserRemoveMethod(user, group);

		group.removeUser(user, currentUser);
		Integer primaryGroupId = new Integer(user.getPrimaryGroupID());
		if (group.getPrimaryKey().equals(primaryGroupId)) {
			// update primary group for user, since it was the group the user was removed from
			Collection<Group> groups = user.getParentGroups();
			Iterator<Group> iter = groups.iterator();
			Group newPG = null;
			if (groups != null && !groups.isEmpty()) {
				// no smart way to find new primary group, just set as first group in user groups collection that
				// is not same as current primary group
				newPG = iter.next();
				while (newPG != null && newPG.getPrimaryKey().equals(primaryGroupId)) {
					if (iter.hasNext()) {
						newPG = iter.next();
					} else {
						newPG = null;
					}
				}
			}
			if (newPG != null) {
				user.setPrimaryGroup(newPG);
			} else {
				user.setPrimaryGroupID(null);
			}
			user.store();
		}
	}

	@Override
	public void setPermissionGroup(User user, Integer primaryGroupId) throws IDOStoreException, RemoteException {
		if (primaryGroupId != null) {
			user.setPrimaryGroupID(primaryGroupId);
			user.store();
		}
	}

	/**
	 * Male: M, male, 0 Female: F, female, 1
	 */
	@Override
	public Integer getGenderId(String gender) throws Exception {
		try {
			GenderHome home = getGenderHome();
			if (gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("0")) {
				if (this.male == null) {
					this.male = home.getMaleGender();
				}
				return (Integer) this.male.getPrimaryKey();
			} else if (gender.equalsIgnoreCase("F") || gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("1")) {
				if (this.female == null) {
					this.female = home.getFemaleGender();
				}
				return (Integer) this.female.getPrimaryKey();
			} else {
				// throw new RuntimeException("String gender must be: M, male,
				// 0, F, female or 1 ");
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returnes true if that genderid refers to the male gender
	 */
	@Override
	public boolean isMale(int genderId) throws RemoteException, FinderException {
		GenderHome home = getGenderHome();
		if (this.male == null) {
			this.male = home.getMaleGender();
		}
		return (((Integer) this.male.getPrimaryKey()).intValue() == genderId);
	}

	/**
	 * Returnes true if that genderid refers to the female gender
	 */
	@Override
	public boolean isFemale(int genderId) throws RemoteException, FinderException {
		return !isMale(genderId);
	}

	private GenderHome getGenderHome() throws RemoteException {
		GenderHome home = (GenderHome) this.getIDOHome(Gender.class);
		return home;
	}

	@Override
	public Phone[] getUserPhones(int userId) throws RemoteException {
		try {
			Collection<Phone> phones = this.getUser(userId).getPhones();
			// if(phones != null){
			return phones.toArray(new Phone[phones.size()]);
			// }
			// return (Phone[])
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
		} catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Phone[] getUserPhones(User user) throws RemoteException {
		try {
			Collection<Phone> phones = user.getPhones();
			// if(phones != null){
			return phones.toArray(new Phone[phones.size()]);
			// }
			// return (Phone[])
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
		} catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Phone getUserPhone(int userId, int phoneTypeId) throws RemoteException {
		try {
			Phone[] result = this.getUserPhones(userId);
			// IDOLegacyEntity[] result =
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					if (result[i].getPhoneTypeId() == phoneTypeId) {
						return result[i];
					}
				}
			}
			return null;
		} catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/*
	 * Deprecated
	 *
	 * (non-Javadoc)
	 *
	 * @see com.idega.user.business.UserBusiness#getUserMail(int)
	 *
	 * @deprecated use getUserMainMail
	 */
	@Override
	public Email getUserMail(int userId) {
		return getUserMail(this.getUser(userId));
	}

	/*
	 * Deprecated
	 *
	 * (non-Javadoc)
	 *
	 * @see com.idega.user.business.UserBusiness#getUserMail(com.idega.user.data.User)
	 *
	 * @deprecated use getUserMainMail
	 */
	@Override
	public Email getUserMail(User user) {
		try {
			return getUsersMainEmail(user);
		} catch (Exception ex) {
			// System.out.println(ex.getMessage());
			// ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void updateUserHomePhone(User user, String phoneNumber) throws EJBException {
		updateUserPhone(user, PhoneBMPBean.getHomeNumberID(), phoneNumber);
	}

	@Override
	public void updateUserWorkPhone(User user, String phoneNumber) throws EJBException {
		updateUserPhone(user, PhoneBMPBean.getWorkNumberID(), phoneNumber);
	}

	@Override
	public void updateUserMobilePhone(User user, String phoneNumber) throws EJBException {
		updateUserPhone(user, PhoneBMPBean.getMobileNumberID(), phoneNumber);
	}

	@Override
	public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber) throws EJBException {
		updateUserPhone(getUser(userId), phoneTypeId, phoneNumber);
	}

	@Override
	public void updateUserPhone(User user, int phoneTypeId, String phoneNumber) throws EJBException {
		try {
			Phone phone = getUserPhone(Integer.valueOf(user.getPrimaryKey().toString()), phoneTypeId);
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
				// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(phone);
				user.addPhone(phone);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
	}

	@Override
	public Email updateUserMail(int userId, String email) throws CreateException, RemoteException {
		return updateUserMail(getUser(userId), email);
	}

	/**
	 * Updates or creates the main email address (that is the email with type "main"l) if the specifield email is empty (that is null or empty) nothing happens.
	 */
	@Override
	public Email updateUserMail(User user, String email) throws CreateException, RemoteException {
		if (StringHandler.isEmpty(email)) {
			return null;
		}
		Email mainEmail = null;
		try {
			// note: call of the following method does some repairing
			// + if main mail is not set yet a main email is figured out
			mainEmail = getUsersMainEmail(user);
		} catch (NoEmailFoundException ex) {
			mainEmail = null;
		}
		// email was found
		if (mainEmail != null) {
			String oldAddress = mainEmail.getEmailAddress();
			// is it an update at all?
			if (!email.equals(oldAddress)) {
				mainEmail.setEmailAddress(email);
				mainEmail.store();
			}
			return mainEmail;
		}
		// not found? create a new one!
		try {
			mainEmail = this.getEmailHome().create();
			EmailType mainEmailType = getEmailTypeHome().findMainEmailType();
			mainEmail.setEmailType(mainEmailType);
			mainEmail.setEmailAddress(email);
			mainEmail.store();
			user.addEmail(mainEmail);
			return mainEmail;
		} catch (FinderException ex) {
			throw new CreateException("Main email type could not be found");
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public void updateUserJob(int userId, String job) {
		if (job == null || job.length() == 0) {
			job = NULL;
		}
		User user = getUser(userId);
		user.setMetaData(JOB_META_DATA_KEY, job);
		user.store();
	}

	@Override
	public String getUserJob(User user) {
		String job = user.getMetaData(JOB_META_DATA_KEY);
		if (job == null || NULL.equals(job)) {
			return "";
		} else {
			return job;
		}
	}

	@Override
	public void updateUserWorkPlace(int userId, String workPlace) {
		if (workPlace == null || workPlace.length() == 0) {
			workPlace = NULL;
		}
		User user = getUser(userId);
		user.setMetaData(WORKPLACE_META_DATA_KEY, workPlace);
		user.store();
	}

	@Override
	public String getUserWorkPlace(User user) {
		String workPlace = user.getMetaData(WORKPLACE_META_DATA_KEY);
		if (workPlace == null || NULL.equals(workPlace)) {
			return "";
		} else {
			return workPlace;
		}
	}

	/**
	 * @deprecated user getUsersMainAddress instead. Gets the users main address and returns it.
	 * @returns the address if found or null if not.
	 */
	@Override
	@Deprecated
	public Address getUserAddress1(int userID) throws EJBException, RemoteException {
		return getUsersMainAddress(userID);
	}

	/**
	 * Gets the user's main address by addresstype and returns it.
	 *
	 * @returns the address if found or null if not.
	 */
	@Override
	public Address getUserAddressByAddressType(int userID, AddressType type) throws EJBException, RemoteException {
		try {
			return getAddressHome().findUserAddressByAddressType(userID, type);
		} catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Gets the user's main address and returns it.
	 *
	 * @returns the address if found or null if not.
	 */
	@Override
	public Address getUsersMainAddress(int userID) throws EJBException, RemoteException {
		try {
			return getAddressHome().findPrimaryUserAddress(userID);
		} catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Gets the users main addresses and returns them.
	 *
	 * @returns a collection of addresses if found or null if not.
	 */
	@Override
	public Collection<Address> getUsersMainAddresses(String[] userIDs) throws EJBException, RemoteException {
		try {
			return getAddressHome().findPrimaryUserAddresses(userIDs);
		} catch (FinderException fe) {
			return null;
		}
	}

	@Override
	public Collection<Address> getUsersMainAddresses(IDOQuery query) throws EJBException, RemoteException {
		try {
			return getAddressHome().findPrimaryUserAddresses(query);
		} catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Gets the users main address and returns it.
	 *
	 * @returns the address if found or null if not.
	 */
	@Override
	public Address getUsersMainAddress(User user) throws RemoteException {
		if (user != null) {
			return getUsersMainAddress(((Integer) user.getPrimaryKey()).intValue());
		}

		return null;
	}

	/**
	 * Gets the users co address and returns it.
	 *
	 * @returns the address if found or null if not.
	 */
	@Override
	public Address getUsersCoAddress(User user) throws RemoteException {
		return getUsersCoAddress(((Integer) user.getPrimaryKey()).intValue());
	}

	/**
	 * Gets the users co address and returns it.
	 *
	 * @returns the address if found or null if not.
	 */
	@Override
	public Address getUsersCoAddress(int userId) throws RemoteException {
		AddressType coAddressType = getAddressHome().getAddressType2();
		return getUserAddressByAddressType(userId, coAddressType);
	}

	/**
	 * Gets the users and returns them.
	 *
	 * @returns a collection of users if found or null if not.
	 */
	@Override
	public Collection<User> getUsers(String[] userIDs) throws EJBException, RemoteException {
		try {
			return getUserHome().findUsers(userIDs);
		} catch (FinderException fe) {
			return null;
		}
	}

	@Override
	public Collection<User> getUsers(IDOQuery query) throws EJBException, RemoteException {
		try {
			return getUserHome().findUsersInQuery(query);
		} catch (FinderException fe) {
			return null;
		}
	}

	/**
	 * Method updateUsersMainAddressOrCreateIfDoesNotExist. This method can both be used to update the user main address or to create one <br>
	 * if one does not exist. Only userId and StreetName(AndNumber) are required to be not null others are optional.
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
	@Override
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox) throws CreateException, RemoteException {
		return updateUsersMainAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city, province, poBox, null);
	}

	@Override
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber, PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID) throws CreateException, RemoteException {
		AddressType mainAddressType = getAddressHome().getAddressType1();
		return updateUsersAddressOrCreateIfDoesNotExist(user, streetNameAndNumber, postalCode, country, city, province, poBox, communeID, mainAddressType);
	}

	@Override
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID) throws CreateException, RemoteException {
		AddressType mainAddressType = getAddressHome().getAddressType1();
		return updateUsersAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city, province, poBox, communeID, mainAddressType);
	}

	/**
	 * Method updateUsersCoAddressOrCreateIfDoesNotExist. This method can both be used to update the user co address or to create one <br>
	 * if one does not exist. Only userId and StreetName(AndNumber) are required to be not null others are optional.
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
	@Override
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox) throws CreateException, RemoteException {
		return updateUsersCoAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city, province, poBox, null);
	}

	@Override
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber, PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID) throws CreateException, RemoteException {
		AddressType coAddressType = getAddressHome().getAddressType2();
		return updateUsersAddressOrCreateIfDoesNotExist(user, streetNameAndNumber, postalCode, country, city, province, poBox, communeID, coAddressType);
	}

	@Override
	public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID) throws CreateException, RemoteException {
		AddressType coAddressType = getAddressHome().getAddressType2();
		return updateUsersAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city, province, poBox, communeID, coAddressType);
	}

	private Address updateUsersAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox, Integer communeID, AddressType addressType) throws CreateException, RemoteException {
		try {
			User user = getUser(userId);
			Country country = null;
			if (countryName != null) {
				country = ((CountryHome) getIDOHome(Country.class)).findByCountryName(countryName);
			}
			PostalCode code = null;
			if (postalCodeId != null) {
				if (postalCodeId.intValue() == -1) {
					code = ((PostalCodeHome) getIDOHome(PostalCode.class)).create();
				} else {
					code = ((PostalCodeHome) getIDOHome(PostalCode.class)).findByPrimaryKey(postalCodeId);
				}
			}
			return updateUsersAddressOrCreateIfDoesNotExist(user, streetNameAndNumber, code, country, city, province, poBox, communeID, addressType);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error updating address for user: " + userId, e);
		}
		return null;
	}

	protected Address updateUsersAddressOrCreateIfDoesNotExist(User user, String streetNameAndNumber, PostalCode postalCode, Country country, String city, String province, String poBox, Integer communeID, AddressType addressType) throws CreateException, RemoteException {
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
				if (country != null) {
					address.setCountry(country);
				}
				address.setPostalCode(postalCode);
				address.setProvince(province);
				address.setCity(city);
				address.setPOBox(poBox);
				address.setStreetName(streetName);
				if (streetNumber != null) {
					address.setStreetNumber(streetNumber);
				} else {
					// Fix when entering unnumbered addresses (Aron )
					address.setStreetNumber("");
				}
				if (communeID == null || communeID.intValue() == -1) {
					address.setCommune(null);
				} else {
					address.setCommuneID(communeID.intValue());
				}
				address.store();
				if (addAddress) {
					user.addAddress(address);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to update or create address for userid : " + user.getPrimaryKey());
			}
		} else {
			throw new CreateException("No streetname or user is null!");
		}
		return address;
	}

	@Override
	public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group) throws EJBException, RemoteException {
		User userToUpdate = this.getUser(userId);
		updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID, date_of_birth, primary_group, null);
	}

	@Override
	public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group, String fullname) throws EJBException, RemoteException {
		User userToUpdate = this.getUser(userId);
		updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID, date_of_birth, primary_group, fullname);
	}

	@Override
	public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group) throws EJBException, RemoteException {
		updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID, date_of_birth, primary_group, null);
	}

	@Override
	public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group, String fullname) throws EJBException, RemoteException {
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
		} else if (fullname != null && (userToUpdate.getDisplayName() == null || "".equals(userToUpdate.getDisplayName()))) {
			// set the display name as the full name
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
	@Override
	public Collection<Email> listOfUserEmails(int iUserId) {
		try {
			return this.getEmailHome().findEmailsForUser(iUserId);
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * DEPRECATED Adds email to the given user, and removes older emails if requested
	 *
	 * @deprecated use updateUserMail
	 */
	@Override
	@Deprecated
	public Email storeUserEmail(Integer userID, String emailAddress, boolean replaceExistentRecord) {
		return storeUserEmail(getUser(userID), emailAddress, replaceExistentRecord);
	}

	/**
	 * DEPRECATED Adds email to the given user, and removes older emails if requested if addres is null or empty the no email vill exist any more for the user
	 *
	 * @return null if no email was stored
	 *
	 * @deprecated use updateuserMail
	 */
	@Override
	@Deprecated
	public Email storeUserEmail(User user, String emailAddress, boolean replaceExistentRecord) {
		try {
			return updateUserMail(user, emailAddress);
		} catch (CreateException e) {
			e.printStackTrace();
			return null;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
		//
		// NOTE: the code below was causing corrupted databases and is left here only for documentation what happened.
		// (the code below is sharing emails among users)
		// Do not use this code.
		//
		// try {
		// if (replaceExistentRecord) {
		// removeUserEmails(user);
		// }
		// if (!"".equals(emailAddress)) {
		// Email emailRecord = lookupEmail(emailAddress);
		// if (emailRecord == null) {
		// emailRecord = this.getEmailHome().create();
		// emailRecord.setEmailAddress(emailAddress);
		// emailRecord.store();
		// }
		// user.addEmail(emailRecord);
		// return emailRecord;
		// }
		// }
		// catch (IDOStoreException e) {
		// e.printStackTrace();
		// }
		// catch (IDOAddRelationshipException e) {
		// e.printStackTrace();
		// }
		// catch (CreateException e) {
		// e.printStackTrace();
		// }
		// return null;
	}

	/**
	 * Removes email relations to given user
	 *
	 * @param user
	 * @return true if successfull, else false
	 */
	@Override
	public boolean removeUserEmails(User user) {
		try {
			user.removeAllEmails();
			return true;
		} catch (IDORemoveRelationshipException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @deprecated use updateUserMail
	 */
	@Override
	@Deprecated
	public void addNewUserEmail(int iUserId, String sNewEmailAddress) {
		storeUserEmail(getUser(iUserId), sNewEmailAddress, false);
	}

	/**
	 * @deprecated use getUserGroupsDirectlyRelated(int iUserId)
	 */
	@Override
	@Deprecated
	public Collection<Group> listOfUserGroups(int iUserId) {
		return getUserGroupsDirectlyRelated(iUserId);
	}

	@Override
	public Collection<Group> getUserGroups(int iUserId) throws EJBException {
		try {
			return getUserGroups(this.getUser(iUserId));
			// return
			// getUserGroups(((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId).getGroupID());
		} catch (Exception ex) {
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
	@Override
	public Collection<User> getUsersInGroup(int iGroupId) {
		try {
			// EntityFinder.findRelated(group,com.idega.user.data.UserBMPBean.getStaticInstance());
			return this.getGroupBusiness().getUsers(iGroupId);
		} catch (Exception ex) {
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
	@Override
	public Collection<User> getUsersInGroup(Group aGroup) {
		try {
			int groupID = ((Integer) aGroup.getPrimaryKey()).intValue();
			return getUsersInGroup(groupID);
		} catch (Exception ex) {
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
	@Override
	public Collection<User> getUsers() throws FinderException, RemoteException {
		// Collection l =
		// EntityFinder.findAll(com.idega.user.data.UserBMPBean.getStaticInstance());
		Collection<User> l = this.getUserHome().findAllUsers();
		return l;
	}

	/**
	 * Returns User from userid, throws an unchecked EJBException if not found
	 *
	 * @throws EJBException
	 *             if nothing found or an error occured
	 */
	@Override
	public User getUser(int iUserId) {
		return getUser(new Integer(iUserId));
	}

	/**
	 * Returns User from userid, throws EJBException if not found
	 */
	@Override
	public User getUser(Integer iUserId) {
		try {
			return getUserHome().findByPrimaryKey(iUserId);
		} catch (Exception ex) {
			throw new EJBException("Error getting user for id: " + iUserId.toString() + " Message: " + ex.getMessage());
		}
		// return null;
	}

	/**
	 * Returns User from personal id returns null if not found
	 */
	@Override
	public User getUser(String personalID) throws FinderException {
		return getUserHome().findByPersonalID(personalID);
	}

	/**
	 * Returns User from personal id returns null if not found
	 */
	@Override
	public User findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(String personalID, String first_name, String last_name) throws FinderException {
		return getUserHome().findByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(personalID, first_name, last_name);
	}

	@Override
	public Collection<User> getUsersInNoGroup() throws SQLException {
		// return
		// EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean.getStaticInstance());
		// Collection nonrelated =
		// EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getStaticInstance());
		// return
		// UserGroupBusiness.getUsersForUserRepresentativeGroups(nonrelated);
		throw new java.lang.UnsupportedOperationException("method getUsersInNoGroup() not implemented");
	}

	@Override
	public Collection<Group> getUserGroupsDirectlyRelated(int iUserId) {
		try {
			return getUserGroupsDirectlyRelated(this.getUser(iUserId));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<User> getUsersInPrimaryGroup(Group group) {
		try {
			// return
			// EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean._COLUMNNAME_PRIMARY_GROUP_ID,group.getID());
			return this.getUserHome().findUsersInPrimaryGroup(group);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<Group> getUserGroupsDirectlyRelated(User user) {
		try {
			return getGroupBusiness().getParentGroups(user); // EntityFinder.findRelated(user,com.idega.user.data.GroupBMPBean.getStaticInstance());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets all the groups that are indirect parents (grand parents etc.) of the user with id iUserId
	 *
	 * @param iUserId
	 *            the ID of the user to get indirect parents for
	 * @return Collection of Group entities that are not direct parents of the specified user
	 */
	@Override
	public Collection<Group> getParentGroupsInDirectForUser(int iUserId) {
		// public Collection getUserGroupsNotDirectlyRelated(int iUserId){
		try {
			User user = this.getUser(iUserId);
			/*
			 * Collection isDirectlyRelated = getUserGroupsDirectlyRelated(user); Collection AllRelatedGroups = getUserGroups(user);
			 *
			 * if(AllRelatedGroups != null){ if(isDirectlyRelated != null){ Iterator iter = isDirectlyRelated.iterator(); while (iter.hasNext()) { Object item = iter.next(); AllRelatedGroups.remove(item); //while(AllRelatedGroups.remove(item)){} } } return AllRelatedGroups; }else { return null; }
			 */
			return this.getGroupBusiness().getParentGroupsInDirect(user.getGroupID());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns all the groups that are not a direct parent of the User with id iUserId. That is both groups that are indirect parents of the user or not at all parents of the user.
	 *
	 * @see com.idega.user.business.GroupBusiness#getAllGroupsNotDirectlyRelated(int)
	 * @return Collection of non direct parent groups
	 */
	@Override
	public Collection<Group> getNonParentGroups(int iUserId) {
		try {
			User user = getUser(iUserId);
			/*
			 * Collection isDirectlyRelated = getUserGroupsDirectlyRelated(user); Collection AllGroups = UserGroupBusiness.getAllGroups(); //EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getStaticInstance());
			 *
			 * if(AllGroups != null){ if(isDirectlyRelated != null){ Iterator iter = isDirectlyRelated.iterator(); while (iter.hasNext()) { Object item = iter.next(); AllGroups.remove(item); //while(AllGroups.remove(item)){} } } return AllGroups; }else{ return null; }
			 */
			return getGroupBusiness().getNonParentGroups(user.getGroupID());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets all the groups that the user is in recursively up the group tree with all availble group types.
	 *
	 * @param aUser
	 *            a User to find parent Groups for
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *             If an error occured
	 */
	@Override
	public Collection<Group> getUserGroups(User aUser) throws EJBException {
		// String[] groupTypesToReturn = new String[2];
		// groupTypesToReturn[0] =
		// com.idega.user.data.GroupBMPBean.getStaticInstance().getGroupTypeValue();
		// groupTypesToReturn[1] =
		// com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
		return getUserGroups(aUser, null, false);
	}

	/**
	 * Gets all the groups that the user is in recursively up the group tree filtered with specified groupTypes
	 *
	 * @param aUser
	 *            a User to find parent Groups for
	 * @param groupTypes
	 *            the Groups a String array of group types of which the Groups to be returned must be = *
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *             If an error occured
	 */
	@Override
	public Collection<Group> getUserGroups(User aUser, String[] groupTypes) throws EJBException {
		return getUserGroups(aUser, groupTypes, false);
	}

	/**
	 * Returns recursively up the group tree parents of User aUser with filtered out with specified groupTypes
	 *
	 * @param aUser
	 *            a User to find parent Groups for
	 * @param groupTypes
	 *            the Groups a String array of group types to be filtered with
	 * @param returnSpecifiedGroupTypes
	 *            if true it returns the Collection with all the groups that are of the types specified in groupTypes[], else it returns the opposite (all the groups that are not of any of the types specified by groupTypes[])
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *             If an error occured
	 */
	@Override
	public Collection<Group> getUserGroups(User aUser, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws EJBException {
		try {
			return getGroupBusiness().getParentGroupsRecursive(aUser.getGroup(), groupTypes, returnSepcifiedGroupTypes);
		} catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	@Override
	public GroupBusiness getGroupBusiness() throws RemoteException {
		return getGroupBusiness(this.getIWApplicationContext());
	}

	private GroupBusiness getGroupBusiness(IWApplicationContext iwac) throws RemoteException {
		return IBOLookup.getServiceInstance(iwac, GroupBusiness.class);
	}

	@Override
	public Collection<User> getAllUsersOrderedByFirstName() throws FinderException, RemoteException {
		return this.getUserHome().findAllUsersOrderedByFirstName();
	}

	@Override
	public Email getUsersMainEmail(User user) throws NoEmailFoundException {
		EmailHome home = getEmailHome();
		try {
			return home.findMainEmailForUser(user);
		} catch (FinderException e) {
			String message = null;
			if (user != null) {
				message = user.getName();
			}
			throw new NoEmailFoundException(message);
		} catch (RemoteException e) {
			throw new IBORuntimeException();
		}
	}

	@Override
	public Phone getUsersHomePhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersHomePhone(user);
		} catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	@Override
	public Phone getUsersWorkPhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersWorkPhone(user);
		} catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	@Override
	public Phone getUsersMobilePhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersMobilePhone(user);
		} catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	@Override
	public Phone getUsersFaxPhone(User user) throws NoPhoneFoundException {
		String userString = null;
		try {
			userString = user.getName();
			return getPhoneHome().findUsersFaxPhone(user);
		} catch (Exception e) {
		}
		throw new NoPhoneFoundException(userString);
	}

	/**
	 * @return Correct name of the group or user or empty string if there was an error getting the name. Gets the name of the group and explicitly checks if the "groupOrUser" and if it is a user it returns the correct name of the user. Else it regularely returns the name of the group.
	 */
	@Override
	public String getNameOfGroupOrUser(Group groupOrUser) {
		try {
			String userGroupType = getUserHome().getGroupType();
			if (groupOrUser.getGroupType().equals(userGroupType)) {
				int userID = ((Integer) groupOrUser.getPrimaryKey()).intValue();
				return getUser(userID).getName();
			} else {
				return groupOrUser.getName();
			}
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public UserProperties getUserProperties(User user) throws RemoteException {
		return getUserProperties(((Integer) user.getPrimaryKey()).intValue());
	}

	@Override
	public UserProperties getUserProperties(int userID) {
		UserProperties properties = new UserProperties(getIWApplicationContext().getIWMainApplication(), userID);
		return properties;
	}


	/**
	 * 	Home page precedence is as follows:
	 *	Users direct home page id
	 *	The only home page found from a parent group
	 *	A home page of the preferred role (if only one found).
	 *	If more than one page is still available and the forward page (PROPERTY_FORWARD_PAGE_URI) is set, goto the forward page (if the group also has a role to choose from)
	 *	else goto the first home page in the preferred group home pages list OR the primary group home page
	 *	OR lastly to the first of the none preferred home pages list (pretty random but we can't decide anyway).
	 *
	 * @param user
	 * @return the URI of the page
	 */
	@Override
	public int getHomePageIDForUser(User user){
		IWMainApplicationSettings settings = this.getIWApplicationContext().getIWMainApplication().getSettings();

		//Check if we need to use the role chooser / start page chooser page
		String forwardPage = settings.getProperty(IWAuthenticator.PROPERTY_FORWARD_PAGE_URI);

		//Check if the user has specifically been set to go to a page, this is highly unlikely but should override everything else.
		int homePageID = user.getHomePageID();
		if (homePageID > 0) {
			return homePageID;
		}

		//Gather all the users groups and see if any of them have home pages.
		ICRole preferredRole = user.getPreferredRole();
		String preferredRoleKey = (preferredRole!=null)? preferredRole.getRoleKey() : null;
		Collection<Integer> homepages = new ArrayList<Integer>();
		Collection<Integer> homePagesOfPreferredRole = new ArrayList<Integer>();
		Collection<Integer> homepagesWithRolesNotPreferred = new ArrayList<Integer>();

		Collection<Group> groups = user.getParentGroups();

		//collect home pages
		for (Group group : groups) {
			if (group.getHomePageID() > 0) {
				Integer pageID = new Integer(group.getHomePageID());

				Collection<String> allRolesForGroup = this.getIWApplicationContext().getIWMainApplication().getAccessController().getAllRolesKeysForGroup(group);
				if( !ListUtil.isEmpty(allRolesForGroup)){
					if(preferredRoleKey!=null && allRolesForGroup.contains(preferredRoleKey)){
						homePagesOfPreferredRole.add(pageID);
					}
					else{
						homepagesWithRolesNotPreferred.add(pageID);
					}
				}
				if(!homepages.contains(pageID)) {
					homepages.add(pageID);
				}
			}
		}

		if(!ListUtil.isEmpty(homepages) && homepages.size()==1){
			return ((homepages.iterator().next())).intValue();
		}


		//preferred role
		if(!ListUtil.isEmpty(homePagesOfPreferredRole)){
			if(homePagesOfPreferredRole.size()==1){
				return ((homePagesOfPreferredRole.iterator().next())).intValue();
			}
		}

		//if homePagesOfPreferredRole is none empty it also is bigger then 1 because of the check above.
		boolean moreThanOneRoleWithPage = (!ListUtil.isEmpty(homePagesOfPreferredRole) || (!ListUtil.isEmpty(homepagesWithRolesNotPreferred) && homepagesWithRolesNotPreferred.size()>1));

		//Check if there was a forward page specified for when there may be more than one page / role to choose from
		if(forwardPage!=null && moreThanOneRoleWithPage ){
			try {
				ICPageHome pageHome = (ICPageHome)IDOLookup.getHome(ICPage.class);
				ICPage page = pageHome.findExistingByUri(forwardPage,this.getIWApplicationContext().getDomain().getID());
				return ((Integer)page.getPrimaryKey()).intValue();
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch(FinderException fe) {
				fe.printStackTrace();
			}
		}
		else{

			//Last resort we use the first page of the preferred role home pages or home pages (if any)
			if(!ListUtil.isEmpty(homePagesOfPreferredRole)){
				return ((homePagesOfPreferredRole.iterator().next())).intValue();
			}

			//use the primary groups home page if any
			Group primary = user.getPrimaryGroup();
			if(primary!=null){
				int homePageId = primary.getHomePageID();
				if(homePageId>0){
					return homePageId;
				}

			}

			if(!ListUtil.isEmpty(homepages)){
				return ((homepages.iterator().next())).intValue();
			}
		}


		return -1;
	}

	/**
	 * @return the id of the homepage for the user if it is set, else it throws a javax.ejb.FinderException Finds the homepage set for the user, if none is set it checks on the homepage set for the users primary group, else it throws a javax.ejb.FinderException
	 */
	@Override
	public com.idega.core.builder.data.ICPage getHomePageForUser(User user) throws javax.ejb.FinderException {
		try {
			int homeID = getHomePageIDForUser(user);
			if (homeID != -1) {
				return getIBPageHome().findByPrimaryKey(homeID);
			} else {
				throw new javax.ejb.FinderException("No homepage found for user");
			}
		} catch (Exception e) {
			throw new javax.ejb.FinderException("Error finding homepage for user. Error was:" + e.getClass().getName() + " with message: " + e.getMessage());
		}
	}

	protected ICPageHome getIBPageHome() throws java.rmi.RemoteException {
		return (ICPageHome) com.idega.data.IDOLookup.getHome(ICPage.class);
	}

	@Override
	public AddressBusiness getAddressBusiness() throws RemoteException {
		return getServiceInstance(AddressBusiness.class);
	}

	/**
	 * Cast a Group that is a "UserReresentative" Group to a User instance.
	 *
	 * @param userGroups
	 *            An instance of a Group that is really a "UserReresentative" group i.e. the Group representation of the User
	 * @param userGroup
	 *            A instnance of a Group that is really a "UserReresentative" group i.e. the Group representation of the User
	 * @return User
	 * @throws EJBException
	 *             If an error occurs casting
	 */
	@Override
	public User castUserGroupToUser(Group userGroup) throws EJBException {
		try {
			if (userGroup instanceof User) {
				return (User) userGroup;
			} else {
				// try{
				return this.getUserHome().findUserForUserGroup(userGroup);
				// }
				// catch(FinderException e){
				// if(userGroup.isUser()){
				// return
				// this.getUserHome().findByPrimaryKey(userGroup.getPrimaryKey());
				// }
				// }
			}
		} catch (Exception e) {
			throw new IBORuntimeException(e);
		}
		// throw new IBORuntimeException("Error find user for group
		// "+userGroup.toString());
	}

	@Override
	public boolean hasUserLogin(User user) throws RemoteException {
		LoginTable lt = LoginDBHandler.getUserLogin(((Integer) user.getPrimaryKey()).intValue());
		return lt != null;
	}

	@Override
	public boolean hasUserLogin(int userID) throws RemoteException {
		LoginTable lt = LoginDBHandler.getUserLogin(userID);
		return lt != null;
	}

	@Override
	public Group getUsersHighestTopGroupNode(User user, List<String> groupTypes, IWUserContext iwuc) throws RemoteException {
		Map<String, Group> groupTypeGroup = new HashMap<String, Group>();
		Collection<Group> topNodes = getUsersTopGroupNodesByViewAndOwnerPermissions(user, iwuc);
		Iterator<Group> iterator = topNodes.iterator();
		while (iterator.hasNext()) {
			Group group = iterator.next();
			String groupType = group.getGroupType();
			groupTypeGroup.put(groupType, group);
		}
		Iterator<String> typeIterator = groupTypes.iterator();
		while (typeIterator.hasNext()) {
			String groupType = typeIterator.next();
			if (groupTypeGroup.containsKey(groupType)) {
				return groupTypeGroup.get(groupType);
			}
		}
		return null;
	}

	/**
	 * Checks if a group is under a user's top group node. This can be used to check if the user is allowed to view the group.
	 *
	 * @param iwc
	 *            IWUserContext
	 * @param group
	 *            The group to check
	 * @param user
	 *            The user to check the group for
	 * @return returns true if any of <code>user</code> s top group nodes is an ancestor of <code>group</code>, false otherwise.
	 */
	@Override
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user) throws RemoteException {
		Collection<Group> topGroupNodes = null;
		try {
			topGroupNodes = getUsersTopGroupNodesByViewAndOwnerPermissions(user, iwc);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (topGroupNodes == null || topGroupNodes.isEmpty()) {
			return false;
		} else {
			// System.out.println("Checking if group " + group.getName() + " is
			// under a top group (" + topGroupNodes.size() + ") for user " +
			// user.getName());
			return isGroupUnderUsersTopGroupNode(iwc, group, user, topGroupNodes);
		}
	}

	/**
	 * Helper method for {@link #isGroupUnderUsersTopGroupNode(IWUserContext, Group, User)}.
	 *
	 * @see #isGroupUnderUsersTopGroupNode(IWUserContext, Group, User)
	 */
	@Override
	public boolean isGroupUnderUsersTopGroupNode(IWUserContext iwc, Group group, User user, Collection<Group> topGroupNodes) {
		boolean found = false; // whether ancestry with a top group node is
		// found or not
		if (group != null && topGroupNodes.contains(group)) {
			// System.out.println("found top group ancestor " +
			// group.getName());
			found = true;
		} else {
			List<Group> parents = group.getParentGroups();
			if (parents != null) {
				Iterator<Group> parentIt = parents.iterator();
				while (parentIt.hasNext() && !found) {
					Group parent = parentIt.next();
					// System.out.println("checking group for top group in ancestors
					// " + parent.getName());
					try {
						found = isGroupUnderUsersTopGroupNode(iwc, parent, user, topGroupNodes);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (found) {
						break;
					}
				}
			}
		}
		return found;
	}

	/*private Collection searchForTopNodes(Collection permissions, Collection initialTopNodes, User user) throws EJBException, RemoteException, FinderException {
		if (permissions == null || permissions.isEmpty()) {
			return ListUtil.getEmptyList();
		}
		NestedSetsContainer groupTree = getGroupBusiness().getLastGroupTreeSnapShot();
		Map topNodesSubTrees = new HashMap();
		Iterator iter = permissions.iterator();
		if (initialTopNodes == null || initialTopNodes.isEmpty()) {
			String groupPK = ((ICPermission) iter.next()).getContextValue(); // this
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
			topNodesSubTrees.put(groupPK, groupTree.subSet(groupPK)); // get the
			// first
			// topnode's
			// subtree
		} else {
			for (Iterator iterator = initialTopNodes.iterator(); iterator.hasNext();) {
				String groupPK = String.valueOf(((Group) iterator.next()).getPrimaryKey());
				topNodesSubTrees.put(groupPK, groupTree.subSet(groupPK));
			}
		}
		Set topNodeSubTreesEntrySet = topNodesSubTrees.entrySet();
		while (iter.hasNext()) {
			ICPermission perm = (ICPermission) iter.next();
			if (!perm.getPermissionValue()) {
				continue;// we don't want to use this permission if is a
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
				} else {
					log("[UserBusinessBean]: Topnode " + sGroup + " dose not exist in the tree but is set in the permissions for the user " + user);
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
			} else if (group.isAlias()) {
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
	}*/

	/*private Collection searchForTopNodesFromTheTop(Collection possibleGroups) throws IDORelationshipException, RemoteException, FinderException {
		Collection domainTopNodes = this.getIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
		Collection topNodes = new ArrayList();
		HashSet prosessedGroups = new HashSet();
		collectTopNodesForSubtree(domainTopNodes, possibleGroups, topNodes, prosessedGroups);
		return topNodes;
	}*/

	/**
	 *
	 * @param childGroups
	 * @param possibleGroups
	 *            collection of prosessed groups to prevent endless recusion
	 * @param topNodes
	 * @param prosessedGroups
	 */
	/*private void collectTopNodesForSubtree(Collection childGroups, Collection possibleGroups, Collection topNodes, Set prosessedGroups) {
		for (Iterator iter = childGroups.iterator(); iter.hasNext();) {
			Group gr = (Group) iter.next();
			if (!prosessedGroups.contains(gr)) {
				prosessedGroups.add(gr);
				if (possibleGroups.contains(gr)) {
					topNodes.add(gr);
					continue;
				} else {
					collectTopNodesForSubtree(gr.getChildGroups(), possibleGroups, topNodes, prosessedGroups);
				}
			}
		}
	}*/

	@Override
	public boolean hasTopNodes(User user, IWUserContext iwuc) {
		try {
			// super admin check is done first
			boolean isSuperUser = iwuc.isSuperAdmin();
			if ((isSuperUser && user == null) || (isSuperUser && iwuc.getCurrentUser().equals(user))) {
				return true;
			} else {
				Collection<Group> topNodes = getUsersTopGroupNodesByViewAndOwnerPermissions(user, iwuc);
				return !ListUtil.isEmpty(topNodes);
			}
		} catch (RemoteException re) {
			return false;
		}
	}

	@Override
	public Collection<Group> getUsersTopGroupNodesByViewAndOwnerPermissionsInThread(User user, Collection<Group> sessionTopNodes, boolean isSuperUser, User currentUser) throws RemoteException {
		Collection<Group> topNodes = new ArrayList<Group>();
		//check for the super user case first
		if ((isSuperUser && user == null) || (isSuperUser && currentUser.equals(user))) {
			try {
				topNodes = this.getIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
				return topNodes;
			}
			catch (Exception e1) {
				topNodes = new ArrayList<Group>();
				e1.printStackTrace();
			}
		}
		if (user != null) {
			topNodes = sessionTopNodes;
			if (topNodes != null && !topNodes.isEmpty()) {
				return topNodes;
			}
			topNodes = getStoredTopNodeGroups(user);
			if (topNodes != null && !topNodes.isEmpty()) {
				return topNodes;
			}
			else {
				log("[UserBusinessBean]: getUsersTopGroupNodesByViewAndOwnerPermissions(...) begins");
				Timer totalTime = new Timer();
				totalTime.start();
				Collection<Group> allViewAndOwnerPermissionGroups = new ArrayList<Group>();
				try {
					GroupBusiness groupBiz = getGroupBusiness();
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
					addGroupPKsToCollectionFromICPermissionCollection(viewPermissions, allViewAndOwnerPermissionGroupPKs);
					Collection ownedPermissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
					//allViewAndOwnerPermissions.removeAll(ownedPermissions);//no
					// double entries thank you
					addGroupPKsToCollectionFromICPermissionCollection(ownedPermissions, allViewAndOwnerPermissionGroupPKs);
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
					log("[UserBusinessBean]: using old topnode search");
					//get all (recursively) parents for permission
					Iterator<Group> permissions = allViewAndOwnerPermissionGroups.iterator();
					Map cachedParents = new HashMap();
					Map<String, Group> cachedGroups = new HashMap<String, Group>();
					while (permissions.hasNext()) {
						Group group = permissions.next();
						if (group != null) {
							Integer primaryKey = (Integer) group.getPrimaryKey();
							if (!groupMap.containsKey(primaryKey)) {
								Group permissionGroup = group;
								if (!cachedGroups.containsKey(primaryKey.toString())) {
									cachedGroups.put(primaryKey.toString(), permissionGroup);
								}
								Collection recParents = groupBiz.getParentGroupsRecursive(permissionGroup, cachedParents, cachedGroups);
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
						else {
							System.out.println("Group in permissions collection = " + group);
							System.out.println("Content of permissions collection = " + permissions);
						}
					}
					time.stop();
					log("[UserBusinessBean]: getting all parents (recursively) complete " + time.getTimeString());
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
							if (thePermissionGroupsId.equals(groupToCompareTo) || skipThese.containsKey(thePermissionGroupsId)) {
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
									Collection aliasesRecursive = getGroupBusiness().getChildGroupsRecursiveResultFiltered(getGroupBusiness().getGroupByGroupID(topNodeId.intValue()), aliasGroupType, true);
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
						log("[UserBusinessBean]: remove the aliases undr another top node complete " + time.getTimeString());
					}
					//finally done! the remaining nodes are the top
					// nodes
					topNodes = groupMap.values();
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
		}
		return topNodes;
	}

	/**
	 * Returns a collection of Groups that are this users top nodes. The nodes that he has either view or owner permissions to <br>
	 * To end up with only the top nodes we do the following: <br>
	 * For each group (key) in the parents Map we check if that group is contained within any of <br>
	 * the other groups' parents. If another group has this group as a parent it is removed and its parent list <br>
	 * and we move on to the next key. This way the map we iterate through will always get smaller until only the <br>
	 * top node groups are left.
	 *
	 * Finally we check for the special case that the remaining top nodes have a shortcut that is not a top node <br>
	 * and if so we need to remove that node unless there is only one node left or if the alias and the real group <br>
	 * are both top nodes.
	 *
	 * @param user
	 * @return @throws RemoteException
	 */
	@Override
	public Collection<Group> getUsersTopGroupNodesByViewAndOwnerPermissions(User user, IWUserContext iwuc) throws RemoteException {
		Collection<Group> topNodes = new ArrayList<Group>();
		// check for the super user case first
		boolean isSuperUser = iwuc.isSuperAdmin();
		if ((isSuperUser && user == null) || (isSuperUser && iwuc.getCurrentUser().equals(user))) {
			try {
				topNodes = this.getIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
				return topNodes;
			} catch (Exception e1) {
				topNodes = new ArrayList<Group>();
				e1.printStackTrace();
			}
		}
		if (user != null) {
			topNodes = (Collection<Group>) iwuc.getSessionAttribute(SESSION_KEY_TOP_NODES + user.getPrimaryKey().toString());
			if (topNodes != null && !topNodes.isEmpty()) {
				return topNodes;
			}
			topNodes = getStoredTopNodeGroups(user);
			if (topNodes != null && !topNodes.isEmpty()) {
				iwuc.setSessionAttribute(SESSION_KEY_TOP_NODES + user.getPrimaryKey().toString(), topNodes);
				return topNodes;
			} else {
				log("[UserBusinessBean]: getUsersTopGroupNodesByViewAndOwnerPermissions(...) begins");
				Timer totalTime = new Timer();
				totalTime.start();
				Collection<Group> allViewAndOwnerPermissionGroups = new ArrayList<Group>();
				try {
					GroupBusiness groupBiz = getGroupBusiness();
					/*if (false) {// (groupBiz.userGroupTreeImageProcedureTopNodeSearch())
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
						// get all view permissions for direct parent and put in
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
							// topNodes =
							// searchForTopNodes(viewPermissions,searchForTopNodes(ownedPermissions,null));
							for (Iterator iter = topNodes.iterator(); iter.hasNext();) {
								Group gr = (Group) iter.next();
								if (gr.isAlias() && topNodes.contains(gr.getAlias())) {
									iter.remove();
								}
							}
							time.stop();
							log("[UserBusinessBean]: searchForTopNodesFromTheTop complete " + time.getTimeString());
						} catch (RemoteException e1) {
							throw new EJBException(e1);
						} catch (FinderException e1) {
							throw new EJBException(e1);
						}
					}*/
					if(true) {
						log("[UserBusinessBean]: not using stored procedure topnode search");
						Timer time = new Timer();
						time.start();
						Map<Integer, Map<Integer, Group>> parents = new HashMap<Integer, Map<Integer, Group>>();
						Map<Integer, Group> groupMap = new HashMap<Integer, Group>();// we need it to be
						// synchronized so we can
						// remove items while in a
						// iterator
						Map<Integer, Integer> aliasMap = new HashMap<Integer, Integer>();
						IDOUtil idoUtil = IDOUtil.getInstance();
						GroupHome grHome = getGroupHome();
						Collection<Group> directlyRelatedParents = getGroupBusiness().getParentGroups(user);
						Iterator<Group> iterating = directlyRelatedParents.iterator();
						List<Group> additionalGroups = new ArrayList<Group>();
						while (iterating.hasNext()) {
							Group parent = iterating.next();
							if (parent != null && parent.getPermissionControllingGroupID() > 0) {
								additionalGroups.add(parent.getPermissionControllingGroup());
							}
						}
						directlyRelatedParents.addAll(additionalGroups);
						Collection<Object> allViewAndOwnerPermissionGroupPKs = new ArrayList<Object>();
						// get all view permissions for direct parent and put in
						// a list
						Collection<ICPermission> viewPermissions = AccessControl.getAllGroupViewPermissionsLegacy(directlyRelatedParents);
						addGroupPKsToCollectionFromICPermissionCollection(viewPermissions, allViewAndOwnerPermissionGroupPKs);
						Collection<ICPermission> ownedPermissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
						// allViewAndOwnerPermissions.removeAll(ownedPermissions);//no
						// double entries thank you
						addGroupPKsToCollectionFromICPermissionCollection(ownedPermissions, allViewAndOwnerPermissionGroupPKs);
						try {
							allViewAndOwnerPermissionGroups = grHome.findByPrimaryKeyCollection(allViewAndOwnerPermissionGroupPKs);
						} catch (FinderException e) {
							log("UserBusiness: In getUsersTopGroupNodesByViewAndOwnerPermissions. groups not found");
							e.printStackTrace();
						}
						time.stop();
						log("[UserBusinessBean]: getting permission groups complete " + time.getTimeString());
						time.start();
						// searchForTopNodesFromTop=3000; //some suitable value
						/*if (false) {// (allViewAndOwnerPermissionGroups.size() >
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
							} catch (IDORelationshipException e1) {
								throw new EJBException(e1);
							} catch (RemoteException e1) {
								throw new EJBException(e1);
							} catch (FinderException e1) {
								throw new EJBException(e1);
							}
						}*/
						if(true) {
							log("[UserBusinessBean]: using old topnode search");
							// get all (recursively) parents for permission
							Iterator<Group> permissions = allViewAndOwnerPermissionGroups.iterator();
							Map<String, Collection<Integer>> cachedParents = new HashMap<String, Collection<Integer>>();
							Map<String, Group> cachedGroups = new HashMap<String, Group>();
							while (permissions.hasNext()) {
								Group group = permissions.next();
								if (group != null) {
									Integer primaryKey = (Integer) group.getPrimaryKey();
									if (!groupMap.containsKey(primaryKey)) {
										Group permissionGroup = group;
										if (!cachedGroups.containsKey(primaryKey.toString())) {
											cachedGroups.put(primaryKey.toString(), permissionGroup);
										}
										Collection<Group> recParents = groupBiz.getParentGroupsRecursive(permissionGroup, cachedParents, cachedGroups);
										Map<Integer, Group> parentMap = idoUtil.convertIDOEntityCollectionToMapOfPrimaryKeysAndEntityValues(recParents);
										parents.put(primaryKey, parentMap);
										groupMap.put(primaryKey, permissionGroup);
										// if it's an alias we don't need the
										// original group and make a list of those
										// groups to filter out later
										if (permissionGroup.isAlias()) {
											Integer originalGroupID = new Integer(permissionGroup.getAliasID());
											aliasMap.put(originalGroupID, primaryKey);
										}
									}
								} else {
									LOGGER.warning("Group in permissions collection = " + group);
									LOGGER.warning("Content of permissions collection = " + permissions);
								}
							}
							time.stop();
							log("[UserBusinessBean]: getting all parents (recursively) complete " + time.getTimeString());
							time.start();
							// Filter out the real top nodes!
							Map<Integer, Boolean> skipThese = new HashMap<Integer, Boolean>();
							Set<Integer> keys = parents.keySet();
							for (Iterator<Integer> iter = keys.iterator(); iter.hasNext();) {
								Integer thePermissionGroupsId = iter.next();

								for (Iterator<Integer> iter2 = parents.keySet().iterator(); iter2.hasNext();) {
									Integer groupToCompareTo = iter2.next();
									// If this group was already checked or is
									// the same as the comparing group, continue
									// (skip this one)
									if (thePermissionGroupsId.equals(groupToCompareTo) || skipThese.containsKey(thePermissionGroupsId)) {
										continue;// dont check for self
									}
									// Get the parents to see if
									// thePermissionGroupsId is in it. ergo it
									// is a parent of the comparing group and
									// therefor a higher node
									Map<Integer, Group> theParents = parents.get(groupToCompareTo);
									// or the permissiongroup has a shortcut
									if (theParents != null && theParents.containsKey(thePermissionGroupsId)) {
										// it's a parent of the comparing group
										// so we don't have to check the
										// comparing group again
										skipThese.put(groupToCompareTo, Boolean.TRUE);// for
										// the
										// check
										// skip
										// check
										groupMap.remove(groupToCompareTo);// the
										// groups
										// that
										// will
										// be
										// left
										// are
										// the
										// top
										// nodes
									}// remove if this group is a child group of
									// myGroup
								}// inner while ends
							}// outer while ends
							time.stop();
							log("[UserBusinessBean]: filter out the real topnodes complete " + time.getTimeString());
							time.start();
							// Now we have to check if the remaining top nodes
							// have a shortcut
							// that is not a top node and if so we need to
							// remove that node
							// unless there is only one node left or if the
							// alias and the real group are both top nodes
							if (groupMap != null && !groupMap.isEmpty()) {
								List<String> aliasGroupType = new ArrayList<String>();
								aliasGroupType.add("alias");
								if (!aliasMap.isEmpty()) {
									for (Iterator<Integer> keyIter = groupMap.keySet().iterator(); keyIter.hasNext();) {
										Integer topNodeId = keyIter.next();
										Integer aliasGroupsId = aliasMap.get(topNodeId);
										if (aliasGroupsId != null) {
											if (!groupMap.containsKey(aliasGroupsId)) {// only
												// remove
												// if
												// they
												// are
												// not
												// both
												// top
												// nodes
												// groupMap.remove(topNodeId);
												System.err.println("Here is the code that once returned concurrentException");
											}
										}
									}
								}
								time.stop();
								log("[UserBusinessBean]: some alias complete " + time.getTimeString());
								time.start();
								// check the children recursively
								List<Integer> groupsToRemove = new ArrayList<Integer>();
								for (Iterator<Integer> keyIter = groupMap.keySet().iterator(); keyIter.hasNext();) {
									Integer topNodeId = keyIter.next();
									if (skipThese.containsKey(topNodeId)) {
										continue;// it's going to be removed
										// later
									} else {
										try {
											// also we need to check the
											// children of the current top nodes
											// recursively for aliases :s
											Group group = getGroupBusiness().getGroupByGroupID(topNodeId.intValue());
											Collection<Group> aliasesRecursive = getGroupBusiness().getChildGroupsRecursiveResultFiltered(group, aliasGroupType, true);
											if (aliasesRecursive != null && !aliasesRecursive.isEmpty()) {
												for (Iterator<Group> aliasIter = aliasesRecursive.iterator(); aliasIter.hasNext();) {
													Group alias = aliasIter.next();
													Integer aliasGroupsId = new Integer(alias.getAliasID());
													if (groupMap.containsKey(aliasGroupsId)) {// only
														// remove
														// if
														// they
														// are
														// not
														// both
														// top
														// nodes
														groupsToRemove.add(aliasGroupsId);
														skipThese.put(aliasGroupsId, Boolean.TRUE);
													}
												}
											}
										} catch (FinderException e1) {
											e1.printStackTrace();
										}
									}
								}
								time.stop();
								log("[UserBusinessBean]: check children (recursively) complete " + time.getTimeString());

								time.start();
								// remove the top nodes that have aliases under
								// another top node, or itself to avoid crashing
								// the server in an endless loop?
								for (Iterator<Integer> removeIter = groupsToRemove.iterator(); removeIter.hasNext();) {
									groupMap.remove(removeIter.next());
								}
								time.stop();
								log("[UserBusinessBean]: remove the aliases undr another top node complete " + time.getTimeString());
							}
							// finally done! the remaining nodes are the top
							// nodes
							topNodes = groupMap.values();
						}
					}
				} catch (EJBException e) {
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

	@Override
	public void addGroupPKsToCollectionFromICPermissionCollection(Collection<ICPermission> ICPermissionSRC, Collection<Object> GroupDEST) {
		GroupHome grHome = getGroupHome();
		for (Iterator<ICPermission> iter = ICPermissionSRC.iterator(); iter.hasNext();) {
			ICPermission perm = iter.next();
			if (!perm.getPermissionValue()) {
				continue;// we don't want to use this permission if is a
				// negative permission (a NOT permission)
			}
			try {
				String groupId = perm.getContextValue();
				Object grPK = grHome.decode(groupId);
				GroupDEST.add(grPK);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public Collection<Group> getStoredTopNodeGroups(User user) {
		try {
			return getTopNodeGroupHome().getTopNodeGroups(user);
		} catch (IDORelationshipException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<TopNodeGroup> getStoredTopGroupNodes(User user) {
		try {
			return getTopNodeGroupHome().findByUser(user);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void removeStoredTopGroupNodes(User user) throws RemoveException {
		Collection<TopNodeGroup> oldNodes = getStoredTopGroupNodes(user);
		if (oldNodes != null && !oldNodes.isEmpty()) {
			for (Iterator<TopNodeGroup> iter = oldNodes.iterator(); iter.hasNext();) {
				TopNodeGroup topnode = iter.next();
				topnode.remove();
			}
		}
	}

	/**
	 * Stores the given group top nodes to the user, by first removing all previously stored top nodes from the user
	 *
	 * @param user
	 * @param nodeGroupIds
	 * @param comment
	 */
	@Override
	public boolean storeUserTopGroupNodes(User user, Collection<Group> nodeGroups, int numberOfPermissions, String totalLoginTime, String comment) {
		javax.transaction.TransactionManager transactionManager = com.idega.transaction.IdegaTransactionManager.getInstance();
		try {
			transactionManager.begin();
			removeStoredTopGroupNodes(user);
			Integer userID = (Integer) user.getPrimaryKey();
			for (Iterator<Group> iter = nodeGroups.iterator(); iter.hasNext();) {
				Integer groupID = (Integer) iter.next().getPrimaryKey();
				TopNodeGroup topNode = getTopNodeGroupHome().create(userID, groupID);
				if (comment != null) {
					topNode.setComment(comment);
				}
				topNode.setLastChanged(IWTimestamp.getTimestampRightNow());
				topNode.setNumberOfPermissions(new Integer(numberOfPermissions));
				topNode.setLoginDuration(totalLoginTime);
				topNode.store();
			}
			transactionManager.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			try {
				transactionManager.rollback();
			} catch (javax.transaction.SystemException sy) {
				sy.printStackTrace(System.err);
			}
			// this is an unusual error therefore localization is it not
			// necessary
		}
		return false;
	}

	/**
	 * Returns a collection of Groups. The groups that he has edit permissions to <br>
	 *
	 * @param user
	 * @return @throws RemoteException
	 */
	@Override
	public Collection<Group> getAllGroupsWithEditPermission(User user, IWUserContext iwuc) {
		Collection<Object> resultGroups = new TreeSet<Object>(); // important to use Set so
		// there will not be any
		// doubles
		GroupHome grHome = getGroupHome();
		// GroupBusiness groupBiz = null;
		// try {
		// groupBiz = getGroupBusiness();
		// }
		// catch (RemoteException ex) {
		// throw new RuntimeException(ex.getMessage());
		// }
		Collection<ICPermission> permissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
		List<Group> parentGroupsList = user.getParentGroups();
		Collection<ICPermission> editPermissions = null;
		try {
			editPermissions = AccessControl.getAllGroupEditPermissionsLegacy(parentGroupsList);
		} catch (IDOLookupException e2) {
			e2.printStackTrace();
		} catch (RemoteException e2) {
			e2.printStackTrace();
		}
		// permissions.removeAll(editPermissions); // avoid double entries
		// permissions.addAll(editPermissions);
		Collection<ICPermission> allPermissions = new ArrayList<ICPermission>();
		allPermissions.addAll(permissions);
		if (editPermissions != null) {
			allPermissions.addAll(editPermissions);
		}
		for (Iterator<ICPermission> iterator = allPermissions.iterator(); iterator.hasNext();) {
			ICPermission perm = iterator.next();
			try {
				String groupId = perm.getContextValue();
				// Group group =
				// groupBiz.getGroupByGroupID(Integer.parseInt(groupId));
				// resultGroups.add(group);
				Object grPK = grHome.decode(groupId);
				resultGroups.add(grPK);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			// catch (FinderException e1) {
			// System.out.println("UserBusiness: In
			// getAllGroupsWithEditPermission. group not
			// found"+perm.getContextValue());
			// }
			// catch (RemoteException ex) {
			// throw new RuntimeException(ex.getMessage());
			// }
		}
		try {
			return grHome.findByPrimaryKeyCollection(resultGroups);
		} catch (FinderException e) {
			LOGGER.log(Level.WARNING, "UserBusiness: In getAllGroupsWithEditPermission. groups not found", e);
			return ListUtil.getEmptyList();
		}
	}

	/**
	 * Returns a collection of Groups. The groups that he has view permissions to <br>
	 *
	 * @param user
	 * @return @throws RemoteException
	 */
	@Override
	public Collection<Group> getAllGroupsWithViewPermission(User user, IWUserContext iwuc) {
		Collection<Object> resultGroups = new TreeSet<Object>(); // important to use Set so
		// there will not be any
		// doubles
		// Group userGroup = null;
		// GroupBusiness groupBiz = null;
		GroupHome grHome = getGroupHome();
		// try {
		// groupBiz = getGroupBusiness();
		// }
		// catch (RemoteException ex) {
		// throw new RuntimeException(ex.getMessage());
		// }
		Collection<ICPermission> permissions = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
		List<Group> parentGroupsList = user.getParentGroups();
		Collection<ICPermission> viewPermissions = null;
		try {
			viewPermissions = AccessControl.getAllGroupViewPermissionsLegacy(parentGroupsList);
		} catch (IDOLookupException e2) {
			e2.printStackTrace();
		} catch (RemoteException e2) {
			e2.printStackTrace();
		}
		// permissions.removeAll(editPermissions); // avoid double entries
		// permissions.addAll(viewPermissions); //Sigtryggur: this caused an
		// error because both of the collection are now prefetched and are
		// therefore IDOPrimaryKeyLists, not normal collections
		Collection<ICPermission> allPermissions = new ArrayList<ICPermission>();
		allPermissions.addAll(permissions);
		if (viewPermissions != null) {
			allPermissions.addAll(viewPermissions);
		}
		// Collection allPermissions = null;
		// try {
		// allPermissions = IDOEntityList.merge(permissions,viewPermissions);
		// } catch (FinderException e) {
		// System.out.println("UserBusiness: In getAllGroupsWithEditPermission.
		// merge failed");
		// e.printStackTrace();
		// return ListUtil.getEmptyList();
		// }
		for (Iterator<ICPermission> iterator = allPermissions.iterator(); iterator.hasNext();) {
			ICPermission perm = iterator.next();
			try {
				String groupId = perm.getContextValue();
				// Group group =
				// groupBiz.getGroupByGroupID(Integer.parseInt(groupId));
				// resultGroups.add(group);
				Object grPK = grHome.decode(groupId);
				resultGroups.add(grPK);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			// catch (FinderException e1) {
			// System.out.println("UserBusiness: In
			// getAllGroupsWithEditPermission. group not
			// found"+perm.getContextValue());
			// }
			// catch (RemoteException ex) {
			// throw new RuntimeException(ex.getMessage());
			// }
		}
		try {
			return grHome.findByPrimaryKeyCollection(resultGroups);
		} catch (FinderException e) {
			LOGGER.log(Level.WARNING, "UserBusiness: In getAllGroupsWithEditPermission. groups not found", e);
			return ListUtil.getEmptyList();
		}
	}

	@Override
	public Map<Integer, String> moveUsers(IWUserContext iwuc, Collection<String> userIds, Group parentGroup, int targetGroupId) {
		return moveUsers(iwuc, userIds, parentGroup, targetGroupId, false, false);
	}

	@Override
	public Map<Integer, String> moveUsers(IWUserContext iwuc, Collection<String> userIds, Group parentGroup, int targetGroupId,
			boolean leaveCopyOfUserInCurrentGroup) {
		return moveUsers(iwuc, userIds, parentGroup, targetGroupId, leaveCopyOfUserInCurrentGroup, false);
	}

	@Override
	public Map<Integer, String> moveUsers(IWUserContext iwuc, Collection<String> userIds, Group parentGroup, int targetGroupId, boolean leaveCopyOfUserInCurrentGroup, boolean copyOrMoveUserInfo) {
		IWMainApplication application = getIWApplicationContext().getIWMainApplication();
		IWBundle bundle = application.getBundle("com.idega.user");
		Locale locale = iwuc.getCurrentLocale();

		IWResourceBundle iwrb = bundle.getResourceBundle(locale);
		Map<Integer, String> result = new HashMap<Integer, String>();
		// check if the source and the target are the same
		if (parentGroup != null) {
			int parentGroupId = ((Integer) parentGroup.getPrimaryKey()).intValue();
			// target and source are the same do nothing
			if (parentGroupId == targetGroupId) {
				String message = iwrb.getLocalizedString("user_source_and_target_are_the_same", "Source group and target group are the same");
				// fill the result map
				Iterator<String> iterator = userIds.iterator();
				while (iterator.hasNext()) {
					String userIdAsString = iterator.next();
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
			// check if we have editpermissions for the targetgroup
			if (!getAccessController().hasEditPermissionFor(targetGroup, iwuc)) {
				// fill the result map
				Iterator<String> iterator = userIds.iterator();
				while (iterator.hasNext()) {
					String userIdAsString = iterator.next();
					Integer userId = new Integer(userIdAsString);
					result.put(userId, iwrb.getLocalizedString("no_edit_permission_for_target_group", "You do not have edit permission for the target group"));
				}
				return result;
			}
		} catch (FinderException ex) {
			throw new EJBException("Error getting group for id: " + targetGroupId + " Message: " + ex.getMessage());
		} catch (RemoteException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		String userIsAlreadyAMemberOfTheGroupMessage = iwrb.getLocalizedString("user_already_member_of_the_target_group", "The user is already a member of the target group");
		// finally perform moving
		Iterator<String> iterator = userIds.iterator();
		while (iterator.hasNext()) {
			String message;
			String userIdAsString = iterator.next();
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
				message = moveUserWithoutTest(iwrb, user, parentGroup, targetGroup, iwuc.getCurrentUser(), leaveCopyOfUserInCurrentGroup);
			}
			// if the user was sucessfully moved the message is null
			result.put(userId, message);
		}
		return result;
	}

	@Override
	public Map<Integer, Map<Object, String>> moveUsers(IWUserContext iwuc, Collection<Group> groups, Collection<String> groupTypesToMoveAmong) {
		IWMainApplication application = getIWApplicationContext().getIWMainApplication();
		IWBundle bundle = application.getBundle("com.idega.user");
		Locale locale = iwuc.getCurrentLocale();
		IWResourceBundle iwrb = bundle.getResourceBundle(locale);
		String noSuitableGroupMessage = iwrb.getLocalizedString("user_suitable_group_could_not_be_found", "A suitable group for the user could not be found.");
		String moreThanOneSuitableGroupMessage = iwrb.getLocalizedString("user_more_than_one_suitable_group_was_found_prefix", "More than one suitable groups where found. The system could not decide where to put the user. The possible groups are: ");
		// key groups id, value group
		Map<String, Group> groupIdGroup = new HashMap<String, Group>();
		// key group id, value users
		Map<String, Collection<User>> groupIdUsers = new HashMap<String, Collection<User>>();
		// key group id, value id of users
		Map<String, Collection<String>>  groupIdUsersId = new HashMap<String, Collection<String>> ();
		// key user id, value user's parent group
		Map<User, Group> userParentGroup = new HashMap<User, Group>();
		// key user id, value user's target group
		Map<User, Collection<Group>> userTargetGroup = new HashMap<User, Collection<Group>>();
		// get all groups
		try {
			GroupBusiness groupBiz = getGroupBusiness();
			Iterator<Group> groupsIterator = groups.iterator();
			String[] usrGroupType = new String[] { User.USER_GROUP_TYPE };
			while (groupsIterator.hasNext()) {
				Group group = groupsIterator.next();
				String groupId = group.getPrimaryKey().toString();
				String groupType = group.getGroupType();
				if (groupTypesToMoveAmong == null || (groupTypesToMoveAmong.contains(groupType))) {
					fillMaps(group, groupId, groupIdGroup, groupIdUsers, groupIdUsersId);
				}
				// Iterator childIterator =
				// groupBiz.getChildGroups(group).iterator();
				Collection<Group> coll = groupBiz.getChildGroupsRecursive(group, usrGroupType, false);
				if (coll != null && !coll.isEmpty()) {
					for (Iterator<Group> childIterator = coll.iterator(); childIterator.hasNext();) {
						Group childGroup = childIterator.next();
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
		Iterator<Map.Entry<String, Group>> groupIdsIterator = groupIdGroup.entrySet().iterator();
		// iterate over groups
		while (groupIdsIterator.hasNext()) {
			Map.Entry<String, Group> entry = groupIdsIterator.next();
			String parentGroupId = entry.getKey();
			Group parentGroup = entry.getValue();
			Collection<User> userInGroup = groupIdUsers.get(parentGroupId);
			Iterator<User> userIterator = userInGroup.iterator();
			// iterate over users within a group
			while (userIterator.hasNext()) {
				Collection<Group> possibleTargets = null;
				User user = userIterator.next();
				// test if the user is assignable to one and only one group
				Iterator<Map.Entry<String, Group>> targetGroupIds = groupIdGroup.entrySet().iterator();
				while (targetGroupIds.hasNext()) {
					Map.Entry<String, Group> targetEntry = targetGroupIds.next();
					String targetGroupId = targetEntry.getKey();
					Group targetGroup = targetEntry.getValue();
					boolean result = true;
					// skip the own group
					if (!targetGroupId.equals(parentGroupId)) {
						// check if the user is already a member of the target
						// group
						Collection<String> userIdsOfTarget = groupIdUsersId.get(targetGroupId);
						result = !userIdsOfTarget.contains(user.getPrimaryKey().toString());
					}
					// check if the user is suited for the target group when the
					// result is still true
					if (result) {
						result = (isUserSuitedForGroup(user, targetGroup) == null);
					}
					if (result) {
						if (possibleTargets == null) {
							possibleTargets = new ArrayList<Group>();
						}
						possibleTargets.add(targetGroup);
					}
				}
				userParentGroup.put(user, parentGroup);
				userTargetGroup.put(user, possibleTargets);
			}
		}
		// perform moving
		Map<Integer, Map<Object, String>> result = new HashMap<Integer, Map<Object, String>>();
		Iterator<Map.Entry<User, Collection<Group>>> userIterator = userTargetGroup.entrySet().iterator();
		while (userIterator.hasNext()) {
			Map.Entry<User, Collection<Group>> entry = userIterator.next();
			User user = entry.getKey();
			Collection<Group> target = entry.getValue();
			Group source = userParentGroup.get(user);
			Integer sourceId = (Integer) source.getPrimaryKey();
			Map<Object, String> map = result.get(sourceId);
			if (map == null) {
				map = new HashMap<Object, String>();
				result.put(sourceId, map);
			}
			if (target != null) {
				if (target.size() == 1) {
					int source_id = ((Integer) source.getPrimaryKey()).intValue();
					Group targetGr = target.iterator().next();
					int target_id = ((Integer) targetGr.getPrimaryKey()).intValue();
					if (source_id != target_id) {
						String message = moveUserWithoutTest(iwrb, user, source, targetGr, iwuc.getCurrentUser());
						// if there is not a transaction error the message is
						// null!
						map.put(user.getPrimaryKey(), message);
					} else {
						map.put(user.getPrimaryKey(), null);
					}
				} else {
					String message = moreThanOneSuitableGroupMessage;
					boolean first = true;
					for (Iterator<Group> iter = target.iterator(); iter.hasNext();) {
						Group gr = iter.next();
						if (first) {
							message += " ";
						} else {
							message += ", ";
						}
						message += gr.getName();
						first = false;
					}
					map.put(user.getPrimaryKey(), message);
				}
			} else {
				map.put(user.getPrimaryKey(), noSuitableGroupMessage);
			}
		}
		return result;
	}

	private void fillMaps(Group group, String groupId, Map<String, Group> groupIdGroup, Map<String, Collection<User>> groupIdUsers, Map<String, Collection<String>> groupIdUsersId) {
		groupIdGroup.put(groupId, group);
		Collection<User> usersInGroup = getUsersInGroup(group);
		groupIdUsers.put(groupId, usersInGroup);
		Collection<String> userIds = new ArrayList<String>();
		Iterator<User> iterator = usersInGroup.iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			userIds.add(user.getPrimaryKey().toString());
		}
		groupIdUsersId.put(groupId, userIds);
	}

	@Override
	public boolean isMemberOfGroup(int parentGroupToTest, User user) {
		// first check the primary group
		/*
		 * Eiki and jonas, commented out because we could not add users from old user system to the same group as their former primary group. We need this method to return false because they don't have a record in ic_group_relation like they should. Group group = user.getPrimaryGroup(); if (group != null) { int primaryGroupId = ((Integer) group.getPrimaryKey()).intValue(); if (parentGroupToTest == primaryGroupId) { return true; } }
		 */
		// then check the group relations
		int userId = ((Integer) user.getPrimaryKey()).intValue();
		Collection<GroupRelation> coll;
		try {
			GroupRelationHome groupRelationHome = (GroupRelationHome) IDOLookup.getHome(GroupRelation.class);
			GroupHome groupHome = (GroupHome) IDOLookup.getHome(Group.class);
			String parentRelation = groupHome.getRelationTypeGroupParent();
			coll = groupRelationHome.findGroupsRelationshipsContainingUniDirectional(parentGroupToTest, userId, parentRelation);
		}
		// Remote and FinderException
		catch (Exception rme) {
			throw new RuntimeException(rme.getMessage());
		}
		return !coll.isEmpty();
	}

	private String moveUserWithoutTest(IWResourceBundle iwrb, User user, Group parentGroup, Group targetGroup, User currentUser) {
		return moveUserWithoutTest(iwrb, user, parentGroup, targetGroup, currentUser, false);
	}

	private String moveUserWithoutTest(IWResourceBundle iwrb, User user, Group parentGroup, Group targetGroup, User currentUser, boolean leaveCopyOfUserInCurrentGroup) {

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
			if (!leaveCopyOfUserInCurrentGroup) {
				if (parentGroup != null) {
					callAllUserGroupPluginBeforeUserRemoveMethod(user, parentGroup);
					parentGroup.removeUser(user, currentUser);
				}
			}
			// set target group
			if (!targetIsSetAsPrimaryGroup) {
				targetGroup.addGroup(userId);
			} else {
				// this is a hack. If the target group is already the primary
				// group
				// it should not be necessary to add a corresponding group
				// relation.
				// but if it is not added the group tree does not know that this
				// user is a child
				// of the target group
				targetGroup.addGroup(userId);
			}

			callAllUserGroupPluginAfterUserCreateOrUpdateMethod(user, targetGroup);

			transactionManager.commit();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			try {
				transactionManager.rollback();
			} catch (javax.transaction.SystemException sy) {
				sy.printStackTrace(System.err);
			}
			// this is an unusual error therefore localization is it not
			// necessary

			if (e instanceof RemoveException) {
				return e.getMessage();
			}

			String msg = e.getMessage();

			String errorMessage = iwrb.getLocalizedString("new_user.transaction_rollback", "User could not be created/added because of the error: ") + msg + iwrb.getLocalizedString("new_user.try_again", " Please try again or contact the system administrator if you think it is a server error.");

			return errorMessage;

		}
		return null;
	}

	/**
	 * Returns a localized error message (if the UserGroupPlugin localized it!) or null if there was no error.
	 */
	@Override
	public String isUserSuitedForGroup(User user, Group targetGroup) {
		try {
			String grouptype = targetGroup.getGroupType();
			Collection<UserGroupPlugInBusiness> plugins = this.pluginsForGroupTypeCachMap.get(grouptype);
			if (plugins == null) {
				plugins = getGroupBusiness().getUserGroupPluginsForGroupType(grouptype);
				this.pluginsForGroupTypeCachMap.put(grouptype, plugins);
			}
			Iterator<UserGroupPlugInBusiness> iter = plugins.iterator();
			while (iter.hasNext()) {
				UserGroupPlugInBusiness pluginBiz = iter.next();
				String message;
				if ((message = pluginBiz.isUserSuitedForGroup(user, targetGroup)) != null) {
					return message;
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return null;
	}

	@Override
	public String getUserApplicationStyleSheetURL() {
		IWMainApplication application = this.getIWMainApplication();
		String styleSheetOverrideURI = application.getSettings().getProperty("USER_APP_STYLE_SHEET", "");

		if (!"".equals(styleSheetOverrideURI)) {
			return styleSheetOverrideURI;
		} else {
			IWBundle bundle = application.getBundle("com.idega.user");
			return bundle.getVirtualPathWithFileNameString("DefaultStyle.css");
		}

	}

	@Override
	public boolean isInDefaultCommune(User user) throws RemoteException, FinderException {
		Address address = getUsersMainAddress(user);
		Commune commune = null;
		if (address != null) {
			commune = getCommuneHome().findByPrimaryKey(new Integer(address.getCommuneID()));
		} else {
			return false;
		}
		if (commune != null) {
			return commune.getIsDefault();
		}
		return false;
	}

	private CommuneHome getCommuneHome() throws RemoteException {
		return (CommuneHome) IDOLookup.getHome(Commune.class);
	}

	/**
	 * Updates or creates a users main address with a fully qualifying address string (see getFullAddressString(address) in AddressBusiness for the format of the string)
	 *
	 * @param user
	 * @param fullAddressString
	 *            e.g. : "Stafnasel 6;107 Reykjavik;Iceland:is_IS;Reykjavik:12345", See javadoc on getFullAddressString(Address address) in AddressBusiness
	 * @throws RemoteException
	 * @throws CreateException
	 */
	@Override
	public void updateUsersMainAddressByFullAddressString(User user, String fullAddressString) throws RemoteException, CreateException {
		if (fullAddressString != null && !"".equals(fullAddressString)) {
			Address mainAddress = getUsersMainAddress(user);
			boolean addAddress = false;
			if (mainAddress == null) {
				mainAddress = getAddressHome().create();
				mainAddress.setAddressType(getAddressBusiness().getMainAddressType());
				addAddress = true;
			}

			mainAddress = getAddressBusiness().getUpdatedAddressByFullAddressString(mainAddress, fullAddressString);

			if (addAddress) {
				try {
					user.addAddress(mainAddress);
				} catch (IDOAddRelationshipException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public User getUserByUniqueId(String uniqueID) throws FinderException {
		User user;
		user = getUserHome().findUserByUniqueId(uniqueID);
		return user;
	}

	@Override
	public Collection<User> getUsersBySpecificGroupsUserstatusDateOfBirthAndGender(Collection<?> groups, Collection<?> userStatuses, Integer yearOfBirthFrom, Integer yearOfBirthTo, String gender) {
		try {
			return getUserHome().ejbFindUsersBySpecificGroupsUserstatusDateOfBirthAndGender(groups, userStatuses, yearOfBirthFrom, yearOfBirthTo, gender);
		} catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}

	private UserCommentHome getUserCommentHome() {
		try {
			return (UserCommentHome) IDOLookup.getHome(UserComment.class);
		} catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	@Override
	public void storeUserComment(User user, String comment, User performer) {
		try {
			UserComment userComment = getUserCommentHome().create();
			userComment.setUser(user);
			userComment.setComment(comment);
			userComment.setCreatedDate(new IWTimestamp().getDate());
			userComment.setCreatedBy(performer);
			userComment.store();
		} catch (CreateException ce) {
			log(ce);
		}
	}

	@Override
	public Collection<UserComment> getUserComments(User user) throws FinderException {
		Collection<UserComment> comments = getUserCommentHome().findAllByUser(user);
		if (comments == null || comments.isEmpty()) {
			throw new FinderException("No comments found for user with PK = " + user.getPrimaryKey());
		}
		return comments;
	}

	/**
	 * This method will try to find the parent of the user (if only one) and then calls callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(group,parentGroup)
	 *
	 * @throws CreateException
	 * @throws RemoteException
	 */
	@Override
	public void callAllUserGroupPluginAfterUserCreateOrUpdateMethod(User user) throws CreateException, RemoteException {
		List<Group> list = user.getParentGroups();
		Group parentGroup = null;
		if (list != null && list.size() == 1) {
			parentGroup = list.iterator().next();
		}

		callAllUserGroupPluginAfterUserCreateOrUpdateMethod(user, parentGroup);
	}

	@Override
	public void callAllUserGroupPluginAfterUserCreateOrUpdateMethod(User user, Group parentGroup) throws CreateException, RemoteException {
		// get plugins and call the method
		Collection<UserGroupPlugInBusiness> allUserPlugins = getGroupBusiness().getUserGroupPlugins();
		Iterator<UserGroupPlugInBusiness> plugs = allUserPlugins.iterator();
		while (plugs.hasNext()) {
			UserGroupPlugInBusiness plugBiz = plugs.next();
			plugBiz.afterUserCreateOrUpdate(user, parentGroup);
		}
	}

	@Override
	public void callAllUserGroupPluginBeforeUserRemoveMethod(User user, Group parentGroup) {
		// get plugins and call the method
		Collection<UserGroupPlugInBusiness> allUserPlugins;
		try {
			allUserPlugins = getGroupBusiness().getUserGroupPlugins();
			Iterator<UserGroupPlugInBusiness> plugs = allUserPlugins.iterator();
			while (plugs.hasNext()) {
				UserGroupPlugInBusiness plugBiz = plugs.next();
				plugBiz.beforeUserRemove(user, parentGroup);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (RemoveException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Use this method for getting rid of shared emails
	 */
	@Override
	public void cleanUserEmails() {
		IDOQuery query = IDOQuery.getStaticInstance();
		// get all users that are sharing emails
		query.append("select ic_user_id from ic_user_email where ic_email_id in ( select ic_email_id from ic_user_email group by  ic_email_id having (count(ic_user_id) > 1))");
		try {
			Collection<User> coll = getUserHome().findUsersInQuery(query);
			Iterator<User> iterator = coll.iterator();
			while (iterator.hasNext()) {
				// "delete" main mail (cut the link)
				User user = iterator.next();
				Email mainEmail = getUsersMainEmail(user);
				String mainEmailAddress = mainEmail.getEmailAddress();
				user.removeEmail(mainEmail);
				// "delete" other mails (cut the links)
				List<String> list = new ArrayList<String>();
				Collection<Email> otherEmails = getEmailHome().findEmailsForUser(user);
				Iterator<Email> firstOtherEmailsIterator = otherEmails.iterator();
				while (firstOtherEmailsIterator.hasNext()) {
					Email otherEmail = firstOtherEmailsIterator.next();
					String otherEmailAddress = otherEmail.getEmailAddress();
					list.add(otherEmailAddress);
					user.removeEmail(otherEmail);
				}
				// create main email
				updateUserMail(user, mainEmailAddress);
				// create other mails
				Iterator<String> secondOtherEmailsIterator = list.iterator();
				while (secondOtherEmailsIterator.hasNext()) {
					String otherEmailAddress = secondOtherEmailsIterator.next();
					if (StringHandler.isNotEmpty(otherEmailAddress)) {
						// we should not store emails without a type but the list of old emails does not have a type,
						// what else could we do?
						Email otherEmail = this.getEmailHome().create();
						otherEmail.setEmailAddress(otherEmailAddress);
						otherEmail.store();
						user.addEmail(otherEmail);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Sets the preferredLocale for the user and STORES to the database.
	 *
	 * @param user
	 * @param preferredLocale
	 *            (the language)
	 */
	@Override
	public void setUsersPreferredLocale(User user, String preferredLocale, boolean storeUser) {
		user.setPreferredLocale(preferredLocale);

		if (storeUser) {
			user.store();
		}
	}

	/**
	 * Sets the preferredRole for the user and STORES to the database.
	 *
	 * @param user
	 * @param preferredRole
	 */
	@Override
	public void setUsersPreferredRole(User user, ICRole preferredRole, boolean storeUser) {
		user.setPreferredRole(preferredRole);

		if (storeUser) {
			user.store();
		}
	}

	/**
	 * @param user
	 * @return a Locale object created with the users preferred locale (language)
	 */
	@Override
	public Locale getUsersPreferredLocale(User user) {
		Locale locale = null;
		if (user != null) {
			String localeString = user.getPreferredLocale();
			if (localeString != null) {
				return LocaleUtil.getLocale(localeString);
			}
		}
		return locale;
	}

	/**
	 * @param user
	 * @return a ICRole object created with the users preferred role
	 */
	@Override
	public ICRole getUsersPreferredRole(User user) {
		ICRole role = null;
		if (user != null) {
			role = user.getPreferredRole();
		}
		return role;
	}

	@Override
	public boolean validatePersonalId(User user, Locale locale) {
		return user == null ? false : validatePersonalId(user.getPersonalID(), locale);
	}

	@Override
	public boolean validatePersonalId(User user) {
		return validatePersonalId(user, CoreUtil.getCurrentLocale());
	}

	@Override
	public boolean validatePersonalId(String personalId) {
		return validatePersonalId(personalId, CoreUtil.getCurrentLocale());
	}

	@Override
	public boolean validatePersonalId(String personalId, Locale locale) {
		if (StringUtil.isEmpty(personalId)) {
			LOGGER.warning("Personal ID is empty!");
			return false;
		}
		if (locale == null) {
			LOGGER.warning("Unkown locale!");
			return false;
		}

		if ("is_IS".equals(locale.toString())) {
			return validateIcelandicSSN(personalId);
		} else if ("sv_SE".equals(locale.toString())) {
			return SocialSecurityNumber.isValidSocialSecurityNumber(personalId, locale);
		} else if ("en".equals(locale.toString())) {
			return true;	//	Default locale, no validator needed
		}

		LOGGER.warning("There is no validator for locale: " + locale + ", personal id: " + personalId);
		return false;
	}

	/**
	 * Validated the Icelandic SSN checksum
	 */
	private boolean validateIcelandicSSN(String ssn) {
		if (StringUtil.isEmpty(ssn)) {
			return false;
		}

		int sum = 0;
		boolean validSSN = false;
		if (ssn.length() == 10) {
			try {
				sum = sum + Integer.parseInt(ssn.substring(0, 1)) * 3;
				sum = sum + Integer.parseInt(ssn.substring(1, 2)) * 2;
				sum = sum + Integer.parseInt(ssn.substring(2, 3)) * 7;
				sum = sum + Integer.parseInt(ssn.substring(3, 4)) * 6;
				sum = sum + Integer.parseInt(ssn.substring(4, 5)) * 5;
				sum = sum + Integer.parseInt(ssn.substring(5, 6)) * 4;
				sum = sum + Integer.parseInt(ssn.substring(6, 7)) * 3;
				sum = sum + Integer.parseInt(ssn.substring(7, 8)) * 2;
				sum = sum + Integer.parseInt(ssn.substring(8, 9)) * 1;
				sum = sum + Integer.parseInt(ssn.substring(9, 10)) * 0;
				if ((sum % 11) == 0) {
					validSSN = true;
				} else {
					LOGGER.warning(ssn + " is not a valid SSN. If fails validation test.");
				}
			} catch (NumberFormatException e) {
				LOGGER.warning(ssn + " is not a valid SSN. It contains characters other than digits.");
			}
		} else {
			LOGGER.warning(ssn + " is not a valid SSN. It is not 10 characters.");
		}
		return validSSN;
	}

	@Override
	public boolean hasValidPersonalId(User user) {
		return validatePersonalId(user);
	}

	@Override
	public boolean hasValidPersonalId(User user, Locale locale) {
		return validatePersonalId(user, locale);
	}

	/**
	 * Gets info about Groups members
	 */
	@Override
	public List<GroupMemberDataBean> getGroupsMembersData(List<String> uniqueIds) {
		if (uniqueIds == null) {
			return null;
		}

		GroupBusiness business = null;
		try {
			business = getGroupBusiness();
		} catch (RemoteException e) {
		}
		if (business == null) {
			return null;
		}

		Group group = null;
		IWContext iwc = CoreUtil.getIWContext();
		List<GroupMemberDataBean> members = new ArrayList<GroupMemberDataBean>();

		for (int i = 0; i < uniqueIds.size(); i++) {
			try {
				group = business.getGroupByUniqueId(uniqueIds.get(i));
			} catch (Exception e) {
			}

			// Filling beans with data
			setComplexData(members, group, business, iwc);
		}

		return getSortedMembersByStatus(members);
	}

	/**
	 * Getting info about all users in selected Group
	 *
	 * @param bean
	 * @param group
	 */
	private void setComplexData(List<GroupMemberDataBean> members, Group group, GroupBusiness groupBusiness, IWContext iwc) {
		if (group == null) {
			return;
		}

		UserInfoColumnsBusiness userInfoBusiness = getUserInfoColumnsBusiness(iwc);

		Collection<User> users = getUsersInGroup(group);
		if (users == null) {
			return;
		}

		int groupId = getParsedValue(group.getId());

		Object o = null;
		User user = null;
		GroupMemberDataBean memberInfo = null;
		for (Iterator<User> it = users.iterator(); it.hasNext();) {
			o = it.next();
			if (o instanceof User) {
				user = (User) o;

				int userId = getParsedValue(user.getId());

				memberInfo = new GroupMemberDataBean();

				// Title, Education, School, Area, Began work
				// extractExtraInfo(memberInfo, user); // TODO may be useful later

				// Age
				memberInfo.setAge(getUserAge(user));

				// Phones
				Phone workPhone = null;
				Phone homePhone = null;
				Phone mobilePhone = null;
				try {
					workPhone = getUsersWorkPhone(user);
				} catch (NoPhoneFoundException e) {
				}
				try {
					homePhone = getUsersHomePhone(user);
				} catch (NoPhoneFoundException e) {
				}
				try {
					mobilePhone = getUsersMobilePhone(user);
				} catch (NoPhoneFoundException e) {
				}
				if (workPhone != null) {
					memberInfo.setWorkPhone(workPhone.getNumber());
				}
				if (homePhone != null) {
					memberInfo.setHomePhone(homePhone.getNumber());
				}
				if (mobilePhone != null) {
					memberInfo.setMobilePhone(mobilePhone.getNumber());
				}

				if (groupBusiness != null) {
					// Addresses (main and company's)
					try {
						memberInfo.setAddress(groupBusiness.getAddressParts(getUsersMainAddress(user)));
					} catch (RemoteException e) {
					}
					try {
						memberInfo.setCompanyAddress(groupBusiness.getAddressParts(getUsersCoAddress(user)));
					} catch (RemoteException e) {
					}
				}

				// Is male?
				try {
					memberInfo.setMale(isMale(user.getGenderID()));
				} catch (RemoteException e) {
				} catch (FinderException e) {
				}

				// Emails
				setUserMails(memberInfo, user);

				// Name
				memberInfo.setName(user.getName());

				// Image
				if (iwc != null) {
					Image image = getUserImage(user);
					if (image != null) {
						memberInfo.setImageUrl(image.getMediaURL(iwc));
					}
				}

				// Extra info
				memberInfo.setExtraInfo(user.getExtraInfo());

				// Description
				memberInfo.setDescription(user.getDescription());

				// Date of birth
				memberInfo.setDateOfBirth(user.getDateOfBirth());

				// Year of birth
				if (user.getDateOfBirth() != null) {
					memberInfo.setYearOfBirth(String.valueOf(new IWTimestamp(user.getDateOfBirth()).getYear()));
				}

				// Job
				memberInfo.setJob(getUserJob(user));

				// Work place
				memberInfo.setWorkPlace(getUserWorkPlace(user));

				// Status
				Status userStatus = getUserStatus(iwc, user, group);
				if (userStatus != null) {
					memberInfo.setStatus(userStatus.getStatusKey());
					memberInfo.setStatusOrder(userStatus.getStatusOrder());
				}

				// Descriptions
				if (userInfoBusiness != null) {
					// User info 1
					try {
						memberInfo.setInfoOne(userInfoBusiness.getUserInfo1(userId, groupId));
					} catch (RemoteException e) {
					}
					// User info 2
					try {
						memberInfo.setInfoTwo(userInfoBusiness.getUserInfo2(userId, groupId));
					} catch (RemoteException e) {
					}
					// User info 3
					try {
						memberInfo.setInfoThree(userInfoBusiness.getUserInfo3(userId, groupId));
					} catch (RemoteException e) {
					}
				}

				// Group name
				memberInfo.setGroupName(group.getName());

				members.add(memberInfo);
			}
		}
	}

	private List<GroupMemberDataBean> getSortedMembersByStatus(List<GroupMemberDataBean> members) {
		if (members == null) {
			return null;
		}

		// Finding users with status
		List<GroupMemberDataBean> allMembers = new ArrayList<GroupMemberDataBean>();
		List<GroupMemberDataBean> membersWithStatusInfo = new ArrayList<GroupMemberDataBean>();
		GroupMemberDataBean memberInfo = null;
		for (int i = 0; i < members.size(); i++) {
			memberInfo = members.get(i);
			if (memberInfo.getStatus() == null) { // Has user status?
				allMembers.add(memberInfo);
			} else {
				membersWithStatusInfo.add(memberInfo);
			}
		}

		// Sorting
		if (membersWithStatusInfo.size() > 0) {
			Collections.sort(membersWithStatusInfo, new GroupMemberDataBeanComparator());
			allMembers.addAll(0, membersWithStatusInfo); // Adding to begin
		}

		return allMembers;
	}

	/**
	 * Returns user's status in concrete group. Note: IWContext may be null, it will be checked
	 */
	@Override
	public Status getUserStatus(IWContext iwc, User user, Group group) {
		if (user == null || group == null) {
			return null;
		}

		int userId = -1;
		int groupId = -1;
		try {
			userId = Integer.valueOf(user.getId()).intValue();
			groupId = Integer.valueOf(group.getId()).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}

		return getUserStatus(iwc, userId, groupId);
	}

	/**
	 * Returns user's status in concrete group. Note: IWContext may be null, it will be checked
	 */
	@Override
	public Status getUserStatus(IWContext iwc, int userId, int groupId) {
		if (statusBusiness == null && iwc == null) { // Checking if we need instance of IWContext
			iwc = CoreUtil.getIWContext();
			if (iwc == null) {
				return null;
			}
		}

		int statusId = -1;
		try {
			statusId = getUserStatusBusiness(iwc).getUserGroupStatus(userId, groupId);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (statusId == -1) {
			return null;
		}

		Status status = null;
		try {
			status = IDOLookup.findByPrimaryKey(Status.class, statusId);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return status;
	}

	private UserStatusBusiness getUserStatusBusiness(IWContext iwc) {
		if (statusBusiness == null) {
			try {
				statusBusiness = IBOLookup.getServiceInstance(iwc, UserStatusBusiness.class);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return statusBusiness;
	}

	private UserInfoColumnsBusiness getUserInfoColumnsBusiness(IWContext iwc) {
		if (userInfoBusiness == null) {
			try {
				userInfoBusiness = IBOLookup.getServiceInstance(iwc, UserInfoColumnsBusiness.class);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return userInfoBusiness;
	}

	@Override
	public Image getUserImage(User user) {
		if (user == null) {
			return null;
		}

		int imageId = user.getSystemImageID();
		Image image = null;
		if (imageId == -1) {
			return null;
		} else {
			try {
				image = new Image(imageId);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return image;
	}

	private void setUserMails(GroupMemberDataBean memberInfo, User user) {
		if (memberInfo == null || user == null) {
			return;
		}

		Collection<Email> emails = user.getEmails();
		if (emails == null) {
			return;
		}
		Object o = null;
		Email email = null;
		List<String> emailsAddresses = new ArrayList<String>();
		for (Iterator<Email> it = emails.iterator(); it.hasNext();) {
			o = it.next();
			if (o instanceof Email) {
				email = (Email) o;
				emailsAddresses.add(email.getEmailAddress());
			}
		}
		memberInfo.setEmailsAddresses(emailsAddresses);
	}

	/*
	 * private void extractExtraInfo(GroupMemberDataBean bean, User user) { if (bean == null || user == null) { return; } EmploymentMemberInfo memberInfo = null; try { memberInfo = getMemberHome().findByPrimaryKey(Integer.valueOf(user.getId())); } catch (NumberFormatException e) { e.printStackTrace(); } catch (FinderException e) { e.printStackTrace(); } if (memberInfo == null) { return; }
	 *
	 * bean.setTitle(memberInfo.getTitle()); bean.setEducation(memberInfo.getEducation()); bean.setSchool(memberInfo.getSchool()); //bean.setArea(memberInfo); TODO bean.setBeganWork(memberInfo.getBeganWork()); }
	 */

	/*
	 * private EmploymentMemberInfoHome getMemberHome() { try { return (EmploymentMemberInfoHome) IDOLookup.getHome(EmploymentMemberInfo.class); } catch (IDOLookupException e) { e.printStackTrace(); } return null; }
	 */

	private String getUserAge(User user) {
		if (user == null) {
			return null;
		}
		if (user.getDateOfBirth() == null) {
			return null;
		}
		IWTimestamp dateOfBirth = new IWTimestamp(user.getDateOfBirth());
		IWTimestamp dateToday = new IWTimestamp();
		return Integer.toString((IWTimestamp.getDaysBetween(dateOfBirth, dateToday)) / 365);
	}

	@Override
	public Date getUserDateOfBirthFromPersonalId(String personalId) {
		Locale locale = CoreUtil.getCurrentLocale();
		if (locale == null) {
			LOGGER.warning("Current locale is unknown!");
			return null;
		}

		if (!validatePersonalId(personalId, locale)) {
			return null;
		}

		if ("is_IS".equals(locale.toString())) {
			return getDateBirthFromIcelandicPersonalId(personalId);
		} else if ("sv_SE".equals(locale.toString())) {
			return getDateBirthFromSwedishPersonalId(personalId);
		}

		LOGGER.warning("There is no date parser from personal id: " + personalId + " and locale: " + locale);
		return null;
	}

	private Date getDateBirthFromIcelandicPersonalId(String personalId) {
		String dateInString = personalId.substring(0, 6);
		java.util.Date date = null;
		try {
			date = userDateOfBirthFormatter.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		if (date == null) {
			return null;
		}

		int lastNumber = -1;
		try {
			lastNumber = Integer.valueOf(personalId.substring(personalId.length() - 1)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (lastNumber < 0) {
			return null;
		}
		IWTimestamp iwDate = new IWTimestamp(date.getTime());
		int minYearsValue = 1900;
		int maxYearsValue = 1999;
		switch (lastNumber) {
		case 0:
			minYearsValue = 2000;
			maxYearsValue = 2099;
			break;
		case 8:
			minYearsValue = 1800;
			maxYearsValue = 1899;
			break;
		}
		iwDate.setYear(getAdjustedYears(iwDate.getYear(), minYearsValue, maxYearsValue));

		return iwDate.getDate();
	}

	/**
	 * Calculate birth date from Swedish personal id
	 * @param personalId Swedish personal id
	 * @return Birth date
	 */
	private Date getDateBirthFromSwedishPersonalId(String personalId) {
		java.util.Date birthDate = null;
		if (StringUtils.isNotBlank(personalId)) {
			if (personalId.length() == 12) {
				String dateInString = personalId.substring(0, 8);
				try {
					birthDate = userDateOfBirthFormatterFullYears.parse(dateInString);
					IWTimestamp iwDate = new IWTimestamp(birthDate.getTime());
					return iwDate.getDate();
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				String dateInString = personalId.substring(0, 6);
				try {
					birthDate = userDateOfBirthFormatterYearsFirst.parse(dateInString);
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
				if (birthDate == null) {
					return null;
				}
				try {
					int lastTwoYearDigitsFromNow = Integer.valueOf(new IWTimestamp().getDateString("yy"));
					IWTimestamp iwDate = new IWTimestamp(birthDate.getTime());
					int lastTwoYearDigitsFromBirthDate = Integer.valueOf(iwDate.getDateString("yy"));
					int minYearsValue = 1900;
					int maxYearsValue = 1999;
					if (personalId.length() == 11) {
						String sign = personalId.substring(6, 7);
						if (sign.equalsIgnoreCase(CoreConstants.MINUS)) {
							if (lastTwoYearDigitsFromBirthDate > lastTwoYearDigitsFromNow) {
								minYearsValue = 1900;
								maxYearsValue = 1999;
							} else {
								minYearsValue = 2000;
								maxYearsValue = 2099;
							}
						} else if (sign.equalsIgnoreCase(CoreConstants.PLUS)) {
							minYearsValue = 1900;
							maxYearsValue = 1999;
						} else {
							return null;
						}
					} else if (personalId.length() == 10) {
						if (lastTwoYearDigitsFromBirthDate > lastTwoYearDigitsFromNow) {
							minYearsValue = 1900;
							maxYearsValue = 1999;
						} else {
							minYearsValue = 2000;
							maxYearsValue = 2099;
						}
					} else {
						return null;
					}
					//Finish calculating years
					iwDate.setYear(getAdjustedYears(iwDate.getYear(), minYearsValue, maxYearsValue));
					return iwDate.getDate();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}

	private int getAdjustedYears(int years, int minValue, int maxValue) {
		while (years > maxValue) {
			years -= 100;
		}
		while (years < minValue) {
			years += 100;
		}
		return years;
	}

	private int getParsedValue(String value) {
		if (value == null) {
			return -1;
		}
		try {
			return Integer.valueOf(value).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public String getUserPassword(User user) {
		LoginTable loginTable = getLoginTableForUser(user);
		if (loginTable == null) {
			return null;
		}

		return loginTable.getUserPasswordInClearText();
	}

	private LoginTable getLoginTableForUser(User user) {
		if (user == null) {
			return null;
		}

		return LoginDBHandler.getUserLogin(((Integer) user.getPrimaryKey()).intValue());
	}

	@Override
	public String getUserLogin(User user) {
		LoginTable loginTable = getLoginTableForUser(user);
		if (loginTable == null) {
			return null;
		}

		return loginTable.getUserLogin();
	}

	@Override
	public List<String> getAllUserGroupsIds(User user, IWUserContext iwuc) throws RemoteException {
		if (user == null || iwuc == null) {
			return null;
		}

		List<Group> userGroups = new ArrayList<Group>();
		GroupBusiness groupBusiness = getGroupBusiness();
		Collection<Group> parentUserGroups = groupBusiness.getParentGroups(user);
		if (parentUserGroups == null) {
			return null;
		}

		Object o = null;
		for (Iterator<Group> it = parentUserGroups.iterator(); it.hasNext();) {
			o = it.next();

			if (o instanceof Group) {
				userGroups.add((Group) o);
			}
		}

		List<String> groupsIds = new ArrayList<String>();
		Collection<ICPermission> permissionsByUserGroups = AccessControl.getAllGroupPermitPermissionsOld(userGroups);
		addIdsFromPermissions(permissionsByUserGroups, groupsIds);

		Collection<ICPermission> permissionsByUser = AccessControl.getAllGroupOwnerPermissionsByGroup(user);
		addIdsFromPermissions(permissionsByUser, groupsIds);

		return groupsIds;
	}

	@Override
	public List<Group> getAllUserGroups(User user, IWUserContext iwuc) throws RemoteException {
		List<String> groupsIds = getAllUserGroupsIds(user, iwuc);
		if (groupsIds == null) {
			return null;
		}

		String[] idsInArray = new String[groupsIds.size()];
		for (int i = 0; i < groupsIds.size(); i++) {
			idsInArray[i] = groupsIds.get(i);
		}

		Collection<Group> groups = null;
		try {
			groups = getGroupBusiness().getGroups(idsInArray);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		if (groups == null) {
			return null;
		}

		List<Group> allUserGroups = new ArrayList<Group>();
		Object o = null;
		for (Iterator<Group> it = groups.iterator(); it.hasNext();) {
			o = it.next();

			if (o instanceof Group) {
				allUserGroups.add((Group) o);
			}
		}

		return allUserGroups;
	}

	private void addIdsFromPermissions(Collection<ICPermission> permissions, List<String> ids) {
		if (permissions == null || ids == null) {
			return;
		}

		Object o = null;
		ICPermission permission = null;
		String id = null;
		for (Iterator<ICPermission> it = permissions.iterator(); it.hasNext();) {
			o = it.next();

			if (o instanceof ICPermission) {
				permission = (ICPermission) o;

				id = permission.getContextValue();
				if (!ids.contains(id)) {
					ids.add(id);
				}
			}
		}
	}

	private String getLoweredStringValueByCurrentLocale(String value) {
		Locale locale = null;
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc != null) {
			locale = iwc.getCurrentLocale();
		}
		if (locale == null) {
			locale = Locale.ENGLISH;
		}

		value = value.toLowerCase(locale);
		return value;
	}

	@Override
	public Collection<User> getUsersByNameOrEmailOrPhone(String nameEmailOrPhone) {
		if (StringUtil.isEmpty(nameEmailOrPhone)) {
			return null;
		}

		Collection<User> usersByNames = getUsersByName(nameEmailOrPhone);
		if (!ListUtil.isEmpty(usersByNames)) {
			return usersByNames;
		}

		Collection<User> usersByEmails = getUsersByEmail(nameEmailOrPhone);
		if (!ListUtil.isEmpty(usersByEmails)) {
			return usersByEmails;
		}

		return getUsersByPhoneNumber(nameEmailOrPhone);
	}

	@Override
	public Collection<User> getUsersByPhoneNumber(String phoneNumber) {
		if (StringUtil.isEmpty(phoneNumber)) {
			return null;
		}

		try {
			return getUserHome().findByPhoneNumber(getLoweredStringValueByCurrentLocale(phoneNumber));
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Collection<User> getUsersByEmail(String email) {
		if (StringUtil.isEmpty(email)) {
			return null;
		}

		try {
			return getUserHome().findUsersByEmail(getLoweredStringValueByCurrentLocale(email), true, true);
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Collection<User> getUsersByName(String name) {
		if (StringUtil.isEmpty(name)) {
			return null;
		}

		name = getLoweredStringValueByCurrentLocale(name);
		String[] nameParts = name.split(CoreConstants.SPACE);
		String firstName = null;
		String middleName = null;
		String lastName = null;
		if (nameParts.length == 3) {
			middleName = nameParts[1];
			lastName = nameParts[2];
		}
		if (nameParts.length == 2) {
			lastName = nameParts[1];
		}
		if (nameParts.length >= 1) {
			firstName = nameParts[0];
		}

		Collection<User> users = null;
		try {
			users = getUserHome().findByNames(firstName, middleName, lastName, true);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		if (ListUtil.isEmpty(users)) {
			try {
				users = getUserHome().findByDisplayName(name, true);
			} catch (FinderException e) {
				e.printStackTrace();
			}
		}

		return users;
	}

	@Override
	public String setPreferredRoleAndGetHomePageUri(String roleKey, IWUserContext iwuc) {
		if (StringUtil.isEmpty(roleKey)) {
			return null;
		}

		IWContext iwc = CoreUtil.getIWContext();
		User currentUser = null;
		try {
			currentUser = iwc.getCurrentUser();
		} catch (NotLoggedOnException e) {
			e.printStackTrace();
			return null;
		}

		ICRoleHome roleHome = null;
		try {
			roleHome = (ICRoleHome) getIDOHome(ICRole.class);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
		ICRole preferredRole = null;
		try {
			preferredRole = roleHome.findByPrimaryKey(roleKey);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
		setUsersPreferredRole(currentUser, preferredRole, true);
		return getPageUriByUserPreferredRole(currentUser, iwuc);
	}

	@Override
	public String getPageUriByUserPreferredRole(User user, IWUserContext iwuc) {
		//FIXME change to use the same logic as the page choosing in IWAuthenticator
		ICRole userPrefferedRole = user.getPreferredRole();
		if (userPrefferedRole == null) {
			return null;
		}

		UserDAO userDAO = getUserDAO();
		com.idega.user.data.bean.User u = userDAO.getUser(Integer.valueOf(user.getId()));

		Collection<com.idega.user.data.bean.Group> userGroups = getAccessController().getAllUserGroupsForRoleKey(user.getPreferredRole().getId(), iwuc, u);
		if (ListUtil.isEmpty(userGroups)) {
			return null;
		}

		int homePageId = -1;
		for (com.idega.user.data.bean.Group userGroup : userGroups) {
			com.idega.core.builder.data.bean.ICPage homePage = userGroup.getHomePage();
			if (homePage == null)
				continue;

			homePageId = homePage.getID();
			if (homePageId > 0) {
				try {
					return getServiceInstance(BuilderService.class).getPageURI(homePageId);
				} catch (IBOLookupException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public List<ICRole> getAvailableRolesForUserAsPreferredRoles(User user) {
		List<ICRole> rolesForUser = new ArrayList<ICRole>();
		AccessController accessController = getAccessController();
		Collection<Group> groups = user.getParentGroups();
		for (Group group : groups) {
			if (group.getHomePageID() > 0) {
				Collection<ICPermission> roles = accessController.getAllRolesForGroup(group);
				if (!ListUtil.isEmpty(roles)) {
					for (ICPermission permission : roles) {
						ICRole role;
						try {
							role = getAccessController().getRoleByRoleKeyOld(permission.getPermissionString());
							if (!rolesForUser.contains(role)) {
								rolesForUser.add(role);
							}
						} catch (FinderException e) {
							continue;
						}
					}
				}
			}
		}
		return rolesForUser;
	}

	/**
	 * @see com.idega.user.business.UserBusiness#getModeratorsForUser(com.idega.user.data.User, com.idega.presentation.IWContext)
	 */
	@Override
	public User getModeratorForUser(User user) {
		try {
			Group company = getPreferedCompany(user);
			if (company != null) {
				User moderator = company.getModerator();
				if (moderator != null) {
					return moderator;
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collection<User> moderators = new ArrayList<User>();
		Collection<Group> userGroups = getUserGroups(user);
		if (userGroups != null) {
			for (Group group : userGroups) {
				if (group.getModerator() != null) {
					moderators.add(group.getModerator());
				}
			}
		}
		if(moderators.size()==1){
			return moderators.iterator().next();
		}

		return null;
	}

	@Override
	public void setPreferedCompany(String companyId, User user) {
		// TODO: constant

		user.setMetaData(MetadataConstants.USER_PREFERED_COMPANY_METADATE_KEY, companyId);
		user.store();
	}

	@Override
	public Group getPreferedCompany(User user) throws RemoteException {
		String companyId = user.getMetaData(MetadataConstants.USER_PREFERED_COMPANY_METADATE_KEY);
		try {
			if (companyId != null) {
				return getGroupBusiness().getGroupByGroupID(Integer.parseInt(companyId));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FinderException e) {
			// Prefered company was not set
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.business.UserBusiness#changeUserPassword(java.lang.String)
	 */
	@Override
	public String changeUserPassword(String newPassword) {
		IWContext iwc = CoreUtil.getIWContext();
		if(changeUserPassword(iwc.getCurrentUser(), newPassword)) {
			return "success";
		} else {
			return "failure";
		}
	}

	@Override
	public boolean changeUserCurrentPassword(IWContext iwc,String newPassword){
		try{
			LoggedOnInfo loggedOnInfo = LoginBusinessBean.getLoggedOnInfo(iwc);
			UserLoginDAO userLoginDAO = ELUtil.getInstance().getBean(UserLoginDAO.class);
			Integer loginId = loggedOnInfo.getUserLogin().getId();
			userLoginDAO.changeLoginPassword(loginId, newPassword);

			User user = getUser(loggedOnInfo.getUser().getId());

			LoginTableHome loginTableHome = (LoginTableHome) IDOLookup.getHome(LoginTable.class);
			LoginTable loginTable = loginTableHome.findByPrimaryKey(loginId);

			return doUpdateLoginInfo(user, loginTable, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed changing current user password", e);
		}
		return false;
	}
	/*
	 * (non-Javadoc)
	 * @see com.idega.user.business.UserBusiness#changeUserPassword(com.idega.user.data.User, java.lang.String)
	 */
	@Override
	public boolean changeUserPassword(User user, String newPassword) {
		if (user == null || StringUtil.isEmpty(newPassword)) {
			return Boolean.FALSE;
		}

		LoginTable loginTable = LoginDBHandler.getUserLogin(user);
		if (loginTable == null) {
			getLogger().warning("Login table not found for: " + user.getName());
			return Boolean.FALSE;
		}

		// encrypt new password
		String encryptedPassword = Encrypter.encryptOneWay(newPassword);
		if (StringUtil.isEmpty(encryptedPassword)) {
			getLogger().warning("Failed to encrypt password for user " + user.getName());
			return Boolean.FALSE;
		}

		// store new password
		loginTable.setUserPassword(encryptedPassword, newPassword);
		try {
			loginTable.store();
		} catch (IDOStoreException e) {
			getLogger().warning("Failed to store password for user " + user.getName());
			return Boolean.FALSE;
		}

		return doUpdateLoginInfo(user, loginTable, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
	}

	private boolean doUpdateLoginInfo(User user, LoginTable loginTable, boolean accountEnabled, boolean passwordNeverExpires, boolean userAllowedToChangePassword, boolean mustChangePasswordNextTime) {
		try {
			LoginDBHandler.updateLoginInfo(loginTable, accountEnabled,
					IWTimestamp.RightNow(), 5000, passwordNeverExpires,
					userAllowedToChangePassword, mustChangePasswordNextTime,
					null);
			getLogger().info("Password for user " + user.getName() + " has been changed");
			return Boolean.TRUE;
		} catch(Exception ex) {
			getLogger().log(Level.WARNING, "Failed to update login info for user: " + user.getName() + " cause of: ", ex);
		}

		return Boolean.FALSE;
	}

	@Override
	public Collection<User> getUsersByNameAndEmailAndPhone(String nameEmailOrPhone) {
		if (StringUtil.isEmpty(nameEmailOrPhone)) {
			return null;
		}

		Set<User> usersBynameEmailAndPhone  = new HashSet<User>();

		Collection<User> usersByNames = getUsersByName(nameEmailOrPhone);

		if(!ListUtil.isEmpty(usersByNames)){
			usersBynameEmailAndPhone.addAll(usersByNames);
		}

		Collection<User> usersByEmails = getUsersByEmail(nameEmailOrPhone);
		if(!ListUtil.isEmpty(usersByEmails)){
			usersBynameEmailAndPhone.addAll(usersByEmails);
		}

		Collection<User> usersByPhone = getUsersByPhoneNumber(nameEmailOrPhone);
		if(!ListUtil.isEmpty(usersByPhone)){
			usersBynameEmailAndPhone.addAll(usersByPhone);
		}

		return usersBynameEmailAndPhone;
	}

	@Override
	public Collection<User> getUsers(String name, String personalID) {
		Collection<User> users = new ArrayList<User>();

		if (!StringUtil.isEmpty(personalID)) {
			try {
				User userByPersonalID = getUser(personalID);
				if (userByPersonalID != null) {
					users.add(userByPersonalID);
				}
			} catch (FinderException e) {
				getLogger().log(Level.WARNING, "Unable to find " + User.class
						+ " by personalID: " + personalID);
			}
		}

		if (ListUtil.isEmpty(users) && !StringUtil.isEmpty(name)) {
			users = getUsersByNameOrEmailOrPhone(name);
		}

		return users;
	}

	/**
	 * <p>Creates new {@link User} with {@link LoginInfo} and sends mail
	 * to given {@link Email}. Method does not check for existing {@link User}s
	 * is so, it just creates new one.
	 * Use {@link UserBusiness#update(String, String, String, String)}
	 * for correct user creation.</p>
	 * @param fullName is {@link User#getName()}, not <code>null</code>;
	 * @param email is {@link Email#getEmailAddress()} for {@link User},
	 * not <code>null</code>;
	 * @return created {@link User} or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected User create(String fullName, String email) {
		if (StringUtil.isEmpty(fullName) || StringUtil.isEmpty(email)) {
			return null;
		}

		User user = null;
		try {
			user = createUser(null, null, null, fullName, null, null, null,
					null, null, fullName);
		} catch (Exception e) {
			getLogger().log(Level.WARNING,
					"Failed to create user cause of: ", e);
		}

		if (user == null) {
			return null;
		}

		LoginTable loginTable = null;
		try {
			loginTable = generateUserLogin(user);
		} catch (Exception e) {
			getLogger().log(Level.WARNING,
					"Failed to create " + LoginTable.class.getName() +
					" for user: " + user +
					" cause of: ", e);
		}

		if (loginTable == null) {
			return null;
		}

		LoginInfo loginInfo = LoginDBHandler.getLoginInfo(loginTable);
		if (loginInfo != null) {
			loginInfo.setChangeNextTime(Boolean.TRUE);
			loginInfo.setAccountEnabled(Boolean.TRUE);
			loginInfo.store();
		}

		IWContext iwc = CoreUtil.getIWContext();
		IWBundle iwrb = getUserBundle(iwc);
		if (iwrb != null) {
			String portNumber = new StringBuilder(":").append(String.valueOf(iwc.getServerPort())).toString();
			String serverLink = StringHandler.replace(iwc.getServerURL(), portNumber, CoreConstants.EMPTY);
			String subject = iwrb.getLocalizedString("account_was_created", "Account was created");
			StringBuilder text = null;
			try {
				text = new StringBuilder(iwrb.getLocalizedString("login_here", "Login here")).append(": ").append(serverLink).append("\n\r")
						.append(iwrb.getLocalizedString("your_user_name", "Your user name")).append(": ").append(loginTable.getUserLogin()).append(", ")
						.append(iwrb.getLocalizedString("your_password", "your password")).append(": ").append(loginTable.getUnencryptedUserPassword()).append(". ")
						.append(iwrb.getLocalizedString("we_recommend_to_change_password_after_login", "We recommend to change password after login!"));
				sendEmail(email, subject, text.toString());
			} catch (PasswordNotKnown e) {
				getLogger().log(Level.WARNING,
						"Password for " + User.class.getName() +
						" was lost, cause of: ", e);
			}
		}

		getLogger().info(User.class.getName() +
				" by primary key: " + user.getPrimaryKey().toString() +
				" successfully created!");
		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.business.UserBusiness#update(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public User update(String primaryKey, String name, String email, String phone) {
		User user = null;
		if (!StringUtil.isEmpty(primaryKey)) {
			/* Searching for existing one by primary key */
			try {
				user = getUserHome().findByPrimaryKey(primaryKey);
			} catch (FinderException e) {
				getLogger().log(Level.WARNING,
						"Failed to get " + User.class.getName() +
						" by primary key: " + primaryKey);
			}
		} else {
			/* Searching for existing one by email and name */
			Collection<User> users = getUserHome().findAllByNameAndEmail(
					name, email);
			if (!ListUtil.isEmpty(users)) {
				user = users.iterator().next();
				if (users.size() > 1) {
					getLogger().log(Level.WARNING,
							"Not unique " + User.class.getName() +
							"'s found by name: '" + name +
							"' and  email: '" + email + "'");
				}
			} else {
				user = create(name, email);
			}
		}

		if (user == null) {
			return null;
		}

		/* Storing user... */
		try {
			user.store();
		} catch (IDOStoreException e) {
			getLogger().log(Level.WARNING,
					"Failed to store user cause of: ", e);
			return null;
		}

		/* Updating phone, if given */
		if (!StringUtil.isEmpty(phone)) {
			updateUserHomePhone(user, phone);
		}

		/* Updating email if given */
		if (!StringUtil.isEmpty(email)) {
			try {
				updateUserMail(user, email);
			} catch (Exception e) {
				getLogger().log(Level.WARNING,
						"Failed to create or update email: '" + email +
						"' for user: '" + user + "' cause of: ", e);
			}
		}

		getLogger().info(User.class.getName() +
				" by primary key: " + user.getPrimaryKey().toString() +
				" successfully updated!");
		return user;
	}

	protected boolean sendEmail(
			String emailTo,
			String subject,
			String text) {
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		if (settings == null) {
			return false;
		}

		String from = settings.getProperty(CoreConstants.PROP_SYSTEM_MAIL_FROM_ADDRESS);
		String host = settings.getProperty(CoreConstants.PROP_SYSTEM_SMTP_MAILSERVER);
		if (StringUtil.isEmpty(from) || StringUtil.isEmpty(host)) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, "Cann't send email from: " + from +
					" via: " + host + ". Set properties for application!");
			return false;
		}

		try {
			SendMail.send(from, emailTo, null, null, host, subject, text);
		} catch (Exception e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, "Error sending mail!", e);
			return false;
		}

		return true;
	}

	protected IWBundle getUserBundle(IWContext iwc) {
		if (iwc != null) {
			return iwc.getIWMainApplication().getBundle(
					CoreConstants.IW_USER_BUNDLE_IDENTIFIER
					);
		}

		return null;
	}

	/**
	 * Getting gender from personal id
	 */
	@Override
	public Gender getGenderFromPersonalId(String personalId) {
		Locale locale = CoreUtil.getCurrentLocale();
		if (locale == null) {
			LOGGER.warning("Current locale is unknown!");
			return null;
		}

		if (!validatePersonalId(personalId, locale)) {
			return null;
		}

		if ("is_IS".equals(locale.toString())) {
			return null;
		} else if ("sv_SE".equals(locale.toString())) {
			return getGenderSwedishPersonalId(personalId);
		} else {
			LOGGER.warning("There is no gender parser from personal id: " + personalId + " and locale: " + locale);
			return null;
		}
	}

	/**
	 * Get gender from Swedish personal id
	 * @param personalId Swedish personal id
	 * @return Gender
	 */
	private Gender getGenderSwedishPersonalId(String personalId) {
		Gender gender = null;
		if (StringUtils.isNotBlank(personalId)) {
			if (personalId.length() == 10 || personalId.length() == 11 || personalId.length() == 12) {
				try {
					int penultimateNumber = Integer.valueOf(personalId.substring(personalId.length() - 2, personalId.length() - 1)).intValue();
					if (penultimateNumber % 2 == 0) {
						//Even = FEMALE
						gender = getGenderHome().getFemaleGender();
					} else {
						//Odd = MALE
						gender = getGenderHome().getMaleGender();
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return gender;
	}


	/**
	 * Saves the user birth country into the METADATA as country's ISO
	 */
	@Override
	public void setBirthCountry(User user, Country country) {
		if (user != null && country != null && !StringUtil.isEmpty(country.getIsoAbbreviation())) {
			user.setMetaData(USER_BIRTH_COUNTRY_META_DATA_KEY, country.getIsoAbbreviation());
			user.store();
		}
	}

	/**
	 * Finds and returns user's birth country
	 */
	@Override
	public Country getBirthCountry(User user) {
		if (user != null) {
			String countryISO = user.getMetaData(USER_BIRTH_COUNTRY_META_DATA_KEY);
			if (!StringUtil.isEmpty(countryISO)) {
				try {
					return getCountryHome().findByIsoAbbreviation(countryISO);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Could not find the country: ", e);
				}
			}
		}
		return null;
	}

	/**
	 * Saves the user languages into the METADATA as language ISO
	 */
	@Override
	public void setUserLanguages(User user, ICLanguage primaryLanguage, ICLanguage secondaryLanguage, ICLanguage thirdLanguage, ICLanguage fourthLanguage) {
		if (user != null) {
			if (primaryLanguage != null && !StringUtil.isEmpty(primaryLanguage.getIsoAbbreviation())) {
				user.setMetaData(USER_PRIMARY_LANGUAGE_META_DATA_KEY, primaryLanguage.getIsoAbbreviation());
			} else {
				user.removeMetaData(USER_PRIMARY_LANGUAGE_META_DATA_KEY);
			}
			if (secondaryLanguage != null && !StringUtil.isEmpty(secondaryLanguage.getIsoAbbreviation())) {
				user.setMetaData(USER_SECONDARY_LANGUAGE_META_DATA_KEY, secondaryLanguage.getIsoAbbreviation());
			} else {
				user.removeMetaData(USER_SECONDARY_LANGUAGE_META_DATA_KEY);
			}
			if (thirdLanguage != null && !StringUtil.isEmpty(thirdLanguage.getIsoAbbreviation())) {
				user.setMetaData(USER_THIRD_LANGUAGE_META_DATA_KEY, thirdLanguage.getIsoAbbreviation());
			} else {
				user.removeMetaData(USER_THIRD_LANGUAGE_META_DATA_KEY);
			}
			if (fourthLanguage != null && !StringUtil.isEmpty(fourthLanguage.getIsoAbbreviation())) {
				user.setMetaData(USER_FOURTH_LANGUAGE_META_DATA_KEY, fourthLanguage.getIsoAbbreviation());
			} else {
				user.removeMetaData(USER_FOURTH_LANGUAGE_META_DATA_KEY);
			}
			user.store();
		}
	}

	/**
	 * Finds and returns user's primary language
	 */
	@Override
	public ICLanguage getUserPrimaryLanguage(User user) {
		if (user != null) {
			String languageISO = user.getMetaData(USER_PRIMARY_LANGUAGE_META_DATA_KEY);
			if (!StringUtil.isEmpty(languageISO)) {
				try {
					return getICLanguageHome().findByISOAbbreviation(languageISO);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Could not find the language: ", e);
				}
			}
		}
		return null;
	}

	/**
	 * Finds and returns user's secondary
	 */
	@Override
	public ICLanguage getUserSecondaryLanguage(User user) {
		if (user != null) {
			String languageISO = user.getMetaData(USER_SECONDARY_LANGUAGE_META_DATA_KEY);
			if (!StringUtil.isEmpty(languageISO)) {
				try {
					return getICLanguageHome().findByISOAbbreviation(languageISO);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Could not find the language: ", e);
				}
			}
		}
		return null;
	}

	/**
	 * Finds and returns user's third language
	 */
	@Override
	public ICLanguage getUserThirdLanguage(User user) {
		if (user != null) {
			String languageISO = user.getMetaData(USER_THIRD_LANGUAGE_META_DATA_KEY);
			if (!StringUtil.isEmpty(languageISO)) {
				try {
					return getICLanguageHome().findByISOAbbreviation(languageISO);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Could not find the language: ", e);
				}
			}
		}
		return null;
	}

	/**
	 * Finds and returns user's fourth language
	 */
	@Override
	public ICLanguage getUserFourthLanguage(User user) {
		if (user != null) {
			String languageISO = user.getMetaData(USER_FOURTH_LANGUAGE_META_DATA_KEY);
			if (!StringUtil.isEmpty(languageISO)) {
				try {
					return getICLanguageHome().findByISOAbbreviation(languageISO);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Could not find the language: ", e);
				}
			}
		}
		return null;
	}


	/**
	 * Saves the year when user came to Iceland into the METADATA
	 */
	@Override
	public void setYearWhenUserCameToIceland(User user, String year) {
		if (user != null) {
			if (!StringUtil.isEmpty(year)) {
				user.setMetaData(USER_YEAR_WHEN_USER_CAME_TO_ICELAND_META_DATA_KEY, year);
			} else {
				user.removeMetaData(USER_YEAR_WHEN_USER_CAME_TO_ICELAND_META_DATA_KEY);
			}
			user.store();
		}
	}

	/**
	 * Finds and returns the year when user came to Iceland
	 */
	@Override
	public String getYearWhenUserCameToIceland(User user) {
		if (user != null) {
			String year = user.getMetaData(USER_YEAR_WHEN_USER_CAME_TO_ICELAND_META_DATA_KEY);
			if (!StringUtil.isEmpty(year)) {
				return year;
			}
		}
		return null;
	}


} // Class UserBusiness
