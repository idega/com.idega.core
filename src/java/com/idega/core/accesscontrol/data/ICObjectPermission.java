package com.idega.core.accesscontrol.data;


public interface ICObjectPermission extends com.idega.data.IDOLegacyEntity
{
 public int getArObjectID();
 public java.lang.String getDescription();
 public java.lang.String getPermissionType();
 public void setArObjectID(java.lang.Integer p0);
 public void setDescription(java.lang.String p0);
 public void setPermissionType(java.lang.String p0);
}
