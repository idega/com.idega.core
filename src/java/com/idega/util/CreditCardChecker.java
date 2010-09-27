package com.idega.util;

/**
 * Creditcardnumber checker Visa <br>
 * MasterCard <br>
 * American Express <br>
 * Diner's Club <br>
 * Carte Blanche <br>
 * Discover <br>
 * en Route <br>
 * JCB <br>
 * 
 **/

public class CreditCardChecker {

	public static boolean isValid(String cardnumber)
			throws NumberFormatException {
		if ((cardnumber == null) || (cardnumber.length() != 16)) {
			return false;
		}

		int sum = 0;
		int multiplier = 1;
		int length = cardnumber.length();

		String digit;
		int numberProduct;

		for (int i = 0; i < length; i++) {
			digit = cardnumber.substring(length - i - 1, length - i);

			try {
				numberProduct = Integer.parseInt(digit, 10) * multiplier;
			} catch (NumberFormatException nfe) {
				return false;
			}

			if (numberProduct >= 10) {
				sum += (numberProduct % 10) + 1;
			} else {
				sum += numberProduct;
			}

			if (multiplier == 1) {
				multiplier++;
			} else {
				multiplier--;
			}
		}

		if ((sum % 10) == 0) {
			return true;
		} else {
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

	public static boolean isNumber(String n) {
		try {
			Double.valueOf(n).doubleValue();
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}

}