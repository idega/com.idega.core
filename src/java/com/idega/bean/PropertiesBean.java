package com.idega.bean;

import java.util.List;

import com.idega.util.CoreConstants;

public class PropertiesBean implements AbstractProperties {
	
	private String server = null;
	private String login = null;
	private String password = null;
	
	private List<String> uniqueIds = null;
	private List<String> localizedText = null;
	
	private boolean isRemoteMode = false;
	private boolean showLabels = true;
	
	public PropertiesBean() {}
	
	public PropertiesBean(PropertiesBean bean) {
		setServer(bean.getServer());
		setLogin(bean.getLogin());
		setPassword(bean.getPassword());
		setUniqueIds(bean.getUniqueIds());
		setLocalizedText(bean.getLocalizedText());
		setRemoteMode(bean.isRemoteMode());
		setShowLabels(bean.isShowLabels());
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getServer() {
		return server;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setServer(String server) {
		if (server == null) {
			this.server = null;
			return;
		}
		if (server.endsWith(CoreConstants.SLASH)) {
			server = server.substring(0, server.lastIndexOf(CoreConstants.SLASH));
		}
		this.server = server;
	}

	public List<String> getUniqueIds() {
		return uniqueIds;
	}

	public void setUniqueIds(List<String> uniqueIds) {
		this.uniqueIds = uniqueIds;
	}

	public boolean isRemoteMode() {
		return isRemoteMode;
	}

	public void setRemoteMode(boolean isRemoteMode) {
		this.isRemoteMode = isRemoteMode;
	}

	public List<String> getLocalizedText() {
		return localizedText;
	}

	public void setLocalizedText(List<String> localizedText) {
		this.localizedText = localizedText;
	}
	
	public boolean isShowLabels() {
		return showLabels;
	}
	
	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}
}
