package com.idega.user.bean;

/**
 * 
 * @author valdas
 *
 *	This class is POJO for Group. Because it will be used via DWR, we need as simple fields as possible, so not all data of Group
 *	will be stored in this class
 */

public class GroupDataBean extends DataBean {

	private String shortName = null;
	private String homePageUrl = null;
	private String phoneNumber = null;
	private String faxNumber = null;
	
	public String getHomePageUrl() {
		return homePageUrl;
	}
	
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
