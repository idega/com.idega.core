package com.idega.util;

import java.util.List;

/**
 * Creditcardnumber checker <br>
 * Visa <br>
 * MasterCard <br>
 * American Express <br>
 * Diner's Club <br>
 * Discover <br>
 * en Route <br>
 **/

public class CreditCardChecker {

	public static void main(String[] args) {
		String number = "377846032140106";
		
		System.out.println(isValid(number));
		System.out.println(getCardType(number).getCode() + ": " + getCardType(number).getName());
	}
	
	/**
	 * Valid a Credit Card number
	 */
	public static boolean isValid(String number) {
		if (getCardType(number) != CreditCardType.INVALID)
			return validCCNumber(number);
		return false;
	}
	
	/**
	 * Validate a credit card number with allowed card types.
	 * 
	 * @param number
	 * @param allowedTypes
	 * @return
	 */
	public static boolean isValid(String number, List<CreditCardType> allowedTypes) {
		CreditCardType type = getCardType(number);
		if (!allowedTypes.contains(type)) {
			return false;
		}
		
		return validCCNumber(number);
	}
	
	public static boolean isNumber(String n) {
		try {
			Double.valueOf(n).doubleValue();
			return true;
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean validCCNumber(String n) {
		try {
			/*
			 * * known as the LUHN Formula (mod10)
			 */
			int j = n.length();

			String[] s1 = new String[j];
			for (int i = 0; i < n.length(); i++)
				s1[i] = "" + n.charAt(i);

			int checksum = 0;

			for (int i = s1.length - 1; i >= 0; i -= 2) {
				int k = 0;

				if (i > 0) {
					k = Integer.valueOf(s1[i - 1]).intValue() * 2;
					if (k > 9) {
						String s = "" + k;
						k = Integer.valueOf(s.substring(0, 1)).intValue()
								+ Integer.valueOf(s.substring(1)).intValue();
					}
					checksum += Integer.valueOf(s1[i]).intValue() + k;
				} else
					checksum += Integer.valueOf(s1[0]).intValue();
			}
			return ((checksum % 10) == 0);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static CreditCardType getCardType(String cardnumber) {
		CreditCardType valid = CreditCardType.INVALID;

		String digit1 = cardnumber.substring(0, 1);
		String digit2 = cardnumber.substring(0, 2);
		String digit3 = cardnumber.substring(0, 3);
		String digit4 = cardnumber.substring(0, 4);

		if (isNumber(cardnumber)) {
			/*
			 * ----* VISA prefix=4* ---- length=13 or 16 (can be 15 too!?!
			 * maybe)
			 */
			if (digit1.equals("4")) {
				if (cardnumber.length() == 13 || cardnumber.length() == 16)
					valid = CreditCardType.VISA;
			}
			/*
			 * ----------* MASTERCARD prefix= 51 ... 55* ---------- length= 16
			 */
			else if (digit2.compareTo("51") >= 0 && digit2.compareTo("55") <= 0) {
				if (cardnumber.length() == 16)
					valid = CreditCardType.MASTERCARD;
			}
			/*
			 * ----* AMEX prefix=34 or 37* ---- length=15
			 */
			else if (digit2.equals("34") || digit2.equals("37")) {
				if (cardnumber.length() == 15)
					valid = CreditCardType.AMERICAN_EXPRESS;
			}
			/*
			 * -----* ENROU prefix=2014 or 2149* ----- length=15
			 */
			else if (digit4.equals("2014") || digit4.equals("2149")) {
				if (cardnumber.length() == 15)
					valid = CreditCardType.EN_ROUTE;
			}
			/*
			 * -----* DCLUB prefix=300 ... 305 or 36 or 38* ----- length=14
			 */
			else if (digit2.equals("36")
					|| digit2.equals("38")
					|| (digit3.compareTo("300") >= 0 && digit3.compareTo("305") <= 0)) {
				if (cardnumber.length() == 14)
					valid = CreditCardType.DINERS_CLUB;
			}
		}

		return valid;
	}
}