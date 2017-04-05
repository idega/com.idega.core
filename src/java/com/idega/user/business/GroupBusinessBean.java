/*
 * $Id: GroupBusinessBean.java,v 1.122 2008/10/22 14:51:16 valdas Exp $ Created
 * in 2002 by gummi
 *
 * Copyright (C) 2002-2005 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega. Use is subject to
 * license terms.
 *
 */
package com.idega.user.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.accesscontrol.data.ICPermissionHome;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.component.data.ICObject;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.contact.data.EmailType;
import com.idega.core.contact.data.EmailTypeHome;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneHome;
import com.idega.core.contact.data.PhoneType;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.user.bean.AddressData;
import com.idega.user.bean.GroupDataBean;
import com.idega.user.dao.GroupDAO;
import com.idega.user.data.Group;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupDomainRelationTypeHome;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupNode;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeBMPBean;
import com.idega.user.data.GroupTypeHome;
import com.idega.user.data.ParentGroupsRecursiveProcedure;
import com.idega.user.data.User;
import com.idega.user.data.UserGroupPlugIn;
import com.idega.user.data.UserGroupPlugInHome;
import com.idega.user.data.UserGroupRepresentative;
import com.idega.user.data.UserGroupRepresentativeHome;
import com.idega.user.data.UserHome;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.NestedSetsContainer;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * This is the the class that holds the main business logic for creating,
 * removing, lookups and manipulating Groups.
 * </p>
 * Copyright (C) idega software 2002-2005 <br/> Last modified: $Date: 2006/02/20
 * 11:04:35 $ by $Author: valdas $
 *
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>,<a
 *         href="eiki@idega.is">Eirikur S. Hrafnsson</a>, <a
 *         href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.122 $
 */
/**
 * @author laddi
 *
 */
public class GroupBusinessBean extends com.idega.business.IBOServiceBean implements GroupBusiness {

	private static final long serialVersionUID = -4430320580217555915L;

	private GroupRelationHome groupRelationHome;
	private UserHome userHome;
	private GroupHome groupHome;
	private UserGroupRepresentativeHome userRepHome;
	private GroupHome permGroupHome;
	private AddressHome addressHome;
	private EmailHome emailHome;
	private EmailTypeHome emailTypeHome;
	private PhoneHome phoneHome;
	private ICFileHome fileHome;
	private String[] userRepresentativeType;
	private static final String GROUP_HOME_FOLDER_LOCALIZATION_PREFIX = "ic_group.home_folder.";

	private NestedSetsContainer groupTreeSnapShot = null;

	public GroupBusinessBean() {
	}

	@Override
	public UserHome getUserHome() {
		if (this.userHome == null) {
			try {
				this.userHome = (UserHome) IDOLookup.getHome(User.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.userHome;
	}

	@Override
	public UserGroupRepresentativeHome getUserGroupRepresentativeHome() {
		if (this.userRepHome == null) {
			try {
				this.userRepHome = (UserGroupRepresentativeHome) IDOLookup.getHome(UserGroupRepresentative.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.userRepHome;
	}

	@Override
	public GroupHome getGroupHome() {
		if (this.groupHome == null) {
			try {
				this.groupHome = (GroupHome) IDOLookup.getHome(Group.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.groupHome;
	}

	@Override
	public GroupHome getPermissionGroupHome() {
		if (this.permGroupHome == null) {
			try {
				this.permGroupHome = (GroupHome) IDOLookup.getHome(PermissionGroup.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.permGroupHome;
	}

	/**
	 * Get all groups in the system that are not UserRepresentative groups
	 *
	 * @return Collection With all grops in the system that are not
	 *         UserRepresentative groups
	 */
	@Override
	public Collection<Group> getAllGroups() {
		try {
			return getGroups(getUserRepresentativeGroupTypeStringArray(), false);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns all groups that are not permission or general groups
	 */
	@Override
	public Collection<Group> getAllNonPermissionOrGeneralGroups() {
		try {
			// filter
			String[] groupsNotToReturn = new String[2];
			groupsNotToReturn[0] = this.getGroupHome().getGroupType();
			// groupsNotToReturn[0] =
			// ((Group)com.idega.user.data.GroupBMPBean.getInstance(Group.class)).getGroupTypeValue();
			groupsNotToReturn[1] = this.getPermissionGroupHome().getGroupType();
			// groupsNotToReturn[0] =
			// ((PermissionGroup)com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getInstance(PermissionGroup.class)).getGroupTypeValue();
			// filter end
			return getGroups(groupsNotToReturn, true);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns all groups filtered by the grouptypes array.
	 *
	 * @param groupTypes
	 *          the Groups a String array of group types to be filtered with
	 * @param returnSpecifiedGroupTypes
	 *          if true it returns the Collection with all the groups that are of
	 *          the types specified in groupTypes[], else it returns the opposite
	 *          (all the groups that are not of any of the types specified by
	 *          groupTypes[])
	 * @return Collection of Groups
	 * @throws Exception
	 *           If an error occured
	 */
	@Override
	public Collection<Group> getGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws Exception {
		Collection<Group> result = getGroupHome().findAllGroups(groupTypes, returnSpecifiedGroupTypes);
		if (result != null) { // TODO move from business level to data level by
													// using 'NOT IN (_list_of_standard_group_ids_)'
			result.removeAll(getAccessController().getStandardGroups());
		}
		return result;
	}

	/**
	 * Returns all the groups that are a direct parent of the group with id
	 * uGroupId
	 *
	 * @return Collection of direct parent groups
	 */
	@Override
	public List<Group> getParentGroups(int uGroupId) throws EJBException, FinderException {
		try {
			Group group = this.getGroupByGroupID(uGroupId);
			return getParentGroups(group);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns all the groups that are a direct parent of the group group
	 *
	 * @return Collection of direct parent groups
	 */
	@Override
	public List<Group> getParentGroups(Group group) {
		// public Collection getGroupsContainingDirectlyRelated(Group group){
		try {
			return group.getParentGroups();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Optimized version of getParentGroups(Group group) by Gummi 25.08.2004
	 * Database access is minimized by passing a Map of cached groupParents and
	 * Map of cached groups to the method
	 */
	@Override
	public Collection<Group> getParentGroups(Group group, Map<String, Collection<Integer>> cachedParents, Map<String, Group> cachedGroups) {
		try {
			return group.getParentGroups(cachedParents, cachedGroups);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns all the groups that are not a direct parent of the Group with id
	 * uGroupId. That is both groups that are indirect parents of the group or not
	 * at all parents of the group.
	 *
	 * @see com.idega.user.business.GroupBusiness#getNonParentGroups(int)
	 * @return Collection of non direct parent groups
	 */
	@Override
	public Collection<Group> getNonParentGroups(int uGroupId) {
		// public Collection getAllGroupsNotDirectlyRelated(int uGroupId){
		try {
			Group group = this.getGroupByGroupID(uGroupId);
			Collection<Group> isDirectlyRelated = getParentGroups(group);
			Collection<Group> AllGroups = getAllGroups();// Filters out userrepresentative
																						// groups //
																						// EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

			if (AllGroups != null) {
				if (isDirectlyRelated != null) {
					AllGroups.remove(isDirectlyRelated);
				}
				AllGroups.remove(group);
				return AllGroups;
			}
			else {
				return null;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns all the groups that are not a direct parent of the group with id
	 * uGroupId which are "Registered" i.e. non system groups such as not of the
	 * type user-representative and permission
	 *
	 * @param uGroupId
	 *          the ID of the group
	 * @return Collection
	 */
	@Override
	public Collection getNonParentGroupsNonPermissionNonGeneral(int uGroupId) {
		// public Collection getRegisteredGroupsNotDirectlyRelated(int uGroupId){
		try {
			Group group = this.getGroupByGroupID(uGroupId);
			Collection isDirectlyRelated = getParentGroups(group);
			Collection AllGroups = getAllNonPermissionOrGeneralGroups();// Filters out
																																	// userrepresentative/permission
																																	// groups //
																																	// EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

			if (AllGroups != null) {
				if (isDirectlyRelated != null) {
					AllGroups.remove(isDirectlyRelated);
				}
				AllGroups.remove(group);
				return AllGroups;
			}
			else {
				return null;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets all the groups that are indirect parents of the group by id uGroupId
	 * recursively up the group tree.
	 *
	 * @param uGroupId
	 * @return Collection of indirect parent (grandparents etc.) Groups
	 */
	@Override
	public Collection getParentGroupsInDirect(int uGroupId) {
		// public Collection getGroupsContainingNotDirectlyRelated(int uGroupId){
		try {
			Group group = this.getGroupByGroupID(uGroupId);
			Collection isDirectlyRelated = getParentGroups(group);
			Collection AllGroups = getParentGroupsRecursive(uGroupId); // EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getInstance());

			if (AllGroups != null) {
				if (isDirectlyRelated != null) {
					Iterator iter = isDirectlyRelated.iterator();
					while (iter.hasNext()) {
						Object item = iter.next();
						AllGroups.remove(item);
						// while(AllGroups.remove(item)){}
					}
				}
				AllGroups.remove(group);
				return AllGroups;
			}
			else {
				return null;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns recursively up the group tree parents of group aGroup
	 *
	 * @param uGroupId
	 *          an id of the Group to be found parents recursively for.
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *           If an error occured
	 */
	@Override
	public Collection getParentGroupsRecursive(int uGroupId) throws EJBException {
		// public Collection getGroupsContaining(int uGroupId)throws EJBException{
		try {
			Group group = this.getGroupByGroupID(uGroupId);
			return getParentGroupsRecursive(group);
		}
		catch (Exception ex) {
			throw new IBORuntimeException(ex);
		}
	}

	/**
	 * Returns recursively up the group tree parents of group aGroup
	 *
	 * @param aGroup
	 *          The Group to be found parents recursively for.
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *           If an error occured
	 */
	@Override
	public <G extends GroupNode<G>> Collection<G> getParentGroupsRecursive(G aGroup) throws EJBException {
		return getParentGroupsRecursive(aGroup, null, null);
	}

	/**
	 * Optimized version of getParentGroupsRecursive(Group) by Sigtryggur
	 * 22.06.2004 Database access is minimized by passing a Map of cached
	 * groupParents and Map of cached groups to the method
	 */
	@Override
	public <G extends GroupNode<G>> Collection<G> getParentGroupsRecursive(G aGroup, Map<String, Collection<Integer>> cachedParents, Map<String, G> cachedGroups) throws EJBException {
		return getParentGroupsRecursive(aGroup, getUserRepresentativeGroupTypeStringArray(), false, cachedParents, cachedGroups);
	}
	@Override
	public Collection<Group> getParentGroupsRecursive(User user, Map<String, Collection<Integer>> cachedParents, Map<String, Group> cachedGroups) throws EJBException {
		return getParentGroupsRecursive(user, getUserRepresentativeGroupTypeStringArray(), false, cachedParents, cachedGroups);
	}

	@Override
	public String[] getUserRepresentativeGroupTypeStringArray() {
		if (this.userRepresentativeType == null) {
			this.userRepresentativeType = new String[1];
			this.userRepresentativeType[0] = this.getUserGroupRepresentativeHome().getGroupType();
		}
		return this.userRepresentativeType;
	}

	/**
	 * Returns recursively up the group tree parents of group aGroup with filtered
	 * out with specified groupTypes
	 *
	 * @param aGroup
	 *          a Group to find parents for
	 * @param groupTypes
	 *          the Groups a String array of group types to be filtered with
	 * @param returnSpecifiedGroupTypes
	 *          if true it returns the Collection with all the groups that are of
	 *          the types specified in groupTypes[], else it returns the opposite
	 *          (all the groups that are not of any of the types specified by
	 *          groupTypes[])
	 * @return Collection of Groups found recursively up the tree
	 * @throws EJBException
	 *           If an error occured
	 */
	@Override
	public <G extends GroupNode<G>> Collection<G> getParentGroupsRecursive(G aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		return getParentGroupsRecursive(aGroup, groupTypes, returnSpecifiedGroupTypes, null, null);
	}
	@Override
	public Collection<Group> getParentGroupsRecursive(User user, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		return getParentGroupsRecursive((Group) user, groupTypes, returnSpecifiedGroupTypes, null, null);
	}
	@Override
	public Collection<Group> getParentGroupsRecursive(
			Group aGroup,
			String[] groupTypes,
			boolean returnSpecifiedGroupTypes
	) throws EJBException, java.rmi.RemoteException {
		return getParentGroupsRecursive(aGroup, groupTypes, returnSpecifiedGroupTypes, null, null);
	}

	private <G extends GroupNode<G>> Collection<G> getParentGroupsRecursive(
			G aGroup,
			String[] groupTypes,
			boolean returnSpecifiedGroupTypes,
			Map<String, Collection<Integer>> cachedParents,
			Map<String, G> cachedGroups
	) throws EJBException {
		return getParentGroupsRecursiveNotUsingStoredProcedure(aGroup, groupTypes, returnSpecifiedGroupTypes, cachedParents, cachedGroups);
	}

	/**
	 * Optimized version of getParentGroupsRecursive(Group,String[],boolean) by
	 * Sigtryggur 22.06.2004 Database access is minimized by passing a Map of
	 * cached groupParents and Map of cached groups to the method
	 */
	private <G extends GroupNode<G>> Collection<G> getParentGroupsRecursiveNotUsingStoredProcedure(
			G aGroup,
			String[] groupTypes,
			boolean returnSpecifiedGroupTypes,
			Map<String, Collection<Integer>> cachedParents,
			Map<String, G> cachedGroups
	) throws EJBException {
		Collection<G> groups = null;
		try {
			groups = aGroup.getParentGroups(cachedParents, cachedGroups);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (ListUtil.isEmpty(groups)) {
			return null;
		}

		List<G> specifiedGroups = new ArrayList<G>();
		List<G> notSpecifiedGroups = new ArrayList<G>();
		try {
			Map<String, G> groupsContained = new LinkedHashMap<String, G>();

			String key = CoreConstants.EMPTY;
			Iterator<G> iter = groups.iterator();
			while (iter.hasNext()) {
				G item = iter.next();
				if (item != null) {
					key = item.getId();
					if (!groupsContained.containsKey(key)) {
						groupsContained.put(key, item);
						putGroupsContaining(item, groupsContained, groupTypes, returnSpecifiedGroupTypes, cachedParents, cachedGroups);
					}
				}
			}

			int j = 0;
			int k = 0;
			Iterator<G> iter2 = groupsContained.values().iterator();
			if (groupTypes != null && groupTypes.length > 0) {
				boolean specified = false;
				while (iter2.hasNext()) {
					G tempObj = iter2.next();
					for (int i = 0; i < groupTypes.length; i++) {
						String groupType = tempObj.getType();
						if (groupType != null && groupType.equals(groupTypes[i])) {
							specifiedGroups.add(j++, tempObj);
							specified = true;
						}
					}
					if (!specified) {
						notSpecifiedGroups.add(k++, tempObj);
					}
					else {
						specified = false;
					}
				}
				notSpecifiedGroups.remove(aGroup);
				specifiedGroups.remove(aGroup);
			}
			else {
				while (iter2.hasNext()) {
					G tempObj = iter2.next();
					notSpecifiedGroups.add(j++, tempObj);
				}
				notSpecifiedGroups.remove(aGroup);
				returnSpecifiedGroupTypes = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnSpecifiedGroupTypes ? specifiedGroups : notSpecifiedGroups;
	}

	/**
	 * Optimized version of putGroupsContaining(Group, Map, String[], boolean) by
	 * Sigtryggur 22.06.2004 Database access is minimized by passing a Map of
	 * cached groupParents and Map of cached groups to the method
	 */
	private <G extends GroupNode<G>> void putGroupsContaining(
			G group,
			Map<String, G> groupsContained,
			String[] groupTypes,
			boolean returnGroupTypes,
			Map<String, Collection<Integer>> cachedParents,
			Map<String, G> cachedGroups
	) throws Exception {
		List<G> pGroups = null;
		if (cachedParents == null) {
			pGroups = group.getParentGroups();// TODO EIKI FINISH THIS
																				// groupTypes,returnGroupTypes);
		} else {
			pGroups = group.getParentGroups(cachedParents, cachedGroups);
		}
		if (pGroups != null) {
			String key = CoreConstants.EMPTY;
			Iterator<G> iter = pGroups.iterator();
			while (iter.hasNext()) {
				G item = iter.next();
				if (item != null) {
					key = item.getId();

					if (!groupsContained.containsKey(key)) {
						groupsContained.put(key, item);
						putGroupsContaining(item, groupsContained, groupTypes, returnGroupTypes, cachedParents, cachedGroups);
					}
				}
			}
		}
	}

	@Override
	public Collection getUsers(int groupId) throws EJBException, FinderException {
		try {
			Group group = this.getGroupByGroupID(groupId);
			return getUsers(group);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	@Override
	public Collection getUsersDirectlyRelated(int groupId) throws EJBException, FinderException {
		try {
			Group group = this.getGroupByGroupID(groupId);
			return getUsersDirectlyRelated(group);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	@Override
	public Collection getUsersNotDirectlyRelated(int groupId) throws EJBException, FinderException {
		try {
			Group group = this.getGroupByGroupID(groupId);
			return getUsersNotDirectlyRelated(group);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	/**
	 * Returns recursively down the group tree children of group with id groupId
	 *
	 * @param groupId
	 *          an id of a Group to find parents for
	 * @return Collection of Groups found recursively down the tree
	 * @throws EJBException
	 *           If an error occured
	 */
	@Override
	public Collection getChildGroupsRecursive(int groupId) throws EJBException, FinderException {
		// public Collection getGroupsContained(int groupId) throws
		// EJBException,FinderException,RemoteException{
		try {
			Group group = this.getGroupByGroupID(groupId);
			return getChildGroupsRecursive(group);
		}
		catch (IOException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	/**
	 * Returns recursively down the group tree children of group aGroup with
	 * filtered out with specified groupTypes
	 *
	 * @param aGroup
	 *          a Group to find children for
	 * @return Collection of Groups found recursively down the tree
	 * @throws EJBException
	 *           If an error occured
	 */
	@Override
	public Collection getChildGroupsRecursive(Group aGroup) throws EJBException {
		return getChildGroupsRecursive(aGroup, getUserRepresentativeGroupTypeStringArray(), false);
	}

	/**
	 * Returns recursively down the group tree children of group aGroup with
	 * filtered out with specified groupTypes. WHEN IT FINDS A GROUP THAT IS NOT
	 * OF A DESIRED GROUP TYPE IT STOPS FOR THAT GROUP. If you want to also
	 * recurse under those groups use the method Collection
	 * getChildGroupsRecursiveResultFiltered(int groupId, Collection
	 * groupTypesAsString, boolean complementSetWanted).
	 *
	 * @param aGroup
	 *          a Group to find children for
	 * @param groupTypes
	 *          the Groups a String array of group types to be filtered with
	 * @param returnSpecifiedGroupTypes
	 *          if true it returns the Collection with all the groups that are of
	 *          the types specified in groupTypes[], else it returns the opposite
	 *          (all the groups that are not of any of the types specified by
	 *          groupTypes[])
	 * @return Collection of Groups found recursively down the tree
	 * @throws EJBException
	 *           If an error occured
	 */
	@Override
	public Collection getChildGroupsRecursive(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		// public Collection getGroupsContained(Group groupContaining, String[]
		// groupTypes, boolean returnSepcifiedGroupTypes) throws RemoteException{
		try {

			Map GroupsContained = new HashMap();// to avoid duplicates

			Collection groups = aGroup.getChildGroups(groupTypes, returnSpecifiedGroupTypes);

			// int j = 0;

			if (groups != null && !groups.isEmpty()) {

				String key = CoreConstants.EMPTY;
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
					Group item = (Group) iter.next();
					if (item != null) {
						key = item.getPrimaryKey().toString();
						if (!GroupsContained.containsKey(key)) {
							GroupsContained.put(key, item);
							putGroupsContained(item, GroupsContained, groupTypes, returnSpecifiedGroupTypes);
						}
					}
				}

				return new ArrayList(GroupsContained.values());
			}
			else {
				return null;
			}
		}
		catch (IOException e) {
			throw new IBORuntimeException(e, this);
		}

	}

	/**
	 * Return all the user directly under(related to) this group.
	 *
	 * @see com.idega.user.business.GroupBusiness#getUsersContained(Group)
	 */
	@Override
	public Collection getUsers(Group group) throws FinderException {
		// filter
		User groupTypeProxy = IDOLookup.instanciateEntity(User.class);

		return group.getChildGroups(groupTypeProxy);
	}

	/**
	 * Return all the user under(related to) this group and any contained group
	 * recursively!
	 *
	 * @see com.idega.user.business.GroupBusiness#getUsersContainedRecursive(Group)
	 */
	@Override
	public Collection getUsersRecursive(Group group) throws FinderException {
		try {
			Collection list = getChildGroupsRecursive(group, getUserRepresentativeGroupTypeStringArray(), true);
			if (list != null && !list.isEmpty()) {
				return getUsersForUserRepresentativeGroups(list);
			}
			else {
				return ListUtil.getEmptyList();
			}
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	/**
	 * Return all the user under(related to) this group and any contained group
	 * recursively!
	 *
	 * @see com.idega.user.business.GroupBusiness#getUsersContainedRecursive(Group)
	 */
	@Override
	public Collection getUsersRecursive(int groupId) throws FinderException {
		try {
			Group group = this.getGroupByGroupID(groupId);
			return getUsersRecursive(group);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	/**
	 * Returns all the groups that are direct children groups of group with id
	 * groupId.
	 *
	 * @param groupId
	 *          an id of a Group to find children groups for
	 * @return Collection of Groups that are Direct children of group aGroup
	 */
	@Override
	public Collection getChildGroups(int groupId) throws EJBException, FinderException {
		// public Collection getGroupsContainedDirectlyRelated(int groupId) throws
		// EJBException,FinderException{
		try {
			Group group = this.getGroupByGroupID(groupId);
			return getChildGroups(group);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	@Override
	public Collection getChildGroupsResultFiltered(Group parentGroup, String groupName, Collection groupTypes, boolean onlyReturnTypesInCollection) throws RemoteException {
		Group group = null;
		Collection ancestorsOfGroup = null;
		Collection allMatchingGroups = getGroupsByGroupNameAndGroupTypes(groupName, groupTypes, onlyReturnTypesInCollection);
		Collection filteredChildGroups = new ArrayList();
		Iterator it = allMatchingGroups.iterator();
		while (it.hasNext()) {
			try {
				group = (Group) it.next();
				ancestorsOfGroup = getParentGroupsRecursive(group);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (ancestorsOfGroup != null && ancestorsOfGroup.contains(parentGroup)) {
				filteredChildGroups.add(group);
			}
		}
		return filteredChildGroups;
	}

	/**
	 * @see getChildGroupsRecursiveResultFiltered(Group group, Collection
	 *      groupTypesAsString, boolean onlyReturnTypesInCollection)
	 * @param groupId
	 * @param groupTypesAsString -
	 *          a collection of strings representing group types, empty or null
	 *          for any type
	 * @param onlyReturnTypesInCollection -
	 *          should be set to true if you want to fetch all the groups that
	 *          have group types that are contained in the collection
	 *          groupTypesAsString else false to exclude those group types
	 * @return a collection of groups
	 */
	@Override
	public Collection getChildGroupsRecursiveResultFiltered(int groupId, Collection groupTypesAsString, boolean onlyReturnTypesInCollection) {
		return getChildGroupsRecursiveResultFiltered(groupId, groupTypesAsString, onlyReturnTypesInCollection, false);
	}

	public Collection getChildGroupsRecursiveResultFiltered(int groupId, Collection groupTypesAsString, boolean onlyReturnTypesInCollection, boolean includeAliases) {
		Group group = null;
		try {
			group = this.getGroupByGroupID(groupId);
		}
		catch (FinderException findEx) {
			System.err.println("[GroupBusiness]: Can't retrieve group. Message is: " + findEx.getMessage());
			findEx.printStackTrace(System.err);
			return new ArrayList();
		}
		catch (RemoteException ex) {
			System.err.println("[GroupBusiness]: Can't retrieve group. Message is: " + ex.getMessage());
			ex.printStackTrace(System.err);
			throw new RuntimeException("[GroupBusiness]: Can't retrieve group.");
		}
		return getChildGroupsRecursiveResultFiltered(group, groupTypesAsString, onlyReturnTypesInCollection, includeAliases);
	}

	@Override
	public Collection<Group> getChildGroupsRecursiveResultFiltered(Group group, Collection<String> groupTypesAsString, boolean onlyReturnTypesInCollection) {
		return getChildGroupsRecursiveResultFiltered(group, groupTypesAsString, onlyReturnTypesInCollection, false);
	}

	@Override
	public Collection<Group> getChildGroupsRecursiveResultFiltered(Group group, Collection<String> groupTypesAsString, boolean onlyReturnTypesInCollection, boolean includeAliases) {
		return getChildGroupsRecursiveResultFiltered(group, groupTypesAsString, onlyReturnTypesInCollection, false, false);
	}

	/**
	 * Returns all the groups that are direct and indirect children of the
	 * specified group. If the grouptype collection is not null and none empty the
	 * returned groups are filtered to only include or exclude those grouptypes in
	 * the returning collection depending on whether the boolean is set to true or
	 * false. The method does not stop recursing a group even if that group is not
	 * specified in the desired grouptype collection. Its children are always
	 * checked also that is the most important difference to the method
	 * getChildGroupsRecursive.
	 *
	 * @param group
	 * @param groupTypesAsString -
	 *          a collection of strings representing group types, empty or null
	 *          for any type
	 * @param onlyReturnTypesInCollection -
	 *          should be set to true if you want to fetch all the groups that
	 *          have group types that are contained in the collection
	 *          groupTypesAsString else false to exclude those group types
	 * @return a collection of groups
	 */
	@Override
	public Collection<Group> getChildGroupsRecursiveResultFiltered(Group group, Collection<String> groupTypesAsString, boolean onlyReturnTypesInCollection, boolean includeAliases, boolean excludeGroupsWithoutMembers) {
		long start = System.currentTimeMillis();
		try {
			// author: Thomas
			Map<Integer, Boolean> alreadyCheckedGroups = new HashMap<Integer, Boolean>();
			Collection<Group> result = new ArrayList<Group>();
			String[] userType = new String[] { getUserHome().getGroupType() };
			getChildGroupsRecursive(group, alreadyCheckedGroups, result, groupTypesAsString, onlyReturnTypesInCollection, includeAliases, excludeGroupsWithoutMembers, userType);
			return result;
		} finally {
			CoreUtil.doDebug(start, System.currentTimeMillis(), "GroupBusinessBean.getChildGroupsRecursiveResultFiltered");
		}
	}

	@Override
	public Collection<User> getUsersFromGroupRecursive(Group group) {
		return getUsersFromGroupRecursive(group, null, false);
	}

	@Override
	public Collection<User> getUsersFromGroupRecursive(Group group, Collection<String> groupTypesAsString, boolean onlyReturnTypesInCollection) {
		// author: Thomas
		Collection<User> users = new ArrayList<User>();
		Collection<Group> groups = getChildGroupsRecursiveResultFiltered(group, groupTypesAsString, onlyReturnTypesInCollection);
		Iterator<Group> iterator = groups.iterator();
		while (iterator.hasNext()) {
			Group tempGroup = iterator.next();
			try {
				users.addAll(getUsers(tempGroup));
			}
			catch (Exception ex) {
			}
		}
		return users;
	}

	private void getChildGroupsRecursive(
			Group currentGroup,
			Map<Integer, Boolean> alreadyCheckedGroups,
			Collection<Group> result,
			Collection<String> groupTypesAsString,
			boolean onlyReturnTypesInCollection,
			boolean includeAliases,
			boolean excludeGroupsWithoutMembers,
			String[] userType
	) {
		Integer currentPrimaryKey = (Integer) currentGroup.getPrimaryKey();
		if (alreadyCheckedGroups.containsKey(currentPrimaryKey)) {
			// already checked, avoid looping
			return;
		}

		alreadyCheckedGroups.put(currentPrimaryKey, Boolean.TRUE);
		String currentGroupType = currentGroup.getGroupType();

		// does the current group belong to the result set?
		// if both are true or false then it belongs, otherwise not. (using XOR)
		if (groupTypesAsString == null || groupTypesAsString.isEmpty()) {
			// no specific type, add all
			if (excludeGroupsWithoutMembers) {
				Collection<Group> users = currentGroup.getChildGroups(userType, true);
				if (!users.isEmpty()) {
					result.add(currentGroup);
				}
			} else {
				result.add(currentGroup);
			}
		} else if (!(groupTypesAsString.contains(currentGroupType) ^ (onlyReturnTypesInCollection))) {
			if (excludeGroupsWithoutMembers) {
				Collection<Group> users = currentGroup.getChildGroups(userType, true);
				if (!users.isEmpty()) {
					result.add(currentGroup);
				}
			} else {
				result.add(currentGroup);
			}
		}

		// go further
		Collection<Group> children = currentGroup.getChildGroups();
		Iterator<Group> childrenIterator = children.iterator();
		while (childrenIterator.hasNext()) {
			Group child = childrenIterator.next();
			if (includeAliases) {
				if (child.isAlias()) {
					if (child.getAlias() != null) {
						alreadyCheckedGroups.put((Integer) child.getPrimaryKey(), Boolean.TRUE);
						child = child.getAlias();
					}
				}
			}
			getChildGroupsRecursive(child, alreadyCheckedGroups, result, groupTypesAsString, onlyReturnTypesInCollection, includeAliases, excludeGroupsWithoutMembers, userType);
		}
	}

	/**
	 * Returns all the groups that are direct children groups of group aGroup.
	 *
	 * @param aGroup
	 *          a group to find children groups for
	 * @return Collection of Groups that are Direct children of group aGroup
	 */
	@Override
	public Collection<Group> getChildGroups(Group aGroup) {
		return getChildGroups(aGroup, getUserRepresentativeGroupTypeStringArray(), false);
	}

	@Override
	public Collection<Group> getChildGroups(Group aGroup, Collection<String> groupTypes, boolean returnSpecifiedGroupTypes) {
		return getChildGroups(aGroup, groupTypes.toArray(new String[groupTypes.size()]), returnSpecifiedGroupTypes);
	}

	@Override
	public Collection<Group> getChildGroups(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) {
		if (aGroup != null) {
			try {
				Collection<Group> list = aGroup.getChildGroups(groupTypes, returnSpecifiedGroupTypes);
				if (list != null) {
					list.remove(aGroup);
				}

				return list;
			} catch (Exception ex) {
				getLogger().log(Level.WARNING,
						"Failed to get list of child groups, cause of: ", ex);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public Collection getUsersDirectlyRelated(Group group) throws EJBException, RemoteException, FinderException {
		// TODO GET USERS DIRECTLY
		Collection<Group> result = group.getChildGroups(this.getUserRepresentativeGroupTypeStringArray(), true);
		return getUsersForUserRepresentativeGroups(result);
	}

	/**
	 * @param groupId
	 *          a group to find Groups under
	 * @return Collection A Collection of Groups that are indirect children
	 *         (grandchildren etc.) of the specified group recursively down the
	 *         group tree
	 * @throws FinderException
	 *           if there was an error finding the group by id groupId
	 * @throws EJBException
	 *           if other errors occur.
	 */
	@Override
	public Collection getChildGroupsInDirect(int groupId) throws EJBException, FinderException {
		// public Collection getGroupsContainedNotDirectlyRelated(int groupId)
		// throws EJBException,FinderException{
		try {
			Group group = this.getGroupByGroupID(groupId);
			return getChildGroupsInDirect(group);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e, this);
		}
	}

	/**
	 * @param group
	 *          a group to find Groups under
	 * @return Collection A Collection of Groups that are indirect children
	 *         (grandchildren etc.) of the specified group recursively down the
	 *         group tree
	 * @throws EJBException
	 *           if an error occurs.
	 */
	@Override
	public Collection getChildGroupsInDirect(Group group) throws EJBException {
		// public Collection getGroupsContainedNotDirectlyRelated(Group group)
		// throws EJBException{
		try {
			Collection isDirectlyRelated = getChildGroups(group);
			Collection AllGroups = getChildGroupsRecursive(group);

			if (AllGroups != null) {
				if (isDirectlyRelated != null) {
					AllGroups.removeAll(isDirectlyRelated);
				}
				AllGroups.remove(group);
				return AllGroups;
			}
			else {
				return null;
			}

		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection getUsersNotDirectlyRelated(Group group) throws EJBException, RemoteException, FinderException {

		Collection DirectUsers = getUsersDirectlyRelated(group);
		Collection notDirectUsers = getUsers(group);

		if (notDirectUsers != null) {
			if (DirectUsers != null) {
				Iterator iter = DirectUsers.iterator();
				while (iter.hasNext()) {
					Object item = iter.next();
					notDirectUsers.remove(item);
				}
			}
			return notDirectUsers;
		}
		else {
			return null;
		}
		/*
		 * if(notDirectUsers != null){ notDirectUsers.removeAll(DirectUsers); }
		 * return notDirectUsers;
		 */
	}

	private void putGroupsContained(Group group, Map GroupsContained, String[] groupTypes, boolean returnGroupTypes) throws RemoteException {
		Collection childGroups = group.getChildGroups(groupTypes, returnGroupTypes);
		if (childGroups != null && !childGroups.isEmpty()) {
			String key = CoreConstants.EMPTY;
			Iterator iter = childGroups.iterator();
			while (iter.hasNext()) {
				Group item = (Group) iter.next();
				key = item.getPrimaryKey().toString();
				if (!GroupsContained.containsKey(key)) {
					GroupsContained.put(key, item);
					putGroupsContained(item, GroupsContained, groupTypes, returnGroupTypes);
				}
			}
		}

	}

	/**
	 * @param groupIDs
	 *          a string array of IDs to be found.
	 * @return A Collection of groups with the specified ids.
	 * @see com.idega.user.business.GroupBusiness#getGroups(String[])
	 */
	@Override
	public Collection getGroups(String[] groupIDs) throws FinderException, RemoteException {
		return this.getGroupHome().findGroups(groupIDs);
	}

	@Override
	public Collection<Group> getGroups(Collection<String> groupIDs) throws FinderException, RemoteException {
		return this.getGroupHome().findGroups(groupIDs);
	}

	@Override
	public Collection getUsersForUserRepresentativeGroups(Collection groups) throws FinderException, RemoteException {
		try {
			return this.getUserHome().findUsersForUserRepresentativeGroups(groups);
		}
		catch (FinderException ex) {
			System.err.println(ex.getMessage());
			return new Vector(0);
		}
	}

	@Override
	public void updateUsersInGroup(int groupId, String[] usrGroupIdsInGroup, User currentUser) throws RemoteException, FinderException {

		if (groupId != -1) {
			Group group = this.getGroupByGroupID(groupId);
			// System.out.println("before");
			Collection lDirect = getUsersDirectlyRelated(groupId);
			Set direct = new HashSet();
			if (lDirect != null) {
				Iterator iter = lDirect.iterator();
				while (iter.hasNext()) {
					User item = (User) iter.next();
					direct.add(Integer.toString(item.getGroupID()));
					// System.out.println("id: "+ item.getGroupID());
				}
			}

			// System.out.println("after");
			Set toRemove = (Set) ((HashSet) direct).clone();
			Set toAdd = new HashSet();

			if (usrGroupIdsInGroup != null) {
				for (int i = 0; i < usrGroupIdsInGroup.length; i++) {

					if (direct.contains(usrGroupIdsInGroup[i])) {
						toRemove.remove(usrGroupIdsInGroup[i]);
					}
					else {
						toAdd.add(usrGroupIdsInGroup[i]);
					}

					// System.out.println("id: "+ usrGroupIdsInGroup[i]);
				}
			}

			// System.out.println("toRemove");
			Iterator iter2 = toRemove.iterator();
			while (iter2.hasNext()) {
				String item = (String) iter2.next();
				// System.out.println("id: "+ item);
				group.removeGroup(Integer.parseInt(item), currentUser, false);
			}

			// System.out.println("toAdd");
			Iterator iter3 = toAdd.iterator();
			while (iter3.hasNext()) {
				String item = (String) iter3.next();
				// System.out.println("id: "+ item);
				group.addGroup(Integer.parseInt(item));
			}

		}
		else {
			// System.out.println("groupId = "+ groupId + ", usrGroupIdsInGroup = "+
			// usrGroupIdsInGroup);
		}

	}

	@Override
	public Group getGroupByGroupID(int id) throws FinderException, RemoteException {
		return this.getGroupHome().findByPrimaryKey(new Integer(id));
	}

	@Override
	public Collection getGroupsByGroupName(String name) throws RemoteException {
		try {
			return this.getGroupHome().findGroupsByName(name);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}

	@Override
	public Collection<Group> getGroupsByGroupNameAndGroupTypes(String name, Collection<?> groupTypes, boolean onlyReturnTypesInCollection) throws RemoteException {
		try {
			return this.getGroupHome().findGroupsByNameAndGroupTypes(name, groupTypes, onlyReturnTypesInCollection);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}

	/**
	 * Gets a collection of groups of the supplied group type and which names
	 * start with the supplied name string ('name%')
	 *
	 * @param type
	 *          the group
	 * @param name
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public Collection getGroupsByGroupTypeAndFirstPartOfName(String groupType, String groupNameStartsWith) throws RemoteException {
		try {
			if (!groupNameStartsWith.endsWith("%")) {
				groupNameStartsWith = groupNameStartsWith + "%";
			}
			return this.getGroupHome().findGroupsByGroupTypeAndLikeName(groupType, groupNameStartsWith);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}

	@Override
	public Collection<Group> getGroupsByAbbreviation(String abbreviation) throws RemoteException {
		try {
			return this.getGroupHome().findGroupsByAbbreviation(abbreviation);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}
	}

	@Override
	public User getUserByID(int id) throws FinderException, RemoteException {
		return this.getUserHome().findByPrimaryKey(new Integer(id));
	}

	@Override
	public void addUser(int groupId, User user) throws EJBException, RemoteException {
		addUser(groupId, user, IWTimestamp.getTimestampRightNow());
	}
	@Override
	public void addUser(int groupId, User user, Timestamp time) throws EJBException, RemoteException {
		try {
			this.getGroupByGroupID(groupId).addGroup(user, time == null ? IWTimestamp.getTimestampRightNow() : time);
		}
		catch (FinderException fe) {
			throw new EJBException(fe.getMessage());
		}
	}

	/**
	 * Not yet implemented
	 */
	@Override
	public GroupHome getGroupHome(String groupType) {
		if (this.groupHome == null) {
			try {
				/**
				 * @todo: implement
				 */
				this.groupHome = (GroupHome) IDOLookup.getHome(Group.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.groupHome;
	}

	@Override
	public GroupRelationHome getGroupRelationHome() {
		if (this.groupRelationHome == null) {
			try {
				this.groupRelationHome = (GroupRelationHome) IDOLookup.getHome(GroupRelation.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.groupRelationHome;
	}

	@Override
	public Group getGroupByUniqueId(String uniqueID) throws FinderException {
		Group group;
		group = getGroupHome().findGroupByUniqueId(uniqueID);
		return group;
	}

	/**
	 * Creates a general group and adds it under the root (directly under in the
	 * group tree) of the default Domain (ICDomain)
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String)
	 */
	@Override
	public Group createGroup(String name) throws CreateException, RemoteException {
		String description = CoreConstants.EMPTY;
		return createGroup(name, description);
	}

	/**
	 * Creates a general group and adds it under the root (directly under in the
	 * group tree) of the default Domain (ICDomain)
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String)
	 */
	@Override
	public Group createGroup(String name, String description) throws CreateException, RemoteException {
		String generaltype = getGroupHome().getGroupType();
		return createGroup(name, description, generaltype);
	}

	/**
	 * Creates a group and adds it under the root (directly under in the group
	 * tree) of the default Domain (ICDomain)
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String)
	 */
	@Override
	public Group createGroup(String name, String description, String type) throws CreateException, RemoteException {
		return createGroup(name, description, type, -1);
	}

	/**
	 * Creates a group and adds it under the default Domain (ICDomain)<br>
	 * If createUnderDomainRoot is true it is added under the root (directly under
	 * in the group tree) of the domain.
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String)
	 */
	@Override
	public Group createGroup(String name, String description, String type, boolean createUnderDomainRoot) throws CreateException, RemoteException {
		return createGroup(name, description, type, -1, -1, createUnderDomainRoot, null);
	}

	/**
	 * Creates a group and adds it under the default Domain (IBDomain) and under
	 * the group parentGroup.
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String)
	 */
	@Override
	public Group createGroupUnder(String name, String description, String type, Group parentGroup) throws CreateException, RemoteException {
		return createGroup(name, description, type, -1, -1, false, parentGroup);
	}

	/**
	 * Creates a general group and adds it under the default Domain (IBDomain) and
	 * under the group parentGroup.
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String)
	 */
	@Override
	public Group createGroupUnder(String name, String description, Group parentGroup) throws CreateException, RemoteException {
		String generaltype = getGroupHome().getGroupType();
		return createGroup(name, description, generaltype, -1, -1, false, parentGroup);
	}

	/**
	 * Creates a group and adds it under the root (directly under in the group
	 * tree) of the default Domain (ICDomain)
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String, int)
	 */
	@Override
	public Group createGroup(String name, String description, String type, int homePageID) throws CreateException, RemoteException {
		return createGroup(name, description, type, homePageID, -1);
	}

	/**
	 * Creates a group and adds it under the root (directly under in the group
	 * tree) of the default Domain (ICDomain)
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String, int)
	 */
	@Override
	public Group createGroup(String name, String description, String type, int homePageID, int aliasID) throws CreateException, RemoteException {
		return createGroup(name, description, type, homePageID, aliasID, true, null);
	}

	/**
	 * Creates a group and adds it under the the default Domain (ICDomain) and
	 * under the group parentGroup.
	 *
	 * @see com.idega.user.business.GroupBusiness#createGroup(String, String,
	 *      String, int)
	 */
	@Override
	public Group createGroupUnder(String name, String description, String type, int homePageID, int aliasID, Group parentGroup) throws CreateException, RemoteException {
		return createGroup(name, description, type, homePageID, aliasID, false, parentGroup);
	}

	@Override
	public Group createGroup(String name, String description, String type, int homePageID, int aliasID, boolean createUnderDomainRoot, Group parentGroup) throws CreateException, RemoteException {
		return createGroup(name, description, type, homePageID, -1, aliasID, createUnderDomainRoot, parentGroup);
	}

	@Override
	public Group createGroup(String name, String description, String type, int homePageID, int homeFolderID, int aliasID, boolean createUnderDomainRoot, Group parentGroup) throws CreateException, RemoteException {
		Group newGroup;
		newGroup = getGroupHome().create();
		newGroup.setName(name);
		newGroup.setDescription(description);

		newGroup.setGroupType(type);
		if (homePageID != -1) {
			newGroup.setHomePageID(homePageID);
		}
		if (aliasID != -1) {
			newGroup.setAliasID(aliasID);
		}

		if (homeFolderID != -1) {
			newGroup.setHomeFolderID(homeFolderID);
		}

		newGroup.store();

		try {
			if (homeFolderID == -1) {
				createGroupHomeFolder(newGroup);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		if (createUnderDomainRoot) {
			addGroupUnderDomainRoot(this.getIWApplicationContext().getDomain(), newGroup);
		}
		else {
			addGroupUnderDomain(this.getIWApplicationContext().getDomain(), newGroup, null);
		}
		if (parentGroup != null) {
			parentGroup.addGroup(newGroup);
		}

		return newGroup;
	}

	@Override
	public ICFileHome getICFileHome() {
		if (this.fileHome == null) {
			try {
				this.fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.fileHome;
	}

	@Override
	public ICFile createGroupHomeFolder(Group group) throws CreateException {
		ICFile file = getICFileHome().create();
		file.setName(group.getName());
		file.setLocalizationKey(GROUP_HOME_FOLDER_LOCALIZATION_PREFIX + group.getGroupType());
		file.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
		file.setDescription("This is a home folder for a group");
		file.store();

		group.setHomeFolder(file);
		group.store();

		return file;
	}

	@Override
	public Collection getAllAllowedGroupTypesForChildren(int groupId, IWUserContext iwuc) {
		// try to get the group
		Group group;
		try {
			group = (groupId > -1) ? getGroupByGroupID(groupId) : null;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return getAllAllowedGroupTypesForChildren(group, iwuc);
	}

	/**
	 * It is allowed and makes sense if the parameter group is null: In this case
	 * alias and general group type is returned.
	 */
	@Override
	public Collection getAllAllowedGroupTypesForChildren(Group group, IWUserContext iwuc) {
		GroupTypeHome groupTypeHome;
		GroupType groupType;
		String groupTypeString;
		try {
			groupTypeHome = (GroupTypeHome) IDOLookup.getHome(GroupType.class);
			// super admin: return all group types
			if (iwuc.isSuperAdmin()) {
				try {
					if (groupTypeHome.getNumberOfVisibleGroupTypes() <= 0) {
						((com.idega.data.GenericEntity) com.idega.data.IDOLookup.instanciateEntity(GroupType.class)).insertStartData();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return groupTypeHome.findVisibleGroupTypes();
			}
			// try to get the corresponding group type
			if (group != null) {
				groupTypeString = group.getGroupType();
				groupType = groupTypeHome.findByPrimaryKey(groupTypeString);
			}
			else {
				// okay, group is null, but we need an instance
				// to get the alias and general group type
				groupTypeString = CoreConstants.EMPTY;
				groupType = GroupTypeBMPBean.getStaticInstance();
			}
		}
		catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}

		// get general and alias group type
		GroupType generalType = findOrCreateGeneralGroupType(groupType, groupTypeHome);
		GroupType aliasType = findOrCreateAliasGroupType(groupType, groupTypeHome);

		ArrayList groupTypes = new ArrayList();
		if (group == null) {
			// first case: group is null
			groupTypes.add(generalType);
			groupTypes.add(aliasType);
		}
		else {

			if (!groupType.getOnlySupportsSameTypeChildGroups()) {
				// add same type
				if (groupType.getSupportsSameTypeChildGroups()) {
					groupTypes.add(groupType);
				}

				int typeSize = groupTypes.size();
				// add children of type of selected group
				addGroupTypeChildren(groupTypes, groupType);

				// we only add general and alias types if there are no child types
				// defined
				if (groupTypes.size() <= typeSize) {
					if (!generalType.getType().equals(groupTypeString) && !groupTypes.contains(groupType)) {
						groupTypes.add(generalType);
					}

					if (!aliasType.getType().equals(groupTypeString) && !groupTypes.contains(groupType)) {
						groupTypes.add(aliasType);
					}
				}

			}
			else {
				// so we can define a type that cannot have children
				if (groupType.getSupportsSameTypeChildGroups()) {
					groupTypes.add(groupType);
				}
			}
		}
		return groupTypes;
	}

	@Override
	public void addGroupTypeChildren(List list, GroupType groupType) {
		Iterator iterator = groupType.getChildrenIterator();
		while (iterator != null && iterator.hasNext()) {
			GroupType child = (GroupType) iterator.next();
			if (!list.contains(child)) {
				list.add(child);
			}
			addGroupTypeChildren(list, child);
		}
	}

	@Override
	public String getGroupType(Class groupClass) throws RemoteException {
		return ((GroupHome) IDOLookup.getHome(groupClass)).getGroupType();
	}

	@Override
	public GroupType getGroupTypeFromString(String type) throws RemoteException, FinderException {
		return getGroupTypeHome().findGroupTypeByGroupTypeString(type);
	}

	/**
	 * Returns a collection of UserGroupPluginBusiness beans or an empty list
	 *
	 * @param plugins
	 * @return a collection of UserGroupPluginBusiness implementing classes
	 */
	protected Collection<UserGroupPlugInBusiness> getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(Collection<UserGroupPlugIn> plugins) {
		if (plugins == null || plugins.isEmpty()) {
			return ListUtil.getEmptyList();
		}

		List<UserGroupPlugInBusiness> list = new ArrayList<UserGroupPlugInBusiness>();
		for (UserGroupPlugIn element: plugins) {
			UserGroupPlugInBusiness pluginBiz;
			try {
				ICObject icObject = element.getBusinessICObject();
				if (icObject != null) {
					Class<UserGroupPlugInBusiness> pluginClass = null;
					try {
						pluginClass = RefactorClassRegistry.forName(icObject.getClassName());
					} catch (Exception e) {
						getLogger().log(Level.WARNING, "Error getting class for name " + icObject.getClassName() + ". ICObject ID: " + icObject.getID(), e);
					}
					if (pluginClass != null) {
						pluginBiz = getServiceInstance(pluginClass);
						list.add(pluginBiz);
					}
				}
				//else should we delete the record?
			} catch (IBOLookupException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * Gets a collection of UserGroupPluginBusiness beans that can operate on the
	 * supplied group type
	 *
	 * @param groupType
	 * @return Collection of plugins
	 * @throws RemoteException
	 */
	@Override
	public Collection<UserGroupPlugInBusiness> getUserGroupPluginsForGroupType(String groupType) throws RemoteException {
		try {
			return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findRegisteredPlugInsForGroupType(groupType));
		} catch (FinderException e) {
			// no big deal, there are no plugins registered. Return an empty list
		} catch (IDORelationshipException e) {
			// no big deal, there are no plugins registered. Return an empty list
		}

		return ListUtil.getEmptyList();
	}

	/**
	 * Gets a collection of UserGroupPluginBusiness beans that can operate on the
	 * supplied group
	 *
	 * @param group
	 * @return Collection of plugins
	 * @throws RemoteException
	 */
	@Override
	public Collection<UserGroupPlugInBusiness> getUserGroupPluginsForGroup(Group group) throws RemoteException {
		try {
			return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findRegisteredPlugInsForGroup(group));
		} catch (FinderException e) {
			// no big deal, there are no plugins registered. Return an empty list
		}

		return ListUtil.getEmptyList();
	}

	/**
	 * Gets a collection of UserGroupPluginBusiness beans that can operate on the
	 * supplied user
	 *
	 * @param user
	 * @return Collection of plugins
	 * @throws RemoteException
	 */
	@Override
	public Collection<UserGroupPlugInBusiness> getUserGroupPluginsForUser(User user) throws RemoteException {
		try {
			// THIS METHOD IS NOT FINISHED AND THE FIND METHOD ONLY GETS ALL PLUGINS
			return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findRegisteredPlugInsForUser(user));
		} catch (FinderException e) {
			// no big deal, there are no plugins registered. Return an empty list
		}

		return ListUtil.getEmptyList();
	}

	/**
	 * Gets a collection of all registered UserGroupPluginBusiness beans
	 *
	 * @return Collection of plugins
	 * @throws RemoteException
	 */
	@Override
	public Collection<UserGroupPlugInBusiness> getUserGroupPlugins() throws RemoteException {
		try {
			return getUserGroupPluginBusinessBeansFromUserGroupPluginEntities(getUserGroupPlugInHome().findAllPlugIns());
		} catch (FinderException e) {
			// no big deal, there are no plugins registered. Return an empty list
		}

		return ListUtil.getEmptyList();
	}

	/**
	 * This method will try to find the parent of the group (if only one) and then
	 * calls
	 * callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(group,parentGroup)
	 */
	@Override
	public void callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(Group group) {
		List list = group.getParentGroups();
		Group parentGroup = null;
		if (list != null && list.size() == 1) {
			parentGroup = (Group) list.iterator().next();
		}
		callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(group, parentGroup);
	}

	@Override
	public void callAllUserGroupPluginAfterGroupCreateOrUpdateMethod(Group group, Group parentGroup) {
		// get plugins and call the method
		Collection pluginsForGroup;
		try {
			pluginsForGroup = getUserGroupPluginsForGroup(group);
			Iterator plugs = pluginsForGroup.iterator();
			while (plugs.hasNext()) {
				UserGroupPlugInBusiness plugBiz = (UserGroupPlugInBusiness) plugs.next();
				plugBiz.afterGroupCreateOrUpdate(group, parentGroup);
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void callAllUserGroupPluginBeforeGroupRemoveMethod(Group group, Group parentGroup) throws RemoteException, RemoveException {
		// get plugins and call the method
		Collection pluginsForGroup;
		pluginsForGroup = getUserGroupPluginsForGroup(group);
		Iterator plugs = pluginsForGroup.iterator();
		while (plugs.hasNext()) {
			UserGroupPlugInBusiness plugBiz = (UserGroupPlugInBusiness) plugs.next();
			plugBiz.beforeGroupRemove(group, parentGroup);
		}
	}

	@Override
	public GroupTypeHome getGroupTypeHome() throws RemoteException {
		return (GroupTypeHome) this.getIDOHome(GroupType.class);
	}

	protected UserGroupPlugInHome getUserGroupPlugInHome() throws RemoteException {
		return (UserGroupPlugInHome) this.getIDOHome(UserGroupPlugIn.class);
	}

	/**
	 * Adds a group direcly under the domain (right in top under the domain in the
	 * group tree). This adds the group with GroupRelationType Top to the domain.
	 *
	 * @param domain
	 * @param group
	 * @throws CreateException
	 * @throws RemoteException
	 */
	@Override
	public void addGroupUnderDomainRoot(ICDomain domain, Group group) throws CreateException, RemoteException {
		GroupDomainRelationTypeHome gdrHome = (GroupDomainRelationTypeHome) getIDOHome(GroupDomainRelationType.class);
		GroupDomainRelationType domRelType;
		try {
			domRelType = gdrHome.getTopNodeRelationType();
			addGroupUnderDomain(domain, group, domRelType);
		}
		catch (FinderException e) {
			logWarning("Error finding GroupRelationType=TOP when adding group under domain");
			log(e);
		}
	}

	@Override
	public void addGroupUnderDomain(ICDomain domain, Group group, GroupDomainRelationType type) throws CreateException, RemoteException {
		GroupDomainRelation relation = IDOLookup.create(GroupDomainRelation.class);
		relation.setDomain(domain);
		relation.setRelatedGroup(group);

		if (type != null) {
			relation.setRelationship(type);
		}

		relation.store();
	}

	/**
	 * Method updateUsersMainAddressOrCreateIfDoesNotExist. This method can both
	 * be used to update the user main address or to create one<br>
	 * if one does not exist. Only userId and StreetName(AndNumber) are required
	 * to be not null others are optional.
	 *
	 * @param userId
	 * @param streetNameAndNumber
	 * @param postalCodeId
	 * @param countryName
	 * @param city
	 * @param province
	 * @param poBox
	 * @return Address the address that was created or updated
	 * @throws CreateException
	 * @throws RemoteException
	 */
	@Override
	public Address updateGroupMainAddressOrCreateIfDoesNotExist(Integer groupId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox) throws CreateException, RemoteException {
		Address address = null;
		if (groupId != null) {
			try {
				AddressBusiness addressBiz = getAddressBusiness();
				String streetName = addressBiz.getStreetNameFromAddressString(streetNameAndNumber);
				String streetNumber = addressBiz.getStreetNumberFromAddressString(streetNameAndNumber);

				Group group = getGroupByGroupID(groupId.intValue());
				address = getGroupMainAddress(group);

				Country country = null;

				if (countryName != null) {
					country = ((CountryHome) getIDOHome(Country.class)).findByCountryName(countryName);
				}

				PostalCode code = null;
				if (postalCodeId != null) {
					code = ((PostalCodeHome) getIDOHome(PostalCode.class)).findByPrimaryKey(postalCodeId);
				}

				boolean addAddress = false;
				/** @todo is this necessary?* */

				if (address == null) {
					AddressHome addressHome = addressBiz.getAddressHome();
					address = addressHome.create();
					AddressType mainAddressType = addressHome.getAddressType1();
					address.setAddressType(mainAddressType);
					addAddress = true;
				}

				if (country != null) {
					address.setCountry(country);
				}
				if (code != null) {
					address.setPostalCode(code);
				}
				if (province != null) {
					address.setProvince(province);
				}
				if (city != null) {
					address.setCity(city);
				}
				if (poBox != null) {
					address.setPOBox(poBox);
				}

				if (!StringUtil.isEmpty(streetName)) {
					address.setStreetName(streetName);
				}

				if (streetNumber != null) {
					address.setStreetNumber(streetNumber);
				}

				address.store();

				if (addAddress) {
					group.addAddress(address);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to update or create address for groupid : " + groupId.toString());
			}

		}
		else {
			throw new CreateException("No streetname or userId is null!");
		}

		return address;
	}

	@Override
	public AddressBusiness getAddressBusiness() throws RemoteException {
		return getServiceInstance(AddressBusiness.class);
	}

	/**
	 * Gets the users main address and returns it.
	 *
	 * @returns the address if found or null if not.
	 */
	@Override
	public Address getGroupMainAddress(Group group) throws RemoteException, IDOLookupException, IDOCompositePrimaryKeyException, IDORelationshipException {
		if (group == null) {
			return null;
		}

		AddressType type = getAddressHome().getAddressType1();
		Collection coll = group.getAddresses(type);
		if (coll == null || coll.isEmpty()) {
			return null;
		}
		// return the first element (there is only one element)
		return (Address) coll.iterator().next();
	}

	@Override
	public AddressHome getAddressHome() {
		if (this.addressHome == null) {
			try {
				this.addressHome = (AddressHome) IDOLookup.getHome(Address.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.addressHome;
	}

	@Override
	public Phone[] getGroupPhones(Group group) throws RemoteException {
		try {
			Collection phones = group.getPhones();
			// if(phones != null){
			return (Phone[]) phones.toArray(new Phone[phones.size()]);
			// }
			// return (Phone[])
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
		}
		catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * deprecated
	 *
	 *  (non-Javadoc)
	 * @see com.idega.user.business.GroupBusiness#getGroupEmail(com.idega.user.data.Group)
	 *
	 * @deprecated use getGroupMainEmail
	 *
	 */
	@Override
	@Deprecated
	public Email getGroupEmail(Group group) {
		try {
			return getGroupMainEmail(group);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public Email getGroupMainEmail(Group group) throws NoEmailFoundException {
		EmailHome home = getEmailHome();
		try {
			return home.findMainEmailForGroup(group);
		}
		catch (FinderException e) {
			String message = null;
			if (group != null) {
				message = group.getName();
			}
			throw new NoEmailFoundException(message);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException();
		}
	}

	/**
	 * updates or creates the main email address (that is the email with type "main"l)
	 */
	@Override
	public Email updateGroupMail(Group group, String email) throws CreateException, RemoteException {
		/**
		 * Updates or creates the main email address (that is the email with type "main"l)
		 * if the specifield email is empty (that is null or empty) nothing happens.
		 */
		if (StringHandler.isEmpty(email)) {
			return null;
		}
		Email mainEmail = null;
		try {
			// note: call of the following method does some repairing
			// + if main mail is not set yet a main email is figured out
			mainEmail = getGroupMainEmail(group);
		}
		catch (NoEmailFoundException ex) {
			mainEmail = null;
		}
		// email was found
		if (mainEmail != null) {
			String oldAddress = mainEmail.getEmailAddress();
			// is it an update at all?
			if (! email.equals(oldAddress)) {
				mainEmail.setEmailAddress(email);
				mainEmail.store();
			}
			return mainEmail;
		}
		// not found? create a new one!
		try {
			mainEmail = this.getEmailHome().create();
			EmailType mainEmailType = getEmailTypeHome().findMainEmailType();
			mainEmail.setEmailType(mainEmailType);
			mainEmail.setEmailAddress(email);
			mainEmail.store();
			group.addEmail(mainEmail);
			return mainEmail;
		}
		catch (FinderException ex) {
			throw new CreateException("Main email type could not be found");
		}
		catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

	@Override
	public EmailHome getEmailHome() {
		if (this.emailHome == null) {
			try {
				this.emailHome = (EmailHome) IDOLookup.getHome(Email.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.emailHome;
	}

	public EmailTypeHome getEmailTypeHome() {
		if (this.emailTypeHome == null) {
			try {
				this.emailTypeHome = (EmailTypeHome) IDOLookup.getHome(EmailType.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.emailTypeHome;
	}

	@Override
	public Phone updateGroupPhone(Group group, int phoneTypeId, String phoneNumber) throws EJBException {
		try {
			Phone phone = getGroupPhone(group, phoneTypeId);
			boolean insert = false;
			if (phone == null) {
				phone = this.getPhoneHome().create();
				phone.setPhoneTypeId(phoneTypeId);
				insert = true;
			}

			if (phoneNumber != null) {
				phone.setNumber(phoneNumber);
			}

			phone.store();
			if (insert) {
				// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(phone);
				group.addPhone(phone);
			}
			return phone;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}

	}

	@Override
	public Phone getGroupPhone(Group group, int phoneTypeId) throws RemoteException {
		try {
			Phone[] result = this.getGroupPhones(group);
			// IDOLegacyEntity[] result =
			// ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
			if (result != null) {
				for (int i = 0; i < result.length; i++) {
					if (result[i].getPhoneTypeId() == phoneTypeId) {
						return result[i];
					}
				}
			}
			return null;
		}
		catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public PhoneHome getPhoneHome() {
		if (this.phoneHome == null) {
			try {
				this.phoneHome = (PhoneHome) IDOLookup.getHome(Phone.class);
			}
			catch (RemoteException rme) {
				throw new RuntimeException(rme.getMessage());
			}
		}
		return this.phoneHome;
	}

	/**
	 * Group is removable if the group is either an alias or has no children.
	 * Childrens are other groups or users.
	 *
	 * @param group
	 * @param parentGroup
	 *          can be null
	 * @return boolean
	 * @throws RemoteException
	 * @throws RemoveException
	 */
	@Override
	public boolean isGroupRemovable(Group group, Group parentGroup) throws RemoteException, RemoveException {
		boolean canRemove = false;
		// childCount checks only groups as children
		canRemove = ((group.getGroupType().equals("alias")) || (group.getChildCount() <= 0 && (getUserBusiness().getUsersInGroup(group).isEmpty())));
		if (canRemove) {
			callAllUserGroupPluginBeforeGroupRemoveMethod(group, parentGroup);
		}
		return canRemove;
	}

	@Override
	public String getNameOfGroupWithParentName(Group group, Collection parentGroups) {
		StringBuffer buffer = new StringBuffer();
		Collection parents = parentGroups;
		buffer.append(group.getName()).append(" ");
		if (parents != null && !parents.isEmpty()) {
			Iterator par = parents.iterator();
			Group parent = (Group) par.next();
			buffer.append("(").append(parent.getName()).append(") ");
		}

		return buffer.toString();
	}

	@Override
	public String getNameOfGroupWithParentName(Group group) {
		return getNameOfGroupWithParentName(group, getParentGroups(group));
	}

	/**
	 * Optimized version of getNameOfGroupWithParentName(Group group) by Gummi
	 * 25.08.2004 Database access is minimized by passing a Map of cached
	 * groupParents and Map of cached groups to the method
	 */
	@Override
	public String getNameOfGroupWithParentName(Group group, Map<String, Collection<Integer>> cachedParents, Map<String, Group> cachedGroups) {
		return getNameOfGroupWithParentName(group, getParentGroups(group, cachedParents, cachedGroups));
	}

	private UserBusiness getUserBusiness() {
		IWApplicationContext context = getIWApplicationContext();
		try {
			return com.idega.business.IBOLookup.getServiceInstance(context, UserBusiness.class);
		}
		catch (java.rmi.RemoteException rme) {
			throw new RuntimeException(rme.getMessage());
		}
	}

	/**
	 * Creates a visible group type from the supplied group type string if it does
	 * not already exist, if it exists it will update the group types visibilty to
	 * true.
	 *
	 * @param groupType
	 * @return a GroupType bean
	 * @throws RemoteException
	 */
	@Override
	public GroupType createVisibleGroupType(String groupType) throws RemoteException {
		return createGroupTypeOrUpdate(groupType, true);
	}

	/**
	 * Creates a group type that has the visibility supplied if the type does not
	 * already exist. If it exist this method will update its visibility.
	 *
	 * @param groupType
	 * @param visible
	 * @return a GroupType bean
	 * @throws RemoteException
	 */
	@Override
	public GroupType createGroupTypeOrUpdate(String groupType, boolean visible) throws RemoteException {
		GroupTypeHome home = getGroupTypeHome();
		try {
			GroupType type = home.findByPrimaryKey(groupType);
			type.setVisibility(visible);
			return type;
		}
		catch (FinderException findEx) {
			try {
				GroupType type = home.create();
				type.setType(groupType);
				type.setVisibility(visible);
				type.store();
				return type;
			}
			catch (CreateException createEx) {
				throw new RuntimeException(createEx.getMessage());
			}
		}
	}

	private GroupType findOrCreateAliasGroupType(GroupType aGroupType, GroupTypeHome home) {
		try {
			GroupType type = home.findByPrimaryKey(home.getAliasGroupTypeString());
			return type;
		}
		catch (FinderException findEx) {
			try {
				GroupType type = home.create();
				type.setGroupTypeAsAliasGroup();
				type.setVisibility(true);
				type.store();
				return type;
			}
			catch (CreateException createEx) {
				throw new RuntimeException(createEx.getMessage());
			}
		}
	}

	private GroupType findOrCreateGeneralGroupType(GroupType aGroupType, GroupTypeHome home) {
		try {
			GroupType type = home.findByPrimaryKey(home.getGeneralGroupTypeString());
			return type;
		}
		catch (FinderException findEx) {
			try {
				GroupType type = home.create();
				type.setGroupTypeAsGeneralGroup();
				type.setVisibility(true);
				type.store();
				return type;
			}
			catch (CreateException createEx) {
				throw new RuntimeException(createEx.getMessage());
			}
		}
	}

	/**
	 * Gives all parent groups owners' primary groups, permit permission to this
	 * group. The permission to give others permissions to this group.
	 */
	@Override
	public void applyPermitPermissionToGroupsParentGroupOwnersPrimaryGroups(Group group) throws RemoteException {
		UserBusiness userBiz = getUserBusiness();
		String groupId = group.getPrimaryKey().toString();
		AccessController access = getAccessController();

		Collection<Group> col = null;
		try {
			col = getParentGroupsRecursive(group);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (col != null && !col.isEmpty()) {
			Iterator<Group> iter = col.iterator();
			while (iter.hasNext()) {
				Group parent = iter.next();
				Collection owners = AccessControl.getAllOwnerGroupPermissionsReverseForGroup(parent);

				if (owners != null && !owners.isEmpty()) {
					Iterator iter2 = owners.iterator();
					while (iter2.hasNext()) {

						ICPermission perm = (ICPermission) iter2.next();
						User user = userBiz.getUser(perm.getGroupID());
						Group primary = user.getPrimaryGroup();
						if (primary != null) {
							String primaryGroupId = primary.getPrimaryKey().toString();
							try {
								// the owners primary group
								access.setPermission(AccessController.CATEGORY_GROUP_ID, this.getIWApplicationContext(), primaryGroupId, groupId, AccessController.PERMISSION_KEY_PERMIT, Boolean.TRUE);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the user as an owner of the group.
	 *
	 * @param group
	 * @param user
	 */
	@Override
	public void applyUserAsGroupsOwner(Group group, User user) {
		AccessController access = getAccessController();

		try {
			access.setAsOwner(group, ((Integer) user.getPrimaryKey()).intValue(), this.getIWApplicationContext());
		}
		catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	@Override
	public void applyCurrentUserAsOwnerOfGroup(IWUserContext iwc, Group group) {
		User user = iwc.getCurrentUser();
		applyUserAsGroupsOwner(group, user);
	}

	/**
	 * Give the current users primary group all permission except for owner
	 *
	 */
	@Override
	public void applyAllGroupPermissionsForGroupToCurrentUsersPrimaryGroup(IWUserContext iwuc, Group group) {

		User user = iwuc.getCurrentUser();

		Group groupToGetPermissions = user.getPrimaryGroup();
		applyAllGroupPermissionsForGroupToGroup(group, groupToGetPermissions);
	}

	/**
	 * Give the users primary group all permission except for owner
	 *
	 */
	@Override
	public void applyAllGroupPermissionsForGroupToUsersPrimaryGroup(Group group, User user) {

		Group groupToGetPermissions = user.getPrimaryGroup();
		applyAllGroupPermissionsForGroupToGroup(group, groupToGetPermissions);
	}

	/**
	 * This methods gives the second group specified all permissions to the other
	 * groups except for owner permission (set to users not groups). The
	 * permissions include: view,edit,create,remove users, and the permission to
	 * give others permissions to it.
	 *
	 * @param iwac
	 * @param groupToSetPermissionTo
	 *          The group the permission apply to.
	 * @param groupToGetPermissions
	 *          The group that will own the permissions e.g. get the rights to do
	 *          the stuff.
	 */
	@Override
	public void applyAllGroupPermissionsForGroupToGroup(Group groupToSetPermissionTo, Group groupToGetPermissions) {
		AccessController access = getAccessController();
		try {
			IWApplicationContext iwac = this.getIWApplicationContext();
			String groupId = groupToGetPermissions.getPrimaryKey().toString();
			String theGroupIDToSetPermissionTo = groupToSetPermissionTo.getPrimaryKey().toString();

			// create permission
			access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac, groupId, theGroupIDToSetPermissionTo, AccessController.PERMISSION_KEY_CREATE, Boolean.TRUE);
			// edit permission
			access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac, groupId, theGroupIDToSetPermissionTo, AccessController.PERMISSION_KEY_EDIT, Boolean.TRUE);
			// delete permission
			access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac, groupId, theGroupIDToSetPermissionTo, AccessController.PERMISSION_KEY_DELETE, Boolean.TRUE);
			// view permission
			access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac, groupId, theGroupIDToSetPermissionTo, AccessController.PERMISSION_KEY_VIEW, Boolean.TRUE);
			// permission to give other permission
			access.setPermission(AccessController.CATEGORY_GROUP_ID, iwac, groupId, theGroupIDToSetPermissionTo, AccessController.PERMISSION_KEY_PERMIT, Boolean.TRUE);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * If the groupToGetInheritanceFrom has inherited permission it is copied to
	 * the other group.
	 *
	 * @param groupToGetInheritanceFrom
	 * @param groupToInheritPermissions
	 */
	@Override
	public void applyPermissionControllingFromGroupToGroup(Group groupToGetInheritanceFrom, Group groupToInheritPermissions) {

		if (groupToGetInheritanceFrom != null) {
			// is controller
			if (groupToGetInheritanceFrom.isPermissionControllingGroup()) {
				groupToInheritPermissions.setPermissionControllingGroup(groupToGetInheritanceFrom);
				groupToInheritPermissions.store();
			}
			// is being controlled
			if (groupToGetInheritanceFrom.getPermissionControllingGroupID() > 0) {
				groupToInheritPermissions.setPermissionControllingGroup(groupToGetInheritanceFrom.getPermissionControllingGroup());
				groupToInheritPermissions.store();
			}

		}

	}

	/**
	 * This method should only be called once for a newly created group if it was
	 * done in code. This method is automatically called if the group is created
	 * in the user application. Sets the user as the owner of the group and gives
	 * his primary group all group permissions to the group. Also gives all
	 * owners' primary groups of the groups parent groups permission to give
	 * others permission to this group. Finally checks the groups parent if any
	 * for inherited permissions and sets them.
	 *
	 * @param newlyCreatedGroup
	 * @param user
	 * @throws RemoteException
	 */
	@Override
	public void applyOwnerAndAllGroupPermissionsToNewlyCreatedGroupForUserAndHisPrimaryGroup(Group newlyCreatedGroup, User user) throws RemoteException {

		// set user as owner of group
		applyUserAsGroupsOwner(newlyCreatedGroup, user);

		// give the users primary group all permission except for owner
		applyAllGroupPermissionsForGroupToUsersPrimaryGroup(newlyCreatedGroup, user);

		// owners should get the permission to give permission for this group
		applyPermitPermissionToGroupsParentGroupOwnersPrimaryGroups(newlyCreatedGroup);

		// check if to parent group is a permissions controlling group or has a
		// reference to a permission controlling group
		Collection parentGroups = newlyCreatedGroup.getParentGroups();

		if (parentGroups != null && !parentGroups.isEmpty()) {
			applyPermissionControllingFromGroupToGroup((Group) parentGroups.iterator().next(), newlyCreatedGroup);
		}

		// apply permissions that have been marked to be inherited to this group
		// from its parents
		applyInheritedPermissionsToGroup(newlyCreatedGroup);

	}

	/**
	 * Applies permissions that have been marked to be inherited to this group
	 * from its parents
	 *
	 * @param newlyCreatedGroup
	 * @throws RemoteException
	 */
	@Override
	public void applyInheritedPermissionsToGroup(Group newlyCreatedGroup) throws RemoteException {
		AccessController access = getAccessController();
		Collection<Group> recursiveParents = null;
		try {
			recursiveParents = getParentGroupsRecursive(newlyCreatedGroup);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (recursiveParents != null && !recursiveParents.isEmpty()) {
			try {
				ICPermissionHome home = (ICPermissionHome) IDOLookup.getHome(ICPermission.class);
				Collection permissions = home.findAllGroupPermissionsToInheritByGroupCollection(recursiveParents);
				Iterator iter = permissions.iterator();
				while (iter.hasNext()) {
					ICPermission perm = (ICPermission) iter.next();
					try {
						access.setPermission(AccessController.CATEGORY_GROUP_ID, this.getIWApplicationContext(), Integer.toString(perm.getGroupID()), newlyCreatedGroup.getPrimaryKey().toString(), perm.getPermissionString(), Boolean.TRUE);
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			catch (FinderException e) {
				e.printStackTrace();// no parents, might happen not really an
				// error
			}
		}
	}

	/**
	 * Returns a collection (list) of User objects that have owner permission to
	 * this group
	 *
	 * @param group
	 *          to get owners for
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public Collection getOwnerUsersForGroup(Group group) throws RemoteException {
		Collection permissions = AccessControl.getAllOwnerGroupPermissionsReverseForGroup(group);
		ArrayList listOfOwnerUsers = new ArrayList();
		UserBusiness userBiz = getUserBusiness();

		// we only want active ones
		Iterator permissionsIter = permissions.iterator();
		while (permissionsIter.hasNext()) {
			ICPermission perm = (ICPermission) permissionsIter.next();
			if (perm.getPermissionValue()) {
				listOfOwnerUsers.add(userBiz.getUser(perm.getGroupID()));
			}
		}

		return listOfOwnerUsers;
	}

	/**
	 * Gets all the groups that have this metadata key and value
	 *
	 * @param key
	 * @param value
	 * @return a collection of Groups or an empty list
	 */
	@Override
	public Collection getGroupsByMetaDataKeyAndValue(String key, String value) {
		Collection groups;
		try {
			groups = getGroupHome().findGroupsByMetaData(key, value);
		}
		catch (FinderException e) {
			return ListUtil.getEmptyList();
		}

		return groups;
	}

	private Collection getParentGroupsRecursiveUsingStoredProcedure(Group aGroup, String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		return ParentGroupsRecursiveProcedure.getInstance().findParentGroupsRecursive(aGroup, groupTypes, returnSpecifiedGroupTypes);
	}

	@Override
	public NestedSetsContainer getLastGroupTreeSnapShot() throws EJBException {
		if (this.groupTreeSnapShot == null) {
			refreshGroupTreeSnapShot();
		}
		return this.groupTreeSnapShot;
	}

	@Override
	public void refreshGroupTreeSnapShotInANewThread() {
		try {
			GroupTreeRefreshThread thread = new GroupTreeRefreshThread();
			thread.start();
		}
		catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refreshGroupTreeSnapShot() throws EJBException {
		try {
			Collection domainTopNodes = this.getIWApplicationContext().getDomain().getTopLevelGroupsUnderDomain();
			NestedSetsContainer nsc = new NestedSetsContainer();
			Iterator iter = domainTopNodes.iterator();
			while (iter.hasNext()) {
				nsc.add(GroupTreeImageProcedure.getInstance().getGroupTree((Group) iter.next()));
			}
			this.groupTreeSnapShot = nsc;
		}
		catch (Exception e) {
			throw new EJBException(e);
		}
	}

	@Override
	public boolean userGroupTreeImageProcedureTopNodeSearch() {
		return GroupTreeImageProcedure.getInstance().isAvailable();
	}

	/**
	 * Returns info about groups
	 */
	@Override
	public List<GroupDataBean> getGroupsData(List<String> uniqueIds) {
		if (uniqueIds == null) {
			return null;
		}

		List<GroupDataBean> groupsData = new ArrayList<GroupDataBean>();
		GroupDataBean dataBean = null;
		Group group = null;
		for (int i = 0; i < uniqueIds.size(); i++) {
			group = null;
			try {
				group = getGroupByUniqueId(uniqueIds.get(i));
			} catch (FinderException e) {
				e.printStackTrace();
			}
			if (group != null) {
				dataBean = new GroupDataBean();

				//	Simple data
				dataBean.setName(group.getName());
				dataBean.setDescription(group.getDescription());
				dataBean.setExtraInfo(group.getExtraInfo());
				dataBean.setHomePageUrl(group.getHomePageURL());
				dataBean.setShortName(group.getShortName());

				//	Complex data (address, phone, fax, emails)
				try {
					dataBean.setAddress(getAddressParts(getGroupMainAddress(group)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				setPhoneAndFax(dataBean, group);
				dataBean.setEmailsAddresses(getEmails(group));

				//	Adding to list
				groupsData.add(dataBean);
			}
		}

		return groupsData;
	}

	private List<String> getEmails(Group group) {
		if (group == null) {
			return null;
		}

		Collection emails = group.getEmails();
		if (emails == null) {
			return null;
		}

		List<String> emailsAddresses = new ArrayList<String>();

		Iterator emailIter = emails.iterator();
		Email email = null;
		Object o = null;
		for (Iterator it = emailIter; it.hasNext(); ) {
			o = it.next();
			if (o instanceof Email) {
				email = (Email) o;
				emailsAddresses.add(email.getEmailAddress());
			}
		}
		return emailsAddresses;
	}

	private void setPhoneAndFax(GroupDataBean dataBean, Group group) {
		if (dataBean == null || group == null) {
			return;
		}

		Collection phones = group.getPhones();
		if (phones == null) {
			return;
		}
		Iterator phoneIter = phones.iterator();
		Object o = null;
		Phone phoneObj = null;
		for (Iterator it = phoneIter; it.hasNext(); ) {
			o = it.next();
			if (o instanceof Phone) {
				phoneObj = (Phone) o;
				if (phoneObj.getPhoneTypeId() == PhoneType.WORK_PHONE_ID) {
					dataBean.setPhoneNumber(phoneObj.getNumber());
				} else {
					if (phoneObj.getPhoneTypeId() == PhoneType.FAX_NUMBER_ID) {
						dataBean.setFaxNumber(phoneObj.getNumber());
					}
				}
			}
		}
	}

	@Override
	public AddressData getAddressParts(Address address) {
		if (address == null) {
			return null;
		}

		AddressData addressData = new AddressData();
		addressData.setStreetAddress(address.getStreetAddress());
		PostalCode postalCode = address.getPostalCode();
		if (postalCode != null) {
			addressData.setPostalCode(postalCode.getPostalCode());
			addressData.setPostalName(postalCode.getName());
			addressData.setCity(postalCode.getName());
		}
		else {
			addressData.setCity(address.getCity());
		}
		return addressData;
	}

	@Override
	public Collection<Group> getUserGroupsByPhrase(IWContext iwc, String phrase) {
		if (iwc == null || StringUtil.isEmpty(phrase)) {
			return null;
		}

		User currentUser = null;
		try {
			currentUser = iwc.getCurrentUser();
		} catch(NotLoggedOnException e) {
			log(Level.WARNING, "Error getting user's groups by phrase: user is not logged!");
			log(e);
		}
		if (currentUser == null) {
			return null;
		}

		Collection<Group> groups = null;
		try {
			groups = getGroupHome().findAllByNamePhrase(phrase, iwc.getCurrentLocale());
		} catch (FinderException e) {
			log(e);
		}
		if (ListUtil.isEmpty(groups)) {
			return null;
		}

		UserBusiness userBusiness = getUserBusiness();
		Collection<Group> allUserGroups = getUserGroups(iwc, currentUser, userBusiness);
		Collection<Group> userGroupsByPhrase = new ArrayList<Group>();
		for (Group group: groups) {
			try {
				if (userBusiness.isGroupUnderUsersTopGroupNode(iwc, group, currentUser, allUserGroups)) {
					userGroupsByPhrase.add(group);
				}
			} catch (RemoteException e) {
				log(e);
			}
		}

		return userGroupsByPhrase;
	}

	private Collection<Group> getUserGroups(IWContext iwc, User currentUser, UserBusiness userBusiness) {
		Collection<Group> groupsByPermissions = null;
		try {
			groupsByPermissions = userBusiness.getUsersTopGroupNodesByViewAndOwnerPermissions(currentUser, iwc);
		} catch (EJBException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		Collection<Group> directGroups = null;
		try {
			directGroups = userBusiness.getUserGroups(currentUser);
		} catch (EJBException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (ListUtil.isEmpty(groupsByPermissions) && ListUtil.isEmpty(directGroups)) {
			return null;
		}
		if (ListUtil.isEmpty(groupsByPermissions)) {
			return directGroups;
		}
		if (ListUtil.isEmpty(directGroups)) {
			return groupsByPermissions;
		}

		List<Group> userGroups = new ArrayList<Group>(directGroups);
		for (Group group: groupsByPermissions) {
			if (!userGroups.contains(group)) {
				userGroups.add(group);
			}
		}

		return userGroups;
	}

	/**
	 *
	 * Last modified: $Date: 2008/10/22 14:51:16 $ by $Author: valdas $
	 *
	 * @author <a href="mailto:gummi@idega.com">gummi</a>
	 * @version $Revision: 1.122 $
	 */
	public class GroupTreeRefreshThread extends Thread {

		private int randID;

		/**
		 *
		 */
		public GroupTreeRefreshThread() {
			this("GroupTreeRefreshThread-", (int) (Math.random() * 1000));
		}

		private GroupTreeRefreshThread(String name, int rand) {
			super(name + rand);
			this.setDaemon(true);
			this.randID = rand;
		}

		@Override
		public void run() {
			try {
				log("[GroupBusiness]: fetch grouptree, new thread started 'randID:" + this.randID + "'");
				refreshGroupTreeSnapShot();
				log("[GroupBusiness]: fetch grouptree, thread done 'randID:" + this.randID + "'");
			}
			catch (EJBException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public Collection<Group> getMostPopularGroups(Collection<String> types,
			int amount) {
		try {
			return this.getGroupHome().getMostPopularGroups(types, amount);
		}
		catch (FinderException e) {
			this.getLogger().log(Level.WARNING, "Failed getting most popular groups because of failed getting group home", e);
			return ListUtil.getEmptyList();
		}
	}

	@Override
	public Collection<Group> getGroups(Collection<String> types, int amount) {
		try {
			return this.getGroupHome().getGroups(types, amount);
		}
		catch (FinderException e) {
			this.getLogger().log(Level.WARNING, "Failed getting most popular groups because of failed getting group home", e);
			return ListUtil.getEmptyList();
		}
	}

	@Override
	public Collection<Group> getGroupsBySearchRequest(String request,
			Collection<String> types, int amount) {
		try {
			return this.getGroupHome().getGroupsBySearchRequest(request,types, amount);
		}
		catch (FinderException e) {
			this.getLogger().log(Level.WARNING, "Failed getting most popular groups because of failed getting group home", e);
			return ListUtil.getEmptyList();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.user.business.GroupBusiness#update(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Collection)
	 */
	@Override
	public List<Group> update(String groupId, String name, String description,
			String city,
			Collection<String> roles) {
		List<Group> groups = new ArrayList<Group>();

		/* Trying by id... */
		if (!StringUtil.isEmpty(groupId)) {
			Group group = null;
			try {
				group = getGroupByGroupID(Integer.valueOf(groupId));
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Failed to find group by id: " +
						groupId + " cause of:  ", e);
			}

			if (group != null) {
				groups.add(group);
			}
		}

		/* Won't create group, if no group name provided */
		if (StringUtil.isEmpty(name) && ListUtil.isEmpty(groups)) {
			getLogger().log(Level.WARNING, "No group name was provided. " +
					"No futher actions will be taken.");
			return Collections.emptyList();
		}

		/* Checking by name... */
		if (ListUtil.isEmpty(groups)) {
			Collection<Group> groupsInDatasource = null;
			try {
				groupsInDatasource = getGroupsByGroupName(name);
			} catch (RemoteException e) {
				getLogger().log(Level.WARNING,
						"Failed to get groups by name: " + name +
						" cause of: ", e);
			}

			if (!ListUtil.isEmpty(groupsInDatasource)) {
				groups.addAll(groupsInDatasource);
			}
		}

		/* Creating new one if not found */
		if (ListUtil.isEmpty(groups)) {
			try {
				groups.add(createGroup(name));
			} catch (Exception e) {
				getLogger().log(Level.WARNING,
						"Failed to create group by name: " + name +
						" cause of: ", e);
			}
		}

		/* Getting access controller for assigning roles */
		AccessController accessController = getAccessController();
		if (accessController == null) {
			return Collections.emptyList();
		}

		for (Group group : groups) {
			if (!StringUtil.isEmpty(city)) {
				try {
					updateGroupMainAddressOrCreateIfDoesNotExist(
							Integer.valueOf(group.getPrimaryKey().toString()),
							null, null, null, city, null, null);
				} catch (Exception e) {
					getLogger().log(Level.WARNING,
							"Failed to update group main address cause of: ", e);
				}
			}

			/* Setting up description */
			if (!StringUtil.isEmpty(description)) {
				group.setDescription(description);
			}

			group.store();

			/* Setting up roles */
			if (!ListUtil.isEmpty(roles)) {
				for (String role : roles) {
					accessController.addRoleToGroup(role, group, getIWApplicationContext());
				}
			}
		}

		return groups;
	}

	@Override
	public GroupDAO getGroupDAO() {
		return ELUtil.getInstance().getBean(GroupDAO.class);
	}

}