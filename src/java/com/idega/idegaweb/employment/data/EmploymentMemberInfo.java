package com.idega.idegaweb.employment.data;

import javax.ejb.*;

public interface EmploymentMemberInfo extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getBeganWork();
 public java.lang.String getCV();
 public java.lang.String getDateOfBirth();
 public java.lang.String getEducation();
 public java.lang.String getIDColumnName();
 public com.idega.data.genericentity.Member getMember();
 public java.lang.String getName();
 public java.lang.String getSchool();
 public java.lang.String getTitle();
 public void setBeganWork(java.lang.String p0);
 public void setCV(java.lang.String p0);
 public void setDateOfBirth(java.lang.String p0);
 public void setEducation(java.lang.String p0);
 public void setSchool(java.lang.String p0);
 public void setTitle(java.lang.String p0);
}
