package com.idega.util;

/** Creditcardnumber checker
*	Visa <br>
*	MasterCard <br>
*	American Express <br>
*	Diner's Club <br>
*	Carte Blanche <br>
*	Discover <br>
*	en Route <br>
*	JCB <br>
*
**/

public class CreditCardChecker {

	public static boolean isValid(String cardnumber) throws NumberFormatException {
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
			}
			catch (NumberFormatException nfe) {
				return false;
			}
			
			if (numberProduct >= 10) {
				sum += (numberProduct % 10) + 1;
			}
			else {
				sum += numberProduct;
			}
			
			if (multiplier == 1) {
				multiplier++;
			}
			else {
				multiplier--;
			}
		}

		if ((sum % 10) == 0) {
			return true;
		}
		else {
			return false;
		}
	}
}