/*
 * $Id: IWUrlRedirector.java,v 1.7 2005/01/14 01:47:56 tryggvil Exp $
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
 *  Last modified: $Date: 2005/01/14 01:47:56 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.7 $
 */
public class IWUrlRedirector extends BaseFilter implements Filter {


	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		setApplicationServletContextPath(request);
		
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
	String getNewRedirectURL(HttpServletRequest request) {
		//TODO: Make this logic support regular expressions
		String requestUri = getURLMinusContextPath(request);
		if(getIWMainApplication(request).isInSetupMode()){
			return getSetupUri(request);
		}
		else if(requestUri.startsWith(OLD_BUILDER_SERVLET_URI) || requestUri.equals(OLD_BUILDER_INDEX_JSP_URI)){
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
		else if(requestUri.startsWith(OLD_IDEGAWEB_LOGIN)){
			if(requestUri.equals(OLD_IDEGAWEB_LOGIN) || requestUri.equals(OLD_IDEGAWEB_LOGIN_WITHSLASH)){
				return getNewLoginUri(request);
			}
		}
		else if(requestUri.equals(NEW_IDEGAWEB_LOGIN_MINUSSLASH)){
				return getNewLoginUri(request);
		}
		throw new RuntimeException("Error handling redirect Url");
	}

	/**
	 * @param request
	 * @return
	 */
	boolean getIfDoRedirect(HttpServletRequest request) {
		if(IWMainApplication.USE_NEW_URL_SCHEME){
			String requestUri = getURLMinusContextPath(request);
			String oldIdegaWebUriWithSlash = OLD_IDEGAWEB_LOGIN_WITHSLASH;
			
			if(getIWMainApplication(request).isInSetupMode()){
				String fullRequestUri = request.getRequestURI();
				//this is a small hack to make the bundles load:
				getIWMainApplication(request).registerBundle("com.idega.core",false);
				getIWMainApplication(request).registerBundle("com.idega.faces",false);
				getIWMainApplication(request).registerBundle("com.idega.webface",false);
				getIWMainApplication(request).registerBundle("com.idega.setup",false);
				if(!fullRequestUri.equals(getSetupUri(request))){
					return true;
				}
			}
			
			if(requestUri.startsWith(OLD_BUILDER_SERVLET_URI)){
				return true;
			}
			else if(requestUri.equals(OLD_BUILDER_INDEX_JSP_URI)){
				return true;
			}
			else if(requestUri.equals(oldIdegaWebUriWithSlash)){
				return true;
			}
			else if(requestUri.equals(OLD_IDEGAWEB_LOGIN)){
				return true;
			}
			else if(requestUri.equals(NEW_IDEGAWEB_LOGIN_MINUSSLASH)){
				return true;
			}
		}
		return false;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
		
}

