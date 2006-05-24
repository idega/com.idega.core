/*
 * $Id: LoginDBHandler.java,v 1.67 2006/05/24 07:46:54 laddi Exp $
 * 
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.core.accesscontrol.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.LoginInfoHome;
import com.idega.core.accesscontrol.data.LoginRecord;
import com.idega.core.accesscontrol.data.LoginRecordHome;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.user.data.User;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORemoveException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;
import com.idega.util.StringHandler;

/**
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst
 *         Sæmundsson </a>
 * @version 1.0
 */
public class LoginDBHandler {

	public static final String LOGIN_USE_PID_AS_GENERATED = "USE_PID_AS_GENERATED_LOGIN";

	public LoginDBHandler() {
	}

	private static LoginTableHome getLoginTableHome() {
		try {
			return (LoginTableHome) IDOLookup.getHome(LoginTable.class);
		}
		catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	private static LoginInfoHome getLoginInfoHome() {
		try {
			return (LoginInfoHome) IDOLookup.getHome(LoginInfo.class);
		}
		catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	/**
	 * @deprecated replaced with createUserLogin()
	 */
	protected static LoginTable createLogin(boolean update, int userID, String userLogin, String password) throws LoginCreateException {
		return createUserLogin(update, userID, userLogin, password);
	}

	protected static LoginTable createUserLogin(boolean update, int userID, String userLogin, String password) throws LoginCreateException {
		LoginTable loginTable = null;
		try {
			loginTable = getLoginTableHome().findLoginForUser(userID);
		}
		catch (FinderException fe) {
			// No login found for user...
		}
		
		if (update) {
			if (loginTable == null) {
				throw new LoginCreateException("User_id : " + userID + " , cannot update login : cannot find login");
			}
		}
		else {
			try {
				loginTable = getLoginTableHome().create();
			}
			catch (CreateException ce) {
				throw new LoginCreateException(ce.getMessage());
			}
		}
		
		if (userID > 0 && !update) {
			loginTable.setUserId(userID);
		}
		else if (!update) {
			throw new LoginCreateException("invalid user_id");
		}
		
		String encryptedPassword = null;
		if (password != null && !"".equals(password)) {
			encryptedPassword = Encrypter.encryptOneWay(password);
			loginTable.setUserPassword(encryptedPassword, password);
		}
		else if (!update) {
			throw new LoginCreateException("Password not valid");
		}
		if (update) {
			if (userLogin != null && !"".equals(userLogin)) {
				if (!loginTable.getUserLogin().equals(userLogin)) {
					try {
						loginTable = getLoginTableHome().findByLogin(userLogin);
					}
					catch (FinderException e) {
						throw new LoginCreateException(e.getMessage());
					}
					if (loginTable != null && loginTable.getUserId() != userID) {
						LoginTable tempLoginTable = loginTable;
						if (tempLoginTable.getUserId() != userID) {
							throw new LoginCreateException("login not valid : in use");
						}
					}
				}
				if (encryptedPassword != null) {
					loginTable.setUserPassword(encryptedPassword, password);
					loginTable.setUserLogin(userLogin);
				}
				else {
					throw new LoginCreateException("Password not valid");
				}
			}
		}
		else {
			if (userLogin != null && !"".equals(userLogin)) {
				try {
					loginTable = getLoginTableHome().findByLogin(userLogin);
					throw new LoginCreateException("login not valid : in use");
				}
				catch (FinderException e) {
					//No login found with user login...
				}
				loginTable.setUserLogin(userLogin);
			}
			else {
				throw new LoginCreateException("Login not valid: null or emptyString");
			}
		}
		loginTable.setLastChanged(IWTimestamp.getTimestampRightNow());
		loginTable.store();
		return loginTable;
	}

	protected static int createLoginInfo(boolean update, LoginTable login, Boolean accountEnabled, IWTimestamp modified, int daysOfVality, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {
		LoginInfo logInfo = null;
		try {
			logInfo = getLoginInfoHome().findByPrimaryKey(login.getPrimaryKey());
		}
		catch (FinderException fe) {
		}
		if (update) {
			if (logInfo == null) {
				throw new Exception("login_id : " + login.getPrimaryKey() + " , cannot update loginInfo : cannot find loginInfo");
			}
			Object pk = logInfo.getPrimaryKey();
			if (!pk.equals(login.getPrimaryKey()) || logInfo == null) {
				throw new Exception("LoginInfo update failed. ");
			}
		}
		else {
			if (logInfo != null) {
				throw new Exception("login_id : " + login.getPrimaryKey() + " , cannot create new loginInfo : user has one already");
			}
			logInfo = ((com.idega.core.accesscontrol.data.LoginInfoHome) com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).create();
		}
		logInfo.setLoginTable(login);
		if (accountEnabled != null) {
			logInfo.setAccountEnabled(accountEnabled);
		}
		if (modified != null) {
			logInfo.setModified(modified);
		}
		else {
			logInfo.setModified(IWTimestamp.RightNow());
		}
		if (daysOfVality > -1) {
			logInfo.setDaysOfVality(daysOfVality);
		}
		if (passwordExpires != null) {
			logInfo.setPasswordExpires(passwordExpires);
		}
		if (userAllowedToChangePassw != null) {
			logInfo.setAllowedToChange(userAllowedToChangePassw);
		}
		if (changeNextTime != null) {
			logInfo.setChangeNextTime(changeNextTime);
		}
		if (encryptionType != null) {
			logInfo.setEncriptionType(encryptionType);
		}
		if (!logInfo.getAllowedToChange() && logInfo.getChangeNextTime()) {
			throw new Exception("inconsistency: userAllowedToChangePassw = false and changeNextTime = true");
		}
		try {
			if (update) {
				logInfo.store();
			}
			else {
				logInfo.store();
			}
		}
		catch (Exception ex) {
			if (update) {
				throw new Exception("LoginInfo update failed. ");
			}
			else {
				throw new Exception("LoginInfo creation failed. ");
			}
		}
		int primaryKey = -1;
		Object pk = logInfo.getPrimaryKey();
		if (pk != null) {
			primaryKey = ((Integer) pk).intValue();
		}
		return primaryKey;
	}

	public static LoginTable createLogin(int user, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws LoginCreateException, RemoteException {
		return createLogin(user, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType, null);
	}

	public static LoginTable createLogin(com.idega.user.data.User user, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {
		int userId = ((Integer) user.getPrimaryKey()).intValue();
		return createLogin(userId, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType, null);
	}

	public static LoginTable createLogin(com.idega.user.data.User user, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType, String loginType) throws Exception {
		return createLogin(user, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType, null);
	}

	public static LoginTable createLogin(int userID, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType, String loginType) throws LoginCreateException, RemoteException {
		LoginTable login = createUserLogin(false, userID, userLogin, password);
		try {
			createLoginInfo(false, login, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
		}
		catch (Exception e) {
			if ("LoginInfo creation failed. ".equals(e.getMessage())) {
				try {
					login.remove();
					throw new LoginCreateException(e.getMessage() + "LoginTable entry was removed");
				}
				catch (RemoveException re) {
					re.printStackTrace();
					throw new LoginCreateException(e.getMessage() + "Transaction faild: LoginTable entry failed to remove");
				}
			}
			else {
				throw new LoginCreateException(e.getMessage());
			}
		}
		return login;
	}

	/**
	 * @deprecated
	 */
	public static void updateLogin(int userID, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {
		updateLogin(userID, userLogin, password, accountEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, null);
	}

	public static void updateLogin(int userID, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {
		LoginTable loginTableID = createLogin(true, userID, userLogin, password);
		createLoginInfo(true, loginTableID, accountEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
	}

	/**
	 * @deprecated
	 */
	public static void updateLoginInfo(LoginTable login, Boolean accoutEnabled, IWTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {
		updateLoginInfo(login, accoutEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, null);
	}

	public static void updateLoginInfo(LoginTable login, Boolean accoutEnabled, IWTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {
		createLoginInfo(true, login, accoutEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
	}

	public static LoginTable createLogin(com.idega.user.data.User user, String userLogin, String password) throws LoginCreateException, RemoteException {
		int userID = ((Integer) user.getPrimaryKey()).intValue();
		return createLogin(userID, userLogin, password);
	}

	public static LoginTable createLogin(int userID, String userLogin, String password) throws LoginCreateException, RemoteException {
		return createLogin(userID, userLogin, password, null, null, -1, null, null, null, null);
	}

	public static void updateLogin(int userID, String userLogin, String password) throws Exception {
		createLogin(true, userID, userLogin, password);
	}

	public static void changePassword(int userID, String password) throws Exception {
		LoginTable loginTable = getLoginTableHome().findLoginForUser(userID);
		changePassword(loginTable, password);
	}

	public static void changePassword(LoginTable login, String password) throws Exception {
		if (login != null) {
			login.setUserPassword(Encrypter.encryptOneWay(password));
			login.store();
		}
		else {
			throw new Exception("Cannot update. Login does not exist");
		}
	}

	/**
	 * @deprecated use getUserLogin
	 */
	public static LoginTable findUserLogin(int iUserId) {
		return getUserLogin(iUserId);
	}

	public static LoginTable getUserLoginByUserName(String userName) {
		try {
			return getLoginTableHome().findByLogin(userName);
		}
		catch (FinderException ex) {
		}
		return null;
	}

	public static LoginTable getUserLogin(int userId) {
		try {
			return getLoginTableHome().findLoginForUser(userId);
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * Finds and returns the first found login for user user unregarding
	 * login type.
	 * </p>
	 * @param user
	 * @return
	 */
	public static LoginTable getUserLogin(User user) {
		try {
			return getLoginTableHome().findLoginForUser(user);
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>
	 * Finds and returns the first found login for user user 
	 * with no set loginType.
	 * </p>
	 * @param user
	 * @return
	 */
	public static LoginTable getDefaultUserLogin(User user) {
		try {
			return getLoginTableHome().findDefaultLoginForUser(user);
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>
	 * Finds and returns the first found login for user user 
	 * with no set loginType.
	 * </p>
	 * @param user
	 * @return
	 */
	public static LoginTable getDefaultUserLogin(int userId) {
		try {
			return getLoginTableHome().findDefaultLoginForUser(userId);
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static LoginInfo getLoginInfo(LoginTable login) {
		LoginInfo li = null;
		try {
			li = getLoginInfoHome().findByPrimaryKey(login.getPrimaryKey());
		}
		catch (FinderException ex) {
			try {
				li = getLoginInfoHome().create();
				li.setLoginTable(login);
				li.store();
			}
			catch (CreateException e) {
				e.printStackTrace();
			}
		}
		return li;
	}

	public static void deleteUserLogin(int userId) throws SQLException {
		deleteLogin(findUserLogin(userId));
	}

	public static boolean isLoginInUse(String login) {
		try {
			return getLoginTableHome().getNumberOfLogins(login) > 0;
		}
		catch (IDOException ex) {
			ex.printStackTrace();
			return true;
		}
	}

	public static void deleteLogin(LoginTable login) {
		if (login != null) {
			try {
				int id = ((Integer) login.getPrimaryKey()).intValue();
				LoginRecordHome lr = ((LoginRecordHome) com.idega.data.IDOLookup.getHomeLegacy(LoginRecord.class));
				java.util.Collection recs = lr.findAllLoginRecords(id);
				Iterator iter = recs.iterator();
				while (iter.hasNext()) {
					((LoginRecord) iter.next()).remove();
				}
			}
			// catch (RemoteException ex)
			// {
			// ex.printStackTrace();
			// }
			catch (RemoveException exc) {
				exc.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
				// assume login record does not exist for this login
			}
			try {
				LoginInfoHome liHome = (LoginInfoHome) IDOLookup.getHome(LoginInfo.class);
				LoginInfo li = liHome.findByPrimaryKey(login.getPrimaryKey());
				li.remove();
			}
			// catch (RemoteException e)
			// {
			// e.printStackTrace();
			// }
			catch (IDOLookupException lookup) {
				lookup.printStackTrace();
			}
			catch (FinderException fe) {
				fe.printStackTrace();
			}
			catch (IDORemoveException ex) {
				ex.printStackTrace();
			}
			catch (RemoveException ex) {
				ex.printStackTrace();
			}
			try {
				login.remove();
			}
			// catch (RemoteException e)
			// {
			// e.printStackTrace();
			// }
			catch (RemoveException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Add-On by Aron 18.01.2001 login/logout tracking
	/**
	 * Records a login record, returns true if succeeds
	 */
	public static LoginRecord recordLogin(LoginTable login, String IPAddress) {
		return recordLogin(login, IPAddress, -1);
	}

	/**
	 * Records a login record, returns true if succeeds
	 */
	public static LoginRecord recordLogin(LoginTable login, String IPAddress, int asUser) {
		try {
			LoginRecordHome lHome = (LoginRecordHome) com.idega.data.IDOLookup.getHome(com.idega.core.accesscontrol.data.LoginRecord.class);
			LoginRecord inRec = lHome.create();
			inRec.setIPAdress(IPAddress);
			inRec.setLogin(login);
			inRec.setLogInStamp(IWTimestamp.getTimestampRightNow());
			if (asUser != -1) {
				inRec.setLoginAsUserID(asUser);
			}
			inRec.store();
			return inRec;
		}
		catch (CreateException ce) {
			ce.printStackTrace();
		}
		catch (RemoteException ex) {
		}
		return null;
	}

	public static int getNumberOfSuccessfulLogins(int iLoginID) {
		try {
			LoginRecordHome lHome = (LoginRecordHome) com.idega.data.IDOLookup.getHome(com.idega.core.accesscontrol.data.LoginRecord.class);
			int numberOfLogins = lHome.getNumberOfLoginsByLoginID(iLoginID);
			return numberOfLogins;
		}
		catch (IDOException ie) {
			ie.printStackTrace();
			return (0);
		}
		catch (RemoteException ex) {
			return (0);
		}
	}
	
	public static boolean hasLoggedIn(LoginTable loginTable) {
		return getNumberOfSuccessfulLogins(new Integer(loginTable.getPrimaryKey().toString()).intValue()) > 0;
	}

	/**
	 * Records a logout record, returns true if succeeds
	 */
	public static boolean recordLogout(LoginRecord loginRecord) {
		if (loginRecord != null) {
			loginRecord.setLogOutStamp(IWTimestamp.getTimestampRightNow());
			loginRecord.store();
			return true;
		}

		return false;
	}

	public static boolean verifyPassword(String user, String password) {
		try {
			LoginTable login = getLoginTableHome().findByLogin(user);
			return Encrypter.verifyOneWayEncrypted(login.getUserPassword(), password);
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		}
		return false;
	}

	protected static User getUser(int userID) throws javax.ejb.FinderException, java.rmi.RemoteException {
		com.idega.user.data.UserHome uHome = (com.idega.user.data.UserHome) com.idega.data.IDOLookup.getHome(User.class);
		return uHome.findByPrimaryKey(new Integer(userID));
	}

	/**
	 * Generates a login for a user with a random password and a login derived
	 * from the users name (or random login if all possible logins are taken)
	 * 
	 * @param userId
	 *          the id for the user.
	 * @throws LoginCreateException
	 *           If an error occurs creating login for the user.
	 */
	public static LoginTable generateUserLogin(int userID) throws LoginCreateException, RemoteException {
		User user;
		try {
			user = getUser(userID);
			List possibleLogins = getPossibleGeneratedUserLogins(user);
			String generatedPassword = getGeneratedPasswordForUser(user);
			// or (int i = 0; i < possibleLogins.length; i++)
			// {
			// String login = possibleLogins[i];
			for (Iterator iter = possibleLogins.iterator(); iter.hasNext();) {
				Object oLogin = iter.next();
				String login = (String) oLogin;
				try {
					return createLogin(userID, login, generatedPassword);
				}
				catch (UserHasLoginException e) {
					throw e;
				}
				catch (LoginCreateException e) {
					// e.printStackTrace();
					System.err.println("Error creating login for userID: " + user.getID() + " with login: " + login);
				}
			}
		}
		catch (FinderException e) {
			e.printStackTrace(System.err);
			System.err.println("Error creating login for userID: " + userID + " cannot find user with ID");
		}
		throw new LoginCreateException("Error creating login for userID: " + userID + ". Exhausted possibilities");
	}

	public static String getGeneratedPasswordForUser() {
		return com.idega.util.StringHandler.getRandomStringNonAmbiguous(8);
	}

	public static String getGeneratedPasswordForUser(User user) {
		return com.idega.util.StringHandler.getRandomStringNonAmbiguous(8);
	}

	private static List getPossibleGeneratedUserLogins(User user) {
		String userFirstName = user.getFirstName();
		String userLastName = user.getLastName();
		String firstName = null;
		String lastName = null;
		String middleName = null;
		if (userFirstName != null) {
			firstName = StringHandler.stripNonRomanCharacters(userFirstName).toLowerCase();
		}
		if (userLastName != null) {
			lastName = StringHandler.stripNonRomanCharacters(userLastName).toLowerCase();
		}
		if (user.getMiddleName() != null) {
			if (!user.getMiddleName().equals("")) {
				middleName = StringHandler.stripNonRomanCharacters(user.getMiddleName()).toLowerCase();
			}
		}
		if (firstName == null) {
			firstName = "";
		}
		if (middleName == null) {
			middleName = "";
		}
		if (lastName == null) {
			lastName = "";
		}
		// String finalPossibility = StringHandler.getRandomString(8);
		ArrayList userNameList = new ArrayList(200);
		try {
			String usePidAsGenerated = getPropertyValue(IWMainApplication.getDefaultIWMainApplication().getCoreBundle(), LOGIN_USE_PID_AS_GENERATED, Boolean.toString(false));
			if (new Boolean(usePidAsGenerated).booleanValue()) {
				userNameList.add(user.getPersonalID());
			}
		}
		catch (RuntimeException e1) {
			e1.printStackTrace();
		}
		try {
			userNameList.add(firstName + lastName.substring(0, 1));
		}
		catch (Exception e) {
			// userNameList.add(StringHandler.getRandomString(8));
		}
		try {
			if (!"".equals(middleName)) {
				userNameList.add(firstName.substring(0, 1) + middleName.substring(0, 1) + lastName.substring(0, 1));
			}
			else {
				userNameList.add(firstName + lastName.substring(0, 2));
			}
		}
		catch (Exception e) {
			// userNameList.add(StringHandler.getRandomString(8));
		}
		try {
			userNameList.add(firstName.substring(0, firstName.length() - 1) + lastName.substring(0, 1));
		}
		catch (Exception e) {
			// userNameList.add(StringHandler.getRandomString(8));
		}
		try {
			userNameList.add(firstName.substring(0, firstName.length()) + lastName.substring(0, 3));
		}
		catch (Exception e) {
			// userNameList.add(StringHandler.getRandomString(8));
		}
		userNameList.addAll(generatePossibleUserNames(firstName, middleName, lastName, 8, (Integer) user.getPrimaryKey()));
		// userNameList.add(finalPossibility);
		return userNameList;
	}

	public static List generatePossibleUserNames(String first, String middle, String last, int userNameLength, Integer userid) {
		int namelength = userNameLength;
		char[][] array = new char[196][namelength];
		String alfabet = first + last + middle;
		alfabet = alfabet.toLowerCase();
		boolean rand = false;
		char[] alfa = alfabet.toCharArray();
		char[] digi = new String("012345679").toCharArray();
		int digilength = digi.length;
		int alfalength = alfabet.length();
		// index1 keeps track of first startindex
		// index2 keeps track of second index
		int index1 = 0;
		int index2 = first.length();
		int index3 = last.length() + middle.length() - 3;
		int count1 = first.length();
		int count2 = 0;
		// int laststep = namelength-first.length();
		String startletters = new String("ZWYX");
		int startlettercount = startletters.length();
		int number = 1;
		java.util.ArrayList list = new java.util.ArrayList(196);
		Random random = new Random();
		boolean breakit = false;
		for (int row = 0; row < array.length && !breakit; row++) {
			int col = 0;
			// add first part of name
			for (int j = 0; j < count1 && index1 + j < alfalength && col < namelength; j++) {
				array[row][col++] = alfa[index1 + j];
			}
			// add second part of name
			if (!rand) {
				for (int j = 0; j < count2 && index2 + (j) < alfalength && col < namelength; j++) {
					array[row][col++] = alfa[index2 + (j)];
				}
			}
			else {
				for (int j = 0; j < count2 && index2 + (j) < alfalength && col < namelength; j++) {
					array[row][col++] = digi[random.nextInt(digilength)];
				}
			}
			// if we havent reached the final length
			// we increase the letter count
			if ((count2 + count1) < namelength) {
				// 
				if (count1 > count2 && count1 < first.length()) {
					count1++;
				}
				else {
					count2++;
				}
			}
			// We have reached the final name length
			// count equals final length of user name
			else {
				// last name limits
				if (count1 > 1 && (count2 + 1) <= last.length()) {
					count1--;
					count2++;
				}
				// first name limits
				/*
				 * else if(count1 <first.length()){ count1++; count2--; }
				 */
				else if (index2 == first.length() && middle.length() > 0) {
					index2 = index3;
					count2 = 1;
					count1 = first.length();
					// laststep = namelength-first.length();
				}
				else if (alfa.length >= 6 && number++ < 99) {
					array[row] = (alfabet.substring(0, 6) + number).toCharArray();
				}
				else if (startlettercount > 0) {
					alfa = (startletters.charAt(startletters.length() - startlettercount) + alfabet).toCharArray();
					index2 = first.length() + 1;
					count2 = 1;
					count1 = first.length() + 2;
					if ((first.length() + 1) <= namelength) {
						count1--;
					}
					startlettercount--;
				}/*
					 * else if (!rand) { rand = true; startlettercount =
					 * startletters.length(); count2 = 1; count1 = first.length(); index2 =
					 * first.length();
					 *  }
					 */
				else {// lets break this
					list.add("u" + userid.toString());
					// System.out.println(row);
					breakit = true;
				}
			}
			// System.out.println(array[row]);
			list.add(new String(array[row]));
		}
		list.trimToSize();
		return list;
	}

	public static void main(String[] args) {
		java.util.List list = generatePossibleUserNames("Maria", "", "Ammar", 8, new Integer(9999));
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			System.out.println(element);
		}
	}

	public static void changeNextTime(LoginTable login, boolean changeValue) {
		LoginInfo info = getLoginInfo(login);
		info.setChangeNextTime(changeValue);
		info.store();
	}

	private static String getPropertyValue(IWBundle iwb, String propertyName, String defaultValue) {
		IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		String value = settings.getProperty(propertyName);
		if (value != null) {
			return value;
		}
		value = iwb.getProperty(propertyName);
		settings.setProperty(propertyName, value != null ? value : defaultValue);
		return defaultValue;
	}
}