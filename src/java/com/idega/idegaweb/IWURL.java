package com.idega.idegaweb;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.ui.Parameter;

public class IWURL {

  private static final String questionMark = "?";
  private static final String equalsMark = "=";
  private static final String ampersand = "&";
  private String baseURL;
  private static List globalMaintainedParameters;
  private static List globalMaintainedBuilderParameters;

  static{
    globalMaintainedParameters = new Vector();
    globalMaintainedParameters.add(Page.IW_FRAME_STORAGE_PARMETER);
    globalMaintainedParameters.add(Page.IW_FRAME_CLASS_PARAMETER);
    globalMaintainedParameters.add(IWMainApplication.classToInstanciateParameter);
    globalMaintainedParameters.add(IWConstants.PARAM_NAME_OUTPUT_MARKUP_LANGUAGE);

    globalMaintainedBuilderParameters = new Vector();
    globalMaintainedBuilderParameters.add(ICBuilderConstants.IB_PAGE_PARAMETER);
  }

  private Map parametersMap;

  public IWURL(String baseURL){
    setBaseURL(baseURL);
  }

  public void setBaseURL(String baseURL){
    int index = baseURL.indexOf(questionMark);
    if(index!=-1){
      String realBase = baseURL.substring(0,index);
      this.baseURL=realBase;
      String theRest = baseURL.substring(index+1);
      breakUp(theRest);
    }
    else{
      this.baseURL=baseURL;
    }
  }

  private void breakUp(String parametersAndValues){
    int ampersandIndex = parametersAndValues.indexOf(ampersand);
    if(ampersandIndex!=-1){
      StringTokenizer token = new StringTokenizer(ampersand);
      while(token.hasMoreTokens()){
        String parameter = token.nextToken();
        int equalsIndex = parameter.indexOf(equalsMark);
        if(equalsIndex!=-1){
          String name = parameter.substring(0,equalsIndex);
          String value = parameter.substring(equalsIndex+1);
          addParameter(name,value);
        }
      }
    }
    else{
        String parameter = parametersAndValues;
        int equalsIndex = parameter.indexOf(equalsMark);
        if(equalsIndex!=-1){
          String name = parameter.substring(0,equalsIndex);
          String value = parameter.substring(equalsIndex+1);
          addParameter(name,value);
        }
    }
  }


  public static IWURL getURL(String baseURL){
    return new IWURL(baseURL);
  }

  public void addPageClassParameter(Class pageClass){
    addParameter(Page.IW_FRAME_CLASS_PARAMETER,IWMainApplication.getEncryptedClassName(pageClass));
  }

  public void addParameter(String parameterName,String parameterValue){
    getParametersMap().put(parameterName,parameterValue);
  }


  public void addParameter(Parameter parameter){
    String name = parameter.getName();
    String value = parameter.getValueAsString();
    addParameter(name,value);
  }

  public void maintainParameter(String parameterName, IWContext iwc){
    String value = iwc.getParameter(parameterName);
    if(value!=null){
      addParameter(parameterName,value);
    }
  }

  private Map getParametersMap(){
    if(parametersMap == null){
      parametersMap = new Hashtable();
    }
    return parametersMap;
  }

  public static IWURL getImageURL(int imageID){
    return null;
  }

  /*public static IWURL getWindowOpenerURL(Class windowClass){
    IWURL url = getURL(IWMainApplication.windowOpenerURL);
    url.addParameter("","");
    return url;
  }*/

  /*public static IWURL getBuilderURL(int pageID){
    return getBuilderURL(Integer.toString(pageID));
  }*/

  /*public static IWURL getBuilderURL(String pageKey){
    IWURL url = getURL(IWMainApplication.BUILDER_SERVLET_URL);
    url.addParameter("","");
    return url;
  }*/

  /**
   * Returns the BaseURL + all parameters
   */
  public String getFullURL(){
    if(hasParameters()){
      String theReturn = baseURL+questionMark;
      Map map = getParametersMap();
      Set keySet = map.keySet();
      Iterator iter = keySet.iterator();
      while (iter.hasNext()) {
        String key = (String)iter.next();
        String value = (String)map.get(key);
        theReturn += key+equalsMark+value+ampersand;
      }
      return theReturn;
    }
    else{
      return baseURL;
    }
  }

  public boolean hasParameters(){
    return (parametersMap!=null);
  }

  /**
   * Returns the BaseURL + all parameters
   */
  public String toString(){
    return getFullURL();
  }

  public static void removeGloballyMaintainedParameter(String parameterName){
    List l = globalMaintainedParameters;
    l.remove(parameterName);
  }

  public static void addGloballyMaintainedParameter(String parameterName){
    List l = globalMaintainedParameters;
    if(l!=null){
      if(!l.contains(parameterName)){
        l.add(parameterName);
      }
    }
  }

  public static List getGloballyMaintainedParameters(IWContext iwc){
    return globalMaintainedParameters;
  }

  public static void addGloballyMaintainedBuilderParameter(String parameterName){
    List l = globalMaintainedBuilderParameters;
    if(l!=null){
      if(!l.contains(parameterName)){
        l.add(parameterName);
      }
    }
  }

  public static List getGloballyMaintainedBuilderParameters(IWContext iwc){
    return globalMaintainedBuilderParameters;
  }


}
