package com.idega.util.text;

import java.util.Comparator;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: 
 * A string that represents an integer is considered as an integer and not
 * as a string. 
 * For example: key "2" is less than key "13".
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 11, 2003
 */
public class StringNumberComparator implements Comparator {

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(Object first, Object second) {
    // if the strings represents integers compare the integers
    if (first instanceof String && second instanceof String)  {
      try {
        Integer firstInteger = new Integer((String)first);
        Integer secondInteger = new Integer((String)second);
        return firstInteger.compareTo(secondInteger);
      }
      catch (NumberFormatException ex)  {
      }
    }
    return ((Comparable) first).compareTo(second);
  }

  
}
