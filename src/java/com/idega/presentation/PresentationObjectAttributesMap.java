/*
 * $Id: PresentationObjectAttributesMap.java,v 1.1.2.1 2007/01/12 19:31:36 idegaweb Exp $
 * Created on 14.11.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * 
 * Last modified: $Date: 2007/01/12 19:31:36 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.1.2.1 $
 */
class PresentationObjectAttributesMap implements Map, Serializable, Cloneable {

	private static final Object[] EMPTY_ARGS = new Object[0];

	private UIComponent _component;

	private Map _attributes = null; //We delegate instead of derive from
									// HashMap, so that we can later optimize
									// Serialization

	private transient Map _propertyDescriptorMap = null;

	PresentationObjectAttributesMap(UIComponent component) {
		this._component = component;
		this._attributes = new HashMap();
	}

	PresentationObjectAttributesMap(UIComponent component, Map attributes) {
		this._component = component;
		this._attributes = attributes;
	}

	public int size() {
		return this._attributes.size();
	}

	public void clear() {
		this._attributes.clear();
	}

	public boolean isEmpty() {
		return this._attributes.isEmpty();
	}

	public boolean containsKey(Object key) {
		checkKey(key);
		if (getPropertyDescriptor((String) key) == null) {
			return this._attributes.containsKey(key);
		}
		else {
			return false;
		}
	}

	/**
	 * @param value
	 *            null is allowed
	 */
	public boolean containsValue(Object value) {
		return this._attributes.containsValue(value);
	}

	public Collection values() {
		return this._attributes.values();
	}

	public void putAll(Map t) {
		for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			put(entry.getKey(), entry.getValue());
		}
	}

	public Set entrySet() {
		return this._attributes.entrySet();
	}

	public Set keySet() {
		return this._attributes.keySet();
	}

	public Object get(Object key) {
		checkKey(key);
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor((String) key);
		if (propertyDescriptor != null) {
			return getComponentProperty(propertyDescriptor);
		}
		else {
			return this._attributes.get(key);
		}
	}

	public Object remove(Object key) {
		checkKey(key);
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor((String) key);
		if (propertyDescriptor != null) {
			throw new IllegalArgumentException("Cannot remove component property attribute");
		}
		return this._attributes.remove(key);
	}

	/**
	 * @param key
	 *            String, null is not allowed
	 * @param value
	 *            null is allowed
	 */
	public Object put(Object key, Object value) {
		checkKey(key);
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor((String) key);
		if (propertyDescriptor != null) {
			if (propertyDescriptor.getReadMethod() != null) {
				Object oldValue = getComponentProperty(propertyDescriptor);
				setComponentProperty(propertyDescriptor, value);
				return oldValue;
			}
			else {
				setComponentProperty(propertyDescriptor, value);
				return null;
			}
		}
		else {
			return this._attributes.put(key, value);
		}
	}

	private PropertyDescriptor getPropertyDescriptor(String key) {
		if (this._propertyDescriptorMap == null) {
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(this._component.getClass());
			}
			catch (IntrospectionException e) {
				throw new FacesException(e);
			}
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			this._propertyDescriptorMap = new HashMap();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
				if (propertyDescriptor.getReadMethod() != null) {
					this._propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
				}
			}
		}
		return (PropertyDescriptor) this._propertyDescriptorMap.get(key);
	}

	private Object getComponentProperty(PropertyDescriptor propertyDescriptor) {
		Method readMethod = propertyDescriptor.getReadMethod();
		if (readMethod == null) {
			throw new IllegalArgumentException("Component property " + propertyDescriptor.getName()
					+ " is not readable");
		}
		try {
			return readMethod.invoke(this._component, EMPTY_ARGS);
		}
		catch (Exception e) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			throw new FacesException("Could not get property " + propertyDescriptor.getName() + " of component "
					+ this._component.getClientId(facesContext), e);
		}
	}

	private void setComponentProperty(PropertyDescriptor propertyDescriptor, Object value) {
		Method writeMethod = propertyDescriptor.getWriteMethod();
		if (writeMethod == null) {
			throw new IllegalArgumentException("Component property " + propertyDescriptor.getName()
					+ " is not writable");
		}
		try {
			writeMethod.invoke(this._component, new Object[] { value });
		}
		catch (Exception e) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			throw new FacesException("Could not set property " + propertyDescriptor.getName() + " of component "
					+ this._component.getClientId(facesContext), e);
		}
	}

	private void checkKey(Object key) {
		if (key == null) {
			throw new NullPointerException("key");
		}
		if (!(key instanceof String)) {
			throw new ClassCastException("key is not a String");
		}
	}

	Map getUnderlyingMap() {
		return this._attributes;
	}
	
	
	public Object clone(){
		Object newObject = null;
		try {
			newObject = super.clone();
			PresentationObjectAttributesMap attributesMap = (PresentationObjectAttributesMap)newObject;
			attributesMap._attributes=(Map) ((HashMap)this._attributes).clone();
			attributesMap._propertyDescriptorMap=(Map) ((HashMap)this._propertyDescriptorMap).clone();
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
	UIComponent getComponent() {
		return this._component;
	}
	/**
	 * @param _component The _component to set.
	 */
	void setComponent(UIComponent _component) {
		this._component = _component;
	}	
}