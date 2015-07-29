package com.idega.servlet.filter.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.servlet.filter.RequestResponseProvider;

public class RequestResponseProviderImpl implements RequestResponseProvider {

	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	private Map<String, Boolean> permissions = new HashMap<String, Boolean>();

	@Override
	public void addPermision(String key, Boolean permission) {
		permissions.put(key, permission);
	}

	@Override
	public Boolean hasPermision(String key) {
		return permissions.get(key);
	}
}