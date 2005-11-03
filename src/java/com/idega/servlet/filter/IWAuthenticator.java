/*
 * $Id: IWAuthenticator.java,v 1.16 2005/11/03 17:36:28 eiki Exp $ Created on 31.7.2004
 * in project com.idega.core
 * 
 * Copyright (C) 2004-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.AuthenticationBusiness;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.jaas.IWJAASAuthenticationRequestWrapper;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.CypherText;

/**
 * <p>
 * This servletFilter is by default mapped early in the filter chain in idegaWeb and 
 * calls the idegaWeb Accesscontrol system to log the user in to the idegaWeb User system.<br/>
 * When the user has a "remember me" cookie set then this filter reads that and
 * logs the user into the system.
 * </p>
 * Last modified: $Date: 2005/11/03 17:36:28 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.16 $
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
	/**
	 * This parameter can be set to forward to a certain page when logging off (and it is succesful)
	 */
	public static final String PARAMETER_REDIRECT_URI_ONLOGOFF = "logoff_redirect_uri";
	
	
	
	private static Logger log = Logger.getLogger(IWAuthenticator.class
			.getName());

	private LoginBusinessBean loginBusiness = new LoginBusinessBean();
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
		//FacesContext fc = FacesContext.getCurrentInstance();
		//IWContext iwc = IWContext.getIWContext(fc);
		IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
		
		authenticationBusiness = getAuthenticationBusiness(iwc);
		
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
		if(iwc.isLoggedOn()){
			lastLoggedOnAsUser = iwc.getCurrentUser();
		}
	
		
		if(useBasicAuthenticationMethod(iwc)){
			if(!iwc.isLoggedOn()){
				if (!getLoginBusiness(iwc).authenticateBasicAuthenticationRequest(iwc)) {	
					getLoginBusiness(iwc).callForBasicAuthentication(iwc,null);
					return;
				}
			}
		} else {
			if(!iwc.isLoggedOn()){
				getLoginBusiness(iwc).authenticateBasicAuthenticationRequest(iwc);
			}
			setApplicationServletContextPath(request);
			
			tryRegularLogin(iwc);
			
			performCookieLogin(iwc);
			addCookie(iwc);
		}
		
		if (iwc.isParameterSet(PARAMETER_REDIRECT_USER_TO_PRIMARY_GROUP_HOME_PAGE) && iwc.isLoggedOn()) {
			Group prmg = iwc.getCurrentUser().getPrimaryGroup(); 
			if (prmg != null) {
				int homePageID = prmg.getHomePageID();
				if (homePageID > 0) {
					response.sendRedirect(getBuilderService(iwc).getPageURI(homePageID));
					return;
				}
			}
		}
		else if (iwc.isParameterSet(PARAMETER_REDIRECT_URI_ONLOGON) && iwc.isLoggedOn()) {
			String uri = iwc.getParameter(PARAMETER_REDIRECT_URI_ONLOGON);
			if (uri!=null) {
				response.sendRedirect(uri);
				return;
			}
		}
		//can be done seperately because it is a logoff action
		if (iwc.isParameterSet(PARAMETER_REDIRECT_URI_ONLOGOFF) && !iwc.isLoggedOn()) {
			String uri = iwc.getParameter(PARAMETER_REDIRECT_URI_ONLOGOFF);
			if (uri!=null) {
				response.sendRedirect(uri);
				return;
			}
		}
		
		//TODO support also on basic authentication (e.g. webdav) or is that not necessery?
		//TODO grab an interrupt exeption and just return; (could be necessery for the methods to be able to use response.sendRedirect)
		if( loginBusiness.isLogOnAction(iwc) && iwc.isLoggedOn()){
			authenticationBusiness.callOnLogonMethodInAllAuthenticationListeners(iwc, iwc.getCurrentUser());
		}
		else if(loginBusiness.isLogOffAction(iwc) && !iwc.isLoggedOn() && lastLoggedOnAsUser!=null){
			authenticationBusiness.callOnLogoffMethodInAllAuthenticationListeners(iwc, lastLoggedOnAsUser);
		}
		
		chain.doFilter(new IWJAASAuthenticationRequestWrapper(iwc), response);
	}

	/**
	 * @param iwc
	 * @return
	 */
	private boolean useBasicAuthenticationMethod(IWContext iwc) {
		return iwc.isWebDavClient();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	public String userIDCookieName = "iwrbusid";

	//public String IW_BUNDLE_IDENTIFIER = "com.idega.block.login";
	public static final String PARAMETER_ALLOWS_COOKIE_LOGIN = "icusallows";
	private AuthenticationBusiness authenticationBusiness;

	

	
	public void tryRegularLogin(IWContext iwc){
		try {
			getLoginBusiness(iwc).actionPerformed(iwc);
		} catch (IWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addCookie(IWContext iwc) {
		Cookie userIDCookie = getCookie(iwc);
		//System.err.println("actionPerformed in LoginCookieListener");
		if (getLoginBusiness(iwc).isLogOffAction(iwc) && userIDCookie != null) {
			userIDCookie.setMaxAge(0);
			iwc.addCookies(userIDCookie);
		}

		else if (iwc.isParameterSet(PARAMETER_ALLOWS_COOKIE_LOGIN)
				&& LoginBusinessBean.isLoggedOn(iwc)) {
			if (userIDCookie == null) {
				//System.err.println("adding cookie");
				String login = getLoginBusiness(iwc).getLoggedOnInfo(iwc)
						.getLogin();
				userIDCookie = new Cookie(userIDCookieName, cypherUserLogin(
						iwc, login));
				userIDCookie.setMaxAge(60 * 60 * 24 * 30);
				iwc.addCookies(userIDCookie);
			}
		}
	}

	public void performCookieLogin(IWContext iwc) {
		Cookie userIDCookie = getCookie(iwc);
		if (!iwc.isLoggedOn()) {
			//System.err.println("no user is logged on");
			if (userIDCookie != null) {
				//System.err.println("found the cookie");
				String cypheredLoginName = userIDCookie.getValue();
				String loginName = deCypherUserLogin(iwc, cypheredLoginName);
				try {
					getLoginBusiness(iwc).logInUnVerified(iwc, loginName);
				} catch (Exception ex) {
					//throw new IWException("Cookie login failed :
					// "+ex.getMessage());
					log.warning("Cookie login failed for loginName: "+loginName+" :" + ex.getMessage());
				}
			} else {//System.err.println("no cookie found");
			}
		}
	}

	private Cookie getCookie(IWContext iwc) {
		Cookie userIDCookie = iwc.getCookie(userIDCookieName);
		return userIDCookie;
	}

	public String getCypherKey(IWApplicationContext iwc) {
		IWMainApplicationSettings settings = iwc.getIWMainApplication()
				.getSettings();
		CypherText cyph = new CypherText();

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

	protected LoginBusinessBean getLoginBusiness(IWContext iwc){
		return loginBusiness;
	}
	
	protected AuthenticationBusiness getAuthenticationBusiness(IWApplicationContext iwac){
		try {
			authenticationBusiness = (AuthenticationBusiness) IBOLookup.getServiceInstance(iwac, AuthenticationBusiness.class);
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		
		return authenticationBusiness;
	}
	
	protected BuilderService getBuilderService(IWContext iwc) throws RemoteException {
		return BuilderServiceFactory.getBuilderService(iwc);
	}
	
}