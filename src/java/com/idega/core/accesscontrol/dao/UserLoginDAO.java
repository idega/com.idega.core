/**
 *
 */
package com.idega.core.accesscontrol.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.idega.business.SpringBeanName;
import com.idega.core.accesscontrol.data.bean.LoginInfo;
import com.idega.core.accesscontrol.data.bean.LoginRecord;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.User;

@SpringBeanName("userLoginDAO")
public interface UserLoginDAO extends GenericDao {

	@Transactional(readOnly = false)
	public UserLogin createLogin(User user, String username, String password, boolean accountEnabled, boolean allowedToChange, boolean changeNextTime, int daysOfValidity, boolean passwordExpires) throws UsernameExistsException;

	@Transactional(readOnly = false)
	public UserLogin createLogin(
			User user,
			String username,
			String password,
			boolean accountEnabled,
			boolean allowedToChange,
			boolean changeNextTime,
			int daysOfValidity,
			boolean passwordExpires,
			String type
	) throws UsernameExistsException;

	public UserLogin findLogin(Integer loginID);

	public UserLogin findLoginForUser(User user);

	public List<UserLogin> findAllLoginsForUser(User user);

	public UserLogin findLoginByUsername(String username);

	public UserLogin findLoginByUserAndUsername(User user, String username);

	@Transactional(readOnly = false)
	public LoginRecord createLoginRecord(UserLogin login, String ipAddress, User performer);

	@Transactional(readOnly = false)
	public boolean createLogoutRecord(LoginRecord record);

	public LoginRecord getLastRecordByUser(User user);

	public LoginRecord getLastRecordByLogin(UserLogin userLogin);

	public Integer getNumberOfLogins(User user);

	public void updateFailedLoginAttempts(LoginInfo info, int attempts);

	public void setAccountValidity(LoginInfo info, boolean enabled);

	public UserLogin getDefaultLoginByUUId(String UUId);

	public void enableUserLogin(String UUId);
	public void changeLoginPassword(Integer loginID,String password);
}