/**
 *
 */
package com.idega.user.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.idega.business.SpringBeanName;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.Gender;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

@SpringBeanName("userDAO")
public interface UserDAO extends GenericDao {

	/**
	 * 
	 * <p>Updates/creates entity</p>
	 * @param user to update/persist, not <code>null</code>;
	 * @return updated created entity or <code>null</code> on failure;
	 */
	public User update(User user);
	
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

	/**
	 * 
	 * @param primaryKeys is {@link Collection} of {@link User#getId()}, 
	 * not <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 */
	List<User> findByPrimaryKeys(Collection<Integer> primaryKeys);

	/**
	 * 
	 * @param personalId {@link User#getPersonalID()} to filter by, skipped if <code>null</code>;
	 * @param firstName {@link User#getFirstName()} to filter by, skipped if <code>null</code>;
	 * @param middleName {@link User#getMiddleName()} to filter by, skipped if <code>null</code>;
	 * @param lastName {@link User#getLastName()} to filter by, skipped if <code>null</code>;
	 * @return filtered entities or {@link Collections#emptyList()} on failure;
	 */
	List<User> findFilteredBy(String personalId, String firstName,
			String middleName, String lastName);

}