/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.idega.util.DBUtil;

@Entity
@Table(name = UserStatus.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = UserStatus.QUERY_FIND_ALL, query = "select us from UserStatus us"),
	@NamedQuery(name = "userStatus.findAllByUser", query = "select us from UserStatus us where us.user = :user order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByUser", query = "select us from UserStatus us where us.user = :user and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByGroup", query = "select us from UserStatus us where us.group = :group order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByGroup", query = "select us from UserStatus us where us.group = :group and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByStatus", query = "select us from UserStatus us where us.status = :status order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByStatus", query = "select us from UserStatus us where us.status = :status and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByUserAndGroup", query = "select us from UserStatus us where us.user = :user and us.group = :group order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByUserAndGroup", query = "select us from UserStatus us where us.user = :user and us.group = :group and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllByUserAndStatus", query = "select us from UserStatus us where us.user = :user and us.status = :status order by us.dateFrom"),
	@NamedQuery(name = "userStatus.findAllActiveByUserAndStatus", query = "select us from UserStatus us where us.user = :user and us.status = :status and us.dateTo is null order by us.dateFrom"),
	@NamedQuery(
			name = UserStatus.QUERY_FIND_STATUSES_IN_GROUPS,
			query = "select us from UserStatus us where us.status.statusKey in (:statusKeys) and (us.group.id in (:ids) or us.group.alias.id in (:ids)) and us.dateTo is null order by us.status.statusOrder, us.dateFrom desc"
	),
	@NamedQuery(
			name = UserStatus.QUERY_FIND_STATUSES_IN_GROUPS_BY_USER_PERSONAL_ID,
			query = "select us from UserStatus us where us.user.personalID = :userPersonalId and us.status.statusKey in (:statusKeys) and (us.group.id in (:ids) or us.group.alias.id in (:ids)) and us.dateTo is null order by us.status.statusOrder, us.dateFrom desc"
	),
	@NamedQuery(
			name = UserStatus.QUERY_FIND_ALL_ACTIVE_IN_GROUPS,
			query = "select us from UserStatus us where (us.group.id in (:ids) or us.group.alias.id in (:ids)) and us.dateTo is null order by us.status.statusOrder, us.dateFrom desc"
	)
})
@Cacheable
public class UserStatus implements Serializable {

	private static final long serialVersionUID = 2044595478957579919L;

	public static final String	ENTITY_NAME = "ic_usergroup_status",

								QUERY_FIND_ALL = "userStatus.findAll",
								QUERY_FIND_STATUSES_IN_GROUPS = "userStatus.findStatusesInGroup",
								QUERY_FIND_ALL_ACTIVE_IN_GROUPS = "userStatus.findAllActiveInGroups",
										QUERY_FIND_STATUSES_IN_GROUPS_BY_USER_PERSONAL_ID = "userStatus.findStatusesInGroupsByUserPersonalId";

	public static final String COLUMN_USER_STATUS_ID = "ic_usergroup_status_id";
	private static final String COLUMN_STATUS = "status_id";
	public static final String COLUMN_USER = "ic_user_id";
	private static final String COLUMN_GROUP = "ic_group_id";
	private static final String COLUMN_DATE_FROM = "date_from";
	private static final String COLUMN_DATE_TO = "date_to";
	private static final String COLUMN_CREATED_BY = "created_by";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_USER_STATUS_ID)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_STATUS)
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_USER)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_GROUP)
	private Group group;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DATE_FROM)
	private Date dateFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = COLUMN_DATE_TO)
	private Date dateTo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_CREATED_BY)
	private User createdBy;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer statusID) {
		this.id = statusID;
	}

	public Status getStatus() {
		status = DBUtil.getInstance().lazyLoad(status);
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getUser() {
		user = DBUtil.getInstance().lazyLoad(user);
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		group = DBUtil.getInstance().lazyLoad(group);
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
		createdBy = DBUtil.getInstance().lazyLoad(createdBy);
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
}