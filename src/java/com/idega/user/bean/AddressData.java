package com.idega.user.bean;

public class AddressData {

	private String streetAddress = null;
	private String postalName = null;
	private String postalCode = null;
	private String city = null;
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getStreetAddress() {
		return streetAddress;
	}
	
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getPostalName() {
		return postalName;
	}

	public void setPostalName(String postalName) {
		this.postalName = postalName;
	}

}