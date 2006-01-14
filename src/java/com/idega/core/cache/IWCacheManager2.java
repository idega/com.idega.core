/*
 * $Id: IWCacheManager2.java,v 1.2 2006/01/14 22:39:12 laddi Exp $
 * Created on 6.1.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.cache;

import java.util.Map;
import java.util.logging.Logger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * TODO tryggvil Describe Type IWCacheManager2
 * </p>
 *  Last modified: $Date: 2006/01/14 22:39:12 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class IWCacheManager2 {
	
	  private static final String IW_CACHEMANAGER_KEY = "iw_cachemanager2";
	  private Logger log = Logger.getLogger(IWCacheManager2.class.getName());
	  private CacheManager internalCacheManager;
	  
	  private static final long CACHE_NEVER_EXPIRES = -1;

	  private IWCacheManager2() {
	  }

	  public static synchronized IWCacheManager2 getInstance(IWMainApplication iwma){
	    IWCacheManager2 iwcm = (IWCacheManager2)iwma.getAttribute(IW_CACHEMANAGER_KEY);
	    if(iwcm==null){
	      iwcm = new IWCacheManager2();
	      iwma.setAttribute(IW_CACHEMANAGER_KEY,iwcm);
	    }
	    return iwcm;
	  }

	
	/**
	 * @return Returns the ehCacheManager.
	 */
	private CacheManager getInternalCacheManager() {
		if(internalCacheManager==null){
			try {
				internalCacheManager=CacheManager.create();
			}
			catch (CacheException e) {
				e.printStackTrace();
			}
		}
		return internalCacheManager;
	}
	
	private Cache getDefaultInternalCache(){
		Cache cache = getInternalCacheManager().getCache("default");
		return cache;
	}
	
	public Map getDefaultCacheMap(){
		CacheMap cacheMap = new CacheMap(getDefaultInternalCache());
		return cacheMap;
	}
	
	
	
}
