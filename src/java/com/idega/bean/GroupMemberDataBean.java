package com.idega.bean;

import java.util.Date;

import com.idega.util.IWTimestamp;

/**
 * 
 * @author valdas
 *
 *	This class is POJO for users of Group. Because it will be used via DWR, we need as simple fields as possible, so not all data
 *	will be stored in this class
 */

public class GroupMemberDataBean extends DataBean {
	
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
	private String job = null;
	private String workPlace = null;
	private String status = null;
	
	private Date dateOfBirth = null;

	private AddressData companyAddress = null;
	
	private boolean male = true;

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

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public AddressData getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(AddressData companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
