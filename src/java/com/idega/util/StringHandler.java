package com.idega.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 * Title:        StringHandler
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega margmi?lun hf.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>,<a href="mailto:gummi@idega.is">Gudmundur Saemundsson</a>
 * @version 1.0  

 */   

public class StringHandler {
  
  /**
   * substitution string if a character is not a letter
   */
  public static String NO_LETTER_SUBSTITUTION = "x";
  
  /** 
   * groups of characters (represented by uni code) 
   * replaced by strings defined in substitution.
   * Use the uni code table (@see http:\\www.unicode.org)
   */
  static public int[][] SUBSTITUTION_GROUP = { 
      // 192 - 207
      { 192 , 193, 194, 195, 196, 197} , // A
      { 198 }, // Ae 
      { 199 }, // C
      { 200, 201, 202, 203}, // E
      { 204, 205, 206, 207}, // I
      // 208 - 223
      { 208 }, // D
      { 209 }, // N
      { 210, 211, 212, 213, 214 }, // O
      { 215 }, // x  - char 215 is no letter
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
      { 255 }}; // y
      
  
  /**
   * substitution strings for characters defined in substitution group
   */
  static public String[] SUBSTITUTION = 
    { "A", "Ae", "C", "E", "I",
      "D", "N", "O", NO_LETTER_SUBSTITUTION , "O", "U", "Y", "Th", "ss",
      "a", "ae", "c", "e", "i",
      "d", "n", "o", NO_LETTER_SUBSTITUTION , "o", "u", "y", "th", "y"};
     
  
	public static String alfabet= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	//Alphabet+Numbers without ambigous characters such as 0 O and I l and 1
	public static String alphabetNonAmbigous= "ABCDEFGHJKLMNPQRSTUVWXYZabcdefhijkmnoprstuvwxyz23456789";
	
	public static final String EMPTY_STRING= "";
	public static final String DASH= "-";
	
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
	 * Returns an random String with the desired length with the Roman
	 * alphabet (upper and lower case) without ambigous characters (which can
	 * be confused together) and numbers 2-9
	 */
	public static String getRandomStringNonAmbiguous(int length) {
		StringBuffer buffer= new StringBuffer();
		for (int i= 0; i < length; i++) {
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
    StringBuffer newString = new StringBuffer();
		for (int i= 0; i < cAinputString.length; i++) {
			char c= cAinputString[i];
			newString.append(translateCharacter(c));
		}
		return newString.toString();
	}
  
  /** 
   * Returns true if the specified object returns a non empty string when the toString() method is invoked else
   * false.
   */
  public static boolean isNotEmpty(Object element) {
    String aString = element.toString();
    return (aString != null && aString.length() > 0);
  }
  
  /**
   * Returns true if each element of the collection returns a non empty string when the toString() method is 
   * invoked else false.
   */
  public static boolean elementsAreNotEmpty(Collection collection)  {
    if (collection == null || collection.isEmpty())   {
      return false;
    }
    Iterator iter = collection.iterator();
    while (iter.hasNext())  {
      Object element = iter.next();
      String aString = element.toString();
      if (! StringHandler.isNotEmpty(aString))  {
        return false;
      }
		}
    return true;
  }
  
  /** Replaces all occurences of the specified pattern in the specified string with the
   * specified replace, ignores case.
   * Example: replace("A cat is not a caterpillar", "ca", "hu") returns "A hut is not a huterpillar"
   * @param str
   * @param pattern
   * @param replace
   * @return modified (new) string
   * @author thomas 
   */
	public static String replaceIgnoreCase(String str, String pattern, String replace)  {
		return replace(str.toUpperCase(), pattern.toUpperCase(), replace);
	}
	
  /** Replaces all occurences of the specified pattern in the specified string with the
   * specified replace
   * Example: replace("A cat is not a caterpillar", "ca", "hu") returns "A hut is not a huterpillar"
   * @param str
   * @param pattern
   * @param replace
   * @return modified (new) string
   * @author thomas 
   */
	public static String replace(String str, String pattern, String replace)  {
		int s = 0;
    int e = 0;
    StringBuffer result = new StringBuffer();
    while ((e = str.indexOf(pattern, s)) >= 0) {
    	result.append(str.substring(s, e));
      result.append(replace);
      s = e+pattern.length();
    }
    result.append(str.substring(s));
    return result.toString();
	}
	
  /** Removes all occurences of the specified pattern from the specified string.
   * Example: replace("A cat is not a caterpillar", "ca") returns "A t is not a terpillar"
   * @param str
   * @param pattern
   * @return modified (new) string
   * @author thomas 
   */
	public static String remove(String str, String pattern)	{
		int s = 0;
    int e = 0;
    StringBuffer result = new StringBuffer();
    while ((e = str.indexOf(pattern, s)) >= 0) {
    	result.append(str.substring(s, e));
      s = e+pattern.length();
    }
    result.append(str.substring(s));
    return result.toString();
	}
		
  /** Removes all occurences of white spaces from the specified string.
   * Example: replace("A cat is not a caterpillar") returns "Acatisnotacaterpillar"
   * @param str
   * @param pattern
   * @return modified (new) string
   * @author thomas 
   */
	public static String removeWhiteSpace(String str)	{
		return StringHandler.remove(str, " ");
	}
	
	/** Gets the words that are contained in a string corresponding to a list of allowed words.
	 * If the string contains a non allowed word an empty list ist returned.
	 * Ignores case and returns only words in upper case.
	 * Example:
	 * list = "(" ")" "or" "xor" "A" "B" 
	 * getElements("A OR(B xor C)", list, false) returns "(" "A" "OR" "(" "B" "XOR" "C" ")"
	 * @param str
	 * @param allowedWords
	 * @param list of allowed words in str else null
	 * @param case sensitivity
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
	
	
	
	
	
	/** Gets the words that are contained in a string corresponding to a list of allowed words.
	 * If the string contains a non allowed word an empty list ist returned.
	 * Example:
	 * list = "(" ")" "or" "xor" "A" "B" 
	 * getElements("A or(B xor C)", list, true) returns "(" "A" "or" "(" "B" "xor" "C" ")"
	 * @param str
	 * @param allowedWords
	 * @param list of allowed words in str else null
	 * @author thomas
	 */
	public static List getElements(String string, Collection allowedWords) {
		List result = new ArrayList();
		  // order of conditions
		SortedSet orderedWords = 
  		new TreeSet(new Comparator() { 
  			public int  compare(Object first, Object second) {
  				int firstLength = ((String) first).length();
  				int secondLength = ((String) second).length();
  				if (firstLength < secondLength) {
  					return 1;
  				}
  				else if (firstLength > secondLength) {
  					return -1;
  				}
  				return ((Comparable) first).compareTo((Comparable) second);
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
			while (wordIterator.hasNext() && (! wordIsRecognized) ) {
				String tempWord = (String) wordIterator.next();
				if (wordIsRecognized == stringWithoutWhiteSpace.startsWith(tempWord, index)) {
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
			
				
		
	
  
	/** Checks if the specified string contains the specified pattern.
	 * Example: replace("A cat is not a caterpillar", "ca") returns true
	 * @param string
	 * @param pattern
	 * @return true if the string contains the specified pattern else false.
	 * @author thomas
	 */
	public static boolean contains(String string, String pattern)	{
		return (string.indexOf(pattern) >= 0);
	}
	

		
    
  
  /**
   * replaces all non roman characters by suitable strings
   */
	private static String translateCharacter(char c) {   
    // get uni code number  
    int value = (int) c;
    // is c a "normal" letter?
    if (( 'A' <= value && value <= 'Z' ) ||
        ( 'a' <= value && value <= 'z'))
      return String.valueOf(c);
    int groupsNumber = SUBSTITUTION_GROUP.length;		
    int i = 0;
    // look up to which group the character belongs
    while (( i < groupsNumber) 
            && (Arrays.binarySearch(SUBSTITUTION_GROUP[i],value) < 0)) {
      i++;
    }
    // if the character does not belong to a group return special substitution
    if (i == groupsNumber)
      return NO_LETTER_SUBSTITUTION;
      // return substitution string
    return SUBSTITUTION[i];
	}
	
	/**
	 * Returns a string or dash if there is no string. If the string is either
	 * null or an empty string a dash (-) is returned else it returns the input
	 * string str.
	 * @param str String to check
	 * @return String which is either a dash or the input str.
	 */
	public static String getStringOrDash(String str){
		if(str==null){
			return DASH;
		}
		else{
			if(str.equals(EMPTY_STRING)){
				return DASH;
			}
			else{
				return str;
			}
		}
	}
	
	
} 
