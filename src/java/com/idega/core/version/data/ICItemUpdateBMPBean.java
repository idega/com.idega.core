/*
 * Created on 23.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.core.version.data;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.idega.data.GenericEntity;
import com.idega.user.data.User;

/**
 * Title:		ICItemUpdateBMPBean
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICItemUpdateBMPBean extends GenericEntity implements ICItemUpdate{

	private final static String COLUMNNAME_DESCRIPTION = "description";
	private final static String COLUMNNAME_UPDATED_TIMESTAMP = "updated_timestamp";
	private final static String COLUMNNAME_UPDATED_BY_USER = "updated_by_user";
	private final static String COLUMNNAME_VERSION_ID = "version_id";

	/**
	 * 
	 */
	public ICItemUpdateBMPBean() {
		super();
	}

	/**
	 * @param id
	 * @throws SQLException
	 */
	public ICItemUpdateBMPBean(int id) throws SQLException {
		super(id);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return "ic_item_update";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMNNAME_DESCRIPTION, "Description", true, true, String.class);
		addAttribute(COLUMNNAME_UPDATED_TIMESTAMP, "Created Timestamp", true, true, String.class);

		addManyToOneRelationship(COLUMNNAME_UPDATED_BY_USER, User.class);
		addManyToOneRelationship(COLUMNNAME_VERSION_ID, ICVersion.class);

	}

	/**
	 * @return
	 */
	public int getUpdatedByUserID() {
		return this.getIntColumnValue(COLUMNNAME_UPDATED_BY_USER);
	}

	/**
	 * @return
	 */
	public User getUpdatedByUser() {
		return (User)this.getColumnValue(COLUMNNAME_UPDATED_BY_USER);
	}

	/**
	 * @return
	 */
	public Timestamp getUpdatedTimestamp() {
		return (Timestamp)this.getColumnValue(COLUMNNAME_UPDATED_TIMESTAMP);
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return this.getStringColumnValue(COLUMNNAME_DESCRIPTION);
	}

	/**
	 * @return
	 */
	public int getVersionID() {
		return this.getIntColumnValue(COLUMNNAME_VERSION_ID);
	}

	/**
	 * @return
	 */
	public ICVersion getVersion() {
		return (ICVersion)this.getColumnValue(COLUMNNAME_VERSION_ID);
	}

	/**
	 * @param userID
	 */
	public void setUpdatedByUser(int userID) {
		this.setColumn(COLUMNNAME_UPDATED_BY_USER, userID);
	}

	/**
	 * @param user
	 */
	public void setUpdatedByUser(User user) {
		this.setColumn(COLUMNNAME_UPDATED_BY_USER, user);
	}

	/**
	 * @param time
	 */
	public void setCreatedTimestamp(Timestamp time) {
		this.setColumn(COLUMNNAME_UPDATED_TIMESTAMP, time);
	}

	/**
	 * @param desc
	 */
	public void setDescription(String desc) {
		this.setColumn(COLUMNNAME_DESCRIPTION, desc);
	}

	/**
	 * @param versionID
	 */
	public void setVersionID(int versionID) {
		this.setColumn(COLUMNNAME_VERSION_ID, versionID);
	}

	/**
	 * @param version
	 */
	public void setVersion(ICVersion version) {
		this.setColumn(COLUMNNAME_VERSION_ID, version);
	}

}
