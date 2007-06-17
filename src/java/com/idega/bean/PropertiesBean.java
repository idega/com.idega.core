package com.idega.bean;

import java.util.List;

import com.idega.util.CoreConstants;

public class PropertiesBean implements AbstractProperties {
	
	private String server = null;
	private String login = null;
	private String password = null;
	
	private List uniqueIds = null;
	private List localizedText = null;
	
	private boolean isRemoteMode = false;
	private boolean showLabels = false;
	private boolean showAddress = true;
	private boolean showDescription = false;
	private boolean showExtraInfo = false;
	private boolean showEmails = true;
	
	public PropertiesBean() {}
	
	public PropertiesBean(PropertiesBean bean) {
		setServer(bean.getServer());
		setLogin(bean.getLogin());
		setPassword(bean.getPassword());
		setUniqueIds(bean.getUniqueIds());
		setLocalizedText(bean.getLocalizedText());
		setRemoteMode(bean.isRemoteMode());
		setShowLabels(bean.isShowLabels());
		setShowAddress(bean.isShowAddress());
		setShowDescription(bean.isShowDescription());
		setShowExtraInfo(bean.isShowExtraInfo());
		setShowEmails(bean.isShowEmails());
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

	public List getUniqueIds() {
		return uniqueIds;
	}

	public void setUniqueIds(List uniqueIds) {
		this.uniqueIds = uniqueIds;
	}

	public boolean isRemoteMode() {
		return isRemoteMode;
	}

	public void setRemoteMode(boolean isRemoteMode) {
		this.isRemoteMode = isRemoteMode;
	}

	public List getLocalizedText() {
		return localizedText;
	}

	public void setLocalizedText(List localizedText) {
		this.localizedText = localizedText;
	}
	
	public boolean isShowLabels() {
		return showLabels;
	}
	
	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public boolean isShowAddress() {
		return showAddress;
	}

	public void setShowAddress(boolean showAddress) {
		this.showAddress = showAddress;
	}

	public boolean isShowDescription() {
		return showDescription;
	}

	public void setShowDescription(boolean showDescription) {
		this.showDescription = showDescription;
	}

	public boolean isShowEmails() {
		return showEmails;
	}

	public void setShowEmails(boolean showEmails) {
		this.showEmails = showEmails;
	}

	public boolean isShowExtraInfo() {
		return showExtraInfo;
	}

	public void setShowExtraInfo(boolean showExtraInfo) {
		this.showExtraInfo = showExtraInfo;
	}
	
}
