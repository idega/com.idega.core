/*
 * $Id: RequestUtil.java,v 1.6 2006/05/23 15:17:32 tryggvil Exp $ Created on
 * 27.1.2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * Last modified: $Date: 2006/05/23 15:17:32 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
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
}
