/*
 * $Id: IWUrlRedirector.java,v 1.1 2004/12/30 23:19:14 tryggvil Exp $
 * Created on 30.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import java.rmi.RemoteException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWMainApplication;


/**
 *  Filter that detects incoming urls and redirects to another url. <br>
 *  Now used for mapping old idegaWeb urls to the new appropriate ones.<br><br>
 * 
 *  Last modified: $Date: 2004/12/30 23:19:14 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class IWUrlRedirector implements Filter {

	static String OLD_BUILDER_SERVLET_URI="/servlet/IBMainServlet";
	static String OLD_BUILDER_INDEX_JSP_URI="/index.jsp";
	static String OLD_BUILDER_PAGE_PARAMETER="ib_page";
	
	static String SLASH = "/";
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		boolean doRedirect = getIfDoRedirect(request);
		if(doRedirect){
			String newUrl = getNewRedirectURL(request);
			response.sendRedirect(newUrl);
		}
		else{
			chain.doFilter(srequest,sresponse);
		}
	}

	/**
	 * @param request
	 * @return
	 */
	private String getNewRedirectURL(HttpServletRequest request) {
		String requestUri = getURLMinusContextPath(request);
		if(requestUri.startsWith(OLD_BUILDER_SERVLET_URI) || requestUri.equals(OLD_BUILDER_INDEX_JSP_URI)){
			String pageId = request.getParameter(OLD_BUILDER_PAGE_PARAMETER);
			IWMainApplication iwma = getIWMainApplication(request);
			BuilderService bs;
			try {
				bs = BuilderServiceFactory.getBuilderService(iwma.getIWApplicationContext());
				if(pageId==null){
					pageId=bs.getRootPageKey();
				}
				return bs.getPageURI(pageId);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("Error handling redirect Url");
	}

	/**
	 * @param request
	 * @return
	 */
	private boolean getIfDoRedirect(HttpServletRequest request) {

		String requestUri = getURLMinusContextPath(request);
			
		if(requestUri.startsWith(OLD_BUILDER_SERVLET_URI)){
			return true;
		}
		else if(requestUri.equals(OLD_BUILDER_INDEX_JSP_URI)){
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	private String getURLMinusContextPath(HttpServletRequest request) {
		IWMainApplication iwma = getIWMainApplication(request);
		
		String appUri = iwma.getApplicationContextURI();
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
		
	
	protected IWMainApplication getIWMainApplication(HttpServletRequest request){
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		return iwma;
	}
		
}

