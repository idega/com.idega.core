/*
 * $Id: EmailTypeHomeImpl.java,v 1.2 2006/05/17 16:40:00 thomas Exp $
 * Created on May 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.data;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOException;
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookupException;


/**
 * 
 *  Last modified: $Date: 2006/05/17 16:40:00 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class EmailTypeHomeImpl extends IDOFactory implements EmailTypeHome {

	protected Class getEntityInterfaceClass() {
		return EmailType.class;
	}

	public EmailType create() throws javax.ejb.CreateException {
		return (EmailType) super.createIDO();
	}

	public EmailType findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (EmailType) super.findByPrimaryKeyIDO(pk);
	}
	
	public EmailType findEmailTypeByUniqueName(String  uniqueName) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((EmailTypeBMPBean) entity).ejbFindEmailTypeByUniqueName(uniqueName);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}	
	
	public EmailType findMainEmailType() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((EmailTypeBMPBean) entity).ejbFindMainEmailType();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public boolean updateStartData() throws IDOException, IDOLookupException, CreateException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		boolean theReturn = ((EmailTypeBMPBean) entity).ejbHomeUpdateStartData();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}
}
