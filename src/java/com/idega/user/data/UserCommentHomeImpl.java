/*
 * $Id: UserCommentHomeImpl.java,v 1.1 2005/02/16 11:11:16 laddi Exp $
 * Created on 31.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOFactory;


/**
 * Last modified: $Date: 2005/02/16 11:11:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class UserCommentHomeImpl extends IDOFactory implements UserCommentHome {

	protected Class getEntityInterfaceClass() {
		return UserComment.class;
	}

	public UserComment create() throws javax.ejb.CreateException {
		return (UserComment) super.createIDO();
	}

	public UserComment findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (UserComment) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllByUser(User user) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((UserCommentBMPBean) entity).ejbFindAllByUser(user);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

}
