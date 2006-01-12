/*
 * $Id: IWJAASAuthenticationRequestWrapper.java,v 1.2 2006/01/12 15:30:21 tryggvil Exp $
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


/**
 * 
 * HttpServletRequestWrapper that overwrites methods used by JAAS and makes it look
 * like user is logged on JAAS if he is logged on IdegaWeb.  If the user is logged on 
 * JAAS then the methods use the super implementation.
 * 
 *  Last modified: $Date: 2006/01/12 15:30:21 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.2 $
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
			userPrincipal = new IWUserPrincipal(lInfo.getLogin());
			userRoles = lInfo.getUserRoles();
		}
	}
	
	public Principal getUserPrincipal(){
		return (userPrincipal!=null)?userPrincipal:super.getUserPrincipal(); // new IWUserPrincipal("root"); //
	}
	
	public String getRemoteUser(){
		return (userPrincipal!=null)?userPrincipal.getName():super.getRemoteUser();
	}
	
	public boolean isUserInRole(String role){
		boolean inIWSystem = (userRoles != null)?userRoles.contains(role):false;
		return inIWSystem || super.isUserInRole(role);
	}
	
}
