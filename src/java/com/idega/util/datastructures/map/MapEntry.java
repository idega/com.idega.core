/*
 * $Id: MapEntry.java,v 1.1 2006/03/30 11:20:37 thomas Exp $
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
 *  Last modified: $Date: 2006/03/30 11:20:37 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */

public class MapEntry implements Map.Entry {
		
	Object myKey =null;
	Object myValue = null;
	
	public MapEntry(Object keyEntry, Object valueEntry) {
		myKey = keyEntry;
		myValue = valueEntry;
	}
	
	public Object getKey() {
		return myKey;
	}
	
	public Object getValue() {
		return myValue;
	}
	
	public Object setValue(Object newValue) {
		Object oldValue = myValue;
		myValue = newValue;
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
			if (myKey != null && myValue != null) {
				return myKey.equals(aKey) && myValue.equals(aValue);
			}
			if (myKey == null && aKey == null && myValue != null) {
				return myValue.equals(aValue);
			}
			if (myValue == null && aValue == null && myKey != null) {
				return myKey.equals(aKey);
			}
			return (myKey == null && aKey == null && myValue == null && aValue == null);
		}
		return false;
	}
	
	public int hashCode() {
		int code = 0;
		if (myKey != null) {
			code = myKey.hashCode();
		}
		if (myValue != null) {
			code += myValue.hashCode();
		}
		return code;
	}
}
	






