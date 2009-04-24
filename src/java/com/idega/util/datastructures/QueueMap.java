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
 * @param <T>
 * @param <E>
 */
public class QueueMap<E, T> extends HashMap<E, T> implements Map<E, T> {

	private static final long serialVersionUID = 709341061424502067L;
	
	private QueueSet<E> _keySet = new QueueSet<E>();
	private List<E> keyList = new ArrayList<E>();
	private List<T> _valueList = new ArrayList<T>();
	
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
	public QueueMap(Map<E, T> m) {
		super(m);
	}
	
	@Override
	public T put(E key, T value){
		removeKey(key);
		this.keyList.add(key);
		this._keySet.add(key);
		this._valueList.add(value);
		return super.put(key, value);
	}
	
	public Object putAtBeginning(E key, T value){
		removeKey(key);
		this.keyList.add(0, key);
		this._keySet.addAtBeginning(key);
		this._valueList.add(0, value);
		return super.put(key, value);
	}

	@Override
	public T remove(Object key){
		Object val = super.remove(key);
		removeKey(key);
		return (T) val;		
	}
	
	@Override
	public Set<E> keySet(){
		return this._keySet;
	}
	
	@Override
	public Collection<T> values(){
		return this._valueList;
	}
	
	public Iterator<T> iterator() {
		return this._valueList.iterator();
	}
	
	private void removeKey(Object key) {
		int oldKeyIndex = this.keyList.indexOf(key);
		if (oldKeyIndex > -1) {
			if(this._valueList.size()>oldKeyIndex){
				this._valueList.remove(oldKeyIndex);
			}
			this.keyList.remove(key);
			this._keySet.remove(key);
		}
	}
	
}
