/*
 * Created on 24.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOHome;

/**
 * @author aron
 *
 * UserStatusHome TODO Describe this type
 */
public interface UserStatusHome extends IDOHome {
    public UserStatus create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

    public UserStatus findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException, java.rmi.RemoteException;

    /**
     * @see com.idega.user.data.UserStatusBMPBean#ejbFindAll
     */
    public Collection findAll() throws FinderException;

    /**
     * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByUserId
     */
    public Collection findAllByUserId(int id) throws FinderException;

    /**
     * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByGroupId
     */
    public Collection findAllByGroupId(int id) throws FinderException;

    /**
     * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByUserIdAndGroupId
     */
    public Collection findAllByUserIdAndGroupId(int user_id, int group_id)
            throws FinderException;

    /**
     * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByUserIdAndGroupId
     */
    public Collection findAllActiveByUserIdAndGroupId(int user_id, int group_id)
            throws FinderException;

    /**
     * @see com.idega.user.data.UserStatusBMPBean#ejbFindAllByUserIDAndStatusID
     */
    public Collection findAllByUserIDAndStatusID(Integer userID,
            Integer statusID) throws FinderException;

}
