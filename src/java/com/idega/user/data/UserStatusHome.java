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

import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.data.IDOHome;


/**
 *
 *  Last modified: $Date$ by $Author$
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision$
 */
public interface UserStatusHome extends IDOHome {

	public UserStatus create() throws javax.ejb.CreateException;

	public UserStatus findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByUserId
	 */
	public Collection findAllByUserId(int id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllActiveByUserId
	 */
	public Collection findAllActiveByUserId(int id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByStatusId
	 */
	public Collection findAllByStatusId(int id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllActiveByStatusId
	 */
	public Collection findAllActiveByStatusId(int id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllActiveByGroupId
	 */
	public Collection findAllActiveByGroupId(int id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByGroupId
	 */
	public Collection findAllByGroupId(int id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByUserIdAndGroupId
	 */
	public Collection findAllByUserIdAndGroupId(int user_id, int group_id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllActiveByUserIdAndGroupId
	 */
	public List<UserStatus> findAllActiveByUserIdAndGroupId(int user_id, int group_id) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByUserIDAndStatusID
	 */
	public Collection findAllByUserIDAndStatusID(Integer userID, Integer statusID) throws FinderException;

	/**
	 * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllActiveByUserIDAndStatusID
	 */
	public Collection findAllActiveByUserIDAndStatusID(Integer userID, Integer statusID) throws FinderException;
}
