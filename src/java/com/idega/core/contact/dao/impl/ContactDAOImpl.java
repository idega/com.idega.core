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

import java.util.List;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.contact.dao.ContactDAO;
import com.idega.core.contact.data.bean.Email;
import com.idega.core.contact.data.bean.EmailType;
import com.idega.core.contact.data.bean.Phone;
import com.idega.core.contact.data.bean.PhoneType;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.data.bean.User;
import com.idega.util.ListUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("contactDAO")
@Transactional(readOnly = true)
public class ContactDAOImpl extends GenericDaoImpl implements ContactDAO {

	@Override
	@Transactional(readOnly = false)
	public Email createEmail(String address, EmailType type) {
		Email email = new Email();
		email.setAddress(address);
		email.setEmailType(type);
		persist(email);

		return email;
	}

	@Override
	public Email findEmailForUserByType(User user, EmailType type) {
		return type == null ? null : findEmailForUserByType(user, type.getUniqueName());
	}

	@Override
	public Email findEmailForUserByType(User user, String type) {
		if (user == null || StringUtil.isEmpty(type)) {
			return null;
		}

		return getSingleResult(Email.QUERY_FIND_BY_USER_AND_TYPE, Email.class, new Param("id", user.getId()));
	}

	@Override
	public EmailType getMainEmailType() {
		return getSingleResult("emailType.findByUniqueType", EmailType.class, new Param("uniqueName", EmailType.MAIN_EMAIL));
	}

	@Override
	public Email findEmailByAddress(String address) {
		List<Email> emails = getResultList("email.findByAddress", Email.class, new Param("address", address));
		if(ListUtil.isEmpty(emails)) {
			return null;
		}
		return emails.get(0);
	}

	@Override
	public Phone createPhone(String number, PhoneType type) {
		Phone phone = new Phone();
		phone.setNumber(number);
		phone.setPhoneType(type);
		persist(phone);
		return phone;
	}

	@Override
	public Phone findPhoneByNumber(String number) {
		if(StringUtil.isEmpty(number)) {
			return null;
		}
		List<Phone> phones = getResultList("phone.findPhoneByNumber", Phone.class, new Param("phoneNumber", number));
		if(ListUtil.isEmpty(phones)) {
			return null;
		}
		return phones.get(0);
	}

}