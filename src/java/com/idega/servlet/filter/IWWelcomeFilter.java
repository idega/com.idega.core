/*
 * $Id: IWWelcomeFilter.java,v 1.6 2005/01/14 00:05:41 tryggvil Exp $
 * Created on 31.7.2004 by tryggvil
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.business.IBOLookup;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

/**
 * <p>
 * This filter detects the incoming url and sends them to the appropriate one if the requestUri of the incoming request is coming to the root of the.
 * </p>
 * 
 *  Last modified: $Date: 2005/01/14 00:05:41 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class IWWelcomeFilter extends BaseFilter {

	private static boolean isInit=false;
	
	private static boolean START_ON_WORKSPACE=true;	
	private static boolean START_ON_PAGES=false;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		

		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		if(!isInit){
			init(request,response);
			isInit=true;
		}
		
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		
		String appUri = iwma.getApplicationContextURI();
		String requestUri = request.getRequestURI();
		String SLASH = "/";
		
		if(!appUri.endsWith(SLASH)){
			appUri =appUri+SLASH;
		}
		
		if(requestUri.equals(appUri)){
			if(START_ON_WORKSPACE){
				//request.getRequestDispatcher("/workspace/").forward(request,response);
				response.sendRedirect(getNewWorkspaceUri(request));
			}
			else if(START_ON_PAGES){
				request.getRequestDispatcher(PAGES_URI).forward(request,response);
			}
		}
		else{
			chain.doFilter(srequest,sresponse);
		}

	}

	/**
	 * @param request
	 * @param response
	 */
	private void init(HttpServletRequest request, HttpServletResponse response) {
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		
		try {
			BuilderService bService = (BuilderService)IBOLookup.getServiceInstance(iwma.getIWApplicationContext(),BuilderService.class);
			ICPage rootPage = bService.getRootPage();
			if(rootPage.getChildCount()>0){
				//set the filter to forward to /pages if there are any subpages
				START_ON_PAGES=true;
				START_ON_WORKSPACE=false;
			}
			/*String serverName = request.getServerName();
			int port = request.getLocalPort();
			if(port!=80){
				serverName += ":"+port;
			}
			iwma.getIWApplicationContext().getDomain().setServerName(serverName);*/
			IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
			//This sets the domain by default:
			iwc.getDomain();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
