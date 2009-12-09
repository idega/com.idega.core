/*
 * $Id: CacheMap.java,v 1.13 2007/02/09 01:55:01 tryggvil Exp $
 * Created on 6.1.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;


/**
 * <p>
 * Wrapper for the Cache implemented as a standard Map
 * </p>
 *  Last modified: $Date: 2007/02/09 01:55:01 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.13 $
 */
public class CacheMap<K extends Serializable, V> implements Map<K, V> {

	private static final Logger LOGGER = Logger.getLogger(CacheMap.class.getName());
	
	private Cache cache;
	private List<CacheMapListener<K, V>> cacheListeners;

	CacheMap(Cache cache) {
		this.cache = cache;
	}
	
	Cache getCache() {
		return this.cache;
	}
	
	public int size() {
		int size = getCache().getKeysWithExpiryCheck().size();
		return size;
	}

	public boolean isEmpty() {
		int size = size();
		return size == 0;
	}

	public boolean containsKey(Object key) {
		try {
			Element element = getCache().get((Serializable) key);
			if(element!=null){
				if(element.getValue()!=null){
					return true;
				}
			}
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}
		catch (CacheException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Method containsValue not implemented");
	}

	@SuppressWarnings("unchecked")
	public V get(Object key) {
		try {
			Element element = getCache().get(key);
			if (element == null) {
				return null;
			}
			
			Object o = element.getObjectValue();
			if (o == null) {
				return null;
			}
			
			K realKey = (K) key;
			V result = (V) o;
			
			if (getCacheListeners() != null) {
				for (Iterator<CacheMapListener<K, V>> iterator = getCacheListeners().iterator(); iterator.hasNext();) {
					CacheMapListener<K, V> listener = iterator.next();
					listener.gotObject(realKey, result);
				}
			}
			
			return result;
		} catch (ClassCastException e) {
			LOGGER.log(Level.WARNING, "Error while casting", e);
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (CacheException e) {
			throw new RuntimeException(e);
		}
	}
	
	public V put(K key, V value) {
		if (key == null || value == null) {
			LOGGER.warning("Some element (key="+key+" or value="+value+") is null! Not adding element to cache.");
			return value;
		}
		
		try {
			Element element = new Element(key, value);
			getCache().put(element);
			if (getCacheListeners() != null) {
				for (Iterator<CacheMapListener<K, V>> iterator = getCacheListeners().iterator(); iterator.hasNext();) {
					CacheMapListener<K, V> listener = iterator.next();
					listener.putObject(key, value);
				}
			}
			
			return value;
		}
		catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		try {
			V realElementToRemove = null;
			Element objectToRemove = getCache().get(key);
			if (objectToRemove != null) {
				realElementToRemove = (V) objectToRemove.getObjectValue();
			}
			
			getCache().remove((Serializable) key);
			
			if (getCacheListeners() != null) {
				K realKey = (K) key;
				for (Iterator<CacheMapListener<K, V>> iterator = getCacheListeners().iterator(); iterator.hasNext();) {
					CacheMapListener<K, V> listener = iterator.next();
					listener.removedObject(realKey);
				}
			}
			
			return realElementToRemove;
		} catch (ClassCastException e) {
			LOGGER.log(Level.WARNING, "Error while casting", e);
			throw new RuntimeException(e);
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		for (Iterator<? extends K> iter = map.keySet().iterator(); iter.hasNext();) {
			K key = iter.next();
			V value = map.get(key);
			if (key != null && value != null) {
				put(key, value);
			}
		}
	}

	public void clear() {
		try {
			getCache().removeAll();
			if (getCacheListeners() != null) {
				for (Iterator<CacheMapListener<K, V>> iterator = getCacheListeners().iterator(); iterator.hasNext();) {
					CacheMapListener<K, V> listener = iterator.next();
					listener.cleared();
				}
			}
			LOGGER.info("Clearing cache: " + this.cache.getName());
		}
		catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
	}

	public Set<K> keySet() {
		Set<K> set = new HashSet<K>();
		List<K> keys;
		try {
			keys = getCache().getKeys();
			set.addAll(keys);
			return set;
		}
		catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
		catch (CacheException e) {
			throw new RuntimeException(e);
		}
	}

	public Collection<V> values() {
		Collection<V> values = new ArrayList<V>();
		for (Iterator<K> iter = keySet().iterator(); iter.hasNext();) {
			K key = iter.next();
			V value = get(key);
			if (value != null) {
				values.add(value);
			}
		}
		return values;
	}

	public Set<Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException("Method entrySet() not implemented");
	}
	
	/**
	 * @return Returns the cacheListeners.
	 */
	public List<CacheMapListener<K, V>> getCacheListeners() {
		return this.cacheListeners;
	}

	/**
	 * @param cacheListeners The cacheListeners to set.
	 */
	public void setCacheListeners(List<CacheMapListener<K, V>> cacheListeners) {
		this.cacheListeners = cacheListeners;
	}
	
	/**
	 * @return Returns the cacheListeners.
	 */
	public void addCacheListener(CacheMapListener<K, V> listener) {
		List<CacheMapListener<K, V>> cacheListeners = getCacheListeners();
		if (cacheListeners == null) {
			cacheListeners = new ArrayList<CacheMapListener<K, V>>();
			setCacheListeners(cacheListeners);
		}
		cacheListeners.add(listener);
	}
}