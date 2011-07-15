//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.core.data;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import com.idega.idegaweb.IWApplicationContext;

/**
 * An abstract data model for implementing Tree structures.
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public interface ICTreeNode {

	/**
	 * Returns the children of the receiver as an Collection.
	 */
	public <T extends ICTreeNode> Collection<T> getChildren();

	/**
	 * Returns the children of the receiver as an Enumeration.
	 */
	public <T extends ICTreeNode> Iterator<T> getChildrenIterator();
	/**
	 *  Returns true if the receiver allows children.
	 */
	public boolean getAllowsChildren();

	/**
	 *  Returns the child TreeNode at index childIndex.
	 */
	public ICTreeNode getChildAtIndex(int childIndex);

	/**
	 *    Returns the number of children TreeNodes the receiver contains.
	 */
	public int getChildCount();

	/**
	 * Returns the index of node in the receivers children.
	 */
	public int getIndex(ICTreeNode node);

	/**
	 *  Returns the parent TreeNode of the receiver.
	 */
	public ICTreeNode getParentNode();

	/**
	 *  Returns true if the receiver is a leaf.
	 */
	public boolean isLeaf();

	/**
	 *  Returns the name of the Node
	 */
	public String getNodeName();

	/**
	 *  @return Returns the name of the Node localized, if localization possible, else it returns getNodeName()
	 */
	public String getNodeName(Locale locale);

	/**
	 *  @return Returns the name of the Node localized from the bundle, if localization possible, else it returns getNodeName(Locale locale)
	 */
	public String getNodeName(Locale locale, IWApplicationContext iwac);


	/**
	 * @deprecated Replaced with getId()
	 */
	@Deprecated
	public int getNodeID();

	/**
	 * @return the number of siblings this node has
	 */
	public int getSiblingCount();

	/**
	 * Returns the unique ID of the Node in the tree
	 * @return the id as a string
	 */
	public String getId();

}