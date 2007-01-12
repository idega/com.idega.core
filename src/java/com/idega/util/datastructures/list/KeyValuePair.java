/*
 * Created on Jul 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.util.datastructures.list;


/**
 * <p>
 * A simple container for storing key value pairs used by KeyValueList
 * </p>
 *  Last modified: $Date: 2007/01/12 19:32:55 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1.2.1 $
 */
public class KeyValuePair {
	
	private Object key;
	private Object value;
	
	public KeyValuePair(Object key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public Object getKey() {
		return this.key;
	}
	
	public Object getValue() {
		return this.value;
	}
}
