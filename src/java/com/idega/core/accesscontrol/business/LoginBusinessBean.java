package com.idega.core.accesscontrol.business;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.servlet.ServletContext;
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
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserProperties;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.reflect.MethodFinder;
/**
 * Title:        LoginBusiness The default login business handler for the accesscontrol framework
 * Description:
 * Copyright:    Copyright (c) 2000-2002 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,<a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1

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

	public static final String SESSION_PRM_LOGINNAME_FOR_INVALID_LOGIN = "loginname_for_invalid_login";
	public static boolean USING_OLD_USER_SYSTEM=false;

	
	
	public LoginBusinessBean() {
	}
	public static boolean isLoggedOn(IWUserContext iwc) {
		return getUser(iwc)!=null;
	    //if (iwc.getSessionAttribute(LoginAttributeParameter) == null) {
		//	return false;
		//}
		//return true;
	}
	
	//public static void internalSetState(IWContext iwc, int state) {
	public static void internalSetState(IWContext iwc, LoginState state)throws RemoteException{
		//iwc.setSessionAttribute(LoginStateParameter, new Integer(state));
		getLoginSession(iwc).setLoginState(state);
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
	protected String getLoginUserName(IWContext iwc) {
		return iwc.getParameter("login");
	}
	/**
	 * To get the password of the current log-in attempt
	 * @return The password the current user is trying to log in with. Returns null if no log-in attemt is going on.
	 */
	protected String getLoginPassword(IWContext iwc) {
		return iwc.getParameter("password");
	}
	/**
	 * @return True if logIn was succesful, false if it failed
	 */
	protected boolean logInUser(IWContext iwc, String username, String password) {
		try {
		    /*
			int didLogin = verifyPasswordAndLogin(iwc, username, password);
			if (didLogin == STATE_LOGGED_ON) {
				onLoginSuccessful(iwc);
				return true;
			}*/
		    LoginState didLogin = verifyPasswordAndLogin(iwc,username,password);
		    if(didLogin.equals(LoginState.LoggedOn)){
		        onLoginSuccessful(iwc);
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
	protected boolean logOutUser(IWContext iwc) throws RemoteException{
		try {

			logOut(iwc);
			//internalSetState(iwc, "loggedoff");
			//internalSetState(iwc, STATE_LOGGED_OUT);
			internalSetState(iwc,LoginState.LoggedOut);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Used for the LoggedOnInfo object to be able to log off users when their session expires.
	 * @return True if logOut was succesful, false if it failed
	 */
	public static boolean logOutUserOnSessionTimeout(HttpSession session, LoggedOnInfo logOnInfo) {
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
	protected void onLoginFailed(IWContext iwc, LoginState loginState, String username)throws RemoteException {  
		logOutUser(iwc);
		//internalSetState(iwc, loginState);
		//iwc.setSessionAttribute(UserAttributeParameter, username);
		internalSetState(iwc,loginState);
		getLoginSession(iwc).setUserLoginName(username);
	}
	/**
	 * Invoked when the login was succesful
	 * Can be overrided in subclasses to alter behaviour
	 * By default this sets the state to "logged on"
	 */
	protected void onLoginSuccessful(IWContext iwc)throws RemoteException {
		//internalSetState(iwc, "loggedon");
		//internalSetState(iwc, STATE_LOGGED_ON);
	    internalSetState(iwc,LoginState.LoggedOn);
	}

	public static boolean isLogOnAction(IWContext iwc) {
		return "login".equals(getControlActionValue(iwc));
	}

	public static boolean isLogOffAction(IWContext iwc) {
		return "logoff".equals(getControlActionValue(iwc));
	}

	protected static boolean isTryAgainAction(IWContext iwc) {
		return "tryagain".equals(getControlActionValue(iwc));
	}

	private static String getControlActionValue(IWContext iwc) {
		return iwc.getParameter(LoginBusinessBean.LoginStateParameter);
	}

	/**
	 * The method invoked when the login presentation module sends a login to this class
	 */
	public boolean actionPerformed(IWContext iwc) throws IWException {
		try { 
			if (isLoggedOn(iwc)) {
				if (isLogOffAction(iwc)) {
					//logOut(iwc);
					//internalSetState(iwc,"loggedoff");
					
					LoggedOnInfo info = getLoggedOnInfo(iwc);
					if (LOGINTYPE_AS_ANOTHER_USER.equals(info.getLoginType())) {
						this.logOutAsAnotherUser(iwc);
						onLoginSuccessful(iwc);
					} else {
						logOutUser(iwc);
					}
				}
			} else {

				if (isLogOnAction(iwc)) {
					//int canLogin = STATE_LOGGED_OUT;
				    LoginState canLogin = LoginState.LoggedOut;
					String username = getLoginUserName(iwc);
					String password = getLoginPassword(iwc);
					if ((username != null) && (password != null)) {
						canLogin = verifyPasswordAndLogin(iwc, username, password);
						//if (canLogin == STATE_LOGGED_ON) {
						if (canLogin.equals(LoginState.LoggedOn)) {
							//isLoggedOn(iwc);
							//internalSetState(iwc,"loggedon");
							// addon
							/*if (iwc.isParameterSet(LoginRedirectPageParameter)) {
								//System.err.println("redirect parameter is set");
								BuilderLogic.getInstance().setCurrentPriorityPageID(iwc, iwc.getParameter(LoginRedirectPageParameter));
							}*/
							onLoginSuccessful(iwc);
						} else {
							//logOut(iwc);
							//internalSetState(iwc,"loginfailed");
							
							/*if(iwc.isParameterSet(LoginFailedRedirectPageParameter)){
								BuilderLogic.getInstance().setCurrentPriorityPageID(iwc, iwc.getParameter(LoginFailedRedirectPageParameter));
								iwc.setSessionAttribute(SESSION_PRM_LOGINNAME_FOR_INVALID_LOGIN,username);
							}*/
							onLoginFailed(iwc, canLogin, username);
						}
					}
				} else if (isTryAgainAction(iwc)) {
					//internalSetState(iwc, "loggedoff");
					//internalSetState(iwc, STATE_LOGGED_OUT);
					internalSetState(iwc, LoginState.LoggedOut);
				}

			}
		} catch (Exception ex) {
			try {
				logOut(iwc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ex.printStackTrace(System.err);
			//throw (IdegaWebException)ex.fillInStackTrace();
		}
		return true;
	}
	
	/**
	 * 
	 * @param iwc
	 * @return Returns null if no basic authentication request was maid.  Login has index = 0 and password = 1.
	 */
	public String[] getLoginNameAndPasswordFromBasicAuthenticationRequest(IWContext iwc){
		String sAuthorizationHeader = iwc.getAuthorizationHeader();
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
	
	public String getLoginNameFromBasicAuthenticationRequest(IWContext iwc){
		String sAuthorizationHeader = iwc.getAuthorizationHeader();
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
	
	public String getPasswordFromBasicAuthenticationRequest(IWContext iwc){
		String sAuthorizationHeader = iwc.getAuthorizationHeader();
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
	public boolean authenticateBasicAuthenticationRequest(IWContext iwc) {
		String sAuthorizationHeader = iwc.getAuthorizationHeader();
	    try {
	    		if(sAuthorizationHeader != null) {
	    			
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
					canLogin = verifyPasswordAndLogin(iwc, username, password);
					if (canLogin.equals(LoginState.LoggedOn)) {
						onLoginSuccessful(iwc);
						return true;
					} else {
						onLoginFailed(iwc, canLogin, username);
						return false;
					}
					
				}
	    		}
    		} catch (Exception ex) {
			try {
				logOut(iwc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ex.printStackTrace(System.err);
			//throw (IdegaWebException)ex.fillInStackTrace();
		}
		return false;
	}
	
	public void callForBasicAuthentication(IWContext iwc, String message) throws IOException{
		iwc.getResponse().addHeader("WWW-Authenticate","Basic realm=\"" + "iw_login" + "\"");
		if(message!=null){
			iwc.getResponse().sendError(401,message);
		} else {
			iwc.getResponse().sendError(401);
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
		}
		/*Object obj = iwc.getSessionAttribute(UserAttributeParameter);
		
		if (obj != null){
		
		  return (User)obj;
		
		}else{
		
		  throw new NotLoggedOnException();
		
		}
		
		*/ catch (RemoteException e) {

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
		Collection logins = ((LoginTableHome)IDOLookup.getHome(LoginTable.class)).findLoginsForUser(user);
		if(!logins.isEmpty()) {
			LoginTable loginTable = (LoginTable)logins.iterator().next();
		
			storeUserAndGroupInformationInSession(iwc, user);
			
			int loginTableId = loginTable.getID();
			int loginRecordId = LoginDBHandler.recordLogin(loginTableId, iwc.getRemoteIpAddress());
			storeLoggedOnInfoInSession(iwc, loginTableId, loginTable.getUserLogin(), user, loginRecordId, loginTable.getLoginType());
			return true;
		}
		return false;
	}	
	protected boolean logIn(IWContext iwc, LoginTable loginTable) throws Exception {
		//New user system
		com.idega.core.user.data.UserHome uHome = (com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHome(User.class);
		User user = uHome.findByPrimaryKey(loginTable.getUserId());
		//New user system end
		
		//Old user system
//		User user = ((com.idega.core.user.data.UserHome) com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(loginTable.getUserId());
		//Old user system end

		storeUserAndGroupInformationInSession(iwc, user);
		
		int loginTableId = loginTable.getID();
		int loginRecordId = LoginDBHandler.recordLogin(loginTableId, iwc.getRemoteIpAddress());
		storeLoggedOnInfoInSession(iwc, loginTableId, loginTable.getUserLogin(), user, loginRecordId, loginTable.getLoginType());
		return true;
	}

	protected void storeUserAndGroupInformationInSession(IWContext iwc, User user) throws Exception {
		List groups = null;
		if(isUsingOldUserSystem()){
			//Old user system
			//iwc.setSessionAttribute(LoginAttributeParameter, new Hashtable());
			
			
			//LoginBusinessBean.setUser(iwc, user);
		    getLoginSession(iwc).setUser(user);
			groups = UserBusiness.getUserGroups(user);
			//Old user system end
		}
		else{
			//New user system
			//iwc.setSessionAttribute(LoginAttributeParameter, new Hashtable());
			//LoginBusinessBean.setUser(iwc, user);
		    getLoginSession(iwc).setUser(user);
			com.idega.user.business.UserBusiness userbusiness = (com.idega.user.business.UserBusiness)com.idega.business.IBOLookup.getServiceInstance(iwc, com.idega.user.business.UserBusiness.class);
			com.idega.user.data.User newUser = com.idega.user.util.Converter.convertToNewUser(user);
			Collection userGroups = userbusiness.getUserGroups(newUser);
			if(userGroups!=null)
			    groups = ListUtil.convertCollectionToList(userGroups);
			//New user system end
		}

		if (groups != null) {
			//LoginBusinessBean.setPermissionGroups(iwc, groups);
		    getLoginSession(iwc).setPermissionGroups(groups);
		}
		int userGroupId = user.getGroupID();
		if (userGroupId != -1) {
			//LoginBusinessBean.setUserRepresentativeGroup(iwc, ((com.idega.core.user.data.UserGroupRepresentativeHome)com.idega.data.IDOLookup.getHomeLegacy(UserGroupRepresentative.class)).findByPrimaryKeyLegacy(userGroupId));
		    getLoginSession(iwc).setRepresentativeGroup(((com.idega.core.user.data.UserGroupRepresentativeHome)com.idega.data.IDOLookup.getHomeLegacy(UserGroupRepresentative.class)).findByPrimaryKeyLegacy(userGroupId));
		}
		if (user.getPrimaryGroupID() != -1) {
		    GenericGroup primaryGroup = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHome(GenericGroup.class)).findByPrimaryKey(new Integer(user.getPrimaryGroupID()));
			//LoginBusinessBean.setPrimaryGroup(iwc, primaryGroup);
			getLoginSession(iwc).setPrimaryGroup(primaryGroup);
		}

		UserProperties properties = new UserProperties(iwc.getIWMainApplication(), user.getID());
		//setLoginAttribute(USER_PROPERTY_PARAMETER, properties, iwc);
		getLoginSession(iwc).setUserProperties(properties);
		
	}

	/**
	 * @return
	 */
	private boolean isUsingOldUserSystem()
	{
		return this.USING_OLD_USER_SYSTEM;
	}
	protected void storeLoggedOnInfoInSession(IWContext iwc, int loginTableId, String login, User user, int loginRecordId, String loginType) throws NotLoggedOnException, RemoteException {
		LoggedOnInfo lInfo = createLoggedOnInfo(iwc);
		lInfo.setLoginTableId(loginTableId);
		lInfo.setLogin(login);
		//lInfo.setSession(iwc.getSession());
		lInfo.setTimeOfLogon(IWTimestamp.RightNow());
		lInfo.setUser(user);
		lInfo.setLoginRecordId(loginRecordId);
		if (loginType != null && !loginType.equals("")) {
			lInfo.setLoginType(loginType);
		}
		
		lInfo.setUserRoles(iwc.getAccessController().getAllRolesForCurrentUser(iwc));
		
		Map m = getLoggedOnInfoMap(iwc);
		m.put(lInfo.getLogin(),lInfo);
		
		//getLoggedOnInfoList(iwc).add(lInfo);
		setLoggedOnInfo(lInfo, iwc);
		
	}

	private LoginState verifyPasswordAndLogin(IWContext iwc, String login, String password) throws Exception {
		LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), login);
		if (login_table == null) {
			//return STATE_NO_USER;
		    return LoginState.NoUser;
		}
		if (login_table.length > 0) {
			LoginTable loginTable = login_table[0];
			User user = loginTable.getUser();
			boolean isAdmin = user.equals(iwc.getAccessController().getAdministratorUser());
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
					if (logIn(iwc, loginTable)) {
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
					String maxStr = iwc.getIWMainApplication().getBundle("com.idega.core").getProperty("max_failed_login_attempts");
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
	
	public static void resetPassword(String login, String newPassword, boolean changeNextTime) throws Exception {
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
	
	public static boolean verifyPassword(User user, String login, String password) throws IOException, SQLException {
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
		//if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
	    if (getLoginSession(iwc) != null) {
			// this.getLoggedOnInfoList(iwc).remove(this.getLoggedOnInfo(iwc));

			LoggedOnInfo info = getLoggedOnInfo(iwc);
			if(info!=null){
				Map lm = getLoggedOnInfoMap(iwc);
				lm.remove(info.getLogin());
			}

			UserProperties properties = getUserProperties(iwc);
			if (properties != null) {
				properties.store();
			}

			//iwc.removeSessionAttribute(LoginAttributeParameter);
			removeLoginSession(iwc);
		}

		HttpSession session = iwc.getSession();
		session.invalidate();
	}

	/**
	 * The key is the login name and the value is com.idega.core.accesscontrol.business.LoggedOnInfo
	 * @return Returns empty Map if no one is logged on
	 */
	public static Map getLoggedOnInfoMap(IWContext iwc) {
		Map loggedOnMap = (Map)iwc.getApplicationAttribute(_APPADDRESS_LOGGED_ON_LIST);
		if (loggedOnMap == null) {
			loggedOnMap = new TreeMap();
			iwc.setApplicationAttribute(_APPADDRESS_LOGGED_ON_LIST, loggedOnMap);
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
	 * The key is the login name and the value is com.idega.core.accesscontrol.business.LoggedOnInfo
	 * @param session
	 * @return
	 */

	public static Map getLoggedOnInfoMap(HttpSession session) {
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
	}

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

	public static void setLoggedOnInfo(LoggedOnInfo lInfo, IWContext iwc) throws NotLoggedOnException, RemoteException {
        // Not stored as LoginAttribute because it is HttpSessionBindingListener
		//setLoginAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, lInfo, iwc);
		if(isLoggedOn(iwc)){
		    
			//iwc.setSessionAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, lInfo);
		    getLoginSession(iwc).setLoggedOnInfo(lInfo);
		} else {
			throw new NotLoggedOnException();
		}
	}
	public static LoginContext changeUserPassword(User user, String password) throws Exception {
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

	public static LoginContext createNewUser(String fullName, String email, String preferredUserName, String preferredPassword) {
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

	// added for cookie login maybe unsafe ( Aron )
	public boolean logInUnVerified(IWContext iwc, String login) throws Exception {
		boolean returner = false;
		LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(), login);
		if (login_table != null && login_table.length > 0) {
			LoginTable lTable = login_table[0];
			if (lTable != null) {
				returner = logIn(iwc, login_table[0]);
				if (returner)
					onLoginSuccessful(iwc);
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

	public boolean retrieveLoginInformation(IWContext iwc) throws NotLoggedOnException, RemoteException {

		//logout
		
	    //if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
	    
	    if(getLoginSession(iwc)!=null){
			Map m = getLoggedOnInfoMap(iwc);
			LoggedOnInfo _logOnInfo = (LoggedOnInfo)m.remove(getLoggedOnInfo(iwc).getLogin());
			if ( _logOnInfo != null ) {
				LoginDBHandler.recordLogout(_logOnInfo.getLoginRecordId());
			}
		}

		//login
		//Object obj = iwc.getSessionAttribute(prmReservedLoginSessionAttribute);
		//Object obj = iwc.getSessionAttribute(prmReservedLoginSessionAttribute);
	    getLoginSession(iwc).retrieve();
		//if (obj != null) {
	    if(getLoginSession(iwc).getUser()!=null){
	        
			//iwc.setSessionAttribute(LoginAttributeParameter, obj);
			return true;
		} else {
			return false;
		}

	}

	public void reserveLoginInformation(IWContext iwc) throws RemoteException {
		
	    //if (iwc.getSessionAttribute(LoginAttributeParameter) != null) {
	    if(getLoginSession(iwc)!=null){
			// this.getLoggedOnInfoList(iwc).remove(this.getLoggedOnInfo(iwc));

			//UserProperties properties = (UserProperties)getLoginAttribute(USER_PROPERTY_PARAMETER, iwc);
		    UserProperties properties = getLoginSession(iwc).getUserProperties();
			if (properties != null)
				properties.store();

			//iwc.setSessionAttribute(prmReservedLoginSessionAttribute, iwc.getSessionAttribute(LoginAttributeParameter));
			//iwc.setSessionAttribute(prmReservedLoginSessionAttribute,getLoginSession(iwc));
			
			getLoginSession(iwc).reserve();
			
                //logout
                //iwc.removeSessionAttribute(LoginAttributeParameter);
                //removeLoginSession(iwc);
           
		}
	}

	public void logOutAsAnotherUser(IWContext iwc) throws NotLoggedOnException, RemoteException {
		LoggedOnInfo info = this.getLoggedOnInfo(iwc);
		int rec = info.getLoginRecordId();
		retrieveLoginInformation(iwc);
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
	    return logInAsAnotherUser(iwc,user,true);
	}

	/**
	 * Use this method if the one calling this method is logged in, else use #logIn(IWContext,User)
	 * @param iwc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private boolean logInAsAnotherUser(IWContext iwc, User user,boolean reserveCurrentUser) throws Exception {

		if (isLoggedOn(iwc)) {
		    LoggedOnInfo info = this.getLoggedOnInfo(iwc);
			if (iwc.getUser().equals(user)) {
				return true;
			//} else if(LOGINTYPE_AS_ANOTHER_USER.equals(info.getLoginType())){
			} else if( getLoginSession(iwc).isReserved()){
				System.out.println("trying to log in as another user faild: log out of current \"other user\"");
				return false;
			}
			if(reserveCurrentUser)
			    reserveLoginInformation(iwc);
			storeUserAndGroupInformationInSession(iwc, user);
			
			int loginRecordId = LoginDBHandler.recordLogin(info.getLoginTableId(), iwc.getRemoteIpAddress(), user.getID());
			storeLoggedOnInfoInSession(iwc, info.getLoginTableId(), info.getLogin(), user, loginRecordId, LOGINTYPE_AS_ANOTHER_USER);
			onLoginSuccessful(iwc);
			
			return true;
		}

		return false;
	}

	public boolean logInByPersonalID(IWContext iwc, String personalID) throws Exception {
		boolean returner = false;
		try {
			com.idega.user.data.User user = getUserBusiness(iwc).getUser(personalID);
			LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getColumnNameUserID(), user.getPrimaryKey().toString());

			LoginTable lTable = this.chooseLoginRecord(iwc, login_table, user);
			if (lTable != null) {
				returner = logIn(iwc, lTable);
				if (returner)
					onLoginSuccessful(iwc);
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
	public LoginTable chooseLoginRecord(IWContext iwc, LoginTable[] loginRecords, User user) throws Exception {
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

	public LoggedOnInfo createLoggedOnInfo(IWContext iwc) {
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
	
	public static LoginSession getLoginSession(IWUserContext iwc) throws RemoteException {
        return (LoginSession) IBOLookup.getSessionInstance(iwc, LoginSession.class);
	}
	
	private static void removeLoginSession(IWUserContext iwc) throws RemoteException,RemoveException {
	   IBOLookup.removeSessionInstance(iwc,LoginSession.class);
	}

}
