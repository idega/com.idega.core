package com.idega.core.version.data;

public interface ICItem extends com.idega.data.IDOEntity
{

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
	 * @uml.property name="currentOpenVersion"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	public com.idega.core.version.data.ICVersion getCurrentOpenVersion();

 public int getCurrentOpenVersionID();

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

 public void initializeAttributes();

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
	 * @uml.property name="currentOpenVersion"
	 */
	public void setCurrentOpenVersion(com.idega.core.version.data.ICVersion p0);

 public void setCurrentOpenVersionID(int p0);

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

}
