package com.idega.core.data;

/**
 * 
 * @deprecated Class replaced with com.idega.user.data.Group
*/
public interface GenericGroup extends com.idega.data.IDOLegacyEntity
{
 public void addGroup(int p0)throws java.sql.SQLException;
 public void addGroup(com.idega.core.data.GenericGroup p0)throws java.sql.SQLException;
 public void addUser(com.idega.core.user.data.User p0)throws java.sql.SQLException;
 public boolean equals(com.idega.core.data.GenericGroup p0);
 public boolean equals(com.idega.data.IDOLegacyEntity p0);
 public com.idega.core.data.GenericGroup findGroup(java.lang.String p0)throws java.sql.SQLException;
 public com.idega.core.data.GenericGroup[] getAllGroupsContained()throws java.sql.SQLException;
 public com.idega.core.data.GenericGroup[] getAllGroupsContainingThis()throws java.sql.SQLException;
 public java.util.List getAllGroupsContainingUser(com.idega.core.user.data.User p0)throws java.sql.SQLException;
 public java.lang.String getDescription();
 public java.lang.String getExtraInfo();
 public java.lang.String getGroupType();
 public java.lang.String getGroupTypeValue();
 public java.util.List getChildGroups(java.lang.String[] p0,boolean p1)throws java.sql.SQLException;
 public java.util.List getChildGroups()throws java.sql.SQLException;
 public java.util.List getParentGroups()throws java.sql.SQLException;
 public java.lang.String getName();
 public void removeGroup(com.idega.core.data.GenericGroup p0)throws java.sql.SQLException;
 public void removeGroup()throws java.sql.SQLException;
 public void removeGroup(int p0,boolean p1)throws java.sql.SQLException;
 public void removeUser(com.idega.core.user.data.User p0)throws java.sql.SQLException;
 public void setDefaultValues();
 public void setDescription(java.lang.String p0);
 public void setExtraInfo(java.lang.String p0);
 public void setGroupType(java.lang.String p0);
 public void setName(java.lang.String p0);
}
