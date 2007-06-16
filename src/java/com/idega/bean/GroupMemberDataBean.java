package com.idega.bean;

import java.util.List;

import com.idega.util.IWTimestamp;

public class GroupMemberDataBean {
	
	private String name = null;
	private String title = null;
	private String education = null;
	private String school = null;
	private String area = null;
	private String beganWork = null;
	private String workPhone = null;
	private String homePhone = null;
	private String mobilePhone = null;
	private String age = null;
	private String imageUrl = null;
	
	private List emailsAddresses = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBeganWork() {
		return beganWork;
	}

	public void setBeganWork(String beganWork) {
		if (beganWork == null) {
			this.beganWork = beganWork;
		}
		else {
			this.beganWork = new IWTimestamp(beganWork).getDateString("dd-MM-yyyy");
		}
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public List getEmailsAddresses() {
		return emailsAddresses;
	}

	public void setEmailsAddresses(List emailsAddresses) {
		this.emailsAddresses = emailsAddresses;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
