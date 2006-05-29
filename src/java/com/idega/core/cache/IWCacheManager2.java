/*
 * $Id: IWCacheManager2.java,v 1.8 2006/05/29 18:17:11 tryggvil Exp $ Created on
 * 6.1.2006 in project com.idega.core
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.ObjectExistsException;
import com.idega.idegaweb.IWMainApplication;

/**
 * <p>
 * TODO tryggvil Describe Type IWCacheManager2
 * </p>
 * Last modified: $Date: 2006/05/29 18:17:11 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.8 $
 */
public class IWCacheManager2 {

	private static final String IW_CACHEMANAGER_KEY = "iw_cachemanager2";
	private CacheManager internalCacheManager;
	private Map cacheMapsMap;

	private IWCacheManager2() {
	}

	public static synchronized IWCacheManager2 getInstance(IWMainApplication iwma) {
		IWCacheManager2 iwcm = (IWCacheManager2) iwma.getAttribute(IW_CACHEMANAGER_KEY);
		if (iwcm == null) {
			iwcm = new IWCacheManager2();
			iwma.setAttribute(IW_CACHEMANAGER_KEY, iwcm);
		}
		return iwcm;
	}

	/**
	 * @return Returns the ehCacheManager.
	 */
	private CacheManager getInternalCacheManager() {
		if (this.internalCacheManager == null) {
			try {
				this.internalCacheManager = CacheManager.create();
			}
			catch (CacheException e) {
				e.printStackTrace();
			}
		}
		return this.internalCacheManager;
	}
	
	private Cache getInternalCache(String cacheName){
		return getInternalCacheManager().getCache(cacheName);
	}

	private Cache getDefaultInternalCache() {
		Cache cache = getInternalCache("default");
		return cache;
	}

	public Map getDefaultCacheMap() {
		CacheMap cacheMap = new CacheMap(getDefaultInternalCache());
		return cacheMap;
	}

	/**
	 * @return Returns the cacheMapsMap.
	 */
	private Map getCacheMapsMap() {
		if(this.cacheMapsMap==null){
			this.cacheMapsMap=new HashMap();
		}
		return this.cacheMapsMap;
	}

	
	/**
	 * @param cacheMapsMap The cacheMapsMap to set.
	 */
	/*private void setCacheMapsMap(Map cacheMapsMap) {
		this.cacheMapsMap = cacheMapsMap;
	}*/

	public Map getCache(String cacheName){
		Map cm = (Map) getCacheMapsMap().get(cacheName);
		if(cm==null){
			Cache cache = getInternalCache(cacheName);
			if(cache==null){
					int cacheSize=1000;
					long cacheTTLIdleSeconds = 1000;
					long cacheTTLSeconds=10000;
					cache = new IWCache(cacheName, cacheSize, true, false, cacheTTLSeconds, cacheTTLIdleSeconds);
	    			try {
						getInternalCacheManager().addCache(cache);
					}
					catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (ObjectExistsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (CacheException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			cm = new CacheMap(cache);
			getCacheMapsMap().put(cacheName,cm);
		}
		else{
			CacheMap ccm = (CacheMap)cm;
			//the status must be alive
			if(ccm.getCache().getStatus()==Cache.STATUS_ALIVE){
				return cm;
			}
			else{
				//if status is not alive try to re-create tha cache
				synchronized (this) {
					getCacheMapsMap().remove(cacheName);
					getInternalCacheManager().removeCache(cacheName);
					return getCache(cacheName);
				}
			}
		}
		return cm;
	}
	
	public void reset(){
		synchronized(this){
			Map internalCaches = getCacheMapsMap();
			Set cacheKeys = internalCaches.keySet();
			for (Iterator iter = cacheKeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Map cm = (Map) internalCaches.get(key);
				cm.clear();
			}
			//this.internalCacheManager=null;
			//this.cacheMapsMap=null;
		}
	}
	
}
