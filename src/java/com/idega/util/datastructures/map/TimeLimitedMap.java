/*
 * $Id: TimeLimitedMap.java,v 1.1 2006/03/30 11:20:37 thomas Exp $
 * Created on Mar 29, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.datastructures.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A map that invalidates entries after a certain time and removes them.
 * 
 * The implementation is done without threads, 
 * that is no daemon is running that checks the entries.
 *  
 * Every call of this map causes a smart check if there are any old entries.
 * 
 * That means after putting some entries into the map the entries 
 * are kept forever if there is no further call of the class.
 * 
 * Use this map if the map is frequently called and if there might be the danger
 * of forgetting entries - that is if another map is used the map might
 * by and by getting too big because of forgotten entries.
 * 
 * This map should not be used to store large number of entries 
 * that should be automatically removed after a while without calling the map at all.
 *  * 
 *  Last modified: $Date: 2006/03/30 11:20:37 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public class TimeLimitedMap implements Map {
	
	// some factory methods for all people that can not calculate or want to avoid stupid bugs
	
	public static TimeLimitedMap getInstanceWithTimeLimitInHours(long hours) {
		return getInstanceWithTimeLimitInMinutes(hours * 60);
	}
	
	public static TimeLimitedMap  getInstanceWithTimeLimitInMinutes(long minutes) {
		return getInstanceWithTimeLimitInSeconds(minutes * 60);
	}
	
	public static TimeLimitedMap getInstanceWithTimeLimitInSeconds(long seconds) {
		return getInstanceWithTimeLimitInMilliseconds(seconds * 1000);
	}
	
	public static TimeLimitedMap getInstanceWithTimeLimitInMilliseconds(long milliseconds) {
		TimeLimitedMap timeLimitedMap = new TimeLimitedMap();
		timeLimitedMap.setTimeOut(milliseconds);
		return timeLimitedMap;
	}
	
	
	/**
	 * Only for testing the class
	 */
	public static void main(String[] args) {
		TimeLimitedMap myMap = new TimeLimitedMap();
		myMap.testImplementation();
	}
	
	private void testImplementation() {
		setTimeOut(6000); // six seconds
		put("weser1", "hallo1");
		Object response1 = get("weser1");
		System.out.println(response1);    
		try {
			// set to 4000 and watch what happens
			Thread.sleep(6000);
		}
		catch (InterruptedException ex) {
			// do nothing
		}
		put("weser2","hallo2");
		Set aSet = entrySet();
		Iterator iterator = aSet.iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			System.out.print(key + " " + value);
		}
	}
	
	private long timeOut = -1; 
	
	private boolean noTimeOutSet = true;
	
	private long oldestTimestamp = -1;
	
	private List values = new ArrayList();
	
	private List keys = new ArrayList();
	
	private List timestamps = new ArrayList();
	
	private int findTimeLimitIndexSetOldestTimestamp(long timeLimit) {
		int timeLimitIndex = -1;
		int endIndex = timestamps.size();
		for (int i = 0; i <  endIndex ; i++) {
			Long timestamp = (Long) timestamps.get(i);
			long longTimestamp = timestamp.longValue();
			if (longTimestamp < timeLimit) {
				timeLimitIndex = i;
			}
			else {
				oldestTimestamp = longTimestamp;
				return timeLimitIndex;
			}
		}
		oldestTimestamp = -1;
		return timeLimitIndex;
	}
			
	private void clean() {
		// first fast check, nothing to do if there is no time limit and if the list is empty
		if (noTimeOutSet || timestamps.isEmpty()) {
			return;
		}
		// second fast check, nothing to do if the oldest entry is fresh
		long timeLimit = System.currentTimeMillis() - timeOut;
		if (oldestTimestamp > timeLimit) {
			return;
		}
		int timeLimitIndex = findTimeLimitIndexSetOldestTimestamp(timeLimit);
		// shortcut - whole list is too old
		if (oldestTimestamp == -1) {
			clear();
			return;
		}
		for (int i = 0; i <= timeLimitIndex ; i++) {
			values.remove(i);
			keys.remove(i);
			timestamps.remove(i);
		}
	}

	public long setTimeOut(long milliseconds) {
		long oldTimeOut = timeOut;
		noTimeOutSet = (milliseconds <= 0);
		timeOut = (noTimeOutSet) ? -1 : milliseconds;
		return oldTimeOut;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		clean();
		return keys.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		// we do not need to set the oldestTimestamp to "-1" - it is still "valid" even for new entries!
		keys.clear();
		values.clear();
		timestamps.clear();
		
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		clean();
		return keys.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		clean();
		return keys.contains(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		clean();
		return values.contains(value);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values() {
		clean();
		return values;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map aMap) {
		clean();
		long timestamp = System.currentTimeMillis();
		Long myTimestamp = new Long(timestamp);
		Iterator iterator = aMap.keySet().iterator(); 
		while(iterator.hasNext()) {
			Object key = iterator.next();
			Object value = aMap.get(key);
			keys.add(key);
			values.add(value);
			timestamps.add( myTimestamp);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		clean();
		Set entries = new HashSet();
		int endIndex = keys.size();
		for (int i = 0; i <  endIndex ; i++) {
			Object tempKey = keys.get(i);
			Object tempValue = values.get(i);
			Map.Entry entry = new MapEntry(tempKey, tempValue);
			entries.add(entry);
		}
		return entries;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		clean();
		return new HashSet(keys);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		clean();
		return getWithoutCleaning(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		clean();
		// look up the latest entry !!
		int index = keys.lastIndexOf(key);
		if (index < 0 ) {
			// nothing to remove
			return null;
		}
		Object oldValue = values.get(index);
		timestamps.remove(index);
		keys.remove(index);
		values.remove(index);
		return oldValue;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		clean();
		Object oldValue = getWithoutCleaning(key);
		long timestamp = System.currentTimeMillis();
		Long myTimestamp = new Long(timestamp);
		keys.add(key);
		values.add(value);
		timestamps.add( myTimestamp);
		return oldValue;
	}
	
	private Object getWithoutCleaning(Object key) {
		// look up the latest entry !!
		int index = keys.lastIndexOf(key);
		return (index < 0) ? null : values.get(index);
	}
	
	
}

