/*
 * PrimaryKey.java, 9.10.2003 11:22:58 - laddi
 * 
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 * 
 */
package com.idega.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author laddi
 */
public class PrimaryKey implements IDOPrimaryKey {

	Map _primaryKeyMap;
	
	public PrimaryKey() {
		this._primaryKeyMap = new HashMap();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOPrimaryKey#getInstance()
	 */
	public IDOPrimaryKey getInstance() {
		return new PrimaryKey();
	}
	
	private boolean isComposite() {
		if (this._primaryKeyMap.size() > 1) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOPrimaryKey#getPrimaryKeyValue(java.lang.String)
	 */
	public Object getPrimaryKeyValue(String columnName) {
		return this._primaryKeyMap.get(columnName.toUpperCase());
	}

	/* (non-Javadoc)
	 * @see com.idega.data.IDOPrimaryKey#setPrimaryKeyValue(java.lang.String, java.lang.Object)
	 */
	public void setPrimaryKeyValue(String columnName, Object PKValue) {
		this._primaryKeyMap.put(columnName.toUpperCase(), PKValue);
	}

	public boolean equals(Object obj) {
		if (obj instanceof IDOPrimaryKey) {
			IDOPrimaryKey comparePK = (IDOPrimaryKey) obj;
			Iterator iter = this._primaryKeyMap.keySet().iterator();
			while (iter.hasNext()) {
				String columnName = (String) iter.next();
				if (!this._primaryKeyMap.get(columnName).equals(comparePK.getPrimaryKeyValue(columnName))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public int hashCode()  { 
		StringBuffer buffer = new StringBuffer(); 
		Iterator iter = this._primaryKeyMap.entrySet().iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
		}
		String keys = buffer.toString(); 

		return keys.hashCode(); 
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean isComposite = isComposite();
		
		Iterator iter = this._primaryKeyMap.keySet().iterator();
		while (iter.hasNext()) {
			String columnName = (String) iter.next();
			if (isComposite) {
				buffer.append(columnName).append("=");
			}
			buffer.append(this._primaryKeyMap.get(columnName));
			if (isComposite && iter.hasNext()) {
				buffer.append("&");
			}
		}
		
		return buffer.toString();
	}
}
