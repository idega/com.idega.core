package com.idega.core.accesscontrol.data;

import javax.ejb.*;

public interface ICPermission extends com.idega.data.IDOEntity,com.idega.data.IDOLegacyEntity
{
 public java.lang.String getPermissionStringValue();
 public void setPermissionStringValue(java.lang.String p0);
 public java.lang.String getPermissionString();
 public java.lang.String getContextType();
 public int getGroupID();
 public void initializeAttributes();
 public void setPermissionValue(java.lang.Boolean p0);
 public void setGroupID(java.lang.Integer p0);
 public void setContextValue(java.lang.String p0);
 public void setPermissionValue(boolean p0);
 public void setGroupID(int p0);
 public boolean getPermissionValue();
 public void setContextType(java.lang.String p0);
 public void setPermissionString(java.lang.String p0);
 public java.lang.String getContextValue();
}
