//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;


import java.util.Locale;
import java.io.File;
import com.idega.util.FileUtil;
import java.util.List;
import com.idega.util.LocaleUtil;
import com.idega.data.EntityControl;
import java.util.Iterator;
import java.util.Vector;
import com.idega.core.accesscontrol.business.AccessController;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.8 - Under development
*/
public class IWMainApplicationSettings extends IWPropertyList{

  private static String IW_SERVICE_CLASS_NAME="iw_service_class_name";
  private static String DEFAULT_TEMPLATE_NAME="defaulttemplatename";
  private static String DEFAULT_TEMPLATE_CLASS="defaulttemplateclass";
  private static String DEFAULT_FONT="defaultfont";
  private static String DEFAULT_FONT_SIZE="defaultfontsize";
  private static String DEFAULT_LOCALE="defaultlocale";
  private static String _SERVICE_CLASSES_KEY = "iw_service_class_key";


  public IWMainApplicationSettings(IWMainApplication application){
    super(application.getPropertiesRealPath(),"idegaweb.pxml",true);
  }

    public void setDefaultTemplate(String templateName,String classname){
      setProperty(DEFAULT_TEMPLATE_NAME,templateName);
      setProperty(DEFAULT_TEMPLATE_CLASS,classname);
    }

    public String getDefaultTemplateName(){
      return getProperty(DEFAULT_TEMPLATE_NAME);
    }

    public String getDefaultTemplateClass(){
      return getProperty(DEFAULT_TEMPLATE_CLASS);
    }

    public String getDefaultFont(){
      return getProperty(DEFAULT_FONT);
    }

    public void setDefaultFont(String fontname){
      setProperty(DEFAULT_FONT,fontname);
    }

    public int getDefaultFontSize(){
      return Integer.parseInt(getProperty(DEFAULT_FONT_SIZE));
    }

    public void setDefaultFontSize(int size){
      setProperty(DEFAULT_FONT_SIZE,size);
    }


    /*public void setDefaultLocale(Locale locale){
        setProperty(DEFAULT_LOCALE,locale.toString());
    }*/

    public void setDefaultLocale(Locale locale){
        setProperty(DEFAULT_LOCALE,locale.toString());
    }

    /*public Locale getDefaultLocale(){
      return (new Locale(getProperty(DEFAULT_LOCALE)));
    }*/

    public Locale getDefaultLocale(){
      String localeIdentifier = getProperty(DEFAULT_LOCALE);
      Locale locale = null;
      if(localeIdentifier==null){
          localeIdentifier=LocaleUtil.getIcelandicLocale().toString();
          locale = LocaleUtil.getLocale(localeIdentifier);
          setDefaultLocale(locale);
      }
      locale = LocaleUtil.getLocale(localeIdentifier);
      return locale;
    }

    public AccessController getDefaultAccessController(){
      return(AccessController) new com.idega.core.accesscontrol.business.AccessControl();
    }

    /**
     * Returns false if the removing fails
     */
    public boolean removeIWService(Class serviceClass){
        return false;
    }

    /**
     * Returns false if the class is wrong or it fails
     */
    public boolean addIWService(Class serviceClass){
      return false;
    }

    /**
     * Returns a list of Class objects corresponding to the IWService Classes
     */
    public List getServiceClasses(){
      //return null;
      IWPropertyList plist = getIWPropertyList(_SERVICE_CLASSES_KEY);
      if(plist!=null){
        List l = new Vector();
        Iterator iter = plist.iterator();
        while (iter.hasNext()) {
          IWProperty item = (IWProperty)iter.next();
          String serviceClass = item.getValue();
          try {
            l.add(Class.forName(serviceClass));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        return l;
      }
      return null;
    }


    public void setEntityAutoCreation(boolean ifAutoCreate){
      this.setProperty("entity-auto-create",ifAutoCreate);
      EntityControl.setAutoCreationOfEntities(ifAutoCreate);
    }

    public boolean getIfEntityAutoCreate(){
      String value = getProperty("entity-auto-create");
      if(value==null){
        return false;
      }
      else{
        return Boolean.valueOf(value).booleanValue();
      }
    }


}
