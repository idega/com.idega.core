/*
 * Created on 20.2.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;


/**
 * Title:        SortedProperties
 * Description:  A class to read and write property files but stores them ordered by key in the property file.
 * Copyright:  (C) 2002 idega software All Rights Reserved.
 * Company:      idega software
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0  
 */
public class SortedProperties extends Properties implements SortedMap
{
	private SortedMap internalSortedMap;
	/**
	 * 
	 */
	public SortedProperties()
	{
		super();
	}
	/**
	 * @param defaults
	 */
	public SortedProperties(Properties defaults)
	{
		super(defaults);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#comparator()
	 */
	 
	 private SortedMap getInternalSortedMap(){
	 	if(internalSortedMap==null){
			internalSortedMap=new TreeMap();
	 	}
	 	return internalSortedMap;
	 }
	 
	public Comparator comparator()
	{
		return getInternalSortedMap().comparator();
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
	 */
	public SortedMap subMap(Object fromKey, Object toKey)
	{
		return getInternalSortedMap().subMap(fromKey,toKey);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#headMap(java.lang.Object)
	 */
	public SortedMap headMap(Object toKey)
	{
		return getInternalSortedMap().headMap(toKey);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#tailMap(java.lang.Object)
	 */
	public SortedMap tailMap(Object fromKey)
	{
		return getInternalSortedMap().tailMap(fromKey);
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#firstKey()
	 */
	public Object firstKey()
	{
		return getInternalSortedMap().firstKey();
	}
	/* (non-Javadoc)
	 * @see java.util.SortedMap#lastKey()
	 */
	public Object lastKey()
	{
		return getInternalSortedMap().lastKey();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public synchronized void clear()
	{
		getInternalSortedMap().clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#contains(java.lang.Object)
	 */
	public synchronized boolean contains(Object value)
	{
		//return super.contains(value);
		return getInternalSortedMap().containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public synchronized boolean containsKey(Object key)
	{
		return getInternalSortedMap().containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value)
	{
		return getInternalSortedMap().containsValue(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#elements()
	 */
	public synchronized Enumeration elements()
	{
		Vector v = new Vector(getInternalSortedMap().values());
		return v.elements();
		//return getInternalSortedMap().elements();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet()
	{
		return getInternalSortedMap().entrySet();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public synchronized boolean equals(Object o)
	{
		return getInternalSortedMap().equals(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#get(java.lang.Object)
	 */
	public synchronized Object get(Object key)
	{
		return getInternalSortedMap().get(key);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public synchronized int hashCode()
	{
		return getInternalSortedMap().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#isEmpty()
	 */
	public boolean isEmpty()
	{
		return getInternalSortedMap().isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#keys()
	 */
	public synchronized Enumeration keys()
	{
		Vector v = new Vector(keySet());
		return v.elements();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet()
	{
		return getInternalSortedMap().keySet();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized Object put(Object key, Object value)
	{
		return getInternalSortedMap().put(key, value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public synchronized void putAll(Map t)
	{
		getInternalSortedMap().putAll(t);
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#rehash()
	 */
	protected void rehash()
	{
		//getInternalSortedMap().rehash();
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#remove(java.lang.Object)
	 */
	public synchronized Object remove(Object key)
	{
		return getInternalSortedMap().remove(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Dictionary#size()
	 */
	public int size()
	{
		return getInternalSortedMap().size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public synchronized String toString()
	{
		return getInternalSortedMap().toString();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values()
	{
		return getInternalSortedMap().values();
	}

}
