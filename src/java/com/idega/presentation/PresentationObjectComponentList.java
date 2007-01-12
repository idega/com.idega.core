/*
 * $Id: PresentationObjectComponentList.java,v 1.5.2.1 2007/01/12 19:31:35 idegaweb Exp $ Created on
 * 14.11.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.presentation;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;

/**
 * Overrided from JSFs standard Children because of the clone() issue.
 * 
 * Last modified: $Date: 2007/01/12 19:31:35 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.5.2.1 $
 */
class PresentationObjectComponentList extends AbstractList implements Serializable,Cloneable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1682244512314682987L;

	private UIComponent _component;

	private List _list = new ArrayList();

	PresentationObjectComponentList(UIComponent component) {
		this._component = component;
	}

	public Object get(int index) {
		try{
			return this._list.get(index);
		}
		catch(ArrayIndexOutOfBoundsException e){
			throw new RuntimeException(e);
		}
	}

	public int size() {
		return this._list.size();
	}

	public Object set(int index, Object value) {
		checkValue(value);
		setNewParent((UIComponent) value);
		UIComponent child = (UIComponent) this._list.set(index, value);
		if (child != null) {
			child.setParent(null);
		}
		return child;
	}

	public boolean add(Object value) {
		checkValue(value);
		setNewParent((UIComponent) value);
		return this._list.add(value);
	}

	public void add(int index, Object value) {
		checkValue(value);
		setNewParent((UIComponent) value);
		this._list.add(index, value);
	}

	public Object remove(int index) {
		UIComponent child = (UIComponent) this._list.remove(index);
		if (child != null) {
			child.setParent(null);
		}
		return child;
	}

	private void setNewParent(UIComponent child) {
		//UIComponent oldParent = child.getParent();
		//if (oldParent != null) {
		//	oldParent.getChildren().remove(child);
		//}
		child.setParent(this._component);
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
			PresentationObjectComponentList componentList = (PresentationObjectComponentList)newObject;
			componentList._list=(List) ((ArrayList)this._list).clone();
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

	/*public Iterator iterator() {
		//return super.iterator();
		return this._list.iterator();
	}

	public ListIterator listIterator() {
		//return super.listIterator();
		return this._list.listIterator();
	}

	public ListIterator listIterator(int index) {
		//return super.listIterator(index);
		return this._list.listIterator(index);
	}

	public boolean addAll(int arg0, Collection arg1) {
		// TODO Auto-generated method stub
		return this._list.addAll(arg0, arg1);
	}

	public void clear() {
		// TODO Auto-generated method stub
		this._list.clear();
	}

	public int indexOf(Object arg0) {
		// TODO Auto-generated method stub
		return this._list.indexOf(arg0);
	}

	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return this._list.lastIndexOf(arg0);
	}

	public List subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return this._list.subList(arg0, arg1);
	}

	public boolean addAll(Collection arg0) {
		// TODO Auto-generated method stub
		return this._list.addAll(arg0);
	}

	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return this._list.contains(arg0);
	}

	public boolean containsAll(Collection arg0) {
		// TODO Auto-generated method stub
		return this._list.containsAll(arg0);
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this._list.isEmpty();
	}

	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return this._list.remove(arg0);
	}

	public boolean removeAll(Collection arg0) {
		// TODO Auto-generated method stub
		return this._list.removeAll(arg0);
	}

	public boolean retainAll(Collection arg0) {
		// TODO Auto-generated method stub
		return this._list.retainAll(arg0);
	}

	public Object[] toArray() {
		// TODO Auto-generated method stub
		return this._list.toArray();
	}

	public Object[] toArray(Object[] arg0) {
		// TODO Auto-generated method stub
		return this._list.toArray(arg0);
	}

	public String toString() {
		// TODO Auto-generated method stub
		return this._list.toString();
	}*/
	
	
}