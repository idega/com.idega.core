package com.idega.data.genericentity;


public interface Group extends com.idega.data.IDOLegacyEntity
{
 public void addGroup(com.idega.data.genericentity.Group p0)throws java.sql.SQLException;
 public void addMember(com.idega.data.genericentity.Member p0)throws java.sql.SQLException;
 public com.idega.data.genericentity.Group[] getAllGroupsContainingMember(com.idega.data.genericentity.Member p0)throws java.sql.SQLException;
 public com.idega.data.genericentity.Group[] getAllGroupsContainingThis()throws java.sql.SQLException;
 public java.lang.String getDescription();
 public java.lang.String getExtraInfo();
 public java.lang.String getGroupType();
 public java.lang.String getGroupTypeValue();
 public java.lang.String getName();
 public void setDefaultValues();
 public void setDescription(java.lang.String p0);
 public void setExtraInfo(java.lang.String p0);
 public void setGroupType(java.lang.String p0);
 public void setName(java.lang.String p0);
}
