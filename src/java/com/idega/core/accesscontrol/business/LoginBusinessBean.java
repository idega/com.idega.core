/*
 * $Id: LoginBusinessBean.java,v 1.56 2006/01/12 15:30:21 tryggvil Exp $
 * 
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.accesscontrol.business;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.LoginInfoHome;
import com.idega.core.accesscontrol.data.LoginRecord;
import com.idega.core.accesscontrol.data.LoginRecordHome;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.data.IDOLookup;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserProperties;
import com.idega.user.util.Converter;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.RequestUtil;

/**
 * <p>
 * This is the default business handler for logging a User into the idegaWeb Authentication system.<br/>
 * This class is used by the IWAuthenticator filter and the default Login module for logging users into
 * the system.<br/>
 * </p>
 *
 * Last modified: $Date: 2006/01/12 15:30:21 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,
 * 		   <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.56 $
 */
public class LoginBusinessBean implements IWPageEventListener {
	//public static String UserAttributeParameter = "user_login";
	//public static String PermissionGroupParameter = "user_permission_groups";
	public static String LoginStateParameter = "login_state";
	//public static String LoginStateMsgParameter = "login_state_msg";
	//public static String LoginRedirectPageParameter = "login_redirect_page";
	//public static String LoginFailedRedirectPageParameter = "login_failed_redirect_page";
	//protected static String LoginAttributeParameter = "login_attributes";
	//private static String prmReservedLoginSessionAttribute = "reserved_login_attributes";
	private static String UserGroupRepresentativeParameter = "ic_user_representative_group";
	private static String PrimaryGroupsParameter = "ic_user_primarygroups";
	private static String PrimaryGroupParameter = "ic_user_primarygroup";
	private static final String _APPADDRESS_LOGGED_ON_LIST = "ic_loggedon_list";
	private static final String _LOGGINADDRESS_LOGGED_ON_INFO = "ic_loggedon_info";
	public static final String USER_PROPERTY_PARAMETER = "user_properties";

	public static final String LOGINTYPE_AS_ANOTHER_USER = "as_another_user";
	/**
	 * Value that the LoginStateParameter can have to signal that a login is being made
	 */
	public static final String LOGIN_EVENT_LOGIN="login";
	/**
	 * Value that the LoginStateParameter can have to signal that a log-out is being made
	 */
	public static final String LOGIN_EVENT_LOGOFF="logoff";
	/**
	 * Value that the LoginStateParameter can have to signal that a login retry is being made
	 */
	public static final String LOGIN_EVENT_TRYAGAIN="tryagain";
	
	public static final String PARAMETER_USERNAME="login";
	public static final String PARAMETER_PASSWORD="password";
	
	public static final String SESSION_PRM_LOGINNAME_FOR_INVALID_LOGIN = "loginname_for_invalid_login";
	public static boolean USING_OLD_USER_SYSTEM=false;
	
	public static final String PARAM_LOGIN_BY_UNIQUE_ID = "l_by_uuid";
	public static final String LOGIN_BY_UUID_AUTHORIZED_HOSTS_LIST = "LOGIN_BY_UUID_AUTHORIZED_HOSTS";

	protected static final String SESSION_KEY_CURRENT_USER = "iw_new_user";
	public static final String BEAN_ID="LoginBusinessBean";
	
	public LoginBusinessBean() {
	}
	
	public static Logger getLogger() {
		return Logger.getLogger(LoginBusinessBean.class.getName());
	}
	
	/**
	 * <p>
	 * Gets the Application-wide instance of this bean (LoginBusinessBean)
	 * </p>
	 * @param iwac
	 * @return
	 */
	public static LoginBusinessBean getLoginBusinessBean(IWApplicationContext iwac){
		LoginBusinessBean instance = (LoginBusinessBean) iwac.getApplicationAttribute(BEAN_ID);
		if(instance==null){
			instance = new LoginBusinessBean();
			iwac.setApplicationAttribute(BEAN_ID,instance);
		}
		return instance;
	}
	
	public static LoginBusinessBean getLoginBusinessBean(HttpServletRequest request){
		HttpSession session = request.getSession();
		return getLoginBusinessBean(session);
	}
	
	public static LoginBusinessBean getLoginBusinessBean(HttpSession session){
		IWApplicationContext iwac = getIWApplicationContext(session);
		return getLoginBusinessBean(iwac);
	}
	
	public static LoginBusinessBean getDefaultLoginBusinessBean(){
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		return getLoginBusinessBean(iwac);
	}
	
	/**
	 * <p>
	 * Checks and return if a user is logged on into the idegaWeb
	 * User System.<br/> This in turn checks if a certain session variable
	 * is set.
	 * </p>
	 * @param iwc
	 * @return
	 */
	public static boolean isLoggedOn(IWUserContext iwc) {
		if(isLoginSessionCreated(iwc)){
			return getUser(iwc)!=null;
		}
		else{
			return false;
		}
	    //if (iwc.getSessionAttribute(LoginAttributeParameter) == null) {
		//	return false;
		//}
		//return true;
	}
	/**
	 * <p>
	 * Checks and return if a user is logged on into the idegaWeb
	 * User System.<br/> This in turn checks if a certain session variable
	 * is set on the session of the current request.
	 * </p>
	 * @param iwc
	 * @return
	 */
	public boolean isLoggedOn(HttpServletRequest request){
		HttpSession session = request.getSession();
		return isLoggedOn(session);
	}
	
	/**
	 * <p>
	 * Checks and return if a user is logged on into the idegaWeb
	 * User System.<br/> This in turn checks if a certain session variable
	 * is set on the session of the current request.
	 * </p>
	 * @param iwc
	 * @return
	 */
	public boolean isLoggedOn(HttpSession session){
		if(isLoginSessionCreated(session)){
			return getUser(session)!=null;
		}
		else{
			return false;
		}
	}
	
	//public static void internalSetState(IWContext iwc, int state) {
	public static void internalSetState(IWContext iwc, LoginState state)throws RemoteException{
		getLoginSession(iwc).setLoginState(state);
	}

	//public static void internalSetState(IWContext iwc, int state) {
	public void internalSetState(HttpServletRequest request, LoginState state){
		try {
			getLoginSession(request).setLoginState(state);
		}
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static LoginState internalGetState(IWContext iwc) {
		try {
            /*Integer state = (Integer)iwc.getSessionAttribute(LoginStateParameter);
            if (state != null)
            	return state.intValue();
            else
            	return STATE_NO_STATE;
            */
            return getLoginSession(iwc).getLoginState();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return LoginState.NoState;
	}

	/**
	 * To get the userame of the current log-in attempt
	 * @return The username the current user is trying to log in with. Returns null if no log-in attemt is going on.
	 */
	protected String getLoginUserName(HttpServletRequest request) {
		return request.getParameter(PARAMETER_USERNAME);
	}
	/**
	 * To get the password of the current log-in attempt
	 * @return The password the current user is trying to log in with. Returns null if no log-in attemt is going on.
	 */
	protected String getLoginPassword(HttpServletRequest request) {
		return request.getParameter(PARAMETER_PASSWORD);
	}
	/**
	 * @return True if logIn was succesful, false if it failed
	 */
	protected boolean logInUser(HttpServletRequest request, String username, String password) {
		try {
		    /*
			int didLogin = verifyPasswordAndLogin(iwc, username, password);
			if (didLogin == STATE_LOGGED_ON) {
				onLoginSuccessful(iwc);
				return true;
			}*/
		    LoginState didLogin = verifyPasswordAndLogin(request,username,password);
		    if(didLogin.equals(LoginState.LoggedOn)){
		        onLoginSuccessful(request);
		        return true;
		    }
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return True if logOut was succesful, false if it failed
	 */
	protected boolean logOutUser(HttpServletRequest request) {
		try {

			logOut(request);
			//internalSetState(iwc, "loggedoff");
			//internalSetState(iwc, STATE_LOGGED_OUT);
			internalSetState(request,LoginState.LoggedOut);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Used for the LoggedOnInfo object to be able to log off users when their session expires.
	 * @return True if logOut was succesful, false if it failed
	 */
	public boolean logOutUserOnSessionTimeout(HttpSession session, LoggedOnInfo logOnInfo) {
		try {
			Map m = getLoggedOnInfoMap(session);
			LoggedOnInfo _logOnInfo = (LoggedOnInfo)m.remove(logOnInfo.getLogin());
			if (_logOnInfo != null) {
				LoginDBHandler.recordLogout(_logOnInfo.getLoginRecordId());
			} else
				return false;

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Invoked when the login failed
	 * Can be overrided in subclasses to alter behaviour
	 * By default this sets the state to "login failed" and does not log in a user
	 */
	//protected void onLoginFailed(IWContext iwc, int loginState, String username) {
	protected void onLoginFailed(HttpServletRequest request, LoginState loginState, String username) {
		logOutUser(request);
		internalSetState(request,loginState);
		try {
			getLoginSession(request).setUserLoginName(username);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @deprecated replaced with onLoginFailed(HttpServletRequest...)
	 */
	//protected void onLoginFailed(IWContext iwc, int loginState, String username) {
	protected void onLoginFailed(IWContext iwc, LoginState loginState, String username)throws RemoteException {  
		onLoginFailed(iwc.getRequest(),loginState,username);
		//internalSetState(iwc, loginState);
		//iwc.setSessionAttribute(UserAttributeParameter, username);
		//internalSetState(iwc,loginState);
		//getLoginSession(iwc).setUserLoginName(username);
	}
	
	/**
	 * Invoked when the login was succesful
	 * Can be overrided in subclasses to alter behaviour
	 * By default this sets the state to "logged on"
	 */
	protected void onLoginSuccessful(HttpServletRequest request){
		internalSetState(request,LoginState.LoggedOn);
	}
	
	/**
	 * This method is called to remain backwards compatible, it may be removed in future versions.
	 * @deprecated Replaced with onLoginSuccesful(HttpServletRequest);
	 */
	protected void onLoginSuccessful(IWContext iwc)throws RemoteException {
		//internalSetState(iwc, "loggedon");
		//internalSetState(iwc, STATE_LOGGED_ON);
	    //internalSetState(iwc,LoginState.LoggedOn);
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

	protected boolean isTryAgainAction(HttpServletRequest request) {
		String controlAction = getControlActionValue(request);
		return LOGIN_EVENT_TRYAGAIN.equals(controlAction);
	}

	private String getControlActionValue(HttpServletRequest request) {
		return request.getParameter(LoginBusinessBean.LoginStateParameter);
	}

	/**
	 * The method invoked when the login presentation module sends a login to this class
	 */
	public boolean actionPerformed(IWContext iwc) throws IWException {
		HttpServletRequest request = iwc.getRequest();
		return processRequest(request);
	}
	
	/**
	 * This method is invoked by the IWAuthenticator and tries to log in or log out
	 * the user depending on the request 	parameters.
	 */
	public boolean processRequest(HttpServletRequest request) throws IWException {
		try { 
			if (isLoggedOn(request)) {
				if (isLogOffAction(request)) {
					//logOut(iwc);
					//internalSetState(iwc,"loggedoff");
					HttpSession session = request.getSession();
					LoggedOnInfo info = getLoggedOnInfo(session);
					if (LOGINTYPE_AS_ANOTHER_USER.equals(info.getLoginType())) {
						this.logOutAsAnotherUser(request);
						onLoginSuccessful(request);
					} else {
						logOutUser(request);
					}
				}
			} else {

				if (isLogOnAction(request)) {
					//int canLogin = STATE_LOGGED_OUT;
				    LoginState canLogin = LoginState.LoggedOut;
					String username = getLoginUserName(request);
					String password = getLoginPassword(request);
					if ((username != null) && (password != null)) {
						canLogin = verifyPasswordAndLogin(request, username, password);
						//if (canLogin == STATE_LOGGED_ON) {
						if (canLogin.equals(LoginState.LoggedOn)) {
							//isLoggedOn(iwc);
							//internalSetState(iwc,"loggedon");
							// addon
							/*if (iwc.isParameterSet(LoginRedirectPageParameter)) {
								//System.err.println("redirect parameter is set");
								BuilderLogic.getInstance().setCurrentPriorityPageID(iwc, iwc.getParameter(LoginRedirectPageParameter));
							}*/
							onLoginSuccessful(request);
						} else {
							//logOut(iwc);
							//internalSetState(iwc,"loginfailed");
							
							/*if(iwc.isParameterSet(LoginFailedRedirectPageParameter)){
								BuilderLogic.getInstance().setCurrentPriorityPageID(iwc, iwc.getParameter(LoginFailedRedirectPageParameter));
								iwc.setSessionAttribute(SESSION_PRM_LOGINNAME_FOR_INVALID_LOGIN,username);
							}*/
							onLoginFailed(request, canLogin, username);
						}
					}
					else if(isLoginByUUID(request)){
						String uuid = request.getParameter(PARAM_LOGIN_BY_UNIQUE_ID);
						boolean success = logInByUUID(request, uuid);
						if(!success){
							String referer = RequestUtil.getReferer(request);
							System.err.println("[LoginBusinessBean] Attempt to login with UUID: "+uuid+" failed from referer: "+referer+" , might be an attack");
						}
					}
				} else if (isTryAgainAction(request)) {
					//internalSetState(iwc, "loggedoff");
					//internalSetState(iwc, STATE_LOGGED_OUT);
					internalSetState(request, LoginState.LoggedOut);
				}

			}
		} catch (Exception ex) {
			try {
				logOut(request);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ex.printStackTrace(System.err);
			//throw (IdegaWebException)ex.fillInStackTrace();
		}
		return true;
	}
	
	/**
	 * If you want to allow all referers to login via uuid do not set the LOGIN_BY_UUID_AUTHORIZED_HOSTS application property.
	 * The LOGIN_BY_UUID_AUTHORIZED_HOSTS property is a commaseparated list of host names and ip numbers that can login via uuid.
	 * @param iwc
	 * @return true if the parameter PARAM_LOGIN_BY_UNIQUE_ID is set and the referer is allowed to login by uuid.
	 */
	protected boolean isLoginByUUID(HttpServletRequest request) {
		if( RequestUtil.isParameterSet(request,PARAM_LOGIN_BY_UNIQUE_ID)){
			String referer = RequestUtil.getReferer(request);
			IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
			String allowedReferers  = iwma.getSettings().getProperty(LOGIN_BY_UUID_AUTHORIZED_HOSTS_LIST);
			if(allowedReferers==null || "".equals(allowedReferers)){
				return true;
			}
			if(referer!=null && allowedReferers.indexOf(referer)>=0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param request
	 * @return Returns null if no basic authentication request was maid.  Login has index = 0 and password = 1.
	 */
	public String[] getLoginNameAndPasswordFromBasicAuthenticationRequest(HttpServletRequest request){
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
    		if(sAuthorizationHeader != null) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
				String unencodedNamePassword = new String(dec.decodeBuffer(encodedNamePassword));
				int seperator = unencodedNamePassword.indexOf(':');
				if(seperator != -1){
					String[] toReturn = new String[2];
					toReturn[0] = unencodedNamePassword.substring(0,seperator);
					toReturn[1] = unencodedNamePassword.substring(seperator+1);
					return toReturn;
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
    		}
    		return null;
	}
	
	public String getLoginNameFromBasicAuthenticationRequest(HttpServletRequest request){
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
    		if(sAuthorizationHeader != null) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
				String unencodedNamePassword = new String(dec.decodeBuffer(encodedNamePassword));
				int seperator = unencodedNamePassword.indexOf(':');
				if(seperator != -1){
					return unencodedNamePassword.substring(0,seperator);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
    		}
    		return null;
	}
	
	public String getPasswordFromBasicAuthenticationRequest(HttpServletRequest request){
		String sAuthorizationHeader = RequestUtil.getAuthorizationHeader(request);
    		if(sAuthorizationHeader != null) {
			try {
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
				String unencodedNamePassword = new String(dec.decodeBuffer(encodedNamePassword));
				int seperator = unencodedNamePassword.indexOf(':');
				if(seperator != -1){
					return unencodedNamePassword.substring(seperator+1);
				}
			}
			catch (IOException e) {
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
	    		if(sAuthorizationHeader != null) {
	    			HttpSession session = request.getSession();
				String encodedNamePassword = sAuthorizationHeader.substring(6);
				sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
				String unencodedNamePassword = new String(dec.decodeBuffer(encodedNamePassword));
//				System.out.println("[IWAuthenticator]:Unencoded name and password: " + unencodedNamePassword);
				int seperator = unencodedNamePassword.indexOf(':');
				if(seperator != -1){
					String username = unencodedNamePassword.substring(0,seperator);
					String password = unencodedNamePassword.substring(seperator+1);
//					System.out.println("[IWAuthenticator]:Unencoded name: "+username+" and password: " + password);
					
					LoginState canLogin = LoginState.LoggedOut;
					
					LoggedOnInfo lInfo = getLoggedOnInfo(session,username);
					if(!isLoggedOn(request) && lInfo != null) {
						//used for re-logging in clients that do not keep cookies/session
						LoginSession lSession = getLoginSession(session);
						lSession.setLoggedOnInfo(lInfo);
						lSession.setUser(lInfo.getUser());
						//TODO: some more variables need to be set in LoginSession if this is supposed to work for clients with more capability than just webdav-ing.  Needs more refactoring than I have time for now. 
						onLoginSuccessful(request);
						return true;
					} else {
						canLogin = verifyPasswordAndLogin(request, username, password);
						if (canLogin.equals(LoginState.LoggedOn)) {
							onLoginSuccessful(request);
							return true;
						} else {
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
			ex.printStackTrace(System.err);
			//throw (IdegaWebException)ex.fillInStackTrace();
		}
		return false;
	}
	
	public void callForBasicAuthentication(HttpServletRequest request,HttpServletResponse response, String message) throws IOException{
		response.addHeader("WWW-Authenticate","Basic realm=\"" + "iw_login" + "\"");
		if(message!=null){
			response.sendError(401,message);
		} else {
			response.sendError(401);
		}
	}
	
	/*
	
	  public boolean isAdmin(IWContext iwc)throws Exception{
	
	    return iwc.isAdmin();
	
	  }
	
	*/
	public static void setLoginAttribute(String key, Object value, IWUserContext iwc) throws NotLoggedOnException {
		if (isLoggedOn(iwc)) {
		    try {
                /*
                Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
                ((Hashtable)obj).put(key, value);
                */
                getLoginSession(iwc).setLoginAttribute(key,value);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		} else {
			throw new NotLoggedOnException();
		}
	}
	public static Object getLoginAttribute(String key, IWUserContext iwc) throws NotLoggedOnException {
		if (isLoggedOn(iwc)) {
		    try {
                /*
                Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
                if (obj == null) {
                	return null;
                } else {
                	return ((Hashtable)obj).get(key);
                }*/
                return getLoginSession(iwc).getLoginAttribute(key);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                
            }
            return null;
		} else {
			throw new NotLoggedOnException();
		}
	}

	public static void removeLoginAttribute(String key, IWUserContext iwc) throws RemoteException,RemoveException{
		if (isLoggedOn(iwc)) {
		    /*
			Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
			if (obj != null) {
				((Hashtable)obj).remove(key);
			}
			*/
		    getLoginSession(iwc).removeLoginAttribute(key);
		} 
		/*else if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
			iwc.removeSessionAttribute(LoginAttributeParameter);
		*/
		else if(getLoginSession(iwc)!=null){
		    removeLoginSession(iwc);
		}
	}
	public static User getUser(IWUserContext iwc)  /* throws NotLoggedOnException */ {
		try {
			//return (User)LoginBusinessBean.getLoginAttribute(UserAttributeParameter, iwc);
		    return getLoginSession(iwc).getUser();
		} catch (NotLoggedOnException ex) {
			return null;
		}catch (RemoteException e) {

        }
		return null;
	}
	/**
	 * <p>
	 * Get the user that is currently logged into the system if any.<br/>
	 * Returns null if no user is logged on.<br/>
	 * </p>
	 * @param request
	 * @return
	 */
	public static User getUser(HttpServletRequest request)  /* throws NotLoggedOnException */ {
		HttpSession session = request.getSession();
		return getDefaultLoginBusinessBean().getUser(session);
	}
	/**
	 * <p>
	 * Get the user that is currently logged into the system if any.<br/>
	 * Returns null if no user is logged on.<br/>
	 * </p>
	 * @param request
	 * @return
	 */
	public User getUser(HttpSession session)  /* throws NotLoggedOnException */ {
		try {
		    return getLoginSession(session).getUser();
		} catch (NotLoggedOnException ex) {
			return null;
		}catch (RemoteException e) {
        }
		return null;
	}
	public static List getPermissionGroups(IWUserContext iwc)  {
		try {
            //return (List)LoginBusinessBean.getLoginAttribute(PermissionGroupParameter, iwc);
            return getLoginSession(iwc).getPermissionGroups();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}
	public static UserGroupRepresentative getUserRepresentativeGroup(IWUserContext iwc)  {
		try {
            //return (UserGroupRepresentative)LoginBusinessBean.getLoginAttribute(UserGroupRepresentativeParameter, iwc);
            return getLoginSession(iwc).getRepresentativeGroup();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}
	public static GenericGroup getPrimaryGroup(IWUserContext iwc){
		try {
            //return (GenericGroup)LoginBusinessBean.getLoginAttribute(PrimaryGroupParameter, iwc);
            return getLoginSession(iwc).getPrimaryGroup();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}
	protected static void setUser(IWUserContext iwc, User user) throws RemoteException {
		//LoginBusinessBean.setLoginAttribute(UserAttributeParameter, user, iwc);
	    getLoginSession(iwc).setUser(user);
	}
	protected static void setPermissionGroups(IWUserContext iwc, List value) throws RemoteException {
		//LoginBusinessBean.setLoginAttribute(PermissionGroupParameter, value, iwc);
	    getLoginSession(iwc).setPermissionGroups(value);
	}
	protected static void setUserRepresentativeGroup(IWUserContext iwc, UserGroupRepresentative value) throws RemoteException {
		//LoginBusinessBean.setLoginAttribute(UserGroupRepresentativeParameter, value, iwc);
	    getLoginSession(iwc).setRepresentativeGroup(value);
	}
	protected static void setPrimaryGroup(IWUserContext iwc, GenericGroup value) throws RemoteException {
		//LoginBusinessBean.setLoginAttribute(PrimaryGroupParameter, value, iwc);
	    getLoginSession(iwc).setPrimaryGroup(value);
	}
	
	/**
	 * Use this method if the one calling this method is not logged in, else use #logInAsAnotherUser(IWContext,User)
	 * @param iwc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	protected boolean logIn(IWContext iwc, User user) throws Exception {
		return logIn(iwc.getRequest(),user);
	}
	
	/**
	 * Use this method if the one calling this method is not logged in, else use #logInAsAnotherUser(IWContext,User)
	 * @param iwc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	protected boolean logIn(HttpServletRequest request, User user) throws Exception {
		Collection logins = ((LoginTableHome)IDOLookup.getHome(LoginTable.class)).findLoginsForUser(user);
		if(!logins.isEmpty()) {
			LoginTable loginTable = (LoginTable)logins.iterator().next();
		
			storeUserAndGroupInformationInSession(request.getSession(), user);
			
			int loginTableId = loginTable.getID();
			int loginRecordId = LoginDBHandler.recordLogin(loginTableId, request.getRemoteAddr());
			storeLoggedOnInfoInSession(request.getSession(), loginTableId, loginTable.getUserLogin(), user, loginRecordId, loginTable.getLoginType());
			return true;
		}
		return false;
	}
	
	protected boolean logIn(IWContext iwc, LoginTable loginTable) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		return logIn(request,loginTable);
	}
	
	protected boolean logIn(HttpServletRequest request, LoginTable loginTable) throws Exception {
		//New user system
		com.idega.core.user.data.UserHome uHome = (com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHome(User.class);
		User user = uHome.findByPrimaryKey(loginTable.getUserId());
		//New user system end
		
		//Old user system5
//		User user = ((com.idega.core.user.data.UserHome) com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(loginTable.getUserId());
		//Old user system end

		storeUserAndGroupInformationInSession(request.getSession(), user);
		
		int loginTableId = loginTable.getID();
		int loginRecordId = LoginDBHandler.recordLogin(loginTableId, request.getRemoteAddr());
		storeLoggedOnInfoInSession(request.getSession(), loginTableId, loginTable.getUserLogin(), user, loginRecordId, loginTable.getLoginType());
		return true;
	}

	protected void storeUserAndGroupInformationInSession(HttpSession session, User user) throws Exception {
		List groups = null;
		LoginSession lSession = getLoginSession(session);
		if(isUsingOldUserSystem()){
			//Old user system
			//iwc.setSessionAttribute(LoginAttributeParameter, new Hashtable());
			
			
			//LoginBusinessBean.setUser(iwc, user);
		    lSession.setUser(user);
			groups = UserBusiness.getUserGroups(user);
			//Old user system end
		}
		else{
			//New user system
			//iwc.setSessionAttribute(LoginAttributeParameter, new Hashtable());
			//LoginBusinessBean.setUser(iwc, user);
			lSession.setUser(user);
			IWApplicationContext iwac = getIWApplicationContext(session);
			com.idega.user.business.UserBusiness userbusiness = getUserBusiness(iwac);
			com.idega.user.data.User newUser = com.idega.user.util.Converter.convertToNewUser(user);
			Collection userGroups = userbusiness.getUserGroups(newUser);
			if(userGroups!=null)
			    groups = ListUtil.convertCollectionToList(userGroups);
			//New user system end
		}

		if (groups != null) {
			//LoginBusinessBean.setPermissionGroups(iwc, groups);
			lSession.setPermissionGroups(groups);
		}
		int userGroupId = user.getGroupID();
		if (userGroupId != -1) {
			//LoginBusinessBean.setUserRepresentativeGroup(iwc, ((com.idega.core.user.data.UserGroupRepresentativeHome)com.idega.data.IDOLookup.getHomeLegacy(UserGroupRepresentative.class)).findByPrimaryKeyLegacy(userGroupId));
			lSession.setRepresentativeGroup(((com.idega.core.user.data.UserGroupRepresentativeHome)com.idega.data.IDOLookup.getHomeLegacy(UserGroupRepresentative.class)).findByPrimaryKeyLegacy(userGroupId));
		}
		if (user.getPrimaryGroupID() != -1) {
		    GenericGroup primaryGroup = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHome(GenericGroup.class)).findByPrimaryKey(new Integer(user.getPrimaryGroupID()));
			//LoginBusinessBean.setPrimaryGroup(iwc, primaryGroup);
		    lSession.setPrimaryGroup(primaryGroup);
		}
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(session.getServletContext());
		UserProperties properties = new UserProperties(iwma, user.getID());
		//setLoginAttribute(USER_PROPERTY_PARAMETER, properties, iwc);
		lSession.setUserProperties(properties);
		
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getIWApplicationContext
	 * </p>
	 * @param session
	 * @return
	 */
	private static IWMainApplication getIWMainApplication(HttpSession session) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(session.getServletContext());
		return iwma;
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method getIWApplicationContext
	 * </p>
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
	private boolean isUsingOldUserSystem()
	{
		return this.USING_OLD_USER_SYSTEM;
	}
	protected void storeLoggedOnInfoInSession(HttpSession session, int loginTableId, String login, User user, int loginRecordId, String loginType) throws NotLoggedOnException, RemoteException {
		LoggedOnInfo lInfo = createLoggedOnInfo();
		lInfo.setLoginTableId(loginTableId);
		lInfo.setLogin(login);
		//lInfo.setSession(iwc.getSession());
		lInfo.setTimeOfLogon(IWTimestamp.RightNow());
		lInfo.setUser(user);
		lInfo.setLoginRecordId(loginRecordId);
		if (loginType != null && !loginType.equals("")) {
			lInfo.setLoginType(loginType);
		}
		
		IWMainApplication iwma = getIWMainApplication(session);
		AccessController aController = iwma.getAccessController();
		IWUserContext iwuc = new IWUserContextImpl(session,session.getServletContext());
		lInfo.setUserRoles(aController.getAllRolesForCurrentUser(iwuc));
		
		Map m = getLoggedOnInfoMap(session);
		m.put(lInfo.getLogin(),lInfo);
		
		//getLoggedOnInfoList(iwc).add(lInfo);
		setLoggedOnInfo(lInfo, session);
		
	}

	private LoginState verifyPasswordAndLogin(HttpServletRequest request, String login, String password) throws Exception {
		LoginTableHome ltableHome = (LoginTableHome) IDOLookup.getHome(LoginTable.class);
		Collection logins = ltableHome.findByLogin(login);
		//LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), login);
		//if (login_table == null) {
		if(logins.isEmpty()){
			//return STATE_NO_USER;
		    return LoginState.NoUser;
		}
		//if (login_table.length > 0) {
		else if(logins.size()>0){
			LoginTable loginTable = (LoginTable) logins.iterator().next();
			//LoginTable loginTable = login_table[0];
			User user = loginTable.getUser();
			IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
			boolean isAdmin = user.equals(iwma.getAccessController().getAdministratorUser());
			if (isLoginExpired(loginTable) && !isAdmin) {
				//return STATE_LOGIN_EXPIRED;
			    return LoginState.Expired;
			}
			LoginInfo loginInfo = null;
			try {
				LoginInfoHome loginInfoHome = (LoginInfoHome) IDOLookup.getHome(LoginInfo.class);
				loginInfo = loginInfoHome.findByPrimaryKey(loginTable.getPrimaryKey());
			} catch (FinderException fe) {
				//Nothing done
			}
			if (Encrypter.verifyOneWayEncrypted(loginTable.getUserPassword(), password)) {
				if (loginTable != null) {
					if (loginInfo!=null && !loginInfo.getAccountEnabled() && !isAdmin) {
						//return STATE_LOGIN_EXPIRED;
					    return LoginState.Expired;
					}
					if (logIn(request, loginTable)) {
						loginInfo.setFailedAttemptCount(0);
						loginInfo.store();
						//return STATE_LOGGED_ON;
						return LoginState.LoggedOn;
					}
				} else {
					try {
						throw new LoginCreateException("No record chosen");
					} catch (LoginCreateException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				if(isAdmin) { // admin must get unlimited attempts
					//return STATE_WRONG_PASSW;
				    return LoginState.WrongPassword;
				}
				//int returnCode = STATE_WRONG_PASSW;
				LoginState returnCode = LoginState.WrongPassword;
				int maxFailedLogginAttempts = 0;
				try {
					String maxStr = iwma.getBundle("com.idega.core").getProperty("max_failed_login_attempts");
					maxFailedLogginAttempts = Integer.parseInt(maxStr);
				} catch(Exception e) {
					// default used, no maximum
				}
				if(maxFailedLogginAttempts!=0) {
					int failedAttempts = loginInfo.getFailedAttemptCount();
					failedAttempts++;
					loginInfo.setFailedAttemptCount(failedAttempts);
					if(failedAttempts==maxFailedLogginAttempts-1) {
						System.out.println("login failed, disabled next time");
						//returnCode = STATE_LOGIN_FAILED_DISABLED_NEXT_TIME;
						returnCode = LoginState.FailedDisabledNextTime;
					} else if(failedAttempts>=maxFailedLogginAttempts) {
						System.out.println("Maximum loggin attemps, disabling account " + login);
						loginInfo.setAccountEnabled(false);
						loginInfo.setFailedAttemptCount(0);
					} else {
						System.out.println("Login failed, #" + failedAttempts);
					}
					loginInfo.store();
				}
				return returnCode;
			}
		} else {
			//return STATE_NO_USER;
		    return LoginState.NoUser;
		}

		//return STATE_LOGIN_FAILED;
		return LoginState.Failed;
	}
	
	public  void resetPassword(String login, String newPassword, boolean changeNextTime) throws Exception {
		LoginTable[] loginTables = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(
				com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), login);
		if (loginTables!=null && loginTables.length > 0) {
			LoginTable loginTable = loginTables[0];
			LoginInfoHome loginInfoHome = (LoginInfoHome) IDOLookup.getHome(LoginInfo.class);
			LoginInfo loginInfo = loginInfoHome.findByPrimaryKey(loginTable.getPrimaryKey());
			User user = loginTable.getUser();
			changeUserPassword(user, newPassword);
			loginInfo.setFailedAttemptCount(0);
			loginInfo.setAccessClosed(false);
			if(changeNextTime) {
				loginInfo.setChangeNextTime(true);
			}
			loginInfo.store();
		}
	}
	
	public boolean verifyPassword(User user, String login, String password) throws IOException, SQLException {
		boolean returner = false;
		LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(), Integer.toString(user.getID()), com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), login);
		if (login_table != null && login_table.length > 0) {
			if (Encrypter.verifyOneWayEncrypted(login_table[0].getUserPassword(), password)) {
				returner = true;
			}
		}
		return returner;
	}
	
	protected void logOut(IWContext iwc) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		logOut(request);
	}
	
	protected void logOut(HttpServletRequest request) throws Exception {
		//if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
		HttpSession session = request.getSession();
	    if (getLoginSession(request) != null) {
			// this.getLoggedOnInfoList(iwc).remove(this.getLoggedOnInfo(iwc));
			LoggedOnInfo info = getLoggedOnInfo(session);
			if(info!=null){
				Map lm = getLoggedOnInfoMap(session);
				lm.remove(info.getLogin());
			}

			UserProperties properties = getUserProperties(session);
			if (properties != null) {
				properties.store();
			}

			//iwc.removeSessionAttribute(LoginAttributeParameter);
			removeLoginSession(session);
		}
		session.invalidate();
	}

	/**
	 * The key is the login name and the value is com.idega.core.accesscontrol.business.LoggedOnInfo
	 * @return Returns empty Map if no one is logged on
	 */
	public static Map getLoggedOnInfoMap(IWContext iwc) {
		return getDefaultLoginBusinessBean().getLoggedOnInfoMap(iwc.getSession());
	}
	
	/**
	 * The key is the login name and the value is com.idega.core.accesscontrol.business.LoggedOnInfo
	 * @return Returns empty Map if no one is logged on
	 */
	public Map getLoggedOnInfoMap(HttpSession session) {
		ServletContext sc = session.getServletContext();
		Map loggedOnMap = (Map)sc.getAttribute(_APPADDRESS_LOGGED_ON_LIST);
		if (loggedOnMap == null) {
			loggedOnMap = new TreeMap();
			sc.setAttribute(_APPADDRESS_LOGGED_ON_LIST, loggedOnMap);
		}
		return loggedOnMap;
	}
	
	/**
	 * @return returns empty Collection if no one is logged on
	 */
	public static Collection getLoggedOnInfoCollection(IWContext iwc) {
		return getLoggedOnInfoMap(iwc).values();
	}
	
	/**
	 * returns null if user is not logged on
	 */
	public static LoggedOnInfo getLoggedOnInfo(IWContext iwc, String loginName) {
		return (LoggedOnInfo)getLoggedOnInfoMap(iwc).get(loginName);
	}
	
	/**
	 * returns null if user is not logged on
	 */
	public LoggedOnInfo getLoggedOnInfo(HttpSession session, String loginName) {
		return (LoggedOnInfo)getLoggedOnInfoMap(session).get(loginName);
	}
	
	/**
	 * The key is the login name and the value is com.idega.core.accesscontrol.business.LoggedOnInfo
	 * @param session
	 * @return
	 */

	/*public static Map getLoggedOnInfoMap(HttpSession session) {
		Map loggedOnMap = null;
		MethodFinder finder = MethodFinder.getInstance();
		ServletContext context = null;

		try {
			Method method = finder.getMethodWithNameAndNoParameters(HttpSession.class, "getServletContext");
			try {
				context = (ServletContext)method.invoke(session, null);
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			System.out.println("The method session.getServletContext() is not in this implementation of the Servlet spec.");
			e.printStackTrace();
		}

		
		if (context != null) {
			loggedOnMap = (Map)context.getAttribute(_APPADDRESS_LOGGED_ON_LIST);
		}

		if (loggedOnMap == null) {
			loggedOnMap = new TreeMap();
			if (context != null) {
				context.setAttribute(_APPADDRESS_LOGGED_ON_LIST, loggedOnMap);
			}
		}
		return loggedOnMap;
	}*/

	public static LoggedOnInfo getLoggedOnInfo(IWUserContext iwc) {
		try {
            // Not stored as LoginAttribute because it is HttpSessionBindingListener
            //return (LoggedOnInfo)getLoginAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, iwc);
            //return (LoggedOnInfo)iwc.getSessionAttribute(_LOGGINADDRESS_LOGGED_ON_INFO);
            return getLoginSession(iwc).getLoggedOnInfo();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}
	
	public LoggedOnInfo getLoggedOnInfo(HttpSession session) {
		try {
            // Not stored as LoginAttribute because it is HttpSessionBindingListener
            //return (LoggedOnInfo)getLoginAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, iwc);
            //return (LoggedOnInfo)iwc.getSessionAttribute(_LOGGINADDRESS_LOGGED_ON_INFO);
            return getLoginSession(session).getLoggedOnInfo();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}


	public static void setLoggedOnInfo(LoggedOnInfo lInfo, IWContext iwc) throws NotLoggedOnException, RemoteException {
		HttpSession session = iwc.getSession();
		getDefaultLoginBusinessBean().setLoggedOnInfo(lInfo,session);
	}
	
	public void setLoggedOnInfo(LoggedOnInfo lInfo, HttpSession session) throws NotLoggedOnException, RemoteException {
        // Not stored as LoginAttribute because it is HttpSessionBindingListener
		//setLoginAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, lInfo, iwc);
		if(isLoggedOn(session)){
		    
			//iwc.setSessionAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, lInfo);
		    getLoginSession(session).setLoggedOnInfo(lInfo);
		} else {
			throw new NotLoggedOnException();
		}
	}
	public LoginContext changeUserPassword(User user, String password) throws Exception {
		LoginTable login = LoginDBHandler.getUserLogin(user.getID());
		LoginDBHandler.changePassword(login, password);
		LoginContext loginContext = new LoginContext(user, login.getUserLogin(), password);
		return loginContext;
	}
	
	/**
	 * Creates a wrapper object around the users login name and password in clear text (no decoding)
	 * @param user
	 * @return
	 */
	public static LoginContext getLoginContext(User user) {
		LoginTable login = LoginDBHandler.getUserLogin(user.getID());
		if(login!=null){
			LoginContext loginContext = new LoginContext(user, login.getUserLogin(), login.getUserPasswordInClearText());
			return loginContext;
		}
		else{
			return null;
		}
	}

	public LoginContext createNewUser(String fullName, String email, String preferredUserName, String preferredPassword) {
		UserBusiness ub = new UserBusiness();
		StringTokenizer tok = new StringTokenizer(fullName);
		String first = "";
		String middle = "";
		String last = "";
		if (tok.hasMoreTokens())
			first = tok.nextToken();
		if (tok.hasMoreTokens())
			middle = tok.nextToken();
		if (tok.hasMoreTokens())
			last = tok.nextToken();
		else {
			last = middle;
			middle = "";
		}
		LoginContext loginContext = null;
		try {
			User user = ub.insertUser(first, middle, last, "", null, null, null, null);
			String login = preferredUserName;
			String pass = preferredPassword;
			if (user != null) {
				if (email != null && email.length() > 0)
					ub.addNewUserEmail(user.getID(), email);
				if (login == null)
					login = LoginCreator.createLogin(user.getName());
				if (pass == null)
					pass = LoginCreator.createPasswd(8);
				LoginDBHandler.createLogin(user.getID(), login, pass);
				loginContext = new LoginContext(user, login, pass);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return loginContext;
	}
	/**
	 * <p>
	 * added for cookie login - calling this may be unsafe ( Aron )
	 * </p>
	 * @param request
	 * @param login
	 * @return
	 * @throws Exception
	 */
	public boolean logInUnVerified(HttpServletRequest request, String login) throws Exception {
		boolean returner = false;
		LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), login);
		if (login_table != null && login_table.length > 0) {
			LoginTable lTable = login_table[0];
			if (lTable != null) {
				returner = logIn(request, login_table[0]);
				if (returner)
					onLoginSuccessful(request);
			} else {
				try {
					throw new LoginCreateException("No record chosen");
				} catch (LoginCreateException e1) {
					e1.printStackTrace();
				}
			}
		}
		return returner;
	}

	public boolean logInAsAnotherUser(IWContext iwc, String personalID) throws Exception {
		boolean returner = false;
		try {
			com.idega.user.data.User user = getUserBusiness(iwc).getUser(personalID);
			returner = logInAsAnotherUser(iwc, user);
		} catch (FinderException e) {
			//e.printStackTrace();
			returner = false;
		} catch (RemoteException e) {
			e.printStackTrace();
			returner = false;
		}
		return returner;
	}

	public boolean retrieveLoginInformation(HttpServletRequest request) throws NotLoggedOnException, RemoteException {

		//logout
		
	    //if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
	    HttpSession session = request.getSession();
	    if(getLoginSession(session)!=null){
			Map m = getLoggedOnInfoMap(session);
			LoggedOnInfo _logOnInfo = (LoggedOnInfo)m.remove(getLoggedOnInfo(session).getLogin());
			if ( _logOnInfo != null ) {
				LoginDBHandler.recordLogout(_logOnInfo.getLoginRecordId());
			}
		}

		//login
		//Object obj = iwc.getSessionAttribute(prmReservedLoginSessionAttribute);
		//Object obj = iwc.getSessionAttribute(prmReservedLoginSessionAttribute);
	    getLoginSession(session).retrieve();
		//if (obj != null) {
	    if(getLoginSession(session).getUser()!=null){
	        
			//iwc.setSessionAttribute(LoginAttributeParameter, obj);
			return true;
		} else {
			return false;
		}

	}

	public void reserveLoginInformation(IWContext iwc) throws RemoteException {
		HttpServletRequest request = iwc.getRequest();
		reserveLoginInformation(request);
	}
	
	public void reserveLoginInformation(HttpServletRequest request) throws RemoteException {
		
	    //if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
	    if(getLoginSession(request)!=null){
			// this.getLoggedOnInfoList(iwc).remove(this.getLoggedOnInfo(iwc));

			//UserProperties properties = (UserProperties)getLoginAttribute(USER_PROPERTY_PARAMETER, iwc);
		    UserProperties properties = getLoginSession(request).getUserProperties();
			if (properties != null)
				properties.store();

			//iwc.setSessionAttribute(prmReservedLoginSessionAttribute, iwc.getSessionAttribute(LoginAttributeParameter));
			//iwc.setSessionAttribute(prmReservedLoginSessionAttribute,getLoginSession(iwc));
			
			getLoginSession(request).reserve();
			
                //logout
                //iwc.removeSessionAttribute(LoginAttributeParameter);
                //removeLoginSession(iwc);
           
		}
	}

	public void logOutAsAnotherUser(HttpServletRequest request) throws NotLoggedOnException, RemoteException {
		HttpSession session = request.getSession();
		LoggedOnInfo info = this.getLoggedOnInfo(session);
		int rec = info.getLoginRecordId();
		retrieveLoginInformation(request);
		info.setLoginType("");
		//setLoggedOnInfo(info,iwc);
		LoginDBHandler.recordLogout(rec);
	}
	
	/**
	 * Use this method if the one calling this method is logged in, else use #logIn(IWContext,User)
	 * @param iwc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean logInAsAnotherUser(IWContext iwc, User user) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		return logInAsAnotherUser(request,user);
	}
	
	/**
	 * Use this method if the one calling this method is logged in, else use #logIn(HttpServletRequest,User)
	 * @param request
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean logInAsAnotherUser(HttpServletRequest request, User user) throws Exception {
	    return logInAsAnotherUser(request,user,true);
	}

	/**
	 * Use this method if the one calling this method is logged in, else use #logIn(IWContext,User)
	 * @param request
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private boolean logInAsAnotherUser(HttpServletRequest request, User user,boolean reserveCurrentUser) throws Exception {

		if (isLoggedOn(request)) {
			HttpSession session = request.getSession();
//		    LoggedOnInfo info = this.getLoggedOnInfo(iwc);
			int loginTableId = -1;
			String login = null;
			Collection logins = ((LoginTableHome)IDOLookup.getHome(LoginTable.class)).findLoginsForUser(user);
			if(!logins.isEmpty()) {
				LoginTable loginTable = (LoginTable)logins.iterator().next();
				loginTableId = loginTable.getID();
				login = loginTable.getUserLogin();
			}
			
			User oldUser = getUser(request);
			if (oldUser.equals(user)) {
				return true;
			}
			if(reserveCurrentUser)
			    reserveLoginInformation(request);
			storeUserAndGroupInformationInSession(session, user);

//			int loginRecordId = LoginDBHandler.recordLogin(info.getLoginTableId(), iwc.getRemoteIpAddress(), user.getID());
//			storeLoggedOnInfoInSession(iwc, info.getLoginTableId(), info.getLogin(), user, loginRecordId, LOGINTYPE_AS_ANOTHER_USER);			
			int loginRecordId = LoginDBHandler.recordLogin(loginTableId, request.getRemoteAddr(), user.getID());
			storeLoggedOnInfoInSession(session, loginTableId, login, user, loginRecordId, LOGINTYPE_AS_ANOTHER_USER);
			onLoginSuccessful(request);
			
			return true;
		}

		return false;
	}


	public boolean logInByPersonalID(IWContext iwc, String personalId) throws Exception {
		HttpServletRequest request = iwc.getRequest();
		return logInByPersonalID(request,personalId);
	}
	
	public boolean logInByPersonalID(HttpServletRequest request, String personalId) throws Exception {
		boolean returner = false;
		try {
			IWApplicationContext iwac = getIWApplicationContext(request.getSession());
			com.idega.user.data.User user = getUserBusiness(iwac).getUser(personalId);
			LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getColumnNameUserID(), user.getPrimaryKey().toString());

			LoginTable lTable = this.chooseLoginRecord(request, login_table, user);
			if (lTable != null) {
				returner = logIn(request, lTable);
				if (returner)
					onLoginSuccessful(request);
			} else {
				try {
					throw new LoginCreateException("No record chosen");
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
	 * Logs you into idegaweb by a universally unique identifier UUID if it finds a user with that id.
	 * @param request
	 * @param uuid
	 * @return true if succeeded in login on a user with his UUID
	 * @throws Exception
	 */
	public boolean logInByUUID(HttpServletRequest request, String uuid) throws Exception {
		boolean returner = false;
		try {
			IWApplicationContext iwac = IWMainApplication.getIWMainApplication(request.getSession().getServletContext()).getIWApplicationContext();
			com.idega.user.data.User user = getUserBusiness(iwac).getUserByUniqueId(uuid);
			LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getColumnNameUserID(), user.getPrimaryKey().toString());

			LoginTable lTable = this.chooseLoginRecord(request, login_table, user);
			if (lTable != null) {
				returner = logIn(request, lTable);
				if (returner)
					onLoginSuccessful(request);
			} else {
				try {
					throw new LoginCreateException("No record chosen");
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
	 * @param loginRecords - all login records for one user
	 * @return LoginTable record to log on the system
	 */
	public LoginTable chooseLoginRecord(HttpServletRequest request, LoginTable[] loginRecords, User user) throws Exception {
		LoginTable chosenRecord = null;
		if (loginRecords != null) {
			for (int i = 0; i < loginRecords.length; i++) {
				String type = loginRecords[i].getLoginType();
				if (!(type != null && !type.equals(""))) {
					chosenRecord = loginRecords[i];
					break;
				}
			}
		}
		return chosenRecord;
	}
	
	/**
	 * Gets the last login record date before current logged record ( second last entry)
	 * @param userId
	 * @return
	 */
	public static java.sql.Date getLastLoginByUser(Integer userId) throws RemoteException{
		try {
			return getLoginRecordHome().getLastLoginByUserID(userId);
		} catch (FinderException e) {
			throw new RemoteException(e.getMessage());
		}
	}
	
	/**
	 * Gets the last login record date before current logged record ( second last entry)
	 * @param userId
	 * @return
	 */
	public static java.sql.Date getLastLoginByLogin(Integer loginId) throws RemoteException{
		try {
			return getLoginRecordHome().getLastLoginByLoginID(loginId);
		} catch (FinderException e) {
			throw new RemoteException(e.getMessage());
		}
	}
	
	private static LoginRecordHome getLoginRecordHome()throws RemoteException{
		return (LoginRecordHome) IDOLookup.getHome(LoginRecord.class);
	}

	public boolean isLoginExpired(LoginTable loginTable) {
		LoginInfo loginInfo = LoginDBHandler.getLoginInfo(loginTable.getID());
		return loginInfo.isLoginExpired();
	}

	protected com.idega.user.business.UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException {
		return (com.idega.user.business.UserBusiness)IBOLookup.getServiceInstance(iwac, com.idega.user.business.UserBusiness.class);
	}

	protected LoggedOnInfo createLoggedOnInfo() {
		return new LoggedOnInfo();
	}

	public static UserProperties getUserProperties(IWUserContext iwuc) {
		try {
            //return (UserProperties)getLoginAttribute(LoginBusinessBean.USER_PROPERTY_PARAMETER, iwuc);
            return getLoginSession(iwuc).getUserProperties();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public UserProperties getUserProperties(HttpSession session) {
		try {
            //return (UserProperties)getLoginAttribute(LoginBusinessBean.USER_PROPERTY_PARAMETER, iwuc);
            return getLoginSession(session).getUserProperties();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	public static LoginSession getLoginSession(IWUserContext iwc) throws RemoteException {
        return (LoginSession) IBOLookup.getSessionInstance(iwc, LoginSession.class);
	}
	
	public LoginSession getLoginSession(HttpServletRequest request) throws RemoteException {
		HttpSession session = request.getSession();
		return getLoginSession(session);
	}
	
	public LoginSession getLoginSession(HttpSession session) throws RemoteException {
        return (LoginSession) IBOLookup.getSessionInstance(session, LoginSession.class);
	}
	
	public static boolean isLoginSessionCreated(IWUserContext iwc){
		return IBOLookup.isSessionBeanInitialized(iwc,LoginSession.class);
	}
	
	public boolean isLoginSessionCreated(HttpSession session){
		return IBOLookup.isSessionBeanInitialized(session,LoginSession.class);
	}
	
	public boolean isLoginSessionCreated(HttpServletRequest request){
		return IBOLookup.isSessionBeanInitialized(request,LoginSession.class);
	}
	
	/**
	 * <p>
	 * Deletes the LoginSession object from the user session
	 * </p>
	 * @param iwc
	 * @throws RemoteException
	 * @throws RemoveException
	 */
	private static void removeLoginSession(IWUserContext iwc) throws RemoteException,RemoveException {
	   IBOLookup.removeSessionInstance(iwc,LoginSession.class);
	}
	/**
	 * <p>
	 * Deletes the LoginSession object from the user session
	 * </p>
	 * @param iwc
	 * @throws RemoteException
	 * @throws RemoveException
	 */
	private void removeLoginSession(HttpSession session) throws RemoteException,RemoveException {
		IBOLookup.removeSessionInstance(session,LoginSession.class);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCurrentUser
	 * </p>
	 * @param context
	 * @return
	 */
	public com.idega.user.data.User getCurrentUser(HttpSession session) {
		com.idega.core.user.data.User user = getUser(session);
		if(user!=null){
			try{
				String sessKey = SESSION_KEY_CURRENT_USER+user.getPrimaryKey().toString();
				com.idega.user.data.User newUser = (com.idega.user.data.User) session.getAttribute(sessKey);
				if(newUser==null){
					newUser = Converter.convertToNewUser(user);
					session.setAttribute(sessKey,newUser);
				}
				return newUser;
			}
			catch(Exception e){
				throw new RuntimeException("IWContext.getCurrentUser(): Error getting primary key of user. Exception was: "+e.getClass().getName()+" : "+e.getMessage());
			}
		}
		else{
			throw new NotLoggedOnException();
		}
		//return null;
	}

}