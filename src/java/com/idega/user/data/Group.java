package com.idega.user.data;

import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.IDOAddRelationshipException;


public interface Group extends com.idega.data.IDOEntity, com.idega.core.ICTreeNode {
	//public int getSiblingCount();
	//public int getChildCount();
	public void removeUser(com.idega.user.data.User p0, com.idega.user.data.User p1);
	public void removeGroup(com.idega.user.data.User p0) throws javax.ejb.EJBException;
	public void setGroupType(java.lang.String p0);
	//public java.util.Iterator getChildren();
	public java.lang.String getGroupTypeValue();
	public void setExtraInfo(java.lang.String p0);
	public void removeGroup(int p0,com.idega.user.data.User p1,  boolean p2) throws javax.ejb.EJBException;
	//public boolean equals(com.idega.user.data.Group p0);
	//public boolean isLeaf();
	public void setDescription(java.lang.String p0);
	//public com.idega.core.ICTreeNode getChildAtIndex(int p0);
	//public int getIndex(com.idega.core.ICTreeNode p0);
	// public boolean equals(com.idega.data.IDOLegacyEntity p0);
	//public boolean getAllowsChildren();
	public void addGroup(com.idega.user.data.Group p0) throws  javax.ejb.EJBException;
	public java.util.List getChildGroups(java.lang.String[] p0, boolean p1) throws javax.ejb.EJBException;
	//public java.util.List getListOfAllGroupsContaining(int p0)throws javax.ejb.EJBException;
	public void addGroup(int p0) throws javax.ejb.EJBException;
	public java.util.List getChildGroups() throws javax.ejb.EJBException;
	public java.util.Collection getAllGroupsContainingUser(com.idega.user.data.User p0) throws  javax.ejb.EJBException;
	//public void setDefaultValues();
	public java.lang.String getDescription();
	public void removeGroup(com.idega.user.data.Group p0, com.idega.user.data.User p1) throws  javax.ejb.EJBException;
	public java.lang.String getGroupType();
	public java.lang.String getName();
	//public int getNodeID();
	public void setName(java.lang.String p0);
	public java.util.List getParentGroups() throws javax.ejb.EJBException;
	public java.lang.String getExtraInfo();
	//public com.idega.core.ICTreeNode getParentNode();
	//public java.lang.String getNodeName();
	// public void addUser(com.idega.user.data.User p0)throws java.rmi.RemoteException;

	public void addRelation(Group groupToAdd, String relationType) throws CreateException;
	public void addRelation(Group groupToAdd, GroupRelationType relationType) throws CreateException;
	public void addRelation(int relatedGroupId, GroupRelationType relationType) throws CreateException;
	public void addRelation(int relatedGroupId, String relationType) throws CreateException;
	public void removeRelation(Group relatedGroup, String relationType) throws RemoveException;
	public void removeRelation(int relatedGroupId, String relationType) throws RemoveException;
	public void addUniqueRelation(int relatedGroupId, String relationType) throws CreateException;
	public void addUniqueRelation(Group relatedGroup, String relationType) throws CreateException;
	public boolean hasRelationTo(int groupId, String relationType);
  /**
   * Returns a collection of Group objects that are related by the relation type relationType with this Group
   */
  public java.util.Collection getRelatedBy(GroupRelationType relationType)throws FinderException;

  /**
   * Returns a collection of Group objects that are related by the relation type relationType with this Group
   */
  public java.util.Collection getRelatedBy(String relationType)throws FinderException;

  public void setCreated(java.sql.Timestamp p0);
  public java.sql.Timestamp getCreated();
  public boolean hasRelationTo(Group group);

  public com.idega.builder.data.IBPage getHomePage();
  public int getHomePageID();
  public void setHomePage(com.idega.builder.data.IBPage p0);
  public void setHomePageID(int p0);
  public void setHomePageID(java.lang.Integer p0);
  public boolean isUser();
  public java.util.Collection getReverseRelatedBy(String relationType) throws FinderException;
  public void setMetaData(java.lang.String p0, java.lang.String p1); 
  public java.lang.String getMetaData(java.lang.String p0);
  public void addAddress(com.idega.core.data.Address p0) throws IDOAddRelationshipException;
  public java.util.Collection getPhones();
  public java.util.Collection getEmails();

	public void setAliasID(int id);
	public void setAlias(Group alias);
	public int getAliasID();
	public Group getAlias();
  public void addEmail(com.idega.core.data.Email email) throws IDOAddRelationshipException;
  public void addPhone(com.idega.core.data.Phone phone) throws IDOAddRelationshipException;
  
  public void setMetaDataAttributes(java.util.Hashtable p0);
  public java.util.Hashtable getMetaDataAttributes();
}

