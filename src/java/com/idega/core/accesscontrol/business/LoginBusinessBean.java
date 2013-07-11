/*
 * $Id: LoginBusinessBean.java,v 1.72 2009/01/30 10:23:35 laddi Exp $
 *
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.accesscontrol.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.bean.UserHasLoggedInEvent;
import com.idega.core.accesscontrol.dao.UserLoginDAO;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.accesscontrol.data.bean.LoginInfo;
import com.idega.core.accesscontrol.data.bean.LoginRecord;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.bean.ICLanguage;
import com.idega.core.user.business.UserBusiness;
import com.idega.data.IDOLookup;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserProperties;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.user.data.bean.UserGroupRepresentative;
import com.idega.util.CoreConstants;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * This is the default business handler for logging a User into the idegaWeb
 * Authentication system.<br/> This class is used by the IWAuthenticator filter
 * and the default Login module for logging users into the system.<br/>
 * </p>
 *
 * Last modified: $Date: 2009/01/30 10:23:35 $ by $Author: laddi $
 *
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>, <a
 *         href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.72 $
 */
public class LoginBusinessBean implements IWPageEventListener {

	private static final Logger LOGGER = Logger.getLogger(LoginBusinessBean.class.getName());

	public static String LoginStateParameter = "login_state";
	private static final String _APPADDRESS_LOGGED_ON_LIST = "ic_loggedon_list";
	public static final String USER_PROPERTY_PARAMETER = "user_properties";
	public static final String LOGINTYPE_AS_ANOTHER_USER = "as_another_user";

	/**
	 * Value that the LoginStateParameter can have to signal that a login is being
	 * made
	 */
	public static final String LOGIN_EVENT_LOGIN = "login";
	/**
	 * Value that the LoginStateParameter can have to signal that a log-out is
	 * being made
	 */
	public static final String LOGIN_EVENT_LOGOFF = "logoff";
	/**
	 * Value that the LoginStateParameter can have to signal that a login retry is
	 * being made
	 */
	public static final String LOGIN_EVENT_TRYAGAIN = "tryagain";

	public static final String PARAMETER_USERNAME = "login";
	public static final String PARAMETER_PASSWORD = "password";
	public static final String PARAMETER_PASSWORD2 = "password2";
	public static final String SESSION_PRM_LOGINNAME_FOR_INVALID_LOGIN = "loginname_for_invalid_login";
	public static boolean USING_OLD_USER_SYSTEM = false;
	public static final String PARAM_LOGIN_BY_UNIQUE_ID = "l_by_uuid";
	public static final String LOGIN_BY_UUID_AUTHORIZED_HOSTS_LIST = "LOGIN_BY_UUID_AUTHORIZED_HOSTS";
	protected static final String SESSION_KEY_CURRENT_USER = "iw_new_user";
	public static final String BEAN_ID = "LoginBusinessBean";

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private GroupDAO groupDAO;

	@Autowired
	private UserLoginDAO userLoginDAO;

	public LoginBusinessBean() {
	}

	/**
	 * <p>
	 * Gets the Application-wide instance of this bean (LoginBusinessBean)
	 * </p>
	 *
	 * @param iwac
	 * @return
	 */
	public static LoginBusinessBean getLoginBusinessBean(IWApplicationContext iwac) {
		LoginBusinessBean instance = (LoginBusinessBean) iwac.getApplicationAttribute(BEAN_ID);
		if (instance == null) {
			instance = new LoginBusinessBean();
			iwac.setApplicationAttribute(BEAN_ID, instance);
		}
		return instance;
	}

	public static LoginBusinessBean getLoginBusinessBean(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return getLoginBusinessBean(session);
	}

	public static LoginBusinessBean getLoginBusinessBean(HttpSession session) {
		IWApplicationContext iwac = getIWApplicationContext(session);
		return getLoginBusinessBean(iwac);
	}

	public static LoginBusinessBean getDefaultLoginBusinessBean() {
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		return getLoginBusinessBean(iwac);
	}

	/**
	 * <p>
	 * Checks and return if a user is logged on into the idegaWeb User System.<br/>
	 * This in turn checks if a certain session variable is set.
	 * </p>
	 *
	 * @param iwc
	 * @return
	 */
	public static boolean isLoggedOn(IWUserContext iwc) {
		try {
			return getLoginSessionBean().getUser() != null;
		}
		catch (BeanCreationException bce) {
			return false;
		}
	}

	/**
	 * <p>
	 * Checks and return if a user is logged on into the idegaWeb User System.<br/>
	 * This in turn checks if a certain session variable is set on the session of
	 * the current request.
	 * </p>
	 *
	 * @param iwc
	 * @return
	 */
	public boolean isLoggedOn(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return isLoggedOn(session);
	}

	/**
	 * <p>
	 * Checks and return if a user is logged on into the idegaWeb User System.<br/>
	 * This in turn checks if a certain session variable is set on the session of
	 * the current request.
	 * </p>
	 *
	 * @param iwc
	 * @return
	 */
	public boolean isLoggedOn(HttpSession session) {
		return getLoginSessionBean().getUser() != null;
	}

	// public static void internalSetState(IWContext iwc, int state) {
	public static void internalSetState(IWContext iwc, LoginState state) throws RemoteException {
		LoginBusinessBean.getLoginSessionBean().setLoginState(state);
	}

	public void internalSetState(HttpServletRequest request, LoginState state) {
		LoginBusinessBean.getLoginSessionBean().setLoginState(state);
	}

	public static LoginState internalGetState(IWContext iwc) {
		return LoginBusinessBean.getLoginSessionBean().getLoginState();
	}

	/**
	 * To get the user name of the current log-in attempt
	 *
	 * @return The user name the current user is trying to log in with. Returns
	 *         null if no log-in attempt is going on.
	 */
	protected String getLoginUserName(HttpServletRequest request) {
		String username = request.getParameter(PARAMETER_USERNAME);
		if (username == null) {
			username = (String) request.getSession().getAttribute(PARAMETER_USERNAME);
			if (username != null) {
				request.getSession().removeAttribute(PARAMETER_USERNAME);
			}
		}

		return username;
	}

	/**
	 * To get the password of the current log-in attempt
	 *
	 * @return The password the current user is trying to log in with. Returns
	 *         null if no log-in attempt is going on.
	 */
	protected String getLoginPassword(HttpServletRequest request) {
		return request.getParameter(PARAMETER_PASSWORD);
	}

	/**
	 * @return True if logIn was succesful, false if it failed
	 */
	public boolean logInUser(HttpServletRequest request, String username, String password) {
		try {
			/*
			 * int didLogin = verifyPasswordAndLogin(iwc, username, password); if
			 * (didLogin == STATE_LOGGED_ON) { onLoginSuccessful(iwc); return true; }
			 */
			LoginState didLogin = verifyPasswordAndLogin(request, username, password);
			if (didLogin.equals(LoginState.LoggedOn)) {
				onLoginSuccessful(request);
				return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return True if logOut was succesful, false if it failed
	 */
	protected boolean logOutUser(HttpServletRequest request) {
		try {
			logOut(request);
			// internalSetState(iwc, "loggedoff");
			// internalSetState(iwc, STATE_LOGGED_OUT);
			internalSetState(request, LoginState.LoggedOut);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public boolean logOutUser(IWContext iwc) {
		return logOutUser(iwc.getRequest());
	}


	/**
	 * Used for the LoggedOnInfo object to be able to log off users when their
	 * session expires.
	 *
	 * @return True if logOut was successful, false if it failed
	 */
	public boolean logOutUserOnSessionTimeout(HttpSession session, LoggedOnInfo logOnInfo) {
		try {
			Map<?, ?> m = getLoggedOnInfoMap(session);
			LoggedOnInfo _logOnInfo = (LoggedOnInfo) m.remove(logOnInfo.getLogin());
			if (_logOnInfo != null) {
				getUserLoginDAO().createLogoutRecord(_logOnInfo.getLoginRecord());
			}
			else {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Invoked when the login failed Can be overrided in subclasses to alter
	 * behaviour By default this sets the state to "login failed" and does not log
	 * in a user
	 */
	// protected void onLoginFailed(IWContext iwc, int loginState, String
	// username) {
	protected void onLoginFailed(HttpServletRequest request, LoginState loginState, String username) {
		logOutUser(request);
		internalSetState(request, loginState);
		try {
			LoginBusinessBean.getLoginSessionBean().setUserLoginName(username);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @deprecated replaced with onLoginFailed(HttpServletRequest...)
	 */
	// protected void onLoginFailed(IWContext iwc, int loginState, String
	// username) {
	@Deprecated
	protected void onLoginFailed(IWContext iwc, LoginState loginState, String username) throws RemoteException {
		onLoginFailed(iwc.getRequest(), loginState, username);
		// internalSetState(iwc, loginState);
		// iwc.setSessionAttribute(UserAttributeParameter, username);
		// internalSetState(iwc,loginState);
		// getLoginSession(iwc).setUserLoginName(username);
	}

	/**
	 * Invoked when the login was succesful Can be overrided in subclasses to
	 * alter behaviour By default this sets the state to "logged on"
	 */
	protected void onLoginSuccessful(HttpServletRequest request) {
		internalSetState(request, LoginState.LoggedOn);
	}

	/**
	 * This method is called to remain backwards compatible, it may be removed in
	 * future versions.
	 *
	 * @deprecated Replaced with onLoginSuccesful(HttpServletRequest);
	 */
	@Deprecated
	protected void onLoginSuccessful(IWContext iwc) throws RemoteException {
		// internalSetState(iwc, "loggedon");
		// internalSetState(iwc, STATE_LOGGED_ON);
		// internalSetState(iwc,LoginState.LoggedOn);
		onLoginSuccessful(iwc.getRequest());
	}

	public static boolean isLogOnAction(IWContext iwc) {
		return LOGIN_EVENT_LOGIN.equals(getControlActionValue(iwc));
	}

	public static boolean isLogOffAction(IWContext iwc) {
		return LOGIN_EVENT_LOGOFF.equals(getControlActionValue(iwc));
	}

	protected static boolean isTryAgainAction(IWContext iwc) {
		return LOGIN_EVENT_TRYAGAIN.equals(getControlActionValue(iwc));
	}

	private static String getControlActionValue(IWContext iwc) {
		return iwc.getParameter(LoginBusinessBean.LoginStateParameter);
	}

	public boolean isLogOnAction(HttpServletRequest request) {
		String controlAction = getControlActionValue(request);
		return LOGIN_EVENT_LOGIN.equals(controlAction);
	}

	public boolean isLogOffAction(HttpServletRequest request) {
		String controlAction = getControlActionValue(request);
		return LOGIN_EVENT_LOGOFF.equals(controlAction);
	}

	public boolean isTryAgainAction(HttpServletRequest request) {
		String controlAction = getControlActionValue(request);
		return LOGIN_EVENT_TRYAGAIN.equals(controlAction);
	}

	private String getControlActionValue(HttpServletRequest request) {
		return request.getParameter(LoginBusinessBean.LoginStateParameter);
	}

	/**
	 * The method invoked when the login presentation module sends a login to this
	 * class
	 */
	@Override
	public boolean actionPerformed(IWContext iwc) throws IWException {
		HttpServletRequest request = iwc.getRequest();
		return processRequest(request);
	}

	/**
	 * This method is invoked by the IWAuthenticator and tries to log in or log
	 * out the user depending on the request parameters.
	 */
	public boolean processRequest(HttpServletRequest request) throws IWException {
		try {
			if (isLoggedOn(request)) {
				if (isLogOffAction(request)) {
					// logOut(iwc);
					// internalSetState(iwc,"loggedoff");
					HttpSession session = request.getSession();
					LoggedOnInfo info = getLoggedOnInfo(session);
					if (LOGINTYPE_AS_ANOTHER_USER.equals(info.getLoginType())) {
						this.logOutAsAnotherUser(request);
						onLoginSuccessful(request);
					}
					else {
						logOutUser(request);
					}
				}
			}
			else {
				if (isLogOnAction(request)) {
					// int canLogin = STATE_LOGGED_OUT;
					LoginState canLogin = LoginState.LoggedOut;
					String username = getLoginUserName(request);
					String password = getLoginPassword(request);
					if ((username != null) && (password != null)) {
						canLogin = verifyPasswordAndLogin(request, username, password);
						// if (canLogin == STATE_LOGGED_ON) {
						if (canLogin.equals(LoginState.LoggedOn)) {
							// isLoggedOn(iwc);
							// internalSetState(iwc,"loggedon");
							// addon
							/*
							 * if (iwc.isParameterSet(LoginRedirectPageParameter)) {
							 * //System.err.println("redirect parameter is set");
							 * BuilderLogic.getInstance().setCurrentPriorityPageID(iwc,
							 * iwc.getParameter(LoginRedirectPageParameter)); }
							 */
							onLoginSuccessful(request);
						}
						else {
							// logOut(iwc);
							// internalSetState(iwc,"loginfailed");
							/*
							 * if(iwc.isParameterSet(LoginFailedRedirectPageParameter)){
							 * BuilderLogic.getInstance().setCurrentPriorityPageID(iwc,
							 * iwc.getParameter(LoginFailedRedirectPageParameter));
							 * iwc.setSessionAttribute(SESSION_PRM_LOGINNAME_FOR_INVALID_LOGIN,username); }
							 */
							onLoginFailed(request, canLogin, username);
						}
					}
					else if (isLoginByUUID(request)) {
						String uuid = request.getParameter(PARAM_LOGIN_BY_UNIQUE_ID);
						boolean success = logInByUUID(request, uuid);
						if (!success) {
							String referer = RequestUtil.getReferer(request);
							LOGGER.warning("Attempt to login with UUID: " + uuid + " failed from referer: " + referer + " , might be an attack");
						}
					}
				}
				else if (isTryAgainAction(request)) {
					internalSetState(request, LoginState.LoggedOut);
				}
			}
		} catch (Exception ex) {
			try {
				logOut(request);
			} catch (Exception e) {
				e.printStackTrace();
			}

			LOGGER.log(Level.WARNING, "Error processing request", ex);
		}
		return true;
	}

	/**
	 * If you want to allow all referers to login via uuid do not set the
	 * LOGIN_BY_UUID_AUTHORIZED_HOSTS application property. The
	 * LOGIN_BY_UUID_AUTHORIZED_HOSTS property is a commaseparated list of host
	 * names and ip numbers that can login via uuid.
	 *
	 * @param iwc
	 * @return true if the parameter PARAM_LOGIN_BY_UNIQUE_ID is set and the
	 *         referer is allowed to login by uuid.
	 */
	protected boolean isLoginByUUID(HttpServletRequest request) {
		if (RequestUtil.isParameterSet(request, PARAM_LOGIN_BY_UNIQUE_ID)) {
			String referer = RequestUtil.getReferer(request);
			IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
			String allowedReferers = iwma.getSettings().getProperty(LOGIN_BY_UUID_AUTHORIZED_HOSTS_LIST);
			if (allowedReferers == null || "".equals(allowedReferers)) {
				return true;
			}
			if (referer != null && allowedReferers.indexOf(referer) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param request
	 * @return Returns null if no basic authentication request was maid. Login has
	 *         index = 0 and password = 1.
	 */
	public String[] getLoginNameAndPasswordFromBasicAuthenticationRequest(HttpServletRequest request) {
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
		if (sAuthorizationHeader != null) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				String unencodedNamePassword = new String(Base64.decodeBase64(encodedNamePassword.getBytes()));
				int seperator = unencodedNamePassword.indexOf(CoreConstants.COLON);
				if (seperator != -1) {
					String[] toReturn = new String[2];
					toReturn[0] = unencodedNamePassword.substring(0, seperator);
					toReturn[1] = unencodedNamePassword.substring(seperator + 1);
					return toReturn;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getLoginNameFromBasicAuthenticationRequest(HttpServletRequest request) {
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
		if (sAuthorizationHeader != null) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
//				sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
				String unencodedNamePassword = new String(Base64.decodeBase64(encodedNamePassword.getBytes()));
				int seperator = unencodedNamePassword.indexOf(':');
				if (seperator != -1) {
					return unencodedNamePassword.substring(0, seperator);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getPasswordFromBasicAuthenticationRequest(HttpServletRequest request) {
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
		if (sAuthorizationHeader != null) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				String unencodedNamePassword = new String(Base64.decodeBase64(encodedNamePassword.getBytes()));
				int seperator = unencodedNamePassword.indexOf(CoreConstants.COLON);
				if (seperator != -1) {
					return unencodedNamePassword.substring(seperator + 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @return Returns true if authentication is successful or else false
	 */
	public boolean authenticateBasicAuthenticationRequest(HttpServletRequest request) {
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
		try {
			if (sAuthorizationHeader != null) {
				HttpSession session = request.getSession();
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				String unencodedNamePassword = new String(Base64.decodeBase64(encodedNamePassword.getBytes()));

				int seperator = unencodedNamePassword.indexOf(':');
				if (seperator != -1) {
					String username = unencodedNamePassword.substring(0, seperator);
					String password = unencodedNamePassword.substring(seperator + 1);

					LoginState canLogin = LoginState.LoggedOut;
					LoggedOnInfo lInfo = getLoggedOnInfo(session, username);
					if (!isLoggedOn(request) && lInfo != null) {
						// used for re-logging in clients that do not keep cookies/session
						LoginSession lSession = LoginBusinessBean.getLoginSessionBean();
						lSession.setLoggedOnInfo(lInfo);
						lSession.setUser(lInfo.getUser());
						// TODO: some more variables need to be set in LoginSession if this
						// is supposed to work for clients with more capability than just
						// webdav-ing. Needs more refactoring than I have time for now.
						onLoginSuccessful(request);
						return true;
					}
					else {
						canLogin = verifyPasswordAndLogin(request, username, password);
						if (canLogin.equals(LoginState.LoggedOn)) {
							onLoginSuccessful(request);
							return true;
						}
						else {
							onLoginFailed(request, canLogin, username);
							return false;
						}
					}
				}
			}
		} catch (Exception ex) {
			try {
				logOut(request);
			} catch (Exception e) {
				e.printStackTrace();
			}

			LOGGER.log(Level.WARNING, "Error authenticating", ex);
		}
		return false;
	}

	public void callForBasicAuthentication(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + "iw_login" + "\"");
		if (message != null) {
			response.sendError(401, message);
		}
		else {
			response.sendError(401);
		}
	}
	public static void setLoginAttribute(String key, Object value, IWUserContext iwc) throws NotLoggedOnException {
		if (isLoggedOn(iwc))
			LoginBusinessBean.getLoginSessionBean().setLoginAttribute(key, value);
		else
			throw new NotLoggedOnException();
	}

	public static Object getLoginAttribute(String key, IWUserContext iwc) throws NotLoggedOnException {
		if (isLoggedOn(iwc))
			return LoginBusinessBean.getLoginSessionBean().getLoginAttribute(key);
		else
			throw new NotLoggedOnException();
	}

	public static void removeLoginAttribute(String key, IWUserContext iwc) throws RemoteException, RemoveException {
		if (isLoggedOn(iwc)) {
			LoginBusinessBean.getLoginSessionBean().removeLoginAttribute(key);
		}
		else if (LoginBusinessBean.getLoginSessionBean() != null) {
			removeLoginSession(iwc);
		}
	}

	public static User getUser(IWUserContext iwc) {
		try {
			return LoginBusinessBean.getLoginSessionBean().getUserEntity();
		}
		catch (NotLoggedOnException ex) {
			return null;
		}
	}

	/**
	 * <p>
	 * Get the user that is currently logged into the system if any.<br/> Returns
	 * null if no user is logged on.<br/>
	 * </p>
	 *
	 * @param request
	 * @return
	 */
	public static User getUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return getDefaultLoginBusinessBean().getUser(session);
	}

	/**
	 * <p>
	 * Get the user that is currently logged into the system if any.<br/> Returns
	 * null if no user is logged on.<br/>
	 * </p>
	 *
	 * @param request
	 * @return
	 */
	public User getUser(HttpSession session) {
		try {
			return LoginBusinessBean.getLoginSessionBean().getUserEntity();
		} catch (NotLoggedOnException ex) {
			return null;
		}
	}

	public static List<Group> getPermissionGroups(IWUserContext iwc) {
		return LoginBusinessBean.getLoginSessionBean().getPermissionGroups();
	}

	public static UserGroupRepresentative getUserRepresentativeGroup(IWUserContext iwc) {
		return LoginBusinessBean.getLoginSessionBean().getRepresentativeGroup();
	}

	public static Group getPrimaryGroup(IWUserContext iwc) {
		return LoginBusinessBean.getLoginSessionBean().getPrimaryGroup();
	}

	protected static void setUser(IWUserContext iwc, User user) throws RemoteException {
		LoginBusinessBean.getLoginSessionBean().setUser(user);
	}

	protected static void setPermissionGroups(IWUserContext iwc, List<Group> value) throws RemoteException {
		LoginBusinessBean.getLoginSessionBean().setPermissionGroups(value);
	}

	protected static void setUserRepresentativeGroup(IWUserContext iwc, UserGroupRepresentative value) throws RemoteException {
		LoginBusinessBean.getLoginSessionBean().setRepresentativeGroup(value);
	}

	protected static void setPrimaryGroup(IWUserContext iwc, Group value) throws RemoteException {
		LoginBusinessBean.getLoginSessionBean().setPrimaryGroup(value);
	}

	/**
	 * Use this method if the one calling this method is not logged in, else use
	 * #logInAsAnotherUser(IWContext,User)
	 *
	 * @param iwc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	protected boolean logIn(IWContext iwc, User user) throws Exception {
		return logIn(iwc.getRequest(), iwc.getResponse(), user);
	}

	/**
	 * Use this method if the one calling this method is not logged in, else use
	 * #logInAsAnotherUser(IWContext,User)
	 *
	 * @param iwc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	protected boolean logIn(HttpServletRequest request, HttpServletResponse response, User user) throws Exception {
		UserLogin userLogin = getUserLoginDAO().findLoginForUser(user);
		storeUserAndGroupInformationInSession(request.getSession(), user);
		LoginRecord loginRecord = getUserLoginDAO().createLoginRecord(userLogin, request.getRemoteAddr(), user);
		storeLoggedOnInfoInSession(request, response, request.getSession(), userLogin, userLogin.getUserLogin(), user, loginRecord,
				userLogin.getLoginType());
		if (user != null)
			ELUtil.getInstance().publishEvent(new UserHasLoggedInEvent(user.getId()));
		return true;
	}

	protected boolean logIn(IWContext iwc, UserLogin userLogin) throws Exception {
		return logIn(iwc.getRequest(), iwc.getResponse(), userLogin);
	}

	protected boolean logIn(HttpServletRequest request, UserLogin userLogin) throws Exception {
		return logIn(request, null, userLogin);
	}

	protected boolean logIn(HttpServletRequest request, HttpServletResponse response, UserLogin userLogin) throws Exception {
		User user = userLogin.getUser();

		storeUserAndGroupInformationInSession(request.getSession(), user);
		LoginRecord loginRecord = getUserLoginDAO().createLoginRecord(userLogin, request.getRemoteAddr(), user);
		storeLoggedOnInfoInSession(request, response, request.getSession(), userLogin, userLogin.getUserLogin(), user, loginRecord, userLogin.getLoginType());
		if (user != null)
			ELUtil.getInstance().publishEvent(new UserHasLoggedInEvent(user.getId()));
		return true;
	}

	protected void storeUserAndGroupInformationInSession(HttpSession session, User user) throws Exception {
		List<Group> groups = null;
		LoginSession lSession = LoginBusinessBean.getLoginSessionBean();
		if (isUsingOldUserSystem()) {
			// Old user system
			// iwc.setSessionAttribute(LoginAttributeParameter, new Hashtable());
			// LoginBusinessBean.setUser(iwc, user);
			lSession.setUser(user);
			groups = UserBusiness.getUserGroups(user.getId().intValue());
			// Old user system end
		} else {
			// New user system
			// iwc.setSessionAttribute(LoginAttributeParameter, new Hashtable());
			// LoginBusinessBean.setUser(iwc, user);
			lSession.setUser(user);
			IWApplicationContext iwac = getIWApplicationContext(session);
			com.idega.user.business.UserBusiness userbusiness = getUserBusiness(iwac);
			com.idega.user.data.User newUser = userbusiness.getUser(user.getId());
			Collection<com.idega.user.data.Group> userGroups = userbusiness.getUserGroups(newUser);
			if (userGroups != null) {
				groups = new ArrayList<Group>();
				for (com.idega.user.data.Group group : userGroups) {
					groups.add(getGroupDAO().findGroup(new Integer(group.getPrimaryKey().toString())));
				}

			// New user system end
			}
		}
		if (groups != null) {
			lSession.setPermissionGroups(groups);
		}
		lSession.setRepresentativeGroup(user.getGroup());

		Group primaryGroup = user.getPrimaryGroup();
		if (primaryGroup != null) {
			lSession.setPrimaryGroup(primaryGroup);
		}

		IWMainApplication iwma = IWMainApplication.getIWMainApplication(session.getServletContext());
		UserProperties properties = new UserProperties(iwma, user.getId());
		lSession.setUserProperties(properties);
	}

	/**
	 *
	 * @param session
	 * @return
	 */
	private static IWMainApplication getIWMainApplication(HttpSession session) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(session.getServletContext());
		return iwma;
	}

	/**
	 *
	 * @param session
	 * @return
	 */
	private static IWApplicationContext getIWApplicationContext(HttpSession session) {
		IWMainApplication iwma = getIWMainApplication(session);
		return iwma.getIWApplicationContext();
	}

	/**
	 * @return
	 */
	private boolean isUsingOldUserSystem() {
		return LoginBusinessBean.USING_OLD_USER_SYSTEM;
	}

	protected void storeLoggedOnInfoInSession(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session,
			UserLogin userLogin,
			String login,
			User user,
			LoginRecord loginRecord,
			String loginType
	) throws NotLoggedOnException, RemoteException {

		LoggedOnInfo lInfo = createLoggedOnInfo();
		lInfo.setUserLogin(userLogin);
		lInfo.setLogin(login);
		lInfo.setTimeOfLogon(IWTimestamp.RightNow());
		lInfo.setUser(user);
		lInfo.setLoginRecord(loginRecord);
		if (!StringUtil.isEmpty(loginType)) {
			lInfo.setLoginType(loginType);
		}

		IWMainApplication iwma = getIWMainApplication(session);
		AccessController aController = iwma.getAccessController();
		IWUserContext iwuc = new IWUserContextImpl(session, session.getServletContext());
		lInfo.setUserRoles(aController.getAllRolesForCurrentUser(iwuc));
		Map<String, Object> m = getLoggedOnInfoMap(session);
		m.put(lInfo.getLogin(), lInfo);
		setLoggedOnInfo(lInfo, session);

		//	Setting current locale for user that just logged in
		String preferredLocale = user.getPreferredLocale();
		Locale locale = null;
		if (StringUtil.isEmpty(preferredLocale)) {
			ICLanguage language = user.getNativeLanguage();
			if (language != null)
				locale = ICLocaleBusiness.getLocaleFromLocaleString(language.getISOAbbreviation());
		} else
			locale = ICLocaleBusiness.getLocaleFromLocaleString(preferredLocale);
		if (locale != null) {
			LOGGER.info("Found preferred locale " + locale + " from '" + preferredLocale + "' for " + user + ", will set as default");
			request.getSession().setAttribute(IWContext.LOCALE_ATTRIBUTE, locale);
		}
	}

	private LoginState verifyPasswordAndLogin(HttpServletRequest request, String login, String password) throws Exception {
		UserLogin userLogin = getUserLoginDAO().findLoginByUsername(login);
		if (userLogin == null) {
			return LoginState.NoUser;
		}

		User user = userLogin.getUser();
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		boolean isAdmin = user.equals(iwma.getAccessController().getAdministratorUser());
		if (isLoginExpired(userLogin) && !isAdmin) {
			return LoginState.Expired;
		}

		LoginInfo loginInfo = userLogin.getLoginInfo();
		if (verifyPassword(userLogin, password)) {
			if (loginInfo != null && !loginInfo.getAccountEnabled() && !isAdmin) {
				return LoginState.Expired;
			}
			if (logIn(request, userLogin)) {
				loginInfo.setFailedAttemptCount(0);
				getUserLoginDAO().merge(loginInfo);
				return LoginState.LoggedOn;
			}
		} else {
			if (isAdmin) { // admin must get unlimited attempts
				return LoginState.WrongPassword;
			}
			int maxFailedLogginAttempts = 0;
			try {
				String maxStr = iwma.getIWApplicationContext().getApplicationSettings().getProperty("max_failed_login_attempts", "0");
				if(maxStr==null){
					maxStr="100";
				}
				maxFailedLogginAttempts = Integer.parseInt(maxStr);
			} catch (Exception e) {
				// default used, no maximum
			}

			if (maxFailedLogginAttempts != 0) {
				int failedAttempts = loginInfo.getFailedAttemptCount();
				failedAttempts++;
				loginInfo.setFailedAttemptCount(failedAttempts);
				if (failedAttempts == maxFailedLogginAttempts - 1) {
					LOGGER.warning("login failed, disabled next time");
				} else if (failedAttempts >= maxFailedLogginAttempts) {
					LOGGER.warning("Maximum loggin attemps, disabling account " + login);
					loginInfo.setAccountEnabled(false);
					loginInfo.setFailedAttemptCount(0);
				} else {
					LOGGER.warning("Login failed, #" + failedAttempts);
				}

				getUserLoginDAO().merge(loginInfo);
			}
		}

		return LoginState.Failed;
	}

	public void resetPassword(String login, String newPassword, boolean changeNextTime) throws Exception {
		UserLogin userLogin = getUserLoginDAO().findLoginByUsername(login);
		LoginInfo loginInfo = userLogin.getLoginInfo();
		User user = userLogin.getUser();
		changeUserPassword(user, newPassword);
		loginInfo.setFailedAttemptCount(0);
		loginInfo.setAccessClosed(false);
		if (changeNextTime) {
			loginInfo.setChangeNextTime(true);
		}
		getUserLoginDAO().merge(loginInfo);
	}

	public boolean verifyPassword(User user, String login, String password) throws FinderException {
		UserLogin loginTable = getUserLoginDAO().findLoginByUserAndUsername(user, login);
		return verifyPassword(loginTable, password);
	}

	/**
	 * <p>
	 * Returns true if the password matches the encrypted value in loginTable.
	 * </p>
	 * @param userLogin
	 * @param password
	 * @return
	 */
	public boolean verifyPassword(UserLogin userLogin, String password){
		if (Encrypter.verifyOneWayEncrypted(userLogin.getUserPassword(), password)) {
			return true;
		}
		return false;
	}

	protected void logOut(IWContext iwc) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		logOut(request);
	}

	protected void logOut(HttpServletRequest request) throws Exception {
		// if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
		HttpSession session = request.getSession();
		if (LoginBusinessBean.getLoginSessionBean() != null) {
			// this.getLoggedOnInfoList(iwc).remove(this.getLoggedOnInfo(iwc));
			LoggedOnInfo info = getLoggedOnInfo(session);
			if (info != null) {
				Map<String, Object> lm = getLoggedOnInfoMap(session);
				lm.remove(info.getLogin());
			}
			UserProperties properties = getUserProperties(session);
			if (properties != null) {
				properties.store();
			}
			// iwc.removeSessionAttribute(LoginAttributeParameter);
			removeLoginSession(session);
		}
		session.invalidate();
	}

	/**
	 * The key is the login name and the value is
	 * com.idega.core.accesscontrol.business.LoggedOnInfo
	 *
	 * @return Returns empty Map if no one is logged on
	 */
	public static Map<String, Object> getLoggedOnInfoMap(IWContext iwc) {
		return getDefaultLoginBusinessBean().getLoggedOnInfoMap(iwc.getSession());
	}

	/**
	 * The key is the login name and the value is
	 * com.idega.core.accesscontrol.business.LoggedOnInfo
	 *
	 * @return Returns empty Map if no one is logged on
	 */
	public Map<String, Object> getLoggedOnInfoMap(HttpSession session) {
		ServletContext sc = session.getServletContext();
		@SuppressWarnings("unchecked")
		Map<String, Object> loggedOnMap = (Map<String, Object>) sc.getAttribute(_APPADDRESS_LOGGED_ON_LIST);
		if (loggedOnMap == null) {
			loggedOnMap = new TreeMap<String, Object>();
			sc.setAttribute(_APPADDRESS_LOGGED_ON_LIST, loggedOnMap);
		}
		return loggedOnMap;
	}

	/**
	 * @return returns empty Collection if no one is logged on
	 */
	public static Collection<Object> getLoggedOnInfoCollection(IWContext iwc) {
		return getLoggedOnInfoMap(iwc).values();
	}

	/**
	 * returns null if user is not logged on
	 */
	public static LoggedOnInfo getLoggedOnInfo(IWContext iwc, String loginName) {
		return (LoggedOnInfo) getLoggedOnInfoMap(iwc).get(loginName);
	}

	/**
	 * returns null if user is not logged on
	 */
	public LoggedOnInfo getLoggedOnInfo(HttpSession session, String loginName) {
		return (LoggedOnInfo) getLoggedOnInfoMap(session).get(loginName);
	}

	/**
	 * The key is the login name and the value is
	 * com.idega.core.accesscontrol.business.LoggedOnInfo
	 *
	 * @param session
	 * @return
	 */
	public static LoggedOnInfo getLoggedOnInfo(IWUserContext iwc) {
		return LoginBusinessBean.getLoginSessionBean().getLoggedOnInfo();
	}

	public LoggedOnInfo getLoggedOnInfo(HttpSession session) {
		return LoginBusinessBean.getLoginSessionBean().getLoggedOnInfo();
	}

	public static void setLoggedOnInfo(LoggedOnInfo lInfo, IWContext iwc) throws NotLoggedOnException, RemoteException {
		HttpSession session = iwc.getSession();
		getDefaultLoginBusinessBean().setLoggedOnInfo(lInfo, session);
	}

	public void setLoggedOnInfo(LoggedOnInfo lInfo, HttpSession session) throws NotLoggedOnException, RemoteException {
		// Not stored as LoginAttribute because it is HttpSessionBindingListener
		// setLoginAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, lInfo, iwc);
		if (isLoggedOn(session)) {
			// iwc.setSessionAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, lInfo);
			LoginBusinessBean.getLoginSessionBean().setLoggedOnInfo(lInfo);
		}
		else {
			throw new NotLoggedOnException();
		}
	}

	@Deprecated
	public LoginContext changeUserPassword(com.idega.user.data.User user, String password) throws Exception {
		return changeUserPassword(getUserDAO().getUser(new Integer(user.getPrimaryKey().toString())), password);
	}

	public LoginContext changeUserPassword(User user, String password) throws Exception {
		UserLogin login = getUserLoginDAO().findLoginForUser(user);
		login.setUserPassword(password);
		getUserLoginDAO().persist(login);

		LoginContext loginContext = new LoginContext(user, login.getUserLogin(), password);
		return loginContext;
	}

	/**
	 * Creates a wrapper object around the users login name and password in clear
	 * text (no decoding)
	 *
	 * @param user
	 * @return
	 */
	public LoginContext getLoginContext(User user) {
		UserLogin login = getUserLoginDAO().findLoginForUser(user);
		if (login != null) {
			LoginContext loginContext = new LoginContext(user, login.getUserLogin(), login.getUserPassword());
			return loginContext;
		}
		else {
			return null;
		}
	}

	public LoginContext createNewUser(IWApplicationContext iwac, String fullName, String email, String preferredUserName, String preferredPassword) {
		StringTokenizer tok = new StringTokenizer(fullName);
		String first = "";
		String middle = "";
		String last = "";
		if (tok.hasMoreTokens()) {
			first = tok.nextToken();
		}
		if (tok.hasMoreTokens()) {
			middle = tok.nextToken();
		}
		if (tok.hasMoreTokens()) {
			last = tok.nextToken();
		}
		else {
			last = middle;
			middle = "";
		}
		LoginContext loginContext = null;
		try {
			User user = getUserDAO().createUser(first, middle, last, fullName, null, null, null, null, null);
			String login = preferredUserName;
			String pass = preferredPassword;
			if (user != null) {
				if (email != null && email.length() > 0) {
					getUserDAO().updateUserMainEmail(user, email);
				}
				if (login == null) {
					login = LoginCreator.createLogin(user.getDisplayName());
				}
				if (pass == null) {
					pass = LoginCreator.createPasswd(8);
				}
				getUserLoginDAO().createLogin(user, login, pass, true, true, false, -1, false);
				loginContext = new LoginContext(user, login, pass);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return loginContext;
	}

	/**
	 * added for cookie login - calling this may be unsafe ( Aron )
<<<<<<< HEAD
=======
	 * </p>
>>>>>>> caedf7bba3f629e1f49cde7e8a02f7404adcf01e
	 *
	 * @param request
	 * @param login
	 * @return
	 * @throws Exception
	 */
	public boolean logInUnVerified(HttpServletRequest request, String login) throws Exception {
		boolean returner = false;
		UserLogin userLogin = getUserLoginDAO().findLoginByUsername(login);

		if (userLogin != null) {
			returner = logIn(request, userLogin);
			if (returner) {
				onLoginSuccessful(request);
			}
		}

		return returner;
	}

	public boolean logInAsAnotherUser(IWContext iwc, String personalID) throws Exception {
		boolean returner = false;
		try {
			User user = getUserDAO().getUser(personalID);
			returner = logInAsAnotherUser(iwc, user);
		}
		catch (FinderException e) {
			returner = false;
		}
		catch (RemoteException e) {
			returner = false;
		}
		return returner;
	}

	public boolean retrieveLoginInformation(HttpServletRequest request) throws NotLoggedOnException, RemoteException {
		HttpSession session = request.getSession();
		if (LoginBusinessBean.getLoginSessionBean() != null) {
			Map<String, Object> m = getLoggedOnInfoMap(session);
			LoggedOnInfo _logOnInfo = (LoggedOnInfo) m.remove(getLoggedOnInfo(session).getLogin());
			if (_logOnInfo != null) {
				getUserLoginDAO().createLogoutRecord(_logOnInfo.getLoginRecord());
			}
		}

		LoginBusinessBean.getLoginSessionBean().retrieve();
		if (LoginBusinessBean.getLoginSessionBean().getUser() != null) {
			return true;
		}
		else {
			return false;
		}
	}

	public void reserveLoginInformation(IWContext iwc) throws RemoteException {
		HttpServletRequest request = iwc.getRequest();
		reserveLoginInformation(request);
	}

	public void reserveLoginInformation(HttpServletRequest request) throws RemoteException {
		if (LoginBusinessBean.getLoginSessionBean() != null) {
			UserProperties properties = LoginBusinessBean.getLoginSessionBean().getUserProperties();
			if (properties != null) {
				properties.store();
			}
			LoginBusinessBean.getLoginSessionBean().reserve();
		}
	}

	public void logOutAsAnotherUser(HttpServletRequest request) throws NotLoggedOnException, RemoteException {
		HttpSession session = request.getSession();
		LoggedOnInfo info = this.getLoggedOnInfo(session);
		LoginRecord rec = info.getLoginRecord();
		retrieveLoginInformation(request);
		info.setLoginType("");
		getUserLoginDAO().createLogoutRecord(rec);
	}

	/**
	 * Use this method if the one calling this method is logged in, else use
	 * #logIn(IWContext,User)
	 *
	 * @param iwc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean logInAsAnotherUser(IWContext iwc, User user) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		return logInAsAnotherUser(request, user);
	}

	/**
	 * Use this method if the one calling this method is logged in, else use
	 * #logIn(HttpServletRequest,User)
	 *
	 * @param request
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean logInAsAnotherUser(HttpServletRequest request, User user) throws Exception {
		return logInAsAnotherUser(request, user, true);
	}

	/**
	 * Use this method if the one calling this method is logged in, else use
	 * #logIn(IWContext,User)
	 *
	 * @param request
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private boolean logInAsAnotherUser(HttpServletRequest request, User user, boolean reserveCurrentUser) throws Exception {
		if (isLoggedOn(request)) {
			HttpSession session = request.getSession();
			UserLogin userLogin = getUserLoginDAO().findLoginForUser(user);
			String login = null;
			if (userLogin != null) {
				login = userLogin.getUserLogin();
			}

			User oldUser = getUser(request);
			if (oldUser.equals(user)) {
				return true;
			}
			if (reserveCurrentUser) {
				reserveLoginInformation(request);
			}
			storeUserAndGroupInformationInSession(session, user);
			LoginRecord loginRecord = getUserLoginDAO().createLoginRecord(userLogin, request.getRemoteAddr(), user);
			storeLoggedOnInfoInSession(request, null, session, userLogin, login, user, loginRecord, LOGINTYPE_AS_ANOTHER_USER);
			onLoginSuccessful(request);
			return true;
		}
		return false;
	}

	public boolean logInByPersonalID(IWContext iwc, String personalId) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		return logInByPersonalID(request, personalId);
	}

	/**
	 * <p>
	 * Log in the user with given personalId if the user exists in the IC_USER table<br/>
	 * This method doesn't take in a loginType which means that the IC_LOGIN record chosen
	 * to log into will not have a loginType set.
	 * </p>
	 * @param request
	 * @param personalId
	 * @return
	 * @throws Exception
	 */
	public boolean logInByPersonalID(HttpServletRequest request, String personalId) throws Exception {
		return logInByPersonalID(request,personalId,null,null,null);
	}

	public boolean hasUserLogin(HttpServletRequest request, String personalId) throws Exception {
		try {
			IWApplicationContext iwac = getIWApplicationContext(request.getSession());
			com.idega.user.data.User user = getUserBusiness(iwac).getUser(personalId);
			LoginTableHome loginTableHome = (LoginTableHome) IDOLookup.getHome(LoginTable.class);
			Collection<LoginTable> logins = loginTableHome.findLoginsForUser(user);

			if (logins == null || logins.isEmpty()) {
				return false;
			}

			return true;
		} catch (EJBException e) {
		}

		return false;
	}

	/**
	 *
	 * <p>Method created for logging in users, created from external services,
	 * which does not have personal id, username or password.</p>
	 * @param iwc current application context, not <code>null</code>;
	 * @param user to login, not <code>null</code>;
	 * @return <code>true</code> when logged in, <code>false</code>
	 * otherwise.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 * @deprecated method is hack and totally unsafe.
	 */
	@Deprecated
	public boolean logInUser(IWContext iwc, User user) {
		List<UserLogin> logins = null;
		try {
			logins = getUserLoginDAO().findAllLoginsForUser(user);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to find logins for user " + user, e);
		}

		UserLogin login = null;
		try {
			login = chooseLoginRecord(iwc.getRequest(), logins, user);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to find " + LoginTable.class +  " for user " + user, e);
		}

		try {
			storeUserAndGroupInformationInSession(iwc.getRequest().getSession(), user);
			LoginRecord loginRecord = LoginDBHandler.recordLogin(login, iwc.getRequest().getRemoteAddr());
			storeLoggedOnInfoInSession(
					iwc.getRequest(),
					iwc.getResponse(),
					iwc.getRequest().getSession(),
					login,
					login != null ? login.getUserLogin() : null,
					user,
					loginRecord,
					login != null ? login.getLoginType() : null
			);

			if (logIn(iwc.getRequest(), login)) {
				onLoginSuccessful(iwc.getRequest());
				return Boolean.TRUE;
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to log in user, cause of: ", e);
		}

		return Boolean.FALSE;
	}

	/**
	 * <p>
	 * Logs the user in by given personalId and specified loginType.
	 * </p>
	 * @param request
	 * @param personalId
	 * @param loginType
	 * @return
	 * @throws Exception
	 */
	public boolean logInByPersonalID(
			HttpServletRequest request,
			String personalId,
			String userName,
			String password,
			String loginType
	) throws Exception {
		boolean returner = false;
		try {
			User user = getUserDAO().getUser(personalId);
			List<UserLogin> logins = getUserLoginDAO().findAllLoginsForUser(user);
			UserLogin userLogin;
			if (loginType==null) {
				userLogin = this.chooseLoginRecord(request, logins, user);
			} else {
				userLogin = this.chooseLoginRecord(request, logins, user,loginType);
			}

			if (userLogin != null) {
				if (userName != null) {
					if (!userLogin.getUserLogin().equals(userName)) {
						return false;
					}
				}

				if (password != null) {
					if (!verifyPassword(userLogin, password)) {
						return false;
					}
				}

				returner = logIn(request, userLogin);
				if (returner) {
					onLoginSuccessful(request);
				}
			} else {
				try {
					throw new LoginCreateException("No matching login record found for user");
				} catch (LoginCreateException e1) {
					e1.printStackTrace();
				}
			}
		} catch (EJBException e) {
			returner = false;
		}

		return returner;
	}

	/**
	 * Logs you into idegaweb by a universally unique identifier UUID if it finds
	 * a user with that id.
	 *
	 * @param request
	 * @param uuid
	 * @return true if succeeded in login on a user with his UUID
	 * @throws Exception
	 */
	public boolean logInByUUID(HttpServletRequest request, String uuid) throws Exception {
		boolean returner = false;
		try {
			User user = getUserDAO().getUserByUUID(uuid);
			List<UserLogin> logins = getUserLoginDAO().findAllLoginsForUser(user);
			UserLogin userLogin = this.chooseLoginRecord(request, logins, user);
			if (userLogin != null) {
				returner = logIn(request, userLogin);
				if (returner) {
					onLoginSuccessful(request);
				}
			}
			else {
				try {
					throw new LoginCreateException("No record chosen");
				}
				catch (LoginCreateException e1) {
					e1.printStackTrace();
				}
			}
		}
		catch (EJBException e) {
			returner = false;
		}
		return returner;
	}

	/**
	 * @param loginRecords -
	 *          all login records for one user
	 * @return LoginTable record to log on the system
	 */
	public UserLogin chooseLoginRecord(
			HttpServletRequest request,
			Collection<UserLogin> loginRecords,
			User user,
			String loginType
	) throws Exception {
		UserLogin chosenRecord = null;
		if (loginRecords != null) {
			for (Iterator<UserLogin> iter = loginRecords.iterator(); iter.hasNext();) {
				UserLogin login = iter.next();
				String type = login.getLoginType();
				if (loginType == null) {
					//searching for the default login where type is not set.
					if (StringUtil.isEmpty(type)) {
						chosenRecord = login;
						break;
					}
				} else {
					if (loginType.equals(type)) {
						chosenRecord = login;
						break;
					}
				}
			}
		}
		return chosenRecord;
	}

	/**
	 * <p>
	 * Chooses a login record with loginType=null or loginType=''
	 * for logging a user in.
	 * </p>
	 * @param loginRecords -
	 *          all login records for one user
	 * @return LoginTable record to log on the system
	 */
	public UserLogin chooseLoginRecord(HttpServletRequest request, Collection<UserLogin> loginRecords, User user) throws Exception {
		return chooseLoginRecord(request, loginRecords, user, null);
	}

	public boolean isLoginExpired(UserLogin userLogin) {
		LoginInfo loginInfo = userLogin.getLoginInfo();
		return loginInfo == null || loginInfo.isLoginExpired();
	}

	protected com.idega.user.business.UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException {
		return (com.idega.user.business.UserBusiness) IBOLookup.getServiceInstance(iwac, com.idega.user.business.UserBusiness.class);
	}

	protected LoggedOnInfo createLoggedOnInfo() {
		return new LoggedOnInfo();
	}

	public static UserProperties getUserProperties(IWUserContext iwuc) {
		return LoginBusinessBean.getLoginSessionBean().getUserProperties();
	}

	public UserProperties getUserProperties(HttpSession session) {
		return LoginBusinessBean.getLoginSessionBean().getUserProperties();
	}

	public static LoginSession getLoginSessionBean() {
		return ELUtil.getInstance().getBean(LoginSession.class);
	}

	/**
	 * Resets the LoginSession object
	 *
	 * @param iwc
	 */
	private static void removeLoginSession(IWUserContext iwc) {
		getLoginSessionBean().reset();
	}

	/**
	 * Resets the LoginSession object
	 *
	 * @param iwc
	 */
	private void removeLoginSession(HttpSession session) {
		getLoginSessionBean().reset();
	}

	public User getCurrentUser(HttpSession session) {
		User user = getUser(session);
		if (user != null) {
			return user;
		}
		else {
			throw new NotLoggedOnException();
		}
	}

	public com.idega.user.data.User getCurrentUserLegacy(HttpSession session) {
		User user = getUser(session);
		if (user != null) {
			try {
				return getUserBusiness(getIWApplicationContext(session)).getUser(user.getId());
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}
		}
		else {
			throw new NotLoggedOnException();
		}
	}

	public static java.sql.Date getLastLoginByUser(Integer userId) throws RemoteException {
		UserLoginDAO dao = ELUtil.getInstance().getBean(UserLoginDAO.class);
		UserDAO userDao = ELUtil.getInstance().getBean(UserDAO.class);

		User user = userDao.getUser(userId);
		if (user != null) {
			LoginRecord record = dao.getLastRecordByUser(user);
			if (record != null) {
				return new IWTimestamp(record.getInStamp()).getDate();
			}
		}
		return null;
	}

	/**
	 * Gets the last login record date before current logged record ( second last
	 * entry)
	 *
	 * @param userId
	 * @return
	 */
	public static java.sql.Date getLastLoginByLogin(Integer loginId) throws RemoteException {
		UserLoginDAO dao = ELUtil.getInstance().getBean(UserLoginDAO.class);

		UserLogin login = dao.findLogin(loginId);
		if (login != null) {
			LoginRecord record = dao.getLastRecordByLogin(login);
			if (record != null) {
				return new IWTimestamp(record.getInStamp()).getDate();
			}
		}
		return null;
	}

	private GroupDAO getGroupDAO() {
		if (groupDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return groupDAO;
	}

	private UserDAO getUserDAO() {
		if (userDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return userDAO;
	}

	private UserLoginDAO getUserLoginDAO() {
		if (userLoginDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return userLoginDAO;
	}
}