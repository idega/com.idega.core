package com.idega.util;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * Title:        StringHandler
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega margmiðlun hf.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>,<a href="mailto:gummi@idega.is">Gudmundur Saemundsson</a>
 * @version 1.0

 */
public class StringHandler {
	public static String alfabet= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	public static final String EMPTY_STRING= "";
	public StringHandler() {}
	/**
	 * Returns an random String with the desired length with the Roman alphabet (upper and lower case) and numbers 0-9
	 */
	public static String getRandomString(int length) {
		StringBuffer buffer= new StringBuffer();
		for (int i= 0; i < length; i++) {
			buffer.append(alfabet.charAt((int) (alfabet.length() * Math.random())));
		}
		return buffer.toString();
	}
	/**
	 * Concatenates two strings to after alphabetical comparison
	 */
	public static String concatAlphabetically(String string1, String string2, String separator) {
		String first;
		String second;
		int compare= string1.compareTo(string2);
		if (compare < 0) {
			first= string1;
			second= string2;
		}
		else {
			second= string1;
			first= string2;
		}
		return first + separator + second;
	}
	public static String concatAlphabetically(String string1, String string2) {
		return concatAlphabetically(string1, string2, "");
	}
	public static Iterator getSeparatorIterator(final String stringToCutDown, final String separator) {
		return new Iterator() {
			private String theString= stringToCutDown;
			private String theSeparator= separator;
			private boolean hasSeparators= false;
			private boolean hasNext= true;
			public Object next() throws NoSuchElementException {
				String theReturn= null;
				try {
					if (hasNext) {
						if (hasSeparators) {
							if (theString.length() > 0) {
								if (!theString.equals(theSeparator)) {
									theReturn= theString.substring(0, theString.indexOf(separator) + 1);
									theString=
										theString.substring(theString.indexOf(separator) + 1, theString.length());
								}
							}
							else {
								throw new NoSuchElementException();
							}
						}
						else {
							if (theString.length() > 0) {
								return theString;
							}
							else {
								throw new NoSuchElementException();
							}
						}
					}
					else {
						throw new NoSuchElementException();
					}
				}
				catch (Exception e) {
					if (e instanceof NoSuchElementException) {
						throw (NoSuchElementException) e.fillInStackTrace();
					}
					else {
						throw new NoSuchElementException();
					}
				}
				return theReturn;
			}
			public boolean hasNext() {
				if (theString != null) {
					if (theString.length() > 0) {
						int index= theString.indexOf(theSeparator);
						while (index == 0) {
							theString= theString.substring(0, theSeparator.length());
							index= theString.indexOf(theSeparator);
						}
						if (index == -1) {
							hasSeparators= false;
						}
						else {
							hasSeparators= true;
						}
					}
					else {
						hasNext= false;
					}
				}
				else {
					hasNext= false;
				}
				return hasNext;
			}
			public void remove() {
				/**

				 * Does Nothing

				 */
			}
		};
	}
	/**
	 * Strips the string of all non-roman characters such as special Icelandic,Swedish,German etc. characters
	 */
	public static String stripNonRomanCharacters(String inputString) {
		char[] cAinputString= inputString.toCharArray();
		for (int i= 0; i < cAinputString.length; i++) {
			char c= cAinputString[i];
			char newC= translateCharacter(c);
			cAinputString[i]= newC;
		}
		String newString= new String(cAinputString);
		return newString;
	}
	private static char translateCharacter(char c) {
		/**
		 * @todo: Finish implementation
		 */
		switch (c) {
			case 'Ä' :
				return 'A';
			case 'ä' :
				return 'a';
			case 'Å' :
				return 'A';
			case 'å' :
				return 'a';
			case 'Á' :
				return 'A';
			case 'á' :
				return 'a';
			case 'É' :
				return 'E';
			case 'é' :
				return 'e';
			case 'Ë' :
				return 'E';
			case 'ë' :
				return 'e';
			case 'Ð' :
				return 'D';
			case 'ð' :
				return 'd';
			case 'Í' :
				return 'I';
			case 'í' :
				return 'i';
			case 'Ï' :
				return 'I';
			case 'ï' :
				return 'i';
			case 'Ö' :
				return 'O';
			case 'ö' :
				return 'o';
			case 'Ó' :
				return 'O';
			case 'ó' :
				return 'o';
			case 'Ü' :
				return 'U';
			case 'ü' :
				return 'u';
			case 'Ú' :
				return 'U';
			case 'ú' :
				return 'u';
			case 'Ý' :
				return 'Y';
			case 'ý' :
				return 'y';
			case 'Þ' :
				return 'T';
			case 'þ' :
				return 't';

		}
		return c;
	}
} // Class StringHandler
