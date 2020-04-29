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
import java.util.Arrays;
import java.util.Base64;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.bean.UserHasLoggedInEvent;
import com.idega.core.accesscontrol.bean.UserHasLoggedOutEvent;
import com.idega.core.accesscontrol.dao.UserLoginDAO;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.accesscontrol.data.bean.LoginInfo;
import com.idega.core.accesscontrol.data.bean.LoginRecord;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.core.accesscontrol.event.LoggedInUserCredentials;
import com.idega.core.accesscontrol.event.LoggedInUserCredentials.LoginType;
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
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.user.business.UserProperties;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.user.data.bean.UserGroupRepresentative;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.DBUtil;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
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
	/**
	 * Value that the LoginStateParameter can have to signal that a sms login is
	 * being made
	 */
	public static final String LOGIN_EVENT_SMS_LOGIN = "smslogin";
	/**
	 * Value that the LoginStateParameter can have to signal that a full login with sms login is
	 * being made
	 */
	public static final String LOGIN_EVENT_FULL_WITH_SMS_LOGIN = "fullwithsmslogin";

	public static final String PARAMETER_USERNAME = "login";
	public static final String PARAMETER_PASSWORD = "password";
	public static final String PARAMETER_PASSWORD2 = "password2";
	public static final String PARAMETER_SMS_CODE = "smsCode";
	public static final String PARAMETER_IS_CANCEL = "isCancel",
								PARAMETER_SESSION_ID = "session_id";
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
		IWApplicationContext iwac = getIWApplicationContext(null, session);
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
			return getLoginSessionBean().getUserEntity() != null;
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
		return getLoginSessionBean().getUserEntity() != null;
	}

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
		String password = request.getParameter(PARAMETER_PASSWORD);
		if (password == null) {
			password = (String) request.getSession().getAttribute(PARAMETER_PASSWORD);
			if (password != null) {
				request.getSession().removeAttribute(PARAMETER_PASSWORD);
			}
		}

		return password;
	}

	/**
	 * To get the sms code of the current log-in attempt
	 *
	 * @return The sms code the current user is trying to log in with. Returns
	 *         null if no log-in attempt is going on.
	 */
	protected String getSMSCode(HttpServletRequest request) {
		String smsCode = request.getParameter(PARAMETER_SMS_CODE);
		if (smsCode == null) {
			smsCode = (String) request.getSession().getAttribute(PARAMETER_SMS_CODE);
			if (smsCode != null) {
				request.getSession().removeAttribute(PARAMETER_SMS_CODE);
			}
		}

		return smsCode;
	}

	/**
	 * To get the isCancel parameter value of the current log-in attempt
	 *
	 * @return The isCancel parameter value
	 */
	protected String getIsCancel(HttpServletRequest request) {
		String isCancel = request.getParameter(PARAMETER_IS_CANCEL);
		if (isCancel == null) {
			isCancel = (String) request.getSession().getAttribute(PARAMETER_IS_CANCEL);
			if (isCancel != null) {
				request.getSession().removeAttribute(PARAMETER_IS_CANCEL);
			}
		}

		return isCancel;
	}

	/**
	 * Remove attributes from session
	 *
	 */
	protected void removeAttributesFromSession(HttpServletRequest request) {
		request.getSession().removeAttribute(PARAMETER_IS_CANCEL);
		request.getSession().removeAttribute(PARAMETER_SMS_CODE);
		request.getSession().removeAttribute(PARAMETER_PASSWORD);
		request.getSession().removeAttribute(PARAMETER_USERNAME);
	}


	/**
	 * To get the user name of the current log-in attempt
	 *
	 * @return The user name the current user is trying to log in with. Returns
	 *         null if no log-in attempt is going on.
	 */
	protected String getLoginUserNameNoRemoveFromSession(HttpServletRequest request) {
		String username = request.getParameter(PARAMETER_USERNAME);
		if (username == null) {
			username = (String) request.getSession().getAttribute(PARAMETER_USERNAME);
		}

		return username;
	}

	/**
	 * To get the password of the current log-in attempt
	 *
	 * @return The password the current user is trying to log in with. Returns
	 *         null if no log-in attempt is going on.
	 */
	protected String getLoginPasswordNoRemoveFromSession(HttpServletRequest request) {
		String password = request.getParameter(PARAMETER_PASSWORD);
		if (password == null) {
			password = (String) request.getSession().getAttribute(PARAMETER_PASSWORD);
		}

		return password;
	}

	/**
	 * To get the sms code of the current log-in attempt
	 *
	 * @return The sms code the current user is trying to log in with. Returns
	 *         null if no log-in attempt is going on.
	 */
	protected String getSMSCodeNoRemoveFromSession(HttpServletRequest request) {
		String smsCode = request.getParameter(PARAMETER_SMS_CODE);
		if (smsCode == null) {
			smsCode = (String) request.getSession().getAttribute(PARAMETER_SMS_CODE);
		}

		return smsCode;
	}

	/**
	 * To get the isCancel parameter value of the current log-in attempt
	 *
	 * @return The isCancel parameter value
	 */
	protected String getIsCancelNoRemoveFromSession(HttpServletRequest request) {
		String isCancel = request.getParameter(PARAMETER_IS_CANCEL);
		if (isCancel == null) {
			isCancel = (String) request.getSession().getAttribute(PARAMETER_IS_CANCEL);
		}

		return isCancel;
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
			if (didLogin.equals(LoginState.LOGGED_ON)) {
				onLoginSuccessful(request, username, password);
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
	protected boolean logOutUser(HttpServletRequest request, HttpServletResponse response, String userName) {
		try {
			logOut(request, userName);
			internalSetState(request, LoginState.LOGGED_OUT);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			doClearCookies(request, response);
		}
	}

	private void doClearCookies(HttpServletRequest request, HttpServletResponse response) {
		if (request == null) {
			return;
		}

		Cookie[] cookies = null;
		try {
			if (response == null) {
				try {
					RequestResponseProvider rrProvider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
					response = rrProvider == null ? null : rrProvider.getResponse();
				} catch (Exception e) {}
			}

			cookies = request.getCookies();
			if (ArrayUtil.isEmpty(cookies)) {
				return;
			}

			String cookieToSkip = "currentLocale";
			for (Cookie cookie: cookies) {
				String name = cookie.getName();
				if (!StringUtil.isEmpty(name) && !name.equals(cookieToSkip)) {
					cookie.setValue(CoreConstants.EMPTY);
					cookie.setPath(CoreConstants.SLASH);
					cookie.setMaxAge(0);
					if (response != null) {
						response.addCookie(cookie);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error clearing cookies" + (ArrayUtil.isEmpty(cookies) ? CoreConstants.EMPTY : Arrays.asList(cookies)), e);
		}
	}

	public boolean logOutUser(IWContext iwc) {
		HttpSession session = iwc.getSession();
		Object loginType = session == null ? null : session.getAttribute(LoggedInUserCredentials.LOGIN_TYPE);

		LoggedOnInfo loggedOnInfo = getLoggedOnInfo(iwc);
		User user = loggedOnInfo == null ? null : loggedOnInfo.getUser();
		String loginTypeFromInfo = loggedOnInfo == null ? null : loggedOnInfo.getLoginType();
		if (logOutUser(iwc.getRequest(), iwc.getResponse(), loggedOnInfo == null ? CoreConstants.EMPTY : loggedOnInfo.getLogin())) {
			if (user != null) {
				String type = loginType == null ? null : loginType.toString();
				type = StringUtil.isEmpty(type) ? loginTypeFromInfo : type;
				ELUtil.getInstance().publishEvent(new UserHasLoggedOutEvent(user.getId(), type));
			}
			return true;
		}
		return false;
	}

	/**
	 * Used for the LoggedOnInfo object to be able to log off users when their
	 * session expires.
	 *
	 * @return True if logOut was successful, false if it failed
	 */
	public boolean logOutUserOnSessionTimeout(HttpSession session, LoggedOnInfo logOnInfo) {
		try {
			Map<String, Object> m = getLoggedOnInfoMap(session);
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
		IWContext iwc = CoreUtil.getIWContext();
		logOutUser(request, iwc == null ? null : iwc.getResponse(), username);
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
	protected void onLoginSuccessful(HttpServletRequest request, String username, String password) {
		internalSetState(request, LoginState.LOGGED_ON);

		if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password)) {
			ELUtil.getInstance().publishEvent(new LoggedInUserCredentials(
					request,
					RequestUtil.getServerURL(request),
					username,
					password,
					LoginType.CREDENTIALS,
					null,
					null
				)
			);
		}
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

	public boolean isSMSLoginAction(HttpServletRequest request) {
		String controlAction = getControlActionValue(request);
		return LOGIN_EVENT_SMS_LOGIN.equals(controlAction);
	}

	public boolean isFullWithSMSLoginAction(HttpServletRequest request) {
		String controlAction = getControlActionValue(request);
		return LOGIN_EVENT_FULL_WITH_SMS_LOGIN.equals(controlAction);
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

	private Collection<TwoStepLoginVerificator> getVerificators(ServletContext sc) {
		Map<String, TwoStepLoginVerificator> verficators = WebApplicationContextUtils.getWebApplicationContext(sc)
				.getBeansOfType(TwoStepLoginVerificator.class);
		return MapUtil.isEmpty(verficators) ? null : verficators.values();
	}

	public static ServletContext getServletContext(HttpServletRequest request, HttpSession session) {
		ServletContext context = null;
		try {
			context = session == null ? null : session.getServletContext();
		} catch (Exception e) {}

		if (context == null) {
			if (request == null) {
				try {
					RequestResponseProvider rrProvider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
					request = rrProvider == null ? null : rrProvider.getRequest();
				} catch (Exception e) {}
			}

			if (request != null) {
				try {
					context = request.getServletContext();
				} catch (Exception e) {}

				if (context == null) {
					try {
						context = request.getSession().getServletContext();
					} catch (Exception e) {}
				}
			}
		}

		return context;
	}

	/**
	 * This method is invoked by the IWAuthenticator and tries to log in or log
	 * out the user depending on the request parameters.
	 */
	public boolean processRequest(HttpServletRequest request) throws IWException {
		String username = null;

		try {
			if (isLoggedOn(request)) {
				if (isLogOffAction(request)) {
					HttpSession session = request.getSession();
					LoggedOnInfo info = getLoggedOnInfo(session);
					if (LOGINTYPE_AS_ANOTHER_USER.equals(info.getLoginType())) {
						this.logOutAsAnotherUser(request);

						UserLogin login = info.getUserLogin();
						onLoginSuccessful(request, login == null ? null : login.getUserLogin(), login == null ? null : login.getUserPassword());
					}
					else {
						IWContext iwc = CoreUtil.getIWContext();
						logOutUser(request, iwc == null ? null : iwc.getResponse(), info.getLogin());
					}
				}

			//	Not logged on
			} else {
				if (isLogOnAction(request)) {
					// int canLogin = STATE_LOGGED_OUT;
					LoginState canLogin = LoginState.LOGGED_OUT;
					username = getLoginUserName(request);
					String password = getLoginPassword(request);
					if ((username != null) && (password != null)) {
						canLogin = verifyPasswordAndLogin(request, username, password);
						// if (canLogin == STATE_LOGGED_ON) {
						if (canLogin.equals(LoginState.LOGGED_ON)) {
							// isLoggedOn(iwc);
							// internalSetState(iwc,"loggedon");
							// addon
							/*
							 * if (iwc.isParameterSet(LoginRedirectPageParameter)) {
							 * //System.err.println("redirect parameter is set");
							 * BuilderLogic.getInstance().setCurrentPriorityPageID(iwc,
							 * iwc.getParameter(LoginRedirectPageParameter)); }
							 */
							onLoginSuccessful(request, username, password);
						} else {
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
					} else if (isLoginByUUID(request)) {
						String uuid = request.getParameter(PARAM_LOGIN_BY_UNIQUE_ID);
						boolean success = logInByUUID(request, uuid);
						if (!success) {
							String referer = RequestUtil.getReferer(request);
							LOGGER.warning("Attempt to login with UUID: " + uuid + " failed from referer: " + referer + " , might be an attack");
						}
					}
				} else if (isTryAgainAction(request)) {
					internalSetState(request, LoginState.LOGGED_OUT);

				//SMS login action
				} else if (isSMSLoginAction(request)) {
					LoginState canLogin = LoginState.LOGGED_OUT;
					username = getLoginUserNameNoRemoveFromSession(request);
					String password = getLoginPasswordNoRemoveFromSession(request);
					canLogin = verifyPasswordWithoutLogin(request, username, password);
					String sessionId = null;
					if (canLogin != null && canLogin.getStateValue() == LoginState.USER_AND_PASSWORD_EXISTS.getStateValue()) {
						HttpSession session = request.getSession();
						ServletContext sc = getServletContext(request, session);
						Collection<TwoStepLoginVerificator> verificators = getVerificators(sc);
						boolean codeSent = false;
						if (ListUtil.isEmpty(verificators)) {
							LOGGER.warning("There are no verificators: " + TwoStepLoginVerificator.class.getName());
						} else {
							sessionId = getSessionId(request, true);
							for (TwoStepLoginVerificator verificator: verificators) {
								//Sending SMS message
								IWApplicationContext iwac = IWMainApplication.getIWMainApplication(sc).getIWApplicationContext();
								codeSent = verificator.doSendSecondStepVerification(iwac, username, sessionId);
								if (codeSent) {
									break;
								}
							}
						}
						if (codeSent) {
							//Putting username and password attributes into the session
							request.getSession().setAttribute(PARAMETER_USERNAME, username);
							request.getSession().setAttribute(PARAMETER_PASSWORD, password);
							request.setAttribute(PARAMETER_SESSION_ID, sessionId);

							//Setting the state
							internalSetState(request, LoginState.USER_AND_PASSWORD_EXISTS);
						} else {
							onLoginFailed(request, LoginState.STEP2FAILED, username);
						}
					} else {
						onLoginFailed(request, canLogin, username);
					}

				//Full login with user name, password and SMS
				} else if (isFullWithSMSLoginAction(request)) {
					username = getLoginUserNameNoRemoveFromSession(request);

					String smsCode = getSMSCode(request);
					String isCancel = getIsCancel(request);
					LoginState canLogin = LoginState.LOGGED_OUT;

					ServletContext sc = request.getSession().getServletContext();
					Collection<TwoStepLoginVerificator> verificators = getVerificators(sc);

					if (!StringUtil.isEmpty(isCancel) && Boolean.parseBoolean(isCancel)) {
						//	Canceling login, invalidate SMS code
						for (TwoStepLoginVerificator verificator: verificators) {
							//Invalidating SMS code
							verificator.invalidateSecondStepVerification(username);
						}

						//	Remove attributes from session
						removeAttributesFromSession(request);

						//	Go back
						onLoginFailed(request, canLogin, username);
					} else {
						String sessionId = getSessionId(request, false);
						LOGGER.info("Session ID to verify SMS code (" + smsCode + ") for user '" + username + "': " + sessionId);	//	TODO

						//	Logging in
						if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(smsCode)) {
							boolean smsCodePassed = false;
							if (!ListUtil.isEmpty(verificators)) {
								for (TwoStepLoginVerificator verificator: verificators) {
									//	Verifying SMS message
									smsCodePassed = verificator.checkSecondStepVerification(smsCode, username, sessionId);
								}
							}
							if (smsCodePassed) {
								LOGGER.info("Passed verification. Username: " + username + ", SMS code: " + smsCode + ", session ID: " + sessionId);	//	TODO

								String password = getLoginPassword(request);
								if (!StringUtil.isEmpty(password)) {
									canLogin = verifyPasswordAndLogin(request, username, password);
									//Remove attributes from session
									removeAttributesFromSession(request);

									//Invalidating SMS code
									for (TwoStepLoginVerificator verificator: verificators) {
										verificator.invalidateSecondStepVerification(username);
									}

									//Login
									if (canLogin.equals(LoginState.LOGGED_ON)) {
										onLoginSuccessful(request, username, password);
									} else {
										onLoginFailed(request, canLogin, username);
									}
								} else {
									LOGGER.warning("Password is unknown, can not login. Username: " + username + ", SMS code: " + smsCode + ", session ID: " + sessionId);
									onLoginFailed(request, LoginState.FAILED, username);
								}
							} else {
								LOGGER.warning("Did not pass SMS code verification. Username: " + username + ", SMS code: " + smsCode + ", session ID: " + sessionId);
								//Setting the state
								onLoginFailed(request, LoginState.FAILED, username);
							}
						} else {
							LOGGER.warning("Either username (" + username + ") or SMS code (" + smsCode + ") are unknown");
							//Setting the state
							onLoginFailed(request, LoginState.FAILED, username);
						}
					}
				}
			}
		} catch (Exception ex) {
			try {
				logOut(request, username);
			} catch (Exception e) {
				e.printStackTrace();
			}

			LOGGER.log(Level.WARNING, "Error processing request " + request.getRequestURI(), ex);
		}
		return true;
	}

	private String getSessionId(HttpServletRequest request, boolean onlyFromSession) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		String sessionId = session.getId();

		if (!onlyFromSession) {
			IWContext iwc = CoreUtil.getIWContext();
			String sessionIdFromParam = iwc == null ? request.getParameter(PARAMETER_SESSION_ID) : iwc.getParameter(PARAMETER_SESSION_ID);
			if (StringUtil.isEmpty(sessionIdFromParam)) {
				LOGGER.warning("Did not find session ID by param's name '" + PARAMETER_SESSION_ID + "'. Using session ID: " + sessionId);
			} else {
				if (sessionId.equals(sessionIdFromParam)) {
					LOGGER.info("IDs are the same: from session and parameter (" + sessionId + ")");
					return sessionId;
				} else {
					LOGGER.warning("Using session ID (" + sessionIdFromParam + ") from parameter!");	//	TODO
					return sessionIdFromParam;
				}
			}
		}

		return sessionId;
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
			IWMainApplication iwma = getIWMainApplication(request, request.getSession());
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
		if (!StringUtil.isEmpty(sAuthorizationHeader)) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				byte[] decodedBytes = null;
				try {
					decodedBytes = Base64.getDecoder().decode(encodedNamePassword.getBytes(CoreConstants.ENCODING_UTF8));
				} catch (Exception e) {}
				String decodedNamePassword = decodedBytes == null ? encodedNamePassword : new String(decodedBytes, CoreConstants.ENCODING_UTF8);
				int seperator = decodedNamePassword.indexOf(CoreConstants.COLON);
				if (seperator != -1) {
					String[] toReturn = new String[2];
					toReturn[0] = decodedNamePassword.substring(0, seperator);
					toReturn[1] = decodedNamePassword.substring(seperator + 1);
					return toReturn;
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error getting login name and password from auth. request encoded in Base64: '" + sAuthorizationHeader + "'", e);
			}
		}
		return null;
	}

	public String getLoginNameFromBasicAuthenticationRequest(HttpServletRequest request) {
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
		if (sAuthorizationHeader != null) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				byte[] decodedBytes = Base64.getDecoder().decode(encodedNamePassword.getBytes(CoreConstants.ENCODING_UTF8));
				String unencodedNamePassword = new String(decodedBytes, CoreConstants.ENCODING_UTF8);
				int seperator = unencodedNamePassword.indexOf(CoreConstants.COLON);
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
				byte[] decodedBytes = Base64.getDecoder().decode(encodedNamePassword.getBytes(CoreConstants.ENCODING_UTF8));
				String unencodedNamePassword = new String(decodedBytes, CoreConstants.ENCODING_UTF8);
				int seperator = unencodedNamePassword.indexOf(':');
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
		String username = null;
		try {
			if (sAuthorizationHeader != null) {
				HttpSession session = request.getSession();
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				byte[] decodedBytes = null;
				try {
					decodedBytes = Base64.getDecoder().decode(encodedNamePassword.getBytes(CoreConstants.ENCODING_UTF8));
				} catch (Exception e) {}
				String decodedNamePassword = decodedBytes == null ? encodedNamePassword : new String(decodedBytes, CoreConstants.ENCODING_UTF8);

				int seperator = decodedNamePassword.indexOf(CoreConstants.COLON);
				if (seperator != -1) {
					username = decodedNamePassword.substring(0, seperator);
					String password = decodedNamePassword.substring(seperator + 1);

					LoginState canLogin = LoginState.LOGGED_OUT;
					LoggedOnInfo lInfo = getLoggedOnInfo(session, username);
					if (!isLoggedOn(request) && lInfo != null) {
						// used for re-logging in clients that do not keep cookies/session
						LoginSession lSession = LoginBusinessBean.getLoginSessionBean();
						lSession.setLoggedOnInfo(lInfo);
						lSession.setUser(lInfo.getUser());
						// TODO: some more variables need to be set in LoginSession if this
						// is supposed to work for clients with more capability than just
						// webdav-ing. Needs more refactoring than I have time for now.
						onLoginSuccessful(request, username, password);
						return true;
					}
					else {
						canLogin = verifyPasswordAndLogin(request, username, password);
						if (canLogin.equals(LoginState.LOGGED_ON)) {
							onLoginSuccessful(request, username, password);
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
				logOut(request, username);
			}
			catch (Exception e) {
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
		if (isLoggedOn(iwc)) {
			LoginBusinessBean.getLoginSessionBean().setLoginAttribute(key, value);
		} else {
			HttpSession session = iwc == null ? null : iwc.getSession();
			throw new NotLoggedOnException("Session ID: " + (session == null ? "unknown" : session.getId()));
		}
	}

	public static Object getLoginAttribute(String key, IWUserContext iwc) throws NotLoggedOnException {
		if (isLoggedOn(iwc)) {
			return LoginBusinessBean.getLoginSessionBean().getLoginAttribute(key);
		} else {
			HttpSession session = iwc == null ? null : iwc.getSession();
			throw new NotLoggedOnException("Session ID: " + (session == null ? "unknown" : session.getId()));
		}
	}

	public static void removeLoginAttribute(String key, IWUserContext iwc) throws RemoteException, RemoveException {
		if (isLoggedOn(iwc)) {
			LoginBusinessBean.getLoginSessionBean().removeLoginAttribute(key);
		}
		else if (LoginBusinessBean.getLoginSessionBean() != null) {
			removeLoginSession(iwc);
		}
	}

	public static com.idega.user.data.bean.User getUser(IWUserContext iwc) {
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
	protected boolean logIn(IWContext iwc, User user, String userName) throws Exception {
		return logIn(iwc.getRequest(), iwc.getResponse(), user, userName);
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
	protected boolean logIn(HttpServletRequest request, HttpServletResponse response, User user, String userName) throws Exception {
		UserLogin userLogin = null;
		try {
			userLogin = getUserLoginDAO().findLoginForUser(user);
		} catch (Exception e) {}
		if (userLogin == null && !StringUtil.isEmpty(userName)) {
			userLogin = getUserLoginDAO().findLoginByUsername(userName);
		}

		if (userLogin == null) {
			LOGGER.warning("Unable to find login for user " + user + ", ID: " + user.getId() + ", user name: " + userName);
			return false;
		}

		HttpSession session = request.getSession(true);
		storeUserAndGroupInformationInSession(session, user);
		LoginRecord loginRecord = getUserLoginDAO().createLoginRecord(userLogin, request.getRemoteAddr(), user);
		String loginType = getLoginType(request, userLogin);
		storeLoggedOnInfoInSession(request, session, userLogin, userLogin.getUserLogin(), user, loginRecord, loginType);
		doPublishLoggedInEvent(request, response, getServletContext(request, session), user, userName, loginType);
		return true;
	}

	protected boolean logIn(IWContext iwc, UserLogin userLogin) throws Exception {
		return logIn(iwc.getRequest(), iwc.getResponse(), userLogin);
	}

	protected boolean logIn(HttpServletRequest request, UserLogin userLogin) throws Exception {
		return logIn(request, null, userLogin);
	}

	private String getLoginType(HttpServletRequest request, UserLogin userLogin) {
		String loginType = request == null ? null : (String) request.getAttribute("login_type");
		loginType = StringUtil.isEmpty(loginType) ?
				userLogin == null ? null : userLogin.getLoginType() :
				loginType;
		return loginType;
	}

	public void doPublishLoggedInEvent(HttpServletRequest request, HttpServletResponse response, ServletContext context, User user, String userName, String loginType) {
		if (user == null) {
			return;
		}

		IWContext iwc = request == null || response == null || context == null ? CoreUtil.getIWContext() : new IWContext(request, response, context);
		ELUtil.getInstance().publishEvent(new UserHasLoggedInEvent(iwc, user.getId(), userName, loginType, request.getSession(true)));
	}

	protected boolean logIn(HttpServletRequest request, HttpServletResponse response, UserLogin userLogin) throws Exception {
		User user = userLogin.getUser();

		HttpSession session = request.getSession(true);
		storeUserAndGroupInformationInSession(session, user);
		LoginRecord loginRecord = getUserLoginDAO().createLoginRecord(userLogin, request.getRemoteAddr(), user);
		String userName = userLogin.getUserLogin();
		String loginType = getLoginType(request, userLogin);
		storeLoggedOnInfoInSession(request, session, userLogin, userName, user, loginRecord, loginType);
		doPublishLoggedInEvent(request, response, getServletContext(request, session), user, userName, loginType);
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
			IWApplicationContext iwac = getIWApplicationContext(null, session);
			com.idega.user.business.UserBusiness userbusiness = getUserBusiness(iwac);
			com.idega.user.data.User newUser = userbusiness.getUser(user.getId());
			Collection<com.idega.user.data.Group> userGroups = userbusiness.getUserGroups(newUser);
			if (userGroups != null) {
				List<Integer> ids = new ArrayList<>();
				try {
					for (com.idega.user.data.Group group: userGroups) {
						ids.add((Integer) group.getPrimaryKey());
					}
					groups = getGroupDAO().findGroups(ids);
				} catch (Exception e) {
					Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Failed to load groups by IDs: " + ids);
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

		IWMainApplication iwma = getIWMainApplication(null, session);
		UserProperties properties = new UserProperties(iwma, user.getId());
		lSession.setUserProperties(properties);
	}

	/**
	 *
	 * @param session
	 * @return
	 */
	private static IWMainApplication getIWMainApplication(HttpServletRequest request, HttpSession session) {
		IWMainApplication iwma = null;
		ServletContext servletContext = null;
		try {
			servletContext = getServletContext(request, session);
			iwma = IWMainApplication.getIWMainApplication(servletContext);
		} catch (Exception e) {
			LOGGER.warning("Error getting " + IWMainApplication.class.getName() + " from session's (" + session + ") servlet context " + servletContext);
		}
		iwma = iwma == null ? IWMainApplication.getDefaultIWMainApplication() : iwma;
		return iwma;
	}

	/**
	 *
	 * @param session
	 * @return
	 */
	private static IWApplicationContext getIWApplicationContext(HttpServletRequest request, HttpSession session) {
		IWMainApplication iwma = getIWMainApplication(request, session);
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

		session = session == null ? request.getSession(true) : session;
		IWMainApplication iwma = getIWMainApplication(request, session);
		AccessController aController = iwma.getAccessController();
		IWUserContext iwuc = new IWUserContextImpl(session, getServletContext(request, session));
		lInfo.setUserRoles(aController.getAllRolesForCurrentUser(iwuc));
		Map<String, Object> m = getLoggedOnInfoMap(session);
		m.put(lInfo.getLogin(), lInfo);
		setLoggedOnInfo(lInfo, session);

		//	Setting current locale for user that just logged in
		String preferredLocale = user.getPreferredLocale();
		Locale locale = null;
		if (StringUtil.isEmpty(preferredLocale)) {
			ICLanguage language = user.getNativeLanguage();
			if (language != null) {
				locale = ICLocaleBusiness.getLocaleFromLocaleString(language.getISOAbbreviation());
			}
		} else {
			locale = ICLocaleBusiness.getLocaleFromLocaleString(preferredLocale);
		}
		if (locale != null) {
			request.getSession(true).setAttribute(IWContext.LOCALE_ATTRIBUTE, locale);
		}
	}

	private boolean isLoginLockIsEnabled() {
		return IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("iw_login_lock_enabled", Boolean.FALSE);
	}

	private LoginState verifyPasswordAndLogin(HttpServletRequest request, String login, String password) throws Exception {
		boolean loginLockIsEnabled = isLoginLockIsEnabled();
		if (loginLockIsEnabled) {
			if (isLoginLocked(request, login)) {
				return LoginState.DISABLED;
			}
		}

		UserLogin userLogin = getUserLoginDAO().findLoginByUsername(login);
		if (userLogin == null) {
			createFailedLoginRecord(request, login);
			return LoginState.USER_NOT_FOUND;
		}

		User user = userLogin.getUser();
		IWMainApplication iwma = getIWMainApplication(request, request.getSession(true));
		boolean isAdmin = user.equals(iwma.getAccessController().getAdministratorUser());
		if (isLoginExpired(userLogin) && !isAdmin) {
			return LoginState.EXPIRED;
		}

		if (user.isDeleted() && !iwma.getSettings().getBoolean("user.deleted_can_login", false)) {
			LOGGER.warning(user + " (ID: " + user.getId() + ") is deleted");
			return LoginState.USER_NOT_FOUND;
		}

		LoginInfo loginInfo = userLogin.getLoginInfo();
		if (verifyPassword(userLogin, password)) {
			if (loginInfo != null && !loginInfo.getAccountEnabled() && !isAdmin) {
				return LoginState.EXPIRED;
			}
			if (logIn(request, userLogin)) {
				loginInfo.setFailedAttemptCount(0);
				getUserLoginDAO().merge(loginInfo);

				if (isLoginLockIsEnabled()) {
					getLoginLock().deleteAllPreviuosRecords(request, login);
				}

				return LoginState.LOGGED_ON;
			}
		} else {
			createFailedLoginRecord(request, login);

			if (isAdmin) { // admin must get unlimited attempts
				return LoginState.WRONG_PASSWORD;
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

		return LoginState.FAILED;
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
	 * @param userLogin is username, not <code>null</code>;
	 * @param password, not <code>null</code>;
	 * @return <code>true</code> if user can login by credentials,
	 * <code>false</code> otherwise!
	 */
	public boolean verifyPassword(UserLogin userLogin, String password){
		if (Encrypter.verifyOneWayEncrypted(userLogin.getUserPassword(), password)) {
			return true;
		}
		return false;
	}

	protected void logOut(IWContext iwc) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		LoggedOnInfo loggedOnInfo = getLoggedOnInfo(iwc);
		logOut(request, loggedOnInfo == null ? CoreConstants.EMPTY : loggedOnInfo.getLogin());
	}

	protected void logOut(HttpServletRequest request, String userName) throws Exception {
		if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("test_logout_stack", false) && !"root".equals(userName)) {
			try {
				throw new RuntimeException("Logging out user '" + userName + "'. Request URI: " + request.getRequestURI());
			} catch (Exception e) {
				String message = "Testing logout stack";
				LOGGER.log(Level.WARNING, message, e);
				CoreUtil.sendExceptionNotification(message, e);
			}
		}

		HttpSession session = request.getSession(true);
		if (LoginBusinessBean.getLoginSessionBean() != null) {
			LoggedOnInfo info = getLoggedOnInfo(session);
			if (info != null) {
				Map<String, Object> lm = getLoggedOnInfoMap(session);
				lm.remove(info.getLogin());
			}
			UserProperties properties = getUserProperties(session);
			if (properties != null) {
				properties.store();
			}
			removeLoginSession(session);
		}
		if (session != null) {
			session.invalidate();
		}
	}

	/**
	 * The key is the login name and the value is
	 * com.idega.core.accesscontrol.business.LoggedOnInfo
	 *
	 * @return Returns empty Map if no one is logged on
	 */
	public static Map<String, Object> getLoggedOnInfoMap(IWContext iwc) {
		return getDefaultLoginBusinessBean().getLoggedOnInfoMap(iwc.getRequest().getSession(true));
	}

	/**
	 * The key is the login name and the value is
	 * com.idega.core.accesscontrol.business.LoggedOnInfo
	 *
	 * @return Returns empty Map if no one is logged on
	 */
	public Map<String, Object> getLoggedOnInfoMap(HttpSession session) {
		ServletContext sc = getServletContext(null, session);
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
		HttpSession session = iwc.getRequest().getSession(true);
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
			throw new NotLoggedOnException("Session ID: " + (session == null ? "unknown" : session.getId()));
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
	 * </p>
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
				onLoginSuccessful(request, login, userLogin.getUserPassword());
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
		HttpSession session = request.getSession(true);
		if (LoginBusinessBean.getLoginSessionBean() != null) {
			Map<String, Object> m = getLoggedOnInfoMap(session);
			LoggedOnInfo _logOnInfo = (LoggedOnInfo) m.remove(getLoggedOnInfo(session).getLogin());
			if (_logOnInfo != null) {
				LoginRecord loginRecord = _logOnInfo.getLoginRecord();
				if (loginRecord != null) {
					try {
						loginRecord = DBUtil.getInstance().lazyLoad(loginRecord);
						getUserLoginDAO().createLogoutRecord(loginRecord);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Error creating logout record", e);
					}
				}
			}
		}

		LoginBusinessBean.getLoginSessionBean().retrieve();
		if (LoginBusinessBean.getLoginSessionBean().getUserEntity() != null) {
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
		HttpSession session = request.getSession(true);
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
			HttpSession session = request.getSession(true);
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
			storeLoggedOnInfoInSession(request, session, userLogin, login, user, loginRecord, LOGINTYPE_AS_ANOTHER_USER);
			onLoginSuccessful(request, login, userLogin.getUserPassword());
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
			IWApplicationContext iwac = getIWApplicationContext(request, request.getSession(true));
			com.idega.user.data.User user = getUserBusiness(iwac).getUser(personalId);
			LoginTableHome loginTableHome = (LoginTableHome) IDOLookup.getHome(LoginTable.class);
			Collection<LoginTable> logins = loginTableHome.findLoginsForUser(user);
			if (ListUtil.isEmpty(logins)) {
				return false;
			}

			return true;
		} catch (FinderException e) {
			LOGGER.warning("User with personal ID " + personalId + " does not exist");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error checking if user with personal ID has login", e);
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
		return logInUser(iwc.getRequest(), user);
	}

	public void setUserLoggedIn(HttpServletRequest request, User user, UserLogin login) throws Exception {
		HttpSession session = request.getSession();
		storeUserAndGroupInformationInSession(session, user);
		LoginRecord loginRecord = LoginDBHandler.recordLogin(login, request.getRemoteAddr());
		storeLoggedOnInfoInSession(
				request,
				session,
				login,
				login != null ? login.getUserLogin() : null,
				user,
				loginRecord,
				login != null ? login.getLoginType() : null
		);

		onLoginSuccessful(request, login != null ? login.getUserLogin() : null, login == null ? null : login.getUserPassword());
	}

	public boolean logInUser(HttpServletRequest request, User user) {
		List<UserLogin> logins = null;
		try {
			logins = getUserLoginDAO().findAllLoginsForUser(user);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to find logins for user " + user, e);
		}

		UserLogin login = null;
		try {
			login = chooseLoginRecord(request, logins, user);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to find " + LoginTable.class.getName() +  " for user " + user, e);
		}

		try {
			if (logIn(request, login)) {
				setUserLoggedIn(request, user, login);
				return Boolean.TRUE;
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to log in user " + user, e);
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
			if (loginType == null) {
				userLogin = this.chooseLoginRecord(request, logins, user);
			} else {
				userLogin = this.chooseLoginRecord(request, logins, user, loginType);
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
					onLoginSuccessful(request, userName, StringUtil.isEmpty(password) ? userLogin.getUserPassword() : password);
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
			if (userLogin == null) {
				try {
					throw new LoginCreateException("No login found by UUID: '" + uuid + "', request URI: " + request.getRequestURI() + request.getQueryString());
				} catch (LoginCreateException e1) {
					e1.printStackTrace();
				}
			} else {
				returner = logIn(request, userLogin);
				if (returner) {
					onLoginSuccessful(request, userLogin.getUserLogin(), userLogin.getUserPassword());
				}
			}
		} catch (EJBException e) {
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
		return IBOLookup.getServiceInstance(iwac, com.idega.user.business.UserBusiness.class);
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
			throw new NotLoggedOnException("Session ID: " + (session == null ? "unknown" : session.getId()));
		}
	}

	public com.idega.user.data.User getCurrentUserLegacy(HttpSession session) {
		User user = getUser(session);
		if (user != null) {
			try {
				return getUserBusiness(getIWApplicationContext(null, session)).getUser(user.getId());
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}
		}
		else {
			throw new NotLoggedOnException("Session ID: " + (session == null ? "unknown" : session.getId()));
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

	/**
	 * <p>Method requires com.idega.block.login module, it will always
	 * return <code>false</code> if module not provided.</p>
	 * @param request to create record for, not <code>null</code>;
	 * @param username is {@link LoginTable#getUserLogin()},
	 * skipped if <code>null</code>;
	 * @return <code>true</code> when record created, <code>false</code>
	 * otherwise.
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean createFailedLoginRecord(HttpServletRequest request, String username) {
		if (getLoginLock() != null) {
			return getLoginLock().createFailedLoginRecord(request, username);
		}

		return false;
	}

	/**
	 *
	 * @param request from where login attempt was made, not <code>null</code>;
	 * @param username is {@link LoginTable#getUserLogin()},
	 * skipped if <code>null</code>;
	 * @return <code>true</code> when there was too many unsuccessful
	 * attempts to login from given IP address, <code>false</code> otherwise.
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	protected boolean isLoginLocked(HttpServletRequest request, String username) {
		if (isLoginLockIsEnabled()) {
			if (getLoginLock() != null) {
				return getLoginLock().isLoginLocked(request, username);
			}
		}

		return Boolean.FALSE;
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

	@Autowired(required=false)
	private LoginLock loginLock;

	protected LoginLock getLoginLock() {
		if (this.loginLock == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.loginLock;
	}

	/**
	 * Verify password and user name, but do not login yet
	 * @param request
	 * @param login
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private LoginState verifyPasswordWithoutLogin(HttpServletRequest request, String login, String password) throws Exception {
		if (StringUtil.isEmpty(login)) {
			return LoginState.FAILED;
		}
		if (StringUtil.isEmpty(password)) {
			return LoginState.FAILED;
		}

		boolean loginLockIsEnabled = isLoginLockIsEnabled();
		if (loginLockIsEnabled) {
			if (isLoginLocked(request, login)) {
				return LoginState.DISABLED;
			}
		}

		UserLogin userLogin = getUserLoginDAO().findLoginByUsername(login);
		if (userLogin == null) {
			createFailedLoginRecord(request, login);
			return LoginState.USER_NOT_FOUND;
		}

		User user = userLogin.getUser();
		IWMainApplication iwma = getIWMainApplication(request, request.getSession());
		boolean isAdmin = user.equals(iwma.getAccessController().getAdministratorUser());
		if (isLoginExpired(userLogin) && !isAdmin) {
			return LoginState.EXPIRED;
		}

		LoginInfo loginInfo = userLogin.getLoginInfo();
		if (verifyPassword(userLogin, password)) {
			if (loginInfo != null && !loginInfo.getAccountEnabled() && !isAdmin) {
				return LoginState.EXPIRED;
			}
			return LoginState.USER_AND_PASSWORD_EXISTS;
		} else {
			createFailedLoginRecord(request, login);

			if (isAdmin) { // admin must get unlimited attempts
				return LoginState.WRONG_PASSWORD;
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

		return LoginState.FAILED;
	}



}