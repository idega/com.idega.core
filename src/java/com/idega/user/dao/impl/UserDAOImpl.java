/**
 *
 */
package com.idega.user.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.ejb.CreateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.contact.dao.ContactDAO;
import com.idega.core.contact.dao.EMailDAO;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.EmailType;
import com.idega.core.location.dao.AddressDAO;
import com.idega.core.location.data.bean.Address;
import com.idega.core.location.data.bean.AddressType;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.data.bean.Metadata;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.user.business.UserBusiness;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;
import com.idega.user.data.UserHome;
import com.idega.user.data.bean.Gender;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.user.data.bean.UserGroupRepresentative;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

@Repository("userDAO")
@Transactional(readOnly = true)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UserDAOImpl extends GenericDaoImpl implements UserDAO {

	public static final String PROPERTY_UNASSIGNED_MEMBERS_GROUP_ID = "unassigned_members_group_id";

	@Autowired
	private ContactDAO contactDAO;

	@Autowired
	private AddressDAO addressDAO;

	@Autowired
	private EMailDAO eMailDAO;

	@Autowired
	private GroupDAO groupDAO;

	private GroupHome getGroupHome() {
		try {
			return (GroupHome) IDOLookup.getHome(com.idega.user.data.Group.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, "Failed to get " + GroupHome.class + " cuase of: ", e);
		}

		return null;
	}

	private GroupTypeHome getGroupTypeHome() {
		try {
			return (GroupTypeHome) IDOLookup.getHome(GroupType.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, "Failed to get " + GroupTypeHome.class + " cuase of: ", e);
		}

		return null;
	}

	private GroupDAO getGroupDAO() {
		if (this.groupDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.groupDAO;
	}

	private EMailDAO getEMailDAO() {
		if (this.eMailDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.eMailDAO;
	}

	private UserBusiness getUserBusiness() {
		try {
			return IBOLookup.getServiceInstance(
					IWMainApplication.getDefaultIWApplicationContext(),
					UserBusiness.class);
		} catch (IBOLookupException e) {
			getLogger().log(Level.WARNING, "Failed to get " + UserBusiness.class + " cause of: ", e);
		}

		return null;
	}

	private IWMainApplicationSettings getSettings() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings();
	}

	private Integer getUnassignedMembersGroupId() {
		IWMainApplicationSettings settings = getSettings();
		if (settings != null) {
			String property = settings.getProperty(PROPERTY_UNASSIGNED_MEMBERS_GROUP_ID, "-1");
			if (property != null) {
				return Integer.valueOf(property);
			}
		}

		return null;
	}

	private void setUnassignedMembersGroupId(Integer id) {
		IWMainApplicationSettings settings = getSettings();
		if (settings != null && id != null) {
			settings.setProperty(PROPERTY_UNASSIGNED_MEMBERS_GROUP_ID, id.toString());
		}
	}

	private Group getUnassignedMembersGroup() {
		Group group = getGroupDAO().findGroup(getUnassignedMembersGroupId());
		if (group == null) {
			com.idega.user.data.Group newGroup = null;
			try {
				newGroup = getGroupHome().create();
			} catch (CreateException e) {
				getLogger().log(Level.WARNING, "Failed to create group, cause of:", e);
			}

			if (newGroup != null) {
				newGroup.setCreated(new Timestamp(System.currentTimeMillis()));
				newGroup.setDescription("Group for containing members without any access rights");
				newGroup.setName("Unassigned Members Group");
				newGroup.setGroupType(getGroupTypeHome().getPermissionGroupTypeString());
				newGroup.setIsPermissionControllingGroup(Boolean.FALSE);
				newGroup.setUniqueId(UUID.randomUUID().toString());

				try {
					newGroup.store();
				} catch (IDOStoreException e) {
					getLogger().log(Level.WARNING, "Failed to save group, cause of:", e);
				}

				setUnassignedMembersGroupId((Integer) newGroup.getPrimaryKey());
				group = getUnassignedMembersGroup();
			}
		}

		return group;
	}

	private UserHome getUserHome() {
		try {
			return (UserHome) IDOLookup.getHome(com.idega.user.data.User.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, "Failed to get " + UserHome.class + " cause of: ", e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public User createUser(String firstName, String middleName, String lastName, String displayName, String personalID, String description, Gender gender, Date dateOfBirth, Group primaryGroup) {
		UserGroupRepresentative group = new UserGroupRepresentative();
		persist(group);

		User user = new User();
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setDisplayName(displayName);
		user.setPersonalID(personalID);
		user.setDescription(description);
		user.setGender(gender);
		user.setDateOfBirth(dateOfBirth);
		user.setPrimaryGroup(primaryGroup);
		user.setGroup(group);
		persist(user);

		return user;
	}



	@Override
	public User getUser(Integer userID) {
		if (userID != null) {
			return find(User.class, userID);
		}

		return null;
	}

	@Override
	public User getUser(String personalID) {
		if (!StringUtil.isEmpty(personalID)) {
			Param param = new Param("personalID", personalID);
			return getSingleResult("user.findByPersonalID", User.class, param);
		}

		return null;
	}

	@Override
	public User getUserByUUID(String uniqueID) {
		Param param = new Param("uniqueId", uniqueID);
		return getSingleResult("user.findByUniqueID", User.class, param);
	}

	@Override
	public List<User> getUsersByNames(String firstName, String middleName, String lastName) {
		Param param1 = new Param("firstName", firstName);
		Param param2 = new Param("middleName", middleName);
		Param param3 = new Param("lastName", lastName);
		return getResultList("user.findByNames", User.class, param1, param2, param3);
	}

	@Override
	public List<User> getUsersByLastName(String lastName) {
		return getResultList("user.findByLastName", User.class, new Param("lastName", lastName));
	}

	@Override
	public Email getUsersMainEmail(User user) {
		EmailType mainEmailType = contactDAO.getMainEmailType();
		return contactDAO.findEmailForUserByType(user, mainEmailType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#updateUserMainEmail(com.idega.user.data.bean.User, java.lang.String)
	 */
	@Override
	public Email updateUserMainEmail(User user, String address) {
		Email email = getUsersMainEmail(user);
		if (email != null) {
			getEMailDAO().update(email.getId(), null, null,	"sub", null);
		}

		return getEMailDAO().update(null,
				user.getId(),
				address,
				contactDAO.getMainEmailType());
	}

	@Override
	public Email updateUserMainEmailAddress(User user, String address) {
		Email email = getUsersMainEmail(user);
		if(email == null) {
			return getEMailDAO().createEmail(user.getId(), address, contactDAO.getMainEmailType());
		}

		List<User> emailUsers = email.getUsers();
		if(emailUsers.size() == 1 && emailUsers.get(0).getId().intValue() == user.getId().intValue()) {
			return getEMailDAO().update(email.getId(), user.getId(), address, contactDAO.getMainEmailType());
		} else {
			return getEMailDAO().createEmail(user.getId(), address, contactDAO.getMainEmailType());
		}
	}

	@Override
	public Gender getGender(String name) {
		Param param = new Param("name", name);

		return getSingleResult("gender.findByName", Gender.class, param);
	}

	@Override
	public Gender getMaleGender() {
		return getGender(Gender.NAME_MALE);
	}

	@Override
	public Gender getFemaleGender() {
		return getGender(Gender.NAME_FEMALE);
	}

	@Override
	public User getUserBySHA1(String sha1) {
		if (StringUtil.isEmpty(sha1))
			return null;

		return getSingleResultByInlineQuery("from " + User.class.getName() + " u where u.sha1 = :sha1", User.class, new Param("sha1", sha1));
	}

	@Override
	public User getByEmailAddress(String emailAddress) {
		List<Email> emails = getResultList("email.findByAddress", Email.class, new Param("address", emailAddress));
		if (ListUtil.isEmpty(emails))
			return null;

		for (Email email: emails) {
			List<User> users = email.getUsers();
			initialize(users);
			if (!ListUtil.isEmpty(users)) {
				return users.get(0);
			}
		}

		return null;
	}

	@Override
	public Address getUsersMainAddress(User user) {
		if (user == null) {
			return null;
		}

		AddressType mainAddressType = addressDAO.getMainAddressType();
		if (mainAddressType == null) {
			return null;
		}

		return getSingleResult(Address.QUERY_FIND_BY_USER_AND_ADDRESS_TYPE, Address.class, new Param("userID", user.getId()), new Param("addressType", mainAddressType));
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#findAll(java.util.Collection)
	 */
	@Override
	public List<User> findAll(Collection<Integer> primaryKeys) {
		ArrayList<User> users = new ArrayList<User>();

		if (!ListUtil.isEmpty(primaryKeys)) {
			return getResultList(User.QUERY_FIND_BY_PRIMARY_KEYS, User.class,
					new Param("primaryKeys", primaryKeys));
		}

		return users;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#getEmailAddresses(com.idega.user.data.bean.User)
	 */
	@Override
	public Set<String> getEmailAddresses(User user) {
		Set<String> emailAddresses = new HashSet<String>();

		if (user != null) {
			List<Email> emails = user.getEmails();
			if (!ListUtil.isEmpty(emails)) {
				for (Email email : emails) {
					String emailAddress = email.getEmailAddress();

					String[] splittedAddresses = emailAddress.split("[\\s,;]+");
					for (String splittedAddress : splittedAddresses) {
						if (splittedAddress.contains(CoreConstants.DOT) && splittedAddress.contains(CoreConstants.AT)) {
							emailAddresses.add(splittedAddress);
						}
					}
				}
			}
		}

		return emailAddresses;
	}

	@Override
	public List<User> findByPhoneNumber(String phoneNumber) {
		if (StringUtil.isEmpty(phoneNumber)) {
			return null;
		}

		return getResultList(User.QUERY_FIND_BY_PHONE_NUMBER, User.class, new Param("number", phoneNumber));
	}

	@Override
	@Transactional(readOnly = false)
	public Metadata setMetadata(User user, String key, String value, String type) {
		if (user == null || StringUtil.isEmpty(key)) {
			return null;
		}

		Metadata metadata = user.setMetadata(key, value, type);
		if (metadata.getId() == null) {
			persist(metadata);
		} else {
			merge(metadata);
		}

		if (metadata.getId() == null) {
			return null;
		}

		merge(user);

		return metadata;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#update(com.idega.user.data.bean.User)
	 */
	@Override
	@Transactional(readOnly = false)
	public User update(User entity) {
		if (entity == null) {
			getLogger().warning("Entity not provided");
			return null;
		}

		if (entity.getId() == null || getUser(entity.getId()) == null) {
			persist(entity);
			if (entity != null && entity.getId() != null) {
				getLogger().fine("Entity: " + entity + " created!");
				return entity;
			}
		} else {
			entity = merge(entity);
			if (entity != null && entity.getId() != null) {
				getLogger().fine("Entity: " + entity + " updated");
				return entity;
			}
		}

		getLogger().warning("Failed to create/update entity: " + entity);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#findByPrimaryKeys(java.util.Collection)
	 */
	@Override
	public List<User> findByPrimaryKeys(Collection<Integer> primaryKeys) {
		if (!ListUtil.isEmpty(primaryKeys)) {
			return getResultListByInlineQuery(
					"FROM " + User.class.getName() + " u "
							+ "WHERE u.userID IN (:primaryKeys)" ,
							User.class, new Param("primaryKeys", primaryKeys));
		}

		return Collections.emptyList();
	}

	private String getLikeExpression(String value) {
		if (StringUtil.isEmpty(value)) {
			return value;
		}

		if (!value.startsWith(CoreConstants.PERCENT)) {
			value = CoreConstants.PERCENT.concat(value);
		}
		if (!value.endsWith(CoreConstants.PERCENT)) {
			value = value.concat(CoreConstants.PERCENT);
		}

		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#findFilteredBy(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<User> findFilteredBy(String personalId, String firstName, String middleName, String lastName, Integer firstResult, Integer maxResults) {
		ArrayList<Param> parameters = new ArrayList<Param>();
		StringBuilder query = new StringBuilder("FROM " + User.class.getName() + " u ");
		query.append("WHERE u.deletedWhen IS NULL ");

		if (!StringUtil.isEmpty(firstName)) {
			parameters.add(new Param("firstName", getLikeExpression(firstName)));
			query.append("AND u.firstName like :firstName ");
		}

		if (!StringUtil.isEmpty(middleName)) {
			parameters.add(new Param("middleName", getLikeExpression(middleName)));
			query.append("AND u.middleName like :middleName ");
		}

		if (!StringUtil.isEmpty(lastName)) {
			parameters.add(new Param("lastName", getLikeExpression(lastName)));
			query.append("AND u.lastName like :lastName ");
		}

		if (!StringUtil.isEmpty(personalId)) {
			parameters.add(new Param("personalId", getLikeExpression(personalId)));
			query.append("AND u.personalID like :personalId ");
		}

		return getResultListByInlineQuery(
				query.toString(),
				User.class,
				firstResult,
				maxResults,
				query.toString(),
				parameters.toArray(new Param[parameters.size()])
		);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#findFilteredBy(java.lang.String)
	 */
	@Override
	public List<User> findFilteredBy(String anyColumn, Integer firstResult, Integer maxResults) {
		if (StringUtil.isEmpty(anyColumn)) {
			return Collections.emptyList();
		}

		anyColumn = getLikeExpression(anyColumn);

		StringBuilder query = new StringBuilder("FROM " + User.class.getName() + " u ");
		query.append("WHERE u.firstName like :param ");
		query.append("OR u.middleName like :param ");
		query.append("OR u.lastName like :param ");
		query.append("OR u.personalID like :param ");

		return getResultListByInlineQuery(
				query.toString(),
				User.class,
				firstResult,
				maxResults,
				query.toString(),
				new Param("param", anyColumn)
		);
	}

	@Override
	public List<User> getUsersByEmailAddress(String emailAddress) {
		List<Email> emails = getResultList("email.findByAddress", Email.class, new Param("address", emailAddress));
		if (ListUtil.isEmpty(emails))
			return null;

		List<User> usersList = new ArrayList<User>();

		for (Email email: emails) {
			List<User> users = email.getUsers();
			initialize(users);
			if (!ListUtil.isEmpty(users)) {
				usersList.addAll(users);
			}
		}

		return usersList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.dao.UserDAO#update(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = false)
	public User update(Integer id, String personalId, String eMail, String name, String gender) {
		User user = null;
		if (id != null) {
			user = find(User.class, id);
		}

		if (user == null) {
			user = getUser(personalId);
		}

		if (user == null) {
			List<User> users = getUsersByEmailAddress(eMail);
			if (!ListUtil.isEmpty(users)) {
				if (users.size() > 1) {
					throw new UnsupportedOperationException("Can't handle emails with more than one owner");
				}

				user = users.iterator().next();
			}
		}

		if (user == null) {
			com.idega.user.data.User ejbUser = null;

			try {
				String[] splittedName = name.split(CoreConstants.SPACE);
				if (splittedName.length > 2) {
					ejbUser = getUserBusiness().createUser(splittedName[0], splittedName[1], splittedName[2], personalId);
				} else if (splittedName.length > 1) {
					ejbUser = getUserBusiness().createUser(splittedName[0], null, splittedName[1], personalId);
				} else if (splittedName.length > 0) {
					ejbUser = getUserBusiness().createUser(splittedName[0], null, null, personalId);
				}
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Failed to create user, cause of:", e);
			}

			if (ejbUser != null) {
				user = find(User.class, Integer.valueOf(ejbUser.getPrimaryKey().toString()));
			}
		}

		if (!StringUtil.isEmpty(personalId)) {
			user.setPersonalID(personalId);
		}

		if (!StringUtil.isEmpty(name)) {
			user.setDisplayName(name);

			String[] splittedName = name.split(CoreConstants.SPACE);
			if (splittedName.length > 2) {
				user.setFirstName(splittedName[0]);
				user.setMiddleName(splittedName[1]);
				user.setLastName(splittedName[2]);
			} else if (splittedName.length > 1) {
				user.setFirstName(splittedName[0]);
				user.setLastName(splittedName[1]);
			} else if (splittedName.length > 0) {
				user.setFirstName(splittedName[0]);
			}
		}

		if (!StringUtil.isEmpty(gender)) {
			if ("M".equalsIgnoreCase(gender)) {
				user.setGender(getMaleGender());
			} else if ("F".equalsIgnoreCase(gender)) {
				user.setGender(getFemaleGender());
			}
		}

		user = update(user);
		if (user != null) {
			getEMailDAO().update(null, user.getId(), eMail);
		}

		return user;
	}
}