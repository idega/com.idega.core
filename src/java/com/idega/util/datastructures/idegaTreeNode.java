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
public interface idegaTreeNode{

/**
 * Returns the children of the reciever as an Enumeration.
 */
public Enumeration children();
/**
 *  Returns true if the receiver allows children.
 */
public boolean getAllowsChildren();

/**
 *  Returns the child TreeNode at index childIndex.
 */
public idegaTreeNode getChildAt(int childIndex);

/**
 *    Returns the number of children TreeNodes the receiver contains.
 */
public int getChildCount();

/**
 * Returns the index of node in the receivers children.
 */
public int getIndex(idegaTreeNode node);

/**
 *  Returns the parent TreeNode of the receiver.
 */
public idegaTreeNode getParent();

/**
 *  Returns true if the receiver is a leaf.
 */
public boolean isLeaf();

/**
 *  Returns the name of the Node
 */
public String getNodeName();



}
