/*
 * $Id: DefaultIWActionURIHandler.java,v 1.4 2005/12/12 11:38:13 tryggvil Exp $
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
 *  Last modified: $Date: 2005/12/12 11:38:13 $ by $Author: tryggvil $
 * A default implementation of IWActionURIHandler. The main method here is the getRedirectURI method.
 * It will return the path part of the action uri by default.
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
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
		return uri.getRedirectURI();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#getIWActionURI(java.lang.String)
	 */
	public IWActionURI getIWActionURI(String requestURI,String queryString) {
		return new IWActionURI(requestURI,queryString);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.uri.IWActionURIHandler#canHandleIWActionURI(com.idega.core.uri.IWActionURI)
	 */
	public boolean canHandleIWActionURI(IWActionURI uri) {
		return false;
	}
	
	public String getHandlerIdentifier(){
		return "default";
	}
}
