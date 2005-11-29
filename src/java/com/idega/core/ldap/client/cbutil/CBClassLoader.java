package com.idega.core.ldap.client.cbutil;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.zip.ZipException;

/**
 * Title:        test
 * Description:  See if we can get this crappy IDE to work properly just once.
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Chris Betts
 * @version 1.0
 */

public class CBClassLoader extends ClassLoader
{
    
    /**
     * a cached list of classes to speed the return of twice loaded classes.
     */

    protected Hashtable classes = new Hashtable();
    protected Hashtable lowerCaseClasses = new Hashtable();

    /**
     *  The resource loader provides the interface to a group of zip files.
     */

    protected CBResourceLoader resourceLoader;

    /**
     * Constructor - note that that the class is useless until at least one resource file has been
     * registered with it using the addResource() method.
     */

    public CBClassLoader(CBResourceLoader loader)
    {
        CBUtility.log("Started CBClassLoader", 7);

        resourceLoader = loader;
    }

    /**
     * Translates the '.' seperators of Class package names into the \ seperators needed for
     * the internal directory structure of the zip file.
     */

    protected String translateClassName(String name)
    {
        if (name.endsWith(".class"))
           name = name.replace('.','/');
        else
            name = name.replace('.','/') + ".class";

        CBUtility.log("looking for class: " + name, 8);
        return name;
    }

    /**
     * This sample function for reading class implementations reads
     * them from the local file system
     */

    private byte[] getClassFromResourceFiles(String className)
        throws ZipException
    {
        className = translateClassName(className);
        return resourceLoader.getResource(className);
    }

    /**
     * This is a simple version for external clients since they
     * will always want the class resolved before it is returned
     * to them.
     */
    public Class findClass(String className) throws ClassNotFoundException
    {
        return (findClass(className, true));
    }


    /**
     * This is the required version of findClass which is called
     * both from findClass above and from the internal function
     * loadClass of the parent.
     */

    public synchronized Class findClass(String className, boolean resolveIt)
        throws ClassNotFoundException
    {
        Class result;
        byte  classData[];

        CBUtility.log("        >>>>>> Load class : "+className,8);

        /* Check our local cache of classes */
        Object local = classes.get(className);
        if (local != null)
        {
            if (local instanceof String && "".equals(local))
            {
                CBUtility.log("        >>>>>> ignoring '" + className + "' (failed to load previously).", 8);
                throw new ClassNotFoundException("ignoring class '" + className + "' (failed to load previously).");
            }
            CBUtility.log("        >>>>>> returning cached result.", 8);
            return (Class)local;
        }

        /* Check with the primordial class loader */
        try {
            result = super.findSystemClass(className);
            CBUtility.log("        >>>>>> returning system class (in CLASSPATH).", 8);
            return result;
        } catch (ClassNotFoundException e) {
            CBUtility.log("        >>>>>> Not a system class - looking in zip files.", 8);
        }

        /* Try to load it from our repository */
        try
        {
            classData = getClassFromResourceFiles(className);
        }
        catch (ZipException e)
        {
            classes.put(className, "");   // stick a dummy entry in as a warning to others...
            lowerCaseClasses.put(className.toLowerCase(), "");
            throw new ClassNotFoundException("Error getting className: '" + className + "' : " + e);
        }

        if (classData == null)
        {
            classes.put(className, "");   // stick a dummy entry in as a warning to others...
            lowerCaseClasses.put(className.toLowerCase(), "");
            throw new ClassNotFoundException();
        }

        /* Define it (parse the class file) */
        result = defineClass(className, classData, 0, classData.length);
        if (result == null) {
            classes.put(className, "");   // stick a dummy entry in as a warning to others...
            lowerCaseClasses.put(className.toLowerCase(), "");
            throw new ClassFormatError();
        }

        if (resolveIt) {
            resolveClass(result);
        }

        classes.put(className, result);
        lowerCaseClasses.put(className.toLowerCase(), result);
        CBUtility.log("        >>>>>> Returning newly loaded zipped class. " + className, 8);
        return result;
    }
    
    public URL getResource(String name) 
    {
        URL bloop = super.getResource(name);
        return bloop;
    }
    
    /**
     *    Returns a 'jar url' to the specified resource.
     *    @param name the name of the resource to look for (e.g. 'HelpSet.hs')
     *    @return the url of the resource, (e.g. 'jar:file:myjarfile.jar!/HelpSet.hs'.
     *            - this will be null if the resource cannot be found in the known 
     *            jar file.
     */
     
    protected URL findResource(String name)
    {
        CBUtility.log("CLASSLOADER MAGIC: looking for: " + name,8);
        CBJarResource container = resourceLoader.getJarContainingResource(name);
        CBUtility.log("CLASSLOADER MAGIC: found container: " + ((container==null)?"null":container.getZipFileName()), 8);
        if (container==null) 
            return null;
            
        String zipFile = container.getZipFileName();
        String url = "jar:file:" + zipFile + "!/" + name;
        CBUtility.log("CLASSLOADER MAGIC: constructed url: " + url, 8);
        
        try
        {
            return new URL(url);                                            
        }
        catch (MalformedURLException e)
        {
            CBUtility.log("Unable to construct url: " +url + "\n -> due to " + e, 1);
            return null;
        }    
    }
    
    public String toString()
    {
        return "CBClassLoader";
    }
    
    public InputStream getResourceAsStream(String name)    
    {
        return super.getResourceAsStream(name);    
    }
}