/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = TopNodeGroup.ENTITY_NAME)
@Cacheable
public class TopNodeGroup implements Serializable {

	private static final long serialVersionUID = -441349910603306044L;

	public final static String ENTITY_NAME = "ic_user_topnodes";

	public static final String COLUMN_USER = "user_id";
	public static final String COLUMN_GROUP = "group_id";
	private static final String COLUMN_LOGIN_DURATION = "login_duration";
	private static final String COLUMN_NUMBER_OF_PERMISSIONS = "number_of_permissions";
	private static final String COLUMN_LAST_CHANGED = "last_changed";
	private static final String COLUMN_COMMENT = "tn_comment";

	@Id
	private TopNodeGroupPK pk = new TopNodeGroupPK();

	@Column(name = COLUMN_LOGIN_DURATION)
	private String loginDuration;

	@Column(name = COLUMN_NUMBER_OF_PERMISSIONS)
	private Integer numberOfPermissions;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_LAST_CHANGED)
	private Date lastChanged;

	@Column(name = COLUMN_COMMENT)
	private String comment;

	public TopNodeGroup(User user, Group group) {
		this.pk = new TopNodeGroupPK(user, group);
	}

	public TopNodeGroup() {}

	public TopNodeGroupPK getID() {
		return pk;
	}

	public Integer getUserID() {
		return this.pk.getUser().getId();
	}

	public User getUser() {
		return this.pk.getUser();
	}

	public void setUser(User user) {
		this.pk.setUser(user);
	}

	public Integer getGroupID() {
		return this.pk.getGroup().getID();
	}

	public Group getGroup() {
		return this.pk.getGroup();
	}

	public void setGroup(Group group) {
		this.pk.setGroup(group);
	}

	public String getLoginDuration() {
		return this.loginDuration;
	}

	public void setLoginDuration(String loginDuration) {
		this.loginDuration = loginDuration;
	}

	public Integer getNumberOfPermissions() {
		return this.numberOfPermissions;
	}

	public void setNumberOfPermissions(Integer numberOfPermissions) {
		this.numberOfPermissions = numberOfPermissions;
	}

	public Date getLastChanged() {
		return this.lastChanged;
	}

	public void setLastChanged(Date lastChanged) {
		this.lastChanged = lastChanged;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}