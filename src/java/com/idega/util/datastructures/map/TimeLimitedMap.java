/*
 * $Id: TimeLimitedMap.java,v 1.3 2006/04/09 12:13:20 laddi Exp $
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
 *  
 *  Last modified: $Date: 2006/04/09 12:13:20 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.3 $
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
		TimeLimitedMap timeLimitedMap = new TimeLimitedMap(milliseconds);
		return timeLimitedMap;
	}
	
	/**
	 * Only for testing the class
	 */
	public static void main(String[] args) {
		TimeLimitedMap myMap = getInstanceWithTimeLimitInSeconds(11);
		myMap.testImplementation();
		//myMap.testImplementation();
	}
	
	// noTimeOut is Set if timeout  <= 0
	private boolean noTimeOut = true;
	
	private long timeOut = 0; 
	
	// first list contains always valid entries
	
	// only the first list is used when there is no timeOut
		
	private List firstValues = new ArrayList();
	
	private List firstKeys = new ArrayList();
	
	private List firstTimestamps = new ArrayList();
	
	// second list contains valid and invlaid entries
	
	private List secondValues = new ArrayList();
	
	private List secondKeys = new ArrayList();
	
	private List secondTimestamps = new ArrayList();
	
	private long secondLatestTimestampTimeout = -1;
	
	private int size = 0;
	
	public TimeLimitedMap(long timeOut) {
		setTimeOut(timeOut);
		initialize();
	}
	
	private void initialize() {
		this.firstValues.clear();
		this.firstKeys.clear();
		this.firstTimestamps.clear();
		this.secondValues.clear();
		this.secondKeys.clear();
		this.secondTimestamps.clear();
		calculateSecondLatestTimestamp(System.currentTimeMillis());
	}
	
	

	
	private void control(long currentTime) {
		if (this.noTimeOut) {
			return;
		}
		// after call of control the first list is always valid!
		if (currentTime > this.secondLatestTimestampTimeout) {
			// the whole second stack is expired
			// move first stack to second (that is delete second stack)
			// create new first stack
			List tempFirstValues = this.secondValues;
			List tempFirstKeys = this.secondKeys;
			List tempFirstTimestamps = this.secondTimestamps;
			this.secondValues = this.firstValues;
			this.secondKeys = this.firstKeys;
			this.secondTimestamps = this.firstTimestamps;
			this.firstValues = tempFirstValues;
			this.firstKeys = tempFirstKeys;
			this.firstTimestamps = tempFirstTimestamps;
			this.firstValues.clear();
			this.firstKeys.clear();
			this.firstTimestamps.clear();
			calculateSecondLatestTimestamp(currentTime);
		}
	}
	
	private void calculateSecondLatestTimestamp(long currentTime) {
		int endIndex = this.secondTimestamps.size() - 1;
		if (endIndex > -1 ) {
			// is there an entry?
			Long tempTimestamp = (Long) this.secondTimestamps.get(endIndex);
			// note: tempTimestamp is the "original" timestamp plus timeout !!!!
			// do not add timeout again
			this.secondLatestTimestampTimeout = tempTimestamp.longValue();
		}
		else {
			// take the current time
			this.secondLatestTimestampTimeout = currentTime + this.timeOut;
		}

	}
	
	private int getValidIndexControl(Object object, List firstList, List secondList)	{
		long currentTime = System.currentTimeMillis();
		control(currentTime);
		// after call of control the first list is always valid!
		// lookup first list
		int index = firstList.lastIndexOf(object);
		if (index > -1 ) {
			index++;
			return index;
		}
		// lookup second list
		index = secondList.lastIndexOf(object);
		if (index > -1 && isValid(index, this.secondTimestamps, currentTime)) {
			index++;
			return -index;
		}
		return 0;
	}
	
	private int getIndexWithoutControl(Object object, List firstList, List secondList) {
		// lookup first list
		int index = firstList.lastIndexOf(object);
		if (index > -1) {
			index++;
			return index;
		}
		// lookup second list
		index = secondList.lastIndexOf(object);
		if (index > -1) {
			index++;
			return -index;
		}
		return 0;
}

	
	private boolean isValid(int index, List timestampList, long currentTime) {
		if (this.noTimeOut) {
			return true;
		}
		Long timestampLong = (Long) timestampList.get(index);
		long timestamp = timestampLong.longValue();
		return (currentTime < timestamp);
	}
	
	private int[] getValidIndicesControl() {
		long currentTime = System.currentTimeMillis();
		control(currentTime);
		// after call of control the first list is always valid!
		int maxSize = this.firstKeys.size() + this.secondKeys.size();
		int[] validIndices = new int[maxSize];
		int k = 0;
		// tricky - index runs down to 1 not 0 !
		for (int i = this.firstKeys.size(); i > 0; i--) {
			validIndices[k++] = i;
		} 
		// the second list
		for (int i = this.secondKeys.size(); i > 0; i--) {
			if (isValid(i -1, this.secondTimestamps, currentTime)) {
				// tricky - to know that this is the index of the second list the index is negative
				validIndices[k++] = -i;
			}
			else {
				// there can't be any more valid indices
				this.size = k;
				return validIndices;
			}
		} 	
		this.size = k;
		return validIndices;
	}
	
	public void setTimeOut(long milliseconds) {
		long currentTime = System.currentTimeMillis();
		this.noTimeOut = (milliseconds <= 0);
		long newTimeOut = (this.noTimeOut) ? 0 : milliseconds;
		if (newTimeOut == this.timeOut) {
			// nothing to do
			return;
		}
		// timestamp of an entry = original timestamp + timeOut
		// new timestamp of an entry = original timestamp + timeOut - timeOut + newTimeOut
		long difference = newTimeOut - this.timeOut;
		// set the new timeout
		this.timeOut  =newTimeOut;
		// change timestamps of both lists
		changeTimestamps(this.firstTimestamps, difference);
		changeTimestamps(this.secondTimestamps, difference);
		// the secondLatestTimestamp is wrong!!!!
		calculateSecondLatestTimestamp(currentTime);
		// note: call calculateSecondLatestTimestamp before call of control !!!
		control(currentTime);
	}
		
		
		
	private void changeTimestamps(List timestampList, long difference) {
		int listSize = timestampList.size();
		for (int i = 0; i < listSize; i++) {
			Long tempLong = (Long) timestampList.get(i);
			long tempTimestamp = tempLong.longValue();
			tempTimestamp += difference;
			tempLong = new Long(tempTimestamp);
			timestampList.set(i, tempLong);
		}
	}
		
	/**
	 * 
	 * 
	 * (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		getValidIndicesControl();
		return this.size;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		initialize();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		control(System.currentTimeMillis());
		// sufficient to check the first list 
		return this.firstTimestamps.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return (getValidIndexControl(key, this.firstKeys, this.secondKeys) != 0);
	}
			
	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return (getValidIndexControl(value, this.firstValues, this.secondValues) != 0);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values() {
		int[] validIndices = getValidIndicesControl();
		List values = new ArrayList(this.size);
		for (int i = this.size -1; i > -1 ; i--) {
			int index = validIndices[i];
			// first list positive, second list negative
			Object value = (index > 0) ? this.firstValues.get(index - 1) : this.secondValues.get(-index - 1);
			values.add(value);
		}
		return values;
	}


	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map aMap) {
		long currentTime = System.currentTimeMillis();
		control(currentTime);
		long timestamp = currentTime + this.timeOut;
		Long myTimestamp = new Long(timestamp);
		Iterator iterator = aMap.keySet().iterator(); 
		while(iterator.hasNext()) {
			Object key = iterator.next();
			removeWithoutControl(key);
			Object value = aMap.get(key);
			this.firstKeys.add(key);
			this.firstKeys.add(value);
			this.firstKeys.add( myTimestamp);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		int[] validIndices = getValidIndicesControl();
		Set entries = new HashSet(this.size);
		for (int i = this.size-1; i > -1 ; i--) {
			int index = validIndices[i];
			// first list positive, second list negative
			Object tempKey = null;
			Object tempValue = null;
			if (index > 0)  {
				tempValue = this.firstValues.get(index - 1);
				tempKey = this.firstKeys.get(index - 1);
			}
			else {
				tempValue = this.secondValues.get(-index - 1);
				tempKey = this.secondKeys.get(-index - 1);
			}
			Map.Entry entry = new MapEntry(tempKey, tempValue);
			entries.add(entry);
		}
		return entries;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		int[] validIndices = getValidIndicesControl();
		Set keys = new HashSet(this.size);
		for (int i = this.size - 1; i > -1 ; i--) {
			int index = validIndices[i];
			// first list positive, second list negative
			Object key = (index > 0) ? this.firstKeys.get(index - 1) : this.secondKeys.get(-index - 1);
			keys.add(key);
		}
		return keys;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		int index = getValidIndexControl(key, this.firstKeys, this.secondKeys);
		if (index == 0) {
			// not found
			return null;
		}
		// first list positive, second list negative
		return (index > 0) ? this.firstValues.get(index - 1) : this.secondValues.get(-index - 1);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return removeControl(key, System.currentTimeMillis());
	}
		
	private Object removeControl(Object key, long currentTime) {
		control(currentTime);
		return removeWithoutControl(key);
	}
			
	private Object removeWithoutControl(Object key) {
		int index = getIndexWithoutControl(key, this.firstKeys, this.secondKeys);
		if (index == 0) {
			// not found
			return null;
		}
		// first list positive, second list negative
		if (index > 0) {
			int k = index  - 1;
			this.firstKeys.remove(k);
			this.firstTimestamps.remove(k);
			return this.firstValues.remove(k);
		}
		int k = -index  - 1;
		this.secondKeys.remove(k);
		this.secondTimestamps.remove(k);
		return this.secondValues.remove(k);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		long currentTime = System.currentTimeMillis();
		Object oldValue = removeControl(key, currentTime);
		long timestamp = currentTime + this.timeOut;
		Long myTimestamp = new Long(timestamp);
		this.firstKeys.add(key);
		this.firstValues.add(value);
		this.firstTimestamps.add( myTimestamp);
		return oldValue;
	}
	
	private void testImplementation() {
		put("weser1", "hallo1");
		Object response1 = get("weser1");
		System.out.println(response1);    
		values();
		keySet();
		
		// ++++++++++++++++++++++++++++++++++++++++++++++++++
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException ex) {
			// do nothing
		}
		// ++++++++++++++++++++++++++++++++++++++++++++++++++

		
		System.out.println("5000");
		put("weser2","hallo2");
		
		Set aSet = entrySet();
		Iterator iterator = aSet.iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			System.out.print(key + " " + value);
		}
		System.out.println("Loop zuende");
		values();
		keySet();
		
		remove("weser2");
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException ex) {
			// do nothing
		}
		// ++++++++++++++++++++++++++++++++++++++++++++++++++

		
		System.out.println("....5000");
		aSet = entrySet();
		iterator = aSet.iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			System.out.print(key + " " + value);
		}
		System.out.println("Loop zuende");
		values();
		keySet();
		
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++++
		try {
			// set to 4000 and watch what happens
			Thread.sleep(5000);
		}
		catch (InterruptedException ex) {
			// do nothing
		}
		// ++++++++++++++++++++++++++++++++++++++++++++++++++

		
		System.out.println("");
		System.out.println("...5000");
		aSet = entrySet();
		iterator = aSet.iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			System.out.print(key + " " + value);
		}
		System.out.println("Loop zuende");
		values();
		keySet();
	}
	
}