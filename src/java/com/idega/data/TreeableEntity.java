package com.idega.data;

import java.util.Collection;
import java.util.Iterator;

import com.idega.core.data.ICTreeNode;

public interface TreeableEntity<Node extends ICTreeNode<?>> extends IDOEntity, ICTreeNode<Node> {
 public void addChild(TreeableEntity<Node> p0)throws java.sql.SQLException;
 public Iterator<TreeableEntity<Node>> getChildrenIterator(java.lang.String p0);
 public Iterator<TreeableEntity<Node>> getChildrenIterator(java.lang.String p0, boolean p1);
 @Override
 public int getIndex(Node p0);
 @Override
 public Collection<Node> getChildren();
 public TreeableEntity<Node> getParentEntity();
 public java.lang.String getTreeRelationshipChildColumnName(TreeableEntity<Node> p0);
 public java.lang.String getTreeRelationshipTableName(TreeableEntity<Node> p0);
 public void moveChildrenFrom(TreeableEntity<Node> p0)throws java.sql.SQLException;
 public void removeChild(TreeableEntity<Node> p0)throws java.sql.SQLException;
 public boolean leafsFirst();
 public boolean sortLeafs();
 public void setLeafsFirst(boolean b);
 public void setToSortLeafs(boolean b);
}