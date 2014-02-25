package com.idega.core.version.data;

import com.idega.core.data.ICTreeNode;
import com.idega.data.IDOEntity;

public interface ICVersion extends IDOEntity, ICTreeNode<ICVersion> {
 @Override
public boolean getAllowsChildren();

 @Override
public int getChildCount();

	/**
	 *
	 * @uml.property name="createdByUser"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	public com.idega.user.data.User getCreatedByUser();

 public int getCreatedByUserID();

	/**
	 *
	 * @uml.property name="createdTimestamp"
	 */
	public java.sql.Timestamp getCreatedTimestamp();

	/**
	 *
	 * @uml.property name="description"
	 */
	public java.lang.String getDescription();

	/**
	 *
	 * @uml.property name="name"
	 */
	public java.lang.String getName();

 @Override
public int getNodeID();

	/**
	 *
	 * @uml.property name="nodeName"
	 */
	@Override
	public java.lang.String getNodeName();

	/**
	 *
	 * @uml.property name="number"
	 */
	public java.lang.String getNumber();

	/**
	 *
	 * @uml.property name="parentVersion"
	 * @uml.associationEnd multiplicity="(0 1)" inverse="parentVersion:com.idega.core.version.data.ICVersion"
	 */
	public com.idega.core.version.data.ICVersion getParentVersion();

 public int getParentVersionID();
 @Override
public int getSiblingCount();
 public void initializeAttributes();
 @Override
public boolean isLeaf();

	/**
	 *
	 * @uml.property name="createdByUser"
	 */
	public void setCreatedByUser(com.idega.user.data.User p0);

 public void setCreatedByUser(int p0);

	/**
	 *
	 * @uml.property name="createdTimestamp"
	 */
	public void setCreatedTimestamp(java.sql.Timestamp p0);

	/**
	 *
	 * @uml.property name="description"
	 */
	public void setDescription(java.lang.String p0);

	/**
	 *
	 * @uml.property name="name"
	 */
	public void setName(java.lang.String p0);

	/**
	 *
	 * @uml.property name="number"
	 */
	public void setNumber(java.lang.String p0);

	/**
	 *
	 * @uml.property name="parentVersion"
	 */
	public void setParentVersion(com.idega.core.version.data.ICVersion p0);

 public void setParentVersionID(int p0);
}
