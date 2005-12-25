/*
 * $Id: IWLDAPUserGroupPluginBusiness.java,v 1.1 2005/12/25 17:14:27 eiki Exp $
 * Created on Nov 30, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.ldap.business;

import java.rmi.RemoteException;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOService;
import com.idega.core.ldap.replication.business.LDAPReplicationBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerBusiness;
import com.idega.core.ldap.server.business.EmbeddedLDAPServerConstants;
import com.idega.presentation.PresentationObject;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.UserGroupPlugInBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2005/12/25 17:14:27 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public interface IWLDAPUserGroupPluginBusiness extends IBOService, UserGroupPlugInBusiness, EmbeddedLDAPServerConstants {

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#afterUserCreateOrUpdate
	 */
	public void afterUserCreateOrUpdate(User user, Group parentGroup) throws CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#isUserSuitedForGroup
	 */
	public String isUserSuitedForGroup(User user, Group targetGroup) throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getGroupBusiness
	 */
	public GroupBusiness getGroupBusiness() throws IBOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getUserBusiness
	 */
	public UserBusiness getUserBusiness() throws IBOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getLDAPReplicationBusiness
	 */
	public LDAPReplicationBusiness getLDAPReplicationBusiness() throws IBOLookupException, java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getEmbeddedLDAPServerBusiness
	 */
	public EmbeddedLDAPServerBusiness getEmbeddedLDAPServerBusiness() throws IBOLookupException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#beforeUserRemove
	 */
	public void beforeUserRemove(User user, Group parentGroup) throws RemoveException, RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#beforeGroupRemove
	 */
	public void beforeGroupRemove(Group group, Group parentGroup) throws RemoveException, RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#afterGroupCreateOrUpdate
	 */
	public void afterGroupCreateOrUpdate(Group group, Group parentGroup) throws CreateException, RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#instanciateEditor
	 */
	public PresentationObject instanciateEditor(Group group) throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#instanciateViewer
	 */
	public PresentationObject instanciateViewer(Group group) throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getUserPropertiesTabs
	 */
	public List getUserPropertiesTabs(User user) throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getGroupPropertiesTabs
	 */
	public List getGroupPropertiesTabs(Group group) throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getMainToolbarElements
	 */
	public List getMainToolbarElements() throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#getGroupToolbarElements
	 */
	public List getGroupToolbarElements(Group group) throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#isUserAssignableFromGroupToGroup
	 */
	public String isUserAssignableFromGroupToGroup(User user, Group sourceGroup, Group targetGroup)
			throws RemoteException;

	/**
	 * @see com.idega.core.ldap.business.IWLDAPUserGroupPluginBusinessBean#canCreateSubGroup
	 */
	public String canCreateSubGroup(Group parentGroup, String groupTypeOfSubGroup) throws RemoteException;
}
