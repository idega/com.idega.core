package com.idega.user.data;

import javax.ejb.*;

public interface User extends com.idega.data.IDOLegacyEntity
{
 public java.sql.Date getDateOfBirth();
 public java.lang.String getDescription();
 public java.lang.String getDisplayName();
 public java.lang.String getFirstName();
 public int getGenderID();
 public int getGroupID();
 public java.lang.String getIDColumnName();
 public java.lang.String getLastName();
 public java.lang.String getMiddleName();
 public java.lang.String getName();
 public int getPrimaryGroupID();
 public int getSystemImageID();
 public void setDateOfBirth(java.sql.Date p0);
 public void setDefaultValues();
 public void setDescription(java.lang.String p0);
 public void setDisplayName(java.lang.String p0);
 public void setFirstName(java.lang.String p0);
 public void setGender(int p0);
 public void setGender(java.lang.Integer p0);
 public void setGroupID(int p0);
 public void setLastName(java.lang.String p0);
 public void setMiddleName(java.lang.String p0);
 public void setPrimaryGroupID(int p0);
 public void setPrimaryGroupID(java.lang.Integer p0);
 public void setSystemImageID(java.lang.Integer p0);
 public void setSystemImageID(int p0);
}
