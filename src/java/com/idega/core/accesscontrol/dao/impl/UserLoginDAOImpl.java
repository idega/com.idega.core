/**
 *
 */
package com.idega.core.accesscontrol.dao.impl;

import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.accesscontrol.dao.UserLoginDAO;
import com.idega.core.accesscontrol.dao.UsernameExistsException;
import com.idega.core.accesscontrol.data.bean.LoginInfo;
import com.idega.core.accesscontrol.data.bean.LoginRecord;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.data.bean.User;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("userLoginDAO")
@Transactional(readOnly = true)
public class UserLoginDAOImpl extends GenericDaoImpl implements UserLoginDAO {

	@Override
	public UserLogin createLogin(
			User user,
			String username,
			String password,
			boolean accountEnabled,
			boolean allowedToChange,
			boolean changeNextTime,
			int daysOfValidity,
			boolean passwordExpires
	) throws UsernameExistsException {
		return createLogin(user, username, password, accountEnabled, allowedToChange, changeNextTime, daysOfValidity, passwordExpires, null);
	}

	@Override
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
	) throws UsernameExistsException {
		UserLogin login = findLoginForUser(user);
		if (login == null) {
			if (doesUsernameExist(username)) {
				throw new UsernameExistsException(user, username);
			}
			login = new UserLogin();
			login.setUser(user);
		}
		login.setLoginType(type);
		login.setUserLogin(username);
		login.setUserPassword(password);
		persist(login);

		LoginInfo info = login.getLoginInfo();
		if (info == null) {
			info = new LoginInfo();
			info.setUserLogin(login);
		}
		info.setAccountEnabled(accountEnabled);
		info.setAllowedToChange(allowedToChange);
		info.setChangeNextTime(changeNextTime);
		info.setDaysOfValidity(daysOfValidity);
		info.setPasswordExpires(passwordExpires);
		persist(info);

		return login;
	}

	@Override
	public UserLogin findLogin(Integer loginID) {
		return find(UserLogin.class, loginID);
	}

	@Override
	public UserLogin findLoginForUser(User user) {
		Param param = new Param("user", user);
		return getSingleResult("login.findDefaultLoginForUser", UserLogin.class, param);
	}

	@Override
	public List<UserLogin> findAllLoginsForUser(User user) {
		if (user == null) {
			getLogger().warning("User is not provided");
			return null;
		}

		Param param = new Param("user", user);
		return getResultList("login.findAllByUser", UserLogin.class, param);
	}

	@Override
	public UserLogin findLoginByUsername(String username) {
		Param param = new Param("userLogin", username);
		return getSingleResult("login.findByLogin", UserLogin.class, param);
	}

	@Override
	public UserLogin findLoginByUserAndUsername(User user, String username) {
		Param param1 = new Param("user", user);
		Param param2 = new Param("userLogin", username);
		return getSingleResult("login.findByUserAndLogin", UserLogin.class, param1, param2);
	}

	private boolean doesUsernameExist(String username) {
		return findLoginByUsername(username) != null;
	}

	@Override
	@Transactional(readOnly = false)
	public LoginRecord createLoginRecord(UserLogin login, String ipAddress, User performer) {
		LoginRecord record = new LoginRecord();
		record.setLogin(login);
		record.setIpAddress(StringUtils.replace(ipAddress, "%", ""));
		record.setUser(performer);
		record.setInStamp(IWTimestamp.getTimestampRightNow());
		persist(record);

		return record;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean createLogoutRecord(LoginRecord record) {
		record.setOutStamp(IWTimestamp.getTimestampRightNow());
		persist(record);

		return true;
	}

	@Override
	public LoginRecord getLastRecordByUser(User user) {
		Param param = new Param("user", user);
		return getSingleResult(LoginRecord.FIND_LAST_BY_USER, LoginRecord.class, param);
	}

	@Override
	public Integer getNumberOfLogins(User user) {
		if (user == null) {
			return null;
		}

		return getSingleResult(LoginRecord.GET_NUMBER_OF_LOGINS_FOR_USER, Integer.class, new Param("user", user));
	}

	@Override
	public LoginRecord getLastRecordByLogin(UserLogin userLogin) {
		Param param = new Param("login", userLogin);
		return getSingleResult("loginRecord.findLastByLogin", LoginRecord.class, param);
	}

	@Override
	public void updateFailedLoginAttempts(LoginInfo info, int attempts) {
		info.setFailedAttemptCount(attempts);
		persist(info);
	}

	@Override
	public void setAccountValidity(LoginInfo info, boolean enabled) {
		info.setAccountEnabled(enabled);
		persist(info);
	}

	@Override
	public UserLogin getDefaultLoginByUUId(String uuid) {
		if (StringUtil.isEmpty(uuid)) {
			return null;
		}

		try {
			List<UserLogin> logins = getResultList(UserLogin.QUERY_FIND_DEFAULT_LOGIN_BY_UUID, UserLogin.class, new Param(User.PROP_UNIQUE_ID, uuid));
			return ListUtil.isEmpty(logins) ? null : logins.get(0);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting login by uuid " + uuid, e);
		}

		return null;
	}

	@Override
	@Transactional
	public void enableUserLogin(String UUId){
		UserLogin userLogin = getDefaultLoginByUUId(UUId);
		userLogin.getLoginInfo().setAccountEnabled(true);
		persist(userLogin);
	}

	@Override
	@Transactional(readOnly=false)
	public void changeLoginPassword(Integer loginID,String password){
		UserLogin userLogin = findLogin(loginID);
		userLogin.setUserPassword(password);
		merge(userLogin);
	}

	@Override
	public UserLogin findByPassword(String password) {
		if (StringUtil.isEmpty(password)) {
			return null;
		}

		try {
			List<UserLogin> logins = getResultList(UserLogin.QUERY_FIND_BY_PASSWORD, UserLogin.class, new Param("password", password));
			return ListUtil.isEmpty(logins) ? null : logins.get(0);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting login by password " + password, e);
		}

		return null;
	}
}