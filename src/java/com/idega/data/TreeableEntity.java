package com.idega.data;

import javax.ejb.*;

public interface TreeableEntity extends com.idega.data.IDOLegacyEntity,com.idega.core.ICTreeNode
{
 public void addChild(com.idega.data.TreeableEntity p0)throws java.sql.SQLException;
 public boolean getAllowsChildren();
 public com.idega.core.ICTreeNode getChildAtIndex(int p0);
 public int getChildCount();
 public java.util.Iterator getChildren(java.lang.String p0);
 public java.util.Iterator getChildren();
 public int getIndex(com.idega.core.ICTreeNode p0);
 public int getNodeID();
 public java.lang.String getNodeName();
 public com.idega.data.TreeableEntity getParentEntity();
 public com.idega.core.ICTreeNode getParentNode();
 public int getSiblingCount();
 public java.lang.String getTreeRelationshipChildColumnName(com.idega.data.TreeableEntity p0);
 public java.lang.String getTreeRelationshipTableName(com.idega.data.TreeableEntity p0);
 public boolean isLeaf();
 public void moveChildrenFrom(com.idega.data.TreeableEntity p0)throws java.sql.SQLException;
 public void removeChild(com.idega.data.TreeableEntity p0)throws java.sql.SQLException;
}
