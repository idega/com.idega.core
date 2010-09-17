package com.idega.idegaweb;

/**
 * A utilityclass for normalizing URL's, multy value parameters are stored under the same map key like so: <br>
 * key = value1&key=value2&key=value3. GetFullURL will return the url with the parameters in alphabetical key order.
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.ui.Parameter;

public class IWURL {

  private static final String questionMark = "?";
  private static final String equalsMark = "=";
  private static final String ampersand = "&";
  private String baseURL;
  private static List<String> globalMaintainedParameters;
  private static List<String> globalMaintainedBuilderParameters;

  static{
    globalMaintainedParameters = new ArrayList<String>();
    globalMaintainedParameters.add(Page.IW_FRAME_STORAGE_PARMETER);
    globalMaintainedParameters.add(Page.IW_FRAME_CLASS_PARAMETER);
    globalMaintainedParameters.add(IWMainApplication.classToInstanciateParameter);
    globalMaintainedParameters.add(IWConstants.PARAM_NAME_OUTPUT_MARKUP_LANGUAGE);

    globalMaintainedBuilderParameters = new ArrayList<String>();
    globalMaintainedBuilderParameters.add(ICBuilderConstants.IB_PAGE_PARAMETER);
  }

  private Map<String, String> parametersMap;

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
      StringTokenizer token = new StringTokenizer(parametersAndValues,ampersand);
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

  public void addPageClassParameter(Class<?> pageClass){
    addParameter(Page.IW_FRAME_CLASS_PARAMETER,IWMainApplication.getEncryptedClassName(pageClass));
  }

  public void addParameter(String parameterName,String parameterValue){
  	String value = getParametersMap().get(parameterName);
  	//multivalue handling
  	if(value!=null){
  		
  		if(!value.equals(parameterValue)){
  		//it is multivalued!
  			value = value+ampersand+parameterName+equalsMark+parameterValue;
  			getParametersMap().put(parameterName,value);
  		}
  		
  	}
  	else{
  		getParametersMap().put(parameterName,parameterValue);
  	}
  	
  	
    
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

  private Map<String, String> getParametersMap(){
    if(this.parametersMap == null){
      this.parametersMap = new TreeMap<String, String>();
    }
    return this.parametersMap;
  }

  public static IWURL getImageURL(int imageID){
    return null;
  }

  /**
   * Returns the BaseURL + all parameters
   */
  public String getFullURL(){
    if(hasParameters()){
      String theReturn = this.baseURL+questionMark;
      Map<String, String> map = getParametersMap();
      Set<String> keySet = map.keySet();
      int last = keySet.size();
      int counter = 0;
      Iterator<String> iter = keySet.iterator();
      while (iter.hasNext()) {
      	counter++;
        String key = iter.next();
        String value = map.get(key);
        if(counter<last){
        		theReturn += key+equalsMark+value+ampersand;
        }
        else{
        		theReturn += key+equalsMark+value;
        }
      }
      return theReturn;
    }
    else{
      return this.baseURL;
    }
  }

  public boolean hasParameters(){
    return (this.parametersMap!=null && !this.parametersMap.isEmpty());
  }

  /**
   * Returns the BaseURL + all parameters
   */
  @Override
public String toString(){
    return getFullURL();
  }

  public static void removeGloballyMaintainedParameter(String parameterName){
    List<String> l = globalMaintainedParameters;
    l.remove(parameterName);
  }

  public static void addGloballyMaintainedParameter(String parameterName){
    List<String> l = globalMaintainedParameters;
    if(l!=null){
      if(!l.contains(parameterName)){
        l.add(parameterName);
      }
    }
  }

  public static List<String> getGloballyMaintainedParameters(IWContext iwc){
    return globalMaintainedParameters;
  }

  public static void addGloballyMaintainedBuilderParameter(String parameterName){
    List<String> l = globalMaintainedBuilderParameters;
    if(l!=null){
      if(!l.contains(parameterName)){
        l.add(parameterName);
      }
    }
  }

  public static List<String> getGloballyMaintainedBuilderParameters(IWContext iwc){
    return globalMaintainedBuilderParameters;
  }

}