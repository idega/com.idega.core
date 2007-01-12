package com.idega.idegaweb;

/**
 * A utilityclass for normalizing URL's, multy value parameters are stored under the same map key like so: <br>
 * key = value1&key=value2&key=value3. GetFullURL will return the url with the parameters in alphabetical key order.
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
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

  public void addPageClassParameter(Class pageClass){
    addParameter(Page.IW_FRAME_CLASS_PARAMETER,IWMainApplication.getEncryptedClassName(pageClass));
  }

  public void addParameter(String parameterName,String parameterValue){
  	String value = (String) getParametersMap().get(parameterName);
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

  private Map getParametersMap(){
    if(this.parametersMap == null){
      this.parametersMap = new TreeMap();
    }
    return this.parametersMap;
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
      String theReturn = this.baseURL+questionMark;
      Map map = getParametersMap();
      Set keySet = map.keySet();
      int last = keySet.size();
      int counter = 0;
      Iterator iter = keySet.iterator();
      while (iter.hasNext()) {
      	counter++;
        String key = (String)iter.next();
        String value = (String)map.get(key);
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
