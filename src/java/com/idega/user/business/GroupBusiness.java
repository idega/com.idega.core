package com.idega.user.business;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.*;

import com.idega.user.data.Group;
import com.idega.user.data.GroupRelationHome;

public interface GroupBusiness extends com.idega.business.IBOService
{
 //public java.util.Collection getGroupsContaining(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 //public java.util.Collection getGroupsContaining(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 //public java.util.Collection getGroupsContaining(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getParentGroupsRecursive(int p0)throws javax.ejb.FinderException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getParentGroupsRecursive(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getParentGroupsRecursive(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public Group getGroupByGroupName(String name)throws FinderException,RemoteException;

 public com.idega.user.data.GroupType getGroupTypeFromString(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public void updateUsersInGroup(int p0,java.lang.String[] p1, com.idega.user.data.User p2)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 
 //public java.util.Collection getRegisteredGroupsNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getNonParentGroupsNonPermissionNonGeneral(int p0) throws java.rmi.RemoteException;
 

 //public java.util.Collection getGroupsContainingNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getParentGroupsInDirect(int p0) throws java.rmi.RemoteException;
 
 public com.idega.user.data.Group createGroup(java.lang.String p0,java.lang.String p1,java.lang.String p2,int p3)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.Group createGroup(java.lang.String p0,java.lang.String p1,java.lang.String p2,int p3,int p4)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 
 //public java.util.Collection getGroupsContainedDirectlyRelated(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.util.Collection getChildGroups(com.idega.user.data.Group p0) throws java.rmi.RemoteException;

 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.user.data.GroupTypeHome getGroupTypeHome()throws java.rmi.RemoteException, java.rmi.RemoteException;

 //public java.util.Collection getRegisteredGroups() throws java.rmi.RemoteException;
 public java.util.Collection getAllNonPermissionOrGeneralGroups() throws java.rmi.RemoteException;
 //public java.util.Collection getGroupsContainingDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getParentGroups(int p0) throws FinderException,java.rmi.RemoteException;
 //public java.util.Collection getGroupsContainingDirectlyRelated(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.util.Collection getParentGroups(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 
 //public java.util.Collection getAllGroupsNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getNonParentGroups(int p0) throws java.rmi.RemoteException;

 public com.idega.user.data.GroupHome getPermissionGroupHome() throws java.rmi.RemoteException;
 public java.util.Collection getGroups(java.lang.String[] p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.UserHome getUserHome() throws java.rmi.RemoteException;
 //public java.util.Collection getRegisteredGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 //public java.util.Collection getGroupsContainedDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getChildGroups(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 /**
 * Returns recursively down the group tree children of group aGroup with filtered out with specified groupTypes
 * @param aGroup a Group to find children for
 * @param groupTypes the Groups a String array of group types of which the returned Groups must by.
 * @return Collection of Groups found recursively down the tree
 * @throws EJBException If an error occured
 */

 
 public void addGroupUnderDomain(com.idega.core.builder.data.ICDomain p0,com.idega.user.data.Group p1,com.idega.user.data.GroupDomainRelationType p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 //public java.util.Collection getRegisteredGroups(com.idega.presentation.IWContext p0) throws java.rmi.RemoteException;
 public java.util.Collection getUserGroupPluginsForUser(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 //public java.util.Collection getGroups(java.lang.String[] p0,boolean p1,com.idega.presentation.IWContext p2)throws java.lang.Exception, java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getGroupHome(java.lang.String p0) throws java.rmi.RemoteException;
 
 //public java.util.Collection getGroupsContained(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 //public java.util.Collection getGroupsContained(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 //public java.util.Collection getGroupsContained(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException, java.rmi.RemoteException; 
 public java.util.Collection getChildGroupsRecursive(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getChildGroupsRecursive(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getChildGroupsRecursive(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException, java.rmi.RemoteException;

 
 //public java.util.Collection getAllGroups(com.idega.presentation.IWContext p0) throws java.rmi.RemoteException;
 public com.idega.user.data.UserGroupRepresentativeHome getUserGroupRepresentativeHome() throws java.rmi.RemoteException;
 
 //public java.util.Collection getGroupsContainedNotDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getChildGroupsInDirect(com.idega.user.data.Group p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 
 public java.util.Collection getUserGroupPluginsForGroupTypeString(java.lang.String p0) throws java.rmi.RemoteException;
 public java.util.Collection getUserGroupPluginsForGroupType(com.idega.user.data.GroupType p0) throws java.rmi.RemoteException;
 public java.lang.String getGroupType(java.lang.Class p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User getUserByID(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroups(java.lang.String[] p0,boolean p1)throws java.lang.Exception, java.rmi.RemoteException;
 public java.util.Collection getAllGroups() throws java.rmi.RemoteException;
 public com.idega.user.data.Group getGroupByGroupID(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 
 public void addUser(int p0,com.idega.user.data.User p1)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 //public java.util.Collection getGroupsContainedNotDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getChildGroupsInDirect(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException;
 
 public com.idega.user.data.GroupHome getGroupHome() throws java.rmi.RemoteException;
 public com.idega.user.data.Group createGroup(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;

 public com.idega.user.data.UserGroupPlugInHome getUserGroupPlugInHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 
 public java.util.Collection getUsersDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException,javax.ejb.FinderException;
 public java.util.Collection getUsersNotDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException,javax.ejb.FinderException;
 public java.util.Collection getUsersDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException;
 public java.util.Collection getUsers(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException;
 public java.util.Collection getUsersNotDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException;
 public Collection getUsersRecursive(int groupId) throws FinderException;
 public Collection getUsersRecursive(Group group) throws FinderException;
public java.util.Collection getUsers(com.idega.user.data.Group p0)throws javax.ejb.FinderException,java.rmi.RemoteException;
 
/**
   * Creates a group with the general grouptype and adds it under the default Domain (IBDomain)
 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
 */
  public Group createGroup(String name)throws CreateException,RemoteException;

/**
   * Creates a group with the general grouptype and adds it under the default Domain (IBDomain)
 * @see com.idega.user.business.GroupBusiness#createGroup(String, String, String)
 */
  public Group createGroup(String name,String description)throws CreateException,RemoteException;

  public com.idega.core.location.data.Address updateGroupMainAddressOrCreateIfDoesNotExist(Integer groupId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox) throws javax.ejb.CreateException,java.rmi.RemoteException;

  public com.idega.core.location.data.Address getGroupMainAddress(Group group) throws RemoteException;
  

  public  com.idega.core.contact.data.Phone[] getGroupPhones(Group group)throws RemoteException;
  public com.idega.core.contact.data.Phone getGroupPhone(Group group, int phoneTypeId) throws RemoteException;
  
  public com.idega.core.contact.data.Email getGroupEmail(Group group)throws NoEmailFoundException;
  
  public void updateGroupMail(Group group, String email) throws CreateException,RemoteException;
  
  public com.idega.core.contact.data.PhoneHome getPhoneHome();
  
  public void updateGroupPhone(Group group, int phoneTypeId, String phoneNumber) throws EJBException;

  public boolean isGroupRemovable(Group group); 
  
  public Collection getAllAllowedGroupTypesForChildren(int groupId, com.idega.idegaweb.IWUserContext iwc);
  
  public Collection getAllAllowedGroupTypesForChildren(Group group, com.idega.idegaweb.IWUserContext iwc);
  
  public String getNameOfGroupWithParentName(Group group);
	public GroupRelationHome getGroupRelationHome();
  
  public Collection getChildGroupsRecursiveResultFiltered(Group group, Collection groupTypesAsString, boolean complementSetWanted) throws java.rmi.RemoteException;
  public Collection getChildGroupsRecursiveResultFiltered(int groupId, Collection groupTypesAsString, boolean complementSetWanted) throws java.rmi.RemoteException;
  public Collection getUsersFromGroupRecursive(Group group) ;
  public Collection getUsersFromGroupRecursive(Group group, Collection groupTypesAsString, boolean complementSetWanted);

}
