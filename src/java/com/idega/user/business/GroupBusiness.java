package com.idega.user.business;

import javax.ejb.*;

public interface GroupBusiness extends com.idega.business.IBOService
{
 public java.util.Collection getGroupsContaining(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.GroupType getGroupTypeFromString(java.lang.String p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public void updateUsersInGroup(int p0,java.lang.String[] p1)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContaining(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersContained(com.idega.user.data.Group p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainingNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersContained(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.Group createGroup(java.lang.String p0,java.lang.String p1,java.lang.String p2,int p3)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public void deleteGroup(com.idega.user.data.Group p0)throws javax.ejb.RemoveException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.user.data.GroupTypeHome getGroupTypeHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedNotDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getPermissionGroupHome() throws java.rmi.RemoteException;
 public java.util.Collection getGroups(java.lang.String[] p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.UserHome getUserHome() throws java.rmi.RemoteException;
 public java.util.Collection getRegisteredGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public java.util.Collection getUsersContainedNotDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedDirectlyRelated(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public void addGroupUnderDomain(com.idega.builder.data.IBDomain p0,com.idega.user.data.Group p1,com.idega.user.data.GroupDomainRelationType p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainingDirectlyRelated(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContained(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getRegisteredGroups(com.idega.presentation.IWContext p0) throws java.rmi.RemoteException;
 public java.util.Collection getUserGroupPluginsForUser(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersContainedDirectlyRelated(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getGroupHome(java.lang.String p0) throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContained(int p0)throws javax.ejb.EJBException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersContainedNotDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.user.data.UserGroupRepresentativeHome getUserGroupRepresentativeHome() throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContaining(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUserGroupPluginsForGroupTypeString(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getGroupType(java.lang.Class p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUserGroupPluginsForGroupType(com.idega.user.data.GroupType p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersForUserRepresentativeGroups(java.util.Collection p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.User getUserByID(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroups(java.lang.String[] p0,boolean p1,com.idega.presentation.IWContext p2)throws java.lang.Exception, java.rmi.RemoteException;
 public java.util.Collection getAllGroups(com.idega.presentation.IWContext p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getGroupByGroupID(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersContainedDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.EJBException,java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainingDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public void addUser(int p0,com.idega.user.data.User p1)throws javax.ejb.EJBException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedNotDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getGroupHome() throws java.rmi.RemoteException;
 public com.idega.user.data.Group createGroup(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.CreateException,java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.UserGroupPlugInHome getUserGroupPlugInHome()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContained(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void deleteGroup(int p0)throws javax.ejb.RemoveException,javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
}
