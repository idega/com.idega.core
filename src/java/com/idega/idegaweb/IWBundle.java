//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;


import java.util.*;
import com.idega.jmodule.object.Image;
import com.idega.jmodule.object.ModuleInfo;
import java.io.*;
import java.util.Hashtable;
import java.util.ResourceBundle;
import com.idega.util.LocaleUtil;
import com.idega.util.FileUtil;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0a1- Under development
*/
public class IWBundle{

  private static final String slash = "/";

  private String identifier;
  private String rootVirtualPath;
  private String rootRealPath;

  private String resourcesVirtualPath;
  private String resourcesRealPath;

  private String propertiesRealPath;

  private String classesRealPath;

  private IWMainApplication superApplication;

  private Hashtable localePaths;
  private Hashtable resourceBundles;
  //debug
  private boolean autoCreate=false;

  private Hashtable handlers;
  //private static Hashtable instances;
  private Hashtable localeRealPaths;

  private Properties localizableStrings;
  private File localizableStringsFile;

  private IWPropertyList propertyList;




   protected IWBundle(String rootRealPath,String bundleIdentifier,IWMainApplication superApplication){
        this(rootRealPath,rootRealPath,bundleIdentifier,superApplication);
   }

   protected IWBundle(String rootRealPath,String rootVirtualPath,String bundleIdentifier,IWMainApplication superApplication){
      this.superApplication=superApplication;
      this.identifier=bundleIdentifier;
      handlers = new Hashtable();
      localeRealPaths = new Hashtable();
      resourceBundles = new Hashtable();
      setRootRealPath(rootRealPath);
      setRootVirtualPath(rootVirtualPath);
      loadBundle();

   }

  /**
  * Beta implementation
  *
   private IWBundle(IWComponent component,ModuleInfo modinfo){
      this("/idegaweb/bundles/"+component.getName()+".bundle");
   }
  */

  /**
   * Beta implementation

   public static IWBundle getInstance(IWComponent component,ModuleInfo modinfo){
      Class componentClass = component.getClass();
      IWBundle instance = (IWBundle) instances.get(componentClass);
      if (instance==null){
        instance = new IWBundle(component,modinfo);
        instances.put(componentClass,instance);
      }
      return instance;
   }
    */

    public void reloadBundle(){
      loadBundle();
    }

   private void loadBundle(){
      setResourcesVirtualPath(getRootVirtualPath()+"/"+"resources");

      System.out.println("getResourcesVirtualPath() : "+ getResourcesVirtualPath() );

      setResourcesRealPath(getRootRealPath()+FileUtil.getFileSeparator()+"resources");

        System.out.println("getResourcesRealPath() : "+ getResourcesRealPath() );


      setPropertiesRealPath(getRootRealPath()+FileUtil.getFileSeparator()+"properties");

        System.out.println("getPropertiesRealPath() : "+ getPropertiesRealPath() );

      setClassesRealPath();
      if(autoCreate){
        this.initializeStructure();
        propertyList = new IWPropertyList(getPropertiesRealPath(),"bundle.pxml",true);
      }
      else{
        propertyList = new IWPropertyList(getPropertiesRealPath(),"bundle.pxml",false);
      }

   }

   protected String getRootRealPath(){
    return rootRealPath;
   }

   protected String getRootVirtualPath(){
    return rootVirtualPath;
   }

   public Image getIconImage(){
      return new Image(getProperty("iconimage"));
   }

    public String getProperty(String propertyName){
      return propertyList.getProperty(propertyName);
    }

    public String getProperty(String propertyName, String returnValueIfNull){
      String prop = getProperty(propertyName);
      if( prop == null ) return returnValueIfNull;
      else return prop;
    }

    public void setProperty(String propertyName,String propertyValue){
      propertyList.setProperty(propertyName,propertyValue);
    }

    public void setProperty(String propertyName,String[] propertyValues){
      propertyList.setProperty(propertyName,propertyValues);
    }

    public void setArrayProperty(String propertyName,String propertyValue){
      propertyList.setArrayProperty(propertyName,propertyValue);
    }

    public void setProperty(String propertyName){
      propertyList.removeProperty(propertyName);
    }
    private void setResourcesRealPath(String path){
        resourcesRealPath=path;
    }

    private void setResourcesVirtualPath(String path){
        resourcesVirtualPath=path;
    }

    private void setPropertiesRealPath(String path){
      propertiesRealPath=path;
    }

    private void setRootRealPath(String path){
        rootRealPath=path;
    }

    public void setRootVirtualPath(String path){
        rootVirtualPath=path;
    }

    public Image getLocalizedImage(String name,Locale locale){
        return getResourceBundle(locale).getImage(name);
    }

    /**
     * Convenience method - Recommended to create a ResourceBundle (through getResourceBundle(locale)) to use instead more efficiently
     */
    public String getLocalizedString(String name,Locale locale){
      return getResourceBundle(locale).getString(name);
    }

    //public String getResourcesPath(ModuleInfo modinfo){
    //    return resourcesPath+"/"+modinfo.getCurrentLocale().toString();
    //}

    //public String getResourcePath(String resourceType,ModuleInfo modinfo){
    //  return null;
    //}

    protected String getClassesRealPath(){
        return classesRealPath;
    }

    private void setClassesRealPath(){
      classesRealPath=this.getRootRealPath()+FileUtil.getFileSeparator()+"classes";
    }


    public String[] getAvailableProperties(){
      return ( (String[]) ((Vector)propertyList.getKeys()).toArray(new String[0]) );
    }


    public String[] getLocalizableStrings(){
      return (String[])getLocalizableStringsProperties().keySet().toArray(new String[0]);
    }

    protected Properties getLocalizableStringsProperties(){
      if(localizableStrings==null){
        localizableStrings = new Properties();
        try{
          localizableStrings.load(new FileInputStream(getLocalizableStringsFile()));
        }
        catch(IOException ex){
          ex.printStackTrace();
        }

      }
      return localizableStrings;
    }

    private File getLocalizableStringsFile(){
        if(localizableStringsFile==null){
          try{
            localizableStringsFile = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(),"Localizable.strings");
          }
          catch(IOException ex){
            ex.printStackTrace();
          }
        }
        return localizableStringsFile;
    }

    public IWResourceBundle getResourceBundle(Locale locale){
        IWResourceBundle theReturn = (IWResourceBundle)resourceBundles.get(locale);
        try{
          if(theReturn == null){
            File file;
            if(autoCreate){
              file = com.idega.util.FileUtil.getFileAndCreateIfNotExists(getResourcesRealPath(locale),"Localized.strings");
            }
            else{
              file = new File(getResourcesRealPath(locale)+FileUtil.getFileSeparator()+"Localized.strings");
            }
            theReturn = new IWResourceBundle(this,file,locale);
            resourceBundles.put(locale,theReturn);
          }
        }
        catch(Exception ex){
          ex.printStackTrace();
        }
        return theReturn;
    }

    /*protected IWLocalizedStringHandler getIWLocalizedStringHandler(Locale locale){
      IWLocalizedStringHandler handler = (IWLocalizedStringHandler) handlers.get(locale);
      if (handler==null){
        handler = new IWLocalizedStringHandler(this,getResourcesRealPath()+File.pathSeparator+"Localized.strings",locale);
        handlers.put(locale,handler);
      }
      return handler;
    }*/


    public String getVersion(){
      String theReturn =  getProperty("version");
      if(theReturn == null){
        theReturn = "1";
      }
      return theReturn;
    }

    public String getBundleType(){
      String theReturn =  getProperty("bundletype");
      if(theReturn == null){
        theReturn = "bundle";
      }
      return theReturn;
    }

    public static String getFileSeparator(){
      return FileUtil.getFileSeparator();
    }

    public void storeState(){
      propertyList.store();
      Enumeration enum = this.resourceBundles.elements();
      while (enum.hasMoreElements()) {
        IWResourceBundle item = (IWResourceBundle)enum.nextElement();
        item.storeState();
      }
      try{
      System.out.println(localizableStringsFile);
      this.getLocalizableStringsProperties().store(new FileOutputStream(getLocalizableStringsFile()),null);
      }
      catch(IOException ex){
        ex.printStackTrace();
      }
    }

    //public String getResourcesVirtualPath(){
    //
    //}

    protected String getResourcesRealPath(){
          return resourcesRealPath;
         //return resourcesPath+"/"+modinfo.getCurrentLocale().toString();
    }

    protected String getResourcesURL(Locale locale){
      return getResourcesVirtualPath(locale);
    }

    protected String getResourcesURL(){
      return getResourcesVirtualPath();
    }

    protected String getResourcesVirtualPath(Locale locale){
    //private String getLocaleDirectory(Locale locale){
        //return this.getResourcesVirtualPath()+File.pathSeparator+locale.toString()+".locale";
        return this.getResourceBundle(locale).getResourcesURL();
    }

    protected String getResourcesVirtualPath(){
    //private String getLocaleDirectory(Locale locale){
        //return this.getResourcesVirtualPath()+File.pathSeparator+locale.toString()+".locale";
        return resourcesVirtualPath;
    }


    protected String getResourcesRealPath(Locale locale){
      String path = (String)localeRealPaths.get(locale);
      if (path==null){
        path = getResourcesRealPath()+FileUtil.getFileSeparator()+locale.toString()+".locale";
        localeRealPaths.put(locale,path);
      }
      return path;
    }


    protected String getPropertiesRealPath(){
      //return getRealRootPath()+File.pathSeparator+"properties";
      return propertiesRealPath;
    }

    public static IWBundle getBundle(String bundleIdentifier , IWMainApplication application){
      return application.getBundle(bundleIdentifier);
    }

    protected void initializeStructure(){
      String[] dirs = new String[5];
      String resourcesDirectory = this.getResourcesRealPath();
      dirs[0]=resourcesDirectory;
      String propertiesDirectory = this.getPropertiesRealPath();
      dirs[1]=propertiesDirectory;
      String classesDirectory = this.getClassesRealPath();
      dirs[2]=classesDirectory;
      Locale english =  Locale.ENGLISH;
      Locale icelandic = LocaleUtil.getIcelandicLocale();

      String enLocalePath = getResourcesRealPath(english);
      dirs[3]=enLocalePath;
      String isLocalePath = getResourcesRealPath(icelandic);
      dirs[4]=isLocalePath;
      for (int i = 0; i < dirs.length; i++) {
        File file = new File(dirs[i]);
        file.mkdirs();
      }
    }

    public String getBundleIdentifier(){
      return identifier;
    }


    public Image getImage(String urlInBundle){
      return new Image(getResourcesURL()+slash+urlInBundle);
    }

    public Image getImage(String urlInBundle, int width, int height){
      return getImage(urlInBundle, "", width, height);
    }

    public Image getImage(String urlInBundle, String name, int width, int height){
      return new Image(getResourcesURL()+slash+urlInBundle,name, width, height);
    }

    public Image getImage(String urlInBundle, String name){
      return new Image(getResourcesURL()+slash+urlInBundle,name);
    }



}
