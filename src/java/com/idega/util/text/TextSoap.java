/*
 *  Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.util.text;

import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
//@todo use regular expressions such as import com.stevesoft.pat.*;

/**
 *  General class for text manipulation
 *
 *@author     <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a> , Eirikur Hrafnsson
 *@created    5. mars 2002
 *@version    1.2
 */
public class TextSoap {

	private static DecimalFormat singleDecimalFormat = new DecimalFormat("0.0");
	private static DecimalFormatSymbols symbols = singleDecimalFormat.getDecimalFormatSymbols();

	/**
	 *  Function to cut out all the text between two tokens in a larger string and
	 *  return the results as a Vector of strings
	 *
	 *@param  inputString  Description of the Parameter
	 *@param  begintoken   Description of the Parameter
	 *@param  endtoken     Description of the Parameter
	 *@return              Description of the Return Value
	 */
	public static Vector FindAllBetween(String inputString, String begintoken, String endtoken) {
		//int arraylength = 200;
		int beginnum = 0;
		int endnum = 0;
		int counter = 0;
		String newString;
		String newSubstring;
		Vector outVector;

		newSubstring = new String("");
		newString = new String("");
		outVector = new Vector(10);
		newString = inputString;
		String tempString = new String("");

		try {
			while ((newString.indexOf(begintoken) != -1) && (newString.indexOf(endtoken) != -1)) {
				beginnum = newString.indexOf(begintoken) + begintoken.length();
				endnum = newString.indexOf(endtoken, beginnum);
				newSubstring = newString.substring(beginnum, endnum);
				tempString = newString.substring(endnum + endtoken.length());
				//cuts down the string from where occurence last fount
				newString = tempString;
				//removeCharacters(newSubstring);
				outVector.addElement(removeCharacters(newSubstring));
				counter++;
			}
		}
		catch (Exception e) {
			outVector.addElement("TextSoapError" + counter);
			e.printStackTrace(System.err);
		}
		outVector.trimToSize();
		//System.out.print(outVector.size());
		return outVector;
	}

	/**
	 *  Function to cut out all the text between multiple instances of a token in
	 *  a larger string and return the results a Vector of Strings
	 *
	 *@param  inputString       Description of the Parameter
	 *@param  beginAndEndToken  Description of the Parameter
	 *@return                   Description of the Return Value
	 */
	public static Vector FindAllBetween(String inputString, String beginAndEndToken) {
		//int arraylength = 200;
		int beginnum = -1;
		int endnum = -1;
		int counter = 0;
		String newString;
		String newSubstring;
		Vector outVector;

		newSubstring = new String("");
		newString = new String("");
		outVector = new Vector(10);
		newString = inputString;
		String tempString = new String("");

		try {
			while (newString.indexOf(beginAndEndToken) != -1) {
				//first round
				if (endnum == -1) {
					endnum = inputString.indexOf(beginAndEndToken);
					newString = inputString.substring(endnum + beginAndEndToken.length());
				}
				//second round
				else {
					beginnum = endnum;
					endnum = inputString.indexOf(beginAndEndToken, beginnum + beginAndEndToken.length());
					newSubstring = inputString.substring(beginnum + beginAndEndToken.length(), endnum);

					//cuts down the string from where occurence last found
					newString = inputString.substring(endnum + beginAndEndToken.length());
					outVector.addElement(removeCharacters(newSubstring));
				}
				counter++;
			}
		}
		catch (Exception e) {
			outVector.addElement("TextSoapError" + counter);
		}
		outVector.trimToSize();

		return outVector;
	}

	/**
	 *  Function to cut out all the text between multiple instances of a token in
	 *  a larger string
	 *
	 *@param  inputString      Description of the Parameter
	 *@param  separatorString  Description of the Parameter
	 *@return                  Description of the Return Value
	 */
	public static Vector FindAllWithSeparator(String inputString, String separatorString) {
		int beginnum = -1;
		int endnum = -1;
		int counter = 0;
		String newString;
		String newSubstring;
		Vector outVector;

		newSubstring = new String("");
		newString = new String("");
		outVector = new Vector(10);
		newString = inputString;
		String tempString = new String("");

		try {
			while (newString.indexOf(separatorString) != -1) {
				//first round
				if (endnum == -1) {
					endnum = inputString.indexOf(separatorString);
					newString = inputString.substring(endnum + separatorString.length());
					newSubstring = inputString.substring(0, endnum);
					outVector.addElement(removeCharacters(newSubstring));
				}
				//second round
				else {
					beginnum = endnum;
					endnum = inputString.indexOf(separatorString, beginnum + separatorString.length());
					//cuts down the string from where occurence last found
					newSubstring = inputString.substring(beginnum + separatorString.length(), endnum);
					outVector.addElement(removeCharacters(newSubstring));
					newString = inputString.substring(endnum + separatorString.length());
				}
				counter++;
			}
			newSubstring = inputString.substring(endnum + separatorString.length());
			outVector.addElement(removeCharacters(newSubstring));
		}
		catch (Exception e) {
			outVector.addElement("TextSoapError" + counter);
		}
		outVector.trimToSize();

		return outVector;
	}

	/**
	 *  Removes unnecessary characters such as \n \t \r and " " from the begining
	 *  and end of a string
	 *
	 *@param  inString  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public static String removeCharacters(String inString) {
		boolean check = true;
		while (check && inString.length() >= 1) {
			if (inString.substring(0, 1).equals("\n")) {
				inString = inString.substring(1, inString.length());
			}
			else
				if (inString.substring(0, 1).equals("\r")) {
					inString = inString.substring(1, inString.length());
				}
				else
					if (inString.substring(0, 1).equals("\t")) {
						inString = inString.substring(1, inString.length());
					}
					else
						if (inString.substring(0, 1).equals(" ")) {
							inString = inString.substring(1, inString.length());
						}
						else
							if (inString.substring(inString.length() - 1, inString.length()).equals("\n")) {
								inString = inString.substring(0, inString.length() - 1);
							}
							else
								if (inString.substring(inString.length() - 1, inString.length()).equals("\r")) {
									inString = inString.substring(0, inString.length() - 1);
								}
								else
									if (inString.substring(inString.length() - 1, inString.length()).equals("\t")) {
										inString = inString.substring(0, inString.length() - 1);
									}
									else
										if (inString.substring(inString.length() - 1, inString.length()).equals(" ")) {
											inString = inString.substring(0, inString.length() - 1);
										}
										else {
											check = false;
										}
		}
		return inString;
	}

	public static String removeLineBreaks(String stringToFormat) {
		return findAndReplace(stringToFormat, "\n", "");
	}

	/**
	 *  Description of the Method
	 *
	 *@param  textString  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	public static String cleanText(String textString) {
		textString = findAndReplace(textString, '\'', '?');
		textString = findAndReplace(textString, "&oslash;", "?");
		textString = findAndReplace(textString, "&Oslash;", "?");
		textString = findAndReplace(textString, "&aring;", "?");
		textString = findAndReplace(textString, "&quot;", "\"");
		textString = findAndReplace(textString, "  ", " ");
		return textString;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  text         Description of the Parameter
	 *@param  charToFind   Description of the Parameter
	 *@param  charReplace  Description of the Parameter
	 *@return              Description of the Return Value
	 */
	public static String findAndReplace(String text, char charToFind, char charReplace) {
		return text.replace(charToFind, charReplace);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  text           Description of the Parameter
	 *@param  stringToFind   Description of the Parameter
	 *@param  stringReplace  Description of the Parameter
	 *@return                Description of the Return Value
	 */
	public static String findAndReplace(String text, String stringToFind, String stringToReplace) {
		// Regex r = new Regex(stringToFind,stringReplace);
		//return r.replaceAll(text); with regular expr. package called PAT
		StringBuffer buf = new StringBuffer("");
		String returnString;
		if (stringToFind != null && !stringToFind.equals("")) {
			int index = text.indexOf(stringToFind);
			int index2 = 0;
			int length = stringToFind.length();
			while (index != -1) {
				buf.append(text.substring(index2, index)); //paste from last index or beginning
				buf.append(stringToReplace);
				index2 = index + length;
				index = text.indexOf(stringToFind, index2);
				if (index == -1) { //paste the last remaining part
					buf.append(text.substring(index2, text.length()));
				}
			}
		}
		returnString = buf.toString();
		if (returnString.equals("")) {
			returnString = text;
		}
		return returnString;
	}

	public static String findAndReplace(String text, String stringToFind, String stringAfterFindString, String stringToReplaceIfAfterStringIsNotPresent) {
		StringBuffer buf = new StringBuffer("");
		String returnString;
		String replaceString;

		if (stringToFind != null && !stringToFind.equals("")) {
			int index = text.indexOf(stringToFind);
			int index2 = 0;
			int length = stringToFind.length();
			int length2 = stringAfterFindString.length();
			while (index != -1) {
				replaceString = stringToFind;
				buf.append(text.substring(index2, index)); //paste from last index or beginning
				index2 = index + length;
				try {
					if (!text.substring(index2, index2 + length2).equals(stringAfterFindString)) {
						replaceString = stringToReplaceIfAfterStringIsNotPresent;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				buf.append(replaceString);
				index = text.indexOf(stringToFind, index2);
				if (index == -1) { //paste the last remaining part
					buf.append(text.substring(index2, text.length()));
				}
			}
		}
		returnString = buf.toString();
		if (returnString.equals("")) {
			returnString = text;
		}
		return returnString;
	}

	public static String findAndReplace(String text, String stringToFind, String stringAfterFindString, String stringToReplaceIfstringAfterFindStringMatches, String stringToReplace) {
		// Regex r = new Regex(stringToFind,stringReplace);
		//return r.replaceAll(text); with regular expr. package called PAT
		StringBuffer buf = new StringBuffer("");
		String returnString;
		String replaceString = stringToReplace;

		if (stringToFind != null && !stringToFind.equals("")) {
			int index = text.indexOf(stringToFind);
			int index2 = 0;
			int length = stringToFind.length();
			int length2 = stringAfterFindString.length();
			while (index != -1) {
				replaceString = stringToReplace;
				buf.append(text.substring(index2, index)); //paste from last index or beginning
				index2 = index + length;
				try {
					if (text.substring(index2, index2 + length2).equals(stringAfterFindString)) {
						replaceString = stringToReplaceIfstringAfterFindStringMatches;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				buf.append(replaceString);
				index = text.indexOf(stringToFind, index2);
				if (index == -1) { //paste the last remaining part
					buf.append(text.substring(index2, text.length()));
				}
			}
		}
		returnString = buf.toString();
		if (returnString.equals("")) {
			returnString = text;
		}
		return returnString;
	}

	public static String findAndReplace(String text, String stringToFind, String stringAfterFindString, String stringAfterFindStringToIgnoreIfFound, String stringToReplaceIfstringAfterFindStringMatches, String stringToReplace) {
		// Regex r = new Regex(stringToFind,stringReplace);
		//return r.replaceAll(text); with regular expr. package called PAT
		StringBuffer buf = new StringBuffer("");
		String returnString;
		String replaceString = stringToReplace;
		if (stringToFind != null && !stringToFind.equals("")) {
			int index = text.indexOf(stringToFind);
			int index2 = 0;
			int length = stringToFind.length();
			int length2 = stringAfterFindString.length();
			int length3 = stringAfterFindStringToIgnoreIfFound.length();
			while (index != -1) {
				replaceString = stringToReplace;
				buf.append(text.substring(index2, index)); //paste from last index or beginning
				index2 = index + length;
				try {
					if (text.substring(index2, index2 + length2).equals(stringAfterFindString)) {
						replaceString = stringToReplaceIfstringAfterFindStringMatches;
					}
					else
						if (text.substring(index2, index2 + length3).equals(stringAfterFindStringToIgnoreIfFound)) {
							replaceString = stringToFind;
						}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				buf.append(replaceString);
				index = text.indexOf(stringToFind, index2);
				if (index == -1) { //paste the last remaining part
					buf.append(text.substring(index2, text.length()));
				}
			}
		}
		returnString = buf.toString();
		if (returnString.equals("")) {
			returnString = text;
		}
		return returnString;
	}

	public static String stripHTMLandBodyTag(String html) {
		Vector crappy = TextSoap.FindAllBetween(html, "<body", "</body>");
		if (crappy.size() > 0) {
			String crap = (String) crappy.elementAt(0);
			int rest = crap.indexOf(">");
			if (rest != -1) {
				html = crap.substring(rest + 1, crap.length());
			}
		}
		else {
			crappy = TextSoap.FindAllBetween(html, "<BODY", "</BODY>");
			if (crappy.size() > 0) {
				String crap = (String) crappy.elementAt(0);
				int rest = crap.indexOf(">");
				if (rest != -1) {
					html = crap.substring(rest + 1, crap.length());
				}
			}
		}
		return html;
	}

	public static String stripHTMLTagAndChangeBodyTagToTable(String html) {
		Vector crappy = TextSoap.FindAllBetween(html, "<body", "</body>");
		String prefix = "<tr><td>";
		String suffix = "</td></tr></table>";
		String tabletag = "<table cellpadding=\"0\" cellspacing=\"0\" ";
		String crap = "";
		int bracket = -1;
		if (crappy.size() > 0) {
			crap = (String) crappy.elementAt(0);
			bracket = crap.indexOf(">");
		}
		else {
			crappy = TextSoap.FindAllBetween(html, "<BODY", "</BODY>");
			if (crappy.size() > 0) {
				crap = (String) crappy.elementAt(0);
				bracket = crap.indexOf(">");
			}
		}
		if (bracket != -1) {
			String temp = crap.substring(bracket + 1, crap.length());
			crap = crap.substring(0, bracket) + prefix + temp;
		}
		html = tabletag + crap + suffix;
		return html;
	}

	public static String addHTMLandBodyTag(String html) {
		StringBuffer buf = new StringBuffer("<html><body>");
		buf.append(html);
		buf.append("</body><html>");
		return buf.toString();
	}

	/**
	 * @todo finish
	 *  Description of the Method
	 *
	 *@param  text          Description of the Parameter
	 *@param  stringToFind  Description of the Parameter
	 *@param  stringInsert  Description of the Parameter
	 *@return               Description of the Return Value
	 */
	public static String findAndInsertAfter(String text, String stringToFind, String stringInsert) {
		return findAndReplace(text, stringToFind, stringToFind + stringInsert);
	}

	/**
	 * @todo finish
	 *  Description of the Method
	 *
	 *@param  text          Description of the Parameter
	 *@param  stringToFind  Description of the Parameter
	 *@param  stringInsert  Description of the Parameter
	 *@return               Description of the Return Value
	 */
	public static String findAndInsertBefore(String text, String stringToFind, String stringInsert) {
		return findAndReplace(text, stringToFind, stringInsert + stringToFind);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  text          Description of the Parameter
	 *@param  stringToFind  Description of the Parameter
	 *@return               Description of the Return Value
	 */
	public static String findAndCut(String text, String stringToFind) {
		return findAndReplace(text, stringToFind, "");
	}

	/**
	 *  Adds a feature to the Zero attribute of the TextSoap class
	 *
	 *@param  numberToFix  The feature to be added to the Zero attribute
	 *@return              Description of the Return Value
	 */
	public static String addZero(int numberToFix) {
		String FixedNumber;
		if (numberToFix < 10) {
			FixedNumber = "0";
		}
		else {
			FixedNumber = "";
		}
		return FixedNumber + numberToFix;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  text  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public static String formatString(String text) {
		text = findAndRemoveHtmlTags(text);
		text = findAndReplace(text, "\n", "<br>");
		return text;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  text  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	public static String findAndRemoveHtmlTags(String text) {
		if (text != null) {
			int firstpos = 0;
			int nextpos = 0;
			while (firstpos > -1 || nextpos > -1) {
				firstpos = text.indexOf("<", firstpos);
				nextpos = text.indexOf(">", firstpos);
				if (nextpos - firstpos > 4) {
					String toCut = text.substring(firstpos, nextpos);
					text = findAndCut(text, toCut);
				}
				if (firstpos > 0 && nextpos > 0) {
					firstpos -= 1;
					nextpos -= 1;
				}
			}
		}
		else {
			// text = "";
		}
		return text;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  text_body  Description of the Parameter
	 *@return            Description of the Return Value
	 */
	public static String formatText(String text_body) {
		if (text_body == null || text_body.equals("")) {
			text_body = "";
		}

		text_body = findAndReplace(text_body, "*", "<li>");
		text_body = findAndReplace(text_body, ".\r\n", ".<br><br>");
		text_body = findAndReplace(text_body, "?\r\n", "?<br><br>");
		text_body = findAndReplace(text_body, "!\r\n", "!<br><br>");
		text_body = findAndReplace(text_body, ")\r\n", ")<br><br>");

		text_body = findAndReplace(text_body, "\r\n", "<br>");
		text_body = findAndReplace(text_body, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		return text_body;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  text_body  Description of the Parameter
	 *@return            Description of the Return Value
	 */
	public static Vector createTextLink(String text_body) {
		Vector linkVector = TextSoap.FindAllBetween(text_body, "Link(", ")");
		return linkVector;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  string         Description of the Parameter
	 *@return                Description of the Return Value
	 *@exception  Exception  Description of the Exception
	 */
	public static String removeWhiteSpace(String string) throws Exception {
		string = findAndReplace(string, " ", "");
		return string;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  myString  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public static boolean numericString(String myString) {
		boolean isTrue = true;
		for (int i = 0; i < myString.length(); i++) {
			if (!(myString.charAt(i) == '0' || myString.charAt(i) == '1' || myString.charAt(i) == '2' || myString.charAt(i) == '3' || myString.charAt(i) == '4' || myString.charAt(i) == '5' || myString.charAt(i) == '6' || myString.charAt(i) == '7' || myString.charAt(i) == '8' || myString.charAt(i) == '9')) {
				isTrue = false;
			}
		}
		return isTrue;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  myString  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public static boolean nonNumericString(String myString) {
		boolean isTrue = true;
		int length = myString.length();
		for (int i = 0; i < length; i++) {
			char c = myString.charAt(i);
			switch (c) {
				case '0' : break;
				case '1' : break;
				case '2' : break;
				case '3' : break;
				case '4' : break;
				case '5' : break;
				case '6' : break;
				case '7' : break;
				case '8' : break;
				case '9' : break;
				default :
					isTrue = false;
					break;
			}
		}
		return isTrue;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  stringToDecimalFormat  Description of the Parameter
	 *@return                        Description of the Return Value
	 */
	public static String singleDecimalFormat(String stringToDecimalFormat) {
		symbols.setDecimalSeparator('.');
		singleDecimalFormat.setDecimalFormatSymbols(symbols);
		double doubleToDecimalFormat = Double.parseDouble(findAndReplace(stringToDecimalFormat, ',', '.'));
		return singleDecimalFormat.format(doubleToDecimalFormat);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  doubleToDecimalFormat  Description of the Parameter
	 *@return                        Description of the Return Value
	 */
	public static String singleDecimalFormat(double doubleToDecimalFormat) {
		symbols.setDecimalSeparator('.');
		singleDecimalFormat.setDecimalFormatSymbols(symbols);
		return singleDecimalFormat.format(doubleToDecimalFormat);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  doubleToDecimalFormat  Description of the Parameter
	 *@param  numberOfDecimals       Description of the Parameter
	 *@return                        Description of the Return Value
	 */
	public static String decimalFormat(double doubleToDecimalFormat, int numberOfDecimals) {
		StringBuffer decimalString = new StringBuffer("0.0");
		//always at least one decimal
		for (int i = 1; i < numberOfDecimals; i++) {
			decimalString.append("0");
		}
		DecimalFormat decimalFormat = new DecimalFormat(decimalString.toString());
		symbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(symbols);
		return decimalFormat.format(doubleToDecimalFormat);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  stringToDecimalFormat  Description of the Parameter
	 *@param  numberOfDecimals       Description of the Parameter
	 *@return                        Description of the Return Value
	 */
	public static String decimalFormat(String stringToDecimalFormat, int numberOfDecimals) {
		StringBuffer decimalString = new StringBuffer("0.0");
		//always at least one decimal
		for (int i = 1; i < numberOfDecimals; i++) {
			decimalString.append("0");
		}
		DecimalFormat decimalFormat = new DecimalFormat(decimalString.toString());
		symbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(symbols);
		double doubleToDecimalFormat = Double.parseDouble(findAndReplace(stringToDecimalFormat, ',', '.'));
		return decimalFormat.format(doubleToDecimalFormat);
	}

	public static String findAndReplaceOnPrefixCondition(String text, String stringToFind, String prefix, String stringToReplace, boolean replaceOnMissingPrefix) {
		StringBuffer buf = new StringBuffer("");
		String returnString;
		String replaceString;
		if (stringToFind != null && !stringToFind.equals("")) {
			int index = text.indexOf(stringToFind);
			int index2 = 0;
			int length = stringToFind.length();
			int length2 = prefix.length();
			boolean A = false;
			boolean B = replaceOnMissingPrefix;
			while (index != -1) {
				replaceString = stringToFind;
				buf.append(text.substring(index2, index)); //paste from last index or beginning
				index2 = index + length;
				try {
					//(A && !B ) || (!A && B) )  XOR
					// isbefore replaceOnMissingPrefix
					// T          T   nothing
					// F          F   nothing
					// T          F   replace
					// F          T   replace
					A = text.substring(index2 - length2 - length, index2 - 2).equals(prefix);
					if ((A && !B) || (!A && B)) {
						replaceString = stringToReplace;
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				buf.append(replaceString);
				index = text.indexOf(stringToFind, index2);
				if (index == -1) { //paste the last remaining part
					buf.append(text.substring(index2, text.length()));
				}
			}
		}
		returnString = buf.toString();
		if (returnString.equals("")) {
			returnString = text;
		}
		return returnString;
	}

	/**
	 * @return the index of the first number or if no number found it returns -1
	 */
	public static int getIndexOfFirstNumberInString(String text) {
		int length = text.length();
		int lowestIndex = length;
		int tempIndex = -1;
		tempIndex = text.indexOf('0');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('1');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('2');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('3');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('4');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('5');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('6');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('7');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('8');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		tempIndex = text.indexOf('9');
		lowestIndex = ((tempIndex != -1) && (tempIndex < lowestIndex)) ? tempIndex : lowestIndex;
		lowestIndex = (lowestIndex == length) ? -1 : lowestIndex;
		return lowestIndex;
	}

	/**
	 * Capitalizes the given string, making the first letter uppercase and the others
	 * lowercase.
	 * @param stringToCapitalize	The string to capitalize
	 * @return String
	 * @throws NullPointerException	if stringToCapitalize is null
	 */
	public static String capitalize(String stringToCapitalize) throws NullPointerException {
		String string = stringToCapitalize.toLowerCase();
		char chars[] = string.toCharArray();
		if (chars.length > 0)
			chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	/**
	 * Capitalizes the given string, making the first characters uppercase as well as all
	 * characters after the specified separator.  All other characters are displayed
	 * lowercase.
	 * @param stringToCapitalize	The string to capitalize
	 * @param separator					The separator to use
	 * @return String
	 * @throws NullPointerException	if stringToCapitalize is null
	 */
	public static String capitalize(String stringToCapitalize, String separator) throws NullPointerException {
		StringTokenizer tokens = new StringTokenizer(stringToCapitalize, separator);
		StringBuffer returnString = new StringBuffer();
		while (tokens.hasMoreTokens()) {
			returnString.append(capitalize(tokens.nextToken()));
			if (tokens.hasMoreTokens())
				returnString.append(separator);
		}
		return returnString.toString();
	}

	/**
	 * Changes all the special characters in the given string to their corresponding symbolic value.
	 * For example: '& => &amp;', '< = &lt;'
	 * @param stringToConvert	The string to convert.
	 * @return String
	 */
	public static String convertSpecialCharacters(String stringToConvert) {
		StringBuffer sb = new StringBuffer();
		int n = stringToConvert.length();
		for (int i = 0; i < n; i++) {
			char c = stringToConvert.charAt(i);
			switch (c) {
				case '<' :
					sb.append("&lt;");
					break;
				case '>' :
					sb.append("&gt;");
					break;
				case '&' :
					sb.append("&amp;");
					break;
				case 'á' :
					sb.append("&aacute;");
					break;
				case 'Á' :
					sb.append("&Aacute;");
					break;
				case 'à' :
					sb.append("&agrave;");
					break;
				case 'À' :
					sb.append("&Agrave;");
					break;
				case 'â' :
					sb.append("&acirc;");
					break;
				case 'Â' :
					sb.append("&Acirc;");
					break;
				case 'ä' :
					sb.append("&auml;");
					break;
				case 'Ä' :
					sb.append("&Auml;");
					break;
				case 'å' :
					sb.append("&aring;");
					break;
				case 'Å' :
					sb.append("&Aring;");
					break;
				case '›' :
					sb.append("&eth;");
					break;
				case '‹' :
					sb.append("&ETH;");
					break;
				case 'ç' :
					sb.append("&ccedil;");
					break;
				case 'Ç' :
					sb.append("&Ccedil;");
					break;
				case 'é' :
					sb.append("&eacute;");
					break;
				case 'É' :
					sb.append("&Eacute;");
					break;
				case 'è' :
					sb.append("&egrave;");
					break;
				case 'È' :
					sb.append("&Egrave;");
					break;
				case 'ê' :
					sb.append("&ecirc;");
					break;
				case 'Ê' :
					sb.append("&Ecirc;");
					break;
				case 'ë' :
					sb.append("&euml;");
					break;
				case 'Ë' :
					sb.append("&Euml;");
					break;
				case 'ï' :
					sb.append("&iuml;");
					break;
				case 'Ï' :
					sb.append("&Iuml;");
					break;
				case 'í' :
					sb.append("&iacute;");
					break;
				case 'Í' :
					sb.append("&Iacute;");
					break;
				case 'ó' :
					sb.append("&oacute;");
					break;
				case 'Ó' :
					sb.append("&Oacute;");
					break;
				case 'ô' :
					sb.append("&ocirc;");
					break;
				case 'Ô' :
					sb.append("&Ocirc;");
					break;
				case 'ø' :
					sb.append("&oslash;");
					break;
				case 'Ø' :
					sb.append("&Oslash;");
					break;
				case 'ß' :
					sb.append("&szlig;");
					break;
				case 'ú' :
					sb.append("&uacute;");
					break;
				case 'Ú' :
					sb.append("&Uacute;");
					break;
				case 'ù' :
					sb.append("&ugrave;");
					break;
				case 'Ù' :
					sb.append("&Ugrave;");
					break;
				case 'û' :
					sb.append("&ucirc;");
					break;
				case 'Û' :
					sb.append("&Ucirc;");
					break;
				case 'ü' :
					sb.append("&uuml;");
					break;
				case 'Ü' :
					sb.append("&Uuml;");
					break;
				case '‡' :
					sb.append("&yacute;");
					break;
				case '†' :
					sb.append("&Yacute;");
					break;
				case 'ﬂ' :
					sb.append("&thorn;");
					break;
				case 'ﬁ' :
					sb.append("&THORN;");
					break;
				case 'æ' :
					sb.append("&aelig;");
					break;
				case 'Æ' :
					sb.append("&AElig;");
					break;
				case 'ö' :
					sb.append("&ouml;");
					break;
				case 'Ö' :
					sb.append("&Ouml;");
					break;
				case '®' :
					sb.append("&reg;");
					break;
				case '©' :
					sb.append("&copy;");
					break;
				case '€' :
					sb.append("&euro;");
					break;
				case ' ' :
					sb.append("&nbsp;");
					break;
				case '\"' :
					sb.append("&quot;");
					break;
				default :
					sb.append(c);
					break;
			}
		}
		return sb.toString();
	}
}
// class TestSoap
