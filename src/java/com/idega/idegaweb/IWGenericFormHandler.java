package com.idega.idegaweb;

import com.idega.presentation.IWContext;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.text.DateFormat;
import com.idega.builder.handler.JavaTypesHandler;

/**
 * Title:        idegaWeb Generic Form Handler
 * Description:  A class to parse forms and get output in a structured format
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWGenericFormHandler implements java.lang.Cloneable{

  private Map parameterDescriptions;
  private Map parameterTypes;


  private static final String COLON = ":";
  private static final String SPACE = " ";
  private static final String TAB = "\t";
  private static final String NEWLINE = "\n";

  public static final String STRING_TYPE = JavaTypesHandler.STRING_TYPE;
  public static final String INTEGER_TYPE =  JavaTypesHandler.INTEGER_TYPE;
  public static final String BOOLEAN_TYPE = JavaTypesHandler.BOOLEAN_TYPE;
  public static final String FLOAT_TYPE = JavaTypesHandler.FLOAT_TYPE;
  public static final String DOUBLE_TYPE = JavaTypesHandler.DOUBLE_TYPE;
  public static final String DATE_TYPE = JavaTypesHandler.DATE_TYPE;
  public static final String TIMESTAMP_TYPE = JavaTypesHandler.TIMESTAMP_TYPE;
  public static final String TIME_TYPE = JavaTypesHandler.TIME_TYPE;


  public IWGenericFormHandler() {
    parameterDescriptions=new HashMap();
    parameterTypes = new HashMap();
  }

  public void addProcessedParameter(String parameterName,String parameterDescription,String parameterType){
    getDescMap().put(parameterName,parameterDescription);
    getTypesMap().put(parameterName,parameterType);
  }

  /*public void addProcessedParameter(String parameterName,String parameterDescription,String parameterType){
    addProcessedParameter(parameterName,parameterDescription,STRING_TYPE);
  }*/


  private Map getDescMap(){
    return parameterDescriptions;
  }

  private Map getTypesMap(){
    return parameterTypes;
  }


  /**
   * Formats the output of the form with descriptions in front of values and newlines between values
   */
  public String processPlainTextFormatted(IWContext iwc){
    StringBuffer buffer = new StringBuffer();

    Iterator iter = getTypesMap().keySet().iterator();
    while (iter.hasNext()) {
      String paramName = (String)iter.next();
      String[] paramValues = iwc.getParameterValues(paramName);
      if(paramValues!=null){
        String desc = getDescriptionForParameter(paramName);
        buffer.append(desc);
        buffer.append(COLON);
        buffer.append(TAB);
        buffer.append(TAB);

        if(paramValues.length==1){
          String currentDiplayedVal = getProcessedValueForDisplay(iwc,paramName,paramValues[0]);
          buffer.append(currentDiplayedVal);
          buffer.append(NEWLINE);
        }
        else{
          for (int i = 0; i < paramValues.length; i++) {
              String currentDiplayedVal = getProcessedValueForDisplay(iwc,paramName,paramValues[i]);
              if(i!=0){
                buffer.append(TAB);
                buffer.append(TAB);
                buffer.append(TAB);
              }
              buffer.append(currentDiplayedVal);
              buffer.append(NEWLINE);
          }

        }
      }
    }

    return buffer.toString();
  }

  private String getDescriptionForParameter(String parameterName){
    return (String)getDescMap().get(parameterName);
  }

  private String getTypeForParameter(String parameterName){
    return (String)getTypesMap().get(parameterName);
  }


  private String getProcessedValueForDisplay(IWContext iwc,String parameterName,String parameterValue){
    String parameterType = getTypeForParameter(parameterName);
    if(parameterType.equals(STRING_TYPE)){
      return parameterValue;
    }
    else if(parameterType.equals(STRING_TYPE)){
      return parameterValue;
    }
    else if(parameterType.equals(BOOLEAN_TYPE)){
      if(parameterValue.equalsIgnoreCase("Y") || parameterValue.equalsIgnoreCase("T") ){
        /**
         * @todo: Localize
         */
        return "True";
      }
      else if(parameterValue.equalsIgnoreCase("N") || parameterValue.equalsIgnoreCase("F") ){
        return "False";
      }
      return parameterValue;
    }
    else if(parameterType.equals(INTEGER_TYPE)){
      return parameterValue;
    }
    else if(parameterType.equals(DOUBLE_TYPE)){
      return parameterValue;
    }
    else if(parameterType.equals(FLOAT_TYPE)){
      return parameterValue;
    }
    else if(parameterType.equals(DATE_TYPE)){
      DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT,iwc.getCurrentLocale());
      com.idega.util.idegaTimestamp its = new com.idega.util.idegaTimestamp(parameterValue);
      java.util.Date date = its.getSQLDate();
      return format.format(date);
    }
    else if(parameterType.equals(TIMESTAMP_TYPE)){
      DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT,iwc.getCurrentLocale());
      com.idega.util.idegaTimestamp its = new com.idega.util.idegaTimestamp(parameterValue);
      java.util.Date date = its.getSQLDate();
      return format.format(date);
    }
    else if(parameterType.equals(TIME_TYPE)){
      return parameterValue;
    }

    return parameterValue;
  }


  public Object clone(){
    try{
      IWGenericFormHandler newHandler = (IWGenericFormHandler)super.clone();

      if(this.parameterDescriptions!=null){
        newHandler.parameterDescriptions = (Map)((HashMap)this.parameterDescriptions).clone();
      }
      if(this.parameterTypes!=null){
        newHandler.parameterTypes = (Map)((HashMap)this.parameterTypes).clone();
      }
      return newHandler;
    }
    catch(java.lang.CloneNotSupportedException e){
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }

}