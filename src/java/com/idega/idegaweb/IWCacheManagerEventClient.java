/*
 * $Id: IWCacheManagerEventClient.java,v 1.2 2007/05/10 22:34:28 thomas Exp $
 * Created on Jan 10, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.MethodCallEventGenerator;
import com.idega.core.event.MethodCallEventHandler;
import com.idega.core.event.impl.EventClient;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLegacyEntity;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:34:28 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */

public class IWCacheManagerEventClient extends EventClient implements MethodCallEventGenerator, MethodCallEventHandler {
	
	private IWCacheManager cacheManager = null;
	
	public IWCacheManagerEventClient(IWCacheManager cacheManager) {
		this.cacheManager = cacheManager;
		initialize(IWCacheManager.class);
	}
	
	public void handleEvent(MethodCallEvent methodCallEvent) {
		if (isEventCompatible(methodCallEvent)) {
			clearAllCaches(methodCallEvent);
			deleteFromCachedTable(methodCallEvent);
			invalidateCache(methodCallEvent);
			invalidateCacheWithPartialKey(methodCallEvent);
			removeCachedEntity(methodCallEvent);
			removeTableFromCache(methodCallEvent);
		}
	}
	
	// start list of pairs 
	// event firing methods and event handling methods 
	
	// pair 1
	
	private static final String CLEAR_ALL_CACHES = "clearAllCaches";
	
	public void clearAllCaches() {
		if (isNothingToDo()) return;
		fireEvent(CLEAR_ALL_CACHES);
	}
	
	public void clearAllCaches(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, CLEAR_ALL_CACHES)) {
			cacheManager.clearAllCaches();
		}
	}
	
	// pair 2
	
	private static final String DELETE_FROM_CACHED_TABLE = "deleteFromCachedTable";
	private static final String ENTITY_NAME = "entityName";
	
	public void deleteFromCachedTable(IDOLegacyEntity entity) {
		if (isNothingToDo()) return; 
		Class entityNameInterface = com.idega.data.IDOLookup.getInterfaceClassFor(entity.getClass());
		fireEvent(DELETE_FROM_CACHED_TABLE, ENTITY_NAME, entityNameInterface.getName());
	}
	
	public void deleteFromCachedTable(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, DELETE_FROM_CACHED_TABLE)) {
			String entityClassString = methodCallEvent.get(ENTITY_NAME);
			IDOLegacyEntity entity = (IDOLegacyEntity) GenericEntity.getStaticInstance(entityClassString);
			cacheManager.deleteFromCachedTable(entity);
		}
	}
	
	// pair 3
	
	private static final String INVALIDATE_CACHE = "invalidateCache";
	private static final String KEY_NAME = "keyName";
	
	public void invalidateCache(String key) {
		if (isNothingToDo()) return;
		fireEvent(INVALIDATE_CACHE, KEY_NAME, key);
	}
	
	public void invalidateCache(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, INVALIDATE_CACHE)) {
			cacheManager.invalidateCache(methodCallEvent.get(KEY_NAME));
		}
	}
	
	// pair 4

	private static final String INVALIDATE_CACHE_WITH_PARTIAL_KEY = "invalidateCacheWithPartialKey";
	private static final String PARTIAL_KEY_NAME = "partialKeyName";
	
	public void invalidateCacheWithPartialKey(String key, String partialKey) {
		if (isNothingToDo()) return;
		fireEvent(INVALIDATE_CACHE_WITH_PARTIAL_KEY, KEY_NAME, key, PARTIAL_KEY_NAME, partialKey);
	}
	
	public void invalidateCacheWithPartialKey(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, INVALIDATE_CACHE_WITH_PARTIAL_KEY)) {
			cacheManager.invalidateCacheWithPartialKey(methodCallEvent.get(KEY_NAME), methodCallEvent.get(PARTIAL_KEY_NAME));
		}
	}
	
	// pair 5
	
	private static final String REMOVE_CACHED_ENTITY = "removeCachedEntity";
	private static final String CACHE_KEY_NAME = "cacheKeyName";
	
	public void removeCachedEntity(String cacheKey) {
		if (isNothingToDo()) return;
		fireEvent(REMOVE_CACHED_ENTITY, CACHE_KEY_NAME, cacheKey);
	}
	
	public void removeCachedEntity(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, REMOVE_CACHED_ENTITY)) {
			cacheManager.removeCachedEntity(methodCallEvent.get(CACHE_KEY_NAME));
		}
	}
	
	// pair 6
	
	private static final String REMOVE_TABLE_FROM_CACHE = "removeTableFromCache";
	private static final String ENTITTY_CLASS_NAME = "entityClassName";
	
	public void removeTableFromCache(Class entityClass) {
		if (isNothingToDo()) return;
		String className = entityClass.getName();
		fireEvent(REMOVE_TABLE_FROM_CACHE, ENTITTY_CLASS_NAME, className);
	}
	
	public void removeTableFromCache(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, REMOVE_TABLE_FROM_CACHE)) {
			String className = methodCallEvent.get(ENTITTY_CLASS_NAME);
			Class entityClass;
			try {
				entityClass = Class.forName(className);
			}
			catch (ClassNotFoundException e) {
				// do nothing
				return;
			}
			cacheManager.removeTableFromCache(entityClass);
		}
	}

	// end of list of pairs
	

	
	
}
