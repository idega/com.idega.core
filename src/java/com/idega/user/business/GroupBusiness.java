/*
 * $Id: GroupBusiness.java,v 1.50 2005/04/17 17:04:46 eiki Exp $
 * Created on Nov 16, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.codehaus.plexus.ldapserver.server.syntax.DirectoryString;
import com.idega.business.IBOService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneHome;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.ldap.client.naming.DN;
import com.idega.core.ldap.util.IWLDAPConstants;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.data.Group;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;
import com.idega.user.data.User;
import com.idega.user.data.UserGroupRepresentativeHome;
import com.idega.user.data.UserHome;
import com.idega.util.datastructures.NestedSetsContainer;


/**
 * 
 *  Last modified: $Date: 2005/04/17 17:04:46 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.50 $
 */
public interface GroupBusiness extends IBOService, IWLDAPConstants {

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserHome
	 */
	public UserHome getUserHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserGroupRepresentativeHome
	 */
	public UserGroupRepresentativeHome getUserGroupRepresentativeHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupHome
	 */
	public GroupHome getGroupHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getPermissionGroupHome
	 */
	public GroupHome getPermissionGroupHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getAllGroups
	 */
	public Collection getAllGroups() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getAllNonPermissionOrGeneralGroups
	 */
	public Collection getAllNonPermissionOrGeneralGroups() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroups
	 */
	public Collection getGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws Exception,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroups
	 */
	public Collection getParentGroups(int uGroupId) throws EJBException, FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroups
	 */
	public Collection getParentGroups(Group group) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroups
	 */
	public Collection getParentGroups(Group group, Map cachedParents, Map cachedGroups) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getNonParentGroups
	 */
	public Collection getNonParentGroups(int uGroupId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getNonParentGroupsNonPermissionNonGeneral
	 */
	public Collection getNonParentGroupsNonPermissionNonGeneral(int uGroupId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroupsInDirect
	 */
	public Collection getParentGroupsInDirect(int uGroupId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroupsRecursive
	 */
	public Collection getParentGroupsRecursive(int uGroupId) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroupsRecursive
	 */
	public Collection getParentGroupsRecursive(Group aGroup) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroupsRecursive
	 */
	public Collection getParentGroupsRecursive(Group aGroup, Map cachedParents, Map cachedGroups) throws EJBException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserRepresentativeGroupTypeStringArray
	 */
	public String[] getUserRepresentativeGroupTypeStringArray() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getParentGroupsRecursive
	 */
	public Collection getParentGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes)
			throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsers
	 */
	public Collection getUsers(int groupId) throws EJBException, FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersDirectlyRelated
	 */
	public Collection getUsersDirectlyRelated(int groupId) throws EJBException, FinderException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersNotDirectlyRelated
	 */
	public Collection getUsersNotDirectlyRelated(int groupId) throws EJBException, FinderException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroupsRecursive
	 */
	public Collection getChildGroupsRecursive(int groupId) throws EJBException, FinderException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroupsRecursive
	 */
	public Collection getChildGroupsRecursive(Group aGroup) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroupsRecursive
	 */
	public Collection getChildGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes)
			throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsers
	 */
	public Collection getUsers(Group group) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersRecursive
	 */
	public Collection getUsersRecursive(Group group) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersRecursive
	 */
	public Collection getUsersRecursive(int groupId) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroups
	 */
	public Collection getChildGroups(int groupId) throws EJBException, FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroupsRecursiveResultFiltered
	 */
	public Collection getChildGroupsRecursiveResultFiltered(int groupId, Collection groupTypesAsString,
			boolean onlyReturnTypesInCollection) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroupsRecursiveResultFiltered
	 */
	public Collection getChildGroupsRecursiveResultFiltered(Group group, Collection groupTypesAsString,
			boolean onlyReturnTypesInCollection) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersFromGroupRecursive
	 */
	public Collection getUsersFromGroupRecursive(Group group) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersFromGroupRecursive
	 */
	public Collection getUsersFromGroupRecursive(Group group, Collection groupTypesAsString,
			boolean onlyReturnTypesInCollection) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroups
	 */
	public Collection getChildGroups(Group aGroup) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroups
	 */
	public Collection getChildGroups(Group aGroup, Collection groupTypes, boolean returnSpecifiedGroupTypes)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroups
	 */
	public Collection getChildGroups(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersDirectlyRelated
	 */
	public Collection getUsersDirectlyRelated(Group group) throws EJBException, RemoteException, FinderException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroupsInDirect
	 */
	public Collection getChildGroupsInDirect(int groupId) throws EJBException, FinderException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getChildGroupsInDirect
	 */
	public Collection getChildGroupsInDirect(Group group) throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersNotDirectlyRelated
	 */
	public Collection getUsersNotDirectlyRelated(Group group) throws EJBException, RemoteException, FinderException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroups
	 */
	public Collection getGroups(String[] groupIDs) throws FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUsersForUserRepresentativeGroups
	 */
	public Collection getUsersForUserRepresentativeGroups(Collection groups) throws FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#updateUsersInGroup
	 */
	public void updateUsersInGroup(int groupId, String[] usrGroupIdsInGroup, User currentUser) throws RemoteException,
			FinderException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupByGroupID
	 */
	public Group getGroupByGroupID(int id) throws FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupsByGroupName
	 */
	public Collection getGroupsByGroupName(String name) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupsByGroupTypeAndFirstPartOfName
	 */
	public Collection getGroupsByGroupTypeAndFirstPartOfName(String groupType, String groupNameStartsWith)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupsByAbbreviation
	 */
	public Collection getGroupsByAbbreviation(String abbreviation) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserByID
	 */
	public User getUserByID(int id) throws FinderException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#addUser
	 */
	public void addUser(int groupId, User user) throws EJBException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupHome
	 */
	public GroupHome getGroupHome(String groupType) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupRelationHome
	 */
	public GroupRelationHome getGroupRelationHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createOrUpdateGroup
	 */
	public Group createOrUpdateGroup(DN distinguishedName, Attributes attributes, boolean createUnderRootDomainGroup,
			Group parentGroup) throws CreateException, NamingException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupByDNOrUniqueId
	 */
	public Group getGroupByDNOrUniqueId(DN distinguishedName, String uniqueID) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupByUniqueId
	 */
	public Group getGroupByUniqueId(String uniqueID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#setMetaDataFromLDAPAttributes
	 */
	public void setMetaDataFromLDAPAttributes(Group group, DN distinguishedName, Attributes attributes)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createOrUpdateGroup
	 */
	public Group createOrUpdateGroup(DN distinguishedName, Attributes attributes) throws CreateException,
			NamingException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createOrUpdateGroup
	 */
	public Group createOrUpdateGroup(DN distinguishedName, Attributes attributes, Group parentGroup)
			throws CreateException, NamingException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroup
	 */
	public Group createGroup(String name) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroup
	 */
	public Group createGroup(String name, String description) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroup
	 */
	public Group createGroup(String name, String description, String type) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroup
	 */
	public Group createGroup(String name, String description, String type, boolean createUnderDomainRoot)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroupUnder
	 */
	public Group createGroupUnder(String name, String description, String type, Group parentGroup)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroupUnder
	 */
	public Group createGroupUnder(String name, String description, Group parentGroup) throws CreateException,
			RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroup
	 */
	public Group createGroup(String name, String description, String type, int homePageID) throws CreateException,
			RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroup
	 */
	public Group createGroup(String name, String description, String type, int homePageID, int aliasID)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroupUnder
	 */
	public Group createGroupUnder(String name, String description, String type, int homePageID, int aliasID,
			Group parentGroup) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getICFileHome
	 */
	public ICFileHome getICFileHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroupHomeFolder
	 */
	public ICFile createGroupHomeFolder(Group group) throws CreateException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getAllAllowedGroupTypesForChildren
	 */
	public Collection getAllAllowedGroupTypesForChildren(int groupId, IWUserContext iwuc)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getAllAllowedGroupTypesForChildren
	 */
	public Collection getAllAllowedGroupTypesForChildren(Group group, IWUserContext iwuc)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#addGroupTypeChildren
	 */
	public void addGroupTypeChildren(List list, GroupType groupType) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupType
	 */
	public String getGroupType(Class groupClass) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupTypeFromString
	 */
	public GroupType getGroupTypeFromString(String type) throws RemoteException, FinderException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserGroupPluginsForGroupType
	 */
	public Collection getUserGroupPluginsForGroupType(String groupType) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserGroupPluginsForGroup
	 */
	public Collection getUserGroupPluginsForGroup(Group group) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserGroupPluginsForUser
	 */
	public Collection getUserGroupPluginsForUser(User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getUserGroupPlugins
	 */
	public Collection getUserGroupPlugins() throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupTypeHome
	 */
	public GroupTypeHome getGroupTypeHome() throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#addGroupUnderDomainRoot
	 */
	public void addGroupUnderDomainRoot(ICDomain domain, Group group) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#addGroupUnderDomain
	 */
	public void addGroupUnderDomain(ICDomain domain, Group group, GroupDomainRelationType type) throws CreateException,
			RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#updateGroupMainAddressOrCreateIfDoesNotExist
	 */
	public Address updateGroupMainAddressOrCreateIfDoesNotExist(Integer groupId, String streetNameAndNumber,
			Integer postalCodeId, String countryName, String city, String province, String poBox)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getAddressBusiness
	 */
	public AddressBusiness getAddressBusiness() throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupMainAddress
	 */
	public Address getGroupMainAddress(Group group) throws RemoteException, IDOLookupException,
			IDOCompositePrimaryKeyException, IDORelationshipException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getAddressHome
	 */
	public AddressHome getAddressHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupPhones
	 */
	public Phone[] getGroupPhones(Group group) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupEmail
	 */
	public Email getGroupEmail(Group group) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#updateGroupMail
	 */
	public void updateGroupMail(Group group, String email) throws CreateException, RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getEmailHome
	 */
	public EmailHome getEmailHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#updateGroupPhone
	 */
	public void updateGroupPhone(Group group, int phoneTypeId, String phoneNumber) throws EJBException,
			java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupPhone
	 */
	public Phone getGroupPhone(Group group, int phoneTypeId) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getPhoneHome
	 */
	public PhoneHome getPhoneHome() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#isGroupRemovable
	 */
	public boolean isGroupRemovable(Group group) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getNameOfGroupWithParentName
	 */
	public String getNameOfGroupWithParentName(Group group, Collection parentGroups) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getNameOfGroupWithParentName
	 */
	public String getNameOfGroupWithParentName(Group group) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getNameOfGroupWithParentName
	 */
	public String getNameOfGroupWithParentName(Group group, Map cachedParents, Map cachedGroups)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createVisibleGroupType
	 */
	public GroupType createVisibleGroupType(String groupType) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#createGroupTypeOrUpdate
	 */
	public GroupType createGroupTypeOrUpdate(String groupType, boolean visible) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyPermitPermissionToGroupsParentGroupOwnersPrimaryGroups
	 */
	public void applyPermitPermissionToGroupsParentGroupOwnersPrimaryGroups(IWUserContext iwc, Group group)
			throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyUserAsGroupsOwner
	 */
	public void applyUserAsGroupsOwner(IWUserContext iwc, Group group, User user) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyCurrentUserAsOwnerOfGroup
	 */
	public void applyCurrentUserAsOwnerOfGroup(IWUserContext iwc, Group group) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyAllGroupPermissionsForGroupToCurrentUsersPrimaryGroup
	 */
	public void applyAllGroupPermissionsForGroupToCurrentUsersPrimaryGroup(IWUserContext iwuc, Group group)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyAllGroupPermissionsForGroupToUsersPrimaryGroup
	 */
	public void applyAllGroupPermissionsForGroupToUsersPrimaryGroup(IWUserContext iwuc, Group group, User user)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyAllGroupPermissionsForGroupToGroup
	 */
	public void applyAllGroupPermissionsForGroupToGroup(IWUserContext iwuc, Group groupToSetPermissionTo,
			Group groupToGetPermissions) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyPermissionControllingFromGroupToGroup
	 */
	public void applyPermissionControllingFromGroupToGroup(Group groupToGetInheritanceFrom,
			Group groupToInheritPermissions) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup
	 */
	public void applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(IWUserContext iwuc,
			Group newlyCreatedGroup, User user) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#applyInheritedPermissionsToGroup
	 */
	public void applyInheritedPermissionsToGroup(IWUserContext iwuc, Group newlyCreatedGroup) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getOwnerUsersForGroup
	 */
	public Collection getOwnerUsersForGroup(Group group) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupByDirectoryString
	 */
	public Group getGroupByDirectoryString(DirectoryString dn) throws RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getGroupsByMetaDataKeyAndValue
	 */
	public Collection getGroupsByMetaDataKeyAndValue(String key, String value) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#getLastGroupTreeSnapShot
	 */
	public NestedSetsContainer getLastGroupTreeSnapShot() throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#refreshGroupTreeSnapShotInANewThread
	 */
	public void refreshGroupTreeSnapShotInANewThread() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#refreshGroupTreeSnapShot
	 */
	public void refreshGroupTreeSnapShot() throws EJBException, java.rmi.RemoteException;

	/**
	 * @see com.idega.user.business.GroupBusinessBean#userGroupTreeImageProcedureTopNodeSearch
	 */
	public boolean userGroupTreeImageProcedureTopNodeSearch() throws java.rmi.RemoteException;
	
	/**
	 * @see com.idega.user.business.GroupBusinessBean#callAllUserGroupPluginAfterGroupCreateOrUpdateMethod
	 */
	public void callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(Group group);

	/**
	 * @see com.idega.user.business.GroupBusinessBean#callAllUserGroupPluginBeforeGroupRemoveMethod
	 */
	public void callAllUserGroupPluginBeforeGroupRemoveMethod(Group group);
}
