/*
 * $Id: IWCacheManager2.java,v 1.10 2006/06/02 10:19:13 tryggvil Exp $ Created on
 * 6.1.2006 in project com.idega.core
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.Status;
import com.idega.idegaweb.IWMainApplication;

/**
 * <p>
 * IWCacheManager2 is a newer replacement for the older IWCacheManager class
 * and is implemented on top of the ehCache framework.
 * </p>
 * Last modified: $Date: 2006/06/02 10:19:13 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.10 $
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
		int cacheSize=1000;
		boolean overFlowToDisk=true;
		boolean isEternal=false;
		return getCache(cacheName,cacheSize,overFlowToDisk,isEternal);
	}
	
	public Map getCache(String cacheName,int cacheSize,boolean overFlowToDisk,boolean isEternal){
		long cacheTTLIdleSeconds = 1000;
		long cacheTTLSeconds=10000;
		return getCache(cacheName,cacheSize,overFlowToDisk,isEternal,cacheTTLSeconds,cacheTTLIdleSeconds);
	}
	
	public Map getCache(String cacheName,int cacheSize,boolean overFlowToDisk,boolean isEternal,long cacheTTLIdleSeconds,long cacheTTLSeconds){
		Map cm = (Map) getCacheMapsMap().get(cacheName);
		if(cm==null){
			Cache cache = getInternalCache(cacheName);
			if(cache==null){
					//cache = new IWCache(cacheName, cacheSize, overFlowToDisk, isEternal, cacheTTLSeconds, cacheTTLIdleSeconds);
					cache = new Cache(cacheName, cacheSize, overFlowToDisk, isEternal, cacheTTLSeconds, cacheTTLIdleSeconds);
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
			if(ccm.getCache().getStatus()==Status.STATUS_ALIVE){
			//if(ccm.getCache().getStatus()==Cache.STATUS_ALIVE){
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
