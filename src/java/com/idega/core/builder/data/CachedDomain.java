/*
 * $Id: CachedDomain.java,v 1.4 2007/12/10 00:16:21 eiki Exp $
 * Created on 20.3.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.data;

import java.util.Collection;
import java.util.Locale;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.RemoveException;
import javax.servlet.http.HttpServletRequest;

import com.idega.core.builder.business.ICDomainLookup;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOLookup;
import com.idega.data.IDONoDatastoreError;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.LocaleUtil;
import com.idega.util.RequestUtil;


/**
 * <p>
 * Implementation of ICDomain that is cached in the application on run-time
 * and stored as an attribute inside IWMainApplicationContext
 * </p>
 *  Last modified: $Date: 2007/12/10 00:16:21 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public class CachedDomain implements ICDomain {

	static final String SLASH = "/";
	
	//cached attributes:
	private boolean hasInitializedCachedAttribute=false;
	private boolean startOnWorkspace=false;	

	private ICDomain runtimeCachedDomainInstance;
	
	//private int startTemplateID;
	private String name;
	//private int startPageID;
	private Collection topLevelGroupsUnderDomain;
	private String uRL;
	private String uRLWithoutLastSlash;
	private ICPage startTemplate;
	private String domainName;
	private ICPage startPage;
	private String serverName;
	private int serverPort=-1;
	private String serverContextPath;
	private String serverProtocol;
	private String uniqueId;
	private Object primaryKey;
	private String defaultLocaleString;
	private String type;
	private String serverAliases;

	
	/**
	 * 
	 */
	public CachedDomain(ICDomain domain) {
		copyDomainData(domain,this);
	}

	private void copyDomainData(ICDomain fromDomain,ICDomain toDomain) {
		
		toDomain.setName(fromDomain.getName());
		
		toDomain.setStartTemplate(fromDomain.getStartTemplate());
		toDomain.setIBPage(fromDomain.getStartPage());
		toDomain.setDomainName(fromDomain.getDomainName());
		if(fromDomain instanceof CachedDomain){
			CachedDomain fromCachedDomain = (CachedDomain)fromDomain;
			//These properties ar handled specially by CachedDomain because
			//they are cached in runtime and should not by default be written down
			toDomain.setURL(fromCachedDomain.uRL);
			toDomain.setServerName(fromCachedDomain.serverName);
			int port = fromCachedDomain.serverPort;
			if(port!=-1){
				toDomain.setServerPort(port);
			}
			toDomain.setServerContextPath(fromCachedDomain.serverContextPath);
			toDomain.setServerProtocol(fromCachedDomain.serverProtocol);
		}
		else{
			toDomain.setURL(fromDomain.getURL());
			toDomain.setServerName(fromDomain.getServerName());
			toDomain.setServerPort(fromDomain.getServerPort());
			toDomain.setServerContextPath(fromDomain.getServerContextPath());
			toDomain.setServerProtocol(fromDomain.getServerProtocol());
		}
		toDomain.setDefaultLocaleString(fromDomain.getDefaultLocaleString());
		toDomain.setDefaultLocale(fromDomain.getDefaultLocale());
		toDomain.setType(fromDomain.getType());
		toDomain.setServerAliases(fromDomain.getServerAliases());
		
		if(toDomain instanceof CachedDomain){
			
			CachedDomain toCachedDomain = (CachedDomain)toDomain;
			//toCachedDomain.setStartTemplateID(fromDomain.getStartTemplateID());
			//toCachedDomain.setStartPageID(fromDomain.getStartPageID());
			try{
				toCachedDomain.setTopLevelGroupsUnderDomain(fromDomain.getTopLevelGroupsUnderDomain());
			}
			catch(Exception e){
				e.printStackTrace();
			}
			toCachedDomain.setURLWithoutLastSlash(fromDomain.getURLWithoutLastSlash());
			toCachedDomain.setUniqueId(fromDomain.getUniqueId());
			toCachedDomain.setPrimaryKey(fromDomain.getPrimaryKey());
			
		}
		else if(toDomain instanceof ICDomainBMPBean){
			/*ICDomainBMPBean toPersistentDomain = (ICDomainBMPBean)toDomain;
			toPersistentDomain.setStartTemplateID(fromDomain.getStartTemplateID());
			toPersistentDomain.setStartPageID(fromDomain.getStartPageID());
			try{
				toPersistentDomain.setTopLevelGroupsUnderDomain(fromDomain.getTopLevelGroupsUnderDomain());
			}
			catch(Exception e){
				e.printStackTrace();
			}
			toPersistentDomain.setURLWithoutLastSlash(fromDomain.getURLWithoutLastSlash());
			toPersistentDomain.setUniqueId(fromDomain.getUniqueId());
			toPersistentDomain.setPrimaryKey(fromDomain.getPrimaryKey());
			toPersistentDomain.setStartTemplate(fromDomain.getStartPage());
			*/
		}
		
	}


	
	/**
	 * @return Returns the domainName.
	 */
	public String getDomainName() {
		return this.domainName;
	}


	
	/**
	 * @param domainName The domainName to set.
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}


	
	
	/**
	 * @param page The iBPage to set.
	 */
	public void setIBPage(ICPage page) {
		setStartPage(page);
	}


	
	/**
	 * @return Returns the iD.
	 */
	public int getID() {
		Integer pk = (Integer)getPrimaryKey();
		return pk.intValue();
	}

	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}


	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}


	
	/**
	 * @return Returns the serverContextPath.
	 */
	public String getServerContextPath() {
		if(this.serverContextPath==null||this.serverContextPath.equals("")){
			return getRuntimeCachedDomainInstance().getServerContextPath();
		}
		return this.serverContextPath;
	}


	
	/**
	 * @param serverContextPath The serverContextPath to set.
	 */
	public void setServerContextPath(String serverContextPath) {
		this.serverContextPath = serverContextPath;
	}


	
	/**
	 * @return Returns the serverName.
	 */
	public String getServerName() {
		if(this.serverName==null||this.serverName.equals("")){
			return getRuntimeCachedDomainInstance().getServerName();
		}
		return this.serverName;
	}


	
	/**
	 * @param serverName The serverName to set.
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}


	
	/**
	 * @return Returns the serverPort.
	 */
	public int getServerPort() {
		if(this.serverPort==-1){
			return getRuntimeCachedDomainInstance().getServerPort();
		}
		return this.serverPort;
	}


	
	/**
	 * @param serverPort The serverPort to set.
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}


	
	/**
	 * @return Returns the serverProtocol.
	 */
	public String getServerProtocol() {
		if(this.serverProtocol==null||this.serverProtocol.equals("")){
			return getRuntimeCachedDomainInstance().getServerProtocol();
		}
		return this.serverProtocol;
	}


	
	/**
	 * @param serverProtocol The serverProtocol to set.
	 */
	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}


	
	/**
	 * @return Returns the startPage.
	 */
	public ICPage getStartPage() {
		return this.startPage;
	}


	
	/**
	 * @param startPage The startPage to set.
	 */
	public void setStartPage(ICPage startPage) {
		this.startPage = startPage;
	}


	
	/**
	 * @return Returns the startPageID.
	 */
	public int getStartPageID() {
		ICPage startPage = getStartPage();
		if(startPage!=null){
			Integer startPageId = (Integer) startPage.getPrimaryKey();
			if(startPageId!=null){
				return startPageId.intValue();
			}
		}
		return -1;
	}


	
	/**
	 * @param startPageID The startPageID to set.
	 */
	public void setStartPageID(int startPageID) {
		try{
			ICPageHome pageHome = (ICPageHome) IDOLookup.getHome(ICPage.class);
			ICPage page = pageHome.findByPrimaryKey(startPageID);
			setStartPage(page);
		}
		catch(Exception e){}
	}


	
	/**
	 * @return Returns the startTemplate.
	 */
	public ICPage getStartTemplate() {
		return this.startTemplate;
	}


	
	/**
	 * @param startTemplate The startTemplate to set.
	 */
	public void setStartTemplate(ICPage startTemplate) {
		this.startTemplate = startTemplate;
	}


	
	/**
	 * @return Returns the startTemplateID.
	 */
	public int getStartTemplateID() {
		ICPage startPage = getStartTemplate();
		if(startPage!=null){
			Integer startPageId = (Integer) startPage.getPrimaryKey();
			if(startPageId!=null){
				return startPageId.intValue();
			}
		}
		return -1;
	}


	
	/**
	 * @param startTemplateID The startTemplateID to set.
	 */
	public void setStartTemplateID(int startTemplateID) {
		try{
			ICPageHome pageHome = (ICPageHome) IDOLookup.getHome(ICPage.class);
			ICPage page = pageHome.findByPrimaryKey(startTemplateID);
			setStartTemplate(page);
		}
		catch(Exception e){}
	}


	
	/**
	 * @return Returns the topLevelGroupsUnderDomain.
	 */
	public Collection getTopLevelGroupsUnderDomain() {
		return this.topLevelGroupsUnderDomain;
	}


	
	/**
	 * @param topLevelGroupsUnderDomain The topLevelGroupsUnderDomain to set.
	 */
	public void setTopLevelGroupsUnderDomain(Collection topLevelGroupsUnderDomain) {
		this.topLevelGroupsUnderDomain = topLevelGroupsUnderDomain;
	}


	
	/**
	 * @return Returns the uniqueId.
	 */
	public String getUniqueId() {
		return this.uniqueId;
	}


	
	/**
	 * @param uniqueId The uniqueId to set.
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}


	
	/**
	 * @return Returns the uRL.
	 */
	public String getURL() {
		if(this.uRL==null||this.uRL.equals("")){
			return getRuntimeCachedDomainInstance().getURL();
		}
		return this.uRL;
	}


	
	/**
	 * @param url The uRL to set.
	 */
	public void setURL(String url) {
		this.uRL = url;
	}


	
	/**
	 * @return Returns the uRLWithoutLastSlash.
	 */
	public String getURLWithoutLastSlash() {
		return this.uRLWithoutLastSlash;
	}


	
	/**
	 * @param withoutLastSlash The uRLWithoutLastSlash to set.
	 */
	public void setURLWithoutLastSlash(String withoutLastSlash) {
		this.uRLWithoutLastSlash = withoutLastSlash;
	}



	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#store()
	 */
	public void store() throws IDOStoreException {
		//throw new UnsupportedOperationException("method store() not implemented in CacedDomain");
		
		ICDomain persistentDomain = ICDomainLookup.getInstance().getPersistentDomainByServerName(getServerName());
		
		copyDomainData(this, persistentDomain);
		
		persistentDomain.store();
		
	}



	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#getEntityDefinition()
	 */
	public IDOEntityDefinition getEntityDefinition() {
		throw new UnsupportedOperationException("method getEntityDefinition() not implemented in CacedDomain");
	}



	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#decode(java.lang.String)
	 */
	public Object decode(String pkString) {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#decode(java.lang.String[])
	 */
	public Collection decode(String[] pkString) {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#getDatasource()
	 */
	public String getDatasource() {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#setDatasource(java.lang.String)
	 */
	public void setDatasource(String datasource) {
		// TODO Auto-generated method stub
		
	}



	/* (non-Javadoc)
	 * @see javax.ejb.EJBLocalObject#getEJBLocalHome()
	 */
	public EJBLocalHome getEJBLocalHome() throws EJBException {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see javax.ejb.EJBLocalObject#getPrimaryKey()
	 */
	public Object getPrimaryKey() throws EJBException {
		return this.primaryKey;
	}



	/* (non-Javadoc)
	 * @see javax.ejb.EJBLocalObject#remove()
	 */
	public void remove() throws RemoveException, EJBException {
		throw new UnsupportedOperationException("method remove() not implemented in CacedDomain");
	}



	/* (non-Javadoc)
	 * @see javax.ejb.EJBLocalObject#isIdentical(javax.ejb.EJBLocalObject)
	 */
	public boolean isIdentical(EJBLocalObject arg0) throws EJBException {
		// TODO Auto-generated method stub
		return false;
	}



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean equals(Object o){
		if(o instanceof ICDomain){
			ICDomain domain = (ICDomain)o;
			return (domain.getPrimaryKey().equals(domain.getPrimaryKey()));	
		}
		return false;
}



	
	/**
	 * @param primaryKey The primaryKey to set.
	 */
	public void setPrimaryKey(Object primaryKey) {
		this.primaryKey = primaryKey;
	}



	public Locale getDefaultLocale() {
		String defaultLocaleString = getDefaultLocaleString();
		if(defaultLocaleString!=null){
			return LocaleUtil.getLocale(defaultLocaleString);	
		}
		return null;
	}



	public String getDefaultLocaleString() {
		return defaultLocaleString;
	}



	public void setDefaultLocale(Locale locale) {
		if(locale!=null){
			setDefaultLocaleString(locale.toString());
		}
	}



	public void setDefaultLocaleString(String serverName) {
		this.defaultLocaleString=serverName;
	}



	public boolean isHasInitializedCachedAttribute() {
		return hasInitializedCachedAttribute;
	}



	public void setHasInitializedCachedAttribute(
			boolean hasInitializedCachedAttribute) {
		this.hasInitializedCachedAttribute = hasInitializedCachedAttribute;
	}


	public boolean isStartOnWorkspace() {
		return startOnWorkspace;
	}

	public void setStartOnWorkspace(boolean startOnWorkspace) {
		this.startOnWorkspace = startOnWorkspace;
	}



	public void initializeCachedInfo(HttpServletRequest request) {
		// TODO Auto-generated method stub
		initializeCachedDomainInfo(request);
		initStartPageInfo(request);
		setHasInitializedCachedAttribute(true);
	}

	/**
	 * Initialized data that may be only known at request time and from the request.
	 * Things such as context path, hostnames ports etc.
	 * @param iwc
	 */
	protected void initializeCachedDomainInfo(HttpServletRequest request ){

		String contextPath = request.getContextPath();
		String serverProtocol = request.getScheme();
		ICDomain domain = getRuntimeCachedDomainInstance();
    		//String setServerName = domain.getServerName();
    		//String setUrl = domain.getURL();
    		//String setContextPath = domain.getServerContextPath();
    		//int setPort = domain.getServerPort();
    		//String setProtocol = domain.getServerProtocol();
    		//if(setServerName==null||setServerName.equals("")){
    			String newServerName = request.getServerName();
    			domain.setServerName(newServerName);
    		//}
    		//if(setUrl==null||setUrl.equals("")){
    			String newServerURL = RequestUtil.getServerURL(request);
    			domain.setURL(newServerURL);
    		//}
    		//if(setContextPath==null||setContextPath.equals("")){
    			
    	        if (contextPath != null) {
    	            if (!contextPath.startsWith(SLASH)) {
    	            	contextPath = SLASH + contextPath;
    	            }
    	        } else {
    	        		contextPath = SLASH;
    	        }
    			
    			domain.setServerContextPath(contextPath);
    		//}
    		//if(setPort==-1){
    			int port = request.getServerPort();
    			if(port!=80){
    				domain.setServerPort(port);
    		//	}
    		}
    		//if(setProtocol==null||setProtocol.equals("")){
    			domain.setServerProtocol(serverProtocol);
    		//}
	}
	
	/**
	 * <p>
	 * Gets an instance that is used to store runtime transient data that is not persisted to the database.
	 * </p>
	 * @return
	 */
	private ICDomain getRuntimeCachedDomainInstance() {
		if(runtimeCachedDomainInstance==null){
			runtimeCachedDomainInstance= new RuntimeCachedDomain();
		}
		return runtimeCachedDomainInstance;
	}



	private void initStartPageInfo(HttpServletRequest request) {
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		
		try {
			//BuilderService bService = (BuilderService)IBOLookup.getServiceInstance(iwma.getIWApplicationContext(),BuilderService.class);
			//ICPage rootPage = bService.getRootPage();
			
			ICPage rootPage = getStartPage();
			//set the filter to forward to /pages if there is a rootPage created
			boolean startOnWorkspace=false;
			if(rootPage==null){
				startOnWorkspace=true;
			}
			
			/*String serverName = request.getServerName();
			int port = request.getLocalPort();
			if(port!=80){
				serverName += ":"+port;
			}
			iwma.getIWApplicationContext().getDomain().setServerName(serverName);*/
			//IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
			//This sets the domain by default:
			//iwc.getDomain();
			
			setStartOnWorkspace(startOnWorkspace);
			
			//initializeDefaultDomain(request);
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		catch (IDONoDatastoreError de) {
			if(!iwma.isInDatabaseLessMode()){
				de.printStackTrace();
			}
		}
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isDefaultDomain() {
		String type = getType();
		if(type!=null&&type.equals(TYPE_DEFAULT)){
			return true;
		}
		return false;
	}

	/**
	 * @return Returns the serverAliases.
	 */
	public String getServerAliases() {
		return serverAliases;
	}

	/**
	 * @param serverAliases The serverAliases to set.
	 */
	public void setServerAliases(String serverAliases) {
		this.serverAliases = serverAliases;
	}
}
