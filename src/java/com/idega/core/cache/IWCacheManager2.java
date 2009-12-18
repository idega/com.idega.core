/*
 * $Id: IWCacheManager2.java,v 1.13 2009/06/04 07:30:04 valdas Exp $ Created on
 * 6.1.2006 in project com.idega.core
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.core.cache;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;
import com.idega.util.IOUtil;

/**
 * <p>
 * IWCacheManager2 is a newer replacement for the older IWCacheManager class
 * and is implemented on top of the ehCache framework.
 * </p>
 * Last modified: $Date: 2009/06/04 07:30:04 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.13 $
 */
public class IWCacheManager2 {

	private static final Logger LOGGER = Logger.getLogger(IWCacheManager2.class.getName());
	
	private static final String IW_CACHEMANAGER_KEY = "iw_cachemanager2";
	
	public static final int DEFAULT_CACHE_SIZE = 1000;
	public static final Boolean DEFAULT_OVERFLOW_TO_DISK = Boolean.TRUE;
	public static final Boolean DEFAULT_ETERNAL = Boolean.FALSE;
	public static final int DEFAULT_CACHE_TTL_IDLE_SECONDS = 1000;
	public static final int DEFAULT_CACHE_TTL_SECONDS = 10000;
	
	private CacheManager internalCacheManager;
	private Map<String, CacheMap> cacheMapsMap;

	private IWCacheManager2() {}

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
	public CacheManager getInternalCacheManager() {
		if (this.internalCacheManager == null) {
			System.setProperty(Cache.NET_SF_EHCACHE_USE_CLASSIC_LRU, Boolean.TRUE.toString());
			
			InputStream streamToConfiguration = null;
			try {
				streamToConfiguration = IOUtil.getStreamFromJar(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER, "WEB-INF/ehcache.xml");
				this.internalCacheManager = CacheManager.create(streamToConfiguration);
			}
			catch (CacheException e) {
				e.printStackTrace();
			} finally {
				IOUtil.close(streamToConfiguration);
			}
		}
		return this.internalCacheManager;
	}
	
	private Cache getInternalCache(String cacheName){
		return getInternalCacheManager().getCache(cacheName);
	}

	private Cache getDefaultInternalCache() {
		Cache cache = getInternalCache(Cache.DEFAULT_CACHE_NAME);
		return cache;
	}

	public <K extends Serializable, V> Map<K, V> getDefaultCacheMap() {
		CacheMap<K, V> cacheMap = new CacheMap<K, V>(getDefaultInternalCache());
		return cacheMap;
	}

	/**
	 * @return Returns the cacheMapsMap.
	 */
	private Map<String, CacheMap> getCacheMapsMap() {
		if (this.cacheMapsMap == null) {
			this.cacheMapsMap = new HashMap<String, CacheMap>();
		}
		return this.cacheMapsMap;
	}

	public <K extends Serializable, V> Map<K, V> getCache(String cacheName) {
		return getCache(cacheName, -1);
	}
	
	public <K extends Serializable, V> Map<K, V> getCache(String cacheName, long cacheTTLSeconds) {
		return getCache(cacheName, DEFAULT_CACHE_SIZE, DEFAULT_OVERFLOW_TO_DISK, DEFAULT_ETERNAL, cacheTTLSeconds);
	}
	
	public <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, boolean overFlowToDisk, boolean isEternal) {
		return getCache(cacheName, cacheSize, overFlowToDisk, isEternal, -1);
	}
	
	private <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, boolean overFlowToDisk, boolean isEternal, long cacheTTLSeconds) {
		if (cacheTTLSeconds < 0) {
			cacheTTLSeconds = DEFAULT_CACHE_TTL_SECONDS;
		}
		return getCache(cacheName, cacheSize, overFlowToDisk, isEternal, DEFAULT_CACHE_TTL_IDLE_SECONDS, cacheTTLSeconds);
	}
	
	public <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, boolean overFlowToDisk, boolean isEternal,
			long cacheTTLIdleSeconds, long cacheTTLSeconds) {
		return getCache(cacheName, cacheSize, MemoryStoreEvictionPolicy.LRU, overFlowToDisk, isEternal, cacheTTLIdleSeconds, cacheTTLSeconds, null, null);
	}
	
	private synchronized <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, MemoryStoreEvictionPolicy memoryPolicy,
			boolean overFlowToDisk, boolean isEternal, long cacheTTLIdleSeconds, long cacheTTLSeconds, RegisteredEventListeners registeredEventListeners,
            BootstrapCacheLoader bootstrapCacheLoader) {
		
		CacheMap<K, V> cm = getCacheMapsMap().get(cacheName);
		if (cm == null) {
			try {
				Cache cache = getInternalCache(cacheName);
				if (cache == null) {
					cache = new Cache(cacheName, cacheSize, memoryPolicy, overFlowToDisk, null, isEternal, cacheTTLSeconds, cacheTTLIdleSeconds, overFlowToDisk,
							cacheTTLIdleSeconds, registeredEventListeners, bootstrapCacheLoader);
					getInternalCacheManager().addCache(cache);
				}
				cm = new CacheMap<K, V>(cache);
				getCacheMapsMap().put(cacheName, cm);
			}
			catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error creating cache: " + cacheName, e);
			}
		}
		else{
			CacheMap<K, V> ccm = cm;
			//the status must be alive
			if (ccm.getCache().getStatus()==Status.STATUS_ALIVE) {
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
			Map<String, CacheMap> internalCaches = getCacheMapsMap();
			Set<String> cacheKeys = internalCaches.keySet();
			for (Iterator<String> iter = cacheKeys.iterator(); iter.hasNext();) {
				String key = iter.next();
				CacheMap<?, ?> cm = internalCaches.get(key);
				cm.clear();
			}
		}
	}
	
	/**
	 * <p>
	 * Shuts down and de-allocates this cache manager
	 * </p>
	 */
	public void shutdown(){
		try{
			getInternalCacheManager().shutdown();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}