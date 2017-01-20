/*
 * $Id: GroupHomeImpl.java,v 1.31 2008/08/12 13:52:55 valdas Exp $
 * Created on Nov 16, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.core.builder.data.ICDomain;
import com.idega.data.IDOEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOFactory;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;

/**
 *
 *  Last modified: $Date: 2008/08/12 13:52:55 $ by $Author: valdas $
 *
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.31 $
 */
public class GroupHomeImpl extends IDOFactory implements GroupHome {

	private static final long serialVersionUID = -2368235582273397196L;

	@Override
	protected Class<Group> getEntityInterfaceClass() {
		return Group.class;
	}

	@Override
	public Group create() throws javax.ejb.CreateException {
		return (Group) super.createIDO();
	}

	@Override
	public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Group) super.findByPrimaryKeyIDO(pk);
	}

	@Override
	public Group createGroup() throws CreateException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbCreateGroup();
		((GroupBMPBean) entity).ejbPostCreate();
		this.idoCheckInPooledEntity(entity);
		try {
			return this.findByPrimaryKey(pk);
		}
		catch (javax.ejb.FinderException fe) {
			throw new com.idega.data.IDOCreateException(fe);
		}
		catch (Exception e) {
			throw new com.idega.data.IDOCreateException(e);
		}
	}

	@Override
	public Collection<Group> findGroupsByName(String name) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsByName(name);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findGroupsByNameAndGroupTypes(String name, Collection groupTypes, boolean onlyReturnTypesInCollection) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsByNameAndGroupTypes(name, groupTypes, onlyReturnTypesInCollection);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findGroupsByGroupTypeAndLikeName(String groupType, String partOfGroupName) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsByGroupTypeAndLikeName(groupType,
				partOfGroupName);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findGroupsByAbbreviation(String abbreviation) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsByAbbreviation(abbreviation);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findGroupsByNameAndDescription(String name, String description) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsByNameAndDescription(name, description);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Group findGroupByPrimaryKey(Object primaryKey) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindGroupByPrimaryKey(primaryKey);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection<Group> findGroupsContainedTemp(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsContainedTemp(containingGroup, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsContained(containingGroup, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Integer> findGroupsContainedIDs(Group containingGroup, Collection groupTypes, boolean returnTypes)
		throws FinderException {
	    com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	    Collection<Integer> ids = ((GroupBMPBean) entity).ejbFindGroupsContained(containingGroup, groupTypes, returnTypes);
	    return ids;
}

	@Override
	public Collection<? extends Group> findGroupsContained(Group containingGroup, Group groupTypeProxy) throws FinderException {
		IDOEntity proxyEntity = this.idoCheckOutPooledEntity();

		IDOEntity entity = (groupTypeProxy != null && (groupTypeProxy instanceof User || groupTypeProxy.getType().equals(User.USER_GROUP_TYPE))) ?
				this.idoCheckOutPooledEntity(User.class) :
				this.idoCheckOutPooledEntity();
		Class<? extends IDOEntity> theClass = entity.getClass();
		java.util.Collection<Integer> ids = ((GroupBMPBean) proxyEntity).ejbFindGroupsContained(containingGroup, groupTypeProxy, theClass);
		this.idoCheckInPooledEntity(entity);

		this.idoCheckInPooledEntity(proxyEntity);

		return this.getEntityCollectionForPrimaryKeys(ids, theClass);
	}

	@Override
	public int getNumberOfGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfGroupsContained(containingGroup, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public int getNumberOfVisibleGroupsContained(Group containingGroup) throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfVisibleGroupsContained(containingGroup);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public Collection<Group> findTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindTopNodeGroupsContained(containingDomain, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public int getNumberOfTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes)
			throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfTopNodeGroupsContained(containingDomain, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public int getNumberOfTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfTopNodeVisibleGroupsContained(containingDomain);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public Collection<Group> findTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindTopNodeVisibleGroupsContained(containingDomain);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findAllGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindAllGroups(groupTypes, returnSepcifiedGroupTypes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public String getGroupType() {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupBMPBean) entity).ejbHomeGetGroupType();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public String getRelationTypeGroupParent() {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupBMPBean) entity).ejbHomeGetRelationTypeGroupParent();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public Collection<Group> findGroups(String[] groupIDs) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroups(groupIDs);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupHome#findGroups(java.util.Collection)
	 */
	@Override
	public Collection<Group> findGroups(Collection<String> groupIDs) {
		if (ListUtil.isEmpty(groupIDs)) {
			return Collections.emptyList();
		}

		try {
			return findGroups(ArrayUtil.convertListToArray(groupIDs));
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).warning(
					"Failed to get " + getEntityInterfaceClass().getSimpleName() +
					" by id's: '" + groupIDs +  "'");
		}

		return Collections.emptyList();
	}

	@Override
	public Collection<Group> findGroupsByType(String type) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsByType(type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> findGroupsByMetaData(String key, String value) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsByMetaData(key, value);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Group findSystemUsersGroup() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindSystemUsersGroup();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection<GroupRelation> findGroupsRelationshipsByRelatedGroup(int groupID, String relationType, String orRelationType)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((GroupBMPBean) entity).ejbFindGroupsRelationshipsByRelatedGroup(groupID,
				relationType, orRelationType);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupHome#findParentGroupKeys(int)
	 */
	@Override
	public Collection<Integer> findParentGroupKeys(int groupID) {
		GroupBMPBean entity = (GroupBMPBean) this.idoCheckOutPooledEntity();
		return entity.ejbFindParentGroups(Arrays.asList(groupID));
	}

	@Override
	public Collection<Integer> findParentGroupsKeys(List<Integer> groupsIDs) {
		GroupBMPBean entity = (GroupBMPBean) this.idoCheckOutPooledEntity();
		return entity.ejbFindParentGroups(groupsIDs);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupHome#findParentGroups(int)
	 */
	@Override
	public Collection<Group> findParentGroups(int groupID) {
		java.util.Collection<Integer> ids = findParentGroupKeys(groupID);
		if (ListUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}

		try {
			return getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"Failed to get groups by primary keys: " + ids);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupHome#findParentGroupsPrimaryKeysRecursively(java.util.Collection)
	 */
	@Override
	public Collection<Integer> findParentGroupsPrimaryKeysRecursively(
			Collection<Integer> primaryKeys) {
		GroupBMPBean entity = (GroupBMPBean) this.idoCheckOutPooledEntity();

		return entity
				.ejbFindParentGroupsRecursively(primaryKeys);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.data.GroupHome#findParentGroupsRecursively(java.util.Collection)
	 */
	@Override
	public Collection<Group> findParentGroupsRecursively(Collection<Integer> primaryKeys) {
		java.util.Collection<Integer> ids = findParentGroupsPrimaryKeysRecursively(primaryKeys);
		if (ListUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}

		try {
			return getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"Failed to get groups by primary keys: " + ids);
		}

		return Collections.emptyList();
	}

	@Override
	public Collection<Integer> findPermissionGroupPrimaryKeys(
			Collection<Integer> primaryKeys) {
		GroupBMPBean entity = (GroupBMPBean) this.idoCheckOutPooledEntity();
		return entity.ejbFindPermissionGroups(primaryKeys);
	}

	@Override
	public Group findByHomePageID(int pageID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindByHomePageID(pageID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Group findGroupByUniqueId(String uniqueIdString) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindGroupByUniqueId(uniqueIdString);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Group findBoardGroupByClubIDAndLeagueID(Integer clubID, Integer leagueID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindBoardGroupByClubIDAndLeagueID(clubID, leagueID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection<Group> findAllByNamePhrase(String phrase, Locale locale) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids  = ((GroupBMPBean) entity).ejbFindAllByNamePhrase(phrase, locale);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> getMostPopularGroups(Collection<String> types,
			int amount) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids  = ((GroupBMPBean) entity).getMostPopularGroups(types,amount);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> getGroups(Collection<String> types, int amount)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids  = ((GroupBMPBean) entity).getGroups(types,amount);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> getGroupsBySearchRequest(String request,
			Collection<String> types, int amount) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids  = ((GroupBMPBean) entity).getGroupsBySearchRequest(request,types,amount);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Group> getReverseRelatedBy(Integer groupId, String relationType) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Group> groups = ((GroupBMPBean) entity).getReverseRelatedBy(groupId, relationType);
		this.idoCheckInPooledEntity(entity);
		return groups;
	}

	@Override
	public Collection<Integer> getGroupsByTypeAndMetadata(String groupType, String metadataName, String metadataValue) throws FinderException {
		GroupBMPBean entity = (GroupBMPBean) this.idoCheckOutPooledEntity();

		return entity.ejbGetGroupsByTypeAndMetadata(groupType, metadataName, metadataValue);
	}


}