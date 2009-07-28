/*
 * $Id: RequestUtil.java,v 1.8 2008/12/19 08:54:45 valdas Exp $ Created on
 * 27.1.2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.servlet.filter.IWAuthenticator;

/**
 * 
 * Last modified: $Date: 2008/12/19 08:54:45 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.8 $
 */
public class RequestUtil {

	private static String SLASH = "/";
	private static final String IMAGEBUTTON_XPOS_SUFFIX = ".x";
	private static final String HEADER_USER_AGENT = "User-agent";
	private static final String HEADER_REFERER = "Referer";
	private static final String HEADER_AUTHORIZATION = "Authorization";

	/**
	 * Calls the method HttpServletRequest.getRequestURI() and cuts front of it
	 * the Context path if any.
	 * 
	 * @param request
	 * @return
	 */
	public static String getURIMinusContextPath(HttpServletRequest request) {
		// IWMainApplication iwma = getIWMainApplication(request);
		// String appUri = iwma.getApplicationContextURI();
		String appUri = request.getContextPath();
		String requestUri = request.getRequestURI();
		if (!appUri.endsWith(SLASH)) {
			appUri = appUri + SLASH;
		}
		if (appUri.equals(SLASH)) {
			return requestUri;
		}
		else {
			// Here we set -1 because we want to keep the "/" character in the
			// beginning
			String newUri = requestUri.substring(appUri.length() - 1);
			return newUri;
		}
	}

	/**
	 * Gets a constructed base URL for the server.
	 * 
	 * @return the servername with port and protocol, e.g.
	 *         http://www.idega.com:8080/
	 */
	public static String getServerURL(HttpServletRequest request) {
		StringBuffer buf = new StringBuffer();
		String scheme = request.getScheme();
		buf.append(scheme);
		buf.append("://");
		/*
		 * if(request.isSecure()){ buf.append("https://"); } else{
		 * buf.append("http://"); }
		 */
		buf.append(request.getServerName());
		if (request.getServerPort() == 80) {
			//do not add port to url
		}
		else if (request.getServerPort() == 443) {
			//do not add port to url
		}
		else{
			buf.append(":").append(request.getServerPort());
		}
		buf.append("/");
		return buf.toString();
	}

	/**
	 * <p>
	 * Gets the cookie with the name cookieName from the Request.<br/> Returns
	 * null if no cookie is found with name.
	 * </p>
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			if (cookies.length > 0) {
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals(cookieName)) {
						return cookies[i];
					}
				}
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Checks if the parameter parameterName is set for the Request or is not an empty String.
	 * <br/> This Method also checks the method sent from an submit button of type image where
	 * the parameterName is [parameterName].x
	 * </p>
	 * @param request
	 * @param parameterName
	 * @return
	 */
	public static boolean isParameterSet(HttpServletRequest request, String parameterName) {
		if (parameterName == null) {
			return false;
		}
		boolean theReturn = false;
		String value = request.getParameter(parameterName);
		if (value != null && value.length() > 0) {
			theReturn = true;
		}
		String imageButtonParameter = parameterName + IMAGEBUTTON_XPOS_SUFFIX;
		value = request.getParameter(imageButtonParameter);
		if (value != null && value.length() > 0) {
			theReturn = true;
		}
		return theReturn;
	}

	/**
	 * <p>
	 * Checks if the parameter parameterName is not set for the Request or is an empty String.
	 * <br/> This Method also checks the method sent from an submit button of type image where
	 * the parameterName is [parameterName].x
	 * </p>
	 * @param request
	 * @param parameterName
	 * @return
	 */
	public static boolean isParameterSetAsEmpty(HttpServletRequest request, String parameterName) {
		if (parameterName == null) {
			return false;
		}
		boolean theReturn = false;
		String value = request.getParameter(parameterName);
		if (value != null && value.length() == 0) {
			theReturn = true;
		}
		value = request.getParameter(parameterName + IMAGEBUTTON_XPOS_SUFFIX);
		if (value != null && value.length() == 0) {
			theReturn = true;
		}
		return theReturn;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getUserAgent
	 * </p>
	 * @param request
	 * @return
	 */
	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader(HEADER_USER_AGENT);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getReferer
	 * </p>
	 * @param request
	 * @return
	 */
	public static String getReferer(HttpServletRequest request) {
		return request.getHeader(HEADER_REFERER);
	}

	/**
	 * <p>
	 * This method gets the header value for the attribute "Authorization" which is
	 * used e.g. for getting username and password in BASIC Authorization/Authentication request
	 * </p>
	 * @return Returns the header value for "Authorization" attribute
	 */
	public static String getAuthorizationHeader(HttpServletRequest request) {
		return request.getHeader(HEADER_AUTHORIZATION);
	}
	
	public static String getRedirectUriByApplicationProperty(HttpServletRequest request, int code) {
		String requestedPage = request.getRequestURI();
		if (!requestedPage.startsWith("/pages/")) {
			return null;
		}
		
		IWMainApplicationSettings settings = null;
		try {
			settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (settings == null) {
			return null;
		}
		
		boolean addLoginRedirectParameter = false;
		String redirectUri = null;
		String loginRedirectString = null;
		switch(code) {
			case HttpServletResponse.SC_FORBIDDEN : {
				redirectUri = settings.getProperty(CoreConstants.PAGE_ERROR_403_HANDLER_PORPERTY);
				addLoginRedirectParameter = true;
				String parameters = getParametersStringFromRequest(request);
				loginRedirectString = new StringBuilder().append("?").append(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON).append('=').append(requestedPage).append("&").append(parameters).toString();
				
				break;
			}
			case HttpServletResponse.SC_NOT_FOUND : {
				redirectUri = settings.getProperty(CoreConstants.PAGE_ERROR_404_HANDLER_PORPERTY);
				
				break;
			}
		}
		if (StringUtil.isEmpty(redirectUri)) {
			return null;
		}
		
		
		
		return new StringBuilder(redirectUri).append(addLoginRedirectParameter ? loginRedirectString : CoreConstants.EMPTY).toString();
	}

	/**
	 * 
	 * @param request
	 * @return String with request parameters, e.g. "parameter1=value1&parameter2=value1&parameter2=value2"
	 */
	public static String getParametersStringFromRequest(HttpServletRequest request) {
		return getParametersStringFromRequest(request,null);
	}
	
	/**
	 * 
	 * @param request
	 * @return String with request parameters, e.g. "parameter1=value1&parameter2=value1&parameter2=value2"
	 */
	public static String getParametersStringFromRequest(HttpServletRequest request, List<String> keysToIgnore) {
		StringBuilder parametersString = new StringBuilder();
		Map parameters = request.getParameterMap();

		if (parameters != null && !parameters.isEmpty()) {
			Set<String> parametersSet = parameters.keySet();
			for (Iterator iterator = parametersSet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if(ListUtil.isEmpty(keysToIgnore) || !(keysToIgnore.contains(key)) ){
					String[] values = request.getParameterValues(key);
					if (values != null && values.length > 0) {
						for (int j = 0; j < values.length; j++) {
							parametersString.append('&').append(key).append('=').append(values[j]);
						}
					}
				}
			}
		}
		
		if(parametersString.length()>0){
			//just to remove the first &
			return parametersString.substring(1);
		}
		else{
			return "";
		}
	}
	
	public static String getRequestParametersAsString(HttpServletRequest request){
		
		
		
		return "";
	}
	
}
