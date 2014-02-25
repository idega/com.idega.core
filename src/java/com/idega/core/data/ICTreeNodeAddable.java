package com.idega.core.data;


/**
 * An abstract data model for implementing Tree structures.
*@author <a href="mailto:aron@idega.is">Aron Birkir</a>
*@version 1.0
*/

public interface ICTreeNodeAddable<Node extends ICTreeNode<?>> extends ICTreeNode<Node> {

	/**
 	*  Adds a child to this nodes children.
 	*/
	public void addChild(ICTreeNodeAddable<Node> child);

	public void setParent(ICTreeNode<Node> parent);
}
