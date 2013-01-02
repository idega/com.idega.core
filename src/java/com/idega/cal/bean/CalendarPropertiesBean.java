package com.idega.cal.bean;

import java.util.List;

import com.idega.user.bean.PropertiesBean;

public class CalendarPropertiesBean extends PropertiesBean {

	private boolean showEntriesAsList = false,
					hideMenu = false,
					hidePreviousAndNext = false,
					showTime = false,
					showMyBedeworkEvents = false;

	private List<String> events = null;
	private List<String> ledgers = null;

	public CalendarPropertiesBean() {
		super();
	}

	public CalendarPropertiesBean(PropertiesBean properties) {
		super(properties);
	}

	public boolean isShowMyBedeworkEvents() {
		return showMyBedeworkEvents;
	}

	public void setShowMyBedeworkEvents(boolean showMyBedeworkEvents) {
		this.showMyBedeworkEvents = showMyBedeworkEvents;
	}

	public List<String> getEvents() {
		return events;
	}

	public void setEvents(List<String> events) {
		this.events = events;
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

	public List<String> getLedgers() {
		return ledgers;
	}

	public void setLedgers(List<String> ledgers) {
		this.ledgers = ledgers;
	}

}
