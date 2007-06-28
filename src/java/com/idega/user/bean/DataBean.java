package com.idega.user.bean;

import java.util.List;

/**
 * 
 * @author valdas
 *
 *	This class is base POJO for Group or users of Group. Because it will be used via DWR, we need as simple fields as possible,
 *	so not all data will be stored in this class
 */

public class DataBean {
	
	private String name = null;
	private String extraInfo = null;
	private String description = null;
	
	private List emailsAddresses = null;
	
	private AddressData address = null;

	public AddressData getAddress() {
		return address;
	}

	public void setAddress(AddressData address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List getEmailsAddresses() {
		return emailsAddresses;
	}

	public void setEmailsAddresses(List emailsAddresses) {
		this.emailsAddresses = emailsAddresses;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
