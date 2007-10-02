/*
 * $Id: EmailValidator.java,v 1.2.4.1 2007/10/02 14:38:31 idegaweb Exp $
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
 * Last modified: $Date: 2007/10/02 14:38:31 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2.4.1 $
 */
public class EmailValidator implements Singleton {

	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new EmailValidator();}};

	
//	RFC 2822 token definitions for valid email - only used together to form a java Pattern object:
	private static final String sp = "\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~";
	private static final String atext = "[a-zA-Z0-9" + sp + "]";
	private static final String atom = atext + "+"; //one or more atext chars 
	private static final String dotAtom = "\\." + atom;
	private static final String localPart = atom + "(" + dotAtom + ")*"; //one atom followed by 0 or more dotAtoms.

//	RFC 1035 tokens for domain names;
	private static final String letter = "[a-zA-Z]";
	private static final String letDig = "[a-zA-Z0-9]";
	private static final String letDigHyp = "[a-zA-Z0-9-]";
	public static final String rfcLabel = letDig + "(" + letDigHyp + "{0,61}" + letDig + ")?";
	private static final String domain = rfcLabel + "(\\." + rfcLabel + ")*\\." + letter + "{2,6}";

//	Combined together, these form the allowed email regexp allowed by RFC 2822:
	private static final String addrSpec = "^" + localPart + "@" + domain + "$";

//	now compile it:
	public static final Pattern VALID_PATTERN = Pattern.compile( addrSpec );
	
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
		//Pattern pat = Pattern.compile("^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$");
		Matcher matcher = VALID_PATTERN.matcher(email);
		return matcher.find();
	}
}