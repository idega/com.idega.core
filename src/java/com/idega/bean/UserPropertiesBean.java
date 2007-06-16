package com.idega.bean;

public class UserPropertiesBean extends PropertiesBean {
	
	private boolean showGroupName = true;
	private boolean showTitle = true;
	private boolean showAge = true;
	private boolean showWorkPhone = true;
	private boolean showHomePhone = true;
	private boolean showMobilePhone = true;
	private boolean showEmails = true;
	private boolean showEducation = true;
	private boolean showSchool = true;
	private boolean showArea = true;
	private boolean showBeganWork = true;
	private boolean showImage = true;
	
	private String imageWidth = null;
	private String imageHeight = null;
	
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
	public boolean isShowEmails() {
		return showEmails;
	}
	public void setShowEmails(boolean showEmails) {
		this.showEmails = showEmails;
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
	
}
