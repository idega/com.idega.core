package com.idega.core.version.data;

public interface ICVersion extends com.idega.data.IDOEntity,com.idega.core.data.ICTreeNode
{
 public boolean getAllowsChildren();
 public com.idega.core.data.ICTreeNode getChildAtIndex(int p0);
 public int getChildCount();

	/**
	 * 
	 * @uml.property name="children"
	 */
	public java.util.Iterator getChildren();

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

 public int getIndex(com.idega.core.data.ICTreeNode p0);

	/**
	 * 
	 * @uml.property name="name"
	 */
	public java.lang.String getName();

 public int getNodeID();

	/**
	 * 
	 * @uml.property name="nodeName"
	 */
	public java.lang.String getNodeName();

	/**
	 * 
	 * @uml.property name="number"
	 */
	public java.lang.String getNumber();

	/**
	 * 
	 * @uml.property name="parentNode"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	public com.idega.core.data.ICTreeNode getParentNode();

	/**
	 * 
	 * @uml.property name="parentVersion"
	 * @uml.associationEnd multiplicity="(0 1)" inverse="parentVersion:com.idega.core.version.data.ICVersion"
	 */
	public com.idega.core.version.data.ICVersion getParentVersion();

 public int getParentVersionID();
 public int getSiblingCount();
 public void initializeAttributes();
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
