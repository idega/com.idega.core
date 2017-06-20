package com.idega.core.component.bean;

import java.util.List;

public class RenderedComponent {

	private String html;
	private String errorMessage;

	private List<String> resources, jsActions;

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public List<String> getJsActions() {
		return jsActions;
	}

	public void setJsActions(List<String> jsActions) {
		this.jsActions = jsActions;
	}

}