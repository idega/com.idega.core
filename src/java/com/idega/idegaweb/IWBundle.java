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
import com.idega.core.data.ICObject;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0a1- Under development
*/
public class IWBundle implements java.lang.Comparable{

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
  private boolean autoCreate=true;

  private Hashtable handlers;
  //private static Hashtable instances;
  private Hashtable localeRealPaths;

  private SortedMap localizableStringsMap;
  private Properties localizableStringsProperties;

  private File localizableStringsFile;

  private IWPropertyList propertyList;

  static final String propertyFileName = "bundle.pxml";

  final static String BUNDLE_IDENTIFIER_PROPERTY_KEY = "iw_bundle_identifier";
  final static String COMPONENTLIST_KEY = "iw_components";

  private final static String COMPONENT_NAME_PROPERTY="component_name";
  private final static String COMPONENT_TYPE_PROPERTY="component_type";
  private final static String COMPONENT_ICON_PROPERTY="component_icon";
  private final static String COMPONENT_CLASS_PROPERTY="component_class";

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
      this.setProperty(BUNDLE_IDENTIFIER_PROPERTY_KEY,bundleIdentifier);
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
      resourceBundles.clear();
      localizableStringsProperties=null;
    }

   private void loadBundle(){
      setResourcesVirtualPath(getRootVirtualPath()+"/"+"resources");

      //System.out.println("getResourcesVirtualPath() : "+ getResourcesVirtualPath() );

      setResourcesRealPath(getRootRealPath()+FileUtil.getFileSeparator()+"resources");

        //System.out.println("getResourcesRealPath() : "+ getResourcesRealPath() );


      setPropertiesRealPath(getRootRealPath()+FileUtil.getFileSeparator()+"properties");

        //System.out.println("getPropertiesRealPath() : "+ getPropertiesRealPath() );

      setClassesRealPath();
      if(autoCreate){
        this.initializeStructure();
        propertyList = new IWPropertyList(getPropertiesRealPath(),propertyFileName,true);
      }
      else{
        propertyList = new IWPropertyList(getPropertiesRealPath(),propertyFileName,false);
      }

      String SystemClassPath = System.getProperty("java.class.path");
      String newClassPath = SystemClassPath+File.pathSeparator+this.getClassesRealPath();
      System.setProperty("java.class.path",newClassPath);

      installComponents();
      try{
        createDataRecords();
      }
      catch(Exception e){
        e.printStackTrace();
      }
   }

   private void createDataRecords()throws Exception{
      List entities = com.idega.data.EntityFinder.findAllByColumn(ICObject.getStaticInstance(ICObject.class),ICObject.getObjectTypeColumnName(),ICObject.COMPONENT_TYPE_DATA,ICObject.getBundleColumnName(),this.getBundleIdentifier());
      if(entities!=null){
        Iterator iter = entities.iterator();
        while (iter.hasNext()) {
          ICObject ico = (ICObject)iter.next();
          try{
            Class c = ico.getObjectClass();
            c.newInstance();
          }
          catch(Exception e){
            e.printStackTrace();
          }
        }
      }
   }


    private void installComponents(){
      List list = this.getComponentKeys();
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        String className = (String)iter.next();
        ICObject ico = ICObject.getICObject(className);
        if(ico==null){
          try{
            ico = new ICObject();
            ico.setObjectClass(Class.forName(className));
            ico.setName(this.getComponentName(className));
            ico.setObjectType(this.getComponentType(className));
            ico.setBundle(this);
            ico.insert();
          }
          catch(Exception e){
            e.printStackTrace();
          }
        }
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
      return (String[])getLocalizableStringsMap().keySet().toArray(new String[0]);
    }

    public boolean removeLocalizableString(String key){
      Enumeration enum = this.resourceBundles.elements();
      while (enum.hasMoreElements()) {
        IWResourceBundle item = (IWResourceBundle)enum.nextElement();
        item.removeString(key);
      }
      return this.localizableStringsMap.remove(key)!=null?true:false;
    }

    protected Properties getLocalizableStringsProperties(){
      initializePropertiesStrings();
      return localizableStringsProperties;
    }

    protected Map getLocalizableStringsMap(){
      initializePropertiesStrings();
      return localizableStringsMap;
    }

    private void initializePropertiesStrings(){
      if(localizableStringsProperties==null){
        localizableStringsProperties = new Properties();
        try{
          localizableStringsProperties.load(new FileInputStream(getLocalizableStringsFile()));
          localizableStringsMap = new TreeMap(localizableStringsProperties);
        }
        catch(IOException ex){
          ex.printStackTrace();
        }
       }
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

    public IWResourceBundle getResourceBundle(ModuleInfo modinfo){
      return getResourceBundle(modinfo.getCurrentLocale());
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
        //System.out.println("localizableStringsFile:"+localizableStringsFile);

        getLocalizableStringsProperties().clear();
        getLocalizableStringsProperties().putAll(localizableStringsMap);
        getLocalizableStringsProperties().store(new FileOutputStream(getLocalizableStringsFile()),null);
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

    /**
     * Returns the ICObjects associated with this bundle
     * Returns null if there is an exception
     */
    public ICObject[] getICObjects(){
      try{
        return (ICObject[])(new ICObject()).findAllByColumn(ICObject.getBundleColumnName(),this.getBundleIdentifier());
      }
      catch(Exception e){
        return null;
      }
    }

    /**
     * Returns the ICObjects associated with this bundle and of the specified componentType
     * Returns null if there is an exception
     */
    public ICObject[] getICObjects(String componentType){
      try{
        return (ICObject[])(new ICObject()).findAllByColumn(ICObject.getBundleColumnName(),this.getBundleIdentifier(),ICObject.getObjectTypeColumnName(),componentType);
      }
      catch(Exception e){
        return null;
      }
    }


    private IWPropertyList getPropertyList(){
      return this.propertyList;
    }

    public static List getAvailableComponentTypes(){
      return ICObject.getAvailableComponentTypes();
    }


    public IWPropertyList getComponentList(){
      IWPropertyList list = getPropertyList().getPropertyList(COMPONENTLIST_KEY);
      if(list==null){
          list = getPropertyList().getNewPropertyList(COMPONENTLIST_KEY);
      }
      return list;
    }

    public void addComponent(String className,String componentType){
      addComponent(className,componentType,className.substring(className.lastIndexOf(".")+1));
    }

    public void addComponent(String className,String componentType,String componentName){
      //getComponentList().setProperty(className,componentName,componentType);
      IWProperty prop = getComponentList().getNewProperty();
      prop.setName(className);
      setComponentProperty(prop,COMPONENT_NAME_PROPERTY,componentName);
      setComponentProperty(prop,COMPONENT_TYPE_PROPERTY,componentType);

        ICObject ico = ICObject.getICObject(className);
        if(ico==null){
          try{
            ico = new ICObject();
            ico.setObjectClass(Class.forName(className));
            ico.setName(componentName);
            ico.setObjectType(componentType);
            ico.setBundle(this);
            ico.insert();
          }
          catch(Exception e){
            e.printStackTrace();
          }
        }
    }

    public void setComponentProperty(String className,String propertyName,String propertyValue){
      IWProperty prop = getComponentList().getIWProperty(className);
      if(prop!=null){
        setComponentProperty(prop,propertyName,propertyValue);
      }
    }

    public void setComponentProperty(IWProperty component,String propertyName,String propertyValue){
      IWPropertyList list = component.getPropertyList();
      if(list==null){
        list = component.getNewPropertyList();
      }
      list.setProperty(propertyName,propertyValue);
    }

    public String getComponentProperty(String className,String property){
      try{
        return getComponentList().getIWProperty(className).getPropertyList().getProperty(property);
      }
      catch(NullPointerException e){
        return null;
      }
    }

    public String getComponentType(String className){
      return getComponentProperty(className,COMPONENT_TYPE_PROPERTY);
    }

    public String getComponentName(String className){
      return getComponentProperty(className,COMPONENT_NAME_PROPERTY);
    }

    public String getComponentName(String className,Locale locale){
      return this.getResourceBundle(locale).getLocalizedString("iw.component."+className,getComponentName(className));
    }

    public void setComponentName(String className,Locale locale,String sName){
      this.getResourceBundle(locale).setString("iw.component."+className,sName);
    }

    public void removeComponent(String className){
      getComponentList().removeProperty(className);
      ICObject.removeICObject(className);
    }

    public List getComponentKeys(){
      return getComponentList().getKeys();
    }


    public int compareTo(Object o){
      IWBundle bundle = (IWBundle)o;
      return this.getBundleIdentifier().compareTo(bundle.getBundleIdentifier());
    }
}
