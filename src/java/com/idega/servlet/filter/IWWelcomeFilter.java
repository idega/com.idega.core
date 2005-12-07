/*
 * $Id: IWWelcomeFilter.java,v 1.11 2005/12/07 11:51:51 tryggvil Exp $
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
import com.idega.data.IDONoDatastoreError;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

/**
 * <p>
 * This filter detects the incoming url and sends them to the appropriate one if the requestUri of the incoming request is coming to the root of the.
 * </p>
 * 
 *  Last modified: $Date: 2005/12/07 11:51:51 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.11 $
 */
public class IWWelcomeFilter extends BaseFilter {

	private static final boolean DEFAULT_VALUE_IS_INIT = false;
	private static boolean isInit= DEFAULT_VALUE_IS_INIT;
	
	private static final boolean DEFAULT_VALUE_START_ON_WORKSPACE = true;
	private static boolean startOnWorkspace= DEFAULT_VALUE_START_ON_WORKSPACE;
	
	private static final boolean DEFAULT_VALUE_START_ON_PAGES = false;
	private static boolean startOnPages= DEFAULT_VALUE_START_ON_PAGES;
	
	
	public static void unload() {
		isInit = DEFAULT_VALUE_IS_INIT;
		startOnWorkspace = DEFAULT_VALUE_START_ON_WORKSPACE;
		startOnPages = DEFAULT_VALUE_START_ON_PAGES;
	}
	
	
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
			if(startOnWorkspace){
				//request.getRequestDispatcher("/workspace/").forward(request,response);
				response.sendRedirect(getNewWorkspaceUri(request));
			}
			else if(startOnPages){
				//request.getRequestDispatcher(PAGES_URI).forward(request,response);
				String pagesUri = getPagesUri(request);
				response.sendRedirect(pagesUri);
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
			if(rootPage!=null){
				if(rootPage.getChildCount()>0){
					//set the filter to forward to /pages if there are any subpages
					startOnPages=true;
					startOnWorkspace=false;
				}
			}
			else{
				startOnWorkspace=true;
				startOnPages=false;
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
		catch (IDONoDatastoreError de) {
			if(!iwma.isInDatabaseLessMode()){
				de.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
