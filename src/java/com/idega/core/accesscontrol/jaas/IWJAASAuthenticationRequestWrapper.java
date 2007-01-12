/*
 * $Id: IWJAASAuthenticationRequestWrapper.java,v 1.1.2.1 2007/01/12 19:31:30 idegaweb Exp $
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
import javax.servlet.http.HttpServletRequestWrapper;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.presentation.IWContext;


/**
 * 
 * HttpServletRequestWrapper that overwrites methods used by JAAS and makes it look
 * like user is logged on JAAS if he is logged on IdegaWeb.  If the user is logged on 
 * JAAS then the methods use the super implementation.
 * 
 *  Last modified: $Date: 2007/01/12 19:31:30 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1.2.1 $
 */
public class IWJAASAuthenticationRequestWrapper extends HttpServletRequestWrapper {

	private Set userRoles = null;
	private Principal userPrincipal = null;
	/**
	 * @param arg0
	 */
	public IWJAASAuthenticationRequestWrapper(IWContext iwc) {
		super(iwc.getRequest());
		
		Principal user = super.getUserPrincipal();
		if( user == null && iwc.isLoggedOn()){
			//log on as user.getName()
			LoggedOnInfo lInfo = LoginBusinessBean.getLoggedOnInfo(iwc);
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
	
}
