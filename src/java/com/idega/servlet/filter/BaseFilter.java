/*
 * $Id: BaseFilter.java,v 1.1 2005/01/07 04:14:41 tryggvil Exp $
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
import com.idega.idegaweb.IWMainApplication;


/**
 *  Class that holds basic functionality used by many filters.<br>
 * 
 *  Last modified: $Date: 2005/01/07 04:14:41 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public abstract class BaseFilter implements Filter{

	protected static String OLD_BUILDER_SERVLET_URI = "/servlet/IBMainServlet";
	protected static String OLD_BUILDER_INDEX_JSP_URI = "/index.jsp";
	protected static String OLD_BUILDER_PAGE_PARAMETER = "ib_page";
	protected static String OLD_IDEGAWEB_LOGIN = "/idegaweb";
	protected static String OLD_IDEGAWEB_LOGIN_WITHSLASH = "/idegaweb/";
	protected static String NEW_IDEGAWEB_LOGIN = "/login/";
	protected static String NEW_IDEGAWEB_LOGIN_MINUSSLASH = "/login";
	protected static String NEW_WORKSPACE_URI="/workspace/";
	protected static String NEW_WORKSPACE_URI_MINUSSLASH="/workspace";
	static String SLASH = "/";

	protected String getNewLoginUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(NEW_IDEGAWEB_LOGIN);
		//return NEW_IDEGAWEB_LOGIN;
	}
	
	protected String getNewWorkspaceUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(NEW_WORKSPACE_URI);
		//return NEW_WORKSPACE_URI;
	}
	
	protected String getURLMinusContextPath(HttpServletRequest request) {
		IWMainApplication iwma = getIWMainApplication(request);
		
		//String appUri = iwma.getApplicationContextURI();
		String appUri = request.getContextPath();
		String requestUri = request.getRequestURI();
	
		if(!appUri.endsWith(SLASH)){
			appUri =appUri+SLASH;
		}
		
		if(appUri.equals(SLASH)){
			return requestUri;
		}
		else{
			//Here we set -1 because we want to keep the "/" character in the beginning
			String newUri = requestUri.substring(appUri.length()-1);
			return newUri;
		}
		
	}

	protected IWMainApplication getIWMainApplication(HttpServletRequest request) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		return iwma;
	}
}
