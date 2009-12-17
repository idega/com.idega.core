package com.idega.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.business.SpringBeanName;

@SpringBeanName("iwCoreServletRequestProvider")
public interface RequestResponseProvider {

	public HttpServletRequest getRequest();
	public void setRequest(HttpServletRequest request);
	
	public HttpServletResponse getResponse();
	public void setResponse(HttpServletResponse response);
	
}
