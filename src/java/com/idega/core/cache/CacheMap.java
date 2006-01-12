/*
 * $Id: CacheMap.java,v 1.1 2006/01/12 15:22:18 tryggvil Exp $
 * Created on 6.1.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;


/**
 * <p>
 * Wrapper for the Cache implemented as a standard Map
 * </p>
 *  Last modified: $Date: 2006/01/12 15:22:18 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class CacheMap implements Map {

	private Cache cache;
	
	/**
	 * 
	 */
	CacheMap(Cache cache) {
		this.cache=cache;
	}
	
	private Cache getCache(){
		return cache;
	}
	

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		int memorySize = (int) getCache().getMemoryStoreSize();
		int diskSize = (int) getCache().getMemoryStoreSize();
		return memorySize+diskSize;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		int size = size();
		return size==0;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		try {
			return getCache().getKeys().contains(key);
		}
		catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Method containsValue not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		try {
			Element element = getCache().get((Serializable) key);
			return element.getValue();
		}
		catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
		catch (CacheException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		try {
			Element element = new Element((Serializable)key,(Serializable)value);
			getCache().put(element);
			return null;
		}
		catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		try {
			getCache().remove((Serializable)key);
			return null;
		}
		catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map map) {
		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			Object value = map.get(key);
			put(key,value);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		try {
			getCache().removeAll();
		}
		catch (IllegalStateException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		Set set = new HashSet();
		List keys;
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

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values() {
		Collection values = new ArrayList();
		for (Iterator iter = keySet().iterator(); iter.hasNext();) {
			Object key = (Object) iter.next();
			Object value = get(key);
			values.add(value);
		}
		return values;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		throw new UnsupportedOperationException("Method entrySet() not implemented");
	}
}
