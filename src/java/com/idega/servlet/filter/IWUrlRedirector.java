/*
 * $Id: IWUrlRedirector.java,v 1.18 2006/03/16 15:32:54 tryggvil Exp $
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
import java.util.Iterator;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.core.builder.business.BuilderPageException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 *  Filter that detects incoming urls and redirects to another url. <br>
 *  Now used for mapping old idegaWeb urls to the new appropriate ones.<br><br>
 * 
 *  Last modified: $Date: 2006/03/16 15:32:54 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.18 $
 */
public class IWUrlRedirector extends BaseFilter implements Filter {


	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		initializeDefaultDomain(request);
		
		try{
			boolean doRedirect = getIfDoRedirect(request);
			if(doRedirect){
				String newUrl = getNewRedirectURL(request);
				response.sendRedirect(newUrl);
			}
			else{
				chain.doFilter(srequest,sresponse);
			}
		}
		catch(BuilderPageException pe){
			if(pe.getCode().equals(BuilderPageException.CODE_NOT_FOUND)){
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			else{
				throw pe;
			}
		}
	}

	/**
	 * @param request
	 * @return
	 */
	String getNewRedirectURL(HttpServletRequest request) {
		//TODO: Make this logic support regular expressions
		String requestUri = getURIMinusContextPath(request);
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
				String newPageUri = bs.getPageURI(pageId);
				Map pMap = request.getParameterMap();
				StringBuffer newUri = new StringBuffer(newPageUri);
				if (pMap != null) {
					Iterator keys = pMap.keySet().iterator();
					boolean first = true;
					while (keys.hasNext()) {
						String key = (String) keys.next();
						if (!key.equals(OLD_BUILDER_PAGE_PARAMETER)) {
							String[] values = (String[]) pMap.get(key);
							if (values != null) {
								for (int i = 0; i < values.length; i++) {
									if (first) {
										newUri.append("?");
										first = false;
									} else {
										newUri.append("&");
									}
									newUri.append(key).append("=").append(values[i]);
								}
							}
						}
					}
				}
				return newUri.toString();
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
		else if(requestUri.equals(NEW_WORKSPACE_URI_MINUSSLASH)){
			return getNewWorkspaceUri(request);
		}
		else if(requestUri.equals(PAGES_URI_MINUSSLASH)){
			return getPagesUri(request);
		}
		else if (requestUri.startsWith(OLD_OBJECT_INSTANCIATOR)) {
			String param = request.getParameter(IWMainApplication.classToInstanciateParameter);
			boolean isClassName = false;
			try {
				Class.forName(param);
				isClassName = true;
			} catch (Exception e) {
			}
			if (param != null && (isClassName || param.length() == 4)) {
				String object = IWMainApplication.decryptClassName(param);
				try {
					Map pMap = request.getParameterMap();
					StringBuffer newUri = new StringBuffer(getIWMainApplication(request).getPublicObjectInstanciatorURI(Class.forName(object)));
					if (pMap != null) {
						Iterator keys = pMap.keySet().iterator();
						boolean first = true;
						while (keys.hasNext()) {
							String key = (String) keys.next();
							if (!key.equals(IWMainApplication.classToInstanciateParameter)) {
								String[] values = (String[]) pMap.get(key);
								if (values != null) {
									for (int i = 0; i < values.length; i++) {
										if (first) {
											newUri.append("?");
											first = false;
										} else {
											newUri.append("&");
										}
										newUri.append(key).append("=").append(values[i]);
									}
								}
							}
						}
					}
					return newUri.toString();
				}
				catch (ClassNotFoundException e) {
					String referer = request.getHeader("Referer");
					System.err.println("[IWUrlRedirector] Referer = "+referer);
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else{
			ViewManager viewManager = ViewManager.getInstance(getIWMainApplication(request));
			ViewNode node = viewManager.getViewNodeForRequest(request);
			if(node!=null){
				if(node.getRedirectsToResourceUri()){
					IWMainApplication iwma = getIWMainApplication(request);
					String resourceUri = node.getResourceURI();
					resourceUri = iwma.getTranslatedURIWithContext(resourceUri);
					return resourceUri;
				}
			}
		}
		String referer = request.getHeader("Referer");
		System.err.println("[IWUrlRedirector] Referer = "+referer);
		throw new RuntimeException("Error handling redirect Url");
	}

	/**
	 * @param request
	 * @return
	 */
	boolean getIfDoRedirect(HttpServletRequest request) {
		if(IWMainApplication.useNewURLScheme){
			String requestUri = getURIMinusContextPath(request);
			String oldIdegaWebUriWithSlash = OLD_IDEGAWEB_LOGIN_WITHSLASH;
			
			ViewManager viewManager = ViewManager.getInstance(getIWMainApplication(request));
			ViewNode node = viewManager.getViewNodeForRequest(request);
			if(node!=null){
				if(node.getRedirectsToResourceUri()){
					return true;
				}
			}
			
			if(getIWMainApplication(request).isInSetupMode()){
				String fullRequestUri = request.getRequestURI();
				//this is a small hack to force the bundles load:
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
			else if(requestUri.equals(NEW_WORKSPACE_URI_MINUSSLASH)){
				return true;
			}
			else if(requestUri.equals(PAGES_URI_MINUSSLASH)){
				return true;
			}
			else if(requestUri.equals(OLD_OBJECT_INSTANCIATOR)){
				return true;
			}
		}
		return false;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
		
}
