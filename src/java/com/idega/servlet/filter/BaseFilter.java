/*
 * $Id: BaseFilter.java,v 1.6 2005/01/27 14:17:05 tryggvil Exp $
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
import com.idega.util.RequestUtil;


/**
 *  Class that holds basic functionality used by many filters.<br>
 * 
 *  Last modified: $Date: 2005/01/27 14:17:05 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public abstract class BaseFilter implements Filter{
	
	private static boolean DEFAULT_VALUE_CHECKED_CURRENT_APPCONTEXT = false;
	private static boolean CHECKED_CURRENT_APPCONTEXT = DEFAULT_VALUE_CHECKED_CURRENT_APPCONTEXT;

	public static void unload()	{
		CHECKED_CURRENT_APPCONTEXT = DEFAULT_VALUE_CHECKED_CURRENT_APPCONTEXT; 
	}

	protected static String OLD_BUILDER_SERVLET_URI = "/servlet/IBMainServlet";
	protected static String OLD_BUILDER_INDEX_JSP_URI = "/index.jsp";
	protected static String OLD_BUILDER_PAGE_PARAMETER = "ib_page";
	protected static String OLD_IDEGAWEB_LOGIN = "/idegaweb";
	protected static String OLD_IDEGAWEB_LOGIN_WITHSLASH = "/idegaweb/";
	protected static String NEW_IDEGAWEB_LOGIN = "/login/";
	protected static String NEW_IDEGAWEB_LOGIN_MINUSSLASH = "/login";
	protected static String NEW_WORKSPACE_URI="/workspace/";
	protected static String NEW_WORKSPACE_URI_MINUSSLASH="/workspace";
	protected static String SETUP_URI="/setup/";
	protected static String PAGES_URI="/pages/";
	
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
	
	protected String getSetupUri(HttpServletRequest request){
		IWMainApplication iwma = getIWMainApplication(request);
		return iwma.getTranslatedURIWithContext(SETUP_URI);
		//return NEW_IDEGAWEB_LOGIN;
	}
	
	protected String getURIMinusContextPath(HttpServletRequest request) {
		return RequestUtil.getURIMinusContextPath(request);
		
	}


	/**
	 * This may be called from several filter subclasses. This should ideally be called by the first filter in the chain.
	 * @param iwc
	 */
	protected void setApplicationServletContextPath(HttpServletRequest request ){
		if (!hasCheckedCurrentAppContext()) {
			String contextPath = request.getContextPath();
			getIWMainApplication(request).setApplicationContextURI(contextPath);
			CHECKED_CURRENT_APPCONTEXT=true;
		}
	}
	
	private boolean hasCheckedCurrentAppContext(){
		return CHECKED_CURRENT_APPCONTEXT;
	}
	
	protected IWMainApplication getIWMainApplication(HttpServletRequest request) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		return iwma;
	}
}
