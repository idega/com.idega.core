//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.core;

import java.util.*;
import com.idega.util.datastructures.idegaTreeNode;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public interface ICTreeNode{

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
public ICTreeNode getChildAt(int childIndex);

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
public ICTreeNode getParent();

/**
 *  Returns true if the receiver is a leaf.
 */
public boolean isLeaf();

/**
 *  Returns the name of the Node
 */
public String getNodeName();



}
