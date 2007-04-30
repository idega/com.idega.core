/*
 * $Id: IWWelcomeFilter.java,v 1.17 2007/04/30 17:20:04 tryggvil Exp $
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
import com.idega.util.IWTimestamp;
import com.idega.util.RequestUtil;

/**
 * <p>
 * This filter detects the incoming url and sends them to the appropriate one if the requestUri of the incoming request is coming to the root of the.
 * </p>
 * 
 *  Last modified: $Date: 2007/04/30 17:20:04 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.17 $
 */
public class IWWelcomeFilter extends BaseFilter {

	/*private static final boolean DEFAULT_VALUE_IS_INIT = false;
	private static boolean isInit= DEFAULT_VALUE_IS_INIT;
	
	private static final boolean DEFAULT_VALUE_START_ON_WORKSPACE = true;
	private static boolean startOnWorkspace= DEFAULT_VALUE_START_ON_WORKSPACE;
	
	private static final boolean DEFAULT_VALUE_START_ON_PAGES = false;
	private static boolean startOnPages= DEFAULT_VALUE_START_ON_PAGES;
	*/

	private Map initializedPages=new HashMap();
	private Map synchronizationObjects=new HashMap();
	
	private static final String PROPERTY_LOG_REQUESTS = "com.idega.core.logrequests";
	
	
	public static void unload() {
		//isInit = DEFAULT_VALUE_IS_INIT;
		//startOnWorkspace = DEFAULT_VALUE_START_ON_WORKSPACE;
		//startOnPages = DEFAULT_VALUE_START_ON_PAGES;
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
		
		/*if(!isInit(request,response)){
			init(request,response);
		}*/
		
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		
		String appUri = iwma.getApplicationContextURI();
		String requestUri = request.getRequestURI();
		String SLASH = "/";
		
		if(!appUri.endsWith(SLASH)){
			appUri =appUri+SLASH;
		}
		
		boolean logRequests = getLogRequests(request);
		if(logRequests){
			logRequest(request);
		}
		
		if(requestUri.equals(appUri)){
			

			boolean startOnWorkspace=getIfStartOnWorkspace(request);
			boolean startOnPages=getIfStartOnPages(request);
			
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
			/**
			 * This Clause is here to protect the server in high loads so that
			 * the server will let the first accessor on each pages cache it self up.
			 */
			if(isSynchronizeFirstAccess(request,response)){
				Object syncObject = getSynchronizationObject(request,response);
				synchronized(syncObject){
					chain.doFilter(srequest,sresponse);
					setSynchronizedFirstAccess(request,response);
				}
			}
			else{
				chain.doFilter(srequest,sresponse);
			}
		}

	}

	private boolean getIfStartOnPages(HttpServletRequest request) {
		return getCachedDomain(request).isStartOnPages();
	}


	private boolean getIfStartOnWorkspace(HttpServletRequest request) {
		return getCachedDomain(request).isStartOnWorkspace();
	}



	/**
	 * <p>
	 * TODO tryggvil describe method logRequest
	 * </p>
	 * @param request
	 */
	private void logRequest(HttpServletRequest request) {
	
		String ip = request.getRemoteAddr();
		String timestamp = IWTimestamp.RightNow().toString();
		String method = request.getMethod();
		String requestUri = request.getRequestURI();
		String userAgent = RequestUtil.getUserAgent(request);
		String protocol = request.getProtocol();

		//127.0.0.1 - - [22/Feb/2006:09:27:41 +0000] "GET / HTTP/1.1" 200 789 "-" "idegaWeb Web Search Engine Crawler http://www.idega.com"
		
		StringBuffer buf = new StringBuffer();
		buf.append(ip);
		buf.append(" - - ");
		buf.append("[");
		buf.append(timestamp);
		buf.append("] ");
		buf.append(method);
		buf.append(" ");
		buf.append(requestUri);
		buf.append(" ");
		buf.append(protocol);
		buf.append(" - - \"-\" ");
		buf.append(" \""+userAgent+"\"");
		
		System.out.println(buf);
	}


	/**
	 * <p>
	 * TODO tryggvil describe method getLogRequests
	 * </p>
	 * @param request
	 * @return
	 */
	private boolean getLogRequests(HttpServletRequest request) {
		
		IWMainApplication iwma = getIWMainApplication(request);
		String prop = iwma.getSettings().getProperty(PROPERTY_LOG_REQUESTS);
		if(prop==null){
			return false;
		}
		else{
			return Boolean.valueOf(prop).booleanValue();
		}
	}



	/*private void init(HttpServletRequest request, HttpServletResponse response) {
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		
		try {
			BuilderService bService = (BuilderService)IBOLookup.getServiceInstance(iwma.getIWApplicationContext(),BuilderService.class);
			ICPage rootPage = bService.getRootPage();
			boolean startOnPages=false;
			boolean startOnWorkspace=false;
			if(rootPage!=null){
				//set the filter to forward to /pages if there is a rootPage created
				startOnPages=true;
				startOnWorkspace=false;
			}
			else{
				startOnWorkspace=true;
				startOnPages=false;
			}
			//String serverName = request.getServerName();
			//int port = request.getLocalPort();
			//if(port!=80){
			//	serverName += ":"+port;
			//}
			iwma.getIWApplicationContext().getDomain().setServerName(serverName);
			//IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
			//This sets the domain by default:
			//iwc.getDomain();
			
			
			//initializeDefaultDomain(request);
		
			//Done to initialize the domain:
			getDomain(request);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		catch (IDONoDatastoreError de) {
			if(!iwma.isInDatabaseLessMode()){
				de.printStackTrace();
			}
		}
	}*/

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}


	protected synchronized Object getSynchronizationObject(HttpServletRequest request, HttpServletResponse response) {
		String requestUri = request.getRequestURI();
		Object value = synchronizationObjects.get(requestUri);
		if(value==null){
			value = requestUri;
			synchronizationObjects.put(requestUri,value);
		}
		return value;
		
	}

	protected boolean isSynchronizeFirstAccess(HttpServletRequest request, HttpServletResponse response) {
		String requestUri = request.getRequestURI();
		return initializedPages.containsKey(requestUri);
	}

	protected void setSynchronizedFirstAccess(HttpServletRequest request, HttpServletResponse response) {
		String requestUri = request.getRequestURI();
		initializedPages.put(requestUri,requestUri);
	}
	

}
