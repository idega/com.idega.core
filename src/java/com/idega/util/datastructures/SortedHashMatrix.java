package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.idega.util.text.StringNumberComparator;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: 
 * Represents a matrix or a two dimensional array.
 * The values are ordered by the first key and then by the second key.
 * This class uses the StringNumberComparator, that is 
 * a string that represents an integer is considered as an integer and not
 * as a string. 
 * For example key "2" is less than key "13".
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 11, 2003
 */
public class SortedHashMatrix {
  
  private SortedMap xDimension;

  public Object put(Object xKey, Object yKey, Object value)  {
    SortedMap yDimension = getYDimension(xKey);
    // retrieve old value first
    Object oldValue = yDimension.get(yKey);
    // store new value
    yDimension.put(yKey, value);
    // return old value
    return oldValue;
  }
  
  public Object get(Object xKey, Object yKey) {
    return getYDimension(xKey).get(yKey);
  }
  
  public List getCopiedListOfValues() {
    List values = new ArrayList();
    Iterator xIterator = xDimension.values().iterator();
    while (xIterator.hasNext())  {
      SortedMap yDimension = (SortedMap) xIterator.next();
      Iterator yIterator = yDimension.values().iterator();
      while (yIterator.hasNext()) {
        Object value = yIterator.next();
        values.add(value);
      }
    }
    return values;
  }
  
  public Set firstKeySet()  {
    return xDimension.keySet();
  }
    
  
  
  private SortedMap getYDimension(Object xKey)  {
    if (xDimension == null) {
      xDimension = new TreeMap(new StringNumberComparator());
      
    }
    SortedMap yDimension = (SortedMap) xDimension.get(xKey);
    if (yDimension == null) {
      yDimension = new TreeMap(new StringNumberComparator());
    }
    return yDimension;
  }
      
  
      
      
      
}
