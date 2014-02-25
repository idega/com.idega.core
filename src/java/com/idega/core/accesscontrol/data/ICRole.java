package com.idega.core.accesscontrol.data;

import com.idega.data.IDOEntity;
import com.idega.data.TreeableEntity;


public interface ICRole extends IDOEntity, TreeableEntity<ICRole>
{
 public java.lang.String getRoleDescriptionLocalizableKey();
 public java.lang.String getRoleKey();
 public java.lang.String getRoleNameLocalizableKey();
 public void setRoleDescriptionLocalizableKey(java.lang.String p0);
 public void setRoleKey(java.lang.String p0);
 public void setRoleNameLocalizableKey(java.lang.String p0);
}
