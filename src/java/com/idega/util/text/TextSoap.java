/*
 *  Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.util.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ibm.icu.text.Transliterator;
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
		//Regex r = new Regex(stringToFind,stringReplace);
		//return r.replaceAll(text); with regular expr. package called PAT
		
		StringBuffer buf = new StringBuffer("");
		if (stringToFind != null && !stringToFind.equals("")) {
			int index = text.indexOf(stringToFind);
			int index2 = 0;
			int length = stringToFind.length();
			
			if (index == -1) {
				return text;
			}
			
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
		return buf.toString();
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

	public static String findAndReplace(String text, String stringToFind, String[] stringsAfterFindString, String stringToReplaceIfNoneOfAfterStringsArePresent) {
		StringBuffer buf = new StringBuffer("");
		String returnString;
		String replaceString;

		if (stringToFind != null && !stringToFind.equals("")) {
			int index = text.indexOf(stringToFind);
			int index2 = 0;
			int length = stringToFind.length();
			while (index != -1) {
				replaceString = stringToFind;
				buf.append(text.substring(index2, index)); //paste from last index or beginning
				index2 = index + length;
				try {
					boolean afterStringFound = false;
					if(stringsAfterFindString!=null) {
						for(int i=0; i<stringsAfterFindString.length && !afterStringFound; i++) {
							String afterStr = stringsAfterFindString[i];
							int length2 = afterStr.length();
							if(text.substring(index2, index2 + length2).equals(afterStr)) {
								afterStringFound = true;
							}
						}
					}
					if (!afterStringFound) {
						replaceString = stringToReplaceIfNoneOfAfterStringsArePresent;
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
	   *  Note: Former method of the class TextFormatter, unchanged name. 
	   *
	   *@param  textBody       Description of the Parameter
	   *@return                Description of the Return Value
	   */
	public static String formatText(String textBody) {
		textBody = TextSoap.findAndReplaceOnPrefixCondition(textBody, "\r\n", ">","<br/>",true);
	    textBody = TextSoap.findAndReplace(textBody, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	    return textBody;
	}	
	
	
	/**
	 *  Note: Former name: "formatText"
	 * 	Name was changed because method formatText of the class TextFormatter
	 * 	was moved to this class.
	 *
	 *@param  text_body  Description of the Parameter
	 *@return            Description of the Return Value
	 */
	public static String formatTabsAndReturnsToHtml(String text_body) {
		if (text_body == null || text_body.equals("")) {
			text_body = "";
		}

		//text_body = findAndReplace(text_body, "*", "<li>");
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
	public static String removeWhiteSpace(String string) {
	    if (string == null || string.equals("")) {
			return "";
		}
	    
	    StringBuffer stringBuff = new StringBuffer(string);
	    StringBuffer resultBuff = new StringBuffer();
		for (int i = 0; i < stringBuff.length(); i++) {
		    char c = stringBuff.charAt(i);
		    if (!Character.isWhitespace(c) && !Character.isSpaceChar(c) ) {
		        resultBuff.append(c);
		    }
		}
		return resultBuff.toString();
	}

	public static String removeWhiteSpaceFromBeginningOfString(String string) {
	    if (string == null || string.equals("")) {
			return "";
		}
	    
	    StringBuffer stringBuff = new StringBuffer(string);
	    StringBuffer resultBuff = new StringBuffer();
		for (int i = 0; i < stringBuff.length()-1; i++) {
		    char c = stringBuff.charAt(i);
		    if (!(Character.isWhitespace(c) || Character.isSpaceChar(c) )) {
		        resultBuff = new StringBuffer(stringBuff.substring(i,stringBuff.length()));
		        break;
		    } 
		}
		return resultBuff.toString();
	}

	public static String removeWhiteSpaceFromEndOfString(String string) {
	    if (string == null || string.equals("")) {
			return "";
		}
	    StringBuffer stringBuff = new StringBuffer(string);
	    StringBuffer resultBuff = new StringBuffer();
		for (int i = stringBuff.length()-1; i > -1; i--) {
		    char c = stringBuff.charAt(i);
		    if (!(Character.isWhitespace(c) || Character.isSpaceChar(c) )) {
		        resultBuff = new StringBuffer(stringBuff.substring(0,i+1));
		        break;
		    } 
		}
		return resultBuff.toString();
	}

	public static String removeWhiteSpaceFromBeginningAndEndOfString(String string) {
		String returnString = string;
		returnString = removeWhiteSpaceFromBeginningOfString(returnString);
		returnString = removeWhiteSpaceFromEndOfString(returnString);
		return returnString;
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
					System.err.println(e.getMessage());
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
		if (chars.length > 0) {
			chars[0] = Character.toUpperCase(chars[0]);
		}
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
			if (tokens.hasMoreTokens()) {
				returnString.append(separator);
			}
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
		Transliterator transliterator = Transliterator.getInstance("Any-Hex");
		StringBuffer sb = new StringBuffer();
		int n = stringToConvert.length();
		for (int i = 0; i < n; i++) {
			char c = stringToConvert.charAt(i);
			String unicode = transliterator.transliterate(String.valueOf(c));
			
			if (unicode.equalsIgnoreCase("\\u003C")) {
				sb.append("&lt;");
			}
			else if (unicode.equals("\\u003E")) {
				sb.append("&gt;");
			}
			else if (unicode.equals("\\u0026")) {
				sb.append("&amp;");
			}
			else if (unicode.equals("\\u00E1")) {
				sb.append("&aacute;");
			}
			else if (unicode.equals("\\u00C1")) {
				sb.append("&Aacute;");
			}
			else if (unicode.equals("\\u00F0")) {
				sb.append("&eth;");
			}
			else if (unicode.equals("\\u00D0")) {
				sb.append("&ETH;");
			}
			else if (unicode.equals("\\u00E4")) {
				sb.append("&auml;");
			}
			else if (unicode.equals("\\u00C4")) {
				sb.append("&Auml;");
			}
			else if (unicode.equals("\\u00E5")) {
				sb.append("&aring;");
			}
			else if (unicode.equals("\\u00C5")) {
				sb.append("&Aring;");
			}
			else if (unicode.equals("\\u00E9")) {
				sb.append("&eacute;");
			}
			else if (unicode.equals("\\u00C9")) {
				sb.append("&Eacute;");
			}
			else if (unicode.equals("\\u00ED")) {
				sb.append("&iacute;");
			}
			else if (unicode.equals("\\u00CD")) {
				sb.append("&Iacute;");
			}
			else if (unicode.equals("\\u00F3")) {
				sb.append("&oacute;");
			}
			else if (unicode.equals("\\u00D3")) {
				sb.append("&Oacute;");
			}
			else if (unicode.equals("\\u00F8")) {
				sb.append("&oslash;");
			}
			else if (unicode.equals("\\u00D8")) {
				sb.append("&Oslash;");
			}
			else if (unicode.equals("\\u00DF")) {
				sb.append("&szlig;");
			}
			else if (unicode.equals("\\u00FA")) {
				sb.append("&uacute;");
			}
			else if (unicode.equals("\\u00DA")) {
				sb.append("&Uacute;");
			}
			else if (unicode.equals("\\u00FC")) {
				sb.append("&uuml;");
			}
			else if (unicode.equals("\\u00DC")) {
				sb.append("&Uuml;");
			}
			else if (unicode.equals("\\u00FD")) {
				sb.append("&yacute;");
			}
			else if (unicode.equals("\\u00DD")) {
				sb.append("&Yacute;");
			}
			else if (unicode.equals("\\u00FE")) {
				sb.append("&thorn;");
			}
			else if (unicode.equals("\\u00DE")) {
				sb.append("&THORN;");
			}
			else if (unicode.equals("\\u00E6")) {
				sb.append("&aelig;");
			}
			else if (unicode.equals("\\u00C6")) {
				sb.append("&AElig;");
			}
			else if (unicode.equals("\\u00F6")) {
				sb.append("&ouml;");
			}
			else if (unicode.equals("\\u00D6")) {
				sb.append("&Ouml;");
			}
			else if (unicode.equals("\\u0022")) {
				sb.append("&quot;");
			}
			else if (unicode.equals("\\u0027")) {
				sb.append("&apos;");
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String encodeToValidExcelSheetName(String sheetName) {
		String encodedString = new String(sheetName);
		encodedString = findAndReplace(encodedString, "\\", " ");
		encodedString = findAndReplace(encodedString, "/", " ");
		encodedString = findAndReplace(encodedString, "*", " ");
		encodedString = findAndReplace(encodedString, "?", " ");
		encodedString = findAndReplace(encodedString, "[", " ");
		encodedString = findAndReplace(encodedString, "]", " ");
		if (encodedString.length() > 31) {
		    encodedString = encodedString.substring(0,31);
		}
		return encodedString;
	}
}
// class TestSoap
