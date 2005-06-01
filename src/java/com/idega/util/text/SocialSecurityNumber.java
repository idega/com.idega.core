package com.idega.util.text;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.idega.util.IWTimestamp;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author       <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class SocialSecurityNumber {
	private String sSSN = null;
	private int iSSN = 0;

	public SocialSecurityNumber() {
	}

	public SocialSecurityNumber(String SSN) {
		this.sSSN = SSN;
	}

	public void setSocialSecurityNumber(String SSN) {
		this.sSSN = SSN;
	}

	public int getAge() {
		if (this.sSSN != null)
			return getAge(this.sSSN);
		else
			return 0;
	}

	public static int getAge(String socialSecurityNumber) {
		int thisYear = new GregorianCalendar().YEAR;
		int age;
		int year = Integer.parseInt(socialSecurityNumber.substring(4, 6));
		if (socialSecurityNumber.length() == 10) {
			if (socialSecurityNumber.endsWith("9"))
				year += 1900;
			if (socialSecurityNumber.endsWith("0"))
				year += 2000;
			if (socialSecurityNumber.endsWith("1"))
				year += 2100; // in the future
			if (socialSecurityNumber.endsWith("2"))
				year += 2200;
		}
		else
			year += 1900;
		age = thisYear - year;
		return age;
	}

	public static Date getDateFromSocialSecurityNumber(String socialSecurityNumber) {
		return getDateFromSocialSecurityNumber(socialSecurityNumber, true);
	}	
	public static Date getDateFromSocialSecurityNumber(String socialSecurityNumber, boolean checkForValidity) {

		if ( !checkForValidity || isValidIcelandicSocialSecurityNumber(socialSecurityNumber) ) {
	      int day = Integer.parseInt(socialSecurityNumber.substring(0, 2));
	      int month = Integer.parseInt(socialSecurityNumber.substring(2, 4));
	      int year = Integer.parseInt(socialSecurityNumber.substring(4, 6));
	      int century = Integer.parseInt(socialSecurityNumber.substring(9));
				if (century == 9)
					year += 1900;
				if (century == 0)
					year += 2000;
				if (century == 1)
					year += 2100;
				if (century == 2)
					year += 2200;
					
				IWTimestamp stamp = new IWTimestamp(day,month,year);
				return stamp.getDate();
		}
	  	return null;
	}
	

	public static boolean isValidSocialSecurityNumber(String ssn,Locale locale){
		if(ssn!=null && locale.equals(new Locale("is","IS"))){
			return isValidIcelandicSocialSecurityNumber(ssn);
		}
		// TODO handle other system locales
		else
			return false;
		
	}

	/**
	 *  Checks for validity of an icelandic ssn
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
}