//idega 2001 - Tryggvi Larusson
/*
*Copyright 2002 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

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
}