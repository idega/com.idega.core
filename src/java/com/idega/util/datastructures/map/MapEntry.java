/*
 * $Id: MapEntry.java,v 1.2 2006/04/09 12:13:20 laddi Exp $
 * Created on Mar 30, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.datastructures.map;

import java.util.Map;

/**
 * Implementation of the interface Map.Entry.
 * 
 * This class is used by implementations of maps but can also be used as a single container.
 * 
 * The equals method checks the interface not the class that is another instance of Map.Entry 
 * is equal if the key and value are equal.
 * 
 *  Last modified: $Date: 2006/04/09 12:13:20 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */

public class MapEntry implements Map.Entry {
		
	Object myKey =null;
	Object myValue = null;
	
	public MapEntry(Object keyEntry, Object valueEntry) {
		this.myKey = keyEntry;
		this.myValue = valueEntry;
	}
	
	public Object getKey() {
		return this.myKey;
	}
	
	public Object getValue() {
		return this.myValue;
	}
	
	public Object setValue(Object newValue) {
		Object oldValue = this.myValue;
		this.myValue = newValue;
		return oldValue;
	}
	
	public boolean equals(Object anotherMap) {
		if (anotherMap == null) {
				return false;
		}
		if (this == anotherMap) {
			return true;
		}
		if (anotherMap instanceof Map.Entry) {
			Object aKey = ((Map.Entry) anotherMap).getKey();
			Object aValue = ((Map.Entry) anotherMap).getValue();
			if (this.myKey != null && this.myValue != null) {
				return this.myKey.equals(aKey) && this.myValue.equals(aValue);
			}
			if (this.myKey == null && aKey == null && this.myValue != null) {
				return this.myValue.equals(aValue);
			}
			if (this.myValue == null && aValue == null && this.myKey != null) {
				return this.myKey.equals(aKey);
			}
			return (this.myKey == null && aKey == null && this.myValue == null && aValue == null);
		}
		return false;
	}
	
	public int hashCode() {
		int code = 0;
		if (this.myKey != null) {
			code = this.myKey.hashCode();
		}
		if (this.myValue != null) {
			code += this.myValue.hashCode();
		}
		return code;
	}
}
	






