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
import java.util.Collections;
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
	public Collection<Group> findGroupsByName(String name) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByNameAndGroupType
	 */
	public Collection<Group> findGroupsByNameAndGroupTypes(String name, Collection<?> groupTypes, boolean onlyReturnTypesInCollection) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByGroupTypeAndLikeName
	 */
	public Collection<Group> findGroupsByGroupTypeAndLikeName(String groupType, String partOfGroupName) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByAbbreviation
	 */
	public Collection<Group> findGroupsByAbbreviation(String abbreviation) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByNameAndDescription
	 */
	public Collection<Group> findGroupsByNameAndDescription(String name, String description) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupByPrimaryKey
	 */
	public Group findGroupByPrimaryKey(Object primaryKey) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContainedTemp
	 */
	public Collection<Group> findGroupsContainedTemp(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContained
	 */
	public Collection<Group> findGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContainedIDs
	 */
	public Collection<Integer> findGroupsContainedIDs(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsContained
	 */
	public Collection<? extends Group> findGroupsContained(Group containingGroup, Group groupTypeProxy) throws FinderException;

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
	public Collection<Group> findTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes)
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
	public Collection<Group> findTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindAllGroups
	 */
	public Collection<Group> findAllGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindAll
	 */
	public Collection<Group> findAll() throws FinderException;

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
	public Collection<Group> findGroups(String[] groupIDs) throws FinderException;

	/**
	 *
	 * @param groupIDs is {@link Collection} of {@link Group#getPrimaryKey()}s,
	 * not <code>null</code>;
	 * @return entities or {@link Collections#emptyList()} on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<Group> findGroups(Collection<String> groupIDs);

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByType
	 */
	public Collection<Group> findGroupsByType(String type) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsByMetaData
	 */
	public Collection<Group> findGroupsByMetaData(String key, String value) throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindSystemUsersGroup
	 */
	public Group findSystemUsersGroup() throws FinderException;

	/**
	 * @see com.idega.user.data.GroupBMPBean#ejbFindGroupsRelationshipsByRelatedGroup
	 */
	public Collection<GroupRelation> findGroupsRelationshipsByRelatedGroup(int groupID, String relationType, String orRelationType)
			throws FinderException;

	/**
	 *
	 * @param groupID is {@link Group#getPrimaryKey()} to search by,
	 * not <code>null</code>;
	 * @return {@link Collection} of {@link Group#getPrimaryKey()} or entities
	 * found or {@link Collections#emptyList()} on failure;
	 */
	Collection<Integer> findParentGroupKeys(int groupID);

	/**
	 *
	 * @param groupID is {@link Group#getPrimaryKey()} to search by,
	 * not <code>null</code>;
	 * @return parents without ancestors  of this {@link Group} or
	 * {@link Collections#emptyList()} on failure;
	 */
	Collection<Group> findParentGroups(int groupID);

	/**
	 *
	 * @param primaryKeys is {@link Collection} of {@link Group#getPrimaryKey()}
	 * to search by, not <code>null</code>;
	 * @return all ancestors of this {@link Group} or
	 * {@link Collections#emptyList()} on failure;
	 */
	Collection<Group> findParentGroupsRecursively(
			Collection<Integer> primaryKeys);

	/**
	 *
	 * @param primaryKeys is {@link Collection} of {@link Group#getPrimaryKey()}
	 * to search by, not <code>null</code>;
	 * @return {@link Collection} of {@link Group#getPrimaryKey()} or entities
	 * found or {@link Collections#emptyList()} on failure;
	 */
	Collection<Integer> findParentGroupsPrimaryKeysRecursively(
			Collection<Integer> primaryKeys);

	/**
	 *
	 * @param primaryKeys is {@link Collection} of {@link Group#getPrimaryKey()}
	 * to search by, not <code>null</code>;
	 * @return {@link Collection} of {@link Group#getPermissionControllingGroupID()}
	 * or {@link Collections#emptyList()} on failure;
	 */
	Collection<Integer> findPermissionGroupPrimaryKeys(Collection<Integer> primaryKeys);

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

	/**
	 * Gets groups by criterias mentioned above and orders by them descending
	 * <br/>Criterias: 			<br/>
	 * <ul>
	 * 		<li>Groups amount in group.</li>
	 * 		<li>Users amount in group.</li>
	 * </ul>
	 * @param amount the maximum number of groups that will be returned if less than or equals 0 returns all maches.
	 * @param types group types that will be returned if empty groups of all types will be returned.
	 * @return
	 */
	public Collection<Group> getMostPopularGroups(Collection<String> types,int amount) throws FinderException;

	/**
	 * Gets groups and returns them ordered by modification date descendant
	 * @param amount the maximum number of groups that will be returned if less than or equals 0 returns all maches.
	 * @param types group types that will be returned if empty groups of all types will be returned.
	 * @return
	 */
	public Collection<Group> getGroups(Collection<String> types,int amount) throws FinderException;

	/**Searches by:
	 * 		name,
	 * 		description
	 * @param request the request by which result will be searched
	 * @param amount the maximum number of groups that will be returned if less than or equals 0 returns all maches.
	 * @param types group types that will be returned if empty groups of all types will be returned.
	 * @return
	 */
	public Collection<Group> getGroupsBySearchRequest(String request, Collection <String> types,int amount) throws FinderException;

	public Collection<Group> getReverseRelatedBy(Integer groupId, String relationType) throws FinderException;

}