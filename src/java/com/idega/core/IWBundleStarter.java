package com.idega.core;

/**
 * This bundle starter starts up an embedded LDAP server. <br>
 * 
 * @copyright Idega Software 2004
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson </a>
 */
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import org.codehaus.plexus.ldapserver.server.EmbeddedLDAPServer;
import com.idega.core.contact.data.Email;
import com.idega.core.ldap.replication.business.LDAPReplicationBusiness;
import com.idega.core.ldap.replication.business.LDAPReplicationConstants;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerConstants;
import com.idega.core.ldap.server.util.Ldap;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.Timer;

public class IWBundleStarter implements IWBundleStartable,EmbeddedLDAPServerConstants,LDAPReplicationConstants {
	private EmbeddedLDAPServer server;

	private DirContext context;

	private Ldap ldap;

	private UserBusiness userBiz;

	private GroupBusiness groupBiz;

	private EmbeddedLDAPServerBusiness embeddedLDAPServerBiz;
	private LDAPReplicationBusiness ldapReplicationBiz;


	public static String LDAP_DOMAIN_NAME_KEY = "ldap_base";

	public static final String LDAP_DOMAIN_NAME_DEFAULT_VALUE = "dc=idega,dc=com";

	public static final String LDAP_CONFIG_DIRECTORY_NAME = "ldap";

	public IWBundleStarter() {

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
		if (this.groupBiz == null) {
			try {
				this.groupBiz = (GroupBusiness) com.idega.business.IBOLookup
						.getServiceInstance(iwc, GroupBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.groupBiz;
	}

	/**
	 * old testing method
	 * 
	 * @param iwac
	 * @throws RemoteException
	 * @throws FinderException
	 */
	private void addAllGroupsAndUsers(IWApplicationContext iwac)
			throws RemoteException, FinderException {

		//create an organization and an organizational unit (a group)
		//		dn: o=Idega,dc=javaldap,dc=com
		//		o: Idega
		//		description: Idega Software
		//		objectClass: organization
		//
		//		dn: ou=ISI,o=Idega,dc=javaldap,dc=com
		//		ou: ISI
		//		objectClass: organizationalUnit

		Attribute orgAttribute;
		Attributes orgAttributes = new BasicAttributes();

		orgAttribute = new BasicAttribute("objectClass");
		orgAttribute.add("top");
		orgAttribute.add("organization");
		orgAttributes.put(orgAttribute);
		orgAttributes.put("o", "Idega");
		try {
			//			context.createSubcontext("o=Idega,"+ ldap.getBaseDN(),
			// orgAttributes);
			this.context.createSubcontext("o=Idega", orgAttributes);
		} catch (NamingException e) {

			e.printStackTrace();
		}

		Attribute orgUnitAttribute;
		Attributes orgUnitAttributes = new BasicAttributes();

		orgUnitAttribute = new BasicAttribute("objectClass");
		orgUnitAttribute.add("top");
		orgUnitAttribute.add("organizationalUnit");
		orgUnitAttributes.put(orgUnitAttribute);
		orgUnitAttributes.put("o", "Idega");
		try {
			//context.createSubcontext("ou=Developers,o=Idega,"+
			// ldap.getBaseDN(), orgAttributes);
			this.context
					.createSubcontext("ou=Developers,o=Idega",
							orgUnitAttributes);
		} catch (NamingException e) {

			e.printStackTrace();
		}

		Attribute attribute;
		Attributes attributes = new BasicAttributes();

		attribute = new BasicAttribute("objectClass");
		attribute.add("top");
		attribute.add("person");
		attribute.add("inetOrgPerson");
		attributes.put(attribute);

		Timer timer = new Timer();
		timer.start();

		Collection users = getUserBusiness(iwac)
				.getAllUsersOrderedByFirstName();
		int max = 3000;
		int counter = 1;

		Iterator iter = users.iterator();
		while (iter.hasNext() && counter < max) {
			User user = (User) iter.next();

			//attributes.put("uid", username);
			//attributes.put("cn", user.getPersonalID());
			String lastName = user.getLastName();
			String firstName = user.getFirstName();
			//	        
			//	        mail: eiki@idega.is
			//			initials: ESH
			//			title: manager, product development
			//			uid: eiki
			//			telephoneNumber: +1 408 555 1862
			//			facsimileTelephoneNumber: +1 408 555 1992
			//			mobile: +1 408 555 1941

			if (lastName != null) {
				attributes.put("sn", lastName);
			} else {
				attributes.put("sn", user.getName());
			}
			attributes.put("givenName", firstName);

			Collection emails = user.getEmails();
			if (emails != null && !emails.isEmpty()) {
				attributes.put("mail", ((Email) emails.iterator().next())
						.getEmailAddress());
			}

			try {
				//context.createSubcontext("cn=" + user.getPersonalID() +
				// ",ou=Developers,o=Idega," + ldap.getBaseDN(), attributes);
				this.context.createSubcontext("cn=" + user.getPersonalID()
						+ ",ou=Developers,o=Idega", attributes);
			} catch (NamingException e) {

				e.printStackTrace();
			}

			counter++;

		}

		timer.stop();

		System.out.print("timi i sek " + (timer.getTime()) / 1000);

	}

	public UserBusiness getUserBusiness(IWApplicationContext iwc) {
		if (this.userBiz == null) {
			try {
				this.userBiz = (UserBusiness) com.idega.business.IBOLookup
						.getServiceInstance(iwc, UserBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.userBiz;
	}

	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness(
			IWApplicationContext iwc) {
		if (this.embeddedLDAPServerBiz == null) {
			try {
				this.embeddedLDAPServerBiz = (EmbeddedLDAPServerBusiness) com.idega.business.IBOLookup
						.getServiceInstance(iwc,
								EmbeddedLDAPServerBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.embeddedLDAPServerBiz;
	}
	
	public LDAPReplicationBusiness getLDAPReplicationBusiness(IWApplicationContext iwc) {
		if (this.ldapReplicationBiz == null) {
			try {
				this.ldapReplicationBiz = (LDAPReplicationBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, LDAPReplicationBusiness.class);
			} catch (java.rmi.RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.ldapReplicationBiz;
	}

}