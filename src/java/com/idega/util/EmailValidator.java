/*
 * $Id: EmailValidator.java,v 1.1 2005/01/05 14:04:42 laddi Exp $
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

/**
 * Last modified: $Date: 2005/01/05 14:04:42 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class EmailValidator {

	private static EmailValidator _instance = null;
	
	/**
	 * A method to get an instance of this class.
	 * 
	 * @return An instance of the EmailValidator class.
	 */
	public static EmailValidator getInstance() {
		if (_instance == null)
			_instance = new EmailValidator();

		return _instance;
	}

	/**
	 * Validates email using regular expressions
	 * 
	 * @param email	The email to validate
	 * @return	A boolean (true/false) depending on if the email is valid or not
	 */
	public boolean validateEmail(String email) {
		Pattern pat = Pattern.compile("^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
		Matcher matcher = pat.matcher(email);
		return matcher.find();
	}
}