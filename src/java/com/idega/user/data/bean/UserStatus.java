/**
 * 
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = UserStatus.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "userStatus.findAll", query = "select us from UserStatus us"),
	@NamedQuery(name = "userStatus.findAllByUser", query = "select us from UserStatus us where us.user = :user order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByUser", query = "select us from UserStatus us where us.user = :user and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByGroup", query = "select us from UserStatus us where us.group = :group order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByGroup", query = "select us from UserStatus us where us.group = :group and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByStatus", query = "select us from UserStatus us where us.status = :status order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByStatus", query = "select us from UserStatus us where us.status = :status and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByUserAndGroup", query = "select us from UserStatus us where us.user = :user and us.group = :group order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByUserAndGroup", query = "select us from UserStatus us where us.user = :user and us.group = :group and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByUserAndStatus", query = "select us from UserStatus us where us.user = :user and us.status = :status order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByUserAndStatus", query = "select us from UserStatus us where us.user = :user and us.status = :status and us.dateTo is null order by us.dateFrom")
})
public class UserStatus implements Serializable {

	private static final long serialVersionUID = 2044595478957579919L;

	public static final String ENTITY_NAME = "ic_usergroup_status";
	public static final String COLUMN_USER_STATUS_ID = "ic_usergroup_status_id";
	private static final String COLUMN_STATUS = "status_id";
	private static final String COLUMN_USER = "ic_user_id";
	private static final String COLUMN_GROUP = "ic_group_id";
	private static final String COLUMN_DATE_FROM = "date_from";
	private static final String COLUMN_DATE_TO = "date_to";
	private static final String COLUMN_CREATED_BY = "created_by";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_USER_STATUS_ID)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = COLUMN_STATUS)
	private Status status;

	@ManyToOne
	@JoinColumn(name = COLUMN_USER)
	private User user;

	@ManyToOne
	@JoinColumn(name = COLUMN_GROUP)
	private Group group;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DATE_FROM)
	private Date dateFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DATE_TO)
	private Date dateTo;

	@ManyToOne
	@JoinColumn(name = COLUMN_CREATED_BY)
	private User createdBy;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer statusID) {
		this.id = statusID;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Date getDateFrom() {
		return this.dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return this.dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
}