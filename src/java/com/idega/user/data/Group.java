/*
 * $Id: Group.java,v 1.51 2008/09/10 11:59:07 juozas Exp $
 * Created on Nov 16, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressType;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.data.MetaDataCapable;
import com.idega.data.UniqueIDCapable;
import com.idega.data.query.SelectQuery;
import com.idega.idegaweb.IWApplicationContext;


/**
 *
 *  Last modified: $Date: 2008/09/10 11:59:07 $ by $Author: juozas $
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.51 $
 */
public interface Group extends IDOEntity, ICTreeNode<Group>, MetaDataCapable, UniqueIDCapable, GroupNode<Group> {

	/**
	 * @see com.idega.user.data.GroupBMPBean#getGroupTypeValue
	 */
	public String getGroupTypeValue();
	
	/**
	 * @see com.idega.user.data.GroupBMPBean#getGroupTypeKey
	 */
	public String getGroupTypeKey();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getGroupTypeDescription
	 */
	public String getGroupTypeDescription();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getGroupType
	 */
	public String getGroupType();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getGroupTypeEntity
	 */
	public GroupType getGroupTypeEntity();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setGroupType
	 */
	public void setGroupType(String groupType);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setGroupType
	 */
	public void setGroupType(GroupType groupType);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setAliasID
	 */
	public void setAliasID(int id);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setAlias
	 */
	public void setAlias(Group alias);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getAliasID
	 */
	public int getAliasID();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getAlias
	 */
	public Group getAlias();

	/**
	 * @see com.idega.user.data.GroupBMPBean#isAlias
	 */
	public boolean isAlias();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setPermissionControllingGroupID
	 */
	public void setPermissionControllingGroupID(int id);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setPermissionControllingGroup
	 */
	public void setPermissionControllingGroup(Group controllingGroup);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getPermissionControllingGroupID
	 */
	public int getPermissionControllingGroupID();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getPermissionControllingGroup
	 */
	public Group getPermissionControllingGroup();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setShortName
	 */
	public void setShortName(String shortName);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setAbbrevation
	 */
	public void setAbbrevation(String abbr);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getDescription
	 */
	public String getDescription();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setDescription
	 */
	public void setDescription(String description);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getExtraInfo
	 */
	public String getExtraInfo();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setExtraInfo
	 */
	public void setExtraInfo(String extraInfo);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getCreated
	 */
	public Timestamp getCreated();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setCreated
	 */
	public void setCreated(Timestamp created);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getHomePageID
	 */
	public int getHomePageID();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getHomePage
	 */
	public ICPage getHomePage();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setHomePageID
	 */
	public void setHomePageID(int pageID);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setHomePageID
	 */
	public void setHomePageID(Integer pageID);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setHomePage
	 */
	public void setHomePage(ICPage page);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getHomeFolderID
	 */
	public int getHomeFolderID();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getHomeFolder
	 */
	public ICFile getHomeFolder();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setHomeFolderID
	 */
	public void setHomeFolderID(int fileID);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setHomeFolderID
	 */
	public void setHomeFolderID(Integer fileID);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setHomeFolder
	 */
	public void setHomeFolder(ICFile file);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getShortName
	 */
	public String getShortName();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getAbbrevation
	 */
	public String getAbbrevation();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getHomePageURL
	 */
	public String getHomePageURL();

	/**
	 * @see com.idega.user.data.GroupBMPBean#setHomePageURL
	 */
	public void setHomePageURL(String homePage);

	/**
	 * @see com.idega.user.data.GroupBMPBean#setIsPermissionControllingGroup
	 */
	public void setIsPermissionControllingGroup(boolean isControlling);

	/**
	 * @see com.idega.user.data.GroupBMPBean#isPermissionControllingGroup
	 */
	public boolean isPermissionControllingGroup();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getParentGroups
	 */
	public List<Group> getParentGroups() throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getParentGroups
	 */
	public List<Group> getParentGroups(Map<String, Collection<Integer>> cachedParents, Map<String, Group> cachedGroups) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildGroups
	 */
	public List<Group> getChildGroups() throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildGroups
	 */
	public List<Group> getChildGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildGroupsIDs
	 */
	public List<Integer> getChildGroupsIDs(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildGroups
	 */
	public Collection<Group> getChildGroups(Group groupTypeProxy) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getAllGroupsContainingUser
	 */
	public Collection<Group> getAllGroupsContainingUser(User user) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addGroup
	 */
	public void addGroup(Group groupToAdd) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addGroup
	 */
	public void addGroup(User userToAdd) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addGroup
	 */
	public void addGroup(Group groupToAdd, Timestamp time) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addGroup
	 */
	public void addGroup(int groupId, Timestamp time) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addGroup
	 */
	public void addGroup(int groupId) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addRelation
	 */
	public void addRelation(Group groupToAdd, String relationType) throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addRelation
	 */
	public void addRelation(Group groupToAdd, GroupRelationType relationType) throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addRelation
	 */
	public void addRelation(int relatedGroupId, GroupRelationType relationType) throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addRelation
	 */
	public void addRelation(int relatedGroupId, String relationType) throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addUniqueRelation
	 */
	public void addUniqueRelation(int relatedGroupId, String relationType, Timestamp time) throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addUniqueRelation
	 */
	public void addUniqueRelation(int relatedGroupId, String relationType) throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addUniqueRelation
	 */
	public void addUniqueRelation(Group relatedGroup, String relationType) throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeRelation
	 */
	public void removeRelation(Group relatedGroup, String relationType) throws RemoveException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeRelation
	 */
	public void removeRelation(int relatedGroupId, String relationType) throws RemoveException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeRelation
	 */
	public void removeRelation(Group relatedGroup, String relationType, User performer) throws RemoveException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeRelation
	 */
	public void removeRelation(int relatedGroupId, String relationType, User performer) throws RemoveException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getRelatedBy
	 */
	public Collection<Group> getRelatedBy(GroupRelationType relationType) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getRelatedBy
	 */
	public Collection<Group> getRelatedBy(String relationType) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getReverseRelatedBy
	 */
	public Collection<Group> getReverseRelatedBy(String relationType) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeUser
	 */
	public void removeUser(User user, User currentUser) throws RemoveException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeUser
	 */
	public void removeUser(User user, User currentUser, Timestamp time) throws RemoveException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#hasRelationTo
	 */
	public boolean hasRelationTo(Group group);

	/**
	 * @see com.idega.user.data.GroupBMPBean#hasRelationTo
	 */
	public boolean hasRelationTo(int groupId);

	/**
	 * @see com.idega.user.data.GroupBMPBean#hasRelationTo
	 */
	public boolean hasRelationTo(int groupId, String relationType);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildrenIterator
	 */
	@Override
	public Iterator<Group> getChildrenIterator();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildren
	 */
	@Override
	public Collection<Group> getChildren();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getAllowsChildren
	 */
	@Override
	public boolean getAllowsChildren();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildAtIndex
	 */
	@Override
	public Group getChildAtIndex(int childIndex);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getChildCount
	 */
	@Override
	public int getChildCount();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getIndex
	 */
	@Override
	public int getIndex(Group group);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getParentNode
	 */
	@Override
	public Group getParentNode();

	/**
	 * @see com.idega.user.data.GroupBMPBean#isLeaf
	 */
	@Override
	public boolean isLeaf();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getNodeName
	 */
	@Override
	public String getNodeName();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getNodeName
	 */
	@Override
	public String getNodeName(Locale locale);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getNodeName
	 */
	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getNodeID
	 */
	@Override
	public int getNodeID();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getSiblingCount
	 */
	@Override
	public int getSiblingCount();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getNodeType
	 */
	public int getNodeType();

	/**
	 * @see com.idega.user.data.GroupBMPBean#store
	 */
	@Override
	public void store();

	/**
	 * @see com.idega.user.data.GroupBMPBean#isUser
	 */
	public boolean isUser();

	/**
	 * @see com.idega.user.data.GroupBMPBean#addAddress
	 */
	public void addAddress(Address address) throws IDOAddRelationshipException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getAddresses
	 */
	public Collection<Address> getAddresses(AddressType addressType) throws IDOLookupException, IDOCompositePrimaryKeyException,
			IDORelationshipException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getPhones
	 */
	public Collection<Phone> getPhones();

	/**
	 * @see com.idega.user.data.GroupBMPBean#getPhones
	 */
	public Collection<Phone> getPhones(String phoneTypeID);

	/**
	 * @see com.idega.user.data.GroupBMPBean#getEmails
	 */
	public Collection<Email> getEmails();

	/**
	 * @see com.idega.user.data.GroupBMPBean#addEmail
	 */
	public void addEmail(Email email) throws IDOAddRelationshipException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#addPhone
	 */
	public void addPhone(Phone phone) throws IDOAddRelationshipException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeGroup
	 */
	public void removeGroup(Group entityToRemoveFrom, User currentUser) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeGroup
	 */
	public void removeGroup(User currentUser) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeGroup
	 */
	public void removeGroup(int relatedGroupId, User currentUser, boolean AllEntries, Timestamp time)
			throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#removeGroup
	 */
	public void removeGroup(int relatedGroupId, User currentUser, boolean AllEntries) throws EJBException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#getSelectQueryConstraints
	 */
	public SelectQuery getSelectQueryConstraints();


	public User getModerator();

	public void setModerator(User moderator);
	public Collection<Group> getRelated(Collection<String> relationTypes);

}
