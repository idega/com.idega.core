package com.idega.user.bean;

import java.util.List;

import com.idega.util.CoreConstants;

public class PropertiesBean implements AbstractProperties {
	
	private String server = null;
	private String login = null;
	private String password = null;
	private String instanceId = null;
	
	private List uniqueIds = null;
	
	private boolean remoteMode = false;
	private boolean showLabels = false;
	private boolean showAddress = true;
	private boolean showDescription = false;
	private boolean showExtraInfo = false;
	private boolean showEmails = true;
	
	private Integer cacheTime = new Integer(10);	//	minutes
	
	public PropertiesBean() {}
	
	public PropertiesBean(PropertiesBean bean) {
		this();
		this.setServer(bean.getServer());
		this.setLogin(bean.getLogin());
		this.setPassword(bean.getPassword());
		this.setInstanceId(bean.getInstanceId());
		this.setUniqueIds(bean.getUniqueIds());
		this.setRemoteMode(bean.isRemoteMode());
		this.setShowLabels(bean.isShowLabels());
		this.setShowAddress(bean.isShowAddress());
		this.setShowDescription(bean.isShowDescription());
		this.setShowExtraInfo(bean.isShowExtraInfo());
		this.setShowEmails(bean.isShowEmails());
	}
	
	public PropertiesBean(PropertiesBean bean, boolean showDescription, boolean showExtraInfo) {
		this(bean);
		this.setShowDescription(showDescription);
		this.setShowExtraInfo(showExtraInfo);
	}
	
	public PropertiesBean(boolean showDescription, boolean showExtraInfo) {
		this.setShowDescription(showDescription);
		this.setShowExtraInfo(showExtraInfo);
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
		return remoteMode;
	}

	public void setRemoteMode(boolean remoteMode) {
		this.remoteMode = remoteMode;
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

	public Integer getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(Integer cacheTime) {
		this.cacheTime = cacheTime;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
}
