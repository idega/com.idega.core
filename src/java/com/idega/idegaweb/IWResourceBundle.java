
package com.idega.idegaweb;
import java.util.Enumeration;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.idega.jmodule.object.Image;
import java.io.FileNotFoundException;
import java.util.MissingResourceException;
import java.util.TreeMap;
import java.util.Iterator;
import com.idega.exception.IWBundleDoesNotExist;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IWResourceBundle extends ResourceBundle {

    // ==================privates====================

    private TreeMap lookup;
    private Properties properties = new Properties();
    //private Properties lookup;
    private Locale locale;
    private File file;
    private IWBundle iwBundleParent;
    private String resourcesURL;
    private static String slash = "/";


    /**
     * Creates a IWResourceBundle for a specific Locale
     * @param file file to read from.
     * @param parent Parent IWBundle to instanciate from
     * @param locale Locale to create from
     */
    public IWResourceBundle (IWBundle parent,File file,Locale locale) throws IOException {
        setIWBundleParent(parent);
        setLocale(locale);
        this.file = file;

        try{
        //lookup.load(new FileInputStream(file));
        properties.load(new FileInputStream(file));
        lookup = new TreeMap(properties);
        setResourcesURL(parent.getResourcesVirtualPath()+"/"+locale.toString()+".locale");        }
        catch(FileNotFoundException e){
          e.printStackTrace(System.err);
        }
    }

    /**
     * Override of ResourceBundle, same semantics
     */
    public Object handleGetObject(String key) {
      if(lookup!=null){
        Object obj = lookup.get(key);
        return obj; // once serialization is in place, you can do non-strings
      }
      else{
        IWBundle parent = getIWBundleParent();
        if(parent!=null){
          throw new IWBundleDoesNotExist(parent.getBundleIdentifier());
        }
        else{
          throw new IWBundleDoesNotExist();
        }
      }
    }

    /**
     * Implementation of ResourceBundle.getKeys.
     */
    public Enumeration getKeys() {
        Enumeration result = null;
        if (parent != null) {
            //final Enumeration myKeys = lookup.keys();
            Iterator iter = lookup.keySet().iterator();
            final Enumeration myKeys = new EnumerationIteratorWrapper(iter);
            final Enumeration parentKeys = parent.getKeys();

            result = new Enumeration() {
                public boolean hasMoreElements() {
                    if (temp == null)
                        nextElement();
                    return temp != null;
                }

                public Object nextElement() {
                    Object returnVal = temp;
                    if (myKeys.hasMoreElements())
                        temp = myKeys.nextElement();
                    else {
                        temp = null;
                        while (temp == null && parentKeys.hasMoreElements()) {
                            temp = parentKeys.nextElement();
                            if (lookup.containsKey(temp))
                                temp = null;
                        }
                    }
                    return returnVal;
                }

                Object temp = null;
            };
        }
        else{
          //result = lookup.keys();
          Iterator iter = lookup.keySet().iterator();
          result = new EnumerationIteratorWrapper(iter);
        }


        return result;

    }

    public Locale getLocale(){
      return locale;
    }

    private void setLocale(Locale locale){
      this.locale=locale;
    }

    public void storeState(){
        try{
          properties.clear();
          properties.putAll(lookup);
          properties.store(new FileOutputStream(file),null);
          //lookup.store(new FileOutputStream(file),null);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException ex){
          ex.printStackTrace();
        }

    }

    /**
     * Uses getString but returns null if resource is not found
     */
    public String getLocalizedString(String key){

      try{
        return super.getString(key);
      }
      catch(MissingResourceException e){
        return null;
      }
    }

    public String getLocalizedString(String key, String returnValueIfNull){
      String returnString = getLocalizedString(key);
      if (returnString == null) return returnValueIfNull;
      else return returnString;
    }

    ///
   //  *@deprecated Replaced with getLocalizedString(key)
   //  */
   // public String getStringChecked(String key){
   //   return getLocalizedString(key);
   // }

    public void setString(String key,String value){
      //lookup.setProperty(key,value);
      lookup.put(key,value);
      String string = (String)this.iwBundleParent.getLocalizableStringsMap().get(key);
      if(string==null){
        this.iwBundleParent.getLocalizableStringsMap().put(key,value);
      }
    }

    public boolean removeString(String key){
      return (String) lookup.remove(key)!=null?true:false;
    }

    private void setIWBundleParent(IWBundle parent){
      this.iwBundleParent=parent;
    }

    public IWBundle getIWBundleParent(){
      return iwBundleParent;
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

    public Image getImage(String urlInBundle, String key, String defaultKeyValue){
      return new Image(getResourcesURL()+slash+urlInBundle,getLocalizedString(key,defaultKeyValue));
    }

    private void setResourcesURL(String url){
      resourcesURL=url;
    }

    public String getResourcesURL(){
      return resourcesURL;
    }


    private class EnumerationIteratorWrapper implements Enumeration{
      private Iterator iterator;

      public EnumerationIteratorWrapper(Iterator iter){
        this.iterator = iter;
      }

      public boolean hasMoreElements(){
        return iterator.hasNext();
      }

      public Object nextElement(){
        return iterator.next();
      }
    }

}
