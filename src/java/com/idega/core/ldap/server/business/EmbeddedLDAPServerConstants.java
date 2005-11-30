/*
 * Created on Aug 16, 2004
 */
package com.idega.core.ldap.server.business;

/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface EmbeddedLDAPServerConstants {
	public static final String PROPS_BACKEND_ZERO_ROOT = "backend.0.root";

	public static final String PROPS_JAVALDAP_SERVER_NAME = "javaldap.server.name";
	public static final String PROPS_JAVALDAP_SERVER_PORT  = "javaldap.server.port";
	public static final String PROPS_JAVALDAP_WEBSERVICE_PORT  = "idegaweb.webservice.port";
	public static final String PROPS_JAVALDAP_SERVER_THREADS = "javaldap.server.threads";
	public static final String PROPS_JAVALDAP_DEBUG = "javaldap.debug";
	public static final String PROPS_JAVALDAP_ROOTUSER = "javaldap.rootuser";
	public static final String PROPS_JAVALDAP_ROOTPW = "javaldap.rootpw";
	public static final String PROPS_JAVALDAP_AUTO_START = "idegaweb.ldap.autostart";
	public static final String PROPS_JAVALDAP_PROPS_REAL_PATH = "ldap.properties.real.path";
}
