package com.idega.core.accesscontrol.business;

import java.util.StringTokenizer;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;

/**
 * Title: LoginCreator Description: Copyright: Copyright (c) 2000-2001 idega.is
 * All Rights Reserved Company: idega
 * 
 * @author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */
public class LoginCreator {

	public static String alpha = "aAáÁbBcCdDðÐeEéÉfFgGhHiIíÍjJkKlLmMnNoOóÓpPqQrRsStTuUúÚvVwWxXyYýÝzZþÞæÆöÖ";
	public static String beta = "aaaabbccddddeeeeffgghhiiiijjkkllmmnnooooppqqrrssttuuuuvvwwxxyyyyzzttaaoo";
	public static String dega = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
	public static String consonants = "bBcCdDfFgGhHjJkKlLmMnNpPqQrRsStTvVwWxXzZþÞ";
	public static String vowels = "aAáÁeEéÉiIíÍoOóÓuUúÚyYýÝæÆöÖ";
	private static final int ONE = 1, TWO = 2, THREE = 3;

	public LoginCreator() {
	}

	public static String createLogin(String name) {
		StringTokenizer st = new StringTokenizer(name);
		int TYPE = 0;
		int tokenCount = st.countTokens();
		int firstSize = 0;
		int thirdIndex = 0;
		String first = null, second = null, third = null;
		if (st.hasMoreTokens()) {
			first = st.nextToken();
			firstSize = first.length();
		}
		if (st.hasMoreTokens()) {
			second = st.nextToken();
		}
		if (st.hasMoreTokens()) {
			third = st.nextToken();
		}
		if (tokenCount == 2) {
			third = second;
		}
		StringBuffer sb = new StringBuffer();
		int index = -1;
		if (firstSize <= 7) {
			TYPE = ONE;
			for (int i = 0; i < first.length(); i++) {
				index = alpha.indexOf(String.valueOf(first.charAt(i)));
				if (index >= 0) {
					sb.append(beta.charAt(index));
				}
			}
			if (third != null) {
				index = alpha.indexOf(String.valueOf(third.charAt(thirdIndex)));
				if (index >= 0) {
					sb.append(beta.charAt(index));
				}
			}
		}
		else if (firstSize > 7 && tokenCount == 2) {
			TYPE = TWO;
			for (int i = 0; i < 5; i++) {
				index = alpha.indexOf(String.valueOf(first.charAt(i)));
				if (index >= 0) {
					sb.append(beta.charAt(index));
				}
			}
			if (second != null) {
				index = alpha.indexOf(String.valueOf(third.charAt(thirdIndex)));
				if (index >= 0) {
					sb.append(beta.charAt(index));
				}
			}
		}
		else if (tokenCount >= 3) {
			TYPE = THREE;
			index = alpha.indexOf(String.valueOf(first.charAt(0)));
			if (index != -1) {
				sb.append(beta.charAt(index));
			}
			index = alpha.indexOf(String.valueOf(second.charAt(0)));
			if (index != -1) {
				sb.append(beta.charAt(index));
			}
			index = alpha.indexOf(String.valueOf(third.charAt(thirdIndex)));
			if (index != -1) {
				sb.append(beta.charAt(index));
			}
		}
		while (checkLoginExistence(sb.toString())) {
			switch (TYPE) {
				case ONE:
				case TWO:
				case THREE:
					if (thirdIndex < third.length()) {
						sb.append(third.charAt(thirdIndex++));
					}
					break;
			}
			// System.err.println(sb.toString());
		}
		// System.err.println(sb.toString());
		return sb.toString();
	}

	public static boolean checkLoginExistence(String login) {
		try {
			LoginTableHome home = (LoginTableHome) IDOLookup.getHome(LoginTable.class);
			return home.getNumberOfLogins(login) > 0;
		}
		catch (IDOException ex) {
			ex.printStackTrace();
			return false;
		}
		catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public static String createPasswd(int NumberOfCharacters) {
		StringBuffer passwd = new StringBuffer(NumberOfCharacters);
		int len = dega.length();
		for (int i = 0; i < NumberOfCharacters; i++) {
			int num = (int) (Math.random() * len);
			passwd.append(dega.charAt(num));
		}
		return passwd.toString();
	}
}
