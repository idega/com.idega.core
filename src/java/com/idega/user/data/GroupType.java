package com.idega.user.data;

import com.idega.data.TreeableEntity;

public interface GroupType extends com.idega.data.IDOEntity, TreeableEntity<GroupType>
{
 public boolean getAutoCreate();
 public java.lang.String getDefaultGroupName();
 public java.lang.String getDescription();
 public java.lang.Integer getMaximumNumberOfInstances();
 public java.lang.Integer getMaximumNumberOfInstancesPerParent();
 public java.lang.Integer getNumberOfInstancesToAutoCreate();
 public java.lang.Class<String> getPrimaryKeyClass();
 public java.lang.String getType();
 public boolean getVisibility();
 public void setAutoCreate(java.lang.Boolean p0);
 public void setDefaultGroupName(java.lang.String p0);
 public void setDescription(java.lang.String p0);
 public void setGroupTypeAsAliasGroup();
 public void setGroupTypeAsGeneralGroup();
 public void setGroupTypeAsPermissionGroup();
 public void setMaximumNumberOfInstances(java.lang.Integer p0);
 public void setMaximumNumberOfInstancesPerParent(java.lang.Integer p0);
 public void setNumberOfInstancesToAutoCreate(java.lang.Integer p0);
 public void setType(java.lang.String p0);
 public void setVisibility(boolean p0);
 public boolean getOnlySupportsSameTypeChildGroups();
 public void setOnlySupportsSameTypeChildGroups(Boolean onlySupportsSameTypeChildGroups);
 public boolean getSupportsSameTypeChildGroups();
 public void setSupportsSameTypeChildGroups(Boolean supportsSameTypeChildGroups);
	public boolean getAllowsPermissions();
}
