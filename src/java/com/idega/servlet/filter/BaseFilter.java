/*
 * $Id: BaseFilter.java,v 1.7 2005/02/01 18:01:28 thomas Exp $
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
import com.idega.repository.data.MutableClass;
import com.idega.util.RequestUtil;



/**
 *  Class that holds basic functionality used by many filters.<br>
 * 
 *  Last modified: $Date: 2005/02/01 18:01:28 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.7 $
 */
public abstract class BaseFilter implements Filter, MutableClass {
	
	private static final boolean DEFAULT_VALUE_CHECKED_CURRENT_APPCONTEXT = false;
	private static boolean checkedCurrentApplicationContext = DEFAULT_VALUE_CHECKED_CURRENT_APPCONTEXT;

	public static void unload()	{
		checkedCurrentApplicationContext = DEFAULT_VALUE_CHECKED_CURRENT_APPCONTEXT; 
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
			checkedCurrentApplicationContext=true;
		}
	}
	
	private boolean hasCheckedCurrentAppContext(){
		return checkedCurrentApplicationContext;
	}
	
	protected IWMainApplication getIWMainApplication(HttpServletRequest request) {
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		return iwma;
	}
}
