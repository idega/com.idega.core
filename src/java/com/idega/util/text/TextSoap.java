/*
 *  Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.util.text;
//import idega.*;
//import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
//@todo use regular expressions such as import com.stevesoft.pat.*;

/**
 *  General class for text manipulation
 *
 *@author     <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a> , Eirikur
 *      Hrafnsson
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
  public static Vector FindAllBetween(String inputString,
      String begintoken, String endtoken) {
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
      while ((newString.indexOf(begintoken) != -1)
           && (newString.indexOf(endtoken) != -1)) {
        //&& (newString != null)){
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

    } catch (Exception e) {
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
  public static Vector FindAllBetween(String inputString,
      String beginAndEndToken) {
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
        //&& (newString != null)){

        //first round
        if (endnum == -1) {
          endnum = inputString.indexOf(beginAndEndToken);
          newString = inputString.substring(endnum + beginAndEndToken.length());
        }
        //second round
        //else if (beginnum == -1){
        else {

          beginnum = endnum;
          endnum = inputString.indexOf(beginAndEndToken,
              beginnum + beginAndEndToken.length());

          newSubstring = inputString.substring(beginnum + beginAndEndToken.length(), endnum);
          //removeCharacters(newSubstring);
          /*
           *  if (newSubstring.length() == 0) {
           *  newSubstring = " ";
           *  }
           */
          //cuts down the string from where occurence last found
          newString = inputString.substring(endnum + beginAndEndToken.length());

          outVector.addElement(removeCharacters(newSubstring));

        }
        //other rounds
        /*
         *  else{
         *  beginnum=endnum;
         *  endnum = newString.indexOf(beginAndEndToken);
         *  newSubstring = newString.substring(beginnum,endnum);
         *  /cuts down the string from where occurence last fount
         *  newString = newString.substring(endnum + beginAndEndToken.length());
         *  outVector.addElement(newSubstring);
         *  }
         */
        counter++;
      }

    } catch (Exception e) {
      outVector.addElement("TextSoapError" + counter);
    }

    outVector.trimToSize();
    //System.out.print(outVector.size());
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
  public static Vector FindAllWithSeparator(String inputString,
      String separatorString) {

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
        //&& (newString != null)){

        //first round
        if (endnum == -1) {
          endnum = inputString.indexOf(separatorString);
          newString = inputString.substring(endnum + separatorString.length());

          newSubstring = inputString.substring(0, endnum);
          //removeCharacters(newSubstring);
          outVector.addElement(removeCharacters(newSubstring));

        }
        //second round
        //else if (beginnum == -1){
        else {

          beginnum = endnum;
          endnum = inputString.indexOf(separatorString,
              beginnum + separatorString.length());


          //cuts down the string from where occurence last found

          newSubstring = inputString.substring(beginnum + separatorString.length(), endnum);
          //removeCharacters(newSubstring);
          outVector.addElement(removeCharacters(newSubstring));

          newString = inputString.substring(endnum + separatorString.length());
        }
        //other rounds
        /*
         *  else{
         *  beginnum=endnum;
         *  endnum = newString.indexOf(beginAndEndToken);
         *  newSubstring = newString.substring(beginnum,endnum);
         *  /cuts down the string from where occurence last fount
         *  newString = newString.substring(endnum + beginAndEndToken.length());
         *  outVector.addElement(newSubstring);
         *  }
         */
        counter++;
      }

      newSubstring = inputString.substring(endnum + separatorString.length());
      //removeCharacters(newSubstring);
      outVector.addElement(removeCharacters(newSubstring));

    } catch (Exception e) {
      outVector.addElement("TextSoapError" + counter);
    }

    outVector.trimToSize();
    //System.out.print(outVector.size());
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
      } else if (inString.substring(0, 1).equals("\r")) {
        inString = inString.substring(1, inString.length());
      } else if (inString.substring(0, 1).equals("\t")) {
        inString = inString.substring(1, inString.length());
      } else if (inString.substring(0, 1).equals(" ")) {
        inString = inString.substring(1, inString.length());
      } else if (inString.substring(inString.length() - 1,
          inString.length()).equals("\n")) {
        inString = inString.substring(0, inString.length() - 1);
      } else if (inString.substring(inString.length() - 1,
          inString.length()).equals("\r")) {
        inString = inString.substring(0, inString.length() - 1);
      } else if (inString.substring(inString.length() - 1,
          inString.length()).equals("\t")) {
        inString = inString.substring(0, inString.length() - 1);
      } else if (inString.substring(inString.length() - 1,
          inString.length()).equals(" ")) {
        inString = inString.substring(0, inString.length() - 1);
      } else {
        check = false;
      }
    }
    return inString;
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
  public static String findAndReplace(String text,
      char charToFind, char charReplace) {
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
    //return r.replaceAll(text);
    StringBuffer buf = new StringBuffer("");
    String returnString;
    int index = text.indexOf(stringToFind);
    int index2 = 0;
    int length1 = stringToReplace.length();
    int length2 = stringToFind.length();
    int length3 = length1 + length2;

    while (index != -1) {
      buf.append(text.substring(index2, index));
      buf.append(stringToReplace);
      index2 = index;
      index = text.indexOf(stringToFind, index2 + length3);
      if (index != -1) {
        buf.append(text.substring(index2 + length1, index));
        //paste from last index
      } else {
        buf.append(text.substring(index2 + length1, text.length()));
      }
    }

    returnString = buf.toString();

    if(returnString.equals("")){
      returnString = text;
    }

    return returnString;
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
  public static String findAndInsertAfter(String text,
      String stringToFind,
      String stringInsert) {
    int index = text.indexOf(stringToFind);
    while (index != -1) {
      StringBuffer buf = new StringBuffer();
      buf.append(text.substring(0, index + stringToFind.length()));
      buf.append(stringInsert);
      buf.append(text.substring(index + stringToFind.length(), text.length()));
      text = buf.toString();
      index = text.indexOf(stringToFind, index);
    }
    return text;
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
  public static String findAndInsertBefore(String text,String stringToFind,String stringInsert) {
    StringBuffer buf = new StringBuffer();
    String returnString;
    int index = text.indexOf(stringToFind);
    int index2 = 0;
    int toInsertlength = stringInsert.length();
    int toFindLength = stringToFind.length();
    int length = toInsertlength + toFindLength;

    while (index != -1) {
      buf.append(text.substring(index2, index));
      buf.append(stringInsert);
      index2 = index;
      index = text.indexOf(stringToFind, index2 + length);
      if (index != -1) {
        buf.append(text.substring(index2 + toFindLength, index));
        //paste from last index
      } else {
        buf.append(text.substring(index2 + toFindLength, text.length()));
      }
    }
    returnString = buf.toString();

    if(returnString.equals("")){
      returnString = text;
    }

    return returnString;
  }


  /**
   *  Description of the Method
   *
   *@param  text          Description of the Parameter
   *@param  stringToFind  Description of the Parameter
   *@return               Description of the Return Value
   */
  public static String findAndCut(String text, String stringToFind) {
    return findAndReplace(text,stringToFind,"");
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
    } else {
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
    //    text = formatText(text);
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

    } else {
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

    //B?a til t?flu
    if (text_body == null || text_body.equals("")) {
      text_body = "";
      //"No text in database...";
    }
    /*
     *  Vector tableVector = createTextTable(text_body);
     *  for ( int a = 0; a < tableVector.size(); a++ ) {
     *  String tableRow = tableVector.elementAt(a).toString();
     *  if ( a == 0 ) {
     *  tableRow = TextSoap.findAndReplace(tableRow,"|","</font></td><td valign=\"top\"><font size=\""+(tableTextSize+1)+"\">");
     *  }
     *  else {
     *  tableRow = TextSoap.findAndReplace(tableRow,"|","</font></td><td valign=\"top\"><font size=\""+tableTextSize+"\">");
     *  }
     *  if ( a == 0 || a == tableVector.size()-1) {
     *  if ( a == 0 ) {
     *  tableRow = "<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\""+tableWidth+"\"><tr bgcolor=\"#FFFFFF\"><td valign=\"top\"><font size=\""+(tableTextSize+1)+"\">"+tableRow+"</font></td></tr>";
     *  }
     *  if ( a == tableVector.size()-1 ) {
     *  tableRow = "<tr bgcolor=\"#FFFFFF\"><td valign=\"top\"><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr></table>";
     *  }
     *  }
     *  else {
     *  tableRow = "<tr bgcolor=\"#FFFFFF\"><td valign=\"top\"><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr>";
     *  }
     *  text_body = TextSoap.findAndReplace(text_body,tableVector.elementAt(a).toString(),tableRow);
     *  }
     *  text_body = TextSoap.findAndReplace(text_body,"|\r\n","");
     *  text_body = TextSoap.findAndReplace(text_body,"|","");
     *  /T?fluger? loki?
     *  /B?a til t?flu 2
     *  if (text_body==null || text_body.equals("")) text_body = "No text in database...";
     *  tableVector = createTextTableNoBanner(text_body);
     *  for ( int a = 0; a < tableVector.size(); a++ ) {
     *  String tableRow = tableVector.elementAt(a).toString();
     *  if ( a == 0 ) {
     *  tableRow = TextSoap.findAndReplace(tableRow,"?","</font></td><td><font size=\""+(tableTextSize+1)+"\">");
     *  }
     *  else {
     *  tableRow = TextSoap.findAndReplace(tableRow,"?","</font></td><td><font size=\""+tableTextSize+"\">");
     *  }
     *  if ( a == 0 || a == tableVector.size()-1) {
     *  if ( a == 0 ) {
     *  tableRow = "<table border=\"0\" cellpadding=\"3\" cellspacing=\"0\" width=\""+tableWidth+"\"><tr bgcolor=\"#FFFFFF\"><td><font size=\""+(tableTextSize+1)+"\">"+tableRow+"</font></td></tr>";
     *  }
     *  if ( a == tableVector.size()-1 ) {
     *  tableRow = "<tr bgcolor=\"#FFFFFF\"><td><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr></table>";
     *  }
     *  }
     *  else {
     *  tableRow = "<tr bgcolor=\"#FFFFFF\"><td><font size=\""+tableTextSize+"\">"+tableRow+"</font></td></tr>";
     *  }
     *  text_body = TextSoap.findAndReplace(text_body,tableVector.elementAt(a).toString(),tableRow);
     *  }
     *  text_body = TextSoap.findAndReplace(text_body,"?\r\n","");
     *  text_body = TextSoap.findAndReplace(text_body,"?","");
     *  /T?fluger? loki?
     *  /B?a til tengla
     *  Vector linkVector = createTextLink(text_body);
     *  for ( int a = 0; a < linkVector.size(); a++ ) {
     *  String link = linkVector.elementAt(a).toString();
     *  int comma = link.indexOf(",");
     *  link = "<a href=\""+link.substring(comma+1,link.length())+"\">"+link.substring(0,comma)+"</a>";
     *  text_body = TextSoap.findAndReplace(text_body,"Link("+linkVector.elementAt(a).toString()+")",link);
     *  }
     */
    //Almenn hreinsun
    text_body = findAndReplace(text_body, "*", "<li>");
    text_body = findAndReplace(text_body, ".\r\n", ".<br><br>");
    text_body = findAndReplace(text_body, "?\r\n", "?<br><br>");
    text_body = findAndReplace(text_body, "!\r\n", "!<br><br>");
    text_body = findAndReplace(text_body, ")\r\n", ")<br><br>");

    //B?a til headline
    /*
     *  Vector testVector = testText(text_body);
     *  while ( text_body.indexOf("\r\n\r\n\r\n") != -1 ) {
     *  text_body = TextSoap.findAndReplace(text_body,"\r\n\r\n\r\n","\r\n\r\n");
     *  }
     *  int head_size = textSize + 1;
     *  for ( int a = 0; a < testVector.size(); a++ ) {
     *  text_body = TextSoap.findAndReplace(text_body,"\r\n\r\n"+testVector.elementAt(a).toString(),"temp");
     *  text_body = TextSoap.findAndReplace(text_body,"temp","<font size=\""+head_size+"\"><b>"+testVector.elementAt(a).toString()+"</b></font>");
     *  }
     */
    //Headlineger? b?in

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
      if (!(myString.charAt(i) == '0'
           || myString.charAt(i) == '1'
           || myString.charAt(i) == '2'
           || myString.charAt(i) == '3'
           || myString.charAt(i) == '4'
           || myString.charAt(i) == '5'
           || myString.charAt(i) == '6'
           || myString.charAt(i) == '7'
           || myString.charAt(i) == '8' || myString.charAt(i) == '9')) {
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

    for (int i = 0; i < myString.length(); i++) {
      if ((myString.charAt(i) == '0'
           || myString.charAt(i) == '1'
           || myString.charAt(i) == '2'
           || myString.charAt(i) == '3'
           || myString.charAt(i) == '4'
           || myString.charAt(i) == '5'
           || myString.charAt(i) == '6'
           || myString.charAt(i) == '7'
           || myString.charAt(i) == '8' || myString.charAt(i) == '9')) {
        isTrue = false;
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
  public static String decimalFormat(double doubleToDecimalFormat,
      int numberOfDecimals) {
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
  public static String decimalFormat(String stringToDecimalFormat,
      int numberOfDecimals) {
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

}
// class TestSoap
