/*
 * $Id: StringHandler.java,v 1.34 2005/03/01 23:25:00 tryggvil Exp $ Created on
 * 14.9.2004
 * 
 * Copyright (C) 2001-2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * This class has utility methods to work with strings. <br>
 * Last modified: $Date: 2005/03/01 23:25:00 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>, <a
 *         href="mailto:gummi@idega.is">Gudmundur Saemundsson </a>
 * @version $Revision: 1.34 $
 */
public class StringHandler {

	/**
	 * substitution string if a character is not a letter
	 */
	public static String NO_LETTER_SUBSTITUTION = "x";

	/**
	 * groups of characters (represented by uni code) replaced by strings
	 * defined in substitution. Use the uni code table (@see
	 * http:\\www.unicode.org)
	 */
	static public int[][] SUBSTITUTION_GROUP = {
	// 192 - 207
			{ 192, 193, 194, 195, 196, 197 }, // A
			{ 198 }, // Ae
			{ 199 }, // C
			{ 200, 201, 202, 203 }, // E
			{ 204, 205, 206, 207 }, // I
			// 208 - 223
			{ 208 }, // D
			{ 209 }, // N
			{ 210, 211, 212, 213, 214 }, // O
			{ 215 }, // x - char 215 is no letter
			{ 216 }, // O
			{ 217, 218, 219, 220 }, // U
			{ 221 }, // Y
			{ 222 }, // Th
			{ 223 }, // ss
			// 224 - 239
			{ 224, 225, 226, 227, 228, 229 }, // a
			{ 230 }, // ae
			{ 231 }, // c
			{ 232, 233, 234, 235 }, // e
			{ 236, 237, 238, 239 }, // i
			// 240 - 255
			{ 240 }, // d
			{ 241 }, // n
			{ 242, 243, 244, 245, 246 }, // o
			{ 247 }, // x char 247 is no letter
			{ 248 }, // o
			{ 249, 250, 251, 252 }, // u
			{ 253 }, // y
			{ 254 }, // th
			{ 255 } }; // y

	/**
	 * substitution strings for characters defined in substitution group
	 */
	static public String[] SUBSTITUTION = { "A", "Ae", "C", "E", "I", "D", "N", "O", NO_LETTER_SUBSTITUTION, "O", "U",
			"Y", "Th", "ss", "a", "ae", "c", "e", "i", "d", "n", "o", NO_LETTER_SUBSTITUTION, "o", "u", "y", "th", "y" };

	public static String alfabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	// Alphabet+Numbers without ambigous characters such as 0 O and I l and 1
	public static String alphabetNonAmbigous = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefhijkmnoprstuvwxyz23456789";

	public static final String EMPTY_STRING = "";

	public static final String DASH = "-";

	public static final String SLASH = "/";

	public static final String NEWLINE = "\n";

	public StringHandler() {
		// empty blocks
	}

	/**
	 * Returns an random String with the desired length with the Roman alphabet
	 * (upper and lower case) and numbers 0-9
	 */
	public static String getRandomString(int length) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(alfabet.charAt((int) (alfabet.length() * Math.random())));
		}
		return buffer.toString();
	}

	/**
	 * Returns an random String with the desired length with the Roman alphabet
	 * (upper and lower case) without ambigous characters (which can be confused
	 * together) and numbers 2-9
	 */
	public static String getRandomStringNonAmbiguous(int length) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buffer.append(alphabetNonAmbigous.charAt((int) (alphabetNonAmbigous.length() * Math.random())));
		}
		return buffer.toString();
	}

	/**
	 * Concatenates two strings to after alphabetical comparison
	 */
	public static String concatAlphabetically(String string1, String string2, String separator) {
		String first;
		String second;
		int compare = string1.compareTo(string2);
		if (compare < 0) {
			first = string1;
			second = string2;
		}
		else {
			second = string1;
			first = string2;
		}
		return first + separator + second;
	}

	public static String concat(String firstString, String secondString) {
		StringBuffer buffer = new StringBuffer(firstString);
		buffer.append(secondString);
		return buffer.toString();
	}

	public static String concatAlphabetically(String string1, String string2) {
		return concatAlphabetically(string1, string2, "");
	}

	public static Iterator getSeparatorIterator(final String stringToCutDown, final String separator) {
		return new Iterator() {

			private String theString = stringToCutDown;

			private String theSeparator = separator;

			private boolean hasSeparators = false;

			private boolean hasNext = true;

			public Object next() throws NoSuchElementException {
				String theReturn = null;
				try {
					if (hasNext) {
						if (hasSeparators) {
							if (theString.length() > 0) {
								if (!theString.equals(theSeparator)) {
									theReturn = theString.substring(0, theString.indexOf(separator) + 1);
									theString = theString.substring(theString.indexOf(separator) + 1,
											theString.length());
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
							throw new NoSuchElementException();
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
					throw new NoSuchElementException();
				}
				return theReturn;
			}

			public boolean hasNext() {
				if (theString != null) {
					if (theString.length() > 0) {
						int index = theString.indexOf(theSeparator);
						while (index == 0) {
							theString = theString.substring(0, theSeparator.length());
							index = theString.indexOf(theSeparator);
						}
						if (index == -1) {
							hasSeparators = false;
						}
						else {
							hasSeparators = true;
						}
					}
					else {
						hasNext = false;
					}
				}
				else {
					hasNext = false;
				}
				return hasNext;
			}

			public void remove() {
				/**
				 * 
				 * Does Nothing
				 * 
				 */
			}
		};
	}

	private static final char[] empty_char_array={};
	/**
	 * Strips the string of all non-roman characters such as special
	 * Icelandic,Swedish,German etc. characters. <br>
	 * This method replaces these characters and re-writes them with Roman
	 * equivalents. <br>
	 * So 'Þ'/&THORN; becomes TH, '‡'/&aacute; becomes a, 'Š'/&auml; becomes ae,
	 * etc.
	 */
	public static String stripNonRomanCharacters(String inputString) {
		return stripNonRomanCharacters(inputString,empty_char_array);
	}
	
	/**
	 * Strips the string of all non-roman characters such as special
	 * Icelandic,Swedish,German etc. characters. <br>
	 * This method replaces these characters and re-writes them with Roman
	 * equivalents. <br>
	 * So 'Þ'/&THORN; becomes Th, '‡'/&aacute; becomes a, 'Š'/&auml; becomes ae,
	 * etc.
	 * @param inputString the input string to replace
	 * @param exceptions an array of characters to maintain in the outputString
	 */
	public static String stripNonRomanCharacters(String inputString,char[] exceptions) {
		char[] cAinputString = inputString.toCharArray();
		StringBuffer newString = new StringBuffer();
		for (int i = 0; i < cAinputString.length; i++) {
			char c = cAinputString[i];
			newString.append(translateNonRomanCharacter(c,exceptions));
		}
		return newString.toString();
	}

	/**
	 * Returns true if the specified object returns a non empty string when the
	 * toString() method is invoked else false.
	 */
	public static boolean isNotEmpty(Object element) {
		if (element == null) {
			return false;
		}
		String aString = element.toString();
		return (aString != null && aString.length() > 0);
	}

	/**
	 * Returns true if each element of the collection returns a non empty string
	 * when the toString() method is invoked else false.
	 */
	public static boolean elementsAreNotEmpty(Collection collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		Iterator iter = collection.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			String aString = element.toString();
			if (!StringHandler.isNotEmpty(aString)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the package name and the name of the specified className.
	 * Example: "com.idega.util.StringHandler" returns {"com.idega.util",
	 * "StringHandler"} "StringHandler" returns {"", "StringHandler"}
	 * 
	 * @param className
	 * @return array containing the package name and the class name without
	 *         package name
	 */
	public static String[] splitOffPackageFromClassName(String className) {
		String packageName;
		String name;
		int index = className.lastIndexOf(".");
		if (index < 0) {
			packageName = "";
			name = className;
		}
		else {
			packageName = className.substring(0, index);
			name = className.substring(++index);
		}
		return new String[] { packageName, name };
	}

	/**
	 * Returns the specified filename without extension but not if the fileName
	 * starts with a dot character. Example: cutExtension("tomcat.gif") returns
	 * "tomcat"; cutExtension("tomcat") returns "tomcat";
	 * cutExtension(".systemFile") returns ".systemFile";
	 * 
	 * @param fileName
	 * @return name without extension
	 */
	public static String cutExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index <= 0) {
			return fileName;
		}
		return fileName.substring(0, index);
	}

	public static String replaceNameKeepExtension(String oldFileNameWithExtension, String newFileNameWithoutExtension) {
		int index = oldFileNameWithExtension.lastIndexOf(".");
		if (index <= 0) {
			return newFileNameWithoutExtension;
		}
		String extension = oldFileNameWithExtension.substring(index);
		return StringHandler.concat(newFileNameWithoutExtension, extension);
	}

	/**
	 * Marks all occurences of the specified pattern in the specified string
	 * with the specified replace in a map, ignores case. The caller can modify
	 * the specified string later by using the keys of the map (they corrrespond
	 * to the index in the string sarting at zero). This method is useful when
	 * more than one key has to be replaced and some keys appear in the replace
	 * strings. Example: getReplaceMapIgnoreCase("A cat is not a caterpillar",
	 * "cat", "rat") returns a map with the following entries: ( 2,"ra") ( 3,
	 * null) (16, "ra") (17, null) getReplaceMapIgnoreCase("A cat is not a
	 * caterpillar", "r", "ur") returns the following map (20, "ur") (26, "ur")
	 * By adding the maps and replacing the characters according to the keys the
	 * result is: "A rat is not a rateurpillaur"
	 */
	public static Map getReplaceMapIgnoreCase(String str, String pattern, String replace) {
		Map indexMap = new HashMap();
		int s = 0;
		int e = 0;
		String upperPattern = pattern.toUpperCase();
		int length = upperPattern.length();
		String upperStr = str.toUpperCase();
		while ((e = upperStr.indexOf(upperPattern, s)) >= 0) {
			indexMap.put(new Integer(e), replace);
			int i = e;
			i++;
			while (i < e + length) {
				indexMap.put(new Integer(i++), null);
			}
			s = i;
		}
		return indexMap;
	}

	/**
	 * Replaces all occurences of the specified pattern in the specified string
	 * with the specified replace, ignores case. Example: replaceIgnoreCase("A
	 * CAT is not a caterpillar", "ca", "hu") returns "A hut is not a
	 * huterpillar"
	 * 
	 * @param str
	 * @param pattern
	 * @param replace
	 * @return modified (new) string
	 * @author thomas
	 */
	public static String replaceIgnoreCase(String str, String pattern, String replace) {
		int s = 0;
		int e = 0;
		String upperPattern = pattern.toUpperCase();
		String upperStr = str.toUpperCase();
		StringBuffer result = new StringBuffer();
		while ((e = upperStr.indexOf(upperPattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + upperPattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

	/**
	 * Replaces all occurences of the specified pattern in the specified string
	 * with the specified replace Example: replace("A cat is not a caterpillar",
	 * "ca", "hu") returns "A hut is not a huterpillar"
	 * 
	 * @param str
	 * @param pattern
	 * @param replace
	 * @return modified (new) string
	 * @author thomas
	 */
	public static String replace(String str, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();
		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

	/**
	 * Removes all occurences of the specified pattern from the specified
	 * string. Example: replace("A cat is not a caterpillar", "ca") returns "A t
	 * is not a terpillar"
	 * 
	 * @param str
	 * @param pattern
	 * @return modified (new) string
	 * @author thomas
	 */
	public static String remove(String str, String pattern) {
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();
		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

	/**
	 * Removes all occurences of white spaces from the specified string.
	 * Example: replace("A cat is not a caterpillar") returns
	 * "Acatisnotacaterpillar"
	 * 
	 * @param str
	 * @param pattern
	 * @return modified (new) string
	 * @author thomas
	 */
	public static String removeWhiteSpace(String str) {
		return StringHandler.remove(str, " ");
	}

	/**
	 * Gets the words that are contained in a string corresponding to a list of
	 * allowed words. If the string contains a non allowed word an empty list
	 * ist returned. Ignores case and returns only words in upper case. Example:
	 * list = "(" ")" "or" "xor" "A" "B" getElements("A OR(B xor C)", list,
	 * false) returns "(" "A" "OR" "(" "B" "XOR" "C" ")"
	 * 
	 * @param str
	 * @param allowedWords
	 * @param list
	 *            of allowed words in str else null
	 * @param case
	 *            sensitivity
	 * @author thomas
	 */
	public static List getElementsIgnoreCase(String str, Collection allowedWords) {
		List words = new ArrayList(allowedWords.size());
		String string = str.toUpperCase();
		Iterator iterator = allowedWords.iterator();
		while (iterator.hasNext()) {
			String word = (String) iterator.next();
			words.add(word.toUpperCase());
		}
		return getElements(string, words);
	}

	/**
	 * Gets the substring that is enclosed by the specified strings. If one of
	 * the parameters is null null is returned. Examples:
	 * substring("caterpillar","cat", "a") returns "erpill"
	 * substring("caterpillar","we", "a") returns null substring
	 * ("caterpillar","","a") returns "caterpill" substring("caterpillar", "",
	 * "") returns "caterpillar" substring("caterpillar","a", "l") returns
	 * "terpil" substring("","","") returns "" substring(null, "cat", "we")
	 * returns null substring("caterpillar", null, "we") returns null
	 * 
	 * @param string
	 * @param start
	 * @param end
	 * @return sunbstring
	 * @author thomas
	 */
	public static String substringEnclosedBy(String string, String start, String end) {
		if (string == null || start == null || end == null) {
			return null;
		}
		int startIndex = string.indexOf(start);
		if (startIndex == -1) {
			return null;
		}
		int endIndex = string.lastIndexOf(end);
		if (endIndex == -1) {
			return null;
		}
		startIndex += start.length();
		return string.substring(startIndex, endIndex);
	}

	/**
	 * Returns a string with an added or increased counter depending on the
	 * specified token. The returned string is unique regarding the specified
	 * collection of strings. If the string is already unique the string is not
	 * modified. Null strings are handled like empty strings. If the
	 * collectionOfString parameter is null the specified string is returned.
	 * Examples: addOrIncreaseCounterIfNecessary("fileName", "_", ("fileName",
	 * "fileName_1", "fileName_3") returns "fileName_2"
	 * addOrIncreaseCounterIfNecessary("fileName", "_", ("fileName_5",
	 * "fileName_1", "fileName_3") returns "fileName"
	 * addOrIncreaseCounterIfNecessary("fileName", "_", null ) returns
	 * "fileName"
	 * 
	 * @param string
	 * @param token
	 * @param collection
	 *            of strings
	 * @return string with added or increased counter if necessary
	 */
	public static String addOrIncreaseCounterIfNecessary(String string, String token, Collection collectionOfStrings) {
		if (string == null) {
			string = "";
		}
		if (collectionOfStrings == null) {
			return string;
		}
		boolean stop = false;
		do {
			stop = false;
			Iterator iterator = collectionOfStrings.iterator();
			while (stop == false && iterator.hasNext()) {
				String name = (String) iterator.next();
				if (string.equals(name)) {
					string = addOrIncreaseCounter(string, token);
					stop = true;
				}
			}
		}
		while (stop == true);
		return string;
	}

	/**
	 * Returns a string with an added or increased counter depending on the
	 * specified token. Null strings are handled like empty strings. Examples:
	 * addOrIncreaseCounter("fileName_13", "_") returns "fileName_14"
	 * addOrIncreaseCounter("fileName", "_") returns "fileName_1"
	 * addOrIncreaseCounter("fileName_12_13", "_") returns "fileName_12_14"
	 * addOrIncreaseCounter("", "_") returns "_1" addOrIncreaseCounter("", "")
	 * returns "1" addOrIncreaseCounter("fileName_13", "") returns
	 * "fileName_131" addOrIncreaseCounter("fileName_13", null) returns
	 * "fileName_131"
	 * 
	 * @param string
	 * @param token
	 * @return string with added or increased counter.
	 */
	public static String addOrIncreaseCounter(String string, String token) {
		if (string == null) {
			string = "";
		}
		if (token == null) {
			token = "";
		}
		// is there an existing counter?
		int endIndex = string.lastIndexOf(token);
		if (endIndex > -1) {
			String number = string.substring(endIndex + token.length());
			if (StringHandler.isNaturalNumber(number)) {
				int counter = Integer.parseInt(number);
				String newNumber = Integer.toString(++counter);
				String newString = string.substring(0, endIndex);
				StringBuffer buffer = new StringBuffer(newString);
				buffer.append(token).append(newNumber);
				return buffer.toString();
			}
		}
		// string hasn't a counter add a counter
		StringBuffer buffer = new StringBuffer(string);
		buffer.append(token);
		buffer.append("1");
		return buffer.toString();
	}

	/**
	 * Returns true if the specified string is a natural number. Zero is
	 * considered as a natural number. Examples: isNaturalNumber("-12") returns
	 * false isNaturalNumber("0") returns true; isNaturalNumber("12") returns
	 * true; isNaturalNumber("f1") returns false; isNaturalNumber("+12") returns
	 * false isNaturalNumber(null) returns false;
	 * 
	 * @param string
	 * @return true if the specified string is a natural number else false
	 * @author thomas
	 */
	public static boolean isNaturalNumber(String string) {
		if (string == null) {
			return false;
		}
		int length = string.length();
		if (length == 0) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			char letter = string.charAt(i);
			if (!Character.isDigit(letter)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the words that are contained in a string corresponding to a list of
	 * allowed words. If the string contains a non allowed word an empty list
	 * ist returned. Example: list = "(" ")" "or" "xor" "A" "B" getElements("A
	 * or(B xor C)", list, true) returns "(" "A" "or" "(" "B" "xor" "C" ")"
	 * 
	 * @param str
	 * @param allowedWords
	 * @param list
	 *            of allowed words in str else null
	 * @author thomas
	 */
	public static List getElements(String string, Collection allowedWords) {
		List result = new ArrayList();
		// order of conditions
		SortedSet orderedWords = new TreeSet(new Comparator() {

			public int compare(Object first, Object second) {
				int firstLength = ((String) first).length();
				int secondLength = ((String) second).length();
				if (firstLength < secondLength) {
					return 1;
				}
				else if (firstLength > secondLength) {
					return -1;
				}
				return ((Comparable) first).compareTo(second);
			}
		});
		orderedWords.addAll(allowedWords);
		String stringWithoutWhiteSpace = StringHandler.removeWhiteSpace(string);
		int length = stringWithoutWhiteSpace.length();
		boolean wordIsRecognized = true;
		int index = 0;
		while (index < length && wordIsRecognized) {
			wordIsRecognized = false;
			Iterator wordIterator = orderedWords.iterator();
			while (wordIterator.hasNext() && (!wordIsRecognized)) {
				String tempWord = (String) wordIterator.next();
				if (stringWithoutWhiteSpace.startsWith(tempWord, index)) {
					wordIsRecognized = true;
					result.add(tempWord);
					index += tempWord.length();
				}
			}
		}
		if (index != length) {
			return null;
		}
		return result;
	}

	/**
	 * Compares two versions strings like "000.003.001" and "0.0044" in a fast
	 * way.
	 * 
	 * @param version1
	 * @param version2
	 * @return Zero if the versions are the same, a value less than zero if the
	 *         first version is less than the second version and value larger
	 *         than zero if the first version is larger.
	 * @author thomas
	 */
	public static int compareVersions(String version1, String version2) {
		int index1 = 0;
		int index2 = 0;
		int result = 0;
		char c1;
		char c2;
		boolean firstDigitIsZero1 = true;
		boolean firstDigitIsZero2 = true;
		if (version1 == null) {
			if (version2 == null) {
				return 0;
			}
			return -1;
		}
		else if (version2 == null) {
			return 1;
		}
		int versionLength1 = version1.length();
		int versionLength2 = version2.length();
		while (true) {
			if (index1 == versionLength1) {
				c1 = '.';
			}
			else {
				c1 = version1.charAt(index1);
			}
			if (index2 == versionLength2) {
				c2 = '.';
			}
			else {
				c2 = version2.charAt(index2);
			}
			// one of the numbers starts with zero value
			if (firstDigitIsZero1 || firstDigitIsZero2) {
				if (firstDigitIsZero1 && c1 == '0' && c2 != '0') {
					index1++;
					firstDigitIsZero2 = false;
				}
				else if (firstDigitIsZero2 && c1 != '0' && c2 == '0') {
					index2++;
					firstDigitIsZero1 = false;
				}
				else if (c1 == '0' && c2 == '0') {
					index1++;
					index2++;
				}
				else {
					firstDigitIsZero1 = false;
					firstDigitIsZero2 = false;
				}
			}
			// both numbers start with a non-zero value
			else if (c1 == '.' && c2 != '.') {
				// c2 is larger
				return -1;
			}
			else if (c1 != '.' && c2 == '.') {
				// c1 is larger
				return 1;
			}
			else if (c1 == '.' && c2 == '.') {
				// both numbers have the same length
				// look at the result!
				if (result != 0) {
					return result;
				}
				// bad luck!
				// go to the next number
				// is there a next number?
				if (index1 == versionLength1 || index2 == versionLength2) {
					return result;
				}
				firstDigitIsZero1 = true;
				firstDigitIsZero2 = true;
				index1++;
				index2++;
			}
			else {
				// both characters are numbers
				if (result == 0) {
					result = c1 - c2;
				}
				index1++;
				index2++;
			}
		}
	}

	/**
	 * Checks if the specified string contains the specified pattern. Example:
	 * replace("A cat is not a caterpillar", "ca") returns true
	 * 
	 * @param string
	 * @param pattern
	 * @return true if the string contains the specified pattern else false.
	 * @author thomas
	 */
	public static boolean contains(String string, String pattern) {
		return (string.indexOf(pattern) >= 0);
	}

	/**
	 * Replaces all non roman characters by suitable strings
	 * @param c the character to replace
	 * @param exceptions an array of exceptions e.g. {1,2,3,4,5,6,7,8,9,0,-}
	 */
	private static String translateNonRomanCharacter(char c, char[] exceptions) {
		// get uni code number
		int value = c;
		// is c a "normal" letter?
		if (('A' <= value && value <= 'Z') || ('a' <= value && value <= 'z') || (Arrays.binarySearch(exceptions,c) >= 0)){
			return String.valueOf(c);
		}
		int groupsNumber = SUBSTITUTION_GROUP.length;
		int i = 0;
		// look up to which group the character belongs
		while ((i < groupsNumber) && (Arrays.binarySearch(SUBSTITUTION_GROUP[i], value) < 0)) {
			i++;
		}
		// if the character does not belong to a group return special
		// substitution
		if (i == groupsNumber)
			return NO_LETTER_SUBSTITUTION;
		// return substitution string
		return SUBSTITUTION[i];
	}

	public static String shortenToLength(CharSequence sequence, int length) {
		int tempLength = sequence.length();
		if (tempLength <= length) {
			return sequence.toString();
		}
		return sequence.subSequence(0, length).toString();
	}

	/**
	 * Returns a string or dash if there is no string. If the string is either
	 * null or an empty string a dash (-) is returned else it returns the input
	 * string str.
	 * 
	 * @param str
	 *            String to check
	 * @return String which is either a dash or the input str.
	 */
	public static String getStringOrDash(String str) {
		if (str == null) {
			return DASH;
		}
		if (str.equals(EMPTY_STRING)) {
			return DASH;
		}
		return str;
	}

	/**
	 * Breaks down the URL string separated by the '/' charachter to an array.
	 * <br>
	 * For instance it breaks down the URL "/component/78909" to
	 * {"component","78909"}
	 * 
	 * @return
	 */
	public static String[] breakDownURL(String urlString) {
		// Performance optimization to handle the first 4 parameters without
		// having to construct the Vector
		String s1 = null;
		String s2 = null;
		String s3 = null;
		String s4 = null;
		List list = null;
		StringTokenizer st = new StringTokenizer(urlString, SLASH);
		int index = 0;
		while (st.hasMoreTokens()) {
			index++;
			if (index == 1) {
				s1 = st.nextToken();
			}
			else if (index == 2) {
				s2 = st.nextToken();
			}
			else if (index == 3) {
				s3 = st.nextToken();
			}
			else if (index == 4) {
				s4 = st.nextToken();
			}
			else if (index == 5) {
				list = new ArrayList();
				list.add(s1);
				list.add(s2);
				list.add(s3);
				list.add(s4);
				list.add(st.nextToken());
			}
			else if (index > 5) {
				st.nextToken();
			}
		}
		if (index == 1) {
			String[] theReturn = { s1 };
			return theReturn;
		}
		else if (index == 2) {
			String[] theReturn = { s1, s2 };
			return theReturn;
		}
		else if (index == 3) {
			String[] theReturn = { s1, s2, s3 };
			return theReturn;
		}
		else if (index == 4) {
			String[] theReturn = { s1, s2, s3, s4 };
			return theReturn;
		}
		else if (index > 4) {
			String[] theReturn = (String[]) list.toArray(new String[0]);
			return theReturn;
		}
		return null;
	}

	public static String firstCharacterToUpperCase(String string) {
		if (string.length() == 1) {
			return string.toUpperCase();
		}
		else if (string.length() > 1) {
			return string.substring(0, 1).toUpperCase().concat(string.substring(1));
		}
		else {
			return string;
		}
	}

	
	private static char[] allowedcharacters={'1','2','3','4','5','6','7','8','9','0','-'};
	/**
	 * Parses the inputString so that it has a "URL friendly" format. i.e. only
	 * latin characters in lowercase without special characters. <br>
	 * e.g. the inputString "Fr&eacute;ttir &aacute; Fors&iacute;&eth;u" becomes
	 * "frettiraforsidu"
	 * 
	 * @param complexString
	 * @return
	 */
	public static String convertToUrlFriendly(String complexString) {
		String noWhitespace = removeWhiteSpace(complexString);
		String nonLatin = stripNonRomanCharacters(noWhitespace,allowedcharacters);
		String lowercase = nonLatin.toLowerCase();
		return lowercase;
	}
	
	/**
	 * Parses the passed inputString so that it removes all occurences of the slash ('/') character
	 * where it is twice or more in a row in a string.<br>
	 * e.g. the inputString '//myurl//mypage//' becomes '/myurl/mypage/'
	 * @param input
	 * @return
	 */
	public static String removeMultipleSlashes(String inputString){
		char[] characters = inputString.toCharArray();
		StringBuffer ret = new StringBuffer();
		boolean lastWasSlash=false;
		for (int i = 0; i < characters.length; i++) {
			char c = characters[i];
			if(c=='/'){
				if(lastWasSlash){
					lastWasSlash=true;
					//skip this character
				}
				else{
					lastWasSlash=true;
					ret.append(c);
				}
			}
			else{
				if(lastWasSlash){
					lastWasSlash=false;
				}
				ret.append(c);
			}
		}
		return ret.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
