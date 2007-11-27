package com.idega.cal.bean;

import java.util.List;

import com.idega.user.bean.PropertiesBean;

public class CalendarPropertiesBean extends PropertiesBean {

	private boolean showEntriesAsList = false;
	private boolean hideMenu = false;
	private boolean hidePreviousAndNext = false;
	private boolean showTime = false;
	
	private List<String> eventsTypes = null;
	
	public CalendarPropertiesBean() {
		super();
	}
	
	public CalendarPropertiesBean(PropertiesBean properties) {
		super(properties);
	}
	
	public List<String> getEventsTypes() {
		return eventsTypes;
	}

	public void setEventsTypes(List<String> eventsTypes) {
		this.eventsTypes = eventsTypes;
	}

	public boolean isShowEntriesAsList() {
		return showEntriesAsList;
	}

	public void setShowEntriesAsList(boolean showEntriesAsList) {
		this.showEntriesAsList = showEntriesAsList;
	}

	public boolean isHideMenu() {
		return hideMenu;
	}

	public void setHideMenu(boolean hideMenu) {
		this.hideMenu = hideMenu;
	}

	public boolean isHidePreviousAndNext() {
		return hidePreviousAndNext;
	}

	public void setHidePreviousAndNext(boolean hidePreviousAndNext) {
		this.hidePreviousAndNext = hidePreviousAndNext;
	}

	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

}
