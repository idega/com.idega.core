package com.idega.core.version.data;

public interface ICItemUpdate extends com.idega.data.IDOEntity
{

	/**
	 * 
	 * @uml.property name="description"
	 */
	public java.lang.String getDescription();

	/**
	 * 
	 * @uml.property name="updatedByUser"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	public com.idega.user.data.User getUpdatedByUser();

 public int getUpdatedByUserID();

	/**
	 * 
	 * @uml.property name="updatedTimestamp"
	 */
	public java.sql.Timestamp getUpdatedTimestamp();

	/**
	 * 
	 * @uml.property name="version"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	public com.idega.core.version.data.ICVersion getVersion();

 public int getVersionID();
 public void initializeAttributes();
 public void setCreatedTimestamp(java.sql.Timestamp p0);

	/**
	 * 
	 * @uml.property name="description"
	 */
	public void setDescription(java.lang.String p0);

	/**
	 * 
	 * @uml.property name="updatedByUser"
	 */
	public void setUpdatedByUser(com.idega.user.data.User p0);

 public void setUpdatedByUser(int p0);

	/**
	 * 
	 * @uml.property name="version"
	 */
	public void setVersion(com.idega.core.version.data.ICVersion p0);

 public void setVersionID(int p0);
}
