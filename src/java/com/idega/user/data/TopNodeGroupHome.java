/*
 * Created on 13.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;
import com.idega.data.IDORelationshipException;

/**
 * @author aron
 *
 * TopNodeGroupHome TODO Describe this type
 */
public interface TopNodeGroupHome extends IDOHome {
    public TopNodeGroup create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

    public TopNodeGroup findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException, java.rmi.RemoteException;

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#ejbFindByPrimaryKey
     */
    public TopNodeGroup findByPrimaryKey(TopNodeGroupKey primaryKey)
            throws FinderException;

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#ejbCreate
     */
    public TopNodeGroup create(TopNodeGroupKey primaryKey)
            throws CreateException;

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#ejbCreate
     */
    public TopNodeGroup create(Integer userID, Integer groupID)
            throws CreateException;

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#ejbFindByUser
     */
    public Collection<TopNodeGroup> findByUser(Integer userID) throws FinderException;

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#ejbFindByUser
     */
    public Collection<TopNodeGroup> findByUser(User user) throws FinderException;

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#ejbFindByGroup
     */
    public Collection<TopNodeGroup> findByGroup(Integer groupID) throws FinderException;

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#ejbHomegetTopNodeGroups
     */
    public Collection<Group> getTopNodeGroups(User user)
            throws IDORelationshipException;

}
