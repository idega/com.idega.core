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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
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
import com.idega.util.datastructures.map.MapUtil;

/**
 *
 * Last modified: $Date: 2008/12/19 08:54:45 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.8 $
 */
public class RequestUtil {

	private static String	SLASH = CoreConstants.SLASH,
							IMAGEBUTTON_XPOS_SUFFIX = ".x",
							HEADER_REFERER = "Referer";

	public static final String	HEADER_ACCEPT_LANGUAGE = "Accept-Language",
								HEADER_USER_AGENT = "User-agent",
								HEADER_AUTHORIZATION = "Authorization",
								HEADER_CONTENT_TYPE = "Content-Type";

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
		buf.append(request.getServerName());
		int port = request.getServerPort();
		if (port == 80 || port == 443) {
			//do not add port to url
		} else {
			buf.append(CoreConstants.COLON).append(port);
		}
		buf.append(CoreConstants.SLASH);
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

	public static String getBrowserLanguage(HttpServletRequest request) {
		return request.getHeader(HEADER_ACCEPT_LANGUAGE);
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
		if (!requestedPage.startsWith(CoreConstants.PAGES_URI_PREFIX + CoreConstants.SLASH)) {
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
				loginRedirectString = new StringBuilder(CoreConstants.QMARK).append(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON)
										.append(CoreConstants.EQ).append(requestedPage).append(CoreConstants.AMP).append(parameters).toString();

				break;
			}
			case HttpServletResponse.SC_NOT_FOUND : {
				redirectUri = settings.getProperty(CoreConstants.PAGE_ERROR_404_HANDLER_PORPERTY, CoreConstants.PAGES_URI_PREFIX);

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
		Map<?, ?> parameters = request.getParameterMap();

		if (parameters != null && !parameters.isEmpty()) {
			Set<?> parametersSet = parameters.keySet();
			for (Iterator<?> iterator = parametersSet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if(ListUtil.isEmpty(keysToIgnore) || !(keysToIgnore.contains(key)) ){
					String[] values = request.getParameterValues(key);
					if (values != null && values.length > 0) {
						for (int j = 0; j < values.length; j++) {
							parametersString.append(CoreConstants.AMP).append(key).append(CoreConstants.EQ).append(values[j]);
						}
					}
				}
			}
		}

		if (parametersString.length() > 0) {
			//just to remove the first &
			return parametersString.substring(1);
		} else{
			return CoreConstants.EMPTY;
		}
	}

	public static String getRequestParametersAsString(HttpServletRequest request){
		return CoreConstants.EMPTY;
	}

	public static Map<String, List<String>> getAllParameters(HttpServletRequest request) {
		if (request == null) {
			return null;
		}

		Map<String, List<String>> params = new HashMap<>();
		Map<String, String[]> requestParams = request.getParameterMap();
		if (!MapUtil.isEmpty(requestParams)) {
			for (String param: requestParams.keySet()) {
				String[] values = requestParams.get(param);
				params.put(param, ArrayUtil.isEmpty(values) ? Collections.emptyList() : Arrays.asList(values));
			}
		}
		return params;
	}

	public static List<String> getParameterValues(HttpServletRequest request, String param) {
		if (StringUtil.isEmpty(param)) {
			return null;
		}

		Map<String, List<String>> params = getAllParameters(request);
		List<String> values = MapUtil.isEmpty(params) ? null : params.get(param);
		return values;
	}

	public static final String getBasicAuthorizationHeader(String clientId, String clientSecret) {
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(URLEncoder.encode(clientId, StandardCharsets.UTF_8.name()));
			sb.append(CoreConstants.COLON);
			sb.append(URLEncoder.encode(clientSecret, StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {}

		return "Basic " + new String(Base64.getEncoder().encode(sb.toString().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}

}