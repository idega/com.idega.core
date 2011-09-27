/**
 * $Id: RuntimeCachedDomain.java,v 1.1 2007/04/09 22:17:59 tryggvil Exp $
 * Created in 2007 by tryggvil
 *
 * Copyright (C) 2000-2007 Idega Software hf. All Rights Reserved.
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

import com.idega.data.IDOEntity;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOStoreException;
import com.idega.util.LocaleUtil;

/**
 * <p>
 * Non-persistent instance of ICDomain that is only used to store runtime set variables such as 
 * serverName, contextPath etc.
 * </p>
 *  Last modified: $Date: 2007/04/09 22:17:59 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class RuntimeCachedDomain implements ICDomain{
	
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
	private String defaultLocaleString;
	private String type;
	private String serverAliases;
	
	public String getDefaultLocaleString() {
		return defaultLocaleString;
	}
	public void setDefaultLocaleString(String defaultLocaleString) {
		this.defaultLocaleString = defaultLocaleString;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public ICPage getIBPage() {
		return iBPage;
	}
	public void setIBPage(ICPage page) {
		iBPage = page;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(Object primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getServerContextPath() {
		return serverContextPath;
	}
	public void setServerContextPath(String serverContextPath) {
		this.serverContextPath = serverContextPath;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public String getServerProtocol() {
		return serverProtocol;
	}
	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}
	public ICPage getStartPage() {
		return startPage;
	}
	public void setStartPage(ICPage startPage) {
		this.startPage = startPage;
	}
	public int getStartPageID() {
		return startPageID;
	}
	public void setStartPageID(int startPageID) {
		this.startPageID = startPageID;
	}
	public ICPage getStartTemplate() {
		return startTemplate;
	}
	public void setStartTemplate(ICPage startTemplate) {
		this.startTemplate = startTemplate;
	}
	public int getStartTemplateID() {
		return startTemplateID;
	}
	public void setStartTemplateID(int startTemplateID) {
		this.startTemplateID = startTemplateID;
	}
	public Collection getTopLevelGroupsUnderDomain() {
		return topLevelGroupsUnderDomain;
	}
	public void setTopLevelGroupsUnderDomain(Collection topLevelGroupsUnderDomain) {
		this.topLevelGroupsUnderDomain = topLevelGroupsUnderDomain;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getURL() {
		return uRL;
	}
	public void setURL(String url) {
		uRL = url;
	}
	public String getURLWithoutLastSlash() {
		return uRLWithoutLastSlash;
	}
	public void setURLWithoutLastSlash(String withoutLastSlash) {
		uRLWithoutLastSlash = withoutLastSlash;
	}
	/**
	 * @return Returns the iD.
	 */
	public int getID() {
		Integer pk = (Integer)getPrimaryKey();
		return pk.intValue();
	}
	public boolean isDefaultDomain() {
		String type = getType();
		if(type!=null&&type.equals(TYPE_DEFAULT)){
			return true;
		}
		return false;
	}

	public Locale getDefaultLocale() {
		return LocaleUtil.getLocale(getDefaultLocaleString());
	}
	public void setDefaultLocale(Locale locale) {
		if(locale!=null){
			setDefaultLocaleString(locale.toString());
		}
	}
	public Integer decode(String pkString) {
		// TODO Auto-generated method stub
		return null;
	}
	public Collection<Integer> decode(String[] pkString) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getDatasource() {
		// TODO Auto-generated method stub
		return null;
	}
	public IDOEntityDefinition getEntityDefinition() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setDatasource(String datasource) {
		// TODO Auto-generated method stub
		
	}
	public void store() throws IDOStoreException {
		// TODO Auto-generated method stub
		
	}
	public EJBLocalHome getEJBLocalHome() throws EJBException {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isIdentical(EJBLocalObject arg0) throws EJBException {
		// TODO Auto-generated method stub
		return false;
	}
	public void remove() throws RemoveException, EJBException {
		// TODO Auto-generated method stub
		
	}
	public int compareTo(IDOEntity o) {
		return 0;
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
