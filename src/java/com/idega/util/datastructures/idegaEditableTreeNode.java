//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.util.datastructures;

import java.util.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public interface idegaEditableTreeNode extends idegaTreeNode{


  /**
   * adds a idegaTreeNode as a child
   */
  public void addChild(idegaTreeNode nodeToAdd);

  /**
   * adds a List of idegaTreeNodes
   */
  public void addChildren(List children);


  /**
   * Removes a child with a specific index and its children recursively
   */
  public void deleteChild(int childIndex);

  /**
   * Removes a List of idegaTreeNodes and their children recursively
   */
  public void deleteChildren(List children);

  /**
   * Removes from tree and removes all children recursively
   */
  public void deleteWithChildren();

  /**
   * Removes from tree without removing all children recursively
   */
  public void removeFromTree();

}
