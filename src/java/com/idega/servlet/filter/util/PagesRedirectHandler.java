package com.idega.servlet.filter.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PagesRedirectHandler {

	public static final String ATTRIBUTE_PAGES_REDIRECT_HANDLER_CLASS = "iw.pages.redirect.handler.class";
	
	public boolean isForwardOnRootURIRequest(HttpServletRequest request, HttpServletResponse response);
	
}