//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import java.io.*;
import java.util.*;
import javax.servlet.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*@deprecated replaced with IWMainApplication
*/
public class IdegaWebHandler extends IWMainApplication{


    public IdegaWebHandler(ServletContext application){
      super(application);
    }

/*
  public static String windowOpenerURL="/common/windowopener.jsp";
  public static String windowOpenerParameter="idegaweb_window_for_page";
  public static String objectInstanciatorURL="/common/intanciator.jsp";
  public static String templateParameter="idegaweb_template";
  public static String templateClassParameter="idegaweb_template_class";
  public static String classToInstanciateParameter="idegaweb_instance_class";

  public static String getObjectInstanciatorURL(Class className,String templateName){
    return getObjectInstanciatorURL(className.getName(),templateName);
  }

  public static String getObjectInstanciatorURL(String className,String templateName){
      return objectInstanciatorURL+"?"+classToInstanciateParameter+"="+getEncryptedClassName(className)+"&"+templateParameter+"="+templateName;
  }


  public static String getObjectInstanciatorURL(String className){
      return objectInstanciatorURL+"?"+classToInstanciateParameter+"="+getEncryptedClassName(className);
  }

  public static String getObjectInstanciatorURL(Class classToInstanciate){
      return getObjectInstanciatorURL(classToInstanciate.getName());
  }

  public static String getObjectInstanciatorURL(Class classToInstanciate,Class templateClass){
    return objectInstanciatorURL+"?"+classToInstanciateParameter+"="+getEncryptedClassName(classToInstanciate)+"&"+templateClassParameter+"="+getEncryptedClassName(templateClass);
  }


  public static String getEncryptedClassName(String classToInstanciate){
      return classToInstanciate;
  }

  public static String getEncryptedClassName(Class classToInstanciate){
      return getEncryptedClassName(classToInstanciate.getName());
  }

  public String decryptClassName(String encryptedClassName){
    return encryptedClassName;
  }

*/

}
