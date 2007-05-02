/*
 * $Id: IDOBeanCache.java,v 1.15.2.1 2007/05/02 14:40:55 thomas Exp $ Crated in
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

/**
 * <p>
 * This class holds a cache for each entity (class) and datasource.
 * </p>
 * Last modified: $Date: 2007/05/02 14:40:55 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.15.2.1 $
 */
public class IDOBeanCache {

	private String cacheName = null;
	private String findQueryCacheName = null;
	private String homeQueryCacheName = null;
	
	private Class entityInterfaceClass = null;
	private String datasource = null;
	private boolean isEternal = false;
	private int maxCachedBeans = -1;
		

	IDOBeanCache(Class entityInterfaceClass,String datasource) {
		initialize(entityInterfaceClass, datasource);
	}
	
	private void initialize(Class entityInterfaceClass, String datasource) {
		this.entityInterfaceClass = entityInterfaceClass;
		this.datasource = datasource;
		IDOEntityDefinition definition = getEntityDefinition();
		isEternal = definition.isAllRecordsCached();
		maxCachedBeans = definition.getMaxCachedBeans();
	}

	private Map getFindQueryCacheMap() {
		return getCacheMap(getFindQueryCacheName());
	}
	
	private Map getHomeQueryCacheMap() {
		return getCacheMap(getHomeQueryCacheName());
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
		return getCacheMap(getCacheName());
	}

	private Map getCacheMap(String nameOfCache) {
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
		return getCacheManger().getCache(nameOfCache, maxCachedBeans, true, isEternal);
	}
	
	private String getCacheName() {
		if (cacheName == null) {
			cacheName = "BeanCache_" + getEntityInterfaceClass().getName();
		}
		return cacheName;
	}
	
	private String getFindQueryCacheName() {
		if (findQueryCacheName == null) {
			findQueryCacheName = "QueryCache_" + getEntityInterfaceClass().getName();
		}
		return findQueryCacheName;
	}
	
	private String getHomeQueryCacheName() {
		if (homeQueryCacheName == null) {
			homeQueryCacheName = "HomeQueryCache_" + getEntityInterfaceClass().getName();
		}
		return homeQueryCacheName;
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
		getHomeQueryCacheMap().clear();
	}

	synchronized void flushAllFindQueryCache() {
		getFindQueryCacheMap().clear();
	}

	synchronized void flushAllBeanCache() {
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
