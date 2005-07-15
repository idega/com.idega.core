/*
 * Created on Jul 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.util.datastructures.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


/**
 * <p>
 * This list stores key value pairs in a very simple way.
 * Use this class if keys are not unique and therefore a map can not be used. 
 * Note: The elements in the list are always of the type KeyValuePair.
 *  </p>
 *  Last modified: $Date: 2005/07/15 11:55:07 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class KeyValueList extends ArrayList {
	
	public KeyValueList() {
		// not empty
	}
	
	public KeyValueList(int initialCapacity) {
		super(initialCapacity);
	}
	
	public void put(Object key, Object value) {
		KeyValuePair pair = new KeyValuePair(key, value);
		add(pair);
	}
	
	public void putAll(Map map) {
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			Object value = map.get(key);
			put(key, value);
		}
	}
}
