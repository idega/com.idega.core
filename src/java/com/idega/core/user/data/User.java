package com.idega.core.user.data;


public interface User extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getDisplayName();
 public void setDateOfBirth(java.sql.Date p0);
 public void setSystemImageID(int p0);
 public void setGroupID(int p0);
 public void setFirstName(java.lang.String p0);
 public java.lang.String getIDColumnName();
 public int getGenderID();
 public java.sql.Date getDateOfBirth();
 public java.lang.String getMiddleName();
 public java.lang.String getLastName();
 public void setPrimaryGroupID(java.lang.Integer p0);
 public void setSystemImageID(java.lang.Integer p0);
 public void setDescription(java.lang.String p0);
 public int getSystemImageID();
 public void setLastName(java.lang.String p0);
 public void setGender(int p0);
 public int getGroupID();
 public void setDefaultValues();
 public java.lang.String getDescription();
 public void setMiddleName(java.lang.String p0);
 public java.lang.String getName();
 public java.lang.String getFirstName();
 public void setDisplayName(java.lang.String p0);
 public void setGender(java.lang.Integer p0);
 public void setPrimaryGroupID(int p0);
 public int getPrimaryGroupID();
 public java.lang.String getPersonalID();
 public void setPersonalID(java.lang.String p0);
}
