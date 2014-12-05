package com.idega.event;

import com.idega.user.data.User;

public class CompanyCreatedEvent extends UserCreatedEvent {

	private static final long serialVersionUID = -7439546891158797572L;

	private String ssn;
	private String name;
	private String address;
	private String postalCode;

	public CompanyCreatedEvent(Object source, User user,  String ssn,  String name, String address, String postalCode) {
		super(source, user);

		this.ssn = ssn;
		this.name = name;
		this.address = address;
		this.postalCode = postalCode;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}