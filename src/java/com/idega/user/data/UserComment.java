/*
 * $Id: UserComment.java,v 1.1 2005/02/16 11:11:16 laddi Exp $
 * Created on 31.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.sql.Date;


import com.idega.data.IDOEntity;


/**
 * Last modified: $Date: 2005/02/16 11:11:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface UserComment extends IDOEntity {

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#getComment
	 */
	public String getComment();

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#getCreatedDate
	 */
	public Date getCreatedDate();

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#getCreatedBy
	 */
	public User getCreatedBy();

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#setUser
	 */
	public void setUser(User user);

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#setComment
	 */
	public void setComment(String comment);

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#setCreatedDate
	 */
	public void setCreatedDate(Date date);

	/**
	 * @see com.idega.user.data.UserCommentBMPBean#setCreatedBy
	 */
	public void setCreatedBy(User user);

}
