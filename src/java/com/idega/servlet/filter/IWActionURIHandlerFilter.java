/*
 * $Id: IWActionURIHandlerFilter.java,v 1.2 2005/02/25 14:45:16 eiki Exp $
 * Created on 30.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.core.uri.IWActionURIManager;


/**
 *  Filter that detects incoming "action" urls and redirects the real url of the registered action handler<br>
 *  e.g. "/idegaweb/action/view/files/cms/article/1.xml" would be redirected to a viewer for an article.
 * 
 *  Last modified: $Date: 2005/02/25 14:45:16 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public class IWActionURIHandlerFilter extends BaseFilter implements Filter {
	
	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest srequest, ServletResponse sresponse,FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		setApplicationServletContextPath(request);
		
		String newUrl = getIWActionRedirectURI(request);
		response.sendRedirect(newUrl);
		
	}

	/**
	 * @param request
	 * @return the uri to redirect to...
	 */
	String getIWActionRedirectURI(HttpServletRequest request) {
		String requestUri = getURIMinusContextPath(request);
		IWActionURIManager manager = IWActionURIManager.getInstance();		
		return manager.getRedirectURI(requestUri);
	}

	public void destroy() {
	}
		
}

