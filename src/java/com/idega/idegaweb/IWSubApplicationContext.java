package com.idega.idegaweb;

import com.idega.core.builder.data.ICDomain;

public class IWSubApplicationContext extends IWMainApplicationContext implements IWApplicationContext{

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4456471343352559694L;

	public IWSubApplicationContext(IWSubApplication application) {
		super(application);
	}

	public Object getApplicationAttribute(String attributeName) {
		return super.getApplicationAttribute(attributeName);
	}

	public Object getApplicationAttribute(String attributeName,
			Object defaultObjectToReturnIfValueIsNull) {
		return super.getApplicationAttribute(attributeName,defaultObjectToReturnIfValueIsNull);
	}

	public IWMainApplicationSettings getApplicationSettings() {
		return super.getApplicationSettings();
	}

	public ICDomain getDomain() {
		return getDomainByServerName(getIWSubApplication().getDomainName());
	}

	public ICDomain getDomainByServerName(String serverName) {
		return super.getDomainByServerName(serverName);
	}

	public IWMainApplication getIWMainApplication() {
		return super.getIWMainApplication();
	}

	public IWSubApplication getIWSubApplication() {
		return (IWSubApplication)getIWMainApplication();
	}

	
	public IWSystemProperties getSystemProperties() {
		return super.getSystemProperties();
	}

	public void removeApplicationAttribute(String attributeName) {
		super.removeApplicationAttribute(attributeName);
	}



	public void setApplicationAttribute(String attributeName,
			Object attributeValue) {
		super.setApplicationAttribute(attributeName, attributeValue);
	}

}
