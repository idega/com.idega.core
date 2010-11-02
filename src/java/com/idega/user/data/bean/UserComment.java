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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = UserComment.ENTITY_NAME)
@NamedQuery(name = "userComment.findAllByUser", query = "select uc from UserComment uc where uc.user = :user order by uc.created desc")
public class UserComment implements Serializable {

	private static final long serialVersionUID = -1060566573127705141L;

	public static final String ENTITY_NAME = "ic_user_comment";
	public static final String COLUMN_USER_COMMENT_ID = "ic_user_comment_id";
	private static final String COLUMN_USER = "ic_user_id";
	private static final String COLUMN_COMMENT = "user_comment";
	private static final String COLUMN_CREATED = "created";
	private static final String COLUMN_CREATED_BY = "created_by";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_USER_COMMENT_ID)
	private Integer userCommentID;

	@ManyToOne
	@JoinColumn(name = COLUMN_USER)
	private User user;

	@Column(name = COLUMN_COMMENT, length = 4000)
	private String comment;

	@Temporal(TemporalType.DATE)
	@Column(name = COLUMN_CREATED)
	private Date created;

	@ManyToOne
	@JoinColumn(name = COLUMN_CREATED_BY)
	private User createdBy;

	public Integer getId() {
		return this.userCommentID;
	}

	public void setId(Integer userCommentID) {
		this.userCommentID = userCommentID;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
}