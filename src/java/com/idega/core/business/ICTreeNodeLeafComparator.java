package com.idega.core.business;

import java.util.Comparator;

import com.idega.core.data.ICTreeNode;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author       <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class ICTreeNodeLeafComparator<Node extends ICTreeNode<?>> implements Comparator<Node> {

  boolean _leafsFirst = false;

  public ICTreeNodeLeafComparator(){
    this(false);
  }

  public ICTreeNodeLeafComparator(boolean leafsFirst){
    this._leafsFirst = leafsFirst;
  }

  @Override
public int compare(Node n1, Node n2) {
      int result = 0;

      boolean n1Leaf = n1.isLeaf();
      boolean n2Leaf = n2.isLeaf();

      if(n1Leaf == n2Leaf){
        result = 0;
      } else if(n1Leaf){
        result = (this._leafsFirst)?-1:1;
      } else {
        result = (this._leafsFirst)?1:-1;
      }

      return result;
  }


  @Override
public boolean equals(Object obj) {
    /**@todo: Implement this java.util.Comparator method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }

}
