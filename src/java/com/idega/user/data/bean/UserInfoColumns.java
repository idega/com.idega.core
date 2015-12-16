/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = UserInfoColumns.ENTITY_NAME)
@NamedQuery(name = "userInfoColumns.findAllByUserAndGroup", query = "select uic from UserInfoColumns uic where uic.user = :user and uic.group = :group")
@Cacheable
public class UserInfoColumns implements Serializable {

	private static final long serialVersionUID = 2934493674581851825L;

	public static final String ENTITY_NAME = "ic_user_info_columns";
	public static final String COLUMN_USER_INFO_COLUMNS_ID = "ic_user_info_columns_id";
	private static final String COLUMN_USER = "ic_user_id";
	private static final String COLUMN_GROUP = "ic_group_id";
	private static final String COLUMN_INFO_1 = "user_info_1";
	private static final String COLUMN_INFO_2 = "user_info_2";
	private static final String COLUMN_INFO_3 = "user_info_3";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_USER_INFO_COLUMNS_ID)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = COLUMN_USER)
	private User user;

	@ManyToOne
	@JoinColumn(name = COLUMN_GROUP)
	private Group group;

	@Column(name = COLUMN_INFO_1)
	private String userInfo1;

	@Column(name = COLUMN_INFO_2)
	private String userInfo2;

	@Column(name = COLUMN_INFO_3)
	private String userInfo3;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getUserInfo1() {
		return this.userInfo1;
	}

	public void setUserInfo1(String userInfo1) {
		this.userInfo1 = userInfo1;
	}

	public String getUserInfo2() {
		return this.userInfo2;
	}

	public void setUserInfo2(String userInfo2) {
		this.userInfo2 = userInfo2;
	}

	public String getUserInfo3() {
		return this.userInfo3;
	}

	public void setUserInfo3(String userInfo3) {
		this.userInfo3 = userInfo3;
	}
}