/*
 * Created on 3.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author laddi
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MultivaluedHashMap extends HashMap {

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		Iterator iter = values().iterator();
		while (iter.hasNext()) {
			Collection element = (Collection) iter.next();
			Iterator iterator = element.iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				if (object.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Collection getCollection(Object key) {
		return (Collection) super.get(key);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		Collection values = getCollection(key);
		if (values == null) {
			values = new ArrayList();
		}
		values.add(value);
		
		return super.put(key, values);
	}
	
	public Object put(Object key, Collection collection){
		Collection values = getCollection(key);
		if (values == null) {
			values = new ArrayList();
		}
		values.addAll(collection);

		return super.put(key, values);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		Collection values = getCollection(key);
		if (values != null) {
			Iterator iter = values.iterator();
			while (iter.hasNext()) {
				return iter.next();
			}
		}
		return null;
	}
	
	/**
	 * To bypass put method in subclasses 
	 * @param key
	 * @param value
	 * @return
	 */
	// (aron 23.08.2004)
	protected Object superPut(Object key, Object value){
	    return super.put(key,value);
	}
}