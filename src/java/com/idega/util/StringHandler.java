package com.idega.util;

import java.lang.*;
import java.lang.StringBuffer;

/**
 * Title:        StringHandler
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega margmiðlun hf.
 * @author
 * @version 1.0
 */

public class StringHandler {

  public static String alfabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  public StringHandler() {
  }

  /**
   * Returns an random String with the desired length with the Roman alphabet (upper and lower case) and numbers 0-9
   */
  public static String getRandomString(int length){
    StringBuffer buffer = new StringBuffer();
    for (int i=0; i<length; i++) {
      buffer.append(alfabet.charAt((int) (alfabet.length()*Math.random())));
    }
    return buffer.toString();
  }

  /**
   * Concatenates two strings to after alphabetical comparison
   */
   public static String concatAlphabetically(String string1,String string2){
      String first;
      String second;
      int compare = string1.compareTo(string2);
      if(compare<0){
         first=string1;
         second=string2;
      }
      else {
         second=string1;
         first=string2;
      }
      return first+second;
    }


} // Class StringHandler