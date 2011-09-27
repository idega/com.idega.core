package com.idega.util.text;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Logger;

import com.idega.util.IWTimestamp;
import com.idega.util.StringUtil;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class SocialSecurityNumber {

	private String sSSN = null;

	public SocialSecurityNumber() {
	}

	public SocialSecurityNumber(String SSN) {
		this.sSSN = SSN;
	}

	public void setSocialSecurityNumber(String SSN) {
		this.sSSN = SSN;
	}

	public int getAge() {
		if (this.sSSN != null) {
			return getAge(this.sSSN);
		}
		else {
			return 0;
		}
	}

	public static int getAge(String socialSecurityNumber) {
		
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		int age;
		int year = Integer.parseInt(socialSecurityNumber.substring(4, 6));
		if (socialSecurityNumber.length() == 10) {
			if (socialSecurityNumber.endsWith("9")) {
				year += 1900;
			}
			if (socialSecurityNumber.endsWith("0")) {
				year += 2000;
			}
			if (socialSecurityNumber.endsWith("1")) {
				year += 2100; // in the future
			}
			if (socialSecurityNumber.endsWith("2")) {
				year += 2200;
			}
		}
		else {
			year += 1900;
		}
		age = thisYear - year;
		return age;
	}

	public static Date getDateFromSocialSecurityNumber(String socialSecurityNumber) {
		return getDateFromSocialSecurityNumber(socialSecurityNumber, true);
	}

	public static Date getDateFromSocialSecurityNumber(String socialSecurityNumber, boolean checkForValidity) {

		if (!checkForValidity || isValidIcelandicSocialSecurityNumber(socialSecurityNumber)) {
			int day = Integer.parseInt(socialSecurityNumber.substring(0, 2));
			int month = Integer.parseInt(socialSecurityNumber.substring(2, 4));
			int year = Integer.parseInt(socialSecurityNumber.substring(4, 6));
			int century = Integer.parseInt(socialSecurityNumber.substring(9));
			if (century == 9) {
				year += 1900;
			}
			if (century == 0) {
				year += 2000;
			}
			if (century == 1) {
				year += 2100;
			}
			if (century == 2) {
				year += 2200;
			}

			IWTimestamp stamp = new IWTimestamp(day, month, year);
			return stamp.getDate();
		}
		return null;
	}

	public static boolean isValidSocialSecurityNumber(String ssn, Locale locale) {
		if (StringUtil.isEmpty(ssn) || locale == null)
			return false;
			
		if (ssn != null && locale.equals(new Locale("is", "IS"))) {
			return isValidIcelandicSocialSecurityNumber(ssn);
		} else {
			Logger.getLogger(SocialSecurityNumber.class.getName()).warning("Social security number '" + ssn + "' can not be verified by locale " + locale +
					". Please implemente verifier!");
			return true;
		}
	}

	/**
	 * Checks for validity of an icelandic ssn
	 */
	public static boolean isValidIcelandicSocialSecurityNumber(String socialSecurityNumber) {
		if (socialSecurityNumber.length() != 10) {
			return (false);
		}

		try {
			int var1 = Integer.parseInt(socialSecurityNumber.substring(0, 1));
			int var2 = Integer.parseInt(socialSecurityNumber.substring(1, 2));
			int var3 = Integer.parseInt(socialSecurityNumber.substring(2, 3));
			int var4 = Integer.parseInt(socialSecurityNumber.substring(3, 4));
			int var5 = Integer.parseInt(socialSecurityNumber.substring(4, 5));
			int var6 = Integer.parseInt(socialSecurityNumber.substring(5, 6));
			int var7 = Integer.parseInt(socialSecurityNumber.substring(6, 7));
			int var8 = Integer.parseInt(socialSecurityNumber.substring(7, 8));
			int var9 = Integer.parseInt(socialSecurityNumber.substring(8, 9));

			int sum = (3 * var1) + (2 * var2) + (7 * var3) + (6 * var4) + (5 * var5) + (4 * var6) + (3 * var7) + (2 * var8);

			int result = sum % 11;
			int variable = 11 - result;

			if (variable == 10) {
				variable = 1;
			}
			else if (variable == 11) {
				variable = 0;
			}

			if (var9 == variable) {
				return (true);
			}
			else {
				return (false);
			}
		}
		catch (Exception e) {
			return (false);
		}
	}

	public static boolean isIndividualSocialSecurityNumber(String socialSecurityNumber, Locale locale) {
		if (isValidSocialSecurityNumber(socialSecurityNumber, locale)) {
			if (locale.equals(new Locale("is", "IS"))) {
				return isIcelandicIndividualSocialSecurityNumber(socialSecurityNumber);
			}
		}
		return false;
	}

	private static boolean isIcelandicIndividualSocialSecurityNumber(String socialSecurityNumber) {
		int var1 = Integer.parseInt(socialSecurityNumber.substring(0, 1));
		return var1 >= 0 && var1 <= 3;
	}

	public static boolean isCompanySocialSecurityNumber(String socialSecurityNumber, Locale locale) {
		if (isValidSocialSecurityNumber(socialSecurityNumber, locale)) {
			if (locale.equals(new Locale("is", "IS"))) {
				return isIcelandicCompanySocialSecurityNumber(socialSecurityNumber);
			}
		}
		return false;
	}

	private static boolean isIcelandicCompanySocialSecurityNumber(String socialSecurityNumber) {
		int var1 = Integer.parseInt(socialSecurityNumber.substring(0, 1));
		return var1 >= 4 && var1 <= 9;
	}

	public static void main(String[] arguments) throws Exception {
		System.out.println(isCompanySocialSecurityNumber("7101002090", new Locale("is", "IS")));
	}
}