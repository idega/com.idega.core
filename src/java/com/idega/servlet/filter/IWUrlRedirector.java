/*
 * $Id: IWUrlRedirector.java,v 1.26 2009/04/20 09:47:56 valdas Exp $
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
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
import com.idega.presentation.DefaultErrorHandlingUriWindow;
import com.idega.util.CoreConstants;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;


/**
 *  Filter that detects incoming urls and redirects to another url. <br>
 *  Now used for mapping old idegaWeb urls to the new appropriate ones.<br><br>
 *
 *  Last modified: $Date: 2009/04/20 09:47:56 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.26 $
 */
public class IWUrlRedirector extends BaseFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(IWUrlRedirector.class.getName());

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) srequest;
		HttpServletResponse response = (HttpServletResponse) sresponse;

		setApplicationContext(request);

		try {
			boolean doRedirect = getIfDoRedirect(request);
			if (doRedirect) {
				String newUrl = getNewRedirectURL(request);
				response.sendRedirect(newUrl);
			} else if (isCorrectPath(request)) {
				chain.doFilter(srequest,sresponse);
			} else {
				String newUrl = getFixedSlashURL(request);
				response.sendRedirect(newUrl);
			}
		} catch (BuilderPageException pe) {
			if (pe.getCode().equals(BuilderPageException.CODE_NOT_FOUND)) {
				String redirectUri = RequestUtil.getRedirectUriByApplicationProperty(request, HttpServletResponse.SC_NOT_FOUND);
				if (StringUtil.isEmpty(redirectUri)) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				LOGGER.warning("Found default page for error 404, redirecting to: " + redirectUri);
				response.sendRedirect(redirectUri);
				return;
			} else {
				throw pe;
			}
		}

		removeApplicationContext(request);
	}

	String getFixedSlashURL(HttpServletRequest request) {
		String queryString = request.getQueryString();
		if(queryString!=null){
			return request.getRequestURI() + "/?"+queryString;
		}
		else{
			return request.getRequestURI() + CoreConstants.SLASH;
		}
	}

	boolean isCorrectPath(HttpServletRequest request) {
		String requestUri = getURIMinusContextPath(request);
		if(requestUri.startsWith(NEW_WORKSPACE_URI_MINUSSLASH) || requestUri.startsWith(PAGES_URI_MINUSSLASH)) {
			int lastSlashIndex = requestUri.lastIndexOf(CoreConstants.SLASH);
			if(lastSlashIndex == requestUri.length() - 1) {
				return true;
			}
			int lastDotIndex = requestUri.lastIndexOf(CoreConstants.DOT);
			if(lastSlashIndex < lastDotIndex) {
				return true;
			} else {
				return false;
			}
		}
		return true;
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
				Map<?, ?> pMap = request.getParameterMap();
				StringBuffer newUri = new StringBuffer(newPageUri);
				if (pMap != null) {
					Iterator<?> keys = pMap.keySet().iterator();
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
				LOGGER.log(Level.WARNING, "Error getting uri for page: " + pageId , e);
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
			String classParam = IWMainApplication.decryptClassName(request.getParameter(IWMainApplication.classToInstanciateParameter));
			Class<?> classToInstanciate = null;
			try {
				classToInstanciate = Class.forName(classParam);
			} catch (Exception e) {
				LOGGER.warning(classParam + " is not class name or such class can not be found!");
			}

			if (classToInstanciate != null) {
				try {
					Class<? extends UIComponent> uiComponentToInstanciate = classToInstanciate.asSubclass(UIComponent.class);
					Map<?, ?> pMap = request.getParameterMap();
					StringBuffer newUri = new StringBuffer(getIWMainApplication(request).getPublicObjectInstanciatorURI(uiComponentToInstanciate));
					if (pMap != null) {
						Iterator<?> keys = pMap.keySet().iterator();
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
				catch(ClassCastException e) {
					LOGGER.log(Level.WARNING, "Error resolving " + UIComponent.class.getSimpleName() + " from: " + classToInstanciate.getName(), e);
				}
				catch (Exception e) {
					LOGGER.log(Level.WARNING, "Error resolving " + UIComponent.class.getSimpleName() + " from: " + classToInstanciate.getName(), e);
				}
			}
		}
		else if (requestUri.startsWith(BUILDER_APPLICATION_URI)) {
			BuilderService builder = null;
			try {
				builder = BuilderServiceFactory.getBuilderService(getIWMainApplication(request).getIWApplicationContext());
			} catch (RemoteException e) {
				LOGGER.log(Level.WARNING, "Error getting " + BuilderService.class.getSimpleName(), e);
			}

			if (builder.isFirstBuilderRun()) {
				return new StringBuffer(NEW_WORKSPACE_URI).append(CoreConstants.CONTENT_VIEW_MANAGER_ID).toString();
			}
			else {
				return new StringBuffer(NEW_WORKSPACE_URI).append(CoreConstants.BUILDER).toString();
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
		System.err.println("Error handling redirect Url");
		return getIWMainApplication(request).getPublicObjectInstanciatorURI(DefaultErrorHandlingUriWindow.class);
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
			else if (requestUri.startsWith(BUILDER_APPLICATION_URI)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
	}

}
