/*
 * $Id: DefaultIWActionURIHandler.java,v 1.2 2005/02/25 14:50:13 eiki Exp $
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
 *  Last modified: $Date: 2005/02/25 14:50:13 $ by $Author: eiki $
 * A default implementation of IWActionURIHandler. The main method here is the getRedirectURI method.
 * It will return the path part of the action uri by default.
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class DefaultIWActionURIHandler implements IWActionURIHandler {


	public DefaultIWActionURIHandler() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#getRedirectURI(java.lang.String)
	 */
//	public String getRedirectURI(String requestURI) {
//		return getIWActionURI(requestURI).getPathPart();
//	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#getRedirectURI(com.idega.core.uri.IWActionURI)
	 */
	public String getRedirectURI(IWActionURI uri) {
		return uri.getPathPart();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#getIWActionURI(java.lang.String)
	 */
	public IWActionURI getIWActionURI(String requestURI) {
		return new IWActionURI(requestURI);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#canHandleIWActionURI(com.idega.core.uri.IWActionURI)
	 */
	public boolean canHandleIWActionURI(IWActionURI uri) {
		return false;
	}
}
