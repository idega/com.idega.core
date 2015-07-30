package com.idega.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StringUtil {

	public static List<String> getValuesFromString(String value, String separator) {

		if (value == null || separator == null)
			return null;

		String[] values = value.split(separator);

		if (values == null)
			return null;

		List<String> extractedValues = new ArrayList<String>();
		Collections.addAll(extractedValues, values);
		return extractedValues;
	}

	/**
	 * Handy method for null and empty check
	 * @param string to check if is null or empty string
	 * @return return str == null || str.length() == 0;
	 */
	public static boolean isEmpty(String str) {

		return str == null || str.length() == 0;
	}

	 /**
	   * Escape characters for text appearing in HTML markup.
	   *
	   * <P>The following characters are replaced with corresponding HTML character entities :
	   * <table border='1' cellpadding='3' cellspacing='0'>
	   * <tr><th> Character </th><th>Replacement</th></tr>
	   * <tr><td> < </td><td> "&lt;" </td></tr>
	   * <tr><td> > </td><td> &gt; </td></tr>
	   * <tr><td> & </td><td> &amp; </td></tr>
	   * <tr><td> " </td><td> &quot;</td></tr>
	   * <tr><td> \t </td><td> &#009;</td></tr>
	   * <tr><td> ! </td><td> &#033;</td></tr>
	   * <tr><td> # </td><td> &#035;</td></tr>
	   * <tr><td> $ </td><td> &#036;</td></tr>
	   * <tr><td> % </td><td> &#037;</td></tr>
	   * <tr><td> ' </td><td> &#039;</td></tr>
	   * <tr><td> ( </td><td> &#040;</td></tr>
	   * <tr><td> ) </td><td> &#041;</td></tr>
	   * <tr><td> * </td><td> &#042;</td></tr>
	   * <tr><td> + </td><td> &#043; </td></tr>
	   * <tr><td> , </td><td> &#044; </td></tr>
	   * <tr><td> - </td><td> &#045; </td></tr>
	   * <tr><td> . </td><td> &#046; </td></tr>
	   * <tr><td> / </td><td> &#047; </td></tr>
	   * <tr><td> : </td><td> &#058;</td></tr>
	   * <tr><td> ; </td><td> &#059;</td></tr>
	   * <tr><td> = </td><td> &#061;</td></tr>
	   * <tr><td> ? </td><td> &#063;</td></tr>
	   * <tr><td> @ </td><td> &#064;</td></tr>
	   * <tr><td> [ </td><td> &#091;</td></tr>
	   * <tr><td> \ </td><td> &#092;</td></tr>
	   * <tr><td> ] </td><td> &#093;</td></tr>
	   * <tr><td> ^ </td><td> &#094;</td></tr>
	   * <tr><td> _ </td><td> &#095;</td></tr>
	   * <tr><td> ` </td><td> &#096;</td></tr>
	   * <tr><td> { </td><td> &#123;</td></tr>
	   * <tr><td> | </td><td> &#124;</td></tr>
	   * <tr><td> } </td><td> &#125;</td></tr>
	   * <tr><td> ~ </td><td> &#126;</td></tr>
	   * </table>
	   */
	public static String escapeHTMLSpecialChars(String aText){
	     StringBuilder result = new StringBuilder();
	     StringCharacterIterator iterator = new StringCharacterIterator(aText);
	     char character =  iterator.current();
	     while (character != CharacterIterator.DONE ){
	       if (character == '<') {
	         result.append("&lt;");
	       }
	       else if (character == '>') {
	         result.append("&gt;");
	       }
	       else if (character == '&') {
	         result.append("&amp;");
	      }
	       else if (character == '\"') {
	         result.append("&quot;");
	       }
	       else if (character == '\t') {
	         addCharEntity(9, result);
	       }
	       else if (character == '!') {
	         addCharEntity(33, result);
	       }
	       else if (character == '#') {
	         addCharEntity(35, result);
	       }
	       else if (character == '$') {
	         addCharEntity(36, result);
	       }
	       else if (character == '%') {
	         addCharEntity(37, result);
	       }
	       else if (character == '\'') {
	         addCharEntity(39, result);
	       }
	       else if (character == '(') {
	         addCharEntity(40, result);
	       }
	       else if (character == ')') {
	         addCharEntity(41, result);
	       }
	       else if (character == '*') {
	         addCharEntity(42, result);
	       }
	       else if (character == '+') {
	         addCharEntity(43, result);
	       }
	       else if (character == ',') {
	         addCharEntity(44, result);
	       }
	       else if (character == '-') {
	         addCharEntity(45, result);
	       }
	       else if (character == '.') {
	         addCharEntity(46, result);
	       }
	       else if (character == '/') {
	         addCharEntity(47, result);
	       }
	       else if (character == ':') {
	         addCharEntity(58, result);
	       }
	       else if (character == ';') {
	         addCharEntity(59, result);
	       }
	       else if (character == '=') {
	         addCharEntity(61, result);
	       }
	       else if (character == '?') {
	         addCharEntity(63, result);
	       }
	       else if (character == '@') {
	         addCharEntity(64, result);
	       }
	       else if (character == '[') {
	         addCharEntity(91, result);
	       }
	       else if (character == '\\') {
	         addCharEntity(92, result);
	       }
	       else if (character == ']') {
	         addCharEntity(93, result);
	       }
	       else if (character == '^') {
	         addCharEntity(94, result);
	       }
	       else if (character == '_') {
	         addCharEntity(95, result);
	       }
	       else if (character == '`') {
	         addCharEntity(96, result);
	       }
	       else if (character == '{') {
	         addCharEntity(123, result);
	       }
	       else if (character == '|') {
	         addCharEntity(124, result);
	       }
	       else if (character == '}') {
	         addCharEntity(125, result);
	       }
	       else if (character == '~') {
	         addCharEntity(126, result);
	       }
	       else {
	         //the char is not a special one
	         //add it to the result as is
	         result.append(character);
	       }
	       character = iterator.next();
	     }
	     return result.toString();
	  }


	private static void addCharEntity(Integer aIdx, StringBuilder aBuilder){
	    String padding = "";
	    if( aIdx <= 9 ){
	       padding = "00";
	    }
	    else if( aIdx <= 99 ){
	      padding = "0";
	    }
	    else {
	      //no prefix
	    }
	    String number = padding + aIdx.toString();
	    aBuilder.append("&#" + number + ";");
	}

	/**
	 * Escape characters not allowed in file names;
	 *
	 * @param pdfName
	 * @return escaped file name;
	 */
	public static String escapeFileNameSpecialCharacters(String fileName) {
		StringBuilder result = new StringBuilder();
		StringCharacterIterator iterator = new StringCharacterIterator(fileName);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			switch (character) {
			case '/':
			case '\\':
			case ':':
			case '|':
			case '?':
			case '%':
			case '*':
			case '>':
			case '<': {
				result.append("_").append((int) (character)).append("_");
				break;
			}
			default:
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public static List<String> getLinesFromString(String content) {
		List<String> strings = new ArrayList<String>();
		if (isEmpty(content))
			return strings;

		LineNumberReader lineReader = new LineNumberReader(new StringReader(content));

		try {
			lineReader.mark(1);
			while (lineReader.read() != -1) {
				lineReader.reset();
				strings.add(lineReader.readLine());
				lineReader.mark(1);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeReader(lineReader);
		}

		return strings;
	}

	/**
	 * 
	 * @param strings of {@link Integer}s to convert, not <code>null</code>;
	 * @return {@link ArrayList} of converted {@link Integer}s 
	 * or {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public static ArrayList<Integer> toIntegers(Collection<String> strings) {
		ArrayList<Integer> integers = new ArrayList<Integer>();

		if (!ListUtil.isEmpty(strings)) {
			for(String string : strings) {
				try {
					integers.add(Integer.valueOf(string));
				} catch (Exception e) {}
			}
		}

		return integers;
	}
}