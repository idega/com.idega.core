//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;


import java.util.Locale;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.8 - Under development
*/
public class IWMainApplicationSettings extends IWPropertyList{


  private static String DEFAULT_TEMPLATE_NAME="defaulttemplatename";
  private static String DEFAULT_TEMPLATE_CLASS="defaulttemplateclass";
  private static String DEFAULT_FONT="defaultfont";
  private static String DEFAULT_FONT_SIZE="defaultfontsize";
  private static String DEFAULT_LOCALE="defaultlocale";


  public IWMainApplicationSettings(IWMainApplication application){
    super(application.getApplicationSpecialPath()+"/idegaweb.xml");
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

    public void setDefaultLocale(String localeString){
        setProperty(DEFAULT_LOCALE,localeString);
    }

    /*public Locale getDefaultLocale(){
      return (new Locale(getProperty(DEFAULT_LOCALE)));
    }*/

    public String getDefaultLocaleString(){
      return getProperty(DEFAULT_LOCALE);
    }



}
