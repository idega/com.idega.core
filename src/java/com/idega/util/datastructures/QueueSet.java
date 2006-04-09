/*
 * Created on 13.8.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Title:		QueueSet
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class QueueSet extends HashSet implements Set {
	
	private List _set = new ArrayList();
	
	/**
	 * 
	 */
	public QueueSet() {
		super();
	}

	/**
	 * @param c
	 */
	public QueueSet(Collection c) {
		super(c);
	}

	/**
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public QueueSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @param initialCapacity
	 */
	public QueueSet(int initialCapacity) {
		super(initialCapacity);
	}
	
	public boolean add(Object obj){
		this._set.add(obj);
		return super.add(obj);
	}
	
	public boolean addAtBeginning(Object obj){
		this._set.add(0,obj);
		return super.add(obj);
	}
	
	public boolean remove(Object obj){
		this._set.remove(obj);
		return super.remove(obj);
	}
	
	public boolean removeAll(Collection c){
		this._set.removeAll(c);
		return super.removeAll(c);
	}
	
	public Iterator iterator(){
		return this._set.iterator();
	}
	
	

}
