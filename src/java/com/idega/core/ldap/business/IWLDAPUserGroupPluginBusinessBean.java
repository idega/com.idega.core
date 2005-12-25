/*
 * $Id: IWLDAPUserGroupPluginBusinessBean.java,v 1.1 2005/12/25 17:14:27 eiki Exp $
 * Created on Jul 3, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.business;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.ldap.replication.business.LDAPReplicationBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerConstants;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.FileUtil;


public class IWLDAPUserGroupPluginBusinessBean extends IBOServiceBean implements UserGroupPlugInBusiness, EmbeddedLDAPServerConstants, IWLDAPUserGroupPluginBusiness{
		
	public void afterUserCreateOrUpdate(User user, Group parentGroup) throws CreateException {
		try {
			LDAPReplicationBusiness biz = getLDAPReplicationBusiness();
			EmbeddedLDAPServerBusiness embeddedBiz = getEmbeddedLDAPServerBusiness();
			
			Map listeners = biz.getReplicationListenerMap();
			String uuid = user.getUniqueId();
			String serverHostName = embeddedBiz.getLDAPSettings().getProperty(PROPS_JAVALDAP_SERVER_NAME);
			String ldapPort= embeddedBiz.getLDAPSettings().getProperty(PROPS_JAVALDAP_SERVER_PORT);
			
			if( uuid!=null && listeners!=null && listeners.isEmpty()){
				for ( Iterator keys = listeners.keySet().iterator(); keys.hasNext();) {
					String remoteServerNameAndPort = (String) keys.next();
					String remoteIwLdapUri = (String) listeners.get(remoteServerNameAndPort);
					
//				Call the webservice
					//name the method onUserChanged ?
					String webserviceQuery = "method=notifyOnUserChanged&UserUUID="+uuid+"&replicationServerHostName="+serverHostName+"&ldapPort="+ldapPort;
					String response = FileUtil.getStringFromURL("http://"+remoteServerNameAndPort+remoteIwLdapUri+"?"+webserviceQuery);
					log("Notifying a replication listener about a user change. Remote server and port: "+remoteServerNameAndPort+" responded with: "+response);	
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String isUserSuitedForGroup(User user, Group targetGroup) throws RemoteException {
		return null;
	}
	
	public GroupBusiness getGroupBusiness() throws IBOLookupException{
		return (GroupBusiness) getServiceInstance(GroupBusiness.class);
	}
	
	public UserBusiness getUserBusiness() throws IBOLookupException{
		return (UserBusiness) getServiceInstance(UserBusiness.class);
	}
	
	public LDAPReplicationBusiness getLDAPReplicationBusiness() throws IBOLookupException{
		return (LDAPReplicationBusiness) getServiceInstance(LDAPReplicationBusiness.class);
	}
	
	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness() throws IBOLookupException{
		return (EmbeddedLDAPServerBusiness) getServiceInstance(EmbeddedLDAPServerBusiness.class);
	}

	public void beforeUserRemove(User user, Group parentGroup) throws RemoveException, RemoteException {	
	}

	public void beforeGroupRemove(Group group, Group parentGroup) throws RemoveException, RemoteException {
	}

	public void afterGroupCreateOrUpdate(Group group, Group parentGroup) throws CreateException, RemoteException {
		//get ldapreplicationbusiness
		//call listeners
	}

	public PresentationObject instanciateEditor(Group group) throws RemoteException {
		return null;
	}

	public PresentationObject instanciateViewer(Group group) throws RemoteException {
		return null;
	}

	public List getUserPropertiesTabs(User user) throws RemoteException {
		return null;
	}

	public List getGroupPropertiesTabs(Group group) throws RemoteException {
		return null;
	}

	public List getMainToolbarElements() throws RemoteException {
		return null;
	}

	public List getGroupToolbarElements(Group group) throws RemoteException {
		return null;
	}

	public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup) throws RemoteException {
		return null;
	}

	public String canCreateSubGroup(Group parentGroup, String groupTypeOfSubGroup) throws RemoteException {
		return null;
	}	
}
