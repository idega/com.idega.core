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
public class HashMatrix<xK, yK, V> {

  protected Map<xK, Map<yK, V>> xDimension;

  public V put(xK xKey, yK yKey, V value)  {
    Map<yK, V> yDimension = getYDimension(xKey);
    // retrieve old value first
    V oldValue = yDimension.get(yKey);
    // store new value
    yDimension.put(yKey, value);
    // return old value
    return oldValue;
  }

  public boolean containsKey(xK xKey, yK yKey) {
  	return (! isEmpty()) && this.xDimension.containsKey(xKey) && 	(get(xKey).containsKey(yKey));
  }

  public V get(xK xKey, yK yKey) {
    return getYDimension(xKey).get(yKey);
  }

  public V remove(xK xKey, yK yKey) {
    Map<yK, V> yMap = getYDimension(xKey);
    V oldObject = yMap.remove(yKey);
    if (yMap.isEmpty()) {
      this.xDimension.remove(xKey);
    }
    return oldObject;
  }

  public Map<yK, V> get(xK xKey)  {
    return getYDimension(xKey);
  }

  public List<V> getCopiedListOfValues() {
    List<V> values = new ArrayList<V>();
    if (this.xDimension == null) {
      return values;
    }

    for (Iterator<Map<yK, V>> xIterator = this.xDimension.values().iterator(); xIterator.hasNext();)  {
      Map<yK, V> yDimension = xIterator.next();
      for ( Iterator<V> yIterator = yDimension.values().iterator(); yIterator.hasNext();) {
        V value = yIterator.next();
        values.add(value);
      }
    }
    return values;
  }

  public Set<xK> firstKeySet()  {
    return (this.xDimension == null) ? new HashSet<xK>(0) : this.xDimension.keySet();
  }

  public int sizeOfFirstKeySet() {
  	return (this.xDimension == null) ? 0 : this.xDimension.size();
  }


  /** Returns true if this matrix contains no key-key-value mappings
   *
   * @return true if this matrix contains no key-key-value-mappings
   */
  public boolean isEmpty()  {
    return ( (this.xDimension == null)  || ( this.xDimension.isEmpty() ) );
  }

  protected Map<yK, V> getYDimension(xK xKey)  {
    if (this.xDimension == null) {
      this.xDimension = new HashMap<xK, Map<yK, V>>();
    }
    Map<yK, V> yDimension = this.xDimension.get(xKey);
    if (yDimension == null) {
      yDimension = new HashMap<yK, V>();
      this.xDimension.put(xKey, yDimension);
    }
    return yDimension;
  }
}

