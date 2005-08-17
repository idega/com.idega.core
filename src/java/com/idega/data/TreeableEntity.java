package com.idega.data;


public interface TreeableEntity extends com.idega.data.IDOEntity,com.idega.core.data.ICTreeNode
{
 public void addChild(com.idega.data.TreeableEntity p0)throws java.sql.SQLException;
 public java.util.Iterator getChildrenIterator(java.lang.String p0);
 public java.util.Iterator getChildrenIterator(java.lang.String p0, boolean p1);
 public int getIndex(com.idega.core.data.ICTreeNode p0);
 public com.idega.data.TreeableEntity getParentEntity();
 public java.lang.String getTreeRelationshipChildColumnName(com.idega.data.TreeableEntity p0);
 public java.lang.String getTreeRelationshipTableName(com.idega.data.TreeableEntity p0);
 public void moveChildrenFrom(com.idega.data.TreeableEntity p0)throws java.sql.SQLException;
 public void removeChild(com.idega.data.TreeableEntity p0)throws java.sql.SQLException;
 public boolean leafsFirst();
 public boolean sortLeafs();
 public void setLeafsFirst(boolean b);
 public void setToSortLeafs(boolean b);

}
