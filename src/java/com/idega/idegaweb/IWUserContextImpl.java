//idega 2001 - Tryggvi Larusson
/*
*Copyright 2002 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import java.security.Principal;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.accesscontrol.jaas.IWUserPrincipal;
import com.idega.presentation.IWContext;



/**
 * Title:        A default implementation of IWUserContext
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWUserContextImpl extends IWContext implements IWUserContext{
	private HttpSession session;
	

	public IWUserContextImpl(HttpSession session,ServletContext sc){
		this.session=session;	
		this.setServletContext(sc);
	}
	
	public HttpSession getSession(){
		return session;	
	}
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWUserContext#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		String userName = getRemoteUser();
		if(userName != null){
			return new IWUserPrincipal(userName);
		} else {
			return null;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWUserContext#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String role) {
		LoggedOnInfo lInfo = LoginBusinessBean.getLoggedOnInfo(this);
		if(lInfo != null){
			Set roles = lInfo.getUserRoles();
			if(roles != null){
				return roles.contains(role);
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.idegaweb.IWUserContext#getRemoteUser()
	 */
	public String getRemoteUser() {
		LoggedOnInfo lInfo = LoginBusinessBean.getLoggedOnInfo(this);
		if(lInfo != null){
			return lInfo.getLogin();
		}
		return null;
	}
}