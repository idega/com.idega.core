/*
 * $Id: BaseFilter.java,v 1.14 2005/12/07 15:37:50 tryggvil Exp $
 * Created on 7.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import com.idega.core.builder.data.ICDomain;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.MutableClass;
import com.idega.util.RequestUtil;



/**
 * <p>
 *  Class that holds basic functionality used by many filters.<br>
 * </p>
 *  Last modified: $Date: 2005/12/07 15:37:50 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.14 $
 */
public abstract class BaseFilter implements Filter, MutableClass {
	
	private static final boolean DEFAULT_VALUE_INITIALIZED_DOMAIN = false;
	private static boolean hasInitializedDefaultDomain = DEFAULT_VALUE_INITIALIZED_DOMAIN;

	public static void unload()	{
		hasInitializedDefaultDomain = DEFAULT_VALUE_INITIALIZED_DOMAIN; 
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
	
	static final String SLASH = "/";

	protected String getNewLoginUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(NEW_IDEGAWEB_LOGIN);
		//return NEW_IDEGAWEB_LOGIN;
	}
	
	protected String getNewLoginUri(HttpServletRequest request,String uriToRedirectTo){
		IWMainApplication iwma = getIWMainApplication(request);
		String baseUri = iwma.getTranslatedURIWithContext(NEW_IDEGAWEB_LOGIN);
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


	/**
	 * This may be called from several filter subclasses. This should ideally be called by the first filter in the chain.
	 * @param iwc
	 */
	protected void initializeDefaultDomain(HttpServletRequest request ){
		if (!hasInitializedDefaultDomain()) {

			String contextPath = request.getContextPath();
			String serverProtocol = request.getScheme();
			ICDomain domain = getIWMainApplication(request).getIWApplicationContext().getDomain();
	    		String setServerName = domain.getServerName();
	    		String setUrl = domain.getURL();
	    		String setContextPath = domain.getServerContextPath();
	    		int setPort = domain.getServerPort();
	    		String setProtocol = domain.getServerProtocol();
	    		if(setServerName==null){
	    			String newServerName = request.getServerName();
	    			domain.setServerName(newServerName);
	    		}
	    		if(setUrl==null){
	    			String newServerURL = RequestUtil.getServerURL(request);
	    			domain.setURL(newServerURL);
	    		}
	    		if(setContextPath==null){
	    			
	    	        if (contextPath != null) {
	    	            if (!contextPath.startsWith(SLASH)) {
	    	            	contextPath = SLASH + contextPath;
	    	            }
	    	        } else {
	    	        		contextPath = SLASH;
	    	        }
	    			
	    			domain.setServerContextPath(contextPath);
	    		}
	    		if(setPort==-1){
	    			int port = request.getServerPort();
	    			if(port!=80){
	    				domain.setServerPort(port);
	    			}
	    		}
	    		if(setProtocol==null){
	    			domain.setServerProtocol(serverProtocol);
	    		}
			
			//getIWMainApplication(request).setApplicationContextURI(contextPath);
			hasInitializedDefaultDomain=true;
		}
	}
	
	private boolean hasInitializedDefaultDomain(){
		return hasInitializedDefaultDomain;
	}
	
	protected IWMainApplication getIWMainApplication(HttpServletRequest request) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		return iwma;
	}
}
