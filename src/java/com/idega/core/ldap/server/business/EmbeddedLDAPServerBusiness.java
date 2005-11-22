/*
 * $Id: EmbeddedLDAPServerBusiness.java,v 1.3 2005/11/22 18:25:58 eiki Exp $
 * Created on Nov 22, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.server.business;

import java.io.IOException;
import java.util.Properties;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.business.IBOService;


/**
 * 
 *  Last modified: $Date: 2005/11/22 18:25:58 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface EmbeddedLDAPServerBusiness extends IBOService, EmbeddedLDAPServerConstants {

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getPathToLDAPConfigFiles
	 */
	public String getPathToLDAPConfigFiles() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#setPathToLDAPConfigFiles
	 */
	public void setPathToLDAPConfigFiles(String path) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#startEmbeddedLDAPServer
	 */
	public boolean startEmbeddedLDAPServer() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#stopEmbeddedLDAPServer
	 */
	public boolean stopEmbeddedLDAPServer() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#isServerStarted
	 */
	public boolean isServerStarted() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#storeProperties
	 */
	public void storeProperties(Properties props, String pathToSettingsFile) throws IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#loadProperties
	 */
	public Properties loadProperties(String pathToSettingsFile) throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getLDAPSettings
	 */
	public Properties getLDAPSettings() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getBackendSettings
	 */
	public Properties getBackendSettings() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#storeLDAPProperties
	 */
	public void storeLDAPProperties() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#storeBackendProperties
	 */
	public void storeBackendProperties() throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getPropertyAndCreateIfDoesNotExist
	 */
	public String getPropertyAndCreateIfDoesNotExist(Properties properties, String key, String defaultValue)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getRootDN
	 */
	public DirectoryString getRootDN() throws java.rmi.RemoteException;
}
