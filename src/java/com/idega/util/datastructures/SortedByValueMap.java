/*
 * Created on Dec 31, 2004
 *
 */
package com.idega.util.datastructures;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.idega.presentation.IWContext;

/**
 * @author Sigtryggur
 * This class constructs a new LinkedHashMap, from any Map, that is ordered by values, not by keys like the Maps implementing SortedMap. 
 */
public class SortedByValueMap extends LinkedHashMap {
    
    /**
     * Constructs new Map with the values sorted in alfabetical order using the default locale 
     * for the running instance of the Java Virtual Machine.
     *
     * @param  map the map containing the unsorted key-value pairs.
     * @throws NullPointerException if the specified map is null.
     */
    public SortedByValueMap(Map map) {
        this(map, null);
    }
    
    /**
     * Constructs new Map with the values sorted in alfabetical order using the current locale of the webapp.
     *
     * @param  map the map containing the unsorted key-value pairs.
     * @param  iwc is the IWContext containing the current locale of the webapp.
     * @throws NullPointerException if the specified map is null.
     */
    public SortedByValueMap(Map map, IWContext iwc) {
        Comparator comparator = new SortByValueComparator(map, iwc);
        List keys = new ArrayList(map.keySet());
        Collections.sort(keys, comparator);
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            put(key, map.get(key));
        }
    }
    
    private class SortByValueComparator implements Comparator {

    	private Map map;
    	private Collator collator = null;

    	public SortByValueComparator(Map map) {
    	    this.map = map;   	    
    	}

    	public SortByValueComparator(Map map, IWContext iwc) {
    	    this.map = map;
    	    if (iwc == null) {
    	        this.collator = Collator.getInstance();
    	    }
    	    else {
    	        this.collator = Collator.getInstance(iwc.getCurrentLocale());
    	    }
    	}

    	public int compare(Object o1, Object o2) {
    	    String s1 = (map.get(o1)).toString();
    	    String s2 = (map.get(o2)).toString();
    	    return collator.compare(s1,s2);
    	}
    }
}