package com.idega.data.genericentity;

import com.idega.data.IDOLegacyEntity;


public interface Member extends IDOLegacyEntity {
 public int getAge();
 public java.util.List getAllGroups()throws java.sql.SQLException;
 public java.sql.Date getDateOfBirth();
 public java.lang.String getFirstName();
 public java.lang.String getGender();
 public com.idega.data.genericentity.Group[] getGenericGroups()throws java.sql.SQLException;
 public int getImageId();
 public java.lang.String getLastName();
 public java.lang.String getMiddleName();
 public java.lang.String getName();
 public java.lang.String getSocialSecurityNumber();
 public void setDateOfBirth(java.sql.Date p0);
 public void setDefaultValues();
 public void setFirstName(java.lang.String p0);
 public void setGender(java.lang.String p0);
 public void setImageId(int p0);
 public void setLastName(java.lang.String p0);
 public void setMiddleName(java.lang.String p0);
 public void setSocialSecurityNumber(java.lang.String p0);
 public void setimage_id(java.lang.Integer p0);
}
