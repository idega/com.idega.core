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
import com.idega.util.DBUtil;

@Entity
@Table(name = ICItem.ENTITY_NAME)
public class ICItem implements Serializable {

	private static final long serialVersionUID = -1561410058296080122L;

	public static final String ENTITY_NAME = "ic_item";
	public static final String COLUMN_ITEM_ID = "ic_item_id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_CREATED_TIMESTAMP = "created_timestamp";
	private static final String COLUMN_CREATED_BY_USER = "created_by_user";
	private static final String COLUMN_CURRENT_OPEN_VERSION_ID = "current_open_version_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ITEM_ID)
	private Integer itemID;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_DESCRIPTION)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_CREATED_TIMESTAMP)
	private Date created;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_CREATED_BY_USER)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_CURRENT_OPEN_VERSION_ID)
	private ICVersion currentVersion;

	/**
	 * @return the itemID
	 */
	public Integer getId() {
		return this.itemID;
	}

	/**
	 * @param itemID
	 *          the itemID to set
	 */
	public void setId(Integer itemID) {
		this.itemID = itemID;
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
		createdBy = DBUtil.getInstance().lazyLoad(createdBy);
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
	 * @return the currentVersion
	 */
	public ICVersion getCurrentVersion() {
		currentVersion = DBUtil.getInstance().lazyLoad(currentVersion);
		return this.currentVersion;
	}

	/**
	 * @param currentVersion
	 *          the currentVersion to set
	 */
	public void setCurrentVersion(ICVersion currentVersion) {
		this.currentVersion = currentVersion;
	}
}