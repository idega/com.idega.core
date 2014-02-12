/*
 * $Id: EmailValidator.java,v 1.2 2005/02/01 17:56:37 thomas Exp $
 * Created on 4.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * Last modified: $Date: 2005/02/01 17:56:37 $ by $Author: thomas $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class EmailValidator implements Singleton {

	private static Instantiator instantiator = new Instantiator() { @Override
	public Object getInstance() { return new EmailValidator();}};


	/**
	 * A method to get an instance of this class.
	 *
	 * @return An instance of the EmailValidator class.
	 */
	public static EmailValidator getInstance() {
		return (EmailValidator) SingletonRepository.getRepository().getInstance(EmailValidator.class, instantiator);
	}

	/**
	 * Validates email using regular expressions
	 *
	 * @param email	The email to validate
	 * @return	A boolean (true/false) depending on if the email is valid or not
	 */
	public boolean validateEmail(String email) {
		if (StringUtil.isEmpty(email)) {
			return false;
		}

		Pattern pat = Pattern.compile("^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
		Matcher matcher = pat.matcher(email);
		return matcher.find();
	}

	public boolean isValid(String email) {
		return validateEmail(email);
	}
}