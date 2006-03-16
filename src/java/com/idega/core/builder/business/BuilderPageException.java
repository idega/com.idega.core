/*
 * $Id: BuilderPageException.java,v 1.1 2006/03/16 15:32:14 tryggvil Exp $
 * Created on 16.3.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.business;


/**
 * <p>
 * Exception thrown on internal errors with Builder Pages.
 * </p>
 *  Last modified: $Date: 2006/03/16 15:32:14 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class BuilderPageException extends RuntimeException {

	public static final String CODE_NOT_FOUND="NOT_FOUND";
	
	private String code;
	private String pageUri;
	
	/**
	 * 
	 */
	public BuilderPageException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public BuilderPageException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BuilderPageException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public BuilderPageException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}

	
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	
	/**
	 * @return Returns the pageUri.
	 */
	public String getPageUri() {
		return pageUri;
	}

	
	/**
	 * @param pageUri The pageUri to set.
	 */
	public void setPageUri(String pageUri) {
		this.pageUri = pageUri;
	}
}
