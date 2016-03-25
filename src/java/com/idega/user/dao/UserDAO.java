/**
 *
 */
package com.idega.user.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.idega.business.SpringBeanName;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.location.data.bean.Address;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.Gender;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

@SpringBeanName("userDAO")
public interface UserDAO extends GenericDao {

	@Transactional(readOnly = false)
	public User createUser(String firstName, String middleName, String lastName, String displayName, String personalID, String description, Gender gender, Date dateOfBirth, Group primaryGroup);

	public User getUser(Integer userID);

	public User getUser(String personalID);

	public User getUserByUUID(String uniqueID);

	public User getUserBySHA1(String sha1);

	public List<User> getUsersByNames(String firstName, String middleName, String lastName);

	public List<User> getUsersByLastName(String lastName);

	public Email getUsersMainEmail(User user);

	public Email updateUserMainEmail(User user, String address);

	public Gender getGender(String name);

	public Gender getMaleGender();

	public Gender getFemaleGender();

	public User getByEmailAddress(String emailAddress);

	public Address getUsersMainAddress(User user);

	/**
	 *
	 * @param primaryKeys is {@link Collection} of {@link User#getId()},
	 * not <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	public List<User> findAll(Collection<Integer> primaryKeys);

	/**
	 *
	 * <p>A workaround to solve problems of bad emails in data source</p>
	 * @param user
	 * @return {@link Set} of correct email addresses
	 */
	Set<String> getEmailAddresses(User user);

	List<User> findByPhoneNumber(String phoneNumber);

}