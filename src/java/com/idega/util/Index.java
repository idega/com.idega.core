package com.idega.util;

/**
 * Title:        com.idega.util.Index
 * Description:  Interface for things that use an index for ordering
 * use the com.idega.IndexComparator for sorting.
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public interface Index {
  public void setIndex(int index);
  public int getIndex();
}