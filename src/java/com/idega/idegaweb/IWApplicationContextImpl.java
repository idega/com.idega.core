package com.idega.idegaweb;

import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.data.IDOLookup;

/**
 * Title:        A default implementation of IWApplicationContext
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWApplicationContextImpl implements IWApplicationContext {
	private final static String IWAPP_CURRENT_DOMAIN_ID = "iw_current_domain_id";
	private IWMainApplication iwma;
	ICDomain domain;
	
	
	protected IWApplicationContextImpl(IWMainApplication superApp){
		this.iwma=superApp;	
	}



	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getIWMainApplication()
	 */
	public IWMainApplication getIWMainApplication() {
		return iwma;
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getApplicationSettings()
	 */
	public IWMainApplicationSettings getApplicationSettings() {
		return iwma.getSettings();
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getSystemProperties()
	 */
	public IWSystemProperties getSystemProperties() {
		return iwma.getSystemProperties();
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#setApplicationAttribute(String, Object)
	 */
	public void setApplicationAttribute(
		String attributeName,
		Object attributeValue) {
		iwma.setAttribute(attributeName,attributeValue);
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getApplicationAttribute(String)
	 */
	public Object getApplicationAttribute(String attributeName) {
		return iwma.getAttribute(attributeName);
	}
	
	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getApplicationAttribute(String,Object)
	 */
	public Object getApplicationAttribute(String attributeName, Object defaultObjectToReturnIfValueIsNull) {
		return iwma.getAttribute(attributeName,defaultObjectToReturnIfValueIsNull);
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#removeApplicationAttribute(String)
	 */
	public void removeApplicationAttribute(String attributeName) {
		iwma.removeAttribute(attributeName);
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getDomain()
	 */
	public ICDomain getDomain() {
		try {
			String id = (String) this.getApplicationAttribute(IWAPP_CURRENT_DOMAIN_ID);
			int domainID = 1;
			if (id != null) {
				try {
					domainID = Integer.parseInt(id);
				} catch (NumberFormatException nfe) {
				}
			}
			if(domain==null){
				ICDomainHome domainHome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);
				domain = domainHome.findByPrimaryKey(domainID);
			}
			return domain;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public boolean equals(Object o){
			if(o instanceof IWApplicationContext){
				IWApplicationContext iwac = (IWApplicationContext)o;
				return (this.getDomain().equals(iwac.getDomain()));	
			}
			return false;
	}

}
