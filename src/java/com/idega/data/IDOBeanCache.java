/*
 * $Id: IDOBeanCache.java,v 1.15 2006/06/08 07:47:42 laddi Exp $ Crated in
 * 2002 by tryggvil
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.data;

import java.util.Collection;
import java.util.Map;
import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.caching.CacheMap;

/**
 * <p>
 * This class holds a cache for each entity (class) and datasource.
 * </p>
 * Last modified: $Date: 2006/06/08 07:47:42 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.15 $
 */
public class IDOBeanCache {

	private Class entityInterfaceClass;
	// Map cacheMap;
	private Map findQueryCacheMap;
	private Map homeQueryCacheMap;
	private String cacheName = null;
	private String datasource;

	IDOBeanCache(Class entityInterfaceClass,String datasource) {
		this.entityInterfaceClass = entityInterfaceClass;
		this.datasource=datasource;
	}

	private Map getFindQueryCacheMap() {
		if (this.findQueryCacheMap == null) {
			// findQueryCacheMap=new HashMap();
			this.findQueryCacheMap = new CacheMap(200);
		}
		return this.findQueryCacheMap;
	}

	private Map getHomeQueryCacheMap() {
		if (this.homeQueryCacheMap == null) {
			// homeQueryCacheMap=new HashMap();
			this.homeQueryCacheMap = new CacheMap(200);
		}
		return this.homeQueryCacheMap;
	}

	/**
	 * <p>
	 * Holds a Map over cached entity objects for this BeanCache. Keys are
	 * primary key object for the entities and value a (IDOEntity) Entity
	 * </p>
	 * 
	 * @return
	 */
	protected Map getCacheMap() {
		int maxCachedBeans = 200;
		boolean overFlowToDisk = true;
		boolean isEternal = false;
		/*
		 * if(this.cacheMap==null){ //cacheMap=new HashMap(); int maxCachedBeans =
		 * 200; IDOEntityDefinition entityDef; try { entityDef =
		 * (IDOEntityDefinition)
		 * IDOLookup.getEntityDefinitionForClass(entityInterfaceClass);
		 * if(entityDef!=null){ maxCachedBeans=entityDef.getMaxCachedBeans(); }
		 * //if(this.entityInterfaceClass.equals(ICObject.class)){ //
		 * maxCachedBeans = 10000; //} this.cacheMap = new
		 * CacheMap(maxCachedBeans); } catch (IDOLookupException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } } return
		 * this.cacheMap;
		 */
		IDOEntityDefinition definition = getEntityDefinition();
		isEternal=definition.isAllRecordsCached();
		maxCachedBeans = definition.getMaxCachedBeans();
		return getCacheManger().getCache(getCacheName(), maxCachedBeans, overFlowToDisk, isEternal);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCacheName
	 * </p>
	 * 
	 * @return
	 */
	private String getCacheName() {
		if (this.cacheName == null) {
			this.cacheName = "BeanCache_" + getEntityInterfaceClass().getName();
		}
		return this.cacheName;
	}

	protected IWCacheManager2 getCacheManger() {
		return IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication());
	}

	IDOEntity getCachedEntity(Object pk) {
		return (IDOEntity) getCacheMap().get(pk);
	}

	void putCachedEntity(Object pk, IDOEntity entity) {
		getCacheMap().put(pk, entity);
	}

	void removeCachedEntity(Object pk) {
		getCacheMap().remove(pk);
	}

	/**
	 * <p>
	 * Returns a Collectino of all cached entity objects in the bean cache for
	 * this BeanCache.
	 * </p>
	 * 
	 * @return
	 */
	protected Collection getCachedEntities() {
		return getCacheMap().values();
	}

	void putCachedFindQuery(String querySQL, Collection pkColl) {
		getFindQueryCacheMap().put(querySQL, pkColl);
	}

	Collection getCachedFindQuery(String querySQL) {
		return (Collection) getFindQueryCacheMap().get(querySQL);
	}

	boolean isFindQueryCached(String queryString) {
		return (getFindQueryCacheMap().get(queryString) != null);
	}

	void putCachedHomeQuery(String querySQL, Object objectToCache) {
		getHomeQueryCacheMap().put(querySQL, objectToCache);
	}

	Object getCachedHomeQuery(String querySQL) {
		return getHomeQueryCacheMap().get(querySQL);
	}

	boolean isHomeQueryCached(String queryString) {
		return (getHomeQueryCacheMap().get(queryString) != null);
	}

	synchronized void flushAllHomeQueryCache() {
		this.homeQueryCacheMap = null;
	}

	synchronized void flushAllFindQueryCache() {
		this.findQueryCacheMap = null;
	}

	synchronized void flushAllBeanCache() {
		// this.cacheMap=null;
		getCacheMap().clear();
	}

	synchronized void flushAllQueryCache() {
		flushAllFindQueryCache();
		flushAllHomeQueryCache();
	}

	/**
	 * @return the entityInterfaceClass
	 */
	protected Class getEntityInterfaceClass() {
		return this.entityInterfaceClass;
	}

	/**
	 * @param entityInterfaceClass
	 *            the entityInterfaceClass to set
	 */
	protected void setEntityInterfaceClass(Class entityInterfaceClass) {
		this.entityInterfaceClass = entityInterfaceClass;
	}

	protected IDOEntityDefinition getEntityDefinition() {
		try {
			return IDOLookup.getEntityDefinitionForClass(getEntityInterfaceClass());
		}
		catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * @return the dataSource
	 */
	protected String getDatasource() {
		return this.datasource;
	}

	
	/**
	 * @param dataSource the dataSource to set
	 */
	protected void setDatasource(String datasource) {
		this.datasource = datasource;
	}
}
