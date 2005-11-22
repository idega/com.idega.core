/*
 * Created on Aug 16, 2004
 */
package com.idega.core.ldap.replication.business;

/**
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface LDAPReplicationConstants {
	public static final String REPLICATION_PROPS_FILE_NAME = "replication.prop";
	
	public static final String PROPS_REPLICATOR_PREFIX = "replicator.";
	public static final String PROPS_REPLICATION_NUM = "replicators.num";
	public static final String PROPS_REPLICATOR_BASE_RDN = ".base.rdn";
	public static final String PROPS_REPLICATOR_BASE_UNIQUE_ID = ".base.unique.id";
	public static final String PROPS_REPLICATOR_HOST= ".host";
	public static final String PROPS_REPLICATOR_PORT = ".port";
	public static final String PROPS_REPLICATOR_REPLICATE_BASE_RDN = ".replicate.base.rdn";
	public static final String PROPS_REPLICATOR_LAST_REPLICATED = ".last.replicated";
	public static final String PROPS_REPLICATOR_NEXT_REPLICATED = ".next.replication";
	public static final String PROPS_REPLICATOR_INTERVAL_MINUTES = ".replication.interval.minutes";
	public static final String PROPS_REPLICATOR_SCHEDULER_STRING = ".scheduler.string";
	public static final String PROPS_REPLICATOR_SEARCH_TIMEOUT_MS = ".search.timeout.milliseconds";
	public static final String PROPS_REPLICATOR_SEARCH_ENTRY_LIMIT= ".returned.entries.limit";
	public static final String PROPS_REPLICATOR_MATCH_BY_UNIQUE_ID= ".match.by.uniqueid";	
	public static final String PROPS_REPLICATOR_ACTIVE= ".active";
	/**
	 * if this property is active/true then this servers iwldap webservice will be notified when changes happen
	 * to a user/group and a single replication is initiated from the replication server's side.
	 */
	public static final String PROPS_REPLICATOR_ACTIVE_LISTENER= ".active.iwldapws.listener";
	public static final String PROPS_REPLICATOR_ROOT_USER= ".root.user";
	public static final String PROPS_REPLICATOR_ROOT_PASSWORD= ".root.password";
	/**
	 * a group to overwrite/update with the baseurl/uniqueid group found
	 */
	public static final String PROPS_REPLICATOR_BASE_GROUP_ID = ".base.group.id";
	/**a group to store the replicated tree under**/
	public static final String PROPS_REPLICATOR_PARENT_GROUP_ID = ".parent.group.id";
	/**if we should repeat the replication or should it be a one time thing**/
	public static final String PROPS_REPLICATOR_REPEAT = ".replication.repeat";	
	/**
	 * The URI to the servers IdegaWeb LDAP Webservice, leave empty if we should assume the default URI
	 */
	public static final String PROPS_REPLICATOR_IWLDAPWS_URI = ".replication.iwldapws.uri";	
	
}
