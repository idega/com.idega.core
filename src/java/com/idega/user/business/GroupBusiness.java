package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.core.builder.data.ICDomain;
import com.idega.core.file.data.ICFile;
import com.idega.core.location.data.Address;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWUserContext;
import com.idega.user.data.Group;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.User;

public interface GroupBusiness extends com.idega.business.IBOService {

    //public java.util.Collection getGroupsContaining(int p0)throws
    // javax.ejb.EJBException, java.rmi.RemoteException;
    //public java.util.Collection
    // getGroupsContaining(com.idega.user.data.Group p0)throws
    // javax.ejb.EJBException,java.rmi.RemoteException,
    // java.rmi.RemoteException;
    //public java.util.Collection
    // getGroupsContaining(com.idega.user.data.Group p0,java.lang.String[]
    // p1,boolean p2)throws javax.ejb.EJBException,java.rmi.RemoteException,
    // java.rmi.RemoteException;
    public java.util.Collection getParentGroupsRecursive(int p0)
            throws javax.ejb.FinderException, javax.ejb.EJBException,
            java.rmi.RemoteException;

    public java.util.Collection getParentGroupsRecursive(
            com.idega.user.data.Group p0) throws javax.ejb.EJBException,
            java.rmi.RemoteException, java.rmi.RemoteException;

    public java.util.Collection getParentGroupsRecursive(
    		com.idega.user.data.Group p0,java.util.Map p1,java.util.Map p2) throws javax.ejb.EJBException,
			java.rmi.RemoteException, java.rmi.RemoteException;


    public java.util.Collection getParentGroupsRecursive(
            com.idega.user.data.Group p0, java.lang.String[] p1, boolean p2)
            throws javax.ejb.EJBException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    public Group getGroupByGroupName(String name) throws FinderException,
            RemoteException;

    public com.idega.user.data.GroupType getGroupTypeFromString(
            java.lang.String p0) throws java.rmi.RemoteException,
            javax.ejb.FinderException, java.rmi.RemoteException;

    public void updateUsersInGroup(int p0, java.lang.String[] p1,
            com.idega.user.data.User p2) throws java.rmi.RemoteException,
            javax.ejb.FinderException, java.rmi.RemoteException;

    //public java.util.Collection getRegisteredGroupsNotDirectlyRelated(int
    // p0) throws java.rmi.RemoteException;
    public java.util.Collection getNonParentGroupsNonPermissionNonGeneral(int p0)
            throws java.rmi.RemoteException;

    //public java.util.Collection getGroupsContainingNotDirectlyRelated(int
    // p0) throws java.rmi.RemoteException;
    public java.util.Collection getParentGroupsInDirect(int p0)
            throws java.rmi.RemoteException;

    public com.idega.user.data.Group createGroup(java.lang.String p0,
            java.lang.String p1, java.lang.String p2, int p3)
            throws javax.ejb.CreateException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    public com.idega.user.data.Group createGroup(java.lang.String p0,
            java.lang.String p1, java.lang.String p2, int p3, int p4)
            throws javax.ejb.CreateException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    //public java.util.Collection
    // getGroupsContainedDirectlyRelated(com.idega.user.data.Group p0) throws
    // java.rmi.RemoteException;
    public java.util.Collection getChildGroups(com.idega.user.data.Group p0)
            throws java.rmi.RemoteException;

    //public java.util.Collection getAllGroupsNotDirectlyRelated(int
    // p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
    public com.idega.user.data.GroupTypeHome getGroupTypeHome()
            throws java.rmi.RemoteException, java.rmi.RemoteException;

    //public java.util.Collection getRegisteredGroups() throws
    // java.rmi.RemoteException;
    public java.util.Collection getAllNonPermissionOrGeneralGroups()
            throws java.rmi.RemoteException;

    //public java.util.Collection getGroupsContainingDirectlyRelated(int p0)
    // throws java.rmi.RemoteException;
    public java.util.Collection getParentGroups(int p0) throws FinderException,
            java.rmi.RemoteException;

    //public java.util.Collection
    // getGroupsContainingDirectlyRelated(com.idega.user.data.Group p0) throws
    // java.rmi.RemoteException;
    public java.util.Collection getParentGroups(com.idega.user.data.Group p0)
            throws java.rmi.RemoteException;

    //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0)
    // throws java.rmi.RemoteException;
    public java.util.Collection getNonParentGroups(int p0)
            throws java.rmi.RemoteException;

    public com.idega.user.data.GroupHome getPermissionGroupHome()
            throws java.rmi.RemoteException;

    public java.util.Collection getGroups(java.lang.String[] p0)
            throws javax.ejb.FinderException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    public com.idega.user.data.UserHome getUserHome()
            throws java.rmi.RemoteException;

    //public java.util.Collection getRegisteredGroupsNotDirectlyRelated(int
    // p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
    //public java.util.Collection getGroupsContainedDirectlyRelated(int
    // p0)throws
    // javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException,
    // java.rmi.RemoteException;
    public java.util.Collection getChildGroups(int p0)
            throws javax.ejb.EJBException, javax.ejb.FinderException,
            java.rmi.RemoteException, java.rmi.RemoteException;

    /**
     * Returns recursively down the group tree children of group aGroup with
     * filtered out with specified groupTypes
     * 
     * @param aGroup
     *            a Group to find children for
     * @param groupTypes
     *            the Groups a String array of group types of which the
     *            returned Groups must by.
     * @return Collection of Groups found recursively down the tree
     * @throws EJBException
     *             If an error occured
     */

    public void addGroupUnderDomain(com.idega.core.builder.data.ICDomain p0,
            com.idega.user.data.Group p1,
            com.idega.user.data.GroupDomainRelationType p2)
            throws javax.ejb.CreateException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    //public java.util.Collection
    // getRegisteredGroups(com.idega.presentation.IWContext p0) throws
    // java.rmi.RemoteException;
    public java.util.Collection getUserGroupPluginsForUser(
            com.idega.user.data.User p0) throws java.rmi.RemoteException;

    //public java.util.Collection getGroups(java.lang.String[] p0,boolean
    // p1,com.idega.presentation.IWContext p2)throws java.lang.Exception,
    // java.rmi.RemoteException;
    public com.idega.user.data.GroupHome getGroupHome(java.lang.String p0)
            throws java.rmi.RemoteException;

    //public java.util.Collection getGroupsContained(com.idega.user.data.Group
    // p0)throws javax.ejb.EJBException,java.rmi.RemoteException,
    // java.rmi.RemoteException;
    //public java.util.Collection getGroupsContained(int p0)throws
    // javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException,
    // java.rmi.RemoteException;
    //public java.util.Collection getGroupsContained(com.idega.user.data.Group
    // p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException,
    // java.rmi.RemoteException;
    public java.util.Collection getChildGroupsRecursive(
            com.idega.user.data.Group p0) throws javax.ejb.EJBException,
            java.rmi.RemoteException, java.rmi.RemoteException;

    public java.util.Collection getChildGroupsRecursive(int p0)
            throws javax.ejb.FinderException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    public java.util.Collection getChildGroupsRecursive(
            com.idega.user.data.Group p0, java.lang.String[] p1, boolean p2)
            throws java.rmi.RemoteException, java.rmi.RemoteException;

    //public java.util.Collection
    // getAllGroups(com.idega.presentation.IWContext p0) throws
    // java.rmi.RemoteException;
    public com.idega.user.data.UserGroupRepresentativeHome getUserGroupRepresentativeHome()
            throws java.rmi.RemoteException;

    //public java.util.Collection
    // getGroupsContainedNotDirectlyRelated(com.idega.user.data.Group p0)throws
    // javax.ejb.EJBException, java.rmi.RemoteException;
    public java.util.Collection getChildGroupsInDirect(
            com.idega.user.data.Group p0) throws javax.ejb.EJBException,
            java.rmi.RemoteException;

    public java.util.Collection getUserGroupPluginsForGroupTypeString(
            java.lang.String p0) throws java.rmi.RemoteException;

    public java.util.Collection getUserGroupPluginsForGroupType(
            com.idega.user.data.GroupType p0) throws java.rmi.RemoteException;

    public java.lang.String getGroupType(java.lang.Class p0)
            throws java.rmi.RemoteException, java.rmi.RemoteException;

    public java.util.Collection getUsersForUserRepresentativeGroups(
            java.util.Collection p0) throws javax.ejb.FinderException,
            java.rmi.RemoteException, java.rmi.RemoteException;

    public com.idega.user.data.User getUserByID(int p0)
            throws javax.ejb.FinderException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    public java.util.Collection getGroups(java.lang.String[] p0, boolean p1)
            throws java.lang.Exception, java.rmi.RemoteException;

    public java.util.Collection getAllGroups() throws java.rmi.RemoteException;

    public com.idega.user.data.Group getGroupByGroupID(int p0)
            throws javax.ejb.FinderException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    public void addUser(int p0, com.idega.user.data.User p1)
            throws javax.ejb.EJBException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    //public java.util.Collection getGroupsContainedNotDirectlyRelated(int
    // p0)throws
    // javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException,
    // java.rmi.RemoteException;
    public java.util.Collection getChildGroupsInDirect(int p0)
            throws javax.ejb.EJBException, javax.ejb.FinderException,
            java.rmi.RemoteException;

    public com.idega.user.data.GroupHome getGroupHome()
            throws java.rmi.RemoteException;

    public com.idega.user.data.Group createGroup(java.lang.String p0,
            java.lang.String p1, java.lang.String p2)
            throws javax.ejb.CreateException, java.rmi.RemoteException,
            java.rmi.RemoteException;

    public com.idega.user.data.UserGroupPlugInHome getUserGroupPlugInHome()
            throws java.rmi.RemoteException, java.rmi.RemoteException;

    public java.util.Collection getUsersDirectlyRelated(
            com.idega.user.data.Group p0) throws javax.ejb.EJBException,
            java.rmi.RemoteException, javax.ejb.FinderException;

    public java.util.Collection getUsersNotDirectlyRelated(
            com.idega.user.data.Group p0) throws javax.ejb.EJBException,
            java.rmi.RemoteException, javax.ejb.FinderException;

    public java.util.Collection getUsersDirectlyRelated(int p0)
            throws javax.ejb.EJBException, javax.ejb.FinderException;

    public java.util.Collection getUsers(int p0) throws javax.ejb.EJBException,
            javax.ejb.FinderException, java.rmi.RemoteException;

    public java.util.Collection getUsersNotDirectlyRelated(int p0)
            throws javax.ejb.EJBException, javax.ejb.FinderException;

    public Collection getUsersRecursive(int groupId) throws FinderException;

    public Collection getUsersRecursive(Group group) throws FinderException;

    public java.util.Collection getUsers(com.idega.user.data.Group p0)
            throws javax.ejb.FinderException, java.rmi.RemoteException;

    /**
     * Creates a group with the general grouptype and adds it under the default
     * Domain (IBDomain)
     * 
     * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
     *      String)
     */
    public Group createGroup(String name) throws CreateException,
            RemoteException;

    /**
     * Creates a group with the general grouptype and adds it under the default
     * Domain (IBDomain)
     * 
     * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
     *      String)
     */
    public Group createGroup(String name, String description)
            throws CreateException, RemoteException;

    public com.idega.core.location.data.Address updateGroupMainAddressOrCreateIfDoesNotExist(
            Integer groupId, String streetNameAndNumber, Integer postalCodeId,
            String countryName, String city, String province, String poBox)
            throws javax.ejb.CreateException, java.rmi.RemoteException;

    public com.idega.core.contact.data.Phone[] getGroupPhones(Group group)
            throws RemoteException;

    public com.idega.core.contact.data.Phone getGroupPhone(Group group,
            int phoneTypeId) throws RemoteException;

    public com.idega.core.contact.data.Email getGroupEmail(Group group)
            throws NoEmailFoundException;

    public void updateGroupMail(Group group, String email)
            throws CreateException, RemoteException;

    public com.idega.core.contact.data.PhoneHome getPhoneHome();

    public void updateGroupPhone(Group group, int phoneTypeId,
            String phoneNumber) throws EJBException;

    public boolean isGroupRemovable(Group group);

    public Collection getAllAllowedGroupTypesForChildren(int groupId,
            com.idega.idegaweb.IWUserContext iwc);

    public Collection getAllAllowedGroupTypesForChildren(Group group,
            com.idega.idegaweb.IWUserContext iwc);

    public String getNameOfGroupWithParentName(Group group);

    public GroupRelationHome getGroupRelationHome();

    public Collection getChildGroupsRecursiveResultFiltered(Group group,
            Collection groupTypesAsString, boolean onlyReturnTypesInCollection)
            throws java.rmi.RemoteException;

    public Collection getChildGroupsRecursiveResultFiltered(int groupId,
            Collection groupTypesAsString, boolean onlyReturnTypesInCollection)
            throws java.rmi.RemoteException;

    public Collection getUsersFromGroupRecursive(Group group);

    public Collection getUsersFromGroupRecursive(Group group,
            Collection groupTypesAsString, boolean onlyReturnTypesInCollection);

    /**
     * Adds a group direcly under the domain (right in top under the domain in
     * the group tree). This adds the group with GroupRelationType Top to the
     * domain.
     * 
     * @param domain
     * @param group
     * @throws CreateException
     * @throws RemoteException
     */
    public void addGroupUnderDomainRoot(ICDomain domain, Group group)
            throws CreateException, RemoteException;

    /**
     * Creates a group and adds it under the default Domain (IBDomain) <br>
     * If createUnderDomainRoot is true it is added under the root (directly
     * under in the group tree) of the domain.
     * 
     * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
     *      String)
     */
    public Group createGroup(String name, String description, String type,
            boolean createUnderDomainRoot) throws CreateException,
            RemoteException;

    /**
     * Creates a group and adds it under the default Domain (IBDomain) and
     * under the group parentGroup.
     */
    public Group createGroupUnder(String name, String description, String type,
            Group parentGroup) throws CreateException, RemoteException;

    /**
     * Creates a group and adds it under the the default Domain (ICDomain) and
     * under the group parentGroup.
     */
    public Group createGroupUnder(String name, String description, String type,
            int homePageID, int aliasID, Group parentGroup)
            throws CreateException, RemoteException;

    /**
     * Creates a general group and adds it under the default Domain (IBDomain)
     * and under the group parentGroup.
     */
    public Group createGroupUnder(String name, String description,
            Group parentGroup) throws CreateException, RemoteException;

    /**
     * Gives all parent groups owners' primary groups, permit permission to this group.
     * The permission to give others permissions to this group.
     */
    public void applyPermitPermissionToGroupsParentGroupOwnersPrimaryGroups(IWUserContext iwuc, Group group) throws RemoteException;
    
    /**
     * Sets the user as the owner of the group.
     * @param iwc
     * @param group
     * @param user
     */
    public void applyUserAsGroupsOwner(IWUserContext iwuc, Group group, User user);
    
    /**
     * Sets the currently logged on user as the owner of the group.
     * @param iwc
     * @param group
     */
    public void applyCurrentUserAsOwnerOfGroup(IWUserContext iwuc, Group group);
    

    /**
     * Give the current users primary group all permission except for owner
     * 
     */
    public void applyAllGroupPermissionsForGroupToCurrentUsersPrimaryGroup(IWUserContext iwuc, Group group);
    
    /**
     * Give the users primary group all permission except for owner
     * 
     */
    public void applyAllGroupPermissionsForGroupToUsersPrimaryGroup(IWUserContext iwuc, Group group, User user);

    /**
     * This methods gives the second group specified all permissions to the other groups except for owner permission (set to users not groups).
     * The permissions include: view,edit,create,remove users, and the permission to give others permissions to it.
     * @param iwuc
     * @param groupToSetPermissionTo The group the permission apply to.
     * @param groupToGetPermissions The group that will own the permissions e.g. get the rights to do the stuff.
     */
    public void applyAllGroupPermissionsForGroupToGroup(IWUserContext iwuc, Group groupToSetPermissionTo, Group groupToGetPermissions);
    
    /**
     * If the groupToGetInheritanceFrom has inherited permission it is copied to the other group.
     * 
     * @param groupToGetInheritanceFrom
     * @param groupToInheritPermissions
     */
    public void applyPermissionControllingFromGroupToGroup(Group groupToGetInheritanceFrom, Group groupToInheritPermissions);

    /**
     * This method should only be called once for a newly created group if it was done in code. This method is
     * automatically called if the group is created in the user application.
     * Sets the user as the owner of the group and gives his primary group all group permissions to the group. 
     * Also gives all owners' primary groups of the groups parent groups permission to give others permission 
     * to this group. Finally checks the groups parent if any for inherited permissions and sets them.
     * @param iwc
     * @param newlyCreatedGroup
     * @param user
     * @throws RemoteException
     */
    public void applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(IWUserContext iwuc,Group newlyCreatedGroup, User user) throws RemoteException;

    /**
     * Returns a collection (list) of User objects that have owner permission to this group 
   * @param group to get owners for
   * @return
   * @throws RemoteException
   */
    
    /**
     * Applies permissions that have been marked to be inherited to this group from its parents
   * @param iwuc
   * @param newlyCreatedGroup
   * @throws RemoteException
   */
  public void applyInheritedPermissionsToGroup(IWUserContext iwuc, Group newlyCreatedGroup) throws RemoteException;
  
  public Collection getOwnerUsersForGroup(Group group) throws RemoteException;
	public ICFile createGroupHomeFolder(Group group) throws CreateException;
  public Address getGroupMainAddress(Group group) throws RemoteException, IDOLookupException, IDOCompositePrimaryKeyException,  IDORelationshipException;

	/**
	 * Optimized version of getParentGroups(Group group) by Gummi 25.08.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	public  Collection getParentGroups(Group group,Map cachedParents, Map cachedGroups) throws RemoteException;
	
	public	 String getNameOfGroupWithParentName(Group group, Collection parentGroups) throws RemoteException;
	/**
	 * Optimized version of getNameOfGroupWithParentName(Group group) by Gummi 25.08.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	  public String getNameOfGroupWithParentName(Group group,Map cachedParents, Map cachedGroups) throws RemoteException;
}
