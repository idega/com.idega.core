/*
 * $Id: IWActionURI.java,v 1.1 2005/02/03 11:25:48 eiki Exp $
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
public class IWActionURI {
	
	private String actionPart;
	private String pathPart;
	private String identifierPart;

	/**
	 * 
	 */
	public IWActionURI(String requestURI) {
		
	}
	
	
	/**
	 * @return Returns the actionPart, e.g. "edit" from /idegaweb/action/edit/files/cms/article/1.xml
	 */
	public String getActionPart() {
		return actionPart;
	}
	
	/**
	 * @return Returns the pathPart, e.g. "/files/cms/article/" from /idegaweb/action/edit/files/cms/article/1.xml
	 */
	public String getPathPart() {
		return pathPart;
	}
	
	/**
	 * @return Returns the identifierPart, e.g. "/1.xml" from /idegaweb/action/edit/files/cms/article/1.xml
	 */
	public String getIdentifierPart() {
		return identifierPart;
	}
}
