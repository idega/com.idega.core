package com.idega.user.data;


import com.idega.data.IDOEntity;

public interface UserInfoColumns extends IDOEntity {

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#getUserId
	 */
	public int getUserId();

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#setUserId
	 */
	public void setUserId(int id);

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#setUser
	 */
	public void setUser(User user);

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#getGroupId
	 */
	public int getGroupId();

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#getGroup
	 */
	public Group getGroup();

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#setGroupId
	 */
	public void setGroupId(int id);

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#setGroup
	 */
	public void setGroup(Group group);

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#getUserInfo
	 */
	public String getUserInfo();

	/**
	 * @see com.idega.user.data.UserInfoColumnsBMPBean#setUserInfo
	 */
	public void setUserInfo(String userInfo);
}