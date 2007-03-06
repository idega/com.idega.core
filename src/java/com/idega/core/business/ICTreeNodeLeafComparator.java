package com.idega.core.business;

import java.util.Comparator;

import com.idega.core.data.ICTreeNode;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author       <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class ICTreeNodeLeafComparator implements Comparator {

  boolean _leafsFirst = false;

  public ICTreeNodeLeafComparator(){
    this(false);
  }

  public ICTreeNodeLeafComparator(boolean leafsFirst){
    this._leafsFirst = leafsFirst;
  }

  public int compare(Object o1, Object o2) {
      ICTreeNode p1 = (ICTreeNode) o1;
      ICTreeNode p2 = (ICTreeNode) o2;
      int result = 0;

      boolean p1Leaf = p1.isLeaf();
      boolean p2Leaf = p2.isLeaf();

      if(p1Leaf == p2Leaf){
        result = 0;
      } else if(p1Leaf){
        result = (this._leafsFirst)?-1:1;
      } else {
        result = (this._leafsFirst)?1:-1;
      }

      return result;
  }


  public boolean equals(Object obj) {
    /**@todo: Implement this java.util.Comparator method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }

}
