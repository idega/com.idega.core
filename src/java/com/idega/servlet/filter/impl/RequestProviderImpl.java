package com.idega.servlet.filter.impl;

import javax.servlet.http.HttpServletRequest;

import com.idega.servlet.filter.RequestProvider;

public class RequestProviderImpl implements RequestProvider {

	private HttpServletRequest request;
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
