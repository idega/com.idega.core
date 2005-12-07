/*
 * $Id: RequestUtil.java,v 1.2 2005/12/07 11:51:51 tryggvil Exp $
 * Created on 27.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 *  Last modified: $Date: 2005/12/07 11:51:51 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class RequestUtil {
	
	private static String SLASH="/";
	
	/**
	 * Calls the method HttpServletRequest.getRequestURI() and cuts front of it the Context path if any.
	 * @param request
	 * @return
	 */
	public static String getURIMinusContextPath(HttpServletRequest request) {
		//IWMainApplication iwma = getIWMainApplication(request);
		
		//String appUri = iwma.getApplicationContextURI();
		String appUri = request.getContextPath();
		String requestUri = request.getRequestURI();
	
		if(!appUri.endsWith(SLASH)){
			appUri =appUri+SLASH;
		}
		
		if(appUri.equals(SLASH)){
			return requestUri;
		}
		else{
			//Here we set -1 because we want to keep the "/" character in the beginning
			String newUri = requestUri.substring(appUri.length()-1);
			return newUri;
		}
	}
	
	/**
	 * Gets a constructed base URL for the server.
	 * @return the servername with port and protocol, e.g. http://www.idega.com:8080/
	 */
	public static String getServerURL(HttpServletRequest request){
		StringBuffer buf = new StringBuffer();
		String scheme = request.getScheme();
		
		buf.append(scheme);
		buf.append("://");
		/*if(request.isSecure()){
			buf.append("https://");
		}
		else{
			buf.append("http://");
		}*/

		buf.append(request.getServerName());
		if( request.getServerPort()!=80 ){
			buf.append(":").append(request.getServerPort());
		}
		
		buf.append("/");
		
		return buf.toString();
	}
	
}
