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
import java.util.Iterator;
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
	
	private QueueSet _keySet = new QueueSet();
	private List keyList = new ArrayList();
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
		removeKey(key);
		keyList.add(key);
		_keySet.add(key);
		_valueList.add(value);
		return super.put(key,value);
	}
	
	public Object putAtBeginning(Object key, Object value){
		removeKey(key);
		keyList.add(0,key);
		_keySet.addAtBeginning(key);
		_valueList.add(0,value);
		return super.put(key,value);
	}

	public Object remove(Object key){
		Object val = super.remove(key);
		removeKey(key);
		return val;		
	}
	
	public Set keySet(){
		return _keySet;
	}
	
	public Collection values(){
		return _valueList;
	}
	
	public Iterator iterator() {
		return _valueList.iterator();
	}
	
	private void removeKey(Object key) {
		int oldKeyIndex = keyList.indexOf(key);
		if (oldKeyIndex > -1) {
			if(_valueList.size()>oldKeyIndex){
				_valueList.remove(oldKeyIndex);
			}
			keyList.remove(key);
			_keySet.remove(key);
		}
	}
	
}
