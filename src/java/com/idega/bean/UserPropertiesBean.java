package com.idega.bean;

import com.idega.util.CoreUtil;

public class UserPropertiesBean extends PropertiesBean {
	
	private boolean showGroupName = false;
	private boolean showTitle = false;
	private boolean showAge = false;
	private boolean showWorkPhone = true;
	private boolean showHomePhone = true;
	private boolean showMobilePhone = true;
	private boolean showEducation = false;
	private boolean showSchool = false;
	private boolean showArea = false;
	private boolean showBeganWork = false;
	private boolean showImage = true;
	private boolean showCompanyAddress = false;
	private boolean showDateOfBirth = false;
	private boolean showJob = false;
	private boolean showWorkplace = false;
	private boolean showStatus = true;
	private boolean addReflection = true;
	
	private String imageWidth = "70";
	private String imageHeight = "90";
	
	private String defaultPhoto = CoreUtil.getCoreBundle().getVirtualPathWithFileNameString("default_photo.jpg");
	
	public UserPropertiesBean() {}
	
	public UserPropertiesBean(PropertiesBean bean) {
		super(bean);
	}
	
	public boolean isShowAge() {
		return showAge;
	}
	public void setShowAge(boolean showAge) {
		this.showAge = showAge;
	}
	public boolean isShowArea() {
		return showArea;
	}
	public void setShowArea(boolean showArea) {
		this.showArea = showArea;
	}
	public boolean isShowBeganWork() {
		return showBeganWork;
	}
	public void setShowBeganWork(boolean showBeganWork) {
		this.showBeganWork = showBeganWork;
	}
	public boolean isShowEducation() {
		return showEducation;
	}
	public void setShowEducation(boolean showEducation) {
		this.showEducation = showEducation;
	}
	public boolean isShowGroupName() {
		return showGroupName;
	}
	public void setShowGroupName(boolean showGroupName) {
		this.showGroupName = showGroupName;
	}
	public boolean isShowHomePhone() {
		return showHomePhone;
	}
	public void setShowHomePhone(boolean showHomePhone) {
		this.showHomePhone = showHomePhone;
	}
	public boolean isShowMobilePhone() {
		return showMobilePhone;
	}
	public void setShowMobilePhone(boolean showMobilePhone) {
		this.showMobilePhone = showMobilePhone;
	}
	public boolean isShowSchool() {
		return showSchool;
	}
	public void setShowSchool(boolean showSchool) {
		this.showSchool = showSchool;
	}
	public boolean isShowTitle() {
		return showTitle;
	}
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}
	public boolean isShowWorkPhone() {
		return showWorkPhone;
	}
	public void setShowWorkPhone(boolean showWorkPhone) {
		this.showWorkPhone = showWorkPhone;
	}
	public boolean isShowImage() {
		return showImage;
	}
	public void setShowImage(boolean showImage) {
		this.showImage = showImage;
	}
	public String getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(String imageHeight) {
		this.imageHeight = imageHeight;
	}
	public String getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}
	public String getDefaultPhoto() {
		return defaultPhoto;
	}
	public void setDefaultPhoto(String defaultPhoto) {
		this.defaultPhoto = defaultPhoto;
	}
	public boolean isShowCompanyAddress() {
		return showCompanyAddress;
	}
	public void setShowCompanyAddress(boolean showCompanyAddress) {
		this.showCompanyAddress = showCompanyAddress;
	}
	public boolean isShowDateOfBirth() {
		return showDateOfBirth;
	}
	public void setShowDateOfBirth(boolean showDateOfBirth) {
		this.showDateOfBirth = showDateOfBirth;
	}
	public boolean isShowJob() {
		return showJob;
	}
	public void setShowJob(boolean showJob) {
		this.showJob = showJob;
	}
	public boolean isShowWorkplace() {
		return showWorkplace;
	}
	public void setShowWorkplace(boolean showWorkplace) {
		this.showWorkplace = showWorkplace;
	}
	public boolean isShowStatus() {
		return showStatus;
	}
	public void setShowStatus(boolean showStatus) {
		this.showStatus = showStatus;
	}
	public boolean isAddReflection() {
		return addReflection;
	}
	public void setAddReflection(boolean addReflection) {
		this.addReflection = addReflection;
	}
}
