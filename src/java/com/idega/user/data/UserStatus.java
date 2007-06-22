/*
 * $Id$
 * Created on Jun 27, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.sql.Timestamp;
import com.idega.data.IDOEntity;


/**
 * 
 *  Last modified: $Date$ by $Author$
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision$
 */
public interface UserStatus extends IDOEntity {

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getStatusId
	 */
	public int getStatusId();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getStatus
	 */
	public Status getStatus();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setStatusId
	 */
	public void setStatusId(int id);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setStatus
	 */
	public void setStatus(Status status);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getUserId
	 */
	public int getUserId();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setUserId
	 */
	public void setUserId(int id);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setUser
	 */
	public void setUser(User user);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getCreatedBy
	 */
	public User getCreatedBy();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getCreatedById
	 */
	public int getCreatedById();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setCreatedBy
	 */
	public void setCreatedBy(int userID);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setCreatedBy
	 */
	public void setCreatedBy(Integer userID);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setCreatedBy
	 */
	public void setCreatedBy(User user);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getGroupId
	 */
	public int getGroupId();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getGroup
	 */
	public Group getGroup();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setGroupId
	 */
	public void setGroupId(int id);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setGroup
	 */
	public void setGroup(Group group);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setDateFrom
	 */
	public void setDateFrom(Timestamp from);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getDateFrom
	 */
	public Timestamp getDateFrom();

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#setDateTo
	 */
	public void setDateTo(Timestamp to);

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#getDateTo
	 */
	public Timestamp getDateTo();
}
