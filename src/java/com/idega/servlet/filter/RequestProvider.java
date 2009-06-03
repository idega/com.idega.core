package com.idega.servlet.filter;

import javax.servlet.http.HttpServletRequest;

import com.idega.business.SpringBeanName;

@SpringBeanName("iwCoreServletRequestProvider")
public interface RequestProvider {

	public HttpServletRequest getRequest();

	public void setRequest(HttpServletRequest request);
	
}
