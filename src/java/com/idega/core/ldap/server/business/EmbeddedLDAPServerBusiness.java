/*
 * Created on Aug 16, 2004
 */
package com.idega.core.ldap.server.business;

import java.io.IOException;
import java.util.Properties;



/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface EmbeddedLDAPServerBusiness {
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
	public void storeProperties(Properties props, String pathToSettingsFile)
			throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#loadProperties
	 */
	public Properties loadProperties(String pathToSettingsFile)
			throws IOException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getLDAPSettings
	 */
	public Properties getLDAPSettings() throws IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getBackendSettings
	 */
	public Properties getBackendSettings() throws IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#storeLDAPProperties
	 */
	public void storeLDAPProperties() throws IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#storeBackendProperties
	 */
	public void storeBackendProperties() throws IOException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.server.business.EmbeddedLDAPServerBusinessBean#getPropertyAndCreateIfDoesNotExist
	 */
	public String getPropertyAndCreateIfDoesNotExist(Properties properties,
			String key, String defaultValue) throws java.rmi.RemoteException;

}
