/*
 * Created on 13.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.data;

import java.sql.Timestamp;


import com.idega.data.IDOEntity;

/**
 * @author aron
 *
 * TopNodeGroup TODO Describe this type
 */
public interface TopNodeGroup extends IDOEntity {
    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#getPrimaryKeyClass
     */
    public Class getPrimaryKeyClass();

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#getUserId
     */
    public Integer getUserId();

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#setUserId
     */
    public void setUserId(Integer id);

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#getGroupId
     */
    public Integer getGroupId();

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#setGroupId
     */
    public void setGroupId(Integer id);

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#getLastChanged
     */
    public Timestamp getLastChanged();

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#setLastChanged
     */
    public void setLastChanged(Timestamp stamp);

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#getComment
     */
    public String getComment();

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#setComment
     */
    public void setComment(String comment);

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#getLoginDuration
     */
    public String getLoginDuration();

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#setLoginDuration
     */
    public void setLoginDuration(String login_duration);

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#getNumberOfPermissions
     */
    public Integer getNumberOfPermissions();

    /**
     * @see com.idega.user.data.TopNodeGroupBMPBean#setNumberOfPermissions
     */
    public void setNumberOfPermissions(Integer number_of_permissions);
}
