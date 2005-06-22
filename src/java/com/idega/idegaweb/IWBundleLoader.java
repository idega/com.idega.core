/*
 * $Id: IWBundleLoader.java,v 1.1 2005/06/22 12:27:27 tryggvil Exp $
 * Created on 31.5.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import javax.faces.FacesException;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 * This is the class responsible for loading the bundles (the new jar method) for the IWMainApplication instance.
 * </p>
 *  Last modified: $Date: 2005/06/22 12:27:27 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class IWBundleLoader {

	private static final Log log = LogFactory.getLog(IWBundleLoader.class);
	
	IWMainApplication iwma;
	ServletContext _externalContext;
	/**
	 * 
	 */
	public IWBundleLoader(IWMainApplication iwma,ServletContext sContext) {
		this.iwma=iwma;
		_externalContext=sContext;
	}
	
	private ServletContext getExternalContext(){
		return _externalContext;
	}
	
	public void loadBundlesFromJars()
    {
        Set jars = getExternalContext().getResourcePaths("/WEB-INF/lib/");
        if (jars != null)
        {
            for (Iterator it = jars.iterator(); it.hasNext();)
            {
                String path = (String) it.next();
                if (path.toLowerCase().endsWith(".jar"))
                {
                    feedJarConfig(path);
                }
            }
        }
    }

    private void feedJarConfig(String jarPath)
        throws FacesException
    {
        try
        {
            // not all containers expand archives, so we have to do it the generic way:
            // 1. get the stream from external context
            InputStream in = getExternalContext().getResourceAsStream(jarPath);
            if (in == null)
            {
                if (jarPath.startsWith("/"))
                {
                    in = getExternalContext().getResourceAsStream(jarPath.substring(1));
                } else
                {
                    in = getExternalContext().getResourceAsStream("/" + jarPath);
                }
            }
            if (in == null)
            {
                log.error("Resource " + jarPath + " not found");
                return;
            }

            // 2. search the jar stream for META-INF/faces-config.xml
            JarInputStream jar = new JarInputStream(in);
            JarEntry entry = jar.getNextJarEntry();
            boolean found = false;

            while (entry != null)
            {
                if (entry.getName().equals("properties/bundle.pxml"))
                {
                    if (log.isDebugEnabled()) log.debug("bundle.pxml found in " + jarPath);
                    found = true;
                    break;
                }
                entry = jar.getNextJarEntry();
            }
            jar.close();

            File tmp = null;

            // 3. if faces-config.xml was found, extract the jar and copy it to a temp file; hand over the temp file
            // to the parser and delete it afterwards
            if (found)
            {
                tmp = File.createTempFile("idegaweb", ".jar");
                in = getExternalContext().getResourceAsStream(jarPath);
                FileOutputStream out = new FileOutputStream(tmp);
                byte[] buffer = new byte[4096];
                int r;

                while ((r = in.read(buffer)) != -1)
                {
                    out.write(buffer, 0, r);
                }
                out.close();

                JarFile jarFile = new JarFile(tmp);
                try
                {
                    JarEntry configFile = jarFile.getJarEntry("properties/bundle.pxml");
                    if (configFile != null)
                    {
                        if (log.isDebugEnabled()) log.debug("bundle.pxml found in jar " + jarPath);
                        InputStream stream = jarFile.getInputStream(configFile);
                        String systemId = "jar:" + tmp.toURL() + "!/" + configFile.getName();
                        if (log.isInfoEnabled()) log.info("Reading config " + systemId);
                        //_dispenser.feed(_unmarshaller.getFacesConfig(stream, systemId));
                        loadBundle(stream,jarPath,systemId);
                    }
                } finally
                {
                    jarFile.close();
                    tmp.delete();
                }
            } else
            {
                if (log.isDebugEnabled()) log.debug("Jar " + jarPath + " contains no faces-config.xml");
            }
        } catch (Exception e)
        {
            throw new FacesException(e);
        }
    }

	/**
	 * <p>
	 * TODO tryggvil describe method loadBundle
	 * </p>
	 * @param stream
	 * @param jarPath
	 * @param systemId
	 */
	private void loadBundle(InputStream stream, String jarPath, String systemId) {
		// TODO Auto-generated method stub
		
		//IWBundle bundle = new DefaultIWBundle(stream,jarPath,systemId);
		
		System.out.println("Found idegaweb bundle in jar: "+jarPath+" and systemId: "+systemId);
		
		//iwma.getBundle()
		
	}

    
    
    
}
