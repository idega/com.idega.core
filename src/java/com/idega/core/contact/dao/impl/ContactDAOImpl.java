/*
 * $Id: ContactDAOImpl.java 1.1 Sep 22, 2009 laddi Exp $
 * Created on Sep 22, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.contact.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.contact.dao.ContactDAO;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.EmailType;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.data.bean.User;

@Scope("singleton")
@Repository("contactDAO")
@Transactional(readOnly = true)
public class ContactDAOImpl extends GenericDaoImpl implements ContactDAO {

	@Transactional(readOnly = false)
	public Email createEmail(String address, EmailType type) {
		Email email = new Email();
		email.setAddress(address);
		email.setEmailType(type);
		persist(email);
		
		return email;
	}

	public Email findEmailForUserByType(User user, EmailType type) {
		Param param1 = new Param("userID", user.getId());
		Param param2 = new Param("uniqueName", type.getUniqueName());
		
		return getSingleResult("email.findByUserAndType", Email.class, param1, param2);
	}
	
	public EmailType getMainEmailType() {
		return getSingleResult("emailType.findMainEmailType", EmailType.class);
	}
}