/*
 * $Id: GroupHome.java,v 1.31 2008/08/12 13:52:56 valdas Exp $
 * Created on Nov 16, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.core.builder.data.ICDomain;
import com.idega.data.IDOException;
import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2008/08/12 13:52:56 $ by $Author: valdas $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.31 $
 */
public interface GroupHome extends IDOHome {

	public Group create() throws javax.ejb.CreateException;

	public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbCreateGroup
	 */
	public Group createGroup() throws CreateException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByName
	 */
	public Collection findGroupsByName(String name) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByNameAndGroupType
	 */
	public Collection findGroupsByNameAndGroupTypes(String name, Collection groupTypes, boolean onlyReturnTypesInCollection) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByGroupTypeAndLikeName
	 */
	public Collection findGroupsByGroupTypeAndLikeName(String groupType, String partOfGroupName) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByAbbreviation
	 */
	public Collection findGroupsByAbbreviation(String abbreviation) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByNameAndDescription
	 */
	public Collection findGroupsByNameAndDescription(String name, String description) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupByPrimaryKey
	 */
	public Group findGroupByPrimaryKey(Object primaryKey) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContainedTemp
	 */
	public Collection findGroupsContainedTemp(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContained
	 */
	public Collection findGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContainedIDs
	 */
	public Collection findGroupsContainedIDs(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContained
	 */
	public Collection findGroupsContained(Group containingGroup, Group groupTypeProxy) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbHomeGetNumberOfGroupsContained
	 */
	public int getNumberOfGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException, IDOException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbHomeGetNumberOfVisibleGroupsContained
	 */
	public int getNumberOfVisibleGroupsContained(Group containingGroup) throws FinderException, IDOException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindTopNodeGroupsContained
	 */
	public Collection findTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbHomeGetNumberOfTopNodeGroupsContained
	 */
	public int getNumberOfTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes)
			throws FinderException, IDOException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbHomeGetNumberOfTopNodeVisibleGroupsContained
	 */
	public int getNumberOfTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException, IDOException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindTopNodeVisibleGroupsContained
	 */
	public Collection findTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindAllGroups
	 */
	public Collection findAllGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbHomeGetGroupType
	 */
	public String getGroupType();

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbHomeGetRelationTypeGroupParent
	 */
	public String getRelationTypeGroupParent();

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroups
	 */
	public Collection findGroups(String[] groupIDs) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByType
	 */
	public Collection findGroupsByType(String type) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByMetaData
	 */
	public Collection findGroupsByMetaData(String key, String value) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindSystemUsersGroup
	 */
	public Group findSystemUsersGroup() throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsRelationshipsByRelatedGroup
	 */
	public Collection findGroupsRelationshipsByRelatedGroup(int groupID, String relationType, String orRelationType)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindParentGroups
	 */
	public Collection findParentGroups(int groupID) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindByHomePageID
	 */
	public Group findByHomePageID(int pageID) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupByUniqueId
	 */
	public Group findGroupByUniqueId(String uniqueIdString) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByMetaData
	 */
	public Group findBoardGroupByClubIDAndLeagueID(Integer clubID, Integer leagueID) throws FinderException;
	
	public Collection<Group> findAllByNamePhrase(String phrase, Locale locale) throws FinderException;
}
