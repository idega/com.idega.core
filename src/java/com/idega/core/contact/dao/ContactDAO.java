/*
 * $Id: ContactDAO.java 1.1 Sep 22, 2009 laddi Exp $
 * Created on Sep 22, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.dao;

import org.springframework.transaction.annotation.Transactional;

import com.idega.business.SpringBeanName;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.EmailType;
import com.idega.core.contact.data.bean.Phone;
import com.idega.core.contact.data.bean.PhoneType;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.User;

@SpringBeanName("contactDAO")
public interface ContactDAO extends GenericDao {

	@Transactional(readOnly = false)
	public Email createEmail(String address, EmailType type);

	public Email findEmailForUserByType(User user, String type);
	public Email findEmailForUserByType(User user, EmailType type);

	public EmailType getMainEmailType();
	
	public Email findEmailByAddress(String address);
	
	public Phone createPhone(String number, PhoneType type);
	
	public Phone findPhoneByNumber(String number);
}