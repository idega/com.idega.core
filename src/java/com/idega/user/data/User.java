package com.idega.user.data;

import javax.ejb.*;

public interface User extends com.idega.data.IDOEntity
{
 public java.lang.String getDisplayName() throws java.rmi.RemoteException;
 public void setDateOfBirth(java.sql.Date p0) throws java.rmi.RemoteException;
 public java.util.Collection getEmails() throws java.rmi.RemoteException;
 public void setSystemImageID(int p0) throws java.rmi.RemoteException;
 public java.lang.String getPersonalID() throws java.rmi.RemoteException;
 public void setGroupID(int p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getUserGroup() throws java.rmi.RemoteException;
 public void setFirstName(java.lang.String p0) throws java.rmi.RemoteException;
 public void setGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
 public java.lang.String getIDColumnName() throws java.rmi.RemoteException;
 public int getGenderID() throws java.rmi.RemoteException;
 public java.sql.Date getDateOfBirth() throws java.rmi.RemoteException;
 public com.idega.user.data.Group getGroup() throws java.rmi.RemoteException;
 public java.lang.String getMiddleName() throws java.rmi.RemoteException;
 public java.lang.String getLastName() throws java.rmi.RemoteException;
 public void addAddress(com.idega.core.data.Address p0)throws com.idega.data.IDOAddRelationshipException, java.rmi.RemoteException;
 public void removeAllAddresses()throws com.idega.data.IDORemoveRelationshipException, java.rmi.RemoteException;
 public java.util.Collection getAddresses() throws java.rmi.RemoteException;
 public void setPrimaryGroupID(java.lang.Integer p0) throws java.rmi.RemoteException;
 public void addPhone(com.idega.core.data.Phone p0)throws com.idega.data.IDOAddRelationshipException, java.rmi.RemoteException;
 public void setSystemImageID(java.lang.Integer p0) throws java.rmi.RemoteException;
 public void setPersonalID(java.lang.String p0) throws java.rmi.RemoteException;
 public void removeAllEmails()throws com.idega.data.IDORemoveRelationshipException, java.rmi.RemoteException;
 public void setDescription(java.lang.String p0) throws java.rmi.RemoteException;
 public int getSystemImageID() throws java.rmi.RemoteException;
 public java.util.Collection getPhones() throws java.rmi.RemoteException;
 public void setLastName(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.user.data.Group getPrimaryGroup() throws java.rmi.RemoteException;
 public void setGender(int p0) throws java.rmi.RemoteException;
 public int getGroupID() throws java.rmi.RemoteException;
 public void addEmail(com.idega.core.data.Email p0)throws com.idega.data.IDOAddRelationshipException, java.rmi.RemoteException;
 public void setDefaultValues() throws java.rmi.RemoteException;
 public java.lang.String getDescription() throws java.rmi.RemoteException;
 public void setMiddleName(java.lang.String p0) throws java.rmi.RemoteException;
 public java.lang.String getName() throws java.rmi.RemoteException;
 public java.lang.String getFirstName() throws java.rmi.RemoteException;
 public void setDisplayName(java.lang.String p0) throws java.rmi.RemoteException;
 public void setGender(java.lang.Integer p0) throws java.rmi.RemoteException;
 public void setPrimaryGroupID(int p0) throws java.rmi.RemoteException;
 public int getPrimaryGroupID() throws java.rmi.RemoteException;
 public void removeAllPhones()throws com.idega.data.IDORemoveRelationshipException, java.rmi.RemoteException;
}
