//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.core;


import java.util.*;
import com.idega.jmodule.object.Image;
import com.idega.jmodule.object.ModuleInfo;
import java.io.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.8 - Under development
*/
public class ICBundle{


  private String rootPath;
  private String resourcesPath;
  private Hashtable handlers;
  private static Hashtable instances;
  ICPropertyList propertyList;
  static String pathSeparator = System.getProperty("file.separator");



  /**
  * Beta implementation
  */
   private ICBundle(String rootPath){
      handlers = new Hashtable();
      setResourcesPath(rootPath+"/resources");
      loadBundle();
   }

  /**
  * Beta implementation
  */
   private ICBundle(ICComponent component,ModuleInfo modinfo){
      this("/idegaweb/bundles/"+component.getName()+".bundle");
   }

  /**
   * Beta implementation
   */
   public static ICBundle getInstance(ICComponent component,ModuleInfo modinfo){
      Class componentClass = component.getClass();
      ICBundle instance = (ICBundle) instances.get(componentClass);
      if (instance==null){
        instance = new ICBundle(component,modinfo);
        instances.put(componentClass,instance);
      }
      return instance;
   }


   private void loadBundle(){
      propertyList = new ICPropertyList(getRootPath()+getPathSeparator()+"bundle.xml");
   }


   public Image getIconImage(){
      return new Image(getProperty("iconimage"));
   }

    public String getProperty(String name){
      return propertyList.getProperty(name);
    }

    public void setProperty(String propertyName,String propertyValue){
      propertyList.setProperty(propertyName,propertyValue);
    }


    private String getRootPath(){
      return rootPath;
    }

    private void setResourcesPath(String path){
        resourcesPath=path;
    }

    private void setRootPath(String path){
      this.rootPath=path;
    }


    public Image getLocalizedImage(String name,ModuleInfo modinfo){
        return new Image(getResourcesPath(modinfo)+"/"+name);
    }


    public String getLocalizedString(String name,ModuleInfo modinfo){
      return getICLocalizedStringHandler(modinfo).getString(name);
    }

    public String getResourcesPath(ModuleInfo modinfo){
        return resourcesPath+"/"+modinfo.getCurrentLocale().toString();
    }

    //public String getResourcePath(String resourceType,ModuleInfo modinfo){
    //  return null;
    //}

    public String getClassesPath(){
        return this.getRootPath()+"/classes";
    }


    public ICLocalizedStringHandler getICLocalizedStringHandler(ModuleInfo modinfo){
      Locale locale = modinfo.getCurrentLocale();
      ICLocalizedStringHandler handler = (ICLocalizedStringHandler) handlers.get(locale);
      if (handler==null){
        handler = new ICLocalizedStringHandler(this,getResourcesPath(modinfo)+"/Localized.strings",locale);
        handlers.put(locale,handler);
      }
      return handler;
    }


    public String getBundleType(){
      String theReturn =  getProperty("bundletype");
      if(theReturn == null){
        theReturn = "bundle";
      }
      return theReturn;
    }

    public static String getPathSeparator(){
      return pathSeparator;
    }

    public void storeState(){
      propertyList.store();
    }


}
