/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.util.text;
//import idega.*;
//import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * General class for text manipulation
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>, Eirikur Hrafnsson
*@version 1.2
*/
public class TextSoap{
   private static DecimalFormat singleDecimalFormat = new DecimalFormat("0.0");
   private static DecimalFormatSymbols symbols = singleDecimalFormat.getDecimalFormatSymbols();

	/**
	 * Function to cut out all the text between two tokens in a larger string and return the results as a Vector of strings
	 */
	public static Vector FindAllBetween(String inputString,String begintoken,String endtoken){
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
		String tempString= new String("");

		try{
			while ((newString.indexOf(begintoken) != -1) && (newString.indexOf(endtoken) != -1) ){//&& (newString != null)){
				beginnum = newString.indexOf(begintoken) + begintoken.length();

				endnum = newString.indexOf(endtoken,beginnum);

				newSubstring = newString.substring(beginnum,endnum);

				tempString = newString.substring(endnum + endtoken.length());//cuts down the string from where occurence last fount

				newString = tempString;

				//removeCharacters(newSubstring);

				outVector.addElement(removeCharacters(newSubstring));
				counter++;
			}

		}
		catch (Exception e){
                  outVector.addElement("TextSoapError" + counter);
                  e.printStackTrace(System.err);
		}

		outVector.trimToSize();
		//System.out.print(outVector.size());
		return outVector;
	}
	/**
	 * Function to cut out all the text between multiple instances of a token in a larger string and return the results a Vector of Strings
	 */
	public static Vector FindAllBetween(String inputString,String beginAndEndToken){
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
		String tempString= new String("");

		try{
			while ( newString.indexOf(beginAndEndToken) != -1 ){//&& (newString != null)){

				//first round
				if (endnum == -1){
					endnum = inputString.indexOf(beginAndEndToken);
					newString = inputString.substring(endnum + beginAndEndToken.length());
				}
				//second round
				//else if (beginnum == -1){
				else{

					beginnum=endnum;
					endnum = inputString.indexOf(beginAndEndToken, beginnum + beginAndEndToken.length());

					newSubstring = inputString.substring(beginnum + beginAndEndToken.length(),endnum);
					//removeCharacters(newSubstring);
					/*if (newSubstring.length() == 0) {
						newSubstring = " ";
					}*/
					//cuts down the string from where occurence last found
					newString = inputString.substring(endnum + beginAndEndToken.length());

					outVector.addElement(removeCharacters(newSubstring));

				}
				//other rounds
				/*else{
					beginnum=endnum;
					endnum = newString.indexOf(beginAndEndToken);

					newSubstring = newString.substring(beginnum,endnum);

						//cuts down the string from where occurence last fount
					newString = newString.substring(endnum + beginAndEndToken.length());

					outVector.addElement(newSubstring);
				}*/

				counter++;
			}

		}
		catch (Exception e){
			outVector.addElement("TextSoapError" + counter);
		}

		outVector.trimToSize();
		//System.out.print(outVector.size());
		return outVector;
	}


	/**
	 * Function to cut out all the text between multiple instances of a token in a larger string
	 */
	public static Vector FindAllWithSeparator(String inputString,String separatorString){

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
		String tempString= new String("");

		try{
			while ( newString.indexOf(separatorString) != -1 ){//&& (newString != null)){

				//first round
				if (endnum == -1){
					endnum = inputString.indexOf(separatorString);
					newString = inputString.substring(endnum + separatorString.length());

					newSubstring = inputString.substring(0,endnum);
					//removeCharacters(newSubstring);
					outVector.addElement(removeCharacters(newSubstring));

				}
				//second round
				//else if (beginnum == -1){
				else{

					beginnum=endnum;
					endnum = inputString.indexOf(separatorString, beginnum + separatorString.length());



					//cuts down the string from where occurence last found

					newSubstring = inputString.substring(beginnum + separatorString.length(),endnum);
					//removeCharacters(newSubstring);
					outVector.addElement(removeCharacters(newSubstring));


					newString = inputString.substring(endnum + separatorString.length());
				}
				//other rounds
				/*else{
					beginnum=endnum;
					endnum = newString.indexOf(beginAndEndToken);

					newSubstring = newString.substring(beginnum,endnum);

						//cuts down the string from where occurence last fount
					newString = newString.substring(endnum + beginAndEndToken.length());

					outVector.addElement(newSubstring);
				}*/

				counter++;
			}


			newSubstring = inputString.substring(endnum + separatorString.length());
			//removeCharacters(newSubstring);
			outVector.addElement(removeCharacters(newSubstring));


		}
		catch (Exception e){
			outVector.addElement("TextSoapError" + counter);
		}

		outVector.trimToSize();
		//System.out.print(outVector.size());
		return outVector;
	}


	/**
	 * Removes unnecessary characters such as \n \t \r and " " from the begining and end of a string
	 */
	public static String removeCharacters(String inString){
		boolean check = true;
		while (check && inString.length() >= 1){
			if (inString.substring(0,1).equals("\n")){
				inString=inString.substring(1,inString.length());
			}
			else if (inString.substring(0,1).equals("\r")){
				inString=inString.substring(1,inString.length());
			}
			else if (inString.substring(0,1).equals("\t")){
				inString=inString.substring(1,inString.length());
			}
			else if (inString.substring(0,1).equals(" ")){
				inString=inString.substring(1,inString.length());
			}
			else if (inString.substring(inString.length()-1,inString.length()).equals("\n")){
				inString=inString.substring(0,inString.length()-1);
			}
			else if (inString.substring(inString.length()-1,inString.length()).equals("\r")){
				inString=inString.substring(0,inString.length()-1);
			}
			else if (inString.substring(inString.length()-1,inString.length()).equals("\t")){
				inString=inString.substring(0,inString.length()-1);
			}
			else if (inString.substring(inString.length()-1,inString.length()).equals(" ")){
				inString=inString.substring(0,inString.length()-1);
			}
			else{
				check=false;
			}
		}
		return inString;
	}

	public static String cleanText(String textString) {

		textString = findAndReplace(textString,'\'','´');
		textString = findAndReplace(textString,"&oslash;","ø");
		textString = findAndReplace(textString,"&Oslash;","Ø");
		textString = findAndReplace(textString,"&aring;","å");
		textString = findAndReplace(textString,"&quot;","\"");
		textString = findAndReplace(textString,"  "," ");

		return textString;
	}

	public static String findAndReplace(String text, char charToFind, char charReplace){
	    return text.replace(charToFind,charReplace);
	}

	public static String findAndReplace(String text, String stringToFind, String stringReplace){

	    while ( text.indexOf(stringToFind) != -1 ) {

			String text2 = text.substring(0,text.indexOf(stringToFind));
			String text3 = text.substring(text.indexOf(stringToFind)+stringToFind.length(),text.length());

			text = text2 + stringReplace + text3;

		}

	    return text;
	}

	public static String findAndCut(String text, String stringToFind){

	    while ( text.indexOf(stringToFind) != -1 ) {

			String text2 = text.substring(0,text.indexOf(stringToFind));
			String text3 = text.substring(text.indexOf(stringToFind)+stringToFind.length(),text.length());

			text = text2 + text3;

		}

	    return text;
	}


  public static String addZero( int numberToFix){
  	String FixedNumber;
	if (numberToFix <10 )
		FixedNumber = "0";
	else
		FixedNumber = "";

	return FixedNumber + numberToFix;
  }



  public static String formatString(String text){
    text = findAndRemoveHtmlTags(text);
    text = findAndReplace(text,"\n", "<br>");
//    text = formatText(text);
    return text;
  }


  public static String findAndRemoveHtmlTags(String text){

    if (text != null){
      int firstpos = 0;
      int nextpos = 0;

      while (firstpos > -1 || nextpos > -1){
        firstpos = text.indexOf("<", firstpos);
        nextpos = text.indexOf(">",firstpos);
        if(nextpos - firstpos > 4){
          String toCut = text.substring(firstpos,nextpos);
          text = findAndCut(text, toCut);
        }
        if(firstpos > 0 && nextpos > 0){
          firstpos -= 1;
          nextpos -= 1;
        }
      }

    }else{
      // text = "";
    }
    return text;
  }




	public static String formatText(String text_body) {

		//Búa til töflu
		if (text_body==null || text_body.equals("")){
                  text_body = "";//"No text in database...";
                }
/*		Vector tableVector = createTextTable(text_body);

		for ( int a = 0; a < tableVector.size(); a++ ) {

			String tableRow = tableVector.elementAt(a).toString();

			if ( a == 0 ) {
				tableRow = TextSoap.findAndReplace(tableRow,"|","</font></td><td valign=\"top\"><font size=\""+(tableTextSize+1)+"\">");
			}

			else {
				tableRow = TextSoap.findAndReplace(tableRow,"|","</font></td><td valign=\"top\"><font size=\""+tableTextSize+"\">");
			}

			if ( a == 0 || a == tableVector.size()-1) {
				if ( a == 0 ) {
					tableRow = "<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\""+tableWidth+"\"><tr bgcolor=\"#FFFFFF\"><td valign=\"top\"><font size=\""+(tableTextSize+1)+"\">"+tableRow+"</font></td></tr>";
				}

				if ( a == tableVector.size()-1 ) {
				tableRow = "<tr bgcolor=\"#FFFFFF\"><td valign=\"top\"><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr></table>";
				}
			}
			else {
				tableRow = "<tr bgcolor=\"#FFFFFF\"><td valign=\"top\"><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr>";
			}

			text_body = TextSoap.findAndReplace(text_body,tableVector.elementAt(a).toString(),tableRow);
		}

		text_body = TextSoap.findAndReplace(text_body,"|\r\n","");
		text_body = TextSoap.findAndReplace(text_body,"|","");
		//Töflugerð lokið


		//Búa til töflu 2
		if (text_body==null || text_body.equals("")) text_body = "No text in database...";
		tableVector = createTextTableNoBanner(text_body);

		for ( int a = 0; a < tableVector.size(); a++ ) {

			String tableRow = tableVector.elementAt(a).toString();

			if ( a == 0 ) {
				tableRow = TextSoap.findAndReplace(tableRow,"?","</font></td><td><font size=\""+(tableTextSize+1)+"\">");
			}

			else {
				tableRow = TextSoap.findAndReplace(tableRow,"?","</font></td><td><font size=\""+tableTextSize+"\">");
			}

			if ( a == 0 || a == tableVector.size()-1) {
				if ( a == 0 ) {
					tableRow = "<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\""+tableWidth+"\"><tr bgcolor=\"#FFFFFF\"><td><font size=\""+(tableTextSize+1)+"\">"+tableRow+"</font></td></tr>";
				}

				if ( a == tableVector.size()-1 ) {
				tableRow = "<tr bgcolor=\"#FFFFFF\"><td><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr></table>";
				}
			}
			else {
				tableRow = "<tr bgcolor=\"#FFFFFF\"><td><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr>";
			}

			text_body = TextSoap.findAndReplace(text_body,tableVector.elementAt(a).toString(),tableRow);
		}

		text_body = TextSoap.findAndReplace(text_body,"?\r\n","");
		text_body = TextSoap.findAndReplace(text_body,"?","");
		//Töflugerð lokið


		//Búa til tengla
		Vector linkVector = createTextLink(text_body);

		for ( int a = 0; a < linkVector.size(); a++ ) {
			String link = linkVector.elementAt(a).toString();
				int comma = link.indexOf(",");

			link = "<a href=\""+link.substring(comma+1,link.length())+"\">"+link.substring(0,comma)+"</a>";

			text_body = TextSoap.findAndReplace(text_body,"Link("+linkVector.elementAt(a).toString()+")",link);
		}
*/
		//Almenn hreinsun
		text_body = findAndReplace(text_body,"*","<li>");
		text_body = findAndReplace(text_body,".\r\n",".<br><br>");
		text_body = findAndReplace(text_body,"?\r\n","?<br><br>");
		text_body = findAndReplace(text_body,"!\r\n","!<br><br>");
		text_body = findAndReplace(text_body,")\r\n",")<br><br>");

		//Búa til headline
		/*Vector testVector = testText(text_body);

		while ( text_body.indexOf("\r\n\r\n\r\n") != -1 ) {
			text_body = TextSoap.findAndReplace(text_body,"\r\n\r\n\r\n","\r\n\r\n");
		}

		int head_size = textSize + 1;

		for ( int a = 0; a < testVector.size(); a++ ) {
			text_body = TextSoap.findAndReplace(text_body,"\r\n\r\n"+testVector.elementAt(a).toString(),"temp");
			text_body = TextSoap.findAndReplace(text_body,"temp","<font size=\""+head_size+"\"><b>"+testVector.elementAt(a).toString()+"</b></font>");
		}*/
		//Headlinegerð búin

		text_body = findAndReplace(text_body,"\r\n","<br>");
		text_body = findAndReplace(text_body,"\t","&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

		return text_body;
	}



	public static Vector createTextLink(String text_body) {

		Vector linkVector = TextSoap.FindAllBetween(text_body,"Link(",")");

		return linkVector;
	}




  public  static String removeWhiteSpace( String string )throws Exception{
    string = findAndReplace(string," ","");
    return string;
 }


 public static boolean numericString( String myString ){

	boolean isTrue = true;

	for (int i = 0; i < myString.length(); i++){
		if (!(myString.charAt(i) == '0' || myString.charAt(i) == '1' || myString.charAt(i) == '2' || myString.charAt(i) == '3' || myString.charAt(i) == '4' || myString.charAt(i) == '5' || myString.charAt(i) == '6' || myString.charAt(i) == '7' || myString.charAt(i) == '8' || myString.charAt(i) == '9'))
			isTrue = false;}

	return isTrue;
}

public static boolean nonNumericString( String myString ){

	boolean isTrue = true;

	for (int i = 0; i < myString.length(); i++){
		if ((myString.charAt(i) == '0' || myString.charAt(i) == '1' || myString.charAt(i) == '2' || myString.charAt(i) == '3' || myString.charAt(i) == '4' || myString.charAt(i) == '5' || myString.charAt(i) == '6' || myString.charAt(i) == '7' || myString.charAt(i) == '8' || myString.charAt(i) == '9'))
			isTrue = false;}

	return isTrue;
}

public static String singleDecimalFormat(String stringToDecimalFormat){
  symbols.setDecimalSeparator('.');
  singleDecimalFormat.setDecimalFormatSymbols(symbols);
  double doubleToDecimalFormat = Double.parseDouble(findAndReplace(stringToDecimalFormat,',','.'));
  return singleDecimalFormat.format(doubleToDecimalFormat);
}

public static String singleDecimalFormat(double doubleToDecimalFormat){
  symbols.setDecimalSeparator('.');
  singleDecimalFormat.setDecimalFormatSymbols(symbols);
  return singleDecimalFormat.format(doubleToDecimalFormat);
}

public static String decimalFormat(double doubleToDecimalFormat, int numberOfDecimals){
  StringBuffer decimalString = new StringBuffer("0.0");//always at least one decimal
  for (int i = 1; i < numberOfDecimals; i++) {
    decimalString.append("0");
  }
  DecimalFormat decimalFormat = new DecimalFormat(decimalString.toString());

  symbols.setDecimalSeparator('.');
  decimalFormat.setDecimalFormatSymbols(symbols);
  return decimalFormat.format(doubleToDecimalFormat);
}

public static String decimalFormat(String stringToDecimalFormat, int numberOfDecimals){
  StringBuffer decimalString = new StringBuffer("0.0");//always at least one decimal
  for (int i = 1; i < numberOfDecimals; i++) {
    decimalString.append("0");
  }
  DecimalFormat decimalFormat = new DecimalFormat(decimalString.toString());

  symbols.setDecimalSeparator('.');
  decimalFormat.setDecimalFormatSymbols(symbols);
  double doubleToDecimalFormat = Double.parseDouble(findAndReplace(stringToDecimalFormat,',','.'));
  return decimalFormat.format(doubleToDecimalFormat);
}

} // class TestSoap
