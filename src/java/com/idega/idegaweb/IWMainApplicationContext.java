package com.idega.idegaweb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.core.builder.business.ICDomainLookup;
import com.idega.core.builder.data.CachedDomain;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;

/**
 * Title:        A default implementation of IWApplicationContext
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWMainApplicationContext implements IWApplicationContext {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 5152712252892799540L;
	
	
	//private final static String IWAPP_CURRENT_DOMAIN_ID = "iw_current_domain_id";
	private IWMainApplication iwma;
	//ICDomain domain;
	
	protected IWMainApplicationContext(IWMainApplication superApp){
		this.iwma=superApp;	
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getIWMainApplication()
	 */
	public IWMainApplication getIWMainApplication() {
		return this.iwma;
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getApplicationSettings()
	 */
	public IWMainApplicationSettings getApplicationSettings() {
		return this.iwma.getSettings();
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getSystemProperties()
	 */
	public IWSystemProperties getSystemProperties() {
		return this.iwma.getSystemProperties();
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#setApplicationAttribute(String, Object)
	 */
	public void setApplicationAttribute(
		String attributeName,
		Object attributeValue) {
		this.iwma.setAttribute(attributeName,attributeValue);
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getApplicationAttribute(String)
	 */
	public Object getApplicationAttribute(String attributeName) {
		return this.iwma.getAttribute(attributeName);
	}
	
	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getApplicationAttribute(String,Object)
	 */
	public Object getApplicationAttribute(String attributeName, Object defaultObjectToReturnIfValueIsNull) {
		return this.iwma.getAttribute(attributeName,defaultObjectToReturnIfValueIsNull);
	}

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#removeApplicationAttribute(String)
	 */
	public void removeApplicationAttribute(String attributeName) {
		this.iwma.removeAttribute(attributeName);
	}


	/*
	public ICDomain getPersistentDomainByServerName(String cacheKey, String serverName) {
		//boolean cachDefaultDomainForThisServerURL = false;
		try {
			//if(serverName!=null && !"".equals(serverName)){
				ICDomain toReturn = (ICDomain)this.domainMap.get(serverName);
				if(toReturn==null){
					ICDomainHome domainHome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);
					ICDomain domain = domainHome.findDomainByServernameOrDefault(serverName);
					//Collection coll = domainHome.findAllDomainsByServerName(serverName);
					//Iterator iter = coll.iterator();
					//if (iter.hasNext()) {
						
						toReturn = domain;
						this.domainMap.put(serverName,toReturn);
						return toReturn;
					//} else {
						//System.out.println("Couldn't find domain record for ServerName : "+ serverName);
						//cachDefaultDomainForThisServerURL=true;
					//}
				} else {
					return toReturn;
				}

		} catch (IDOLookupException e1) {
			e1.printStackTrace();
		} catch (FinderException e1) {
			//e1.printStackTrace();
			System.out.println("Couldn't find domain record for ServerName : "+ serverName);
			//cachDefaultDomainForThisServerURL=true;
		}
		
		try {
			if(this.domain==null){
				ICDomainHome domainHome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);
				ICDomain realDomain = domainHome.findFirstDomain();
				//ICDomain realDomain = domainHome.findByPrimaryKey(domainID);
				//domain = new CachedDomain(realDomain);
				this.domain= realDomain;
			}
			
			if(cachDefaultDomainForThisServerURL){
				this.domainMap.put(serverName,this.domain);
			}
			
			return this.domain;
		} catch (Exception e) {
			try {
				ICDomainHome domainHome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);
				ICDomain realDomain = domainHome.findFirstDomain();
				this.domain= realDomain;
				
				return this.domain;
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}

			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}*/
	
	public boolean equals(Object o){
			if(o instanceof IWApplicationContext){
				IWApplicationContext iwac = (IWApplicationContext)o;
				return (this.getDomain().equals(iwac.getDomain()));	
			}
			return false;
	}

	protected ICDomainLookup getDomainLookup(){
		return ICDomainLookup.getInstance();
	}
	
	public ICDomain getDomain() {
		return getDomainLookup().getDefaultDomain();
	}

	public ICDomain getDomainByServerName(String serverName) {
		return getDomainLookup().getDomainByServerName(serverName);
	}

}