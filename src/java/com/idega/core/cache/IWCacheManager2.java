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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
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

	private IWCacheManager2() {}

	public static IWCacheManager2 getInstance(IWMainApplication iwma) {
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
			InputStream streamToConfiguration = null;
			try {
				streamToConfiguration = IOUtil.getStreamFromJar(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER, "WEB-INF/ehcache.xml");
				this.internalCacheManager = CacheManager.create(streamToConfiguration);
			} catch (CacheException e) {
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

	public <K extends Serializable, V> Map<K, V> getDefaultCacheMap() {
		return getCache(
				Cache.DEFAULT_CACHE_NAME,
				DEFAULT_CACHE_SIZE,
				MemoryStoreEvictionPolicy.LFU,
				DEFAULT_OVERFLOW_TO_DISK,
				DEFAULT_ETERNAL,
				DEFAULT_CACHE_TTL_SECONDS,
				DEFAULT_CACHE_TTL_IDLE_SECONDS,
				null,
				null,
				true,
				null,
				null
		);
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

	public <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, boolean overFlowToDisk, boolean isEternal,
			long cacheTTLSeconds) {

		if (cacheTTLSeconds < 0)
			cacheTTLSeconds = DEFAULT_CACHE_TTL_SECONDS;

		return getCache(cacheName, cacheSize, overFlowToDisk, isEternal, DEFAULT_CACHE_TTL_IDLE_SECONDS, cacheTTLSeconds, Boolean.TRUE);
	}

	public <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, boolean overFlowToDisk, boolean isEternal,
			long cacheTTLIdleSeconds, long cacheTTLSeconds) {
		return getCache(cacheName, cacheSize, overFlowToDisk, isEternal, cacheTTLIdleSeconds, cacheTTLSeconds, Boolean.TRUE);
	}

	public <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, boolean overFlowToDisk, boolean isEternal,
			long cacheTTLIdleSeconds, long cacheTTLSeconds, boolean resetable) {
		return getCache(cacheName, cacheSize, MemoryStoreEvictionPolicy.LFU, overFlowToDisk, isEternal, cacheTTLIdleSeconds, cacheTTLSeconds, null,
				null, resetable, null, null);
	}

	public <K extends Serializable, V> Map<K, V> getCache(String cacheName, int cacheSize, boolean overFlowToDisk, boolean isEternal,
			long cacheTTLIdleSeconds, long cacheTTLSeconds, boolean resetable, CacheMapListener<K, V> cacheListener,
			CacheMapGuardian<K, V> cacheGuardian) {
		return getCache(cacheName, cacheSize, MemoryStoreEvictionPolicy.LFU, overFlowToDisk, isEternal, cacheTTLIdleSeconds, cacheTTLSeconds, null,
				null, resetable, cacheListener, cacheGuardian);
	}

	private synchronized <K extends Serializable, V extends Object> Map<K, V> getCache(
			String cacheName,
			int cacheSize,
			MemoryStoreEvictionPolicy memoryPolicy,
			boolean overFlowToDisk,
			boolean isEternal,
			long cacheTTLIdleSeconds,
			long cacheTTLSeconds,
			RegisteredEventListeners registeredEventListeners,
			BootstrapCacheLoader bootstrapCacheLoader,
			boolean resetable,
			CacheMapListener<K, V> cacheListener,
			CacheMapGuardian<K, V> cacheGuardian
	) {
		Cache cache = getInternalCache(cacheName);
		if (cache == null) {
			try {
				isEternal = cacheTTLSeconds > 0 || cacheTTLIdleSeconds > 0 ? false : isEternal;
				CacheConfiguration config = new CacheConfiguration(cacheName, cacheSize);
				config.setMemoryStoreEvictionPolicyFromObject(memoryPolicy);

				PersistenceConfiguration persistenceConfig = new PersistenceConfiguration();
				persistenceConfig.setStrategy(overFlowToDisk ? Strategy.LOCALTEMPSWAP.name() : Strategy.NONE.name());
				config.addPersistence(persistenceConfig);

				config.setEternal(isEternal);
				config.setTimeToLiveSeconds(cacheTTLSeconds);
				config.setTimeToIdleSeconds(cacheTTLIdleSeconds);
				cache = new Cache(config, registeredEventListeners, bootstrapCacheLoader);
				getInternalCacheManager().addCache(cache);

				return new CacheMap<K, V>(cache, cacheName, resetable, cacheListener, cacheGuardian);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error creating cache: " + cacheName, e);
			}

		} else if (cache.getStatus() == Status.STATUS_ALIVE) {
			//	Checking if cache is alive
			return new CacheMap<K, V>(cache, cacheName, resetable, cacheListener, cacheGuardian);
		}

		//	If status is not alive try to re-create the cache
		getInternalCacheManager().removeCache(cacheName);
		return getCache(cacheName, cacheSize, memoryPolicy, overFlowToDisk, isEternal, cacheTTLIdleSeconds, cacheTTLSeconds, registeredEventListeners, bootstrapCacheLoader, resetable, cacheListener, cacheGuardian);
	}

	public void invalidate(String cacheName) {
		CacheManager manager = getInternalCacheManager();
		Cache cache = manager.getCache(cacheName);
		if (cache != null)
			cache.removeAll();

		manager.removeCache(cacheName);
	}

	public void reset() {
		CacheManager manager = getInternalCacheManager();
			String[] cacheKeys = manager.getCacheNames();
			for (String cacheName: cacheKeys) {
				Cache cache = manager.getCache(cacheName);
				if (cache != null)
					cache.removeAll();
			}
	}

	/**
	 * <p>
	 * Shuts down and de-allocates this cache manager
	 * </p>
	 */
	public void shutdown() {
		try {
			getInternalCacheManager().shutdown();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}