package com.idega.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

    public static Iterator getSeparatorIterator(final String stringToCutDown, final String separator){
      return new Iterator(){
        private String theString = stringToCutDown;
        private String theSeparator = separator;
        private boolean hasSeparators=false;
        private boolean hasNext=true;

        public Object next()throws NoSuchElementException{
            String theReturn=null;
            try{
              if(hasNext){
                if(hasSeparators){
                  if(theString.length()>0){
                    if(!theString.equals(theSeparator)){
                      theReturn = theString.substring(0,theString.indexOf(separator)+1);
                      theString = theString.substring(theString.indexOf(separator)+1,theString.length());
                    }
                  }
                  else{
                    throw new NoSuchElementException();
                  }
                }
                else{
                  if(theString.length() > 0){
                    return theString;
                  }
                  else{
                    throw new NoSuchElementException();
                  }
                }
              }
              else{
                throw new NoSuchElementException();
              }
            }
            catch(Exception e){
              if(e instanceof NoSuchElementException){
                throw (NoSuchElementException)e.fillInStackTrace();
              }
              else{
                throw new NoSuchElementException();
              }
            }

            return theReturn;
        }

        public boolean hasNext(){
          if(theString !=null){
            if(theString.length() > 0){
              int index = theString.indexOf(theSeparator);
              while(index==0){
                theString = theString.substring(0,theSeparator.length());
                index=theString.indexOf(theSeparator);
              }
              if(index==-1){
                hasSeparators=false;
              }
              else{
                hasSeparators=true;
              }
            }
            else{
              hasNext=false;
            }
          }
          else{
            hasNext=false;
          }

          return hasNext;
        }

        public void remove(){
        /**
         * Does Nothing
         */
        }

      };
    }


} // Class StringHandler