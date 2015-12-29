/**
 *
 */
package com.idega.user.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.contact.dao.ContactDAO;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.EmailType;
import com.idega.core.location.dao.AddressDAO;
import com.idega.core.location.data.bean.Address;
import com.idega.core.location.data.bean.AddressType;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.bean.Gender;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.user.data.bean.UserGroupRepresentative;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

@Repository("userDAO")
@Transactional(readOnly = true)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UserDAOImpl extends GenericDaoImpl implements UserDAO {

	@Autowired
	private ContactDAO contactDAO;

	@Autowired
	private AddressDAO addressDAO;

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
		Param param = new Param("personalID", personalID);
		return getSingleResult("user.findByPersonalID", User.class, param);
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

	@Override
	public Email updateUserMainEmail(User user, String address) {
		Email email = getUsersMainEmail(user);
		if (email == null) {
			email = contactDAO.createEmail(address, contactDAO.getMainEmailType());
			persist(email);
		} else {
			email.setAddress(address);
			merge(email);
		}

		List<Email> emails = user.getEmails();
		if (emails == null) {
			emails = new ArrayList<Email>();
		}
		emails.add(email);
		user.setEmails(emails);
		merge(user);

		return email;
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
}