/*
 * $Id: IWJAASAutenticationRequestWrapper.java,v 1.1 2004/12/13 11:53:38 gummi Exp $
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
 *  Last modified: $Date: 2004/12/13 11:53:38 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class IWJAASAutenticationRequestWrapper extends HttpServletRequestWrapper {

	private Set userRoles = null;
	private Principal userPrincipal = null;
	/**
	 * @param arg0
	 */
	public IWJAASAutenticationRequestWrapper(IWContext iwc) {
		super(iwc.getRequest());
		iwc = new IWContext(iwc.getRequest(),iwc.getResponse(),iwc.getSession().getServletContext());
		
		Principal user = super.getUserPrincipal();
		if( user == null && iwc.isLoggedOn()){
			//log on as user.getName()
			LoggedOnInfo lInfo = LoginBusinessBean.getLoggedOnInfo(iwc);
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
