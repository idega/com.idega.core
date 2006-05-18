/*
 * $Id: Email.java,v 1.3 2006/05/18 11:43:43 laddi Exp $
 * Created on May 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.data;



/**
 * 
 *  Last modified: $Date: 2006/05/18 11:43:43 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
 */
public interface Email extends com.idega.data.IDOLegacyEntity, EmailDataView {

	/**
	 * @see com.idega.core.contact.data.EmailBMPBean#setEmailAddress
	 */
	public void setEmailAddress(String address);

	/**
	 * @see com.idega.core.contact.data.EmailBMPBean#getEmailAddress
	 */
	public String getEmailAddress();
	
	public void setEmailType(EmailType emailType);
	
	public EmailType getEmailType();

	/**
	 * @see com.idega.core.contact.data.EmailBMPBean#setEmailTypeId
	 */
	public void setEmailTypeId(int id);

	/**
	 * @see com.idega.core.contact.data.EmailBMPBean#getEmailTypeId
	 */
	public int getEmailTypeId();
}
