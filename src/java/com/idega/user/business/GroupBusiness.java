package com.idega.user.business;

import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.Group;
import com.idega.builder.data.IBDomain;
import java.rmi.RemoteException;
import javax.ejb.*;

public interface GroupBusiness extends com.idega.business.IBOService
{
 public java.util.Collection getGroupsContaining(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public void updateUsersInGroup(int p0,java.lang.String[] p1)throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContaining(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUsersContained(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainingNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedDirectlyRelated(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public void deleteGroup(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.RemoveException, java.rmi.RemoteException;
 public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getPermissionGroupHome() throws java.rmi.RemoteException;
 public java.util.Collection getGroups(java.lang.String[] p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.user.data.UserHome getUserHome() throws java.rmi.RemoteException;
 public java.util.Collection getRegisteredGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedDirectlyRelated(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainingDirectlyRelated(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContained(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getRegisteredGroups(com.idega.presentation.IWContext p0) throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContained(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUsersContainedNotDirectlyRelated(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUsersContained(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
// public com.idega.user.data.UserGroupRepresentativeHome getUserGroupRepresentativeHome() throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedNotDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContaining(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUsersForUserRepresentativeGroups(java.util.Collection p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.user.data.User getUserByID(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection getGroups(java.lang.String[] p0,boolean p1,com.idega.presentation.IWContext p2)throws java.lang.Exception, java.rmi.RemoteException;
 public java.util.Collection getAllGroups(com.idega.presentation.IWContext p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getGroupByGroupID(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public java.util.Collection getUsersContainedDirectlyRelated(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainingDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public void addUser(int p0,com.idega.user.data.User p1)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getGroupsContainedNotDirectlyRelated(int p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getGroupHome() throws java.rmi.RemoteException;
 public java.util.Collection getGroupsContained(com.idega.user.data.Group p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersContainedNotDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.FinderException,java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUsersContainedDirectlyRelated(com.idega.user.data.Group p0)throws javax.ejb.FinderException,java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public void deleteGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.RemoveException, java.rmi.RemoteException;

 public java.lang.String getGroupType(java.lang.Class p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.data.Group createGroup(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public void addGroupUnderDomain(IBDomain domain, Group group, GroupDomainRelationType type) throws CreateException,RemoteException;

}
