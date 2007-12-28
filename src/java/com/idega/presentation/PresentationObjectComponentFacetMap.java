/*
 * $Id: PresentationObjectComponentFacetMap.java,v 1.4 2007/12/28 13:23:04 valdas Exp $
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
 * Last modified: $Date: 2007/12/28 13:23:04 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.4 $
 */
public class PresentationObjectComponentFacetMap implements Map<String, UIComponent>, Serializable, Cloneable {

	private static final long serialVersionUID = -7199235020873880634L;

	private UIComponent _component;

	private Map<String, UIComponent> _map = new HashMap<String, UIComponent>();

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

	public Collection<UIComponent> values() {
		return this._map.values();
	}

	/*public void putAll(Map t) {
		for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			put(entry.getKey(), entry.getValue());
		}
	}*/
	
	public UIComponent put(String key, UIComponent value) {
		checkKey(key);
		checkValue(value);
		setNewParent(key, value);
		return this._map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public void putAll(Map<? extends String, ? extends UIComponent> t) {
		if (t == null) {
			throw new NullPointerException("Map is null");
		}
		
		Entry<String, UIComponent> entry = null;
		Object o = null;
		for (Iterator<?> it = t.entrySet().iterator(); it.hasNext();) {
			o = it.next();
			if (o instanceof Entry) {
				entry = (Entry<String, UIComponent>) o;
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	public Set<Entry<String, UIComponent>> entrySet() {
		return this._map.entrySet();
	}

	public Set<String> keySet() {
		return this._map.keySet();
	}

	public UIComponent get(Object key) {
		checkKey(key);
		return this._map.get(key);
	}

	public UIComponent remove(Object key) {
		checkKey(key);
		UIComponent facet = (UIComponent) this._map.remove(key);
		if (facet != null) {
			facet.setParent(null);
		}
		return facet;
	}

	private void setNewParent(String facetName, UIComponent facet) {
		UIComponent oldParent = facet.getParent();
		if (oldParent != null) {
			oldParent.getFacets().remove(facetName);
		}
		facet.setParent(this._component);
	}

	private void checkKey(Object key) {
		if (!(key instanceof String)) {
			throw new ClassCastException("key is not a String");
		}
	}

	private void checkValue(Object value) {
		if (!(value instanceof UIComponent)) {
			throw new ClassCastException("value is not a UIComponent");
		}
	}


	@SuppressWarnings("unchecked")
	public Object clone(){
		Object newObject = null;
		try {
			newObject = super.clone();
			PresentationObjectComponentFacetMap facetMap = (PresentationObjectComponentFacetMap)newObject;
			facetMap._map=(Map) ((HashMap)this._map).clone();
		}
		catch (CloneNotSupportedException e) {
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