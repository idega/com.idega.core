package com.idega.user.business;

import javax.ejb.*;

import com.idega.core.data.Email;
import com.idega.core.data.Phone;

public interface UserBusiness extends com.idega.business.IBOService
{
 public java.util.Collection getUsers()throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.user.data.User getUser(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getUserGroupsNotDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public void updateUserMail(int p0,java.lang.String p1)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public java.util.Collection listOfUserEmails(int p0) throws java.rmi.RemoteException;
 public void setUserUnderDomain(com.idega.builder.data.IBDomain p0,com.idega.user.data.User p1,com.idega.user.data.GroupDomainRelationType p2)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public void deleteUser(int p0)throws javax.ejb.RemoveException, java.rmi.RemoteException;
 public java.util.Collection getAllGroupsNotDirectlyRelated(int p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public com.idega.core.data.Email lookupEmail(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.core.data.Email getUserMail(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersInPrimaryGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.util.Collection getUsersInNoGroup()throws java.sql.SQLException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,com.idega.util.IWTimestamp p7,java.lang.Integer p8)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.user.data.UserHome getUserHome() throws java.rmi.RemoteException;
 public com.idega.core.data.AddressHome getAddressHome() throws java.rmi.RemoteException;
 public com.idega.core.data.Address getUserAddress1(int p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.core.data.PhoneHome getPhoneHome() throws java.rmi.RemoteException;
 public com.idega.user.data.User insertUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.Integer p5,com.idega.util.IWTimestamp p6,java.lang.Integer p7)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.user.data.User createUserWithLogin(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.Integer p5,com.idega.util.IWTimestamp p6,java.lang.Integer p7,java.lang.String p8,java.lang.String p9,java.lang.Boolean p10,com.idega.util.IWTimestamp p11,int p12,java.lang.Boolean p13,java.lang.Boolean p14,java.lang.Boolean p15,java.lang.String p16)throws javax.ejb.CreateException, java.rmi.RemoteException;
 public void updateUserPhone(int p0,int p1,java.lang.String p2)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.core.data.EmailHome getEmailHome() throws java.rmi.RemoteException;
 public com.idega.core.data.Address getUsersMainAddress(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.core.data.Phone[] getUserPhones(int p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void updateUser(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,com.idega.util.IWTimestamp p7,java.lang.Integer p8)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public java.util.Collection getUsersInGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public void updateUser(com.idega.user.data.User p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.Integer p6,com.idega.util.IWTimestamp p7,java.lang.Integer p8)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException;
 public com.idega.core.accesscontrol.data.LoginTable generateUserLogin(com.idega.user.data.User p0)throws java.lang.Exception, java.rmi.RemoteException;
 public void addNewUserEmail(int p0,java.lang.String p1) throws java.rmi.RemoteException;
 public void updateUserAddress1(int p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.Integer p4,java.lang.String p5,java.lang.Integer p6,java.lang.String p7)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public com.idega.core.accesscontrol.data.LoginTable generateUserLogin(int p0)throws java.lang.Exception, java.rmi.RemoteException;
 public java.util.Collection getUserGroups(com.idega.user.data.User p0,java.lang.String[] p1,boolean p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.user.business.GroupBusiness getGroupBusiness()throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void setPermissionGroup(com.idega.user.data.User p0,java.lang.Integer p1)throws java.rmi.RemoteException,com.idega.data.IDOStoreException, java.rmi.RemoteException;
 public java.util.Collection listOfUserGroups(int p0) throws java.rmi.RemoteException;
 public java.util.Collection getUserGroups(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException;
 public java.util.Collection getUserGroupsDirectlyRelated(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,com.idega.user.data.Gender p4,com.idega.util.IWTimestamp p5)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public java.util.Collection getAllUsersOrderedByFirstName()throws java.rmi.RemoteException,javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.core.data.Email getUserMail(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.GroupHome getGroupHome() throws java.rmi.RemoteException;
 public java.lang.Integer getGenderId(java.lang.String p0)throws java.lang.Exception, java.rmi.RemoteException;
 public java.util.Collection getUserGroupsDirectlyRelated(com.idega.user.data.User p0) throws java.rmi.RemoteException;
 public com.idega.core.data.Phone getUserPhone(int p0,int p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersInGroup(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User createUser(java.lang.String p0,java.lang.String p1,java.lang.String p2)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public java.util.Collection getUserGroups(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException;

public Email getUserMainEmail(com.idega.user.data.User user)throws NoEmailFoundException,java.rmi.RemoteException;
 public Phone getUsersHomePhone(com.idega.user.data.User p0)throws NoPhoneFoundException, java.rmi.RemoteException;
 public Phone getUsersWorkPhone(com.idega.user.data.User p0)throws NoPhoneFoundException, java.rmi.RemoteException;
 public Phone getUsersMobilePhone(com.idega.user.data.User p0)throws NoPhoneFoundException, java.rmi.RemoteException;
 public Phone getUsersFaxPhone(com.idega.user.data.User p0)throws NoPhoneFoundException, java.rmi.RemoteException;
/**
 * @return Correct name of the group or user or null if there was an error getting the name.
 * Gets the name of the group and explicitely checks if the "groupOrUser" and if it is a user it 
 * returns the correct name of the user. Else it regularely returns the name of the group.
 **/
  public String getNameOfGroupOrUser(com.idega.user.data.Group groupOrUser) throws java.rmi.RemoteException;

}
