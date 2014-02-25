/*
 * $Id: DefaultTreeNode.java,v 1.2 2006/05/31 11:12:02 laddi Exp $
 * Created on 26.5.2006 in project com.idega.core
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.idegaweb.IWApplicationContext;


/**
 * <p>
 * Default implementation of the ICTreeNode interface
 * </p>
 *  Last modified: $Date: 2006/05/31 11:12:02 $ by $Author: laddi $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 * @param <Node>
 */
public class DefaultTreeNode<Node extends ICTreeNode<?>> implements ICTreeNode<Node> {

	protected List<Node> children = new ArrayList<Node>();
	protected DefaultTreeNode<Node> parentNode = null;
	protected String name;
	protected String id;

	public DefaultTreeNode() {
		this("untitled", 0);
	}

	public DefaultTreeNode(String nodeName, int id) {
		this(nodeName,Integer.toString(id));
	}

	public DefaultTreeNode(String nodeName, String id) {
		this.name = nodeName;
		this.id = id;
	}

	/**
	 * Returns the children of the reciever as an Iterator.
	 */
	@Override
	public Iterator<Node> getChildrenIterator() {
	    Iterator<Node> it = null;
	    Collection<Node> children = getChildren();
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it;
	}

	/**
	 * Returns the children of the reciever as a Collection.
	 */
	@Override
	public Collection<Node> getChildren() {
		if (this.children != null) {
			return this.children;
		} else {
			return null;
		}
	}

	/**
	 *  Returns true if the receiver allows children.
	 */
	@Override
	public boolean getAllowsChildren() {
		if (this.children != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *  Returns the child TreeNode at index childIndex.
	 */
	@Override
	public Node getChildAtIndex(int childIndex) {
		return this.children.get(childIndex);
	}

	/**
	 *    Returns the number of children TreeNodes the receiver contains.
	 */
	@Override
	public int getChildCount() {
		return this.children.size();
	}

	/**
	 * Returns the index of node in the receivers children.
	 */
	@Override
	public int getIndex(Node node) {
		return this.children.indexOf(node);
	}

	/**
	 *  Returns the parent TreeNode of the receiver.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Node getParentNode() {
		return (Node) this.parentNode;
	}

	/**
	 *  Returns true if the receiver is a leaf.
	 */
	@Override
	public boolean isLeaf() {
		return (this.getChildCount() == 0);
	}

	/**
	 *  Returns the name of the Node
	 */
	@Override
	public String getNodeName() {
		return this.name;
	}

	/**
	 *  Returns the name of the Node
	 */
	@Override
	public String getNodeName(Locale locale ) {
		return getNodeName();
	}

	/**
	 *  Returns the name of the Node
	 */
	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac ) {
		return getNodeName(locale);
	}

	/**
	 * Returns the unique ID of the Node in the tree
	 */
	@Override
	public int getNodeID() {
		return Integer.parseInt(getId());
	}

	/**
	 * @return the number of siblings this node has
	 */
	@Override
	public int getSiblingCount() {
		try {
			return this.getParentNode().getChildCount() - 1;
		} catch (Exception ex) {
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	public void addTreeNode(Node node) {
		if (node instanceof DefaultTreeNode) {
			((DefaultTreeNode<Node>) node).setParentNode(this);
		}
		this.children.add(node);
	}

	public void setParentNode(DefaultTreeNode<Node> node) {
		this.parentNode = node;
	}

	public void clear() {
		if (this.children != null) {
			this.children.clear();
		}
	}

	@Override
	public String getId(){
		return this.id;
	}

}