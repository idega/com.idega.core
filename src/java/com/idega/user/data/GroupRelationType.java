package com.idega.user.data;


public interface GroupRelationType extends com.idega.data.IDOEntity
{
 public java.lang.String getDescription();
 public java.lang.String getIDColumnName();
 public java.lang.Class getPrimaryKeyClass();
 public java.lang.String getType();
 public void initializeAttributes();
 public void setDescription(java.lang.String p0);
 public void setType(java.lang.String p0);
}
