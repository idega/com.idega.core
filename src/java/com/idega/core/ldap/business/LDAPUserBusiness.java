package com.idega.core.ldap.business;


public interface LDAPUserBusiness extends com.idega.business.IBOService,com.idega.core.ldap.util.IWLDAPConstants
{
 public com.idega.user.data.User getUserByDirectoryString(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.user.data.User getUserByDirectoryString(org.codehaus.plexus.ldapserver.server.syntax.DirectoryString p0) throws java.rmi.RemoteException;
 public void setMetaDataFromLDAPAttributes(com.idega.user.data.User p0,com.idega.core.ldap.client.naming.DN p1,javax.naming.directory.Attributes p2) throws java.rmi.RemoteException;
 public com.idega.user.data.User createOrUpdateUser(com.idega.core.ldap.client.naming.DN p0,javax.naming.directory.Attributes p1,com.idega.user.data.Group p2)throws java.rmi.RemoteException,javax.ejb.CreateException,javax.naming.NamingException, java.rmi.RemoteException;
 public com.idega.user.data.User createOrUpdateUser(com.idega.core.ldap.client.naming.DN p0,javax.naming.directory.Attributes p1)throws javax.ejb.CreateException,javax.naming.NamingException,java.rmi.RemoteException, java.rmi.RemoteException;
 public java.util.Collection getUsersByLDAPAttribute(java.lang.String p0,java.lang.String p1) throws java.rmi.RemoteException;
}
