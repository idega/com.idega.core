package com.idega.core.ldap.client.cbutil;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 *    This 'International Text' static class acts as a central source of all localised
 *    strings.  If it cannot find a localised properties file, the
 *    default (english) file is used.  If that file can not be found, the key used to
 *    look up the translation is returned unchanged - hence it is useful if the keys
 *    are meaningful in their own right (i.e. use 'File' rather than 'String 42' as
 *    a key).<p>
 *
 *    Currently the class is static to provide easy access to i18n text by
 *    all classes and plugins, and because it is difficult to see how 
 *    supporting multiple different-locale languages would be helpful (note
 *    that multiple languages can be supported simply by using unicode).
 */

public class CBIntText
{
    static Locale locale = null;
    
    static Hashtable translations;
    
    private static boolean errorGiven = false;  // This is a 'complain once' class - usually it's either working or it's not...
    
    /*
     * If the local language is english, don't print warning messages
     * about missing translation files (it confuses soooo many people)
     */
     
    private static boolean english = true;  

    /**
     *    This initialises the international text class.  
     *    @param bundleLocation the name of the i18n bundle (e.g. "language/JX_ja.properties").
     *    @param customLoader a custom class loader (may be null).  JX uses a custom loader
     *           that auto-detects unicode and utf-8 files for ease of configuration by
     *           non-expert users.
     *
     */
     
    public static void init(String bundleLocation, ClassLoader customLoader)
    {
        locale = Locale.getDefault();

        Locale def = Locale.getDefault();

        CBUtility.log( "Default Locale is: "+ def.getDisplayName(), 1);
            
        CBUtility.log("Using Locale: "+locale.getDisplayName()  + " for " + locale.getDisplayCountry(), 4);
        CBUtility.log("language, localized for default locale is: " + def.getDisplayLanguage( locale ), 4);
        CBUtility.log("country name, localized for default locale: " + def.getDisplayCountry( locale ), 4);
        CBUtility.log("Default language, localized for your locale is: " + locale.getDisplayLanguage( def ), 4);
        CBUtility.log("Default country name, localized for your locale is: " + locale.getDisplayCountry( def ), 4);

        translations = new Hashtable(500);

        addBundle(bundleLocation, customLoader);
        
        if (!def.getLanguage().equals("en"))
            english = false;
    }

    /**
     *    Once init() has been called, this method can be used to add
     *    multiple resource bundles to the universal 'CBIntText' list of
     *    translations.  Note that clashes (i.e. translations of identical
     *    terms) are resolved with the earliest registered bundle having
     *    priority.  (i.e. if both JX and a plugin have a translation for
     *    'file', the JX one will be used).  If this is a problem, remember
     *    that the translated string is arbitrary - e.g. a plugin could
     *    translate the string 'pluginFile' instead.
     */

    public static void addBundle(String bundleLocation, ClassLoader customLoader)
    {
    
        CBUtility.log("adding resource bundle: " + bundleLocation + " using loader: " + customLoader.toString(), 4);    
    
        int startSize = translations.size();
        
        if (locale == null)
        {
            CBUtility.log(" ERROR: - CBIntText.addBundle() has been called before CBIntText was initialised! - ignoring call.");
            return;
        }
    
        try
        {
//            CBResourceBundle bundle = new CBResourceBundle( bundleLocation, locale, customLoader);
            CBResourceBundle bundle = new CBResourceBundle( bundleLocation, locale);

            String name = bundle.getString("name");
            CBUtility.log(" added language localizaton set: " +  ((name==null)?"(not named)":name), 1);
            
            // Copy the new resource set into the hashtable, unless
            // a value already exists in the hashtable... (earlier values have precedence).
            
            Enumeration keys = bundle.getKeys();
            while (keys.hasMoreElements())
            {
                Object key = keys.nextElement();
                boolean debug = (CBUtility.getLogDebugLevel() >= 8);
                if (translations.containsKey(key) == false)
                {
                    if (debug) CBUtility.log("adding key: " + key + " trans: " + bundle.getString((String)key));
                        
                    translations.put(key, bundle.getString((String)key));
                }    
            }    
            
        }
        catch ( MissingResourceException e)
        {
              CBUtility.log("unable to load resource bundle for " + locale.getDisplayLanguage( locale ) + " in country " + locale.getDisplayCountry( locale ), 1);
        }
        finally
        {
           if (startSize < translations.size())  // i.e. we added stuff...
           {
               CBUtility.log(" locale language is " + locale.getDisplayLanguage( locale ) + " in country " + locale.getDisplayCountry( locale ), 1);
           }
           else
           {
               CBUtility.log("Unable to load language resource bundle (couldn't even find default 'JX.properties' file)!", 1);
           }
        }
    }

    /**
     *    This attempts to get the localised version of a string.
     *    If anything goes wrong, it attempts to return the key
     *    string it was given - hence using a meaningfull key is
     *    a good harm minimization strategy.
     */
     
    public static String get(String key)
    {

       if (key == null)       // sanity check that a valid key has been passed.
        {
            return "null key";
        }

        if (translations==null || translations.size() == 0)   // we never opened a bundle...
        {
            if (errorGiven == false)  // only print error message once!  (otherwise we'd print an
            {                         // error for every string in the program!)
        	    if (!english) CBUtility.log("Unable to translate (" + key + ") - can't find language resource bundle.");
                errorGiven = true;
            }
            return key;        // try to keep on trucking using the (english) key phrase
        }

        String ret = null;

        try
        {
            String val = (String)translations.get(key);  // return the translated word!
            if (val == null)  // this shouldn't happen, but can occur with an out-of-date (=incomplete) translation file.
            {
                if (!english) CBUtility.log("Can't find translation for (" + key + ") - returning unchanged.",3);
                return key;
            }
            return val;
        }
        catch (MissingResourceException e)
        {
            return key;        // couldn't find a translation, so return the keyword instead.
        }
    }
}