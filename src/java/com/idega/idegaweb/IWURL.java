package com.idega.idegaweb;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import com.idega.jmodule.object.interfaceobject.Parameter;
import com.idega.idegaweb.IWMainApplication;
import com.idega.jmodule.object.Page;
import com.idega.jmodule.object.ModuleInfo;

import java.util.List;
import java.util.Vector;


public class IWURL {

  private String baseURL;
  private static List globalMaintainedParameters;

  static{
    globalMaintainedParameters = new Vector();
    globalMaintainedParameters.add(Page.IW_FRAME_STORAGE_PARMETER);
    globalMaintainedParameters.add(Page.IW_FRAME_CLASS_PARAMETER);
    globalMaintainedParameters.add(IWMainApplication.classToInstanciateParameter);
  }

  private IWURL(String baseURL){
    this.baseURL=baseURL;
  }

  public static IWURL getURL(String baseURL){
    return new IWURL(baseURL);
  }

  /**
   * @todo implement
   */
  public void addParameter(String parameterName,String parameterValue){

  }

  /**
   * @todo implement
   */
  public void addParameter(Parameter parameter){

  }

  public static IWURL getImageURL(int imageID){
    return null;
  }

  public static IWURL getWindowOpenerURL(Class windowClass){
    IWURL url = getURL(IWMainApplication.windowOpenerURL);
    url.addParameter("","");
    return url;
  }

  public static IWURL getBuilderURL(int pageID){
    return getBuilderURL(Integer.toString(pageID));
  }

  public static IWURL getBuilderURL(String pageKey){
    IWURL url = getURL(IWMainApplication.BUILDER_SERVLET_URL);
    url.addParameter("","");
    return url;
  }

  public String toString(){
    return baseURL;
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

  public static List getGloballyMaintainedParameters(ModuleInfo modinfo){
    return globalMaintainedParameters;
  }

}