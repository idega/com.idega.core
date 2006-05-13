/*
 * $Id: IWJAASAuthenticationRequestWrapper.java,v 1.6 2006/05/13 13:29:04 tryggvil Exp $
 * Created on 3.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.jaas;

import java.security.Principal;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.appserver.AppServer;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 * HttpServletRequestWrapper that overwrites methods used by JAAS and makes it look
 * like user is logged on JAAS if he is logged on IdegaWeb.  If the user is logged on 
 * JAAS then the methods use the super implementation.
 * 
 *  Last modified: $Date: 2006/05/13 13:29:04 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.6 $
 */
public class IWJAASAuthenticationRequestWrapper extends HttpServletRequestWrapper {

	private Set userRoles = null;
	private Principal userPrincipal = null;

	
	/**
	 * @param arg0
	 */
	public IWJAASAuthenticationRequestWrapper(HttpServletRequest request) {
		super(request);
		
		Principal user = super.getUserPrincipal();
		LoginBusinessBean loginBean = LoginBusinessBean.getLoginBusinessBean(request);
		if( user == null && loginBean.isLoggedOn(request)){
			//log on as user.getName()
			HttpSession session = request.getSession();
			LoggedOnInfo lInfo = loginBean.getLoggedOnInfo(session);
			this.userPrincipal = new IWUserPrincipal(lInfo.getLogin());
			this.userRoles = lInfo.getUserRoles();
		}
	}
	
	public Principal getUserPrincipal(){
		return (this.userPrincipal!=null)?this.userPrincipal:super.getUserPrincipal(); // new IWUserPrincipal("root"); //
	}
	
	public String getRemoteUser(){
		return (this.userPrincipal!=null)?this.userPrincipal.getName():super.getRemoteUser();
	}
	
	public boolean isUserInRole(String role){
		boolean inIWSystem = (this.userRoles != null)?this.userRoles.contains(role):false;
		return inIWSystem || super.isUserInRole(role);
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequestWrapper#getPathInfo()
	 */
	public String getPathInfo() {
		String superPathInfo = super.getPathInfo();
		if(superPathInfo==null){
			IWMainApplication iwma = IWMainApplication.getIWMainApplication(this.getSession().getServletContext());
			AppServer appServer = iwma.getApplicationServer();
			if(appServer.getVendor().startsWith("Oracle")){
				return "/";
			}
		}
		return superPathInfo;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequestWrapper#getPathTranslated()
	 */
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return super.getPathTranslated();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURI()
	 */
	public String getRequestURI() {
		// TODO Auto-generated method stub
		return super.getRequestURI();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURL()
	 */
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return super.getRequestURL();
	}
	
}
