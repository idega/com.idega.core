/*
 * Created on 13.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.data;


import com.idega.data.PrimaryKey;

/**
 * @author aron
 *
 * TopNodeGroupKey TODO Describe this type
 */
public class TopNodeGroupKey extends PrimaryKey {
   

    	private String COLUMN_USER_ID = TopNodeGroupBMPBean.COLUMN_USER_ID;
    	private String COLUMN_GROUP_ID = TopNodeGroupBMPBean.COLUMN_GROUP_ID;
    	
    	/**
    	 * @param scorecardID
    	 * @param holeID
    	 */
    	public TopNodeGroupKey(Object userID, Object groupID) {
    		this();
    		setUser(userID);
    		setGroup(groupID);
    	}
    	
    	public TopNodeGroupKey() {
    		super();
    	}
    	
    	public void setUser(Object userID) {
    		setPrimaryKeyValue(COLUMN_USER_ID, userID);
    	}

    	public Object getUser() {
    		return getPrimaryKeyValue(COLUMN_USER_ID);
    	}

    	public void setGroup(Object groupID) {
    		setPrimaryKeyValue(COLUMN_GROUP_ID, groupID);
    	}

    	public Object getGroup() {
    		return getPrimaryKeyValue(COLUMN_GROUP_ID);
    	}
    
}
