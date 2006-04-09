package com.idega.util.datastructures;

import java.util.Map;

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
public class SortedHashMatrix extends HashMatrix {
  
 protected Map getYDimension(Object xKey)  {
    if (this.xDimension == null) {
      this.xDimension = new TreeMap(new StringNumberComparator());
    }
    SortedMap yDimension = (SortedMap) this.xDimension.get(xKey);
    if (yDimension == null) {
      yDimension = new TreeMap(new StringNumberComparator());
      this.xDimension.put(xKey, yDimension);
    }
    return yDimension;
  }
}
