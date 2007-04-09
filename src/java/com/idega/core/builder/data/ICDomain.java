package com.idega.core.builder.data;


import java.util.Locale;
import com.idega.data.IDORelationshipException;
import java.util.Collection;
import javax.ejb.FinderException;
import java.rmi.RemoteException;
import com.idega.data.IDOEntity;

public interface ICDomain extends IDOEntity {
	
	public static final String TYPE_DEFAULT="default";
	public static final String TYPE_SUBDOMAIN="subdomain";
	
	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getStartPage
	 */
	public ICPage getStartPage();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getStartPageID
	 */
	public int getStartPageID();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getStartTemplate
	 */
	public ICPage getStartTemplate();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getStartTemplateID
	 */
	public int getStartTemplateID();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getDomainName
	 */
	public String getDomainName();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setDomainName
	 */
	public void setDomainName(String domainName);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getURL
	 */
	public String getURL();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setURL
	 */
	public void setURL(String url);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getTopLevelGroupsUnderDomain
	 */
	public Collection getTopLevelGroupsUnderDomain() throws IDORelationshipException, RemoteException, FinderException;

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setIBPage
	 */
	public void setIBPage(ICPage page);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setStartTemplate
	 */
	public void setStartTemplate(ICPage template);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setServerName
	 */
	public void setServerName(String serverName);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getServerName
	 */
	public String getServerName();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setServerPort
	 */
	public void setServerPort(int serverPort);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getServerPort
	 */
	public int getServerPort();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setServerContextPath
	 */
	public void setServerContextPath(String serverContextPath);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getServerContextPath
	 */
	public String getServerContextPath();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setServerProtocol
	 */
	public void setServerProtocol(String serverProtocol);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getServerProtocol
	 */
	public String getServerProtocol();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setDefaultLocale
	 */
	public void setDefaultLocale(Locale locale);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setDefaultLocaleString
	 */
	public void setDefaultLocaleString(String serverName);

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getDefaultLocaleString
	 */
	public String getDefaultLocaleString();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getDefaultLocale
	 */
	public Locale getDefaultLocale();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getURLWithoutLastSlash
	 */
	public String getURLWithoutLastSlash();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getUniqueId
	 */
	public String getUniqueId();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#getType
	 */
	public String getType();

	/**
	 * @see com.idega.core.builder.data.ICDomainBMPBean#setType
	 */
	public void setType(String type);

	public int getID();

	public boolean isDefaultDomain();
	
	public String getServerAliases();
	
	public void setServerAliases(String aliases);
}