/**
 * 
 */
package com.idega.user.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.contact.dao.ContactDAO;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.bean.Gender;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.user.data.bean.UserGroupRepresentative;

@Scope("singleton")
@Repository("userDAO")
@Transactional(readOnly = true)
public class UserDAOImpl extends GenericDaoImpl implements UserDAO {

	@Autowired
	ContactDAO contactDAO;
	
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
	
	public User getUser(Integer userID) {
		return find(User.class, userID);
	}

	public User getUser(String personalID) {
		Param param = new Param("personalID", personalID);
		return getSingleResult("user.findByPersonalID", User.class, param);
	}

	public User getUserByUUID(String uniqueID) {
		Param param = new Param("uniqueId", uniqueID);
		return getSingleResult("user.findByUniqueID", User.class, param);
	}

	public List<User> getUsersByNames(String firstName, String middleName, String lastName) {
		Param param1 = new Param("firstName", firstName);
		Param param2 = new Param("middleName", middleName);
		Param param3 = new Param("lastName", lastName);
		return getResultList("user.findByNames", User.class, param1, param2, param3);
	}
	
	public Email getUsersMainEmail(User user) {
		return contactDAO.findEmailForUserByType(user, contactDAO.getMainEmailType());
	}
	
	public Email updateUserMainEmail(User user, String address) {
		Email email = getUsersMainEmail(user);
		if (email == null) {
			email = contactDAO.createEmail(address, contactDAO.getMainEmailType());
		}
		else {
			email.setAddress(address);
		}
		persist(email);
		
		user.getEmails().add(email);
		persist(user);
		
		return email;
	}
	
	public Gender getGender(String name) {
		Param param = new Param("name", name);

		return getSingleResult("gender.findByName", Gender.class, param);
	}
	
	public Gender getMaleGender() {
		return getGender(Gender.NAME_MALE);
	}
	
	public Gender getFemaleGender() {
		return getGender(Gender.NAME_FEMALE);
	}
}