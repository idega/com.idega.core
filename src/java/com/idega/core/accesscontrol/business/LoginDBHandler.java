/*
 * $Id: LoginDBHandler.java,v 1.52 2004/06/15 20:07:50 aron Exp $
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

import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.LoginInfoHome;
import com.idega.core.accesscontrol.data.LoginRecord;
import com.idega.core.accesscontrol.data.LoginRecordHome;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableBMPBean;
import com.idega.core.user.data.User;
import com.idega.data.EntityFinder;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORemoveException;
import com.idega.idegaweb.IWMainApplication;
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

	/**
	 * @deprecated replaced with createUserLogin()
	 */
	protected static int createLogin(boolean update, int userID, String userLogin, String password) throws LoginCreateException, RemoteException {
		return createUserLogin(update, userID, userLogin, password).getID();
	}

	protected static LoginTable createUserLogin(boolean update, int userID, String userLogin, String password) throws LoginCreateException, RemoteException {
		List noLogin;
		try {
			noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(), userID);
		}
		catch (SQLException e) {
			throw new LoginCreateException(e.getMessage());
		}

		LoginTable loginTable;
		if (update) {
			if (noLogin == null) { throw new LoginCreateException("User_id : " + userID + " , cannot update login : cannot find login"); }
			loginTable = (LoginTable) noLogin.get(0);
			if (loginTable.getUserId() != userID || loginTable == null) { throw new LoginCreateException("Login update failed."); }
		}
		else {
			//			if (noLogin != null)
			//			{
			//				throw new UserHasLoginException("User_id : " + userID + " , cannot
			// create new login : user has one already");
			//throw new Exception("User_id : " + userID + " , cannot create new login
			// : user has one already");
			//			}
			loginTable = ((com.idega.core.accesscontrol.data.LoginTableHome) com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).createLegacy();
		}
		if (userID > 0 && !update) {
			loginTable.setUserId(userID);
		}
		else if (!update) { throw new LoginCreateException("invalid user_id"); }
		String encryptedPassword = null;
		if (password != null && !"".equals(password)) {
			encryptedPassword = Encrypter.encryptOneWay(password);
			loginTable.setUserPassword(encryptedPassword, password);
		}
		else if (!update) { throw new LoginCreateException("Password not valid"); }
		if (update) {
			if (userLogin != null && !"".equals(userLogin)) {
				if (!loginTable.getUserLogin().equals(userLogin)) {
					try {
						noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), userLogin);
					}
					catch (SQLException e) {
						throw new LoginCreateException(e.getMessage());
					}
					if (noLogin != null && (noLogin.size() > 0 && ((LoginTable) noLogin.get(0)).getUserId() != userID)) {
						LoginTable tempLoginTable = (LoginTable) noLogin.get(0);
						if (tempLoginTable.getUserId() != userID) throw new LoginCreateException("login not valid : in use");
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
					noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), userLogin);
				}
				catch (SQLException e) {
					throw new LoginCreateException(e.getMessage());
				}
				if (noLogin != null) { throw new LoginCreateException("login not valid : in use"); }
				loginTable.setUserLogin(userLogin);
			}
			else {
				throw new LoginCreateException("Login not valid: null or emptyString");
			}
		}
		loginTable.setLastChanged(IWTimestamp.getTimestampRightNow());
		try {
			if (update) {
				loginTable.update();
			}
			else {
				loginTable.insert();
			}
		}
		catch (SQLException ex) {
			if (update) {
				throw new LoginCreateException("Login update failed");
			}
			else {
				throw new LoginCreateException("Login creation failed");
			}
		}
		if (loginTable.getID() < 1 && !update) { throw new LoginCreateException("Login creation failed, login_id : " + loginTable.getID()); }
		return loginTable;
	}

	protected static int createLoginInfo(boolean update, int loginTableID, Boolean accountEnabled, IWTimestamp modified, int daysOfVality, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {
		/*
		 * List noLoginInfo = EntityFinder.findAllByColumn(
		 * com.idega.core.accesscontrol.data.LoginInfoBMPBean.getStaticInstance(),
		 * com.idega.core.accesscontrol.data.LoginInfoBMPBean.getLoginTableIdColumnName(),
		 * loginTableID);
		 */
		//LoginInfo logInfo;
		LoginInfoHome linfoHome = (LoginInfoHome) IDOLookup.getHome(LoginInfo.class);
		LoginInfo logInfo = null;

		try {
			logInfo = linfoHome.findByPrimaryKey(new Integer(loginTableID));
		}
		catch (FinderException fe) {
		}

		if (update) {
			if (logInfo == null) { throw new Exception("login_id : " + loginTableID + " , cannot update loginInfo : cannot find loginInfo"); }
			int primaryKey = -1;
			Object pk = logInfo.getPrimaryKey();
			if (pk != null) {
				primaryKey = ((Integer) pk).intValue();
			}
			if (primaryKey != loginTableID || logInfo == null) { throw new Exception("LoginInfo update failed. "); }
		}
		else {
			if (logInfo != null) { throw new Exception("login_id : " + loginTableID + " , cannot create new loginInfo : user has one already"); }
			logInfo = ((com.idega.core.accesscontrol.data.LoginInfoHome) com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).create();
		}
		logInfo.setID(loginTableID);
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
		if (!logInfo.getAllowedToChange() && logInfo.getChangeNextTime()) { throw new Exception("inconsistency: userAllowedToChangePassw = false and changeNextTime = true"); }
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
		int loginTableID = login.getID();
		try {
			createLoginInfo(false, loginTableID, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
		}
		catch (Exception e) {
			if ("LoginInfo creation failed. ".equals(e.getMessage())) {
				try {
					((com.idega.core.accesscontrol.data.LoginTableHome) com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).findByPrimaryKeyLegacy(loginTableID).delete();
					throw new LoginCreateException(e.getMessage() + "LoginTable entry was removed");
				}
				catch (SQLException sql) {
					sql.printStackTrace();
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
		int loginTableID = createLogin(true, userID, userLogin, password);
		createLoginInfo(true, loginTableID, accountEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
	}

	/**
	 * @deprecated
	 */
	public static void updateLoginInfo(int loginTableID, Boolean accoutEnabled, IWTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime) throws Exception {
		updateLoginInfo(loginTableID, accoutEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, null);
	}

	public static void updateLoginInfo(int loginTableID, Boolean accoutEnabled, IWTimestamp modified, int daysOfVality, Boolean passwNeverExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime, String encryptionType) throws Exception {
		createLoginInfo(true, loginTableID, accoutEnabled, modified, daysOfVality, passwNeverExpires, userAllowedToChangePassw, changeNextTime, encryptionType);
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
		LoginTable loginTable;
		List noLogin = EntityFinder.findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(), userID);
		loginTable = (LoginTable) noLogin.get(0);
		changePassword(loginTable, password);
	}

	public static void changePassword(LoginTable login, String password) throws Exception {
		if (login != null) {
			login.setUserPassword(Encrypter.encryptOneWay(password));
			login.update();
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
			LoginTable[] login_table = (LoginTable[]) (LoginTableBMPBean.getStaticInstance()).findAllByColumn(LoginTableBMPBean.getUserLoginColumnName(), userName);
			if (login_table != null && login_table.length > 0) return login_table[0];
		}
		catch (SQLException ex) {
		}
		return null;
	}

	public static LoginTable getUserLogin(int userId) {
		LoginTable LT = null;
		try {
			LoginTable l = com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance();
			List list = EntityFinder.findAllByColumn(l, com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(), userId);
			if (list != null) {
				LT = (LoginTable) list.get(0);
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			LT = null;
		}
		return LT;
	}

	public static LoginInfo getLoginInfo(int loginTableId) {
		LoginInfo li = null;
		try {
			LoginInfoHome liHome = (LoginInfoHome) IDOLookup.getHome(LoginInfo.class);
			li = liHome.findByPrimaryKey(new Integer(loginTableId));
		}
		catch (Exception ex) {
			try {
				LoginInfoHome liHome = (LoginInfoHome) IDOLookup.getHome(LoginInfo.class);
				li = liHome.create();
				li.setID(loginTableId);
				li.store();
			}
			catch (Exception e) {
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
			return (0 < com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance().getNumberOfRecords(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), login));
		}
		catch (Exception ex) {
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
			//catch (RemoteException ex)
			//{
			//	ex.printStackTrace();
			//}
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
			//catch (RemoteException e)
			//{
			//	e.printStackTrace();
			//}
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
			//catch (RemoteException e)
			//{
			//	e.printStackTrace();
			//}
			catch (RemoveException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Add-On by Aron 18.01.2001 login/logout tracking
	/**
	 * Records a login record, returns true if succeeds
	 */
	public static int recordLogin(int iLoginId, String IPAddress) {
		return recordLogin(iLoginId, IPAddress, -1);
	}

	/**
	 * Records a login record, returns true if succeeds
	 */
	public static int recordLogin(int iLoginId, String IPAddress, int asUser) {
		try {

			LoginRecordHome lHome = (LoginRecordHome) com.idega.data.IDOLookup.getHome(com.idega.core.accesscontrol.data.LoginRecord.class);

			LoginRecord inRec = lHome.create();
			inRec.setIPAdress(IPAddress);
			inRec.setLoginId(iLoginId);
			inRec.setLogInStamp(IWTimestamp.getTimestampRightNow());
			if (asUser != -1) {
				inRec.setLoginAsUserID(asUser);
			}
			inRec.store();
			Integer id = (Integer) inRec.getPrimaryKey();
			return id.intValue();
		}
		catch (CreateException ce) {
			ce.printStackTrace();
			return (-1);
		}
		catch (RemoteException ex) {
			return (-1);
		}
	}

	/**
	 * Records a logout record, returns true if succeeds
	 */
	public static boolean recordLogout(int iLoginRecordId) {
		try {
			LoginRecord lr = ((LoginRecordHome) com.idega.data.IDOLookup.getHomeLegacy(LoginRecord.class)).findByPrimaryKey(new Integer(iLoginRecordId));
			lr.setLogOutStamp(IWTimestamp.getTimestampRightNow());
			lr.store();
			return true;
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		//catch (RemoteException e)
		//{
		//	e.printStackTrace();
		//}
		return false;
	}

	public static boolean verifyPassword(String user, String password) throws Exception {
		java.util.List c = EntityFinder.getInstance().findAllByColumn(LoginTable.class, com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), user);
		if (c != null && c.size() > 0) {
			LoginTable lt = (LoginTable) c.get(0);
			return Encrypter.verifyOneWayEncrypted(lt.getUserPassword(), password);
		}
		return false;
	}

	protected static User getUser(int userID) throws javax.ejb.FinderException, java.rmi.RemoteException {
		com.idega.core.user.data.UserHome uHome = (com.idega.core.user.data.UserHome) com.idega.data.IDOLookup.getHome(User.class);
		return uHome.findByPrimaryKey(userID);
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
			String generatedPassword = getGeneratedPasswordForUser();

			//or (int i = 0; i < possibleLogins.length; i++)
			//{
			//	String login = possibleLogins[i];
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
					//e.printStackTrace();
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
		if (firstName == null) firstName = "";
		if (middleName == null) middleName = "";
		if (lastName == null) lastName = "";
		String finalPossibility = StringHandler.getRandomString(8);
		ArrayList userNameList = new ArrayList(200);
		try {
			userNameList.add(firstName + lastName.substring(0, 1));
		}
		catch (Exception e) {
			userNameList.add(StringHandler.getRandomString(8));
		}
		try {
			if (middleName != null) {
				userNameList.add(firstName.substring(0, 1) + middleName.substring(0, 1) + lastName.substring(0, 1));
			}
			else {
				userNameList.add(firstName + lastName.substring(0, 2));
			}
		}
		catch (Exception e) {
			userNameList.add(StringHandler.getRandomString(8));
		}
		try {
			userNameList.add(firstName.substring(0, firstName.length() - 1) + lastName.substring(0, 1));
		}
		catch (Exception e) {
			userNameList.add(StringHandler.getRandomString(8));
		}
		try {
			userNameList.add(firstName.substring(0, firstName.length()) + lastName.substring(0, 3));
		}
		catch (Exception e) {
			userNameList.add(StringHandler.getRandomString(8));
		}
		
		try {
			String usePidAsGenerated = IWMainApplication.getDefaultIWMainApplication().getCoreBundle().getProperty(LOGIN_USE_PID_AS_GENERATED,Boolean.toString(false));
			if(Boolean.getBoolean(usePidAsGenerated)){
				userNameList.add(user.getPersonalID());
			}
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		}

		userNameList.addAll(generatePossibleUserNames(firstName, middleName, lastName, 8,(Integer)user.getPrimaryKey()));
		userNameList.add(finalPossibility);
		return userNameList;
	}

	public static List generatePossibleUserNames(String first, String middle, String last, int userNameLength,Integer userid) {
		int namelength = userNameLength;
		char[][] array = new char[196][namelength];
		String alfabet = first + last + middle;
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
		//int laststep = namelength-first.length();
		String startletters = new String("ZWYX");
		int startlettercount = startletters.length();
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

				for (int j = 0; j < count2 && index2 + (j) < alfalength && col < namelength; j++)
					array[row][col++] = digi[random.nextInt(digilength)];
			}

			// if we havent reached the final length
			// we increase the letter count
			if ((count2 + count1) < namelength) {
				// 
				if (count1 > count2 && count1 < first.length())
					count1++;
				else
					count2++;
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
					//laststep = namelength-first.length();
				}

				else if (startlettercount > 0) {

					alfa = (startletters.charAt(startletters.length() - startlettercount) + alfabet).toCharArray();
					index2 = first.length() + 1;
					count2 = 1;
					count1 = first.length() + 2;
					if ((first.length() + 1) <= namelength) count1--;

					startlettercount--;
				}/*
				else if (!rand) {
					rand = true;
					startlettercount = startletters.length();
					count2 = 1;
					count1 = first.length();
					index2 = first.length();

				}*/
				else {// lets break this
					list.add("u"+userid.toString());
					//System.out.println(row);
					breakit = true;

				}
			}
			//System.out.println(array[row]);
			list.add(new String(array[row]));
		}

		list.trimToSize();
		System.out.println(list.size());
		return list;
	}

	public static void main(String[] args) {
		java.util.List list = generatePossibleUserNames("jon", "skafti", "sigurdsson", 8,new Integer(9999));
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			System.out.println(element);
		}

	}

	public static void changeNextTime(LoginTable login, boolean changeValue) {
		LoginInfo info = getLoginInfo(login.getID());
		info.setChangeNextTime(changeValue);
		info.store();
	}
}