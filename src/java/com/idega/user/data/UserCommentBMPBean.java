/*
 * $Id: UserCommentBMPBean.java,v 1.1 2005/02/16 11:11:16 laddi Exp $
 * Created on 31.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.sql.Date;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;


/**
 * Last modified: $Date: 2005/02/16 11:11:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class UserCommentBMPBean extends GenericEntity  implements UserComment{

	private static final String ENTITY_NAME = "ic_user_comment";
	
	private static final String COLUMN_USER = "ic_user_id";
	private static final String COLUMN_COMMENT = "user_comment";
	private static final String COLUMN_CREATED = "created";
	private static final String COLUMN_CREATED_BY = "created_by";
	
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		
		addManyToOneRelationship(COLUMN_USER, User.class);
		addAttribute(COLUMN_COMMENT, "Comment", String.class, 4000);
		addAttribute(COLUMN_CREATED, "Created date", Date.class);
		addManyToOneRelationship(COLUMN_CREATED_BY, User.class);
	}
	
	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);
	}
	
	public String getComment() {
		return getStringColumnValue(COLUMN_COMMENT);
	}
	
	public Date getCreatedDate() {
		return getDateColumnValue(COLUMN_CREATED);
	}
	
	public User getCreatedBy() {
		return (User) getColumnValue(COLUMN_CREATED_BY);
	}
	
	public void setUser(User user) {
		setColumn(COLUMN_USER, user);
	}
	
	public void setComment(String comment) {
		setColumn(COLUMN_COMMENT, comment);
	}
	
	public void setCreatedDate(Date date) {
		setColumn(COLUMN_CREATED, date);
	}
	
	public void setCreatedBy(User user) {
		setColumn(COLUMN_CREATED_BY, user);
	}
	
	public Collection ejbFindAllByUser(User user) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn());
		query.addCriteria(new MatchCriteria(table, COLUMN_USER, MatchCriteria.EQUALS, user));
		query.addOrder(table, COLUMN_CREATED, false);
		
		return idoFindPKsByQuery(query);
	}
}