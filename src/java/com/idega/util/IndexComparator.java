package com.idega.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Title: com.idega.util.IndexComparator
 * Description: Compares objects that implement the com.idega.util.Index interface and sorts
 * Copyright:    Copyright (c) 2002
 * Company:      idega software
 * @author       <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class IndexComparator implements Comparator {

  public static final int ORDER_BY_INDEX   = 1;
  public static final int REVERSE_ORDER_BY_INDEX   = 2;

  private int sortBy;

  public IndexComparator() {
      this.sortBy = ORDER_BY_INDEX;
  }

  public IndexComparator(int toSortBy) {
      this.sortBy = toSortBy;
  }

  public void sortBy(int toSortBy) {
      this.sortBy = toSortBy;
  }

  public int compare(Object o1, Object o2) {
      int result = 0;

      switch (this.sortBy) {
        case ORDER_BY_INDEX   : result = indexSort(o1,o2);
        break;
        case REVERSE_ORDER_BY_INDEX   : result = reverseIndexSort(o1,o2);
        break;
      }

      return result;
  }

  /**
   *  Sorts by indexes in low to high order
   */
  private int indexSort(Object o1, Object o2) {
    int result;
    Index p1 = (Index) o1;
    Index p2 = (Index) o2;
    if( p1.getIndex() > p2.getIndex() ){
      result = 1;
    }
    else if( p1.getIndex() < p2.getIndex() ){
      result = -1;
    }
	else {
		result = 0;
	}

    return result;
  }

  /**
   *  Sorts by indexes in high to low order
   */
  private int reverseIndexSort(Object o1, Object o2) {
    return (-1*indexSort(o1,o2));
  }

  public boolean equals(Object obj) {
    /**@todo: Implement this java.util.Comparator method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }

  public Iterator sort(Index[] indexes, int toSortBy) {
      this.sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < indexes.length; i++) {
          list.add(indexes[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public Iterator sort(Index[] indexes) {
      List list = new LinkedList();
      for(int i = 0; i < indexes.length; i++) {
          list.add(indexes[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public Index[] sortedArray(Index[] indexes, int toSortBy) {
      this.sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < indexes.length; i++) {
          list.add(indexes[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          indexes[i] = (Index) objArr[i];
      }
      return (indexes);
  }

  public Vector sortedArray(Vector list) {
      Collections.sort(list, this);
      return list;
  }

  public ArrayList sortedArrayList(ArrayList list) {
      Collections.sort(list, this);
      return list;
  }

  public Index[] sortedArray(Index[] indexes) {
      List list = new LinkedList();
      for(int i = 0; i < indexes.length; i++) {
          list.add(indexes[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          indexes[i] = (Index) objArr[i];
      }
      return (indexes);
  }

  public Index[] reverseSortedArray(Index[] indexes, int toSortBy) {
      this.sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < indexes.length; i++) {
          list.add(indexes[i]);
      }
      Collections.sort(list, this);
      Collections.reverse(list);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          indexes[i] = (Index) objArr[i];
      }
      return (indexes);
  }

}
