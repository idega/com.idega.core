/*
 * $Id: DefaultIWActionURIHandler.java,v 1.1 2005/02/03 11:25:48 eiki Exp $
 * Created on Jan 31, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;


/**
 * 
 *  Last modified: $Date: 2005/02/03 11:25:48 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class DefaultIWActionURIHandler implements IWActionURIHandler {

	/**
	 * 
	 */
	public DefaultIWActionURIHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#getRedirectURL(java.lang.String)
	 */
	public String getRedirectURL(String requestURI) {
		return requestURI;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#getRedirectURL(com.idega.core.uri.IWActionURI)
	 */
	public String getRedirectURL(IWActionURI uri) {
		// TODO Auto-generated method stub
		return "";
	}
}
