package com.idega.bean;

import java.util.List;

/**
 * 
 * @author valdas
 *
 *	This class is POJO for Group. Because it will be used via DWR, we need as simple fields as possible, so not all data of Group
 *	will be stored in this class
 */

public class GroupDataBean {

	private String name = null;
	private String shortName = null;
	private String homePageUrl = null;
	private String description = null;
	private String extraInfo = null;
	private String phoneNumber = null;
	private String faxNumber = null;
	
	private List emailAddresses = null;
	private AddressData address = null;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List getEmailAddresses() {
		return emailAddresses;
	}
	
	public void setEmailAddresses(List emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	
	public String getExtraInfo() {
		return extraInfo;
	}
	
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	public String getHomePageUrl() {
		return homePageUrl;
	}
	
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public AddressData getAddress() {
		return address;
	}

	public void setAddress(AddressData address) {
		this.address = address;
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
