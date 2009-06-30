/*
 * $Id: BaseFilter.java,v 1.21 2008/02/13 14:07:32 valdas Exp $
 * Created on 7.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.builder.data.CachedDomain;
import com.idega.core.builder.data.ICDomain;
import com.idega.idegaweb.IWApplicationContextFactory;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.repository.data.MutableClass;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.RequestUtil;



/**
 * <p>
 *  Class that holds basic functionality used by many filters.<br>
 * </p>
 *  Last modified: $Date: 2008/02/13 14:07:32 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.21 $
 */
public abstract class BaseFilter implements Filter, MutableClass {
	
	//private static final boolean DEFAULT_VALUE_INITIALIZED_DOMAIN = false;
	//private static boolean hasInitializedDefaultDomain = DEFAULT_VALUE_INITIALIZED_DOMAIN;

	public static void unload()	{
		//hasInitializedDefaultDomain = DEFAULT_VALUE_INITIALIZED_DOMAIN; 
	}

	protected static final String OLD_BUILDER_SERVLET_URI = "/servlet/IBMainServlet";
	protected static final String OLD_OBJECT_INSTANCIATOR = "/servlet/ObjectInstanciator";
	protected static final String OLD_BUILDER_INDEX_JSP_URI = "/index.jsp";
	protected static final String OLD_BUILDER_PAGE_PARAMETER = "ib_page";
	protected static final String OLD_IDEGAWEB_LOGIN = "/idegaweb";
	protected static final String OLD_IDEGAWEB_LOGIN_WITHSLASH = "/idegaweb/";
	protected static final String NEW_IDEGAWEB_LOGIN = "/login/";
	protected static final String NEW_IDEGAWEB_LOGIN_MINUSSLASH = "/login";
	protected static final String NEW_WORKSPACE_URI="/workspace/";
	protected static final String NEW_WORKSPACE_URI_MINUSSLASH="/workspace";
	protected static final String SETUP_URI="/setup/";
	protected static final String PAGES_URI="/pages/";
	protected static final String PAGES_URI_MINUSSLASH="/pages";
	protected static final String ENC_PARAMS_PARAM = "encParams";
	protected static final String BUILDER_APPLICATION_URI = NEW_WORKSPACE_URI + CoreConstants.BUILDER_APPLICATION + CoreConstants.SLASH;
	protected static boolean INITIALIZE_CACHED_DOMAIN_ON_NEXT_REQUEST = true;
	
	static final String SLASH = "/";

	protected String getNewLoginUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(NEW_IDEGAWEB_LOGIN);
		//return NEW_IDEGAWEB_LOGIN;
	}
	
	public static void reInitializeCachedDomainOnNextRequest() {
		INITIALIZE_CACHED_DOMAIN_ON_NEXT_REQUEST = true;
	}
	
	protected String getNewLoginUri(HttpServletRequest request,String uriToRedirectTo){
		IWMainApplication iwma = getIWMainApplication(request);
		String baseUri = iwma.getTranslatedURIWithContext(NEW_IDEGAWEB_LOGIN);
		String q_string = null;
		
		if(request.getParameter(ENC_PARAMS_PARAM) != null)
			q_string = request.getQueryString();
		
		if(q_string != null) {
			
			try {
				baseUri = baseUri+"?"+IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON+"="+URLEncoder.encode(
						uriToRedirectTo+"?"+q_string, CoreConstants.ENCODING_UTF8);
			} catch (UnsupportedEncodingException e) {
				baseUri = baseUri+"?"+IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON+"="+uriToRedirectTo;
			}
		} else
			baseUri = baseUri+"?"+IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON+"="+uriToRedirectTo;
		
		return baseUri;
		//return NEW_IDEGAWEB_LOGIN;
	}
	
	protected String getNewWorkspaceUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(NEW_WORKSPACE_URI);
		//return NEW_WORKSPACE_URI;
	}
	
	protected String getSetupUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(SETUP_URI);
		//return NEW_IDEGAWEB_LOGIN;
	}
	
	/**
	 * Gets the pages uri prefixed with context path
	 * @param request
	 * @return
	 */
	protected String getPagesUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(PAGES_URI);
	}
	
	protected String getURIMinusContextPath(HttpServletRequest request) {
		return RequestUtil.getURIMinusContextPath(request);
		
	}

	
	protected IWMainApplication getIWMainApplication(HttpServletRequest request) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		return iwma;
	}
	

	protected LoginBusinessBean getLoginBusiness(HttpServletRequest request){
		return LoginBusinessBean.getLoginBusinessBean(request);
	}
	
	protected ICDomain getDomain(HttpServletRequest request){
		
		String serverName = request.getServerName();
		ICDomain domain = getIWMainApplication(request).getIWApplicationContext().getDomainByServerName(serverName);
		if(domain instanceof CachedDomain){
			CachedDomain cachedDomain = (CachedDomain)domain;
			if(INITIALIZE_CACHED_DOMAIN_ON_NEXT_REQUEST || !cachedDomain.isHasInitializedCachedAttribute()){
				cachedDomain.initializeCachedInfo(request);
				INITIALIZE_CACHED_DOMAIN_ON_NEXT_REQUEST = false;
			}
			
		}
		return domain;
	}
	
	protected CachedDomain getCachedDomain(HttpServletRequest request){
		return (CachedDomain)getDomain(request);
	}
	
	/**
	 * <p>
	 * Detects and sets the IWApplicationContext instance and associates it to the current Thread.<br/>
	 * This should live throughout the request processing (until removeApplicationContext() is called).<br/>
	 * These methods should called by the first ServletFilter in the Chain (which currently iw IWUrlRedirector).
	 * </p>
	 * @param request
	 */
	protected void setApplicationContext(HttpServletRequest request){
		//We call getDomain() to make sure it is initialized:
		getDomain(request);
		IWApplicationContextFactory.setCurrentIWApplicationContext(request);
	}
	/**
	 * <p>
	 * Removed the set IWApplicationContext instance from the current Thread, that was prevously 
	 * allocated by setApplicationContext() method.<br/>
	 * These methods should called by the first ServletFilter in the Chain (which currently iw IWUrlRedirector).
	 * </p>
	 * @param request
	 */
	protected void removeApplicationContext(HttpServletRequest request){
		IWApplicationContextFactory.removeCurrentIWApplicationContext(request);
	}
	
	protected IWContext getIWContext(HttpServletRequest request, HttpServletResponse response) {
		IWContext iwc = CoreUtil.getIWContext();
		
		if (iwc == null) {
			try {
				iwc = new IWContext(request, response, request.getSession().getServletContext());
			} catch(Exception e) {
				Logger.getLogger(BaseFilter.class.getName()).log(Level.SEVERE, "Error creating instance of " + IWContext.class.getSimpleName(), e);
				CoreUtil.sendExceptionNotification(e);
			}
		}
		
		return iwc;
	}
}
