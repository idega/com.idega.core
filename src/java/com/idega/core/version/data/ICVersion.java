package com.idega.core.version.data;


public interface ICVersion extends com.idega.data.IDOEntity,com.idega.core.data.ICTreeNode
{
 public boolean getAllowsChildren();
 public com.idega.core.data.ICTreeNode getChildAtIndex(int p0);
 public int getChildCount();
 public java.util.Iterator getChildren();
 public com.idega.user.data.User getCreatedByUser();
 public int getCreatedByUserID();
 public java.sql.Timestamp getCreatedTimestamp();
 public java.lang.String getDescription();
 public int getIndex(com.idega.core.data.ICTreeNode p0);
 public java.lang.String getName();
 public int getNodeID();
 public java.lang.String getNodeName();
 public java.lang.String getNumber();
 public com.idega.core.data.ICTreeNode getParentNode();
 public com.idega.core.version.data.ICVersion getParentVersion();
 public int getParentVersionID();
 public int getSiblingCount();
 public void initializeAttributes();
 public boolean isLeaf();
 public void setCreatedByUser(com.idega.user.data.User p0);
 public void setCreatedByUser(int p0);
 public void setCreatedTimestamp(java.sql.Timestamp p0);
 public void setDescription(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setNumber(java.lang.String p0);
 public void setParentVersion(com.idega.core.version.data.ICVersion p0);
 public void setParentVersionID(int p0);
}
