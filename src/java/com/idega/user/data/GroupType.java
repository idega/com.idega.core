package com.idega.user.data;


import com.idega.data.IDOEntity;
import com.idega.data.TreeableEntity;

public interface GroupType extends IDOEntity, TreeableEntity {
	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setVisibility
	 */
	public void setVisibility(boolean visible);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setType
	 */
	public void setType(String type);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getType
	 */
	public String getType();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setDescription
	 */
	public void setDescription(String desc);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getDescription
	 */
	public String getDescription();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setDefaultGroupName
	 */
	public void setDefaultGroupName(String name);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getDefaultGroupName
	 */
	public String getDefaultGroupName();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getNumberOfInstancesToAutoCreate
	 */
	public Integer getNumberOfInstancesToAutoCreate();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setNumberOfInstancesToAutoCreate
	 */
	public void setNumberOfInstancesToAutoCreate(Integer number);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getMaximumNumberOfInstances
	 */
	public Integer getMaximumNumberOfInstances();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setMaximumNumberOfInstances
	 */
	public void setMaximumNumberOfInstances(Integer max);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getMaximumNumberOfInstancesPerParent
	 */
	public Integer getMaximumNumberOfInstancesPerParent();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setMaximumNumberOfInstancesPerParent
	 */
	public void setMaximumNumberOfInstancesPerParent(Integer max);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getPrimaryKeyClass
	 */
	public Class getPrimaryKeyClass();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setGroupTypeAsGeneralGroup
	 */
	public void setGroupTypeAsGeneralGroup();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setGroupTypeAsPermissionGroup
	 */
	public void setGroupTypeAsPermissionGroup();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setGroupTypeAsAliasGroup
	 */
	public void setGroupTypeAsAliasGroup();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getVisibility
	 */
	public boolean getVisibility();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getOnlySupportsSameTypeChildGroups
	 */
	public boolean getOnlySupportsSameTypeChildGroups();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setOnlySupportsSameTypeChildGroups
	 */
	public void setOnlySupportsSameTypeChildGroups(
			Boolean onlySupportsSameTypeChildGroups);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getSupportsSameTypeChildGroups
	 */
	public boolean getSupportsSameTypeChildGroups();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setSupportsSameTypeChildGroups
	 */
	public void setSupportsSameTypeChildGroups(
			Boolean supportsSameTypeChildGroups);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getAutoCreate
	 */
	public boolean getAutoCreate();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setAutoCreate
	 */
	public void setAutoCreate(Boolean autoCreate);

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#getAllowsPermissions
	 */
	public boolean getAllowsPermissions();

	/**
	 * @see com.idega.user.data.GroupTypeBMPBean#setAllowsPermissions
	 */
	public void setAllowsPermissions(boolean allowsPermissions);
}