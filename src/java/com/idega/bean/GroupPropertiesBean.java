package com.idega.bean;

public class GroupPropertiesBean extends PropertiesBean {
	
	private boolean showName = true;
	private boolean showHomePage = true;
	private boolean showDescription = true;
	private boolean showExtraInfo = true;
	private boolean showShortName = true;
	private boolean showPhone = true;
	private boolean showFax = true;
	private boolean showEmails = true;
	private boolean showAddress = true;
	private boolean showEmptyFields = true;
	
	public GroupPropertiesBean() {}
	
	public GroupPropertiesBean(PropertiesBean bean) {
		super(bean);
	}
	
	public boolean isShowAddress() {
		return showAddress;
	}
	public void setShowAddress(boolean showAddress) {
		this.showAddress = showAddress;
	}
	public boolean isShowDescription() {
		return showDescription;
	}
	public void setShowDescription(boolean showDescription) {
		this.showDescription = showDescription;
	}
	public boolean isShowEmails() {
		return showEmails;
	}
	public void setShowEmails(boolean showEmails) {
		this.showEmails = showEmails;
	}
	public boolean isShowEmptyFields() {
		return showEmptyFields;
	}
	public void setShowEmptyFields(boolean showEmptyFields) {
		this.showEmptyFields = showEmptyFields;
	}
	public boolean isShowExtraInfo() {
		return showExtraInfo;
	}
	public void setShowExtraInfo(boolean showExtraInfo) {
		this.showExtraInfo = showExtraInfo;
	}
	public boolean isShowFax() {
		return showFax;
	}
	public void setShowFax(boolean showFax) {
		this.showFax = showFax;
	}
	public boolean isShowHomePage() {
		return showHomePage;
	}
	public void setShowHomePage(boolean showHomePage) {
		this.showHomePage = showHomePage;
	}
	public boolean isShowName() {
		return showName;
	}
	public void setShowName(boolean showName) {
		this.showName = showName;
	}
	public boolean isShowPhone() {
		return showPhone;
	}
	public void setShowPhone(boolean showPhone) {
		this.showPhone = showPhone;
	}
	public boolean isShowShortName() {
		return showShortName;
	}
	public void setShowShortName(boolean showShortName) {
		this.showShortName = showShortName;
	}
}
