package com.idega.util.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * Represents a matrix or a two dimensional array.
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Aug 31, 2003
 */
public class HashMatrix {
  
  protected Map xDimension;

  public Object put(Object xKey, Object yKey, Object value)  {
    Map yDimension = getYDimension(xKey);
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
    if (xDimension == null) {
      return values;
    }
    Iterator xIterator = xDimension.values().iterator();
    while (xIterator.hasNext())  {
      Map yDimension = (Map) xIterator.next();
      Iterator yIterator = yDimension.values().iterator();
      while (yIterator.hasNext()) {
        Object value = yIterator.next();
        values.add(value);
      }
    }
    return values;
  }
  
  public Set firstKeySet()  {
    return (xDimension == null) ? new HashSet(0) : xDimension.keySet();
  }
 
  protected Map getYDimension(Object xKey)  {
    if (xDimension == null) {
      xDimension = new HashMap();
    }
    Map yDimension = (Map) xDimension.get(xKey);
    if (yDimension == null) {
      yDimension = new HashMap();
      xDimension.put(xKey, yDimension);
    }
    return yDimension;
  }
}
  

}
