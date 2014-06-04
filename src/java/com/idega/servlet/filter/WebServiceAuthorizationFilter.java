/*
 * $Id: WebServiceAuthorizationFilter.java,v 1.2 2007/01/22 08:16:56 tryggvil Exp $
 * Created on Apr 4, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.dao.UserLoginDAO;
import com.idega.core.accesscontrol.data.bean.UserLogin;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.RequestUtil;
import com.idega.util.StringHandler;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * This Servlet filter sits by default in front of all (Axis) web services
 * and blocks unpriviliged access. <br/>
 * So access by clients that desire to use the services needs to be configured
 * with the WS_DO_BASIC_AUTHENTICATION or WS_VALID_IP application properties.
 * </p>
 *  Last modified: $Date: 2007/01/22 08:16:56 $ by $Author: tryggvil $
 *
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class WebServiceAuthorizationFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(WebServiceAuthorizationFilter.class.getName());
	
	private final String WEB_SERVICE_USER_ROLE = "web_service_user";

	private final String DO_BASIC_AUTHENTICATION = "WS_DO_BASIC_AUTHENTICATION";

	private final String VALID_IP = "WS_VALID_IP";

	private LoginBusinessBean loginBusiness = null;

	@Autowired
	private UserLoginDAO userLoginDAO;

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest myRequest, ServletResponse myResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) myRequest;
		HttpServletResponse response = (HttpServletResponse) myResponse;
		
		ServletContext myServletContext = request.getSession().getServletContext();
	   	// getting the application context
    	IWMainApplication mainApplication = IWMainApplication.getIWMainApplication(myServletContext);
    	boolean doCheck = mainApplication.getIWApplicationContext().getApplicationSettings().getBoolean(this.DO_BASIC_AUTHENTICATION, true);

    	if (doCheck) {
    		if (!requestIsValid(request)) {
    			LOGGER.warning("Invalid request: basic authentication needed for " + request.getRequestURI() + request.getQueryString());
    			//send a 403 error
    			response.sendError(HttpServletResponse.SC_FORBIDDEN);
    			return;
    		}
    	} else {
			boolean isValid = false;
			String clientIP = null, validIP = null;
	    	try {
	    		clientIP = request.getRemoteAddr();
	    		validIP = mainApplication.getIWApplicationContext().getApplicationSettings().getProperty(this.VALID_IP, "");
	    		if (CoreConstants.STAR.equals(validIP)) {
	    			isValid = true;
	    		} else {
		    		String[] ips = validIP.split("\\;");
		    		for (int i = 0; i < ips.length; i++) {
		    			if (ips[i].equals(clientIP)) {
		    				isValid = true;
		    				break;
		    			}
		    		}
	    		}
	    	} catch (Exception e) {
	    		LOGGER.log(Level.WARNING, "Error determening if IP address (" + clientIP + ") is valid to access " +  request.getRequestURI() + request.getQueryString());
	    		isValid = false;
	    	}
	
	    	if (!isValid) {
	    		LOGGER.warning("Invalid request: client's IP address (" + clientIP + ") is not a valid IP (valid IPs: " + validIP + ") " + request.getRequestURI() + request.getQueryString());
	    		//send a 403 error
	    		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	    		return;
	    	}
    	}

		chain.doFilter(request, response);
	}


	private boolean requestIsValid(HttpServletRequest request) {
		String decodedNamePassword = getDecodedNamePassword(request);
		if (decodedNamePassword == null) {
			return false;
		}
		int delimiterIndex = decodedNamePassword.indexOf(":");
		if (delimiterIndex < 0) {
			return false;
		}
		String name = decodedNamePassword.substring(0, delimiterIndex);
		String password = decodedNamePassword.substring(delimiterIndex + 1);
		if (! StringHandler.isNotEmpty(name)) {
			return false;
		}
		if (! StringHandler.isNotEmpty(password)) {
			return false;
		}
		return checkUserPasswordAndRole(request, name, password);
	}

	private boolean checkUserPasswordAndRole(HttpServletRequest myRequest, String name, String password) {
		ServletContext myServletContext = myRequest.getSession().getServletContext();

    	IWMainApplication mainApplication = IWMainApplication.getIWMainApplication(myServletContext);
    	IWApplicationContext iwac = mainApplication.getIWApplicationContext();

		UserLogin login = getUserLoginDAO().findLoginByUsername(name);
		if (login != null) {
    		if (! hasRole(login, mainApplication)) {
    			return false;
    		}
    		return getLoginBusiness(iwac).verifyPassword(login, password);
		}
		else {
			return false;
		}
	}

	private boolean hasRole(UserLogin userLogin, IWMainApplication iwMainApplication) {
		User user = userLogin.getUser();
		List<Group> groups = user.getUserRepresentative().getParentGroups();
		if (groups != null) {
			AccessController accessController = iwMainApplication.getAccessController();
			for (Iterator<Group> groupIterator = groups.iterator(); groupIterator.hasNext();) {
				Group group = groupIterator.next();
				if (accessController.hasRole(this.WEB_SERVICE_USER_ROLE, group, null)) {
					return true;
				}
			}
		}
		return false;
	}

	private String getDecodedNamePassword(HttpServletRequest request) {
		String basicNamePassword = request.getHeader(RequestUtil.HEADER_AUTHORIZATION);
		if (basicNamePassword == null) {
			return null;
		}
		basicNamePassword = basicNamePassword.trim();
		if (! basicNamePassword.startsWith("Basic")) {
			return null;
		}
		if (basicNamePassword.length() < 6 ) {
			return null;
		}
		String namePassword = basicNamePassword.substring(6);
		try {
			byte[] decodedNamePasswordArray = Base64.decodeBase64(namePassword.getBytes());
			ByteBuffer wrappedDecodedNamePasswordArray = ByteBuffer.wrap(decodedNamePasswordArray);
			Charset charset = Charset.forName("ISO-8859-1");
			CharBuffer buffer = charset.decode(wrappedDecodedNamePasswordArray);
			return buffer.toString();
		} catch (Exception ex) {
			return null;
		}
	}

	private LoginBusinessBean getLoginBusiness(IWApplicationContext iwac) {
		if (this.loginBusiness == null) {
			this.loginBusiness = LoginBusinessBean.getLoginBusinessBean(iwac);
		}
		return this.loginBusiness;
	}

	@Override
	public void destroy() {
		// noting to destroy
	}

	public UserLoginDAO getUserLoginDAO() {
		if (this.userLoginDAO == null) {
			ELUtil.getInstance().autowire(this);
		}
		return userLoginDAO;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}