/*
 * $Id: EmbeddedLDAPServerBusiness.java,v 1.2 2004/09/21 18:57:59 eiki Exp $
 * Created on Sep 21, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
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
 *  Last modified: $Date: 2004/09/21 18:57:59 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public interface EmbeddedLDAPServerBusiness extends IBOService, EmbeddedLDAPServerConstants {

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getPathToLDAPConfigFiles
	 */
	public String getPathToLDAPConfigFiles() throws java.rmi.RemoteException;

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
