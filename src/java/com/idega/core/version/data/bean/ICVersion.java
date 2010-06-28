/**
 * 
 */
package com.idega.core.version.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.idega.user.data.bean.User;

@Entity
@Table(name = ICVersion.ENTITY_NAME)
public class ICVersion implements Serializable {

	private static final long serialVersionUID = -6558805663260153406L;

	public static final String ENTITY_NAME = "ic_version";
	public static final String COLUMN_VERSION_ID = "ic_version_id";
	private static final String COLUMN_PARENT_VERSION_ID = "parent_version_id";
	private static final String COLUMN_PARENT_HISTORY_ID = "parent_history_id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_NUMBER = "version_number";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_CREATED_TIMESTAMP = "created_timestamp";
	private static final String COLUMN_CREATED_BY_USER = "created_by_user";
	private static final String COLUMN_ITEM_ID = "ic_item_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_VERSION_ID)
	private Integer versionID;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_PARENT_VERSION_ID)
	private ICVersion parentVersion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_PARENT_HISTORY_ID)
	private ICVersion parentHistory;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_NUMBER)
	private String number;

	@Column(name = COLUMN_DESCRIPTION)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_CREATED_TIMESTAMP)
	private Date created;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_CREATED_BY_USER)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_ITEM_ID)
	private ICItem item;

	/**
	 * @return the versionID
	 */
	public Integer getId() {
		return this.versionID;
	}

	/**
	 * @param versionID
	 *          the versionID to set
	 */
	public void setId(Integer versionID) {
		this.versionID = versionID;
	}

	/**
	 * @return the parentVersion
	 */
	public ICVersion getParentVersion() {
		return this.parentVersion;
	}

	/**
	 * @param parentVersion
	 *          the parentVersion to set
	 */
	public void setParentVersion(ICVersion parentVersion) {
		this.parentVersion = parentVersion;
	}

	/**
	 * @return the parentHistory
	 */
	public ICVersion getParentHistory() {
		return this.parentHistory;
	}

	/**
	 * @param parentHistory
	 *          the parentHistory to set
	 */
	public void setParentHistory(ICVersion parentHistory) {
		this.parentHistory = parentHistory;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return this.number;
	}

	/**
	 * @param number
	 *          the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *          the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return this.created;
	}

	/**
	 * @param created
	 *          the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return this.createdBy;
	}

	/**
	 * @param createdBy
	 *          the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the item
	 */
	public ICItem getItem() {
		return this.item;
	}

	/**
	 * @param item
	 *          the item to set
	 */
	public void setItem(ICItem item) {
		this.item = item;
	}
}