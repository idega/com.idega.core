/*
 * $Id: GroupHomeImpl.java,v 1.25 2004/10/18 17:22:46 eiki Exp $
 * Created on Oct 18, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.core.builder.data.ICDomain;
import com.idega.data.IDOException;
import com.idega.data.IDOFactory;


/**
 * 
 *  Last modified: $Date: 2004/10/18 17:22:46 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.25 $
 */
public class GroupHomeImpl extends IDOFactory implements GroupHome {

	protected Class getEntityInterfaceClass() {
		return Group.class;
	}

	public Group create() throws javax.ejb.CreateException {
		return (Group) super.createIDO();
	}

	public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Group) super.findByPrimaryKeyIDO(pk);
	}

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

	public Collection findGroupsByName(String name) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsByName(name);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findGroupsByAbbreviation(String abbreviation) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsByAbbreviation(abbreviation);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findGroupsByNameAndDescription(String name, String description) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsByNameAndDescription(name, description);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Group findGroupByPrimaryKey(Object primaryKey) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindGroupByPrimaryKey(primaryKey);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsContained(containingGroup, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findGroupsContained(Group containingGroup, Group groupTypeProxy) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsContained(containingGroup, groupTypeProxy);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public int getNumberOfGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes)
			throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfGroupsContained(containingGroup, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public int getNumberOfVisibleGroupsContained(Group containingGroup) throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfVisibleGroupsContained(containingGroup);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindTopNodeGroupsContained(containingDomain, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public int getNumberOfTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes)
			throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfTopNodeGroupsContained(containingDomain, groupTypes,
				returnTypes);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public int getNumberOfTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException, IDOException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		int theReturn = ((GroupBMPBean) entity).ejbHomeGetNumberOfTopNodeVisibleGroupsContained(containingDomain);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindTopNodeVisibleGroupsContained(containingDomain);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindAllGroups(groupTypes, returnSepcifiedGroupTypes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public String getGroupType() {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupBMPBean) entity).ejbHomeGetGroupType();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public String getRelationTypeGroupParent() {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		String theReturn = ((GroupBMPBean) entity).ejbHomeGetRelationTypeGroupParent();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findGroups(String[] groupIDs) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroups(groupIDs);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findGroupsByType(String type) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsByType(type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findGroupsByMetaData(String key, String value) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsByMetaData(key, value);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Group findSystemUsersGroup() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindSystemUsersGroup();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findGroupsRelationshipsByRelatedGroup(int groupID, String relationType, String orRelationType)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindGroupsRelationshipsByRelatedGroup(groupID,
				relationType, orRelationType);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findParentGroups(int groupID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((GroupBMPBean) entity).ejbFindParentGroups(groupID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Group findByHomePageID(int pageID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindByHomePageID(pageID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Group findGroupByUniqueId(String uniqueIdString) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((GroupBMPBean) entity).ejbFindGroupByUniqueId(uniqueIdString);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}
