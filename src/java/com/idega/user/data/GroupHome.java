package com.idega.user.data;

import java.util.Collection;

import javax.ejb.FinderException;


public interface GroupHome extends com.idega.data.IDOHome
{
 public Group create() throws javax.ejb.CreateException;
 public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Group createGroup()throws javax.ejb.CreateException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;
 public java.util.Collection findAllGroups(java.lang.String[] p0,boolean p1)throws javax.ejb.FinderException;
 public Group findByHomePageID(int p0)throws javax.ejb.FinderException;
 public Group findByName(java.lang.String p0)throws javax.ejb.FinderException;
 public Group findGroupByPrimaryKey(java.lang.Object p0)throws javax.ejb.FinderException;
 public Group findGroupByUniqueId(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroups(java.lang.String[] p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsByMetaData(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsByType(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findGroupsContained(com.idega.user.data.Group p0,java.util.Collection p1,boolean p2)throws javax.ejb.FinderException;
 public Collection findGroupsContained(Group containingGroup, Group groupTypeProxy) throws FinderException;
 public java.util.Collection findGroupsRelationshipsByRelatedGroup(int p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException;
 public java.util.Collection findParentGroups(int p0)throws javax.ejb.FinderException;
 public Group findSystemUsersGroup()throws javax.ejb.FinderException;
 public java.util.Collection findTopNodeGroupsContained(com.idega.core.builder.data.ICDomain p0,java.util.Collection p1,boolean p2)throws javax.ejb.FinderException;
 public java.util.Collection findTopNodeVisibleGroupsContained(com.idega.core.builder.data.ICDomain p0)throws javax.ejb.FinderException;
 public java.util.Collection findByPrimaryKeyCollection(java.util.Collection p0)throws javax.ejb.FinderException;
 public java.lang.String getGroupType();
 public int getNumberOfGroupsContained(com.idega.user.data.Group p0,java.util.Collection p1,boolean p2)throws javax.ejb.FinderException,com.idega.data.IDOException;
 public int getNumberOfTopNodeGroupsContained(com.idega.core.builder.data.ICDomain p0,java.util.Collection p1,boolean p2)throws javax.ejb.FinderException,com.idega.data.IDOException;
 public int getNumberOfTopNodeVisibleGroupsContained(com.idega.core.builder.data.ICDomain p0)throws javax.ejb.FinderException,com.idega.data.IDOException;
 public int getNumberOfVisibleGroupsContained(com.idega.user.data.Group p0)throws javax.ejb.FinderException,com.idega.data.IDOException;
 public java.lang.String getRelationTypeGroupParent();

}