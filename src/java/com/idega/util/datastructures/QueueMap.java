/*
 * Created on 13.8.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Title:		ConsistentMap
 * Description: - Not fully implemented - <p> returns valeues and keys in the same order that they are putted
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		0.5
 */
public class QueueMap extends HashMap implements Map {
	
	private Set _keySet = new QueueSet();
	private List _valueList = new ArrayList();
	
	/**
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public QueueMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @param initialCapacity
	 */
	public QueueMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 
	 */
	public QueueMap() {
		super();
	}

	/**
	 * @param m
	 */
	public QueueMap(Map m) {
		super(m);
	}
	
	public Object put(Object key, Object value){
		_keySet.add(key);
		_valueList.add(value);
		return super.put(key,value);
	}

	public Object remove(Object key){
		Object val = super.remove(key);
		_keySet.remove(key);
		_valueList.remove(val);
		return val;		
	}
	
	public Set keySet(){
		return _keySet;
	}
	
	public Collection values(){
		return _valueList;
	}
	
}
