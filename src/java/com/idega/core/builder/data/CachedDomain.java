/*
 * $Id: CachedDomain.java,v 1.2 2006/04/09 12:13:15 laddi Exp $
 * Created on 20.3.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.data;

import java.util.Collection;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.RemoveException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOStoreException;


/**
 * <p>
 * Implementation of ICDomain that is cached in the application on run-time
 * and stored as an attribute inside IWMainApplicationContext
 * </p>
 *  Last modified: $Date: 2006/04/09 12:13:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class CachedDomain implements ICDomain {

	private int startTemplateID;
	private String name;
	private int startPageID;
	private Collection topLevelGroupsUnderDomain;
	private String uRL;
	private String uRLWithoutLastSlash;
	private ICPage startTemplate;
	private String domainName;
	private ICPage iBPage;
	private ICPage startPage;
	private String serverName;
	private int serverPort;
	private String serverContextPath;
	private String serverProtocol;
	private String uniqueId;
	private Object primaryKey;
	
	
	/**
	 * 
	 */
	public CachedDomain(ICDomain domain) {
		setStartTemplateID(domain.getStartTemplateID());
		setName(domain.getName());
		setStartPageID(domain.getStartPageID());
		try{
			setTopLevelGroupsUnderDomain(domain.getTopLevelGroupsUnderDomain());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		setURL(domain.getURL());
		setURLWithoutLastSlash(domain.getURLWithoutLastSlash());
		setStartTemplate(domain.getStartTemplate());
		setDomainName(domain.getDomainName());
		setStartPage(domain.getStartPage());
		setServerName(domain.getServerName());
		setServerPort(domain.getServerPort());
		setServerContextPath(domain.getServerContextPath());
		setServerProtocol(domain.getServerProtocol());
		setUniqueId(domain.getUniqueId());
		setPrimaryKey(domain.getPrimaryKey());
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
	 * @return Returns the iBPage.
	 */
	public ICPage getIBPage() {
		return this.iBPage;
	}


	
	/**
	 * @param page The iBPage to set.
	 */
	public void setIBPage(ICPage page) {
		this.iBPage = page;
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
		return this.startPageID;
	}


	
	/**
	 * @param startPageID The startPageID to set.
	 */
	public void setStartPageID(int startPageID) {
		this.startPageID = startPageID;
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
		return this.startTemplateID;
	}


	
	/**
	 * @param startTemplateID The startTemplateID to set.
	 */
	public void setStartTemplateID(int startTemplateID) {
		this.startTemplateID = startTemplateID;
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
		throw new UnsupportedOperationException("method store() not implemented in CacedDomain");
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
}
