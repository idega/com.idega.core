package com.idega.core.ldap.business;

/**
 * This bundle starter starts up an embedded LDAP server. <br>
 * 
 * @copyright Idega Software 2004
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson </a>
 */
import java.rmi.RemoteException;
import com.idega.core.ldap.replication.business.LDAPReplicationBusiness;
import com.idega.core.ldap.replication.business.LDAPReplicationConstants;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerConstants;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;

public class IWLDAPBundleStarter implements IWBundleStartable,EmbeddedLDAPServerConstants,LDAPReplicationConstants {
	private UserBusiness userBiz;

	private GroupBusiness groupBiz;

	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;
	private LDAPReplicationBusiness ldapReplicationBiz;


	public static String LDAP_DOMAIN_NAME_KEY = "ldap_base";

	public static final String LDAP_DOMAIN_NAME_DEFAULT_VALUE = "dc=idega,dc=com";

	public static final String LDAP_CONFIG_DIRECTORY_NAME = "ldap";

	public IWLDAPBundleStarter() {

	}

	public void start(IWBundle starterBundle) {
		IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
		
		//start the embedded ldap server if it is auto startable
		try {
			String autoStartLDAPServer = getEmbeddedLDAPServerBusiness(iwac).getPropertyAndCreateIfDoesNotExist(getEmbeddedLDAPServerBusiness(iwac).getLDAPSettings(),PROPS_JAVALDAP_AUTO_START,"false");
			if(autoStartLDAPServer.toLowerCase().equals("true")){
				getEmbeddedLDAPServerBusiness(iwac).startEmbeddedLDAPServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//start all auto startable replicators
		try {
			getLDAPReplicationBusiness(iwac).startAllReplicators();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see com.idega.idegaweb.IWBundleStartable#stop(IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		IWApplicationContext iwac = starterBundle.getApplication().getIWApplicationContext();
		
		try {
			getEmbeddedLDAPServerBusiness(iwac).stopEmbeddedLDAPServer();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			getLDAPReplicationBusiness(iwac).stopAllReplicators();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GroupBusiness getGroupBusiness(IWApplicationContext iwc) {
		if (groupBiz == null) {
			try {
				groupBiz = (GroupBusiness) com.idega.business.IBOLookup
						.getServiceInstance(iwc, GroupBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return groupBiz;
	}

	public UserBusiness getUserBusiness(IWApplicationContext iwc) {
		if (userBiz == null) {
			try {
				userBiz = (UserBusiness) com.idega.business.IBOLookup
						.getServiceInstance(iwc, UserBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return userBiz;
	}

	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness(
			IWApplicationContext iwc) {
		if (embeddedLDAPServerBiz == null) {
			try {
				embeddedLDAPServerBiz = (EmbeddedLDAPServerBusiness) com.idega.business.IBOLookup
						.getServiceInstance(iwc,
								EmbeddedLDAPServerBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return embeddedLDAPServerBiz;
	}
	
	public LDAPReplicationBusiness getLDAPReplicationBusiness(IWApplicationContext iwc) {
		if (ldapReplicationBiz == null) {
			try {
				ldapReplicationBiz = (LDAPReplicationBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, LDAPReplicationBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return ldapReplicationBiz;
	}

}