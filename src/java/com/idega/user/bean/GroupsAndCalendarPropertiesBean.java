package com.idega.user.bean;

import java.util.List;

import com.idega.util.CoreConstants;

//public class GroupsAndCalendarPropertiesBean implements AbstractProperties {
public class GroupsAndCalendarPropertiesBean {
	
	private String server = null;
	private String login = null;
	private String password = null;
	
	private boolean isRemoteMode = false;
//	private boolean showEntriesAsList = false;
//	private boolean showMenuButtons = false;
//	private boolean showPreviousAndNextButtons = false;
	
	private List calendarAttributes = null;
	
	public GroupsAndCalendarPropertiesBean(){}
	
//	public boolean isShowEntriesAsList() {
//		return showEntriesAsList;
//	}
//
//	public void setShowEntriesAsList(boolean showEntriesAsList) {
//		this.showEntriesAsList = showEntriesAsList;
//	}
//
//	public boolean isShowMenuButtons() {
//		return showMenuButtons;
//	}
//
//	public void setShowMenuButtons(boolean showMenuButtons) {
//		this.showMenuButtons = showMenuButtons;
//	}
//
//	public boolean isShowPreviousAndNextButtons() {
//		return showPreviousAndNextButtons;
//	}
//
//	public void setShowPreviousAndNextButtons(boolean showPreviousAndNextButtons) {
//		this.showPreviousAndNextButtons = showPreviousAndNextButtons;
//	}

	public List getCalendarAttributes() {
		return calendarAttributes;
	}

	public void setCalendarAttributes(List calendarAttributes) {
		this.calendarAttributes = calendarAttributes;
	}

	
//	public GroupsAndCalendarPropertiesBean(GroupsAndCalendarPropertiesBean bean) {
//		setServer(bean.getServer());
//		setLogin(bean.getLogin());
//		setPassword(bean.getPassword());
//		setRemoteMode(bean.isRemoteMode());
//		setCalendarAttributes(bean.getCalendarAttributes());
//	}

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

	public boolean isRemoteMode() {
		return isRemoteMode;
	}

	public void setRemoteMode(boolean isRemoteMode) {
		this.isRemoteMode = isRemoteMode;
	}
}
