/*
 * $Id: PresentationObjectComponentFacetMap.java,v 1.3 2006/04/09 12:13:13 laddi Exp $
 * Created on 14.11.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.faces.component.UIComponent;

/**
 * Overrided from JSFs standard FacetsMap because of the clone() issue.
 * 
 * Last modified: $Date: 2006/04/09 12:13:13 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.3 $
 */
public class PresentationObjectComponentFacetMap implements Map, Serializable,Cloneable{

	private UIComponent _component;

	private Map _map = new HashMap();

	public PresentationObjectComponentFacetMap(UIComponent component) {
		this._component = component;
	}

	public int size() {
		return this._map.size();
	}

	public void clear() {
		this._map.clear();
	}

	public boolean isEmpty() {
		return this._map.isEmpty();
	}

	public boolean containsKey(Object key) {
		checkKey(key);
		return this._map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		checkValue(value);
		return false;
	}

	public Collection values() {
		return this._map.values();
	}

	public void putAll(Map t) {
		for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			put(entry.getKey(), entry.getValue());
		}
	}

	public Set entrySet() {
		return this._map.entrySet();
	}

	public Set keySet() {
		return this._map.keySet();
	}

	public Object get(Object key) {
		checkKey(key);
		return this._map.get(key);
	}

	public Object remove(Object key) {
		checkKey(key);
		UIComponent facet = (UIComponent) this._map.remove(key);
		if (facet != null) {
			facet.setParent(null);
		}
		return facet;
	}

	public Object put(Object key, Object value) {
		checkKey(key);
		checkValue(value);
		setNewParent((String) key, (UIComponent) value);
		return this._map.put(key, value);
	}

	private void setNewParent(String facetName, UIComponent facet) {
		UIComponent oldParent = facet.getParent();
		if (oldParent != null) {
			oldParent.getFacets().remove(facetName);
		}
		facet.setParent(this._component);
	}

	private void checkKey(Object key) {
		if (key == null) {
			throw new NullPointerException("key");
		}
		if (!(key instanceof String)) {
			throw new ClassCastException("key is not a String");
		}
	}

	private void checkValue(Object value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		if (!(value instanceof UIComponent)) {
			throw new ClassCastException("value is not a UIComponent");
		}
	}


	public Object clone(){
		Object newObject = null;
		try {
			newObject = super.clone();
			PresentationObjectComponentFacetMap facetMap = (PresentationObjectComponentFacetMap)newObject;
			facetMap._map=(Map) ((HashMap)this._map).clone();
		}
		catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newObject;
	}
	
	
	/**
	 * @return Returns the _component.
	 */
	protected UIComponent getComponent() {
		return this._component;
	}
	/**
	 * @param _component The _component to set.
	 */
	protected void setComponent(UIComponent _component) {
		this._component = _component;
	}
}