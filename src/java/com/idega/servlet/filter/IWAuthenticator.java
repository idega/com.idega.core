/*
 * $Id: IWAuthenticator.java,v 1.32 2008/05/30 14:16:03 civilis Exp $ Created on 31.7.2004
 * in project com.idega.core
 * 
 * Copyright (C) 2004-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.AuthenticationBusiness;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.business.ServletFilterChainInterruptException;
import com.idega.core.accesscontrol.jaas.IWCallbackHandler;
import com.idega.core.accesscontrol.jaas.IWJAASAuthenticationRequestWrapper;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.repository.data.ImplementorRepository;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.CypherText;
import com.idega.util.RequestUtil;

/**
 * <p>
 * This servletFilter is by default mapped early in the filter chain in idegaWeb and 
 * calls the idegaWeb Accesscontrol system to log the user in to the idegaWeb User system.<br/>
 * When the user has a "remember me" cookie set then this filter reads that and
 * logs the user into the system.
 * </p>
 * Last modified: $Date: 2008/05/30 14:16:03 $ by $Author: civilis $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.32 $
 */
public class IWAuthenticator extends BaseFilter {

	/**
	 * This parameter can be set
	 */
	public static final String PARAMETER_REDIRECT_USER_TO_PRIMARY_GROUP_HOME_PAGE = "logon_redirect_user";
	/**
	 * This parameter can be set to forward to a certain page when logging in (and it is succesful)
	 */
	public static final String PARAMETER_REDIRECT_URI_ONLOGON = "logon_redirect_uri";
	private static final String PERSONAL_ID_PATTERN="\\$\\{currentUser.personalID\\}";
	private static final String TICKET_PATTERN="\\$\\{currentUser.ticket\\}";
	/**
	 * This parameter can be set to forward to a certain page when logging off (and it is succesful)
	 */
	public static final String PARAMETER_REDIRECT_URI_ONLOGOFF = "logoff_redirect_uri";
	public static final String COOKIE_NAME = "iwrbusid";
	//public String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";
	public static final String PARAMETER_ALLOWS_COOKIE_LOGIN = "icusallows";
	
	// following string are used as keys in the sharedState map used by LoginModules
	public static final String SESSION_KEY = "session"; // a HttpSession
	public static final String REQUEST_KEY = "request"; // a HttpRequest
	
	private static Logger log = Logger.getLogger(IWAuthenticator.class
			.getName());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		HttpSession session = request.getSession();
		
//		Enumeration headerNames = request.getHeaderNames();
//		System.out.println("------------HEADER BEGINS-------------");
//		while (headerNames.hasMoreElements()) {
//		String headerName = (String) headerNames.nextElement();
//		System.out.println("\t["+headerName+"]: "+request.getHeader(headerName));
//		}
//		System.out.println("------------HEADER ENDS-------------");
		
//		Enumeration parameterNames = request.getParameterNames();
//		System.out.println("------------PARAMETERS BEGINS-------------");
//		while (parameterNames.hasMoreElements()) {
//		String parameterName = (String) parameterNames.nextElement();
//		System.out.println("\t["+parameterNames+"]: "+request.getParameter(parameterName));
//		}
//		System.out.println("------------PARAMETERS ENDS-------------");
		User lastLoggedOnAsUser = null;
		LoginBusinessBean loginBusiness = getLoginBusiness(request);
		boolean isLoggedOn = loginBusiness.isLoggedOn(request);
		
		if(isLoggedOn){
			//lastLoggedOnAsUser = iwc.getCurrentUser();
			lastLoggedOnAsUser = loginBusiness.getCurrentUser(session);
		}
		
		if(useBasicAuthenticationMethod(request)){
			if(!isLoggedOn){
				if (!loginBusiness.authenticateBasicAuthenticationRequest(request)) {	
					loginBusiness.callForBasicAuthentication(request,response,null);
					return;
				}
			}
		} else {
			if(!isLoggedOn){
				loginBusiness.authenticateBasicAuthenticationRequest(request);
			}
			//initializeDefaultDomain(request);
			tryRegularLogin(request);
			tryCookieLogin(request,response,loginBusiness);
			//addCookie(request,response,loginBusiness);
		}
		
		processJAASLogin(request);
		
		boolean didRedirect = processRedirects(request, response, session, loginBusiness);
		if(didRedirect){
			return;
		}
		
		if(!isLoggedOn)
			isLoggedOn = loginBusiness.isLoggedOn(request);
		
		if(lastLoggedOnAsUser == null && isLoggedOn)
			lastLoggedOnAsUser = loginBusiness.getCurrentUser(session);
		
		boolean didInterrupt = processAuthenticationListeners(request, response, session, lastLoggedOnAsUser, loginBusiness, isLoggedOn);
		if(didInterrupt){
			return;
		}
		
		chain.doFilter(new IWJAASAuthenticationRequestWrapper(request), response);

	}

	/**
	 * <p>
	 * Processes the possibly attatched AuthenticationListeners. Returns true if one of the
	 * authenticationfilters interrupts the filter chain.
	 * </p>
	 * @param request
	 * @param response
	 * @param session
	 * @param lastLoggedOnAsUser
	 * @param loginBusiness
	 * @param isLoggedOn
	 * @throws RemoteException
	 */
	protected boolean processAuthenticationListeners(HttpServletRequest request, HttpServletResponse response, HttpSession session, User lastLoggedOnAsUser, LoginBusinessBean loginBusiness, boolean isLoggedOn) throws RemoteException {
		//TODO support also on basic authentication (e.g. webdav) or is that not necessery?
		//TODO grab an interrupt exeption and just return; (could be necessery for the methods to be able to use response.sendRedirect)
		if( loginBusiness.isLogOnAction(request) && isLoggedOn){
			try {
				AuthenticationBusiness authenticationBusiness = getAuthenticationBusiness(request);
				User currentUser = loginBusiness.getCurrentUser(session);
				//TODO: Remove IWContext
				IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
				authenticationBusiness.callOnLogonMethodInAllAuthenticationListeners(iwc, currentUser);
			}
			catch (ServletFilterChainInterruptException e) {
				//this is normal behaviour if e.g. the listener issues a response.sendRedirect(...)
				System.out.println("[IWAuthenticator] - Filter chain interrupted. The reason was: "+e.getMessage());
				return true;
			}
		}
//		else if(loginBusiness.isLogOffAction(request) && !isLoggedOn && lastLoggedOnAsUser!=null){
		else if(loginBusiness.isLogOffAction(request) && lastLoggedOnAsUser!=null){
			try {
				AuthenticationBusiness authenticationBusiness = getAuthenticationBusiness(request);
				//TODO: Remove IWContext
				IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
				authenticationBusiness.callOnLogoffMethodInAllAuthenticationListeners(iwc, lastLoggedOnAsUser);
			}
			catch (ServletFilterChainInterruptException e) {
				//this is normal behaviour if e.g. the listener issues a response.sendRedirect(...)
				System.out.println("[IWAuthenticator] - Filter chain interrupted. The reason was: "+e.getMessage());
				return true;
			}
		}
		return false;
	}
	
	protected boolean processRedirectsToUserHome(HttpServletRequest request, HttpServletResponse response, HttpSession session, LoginBusinessBean loginBusiness, boolean isLoggedOn) throws IOException, RemoteException {
		if(isLoggedOn) {
			User user = loginBusiness.getCurrentUser(session);
			int homePageID = user.getHomePageID();
			if (homePageID > 0) {
				IWApplicationContext iwac = getIWMainApplication(request).getIWApplicationContext();
				response.sendRedirect(getBuilderService(iwac).getPageURI(homePageID));
				return true;
			}
			
			Group prmg = user.getPrimaryGroup(); 
			if (prmg != null) {
				homePageID = prmg.getHomePageID();
				if (homePageID > 0) {
					IWApplicationContext iwac = getIWMainApplication(request).getIWApplicationContext();
					response.sendRedirect(getBuilderService(iwac).getPageURI(homePageID));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Processes possible redirects that might happen at login time.
	 * Returns true if a redirect did happen.
	 * </p>
	 * @param request
	 * @param response
	 * @param session
	 * @param loginBusiness
	 * @return
	 * @throws IOException
	 * @throws RemoteException
	 */
	protected boolean processRedirects(HttpServletRequest request, HttpServletResponse response, HttpSession session, LoginBusinessBean loginBusiness) throws IOException, RemoteException {
		//We have to call this method again because the user might just have logged on before:
		boolean isLoggedOn = loginBusiness.isLoggedOn(request);
		if (RequestUtil.isParameterSet(request,PARAMETER_REDIRECT_USER_TO_PRIMARY_GROUP_HOME_PAGE)){
			processRedirectsToUserHome(request, response, session, loginBusiness, isLoggedOn);
		}
		if (RequestUtil.isParameterSet(request,PARAMETER_REDIRECT_URI_ONLOGON) && isLoggedOn) {
			String uri = getLoginRedirectUriOnLogonParsedWithVariables(request);
			if (uri!=null) {
				response.sendRedirect(uri);
				return true;
			}
		}
		if (RequestUtil.isParameterSet(request,PARAMETER_REDIRECT_URI_ONLOGOFF) && !isLoggedOn) {
			String uri = request.getParameter(PARAMETER_REDIRECT_URI_ONLOGOFF);
			if (uri!=null) {
				response.sendRedirect(uri);
				return true;
			}
		}
		return false;
	}
	
	
	

	/**
	 * <p>
	 * Parses the set RedirectOnLogon URI and replaces with user variables such as the
	 * variables ${currentUser.personalID}, ${currentUser.ticket} in the URL String.
	 * </p>
	 * @param request
	 * @return
	 */
	public static String getLoginRedirectUriOnLogonParsedWithVariables(HttpServletRequest request) {
		String uri = request.getParameter(PARAMETER_REDIRECT_URI_ONLOGON);
		
		if(uri != null) {
			try {
				uri = URLDecoder.decode(uri, CoreConstants.ENCODING_UTF8);
			} catch (UnsupportedEncodingException e) {
				// TODO: what tha heck container is that?
				log.log(Level.WARNING, "Exception while decoding redirect uri parameter: "+PARAMETER_REDIRECT_URI_ONLOGON+" by using "+CoreConstants.ENCODING_UTF8+" encoding", e);
			}
		}
		
		uri = getUriParsedWithVariables(request,uri);
		return uri;
	}
	
	/**
	 * <p>
	 * Parses the set RedirectOnLogon URI and replaces with user variables such as the
	 * variables ${currentUser.personalID}, ${currentUser.ticket} in the URL String.
	 * </p>
	 * @param request
	 * @return
	 */
	public static String getUriParsedWithVariables(HttpServletRequest request,String uri) {
		
		LoginBusinessBean loginBean = LoginBusinessBean.getLoginBusinessBean(request);
		LoggedOnInfo info = loginBean.getLoggedOnInfo(request.getSession());
		
		User user = loginBean.getCurrentUser(request.getSession());
		if(user!=null){
			String personalId=user.getPersonalID();
			uri = uri.replaceAll(PERSONAL_ID_PATTERN,personalId);
		}
		String ticket = info.getTicket();
		if(ticket!=null){
			uri = uri.replaceAll(TICKET_PATTERN,ticket);
		}
		return uri;
	}

	/**
	 * @param iwc
	 * @return
	 */
	private boolean useBasicAuthenticationMethod(HttpServletRequest request) {
		return IWContext.isWebDavClient(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
	
	public void tryRegularLogin(HttpServletRequest request){
		try {
			getLoginBusiness(request).processRequest(request);
		} catch (IWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Authenticates in/out with reading existing cookie or creating it on login or removing it on logout
	 * </p>
	 * @param request
	 * @param response
	 * @param loginBusiness
	 */
	public void tryCookieLogin(HttpServletRequest request,HttpServletResponse response,LoginBusinessBean loginBusiness) {
		if (!loginBusiness.isLoggedOn(request)) {
			//No user is logged in, try to authenticate with the cookie:
			Cookie userIDCookie = getCookie(request);
			//System.err.println("no user is logged on");
			if (userIDCookie != null) {
				//System.err.println("found the cookie");

				if(loginBusiness.isLogOffAction(request)) {
					//A cookie is found
					//delete it:
					userIDCookie.setMaxAge(0);
					response.addCookie(userIDCookie);
				}
				else{
					String cypheredLoginName = userIDCookie.getValue();
					IWApplicationContext iwac = getIWMainApplication(request).getIWApplicationContext();
					String loginName = deCypherUserLogin(iwac, cypheredLoginName);
					try {
						loginBusiness.logInUnVerified(request, loginName);
					} catch (Exception ex) {
						//throw new IWException("Cookie login failed :
						// "+ex.getMessage());
						log.warning("Cookie login failed for loginName: "+loginName+" :" + ex.getMessage());
					}
				}
			} else {
			}
		}
		else{
			//A user is logged in
			if (loginBusiness.isLogOffAction(request)) {
				Cookie userIDCookie = getCookie(request);
				if(userIDCookie != null){
					//A cookie is found, try to remove it on logout
					userIDCookie.setMaxAge(0);
					response.addCookie(userIDCookie);
				}
			}
			else if (RequestUtil.isParameterSet(request,PARAMETER_ALLOWS_COOKIE_LOGIN)) {
				Cookie userIDCookie = getCookie(request);
				HttpSession session = request.getSession();
				String login = loginBusiness.getLoggedOnInfo(session).getLogin();
				IWMainApplication iwma = getIWMainApplication(request);
				IWApplicationContext iwac = iwma.getIWApplicationContext();
				String cypheredLogin = cypherUserLogin(iwac, login);
				int maxAge = 60 * 60 * 24 * 30;
				if (userIDCookie == null) {
					//No cookie exists on logon, try to add it:
					userIDCookie = new Cookie(COOKIE_NAME,cypheredLogin);
					userIDCookie.setMaxAge(maxAge);
					response.addCookie(userIDCookie);
				}
				else{
					userIDCookie.setValue(login);
					userIDCookie.setMaxAge(maxAge);
				}
			}
		}
	}

	private Cookie getCookie(HttpServletRequest request) {
		Cookie userIDCookie = RequestUtil.getCookie(request,COOKIE_NAME);
		return userIDCookie;
	}

	public String getCypherKey(IWApplicationContext iwc) {
		CypherText cyph = new CypherText();
		IWMainApplicationSettings settings = iwc.getApplicationSettings();
		String cypherKey = settings.getProperty("cypherKey");
		if ((cypherKey == null) || (cypherKey.equalsIgnoreCase(""))) {
			cypherKey = cyph.getKey(100);
			settings.setProperty("cypherKey", cypherKey);
		}
		return (cypherKey);
	}

	protected String cypherUserLogin(IWApplicationContext iwc, String userLogin) {
		String key = getCypherKey(iwc);
		String cypheredId = new CypherText().doCyper(userLogin, key);
		log.fine("Cyphered " + userLogin + "to " + cypheredId);
		return cypheredId;
	}

	protected String deCypherUserLogin(IWApplicationContext iwc,
			String cypheredLogin) {
		String key = getCypherKey(iwc);
		return new CypherText().doDeCypher(cypheredLogin, key);
	}
	
	protected AuthenticationBusiness getAuthenticationBusiness(HttpServletRequest request){
		AuthenticationBusiness authenticationBusiness = null;
		try {
			IWApplicationContext iwac = getIWMainApplication(request).getIWApplicationContext();
			authenticationBusiness = (AuthenticationBusiness) IBOLookup.getServiceInstance(iwac, AuthenticationBusiness.class);
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		return authenticationBusiness;
	}
	
	protected BuilderService getBuilderService(IWApplicationContext iwac) throws RemoteException {
		return BuilderServiceFactory.getBuilderService(iwac);
	}

	protected void processJAASLogin(HttpServletRequest request)  {
		List loginModules = ImplementorRepository.getInstance().newInstances(LoginModule.class, this.getClass());
		// just a shortcut 
		if (loginModules.isEmpty()) {
			return;
		}
		CallbackHandler callbackHandler = new IWCallbackHandler(request);
		Map sharedState = new HashMap(3);
		HttpSession session = request.getSession();
		sharedState.put(IWAuthenticator.REQUEST_KEY, request);
		sharedState.put(IWAuthenticator.SESSION_KEY, session);
		Iterator iteratorFirst = loginModules.iterator();
		while (iteratorFirst.hasNext()) {
			LoginModule loginModule = (LoginModule) iteratorFirst.next();
			try {
				loginModule.initialize(null, callbackHandler, sharedState, null);
				loginModule.login();
			}
			catch (LoginException e) {
				e.printStackTrace();
			}
		}
		Iterator iteratorSecond = loginModules.iterator();
		while (iteratorSecond.hasNext()) {
			LoginModule loginModule = (LoginModule) iteratorSecond.next();
			try {
				loginModule.commit();
			}
			catch (LoginException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

//		http://formbuilder.idega.is/login/?logon_redirect_uri=/workspace/
		try {
			
			String encoded = URLEncoder.encode("http://formbuilder.idega.is/login/?logon_redirect_uri=/workspace/", CoreConstants.ENCODING_UTF8);
			System.out.println("encooded: "+encoded);
			String decoded = URLDecoder.decode(encoded, CoreConstants.ENCODING_UTF8);
			System.out.println("decoded: "+decoded);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
