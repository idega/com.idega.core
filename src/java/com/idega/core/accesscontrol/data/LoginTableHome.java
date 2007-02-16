package com.idega.core.accesscontrol.data;


import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOException;
import com.idega.data.IDOHome;
import com.idega.user.data.User;

public interface LoginTableHome extends IDOHome {
	public LoginTable create() throws CreateException;

	public LoginTable findByPrimaryKey(Object pk) throws FinderException;

	public LoginTable findLoginForUser(User user) throws FinderException;

	public Collection findLoginsForUser(User user) throws FinderException;

	public LoginTable findLoginForUser(int userID) throws FinderException;

	public Collection findLoginsForUser(int userID) throws FinderException;

	public int getNumberOfLogins(String userName) throws IDOException;

	public LoginTable findByLogin(String login) throws FinderException;

	public LoginTable findByUserAndLogin(User user, String login) throws FinderException;

	public LoginTable findByUserAndType(User user, String loginType) throws FinderException;

	public LoginTable findDefaultLoginForUser(int userID) throws FinderException;

	public LoginTable findDefaultLoginForUser(User user) throws FinderException;
}