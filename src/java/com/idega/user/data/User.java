package com.idega.user.data;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJBException;

import com.idega.core.accesscontrol.data.ICRole;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.data.ICLanguage;
import com.idega.core.location.data.Address;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOEntity;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOReportableEntity;

public interface User extends IDOEntity, Group, com.idega.core.user.data.User, IDOReportableEntity {

 public static final String FIELD_USER_ID = "IC_USER_ID";
 public static final String FIELD_FIRST_NAME = "FIRST_NAME";
 public static final String FIELD_MIDDLE_NAME = "MIDDLE_NAME";
 public static final String FIELD_LAST_NAME = "LAST_NAME";
 public static final String FIELD_DISPLAY_NAME = "DISPLAY_NAME";
 public static final String FIELD_DESCRIPTION = "DESCRIPTION";
 public static final String FIELD_DATE_OF_BIRTH = "DATE_OF_BIRTH";
 public static final String FIELD_GENDER = "IC_GENDER_ID";
 public static final String FIELD_SYSTEM_IMAGE_ID = "SYSTEM_IMAGE_ID";
 public static final String FIELD_PRIMARY_GROUP_ID = "PRIMARY_GROUP";
 public static final String FIELD_PERSONAL_ID = "PERSONAL_ID";
 public static final String FIELD_HOME_PAGE_ID = "HOME_PAGE_ID";
 public static final String FIELD_DELETED = "DELETED";
 public static final String FIELD_DELETED_BY = "DELETED_BY";
 public static final String FIELD_DELETED_WHEN = "DELETED_WHEN";
 public static final String USER_GROUP_TYPE="ic_user_representative";
 public static final String FIELD_JURIDICAL_PERSON = "juridical_person";

 public static final String FIELD_SHA1 = "sha1",
		 					FIELD_SHA1_INDEX = "IDX_IC_USER_SHA1";

 @Override
public void setPrimaryGroupID(java.lang.Integer p0);
 @Override
public int getHomePageID();
 @Override
public void setDateOfBirth(java.sql.Date p0);
 //public java.lang.String getNameLastFirst();
 @Override
public void setHomePageID(int p0);
 @Override
public Collection<Email> getEmails();
 @Override
public com.idega.core.builder.data.ICPage getHomePage();
 @Override
public Collection<Phone> getPhones();
 @Override
public Collection<Phone> getPhones(String phoneTypeID);
 @Override
public void setGender(java.lang.Integer p0);
 public void addUser(com.idega.user.data.User p0);
 @Override
public void setFirstName(java.lang.String p0);
 @Override
public void addGroup(com.idega.user.data.Group p0)throws javax.ejb.EJBException;
 @Override
public Group getChildAtIndex(int p0);
 @Override
public void setGender(int p0);
 @Override
public void setCreated(java.sql.Timestamp p0);
 @Override
public java.sql.Date getDateOfBirth();
 @Override
public java.lang.String getFirstName();
 public void removeAddress(com.idega.core.location.data.Address p0)throws com.idega.data.IDORemoveRelationshipException;
 @Override
public int getSystemImageID();
public int getIndex(User p0);
 @Override
public void setSystemImageID(java.lang.Integer p0);
 @Override
public void setLastName(java.lang.String p0);
 @Override
public void setGroupType(java.lang.String p0);
 @Override
public int getNodeID();
 @Override
public int getPrimaryGroupID();
 @Override
public void setExtraInfo(java.lang.String p0);
 @Override
public int getChildCount();
 @Override
public void addEmail(com.idega.core.contact.data.Email p0)throws com.idega.data.IDOAddRelationshipException;
 @Override
public boolean isLeaf();
 public void setFullName(java.lang.String p0);
 public void removeEmail(com.idega.core.contact.data.Email p0)throws com.idega.data.IDORemoveRelationshipException;
 public List<Group> getListOfAllGroupsContaining(int p0)throws javax.ejb.EJBException;
 @Override
 public Iterator<Group> getChildrenIterator();
 public Collection<Address> getAddresses();
 @Override
public boolean isUser();
 @Override
public java.lang.String getGroupType();
 @Override
public java.lang.String getName();
 @Override
public java.lang.String getExtraInfo();
 @Override
public java.lang.String getPersonalID();
 @Override
public void setPersonalID(java.lang.String p0);
 @Override
public void setPrimaryGroupID(int p0);
 @Override
public java.lang.String getNodeName();
 @Override
public void setHomePageID(java.lang.Integer p0);
 @Override
public void setSystemImageID(int p0);
 public void removeGroup(com.idega.user.data.Group p0)throws javax.ejb.EJBException;
 @Override
public List<Group> getParentGroups();
 @Override
public Collection<Group> getAllGroupsContainingUser(com.idega.user.data.User p0)throws javax.ejb.EJBException;
 @Override
public List<Group> getChildGroups(java.lang.String[] p0,boolean p1)throws javax.ejb.EJBException;
 @Override
public java.lang.String getLastName();
 @Override
public int getGroupID();
 @Override
public java.lang.String getGroupTypeKey();
 @Override
public void addAddress(com.idega.core.location.data.Address p0)throws com.idega.data.IDOAddRelationshipException;
 @Override
public void setDescription(java.lang.String p0);
 @Override
public void setDisplayName(java.lang.String p0);
 @Override
public void addGroup(int p0)throws javax.ejb.EJBException;
 public void removeGroup(int p0,boolean p1)throws javax.ejb.EJBException;
 public void removeGroup()throws javax.ejb.EJBException;
 @Override
public List<Group> getChildGroups()throws javax.ejb.EJBException;
 public com.idega.user.data.Group getUserGroup();
 @Override
public User getParentNode();
 @Override
public java.lang.String getDisplayName();
 public void removeUser(com.idega.user.data.User p0);
 public com.idega.user.data.Group getGroup();
 @Override
public boolean getAllowsChildren();
 @Override
public java.lang.String getGroupTypeDescription();
 @Override
public java.lang.String getDescription();
 @Override
public int getGenderID();
 public com.idega.user.data.Gender getGender();
 public com.idega.user.data.Group getPrimaryGroup();
 @Override
public java.sql.Timestamp getCreated();
 @Override
public void setHomePage(com.idega.core.builder.data.ICPage p0);
 public void removeAllAddresses()throws com.idega.data.IDORemoveRelationshipException;
 @Override
public void addPhone(com.idega.core.contact.data.Phone p0)throws com.idega.data.IDOAddRelationshipException;
 public void initializeAttributes();
 public boolean getGroupTypeVisibility();
 @Override
public void setGroupID(int p0);
 public void removePhone(com.idega.core.contact.data.Phone p0)throws com.idega.data.IDORemoveRelationshipException;
 @Override
public java.lang.String getMiddleName();
 @Override
public void setMiddleName(java.lang.String p0);
 //public java.lang.String getNameLastFirst(boolean p0);
 public void removeAllEmails()throws com.idega.data.IDORemoveRelationshipException;
 public void removeAllPhones()throws com.idega.data.IDORemoveRelationshipException;
 public void setPrimaryGroup(com.idega.user.data.Group p0);
 @Override
public java.lang.String getGroupTypeValue();
 @Override
public java.lang.String getIDColumnName();
 public boolean getDeleted();
 public void setDeleted(boolean isDeleted);
 public int getDeletedBy();
 public void setDeletedBy(int userId);
 public java.sql.Timestamp getDeletedWhen();
 public void setDeletedWhen(java.sql.Timestamp p0);
 public void delete(int p0)throws java.sql.SQLException;
 public boolean equals(com.idega.data.IDOEntity entity);
 public com.idega.core.localisation.data.ICLanguage getNativeLanguage();
 public void setNativeLanguage(int ICLanguageID);
 public void setNativeLanguage(com.idega.core.localisation.data.ICLanguage language);
 public boolean isDeceased();
 public void setFamilyID(String familyID);
 public String getFamilyID();
 public void setPreferredLocale(String preferredLocale);
 public void setPreferredRole(ICRole preferredRole);
 public String getPreferredLocale();
 public ICRole getPreferredRole();
 public void setUserProperties(ICFile file);
 public ICFile getUserProperties();
 public Address getUsersMainAddress() throws EJBException, RemoteException ;
 public Phone getUsersHomePhone() throws EJBException, RemoteException ;
 public Phone getUsersWorkPhone() throws EJBException, RemoteException ;
 public Phone getUsersMobilePhone() throws EJBException, RemoteException ;
 public Phone getUsersFaxPhone() throws EJBException, RemoteException ;
 public Email getUsersEmail() throws EJBException, RemoteException ;
 public boolean isJuridicalPerson();
 public void setJuridicalPerson(boolean juridicalPerson);
 public boolean getDisplayNameSetManually();
 public void setDisplayNameSetManually(boolean diplayNameSetManually);
 public void setLastReadFromImport(Timestamp timestamp);
 public Timestamp getLastReadFromImport();
 public void setResume(String resume);
 public String getResume();
 public Collection<ICLanguage> getLanguages() throws IDORelationshipException;
 public void addLanguage(ICLanguage language) throws IDOAddRelationshipException;
 public void removeLanguage(ICLanguage language) throws IDORemoveRelationshipException;

 public String getSHA1();
 public void setSHA1(String sha1);
 
 public Collection<User> getRelatedUsers(Collection<String> relationTypes);
}
