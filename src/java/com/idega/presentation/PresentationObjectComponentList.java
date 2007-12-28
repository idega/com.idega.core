/*
 * $Id: PresentationObjectComponentList.java,v 1.7 2007/12/28 13:23:04 valdas Exp $ Created on
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
 * Last modified: $Date: 2007/12/28 13:23:04 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson </a>
 * @version $Revision: 1.7 $
 */
class PresentationObjectComponentList extends AbstractList<UIComponent> implements Serializable,Cloneable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1682244512314682987L;

	private UIComponent _component;

	private List<UIComponent> _list = new ArrayList<UIComponent>();

	PresentationObjectComponentList(UIComponent component) {
		this._component = component;
	}

	public UIComponent get(int index) {
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

	public UIComponent set(int index, UIComponent value) {
		checkValue(value);
		setNewParent((UIComponent) value);
		UIComponent child = this._list.set(index, value);
		if (child != null) {
			child.setParent(null);
		}
		return child;
	}

	public boolean add(UIComponent value) {
		checkValue(value);
		setNewParent(value);
		return this._list.add(value);
	}

	public void add(int index, UIComponent value) {
		checkValue(value);
		setNewParent(value);
		this._list.add(index, value);
	}

	public UIComponent remove(int index) {
		UIComponent child = this._list.remove(index);
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
		if (!(value instanceof UIComponent)) {
			throw new ClassCastException("value is not a UIComponent");
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object clone(){
		Object newObject = null;
		try {
			newObject = super.clone();
			PresentationObjectComponentList componentList = (PresentationObjectComponentList) newObject;
			Object clone = ((ArrayList<UIComponent>)this._list).clone();
			if (clone instanceof List) {
				componentList._list = (List<UIComponent>) clone;
			}
		}
		catch (CloneNotSupportedException e) {
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