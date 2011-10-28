package com.idega.user.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneBMPBean;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressType;
import com.idega.core.net.data.ICNetwork;
import com.idega.core.net.data.ICProtocol;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDOUtil;
import com.idega.data.MetaDataCapable;
import com.idega.data.UniqueIDCapable;
import com.idega.data.query.AND;
import com.idega.data.query.Column;
import com.idega.data.query.Criteria;
import com.idega.data.query.InCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;

/**
 * Title: IW Core Description: Copyright: Copyright (c) 2001-2003 idega software Company: idega.is
 * 
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,
 * @version 1.0
 */
public class GroupBMPBean extends com.idega.core.data.GenericGroupBMPBean implements Group, ICTreeNode, MetaDataCapable, UniqueIDCapable {

	private static final int PREFETCH_SIZE = 100;
	public static final int GROUP_ID_EVERYONE = -7913;
	public static final int GROUP_ID_USERS = -1906;

	private static final String RELATION_TYPE_GROUP_PARENT = "GROUP_PARENT";
	private static final String SQL_RELATION_ADDRESS = "IC_GROUP_ADDRESS";
	public final static String SQL_RELATION_EMAIL = "IC_GROUP_EMAIL";
	public final static String SQL_RELATION_PHONE = "IC_GROUP_PHONE";

	private static final String ENTITY_NAME = "ic_group";
	static final String COLUMN_GROUP_ID = "IC_GROUP_ID";
	static final String COLUMN_NAME = "NAME";
	static final String COLUMN_GROUP_TYPE = "GROUP_TYPE";
	static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	static final String COLUMN_EXTRA_INFO = "EXTRA_INFO";
	static final String COLUMN_CREATED = "CREATED";
	static final String COLUMN_HOME_PAGE_ID = "HOME_PAGE_ID";
	static final String COLUMN_HOME_FOLDER_ID = "HOME_FOLDER_ID";
	static final String COLUMN_ALIAS_TO_GROUP = "ALIAS_ID";
	static final String COLUMN_PERMISSION_CONTROLLING_GROUP = "PERM_GROUP_ID";
	static final String COLUMN_IS_PERMISSION_CONTROLLING_GROUP = "IS_PERM_CONTROLLING";
	static final String COLUMN_SHORT_NAME = "SHORT_NAME";
	static final String COLUMN_ABBREVATION = "ABBR";

	static final String META_DATA_HOME_PAGE = "homepage";

	private static List userGroupTypeSingletonList;

	protected boolean doInsertInCreate() {
		return true;
	}

	public final void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getNameColumnName(), "Group name", true, true, "java.lang.String");
		addAttribute(getGroupDescriptionColumnName(), "Description", true, true, "java.lang.String");
		addAttribute(getExtraInfoColumnName(), "Extra information", true, true, "java.lang.String");
		addAttribute(COLUMN_CREATED, "Created when", Timestamp.class);
		addAttribute(getColumnNameHomePageID(), "Home page ID", true, true, Integer.class, "many-to-one", ICPage.class);
		setNullable(getColumnNameHomePageID(), true);
		addAttribute(COLUMN_SHORT_NAME, "Short name", true, true, String.class);
		addAttribute(COLUMN_ABBREVATION, "Abbrevation", true, true, String.class);

		// adds a unique id string column to this entity that is set when the entity
		// is first stored.
		addUniqueIDColumn();

		addMetaDataRelationship();
		// can have extra info in the ic_metadata table

		// id of the group that has the permissions for this group. If this is not
		// null then this group has inherited permissions.
		addManyToOneRelationship(COLUMN_PERMISSION_CONTROLLING_GROUP, Group.class);
		addManyToOneRelationship(getGroupTypeColumnName(), GroupType.class);

		addAttribute(COLUMN_IS_PERMISSION_CONTROLLING_GROUP, "Do children of this group get same permissions", true, true, Boolean.class);

		addManyToOneRelationship(COLUMN_ALIAS_TO_GROUP, Group.class);
		setNullable(COLUMN_ALIAS_TO_GROUP, true);

		addManyToOneRelationship(COLUMN_HOME_FOLDER_ID, ICFile.class);

		// indexes
		addIndex("IDX_IC_GROUP_1", new String[] { COLUMN_GROUP_TYPE, COLUMN_GROUP_ID });
		addIndex("IDX_IC_GROUP_2", COLUMN_NAME);
		addIndex("IDX_IC_GROUP_3", COLUMN_GROUP_ID);
		addIndex("IDX_IC_GROUP_4", COLUMN_GROUP_TYPE);
		addIndex("IDX_IC_GROUP_5", new String[] { COLUMN_GROUP_ID, COLUMN_GROUP_TYPE });
		addIndex("IDX_IC_GROUP_6", COLUMN_HOME_PAGE_ID);
		addIndex("IDX_IC_GROUP_7", COLUMN_ABBREVATION);
		addIndex("IDX_IC_GROUP_8", new String[] { COLUMN_GROUP_ID, COLUMN_NAME, COLUMN_GROUP_TYPE });
		addIndex("IDX_IC_GROUP_9", UNIQUE_ID_COLUMN_NAME);
		addIndex("IDX_IC_GROUP_10", new String[] { COLUMN_GROUP_TYPE, COLUMN_NAME });

		this.addManyToManyRelationShip(ICNetwork.class, "ic_group_network");
		this.addManyToManyRelationShip(ICProtocol.class, "ic_group_protocol");
		this.addManyToManyRelationShip(Phone.class, SQL_RELATION_PHONE);
		this.addManyToManyRelationShip(Email.class, SQL_RELATION_EMAIL);
		this.addManyToManyRelationShip(Address.class, SQL_RELATION_ADDRESS);
	}

	public final String getEntityName() {
		return ENTITY_NAME;
	}

	// public String getNameOfMiddleTable(IDOLegacyEntity entity1,IDOLegacyEntity
	// entity2){
	// return "ic_group_user";
	// }
	public void setDefaultValues() {
		// DO NOT USE setColumn
		initializeColumnValue(COLUMN_GROUP_TYPE, getGroupTypeValue());
		initializeColumnValue(COLUMN_CREATED, IWTimestamp.getTimestampRightNow());
	}

	// public void insertStartData(){
	// try {
	// GroupTypeHome tghome = (GroupTypeHome)IDOLookup.getHome(GroupType.class);
	// try{
	// GroupType type = tghome.findByPrimaryKey(getGroupTypeKey());
	// } catch(FinderException e){
	// e.printStackTrace();
	// try {
	// GroupType type = tghome.create();
	// type.setType(getGroupTypeKey());
	// type.setDescription(getGroupTypeDescription());
	// type.store();
	// }
	// catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }
	// }
	// catch (RemoteException rmi){
	// throw new EJBException(rmi);
	// }
	// }
	// protected boolean doInsertInCreate(){
	// return true;
	// }
	/**
	 * overwrite in extended classes
	 */
	public String getGroupTypeValue() {
		return getGroupTypeHome().getGeneralGroupTypeString();
	}

	public String getGroupTypeKey() {
		return getGroupTypeValue();
	}

	public String getGroupTypeDescription() {
		return "";
	}

	/* ColumNames begin */
	public static String getColumnNameGroupID() {
		return COLUMN_GROUP_ID;
	}

	public static String getNameColumnName() {
		return COLUMN_NAME;
	}

	public static String getGroupTypeColumnName() {
		return COLUMN_GROUP_TYPE;
	}

	public static String getGroupDescriptionColumnName() {
		return COLUMN_DESCRIPTION;
	}

	public static String getExtraInfoColumnName() {
		return COLUMN_EXTRA_INFO;
	}

	public static String getColumnNameHomePageID() {
		return COLUMN_HOME_PAGE_ID;
	}

	public static String getColumnNameShortName() {
		return COLUMN_SHORT_NAME;
	}

	public static String getColumnNameAbbrevation() {
		return COLUMN_ABBREVATION;
	}

	/* ColumNames end */
	/* functions begin */
	public String getName() {
		return (String) getColumnValue(getNameColumnName());
	}

	public void setName(String name) {
		setColumn(getNameColumnName(), name);
	}

	public String getGroupType() {
		// try {
		// was going to the database everytime!! I kill you tryggvi/gummi (eiki)

		// return (String) ((GroupType)
		// getColumnValue(getGroupTypeColumnName())).getPrimaryKey();
		return getStringColumnValue(getGroupTypeColumnName());

		// } catch (RemoteException ex) {
		// throw new EJBException(ex);
		// }
	}

	public GroupType getGroupTypeEntity() {
		return (GroupType) getColumnValue(getGroupTypeColumnName());
	}
	
	public void setGroupType(String groupType) {
		setColumn(getGroupTypeColumnName(), groupType);
	}

	public void setGroupType(GroupType groupType) {
		setColumn(getGroupTypeColumnName(), groupType);
	}

	public void setAliasID(int id) {
		setColumn(COLUMN_ALIAS_TO_GROUP, id);
	}

	public void setAlias(Group alias) {
		setColumn(COLUMN_ALIAS_TO_GROUP, alias);
	}

	public int getAliasID() {
		return getIntColumnValue(COLUMN_ALIAS_TO_GROUP);
	}

	public Group getAlias() {
		return (Group) getColumnValue(COLUMN_ALIAS_TO_GROUP);
	}

	/**
	 * This only returns true if the group is of the type alias and the id is bigger than 0
	 */
	public boolean isAlias() {
		return ("alias".equals(getGroupType()) && (getAliasID() > 0));
	}

	public void setPermissionControllingGroupID(int id) {
		setColumn(COLUMN_PERMISSION_CONTROLLING_GROUP, id);
	}

	public void setPermissionControllingGroup(Group controllingGroup) {
		setColumn(COLUMN_PERMISSION_CONTROLLING_GROUP, controllingGroup);
	}

	public int getPermissionControllingGroupID() {
		return getIntColumnValue(COLUMN_PERMISSION_CONTROLLING_GROUP);
	}

	public Group getPermissionControllingGroup() {
		return (Group) getColumnValue(COLUMN_PERMISSION_CONTROLLING_GROUP);
	}

	public void setShortName(String shortName) {
		setColumn(COLUMN_SHORT_NAME, shortName);
	}

	public void setAbbrevation(String abbr) {
		setColumn(COLUMN_ABBREVATION, abbr);
	}

	public String getDescription() {
		return (String) getColumnValue(getGroupDescriptionColumnName());
	}

	public void setDescription(String description) {
		setColumn(getGroupDescriptionColumnName(), description);
	}

	public String getExtraInfo() {
		return (String) getColumnValue(getExtraInfoColumnName());
	}

	public void setExtraInfo(String extraInfo) {
		setColumn(getExtraInfoColumnName(), extraInfo);
	}

	public Timestamp getCreated() {
		return ((Timestamp) getColumnValue(COLUMN_CREATED));
	}

	public void setCreated(Timestamp created) {
		setColumn(GroupBMPBean.COLUMN_CREATED, created);
	}

	public int getHomePageID() {
		return getIntColumnValue(getColumnNameHomePageID());
	}

	public ICPage getHomePage() {
		return (ICPage) getColumnValue(getColumnNameHomePageID());
	}

	public void setHomePageID(int pageID) {
		setColumn(getColumnNameHomePageID(), pageID);
	}

	public void setHomePageID(Integer pageID) {
		setColumn(getColumnNameHomePageID(), pageID);
	}

	public void setHomePage(ICPage page) {
		setColumn(getColumnNameHomePageID(), page);
	}

	public int getHomeFolderID() {
		return getIntColumnValue(COLUMN_HOME_FOLDER_ID);
	}

	public ICFile getHomeFolder() {
		return (ICFile) getColumnValue(COLUMN_HOME_FOLDER_ID);
	}

	public void setHomeFolderID(int fileID) {
		setColumn(COLUMN_HOME_FOLDER_ID, fileID);
	}

	public void setHomeFolderID(Integer fileID) {
		setColumn(COLUMN_HOME_FOLDER_ID, fileID);
	}

	public void setHomeFolder(ICFile file) {
		setHomeFolderID((Integer) file.getPrimaryKey());
	}

	public String getShortName() {
		return getStringColumnValue(COLUMN_SHORT_NAME);
	}

	public String getAbbrevation() {
		return getStringColumnValue(COLUMN_ABBREVATION);
	}

	public String getHomePageURL() {
		return getMetaData(META_DATA_HOME_PAGE);
	}

	public void setHomePageURL(String homePage) {
		setMetaData(META_DATA_HOME_PAGE, homePage);
	}

	public void setIsPermissionControllingGroup(boolean isControlling) {
		setColumn(COLUMN_IS_PERMISSION_CONTROLLING_GROUP, isControlling);
	}

	public boolean isPermissionControllingGroup() {
		return getBooleanColumnValue(COLUMN_IS_PERMISSION_CONTROLLING_GROUP, false);
	}

	/**
	 * Gets a list of all the groups that this "group" is directly member of.
	 * 
	 * @see com.idega.user.data.Group#getListOfAllGroupsContainingThis()
	 */
	public List getParentGroups() throws EJBException {
		return getParentGroups(null, null);
	}

	/**
	 * Optimized version of getParentGroups() by Sigtryggur 22.06.2004 Database access is minimized by passing a Map of cached groupParents and Map of
	 * cached groups to the method
	 */
	public List getParentGroups(Map cachedParents, Map cachedGroups) throws EJBException {
		List theReturn = new ArrayList();
		try {
			Group parent = null;
			Collection parents = getCollectionOfParents(cachedParents, cachedGroups);
			Iterator parIter = parents.iterator();
			while (parIter.hasNext()) {
				parent = (Group) parIter.next();
				if (parent != null && !theReturn.contains(parent)) {
					theReturn.add(parent);
				}
			}
			if (isUser()) {
				try {
					User user = getUserForGroup();
					Group usersPrimaryGroup = null;
					String key = String.valueOf(user.getPrimaryGroupID());
					if (cachedGroups != null) {
						if (cachedGroups.containsKey(key)) {
							usersPrimaryGroup = (Group) cachedGroups.get(key);
						}
						else {
							usersPrimaryGroup = user.getPrimaryGroup();
							cachedGroups.put(key, usersPrimaryGroup);
						}
					}
					else {
						usersPrimaryGroup = user.getPrimaryGroup();
					}
					if (usersPrimaryGroup != null && !theReturn.contains(usersPrimaryGroup)) {
						theReturn.add(usersPrimaryGroup);
					}
				}
				catch (FinderException e1) {
					e1.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
		return theReturn;
	}

	private Collection getCollectionOfParents(Map cachedParents, Map cachedGroups) throws FinderException {
		Collection col = null;
		String key = this.getPrimaryKey().toString();
		if (cachedParents != null) {
			if (cachedParents.containsKey(key)) {
				col = (Collection) cachedParents.get(key);
			}
			else {
				col = ejbFindParentGroups(this.getID());
				cachedParents.put(key, col);
			}
		}
		else {
			col = ejbFindParentGroups(this.getID());
		}

		Collection returnCol = new ArrayList();
		Group parent = null;
		Integer parentID = null;
		Iterator iter = col.iterator();
		while (iter.hasNext()) {
			parentID = (Integer) iter.next();
			key = parentID.toString();
			if (cachedGroups != null) {
				if (cachedGroups.containsKey(key)) {
					parent = (Group) cachedGroups.get(key);
				}
				else {
					parent = getGroupHome().findByPrimaryKey(parentID);
					cachedGroups.put(key, parent);
				}
			}
			else {
				parent = getGroupHome().findByPrimaryKey(parentID);
			}
			returnCol.add(parent);
		}
		return returnCol;
	}

	protected Collection getChildGroupRelationships() throws FinderException {
		// null type is included as relation type for backwards compatability.
		return this.getGroupRelationHome().findGroupsRelationshipsByRelatedGroup(this.getID(), RELATION_TYPE_GROUP_PARENT, null);
	}

	/**
	 * Returns the User instance representing the Group if the Group is of type UserGroupRepresentative
	 * 
	 * @throws IDOLookupException
	 * @throws FinderException
	 */
	private User getUserForGroup() throws IDOLookupException, FinderException {
		UserHome uHome = (UserHome) IDOLookup.getHome(User.class);
		return uHome.findUserForUserGroup(this);
	}

	/**
	 * Finds all the GroupRelations that point to groups that "this" group is a direct parent for
	 * 
	 * @return Collection of GroupRelations
	 */
	protected Collection getParentalGroupRelationships() throws FinderException {
		// null type is included as relation type for backwards compatability.
		return this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), RELATION_TYPE_GROUP_PARENT, null);
	}

	/**
	 * @deprecated
	 */
	protected List getListOfAllGroupsContaining(int group_id) throws EJBException {
		try {
			Group group = this.getGroupHome().findByPrimaryKey(new Integer(group_id));
			return group.getParentGroups();
		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}

	public Integer ejbCreateGroup() throws CreateException {
		return (Integer) this.ejbCreate();
	}

	public void ejbPostCreateGroup() throws CreateException {
	}

	public Collection ejbFindGroupsByName(String name) throws FinderException {
		return this.idoFindPKsBySQL("select * from " + this.getEntityName() + " where " + GroupBMPBean.getNameColumnName() + " = '" + name + "'");
	}

	public Collection ejbFindGroupsByNameAndGroupTypes(String name, Collection groupTypes, boolean onlyReturnTypesInCollection) throws FinderException {
		// String groupTypeValue = null;
		String notString = null;
		IDOQuery query = idoQuery();
		query.append("select * from " + this.getEntityName() + " where " + GroupBMPBean.getNameColumnName() + " = '" + name + "' and " + GroupBMPBean.getGroupTypeColumnName());
		if (groupTypes != null && groupTypes.size() == 1) {
			notString = onlyReturnTypesInCollection ? " " : " !";
			query.append(notString + "= '" + groupTypes.iterator().next() + "'");
		}
		else {
			notString = onlyReturnTypesInCollection ? " " : " not ";
			query.append(notString + "in (");
			query.appendCommaDelimitedWithinSingleQuotes(groupTypes).append(")");
		}
		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindGroupsByGroupTypeAndLikeName(String groupType, String partOfGroupName) throws FinderException {
		return this.idoFindPKsBySQL("select * from " + this.getEntityName() + " where " + GroupBMPBean.getGroupTypeColumnName() + " = '" + groupType + "' and " + GroupBMPBean.getNameColumnName() + " like '" + partOfGroupName + "' order by " + GroupBMPBean.getNameColumnName());
	}

	public Collection ejbFindGroupsByAbbreviation(String abbreviation) throws FinderException {
		return this.idoFindPKsBySQL("select * from " + this.getEntityName() + " where " + COLUMN_ABBREVATION + " = '" + abbreviation + "'");
	}

	public Collection ejbFindGroupsByNameAndDescription(String name, String description) throws FinderException {
		Table table = new Table(this);
		Column nameCol = new Column(table, getNameColumnName());
		Column desc = new Column(table, getGroupDescriptionColumnName());

		SelectQuery query = new SelectQuery(table);
		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(nameCol, MatchCriteria.EQUALS, name));
		query.addCriteria(new MatchCriteria(desc, MatchCriteria.EQUALS, description));

		return this.idoFindPKsByQuery(query);
	}

	public Integer ejbFindGroupByPrimaryKey(Object primaryKey) throws FinderException {
		return (Integer) this.ejbFindByPrimaryKey(primaryKey);
	}

	/**
	 * @return A list of groups (not users) under this group
	 */
	public List getChildGroups() throws EJBException {
		List theReturn = new ArrayList();

		try {
			theReturn.addAll(ListUtil.convertCollectionToList(getGroupHome().findGroupsContained(this, getUserGroupTypeList(), false)));
			return theReturn;
		}
		catch (FinderException e) {
			e.printStackTrace();
			return theReturn;
		}

	}

	/**
	 * Gets the children of the containingGroup
	 * 
	 * @param containingGroup
	 * @param groupTypes
	 * @param returnTypes
	 * @return
	 * @throws FinderException
	 */
	public Collection ejbFindGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes) throws FinderException {
		Table groupTable = new Table(ENTITY_NAME, "g");
		Table groupRelTable = new Table(GroupRelationBMPBean.TABLE_NAME, "gr");
		SelectQuery query = new SelectQuery(groupTable);
		query.addColumn(new WildCardColumn(groupTable));
		query.addJoin(groupTable, COLUMN_GROUP_ID, groupRelTable, GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN);
		if (groupTypes != null && !groupTypes.isEmpty()) {
			if (groupTypes.size() == 1) {
				if (returnTypes) {
					query.addCriteria(new MatchCriteria(groupTable, COLUMN_GROUP_TYPE, MatchCriteria.EQUALS, groupTypes.iterator().next().toString()));
				} else {
					query.addCriteria(new MatchCriteria(groupTable, COLUMN_GROUP_TYPE, MatchCriteria.NOTEQUALS, groupTypes.iterator().next().toString()));					
				}
			}
			else {
				query.addCriteria(new InCriteria(groupTable, COLUMN_GROUP_TYPE, groupTypes, !returnTypes));
			}
		}
		query.addCriteria(new MatchCriteria(groupRelTable, GroupRelationBMPBean.GROUP_ID_COLUMN, MatchCriteria.EQUALS, containingGroup.getPrimaryKey()));
		query.addCriteria(new MatchCriteria(groupRelTable, GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN, MatchCriteria.EQUALS, RELATION_TYPE_GROUP_PARENT));
		query.addCriteria(new OR(new MatchCriteria(groupRelTable, GroupRelationBMPBean.STATUS_COLUMN, MatchCriteria.EQUALS, GroupRelationBMPBean.STATUS_ACTIVE), new MatchCriteria(groupRelTable, GroupRelationBMPBean.STATUS_COLUMN, MatchCriteria.EQUALS, GroupRelationBMPBean.STATUS_PASSIVE_PENDING)));
		query.addOrder(groupTable, COLUMN_NAME, true);
				
		return idoFindPKsByQueryUsingLoadBalance(query, PREFETCH_SIZE);
		// return idoFindPKsBySQL(query.toString());
	}

	public Collection ejbFindGroupsContainedTemp(Group containingGroup, Collection groupTypes, boolean returnTypes) throws FinderException {

		String findGroupRelationsSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer) containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);

		SelectQuery query = idoSelectQuery();
		Criteria theCriteria = null;
		if (groupTypes != null && !groupTypes.isEmpty()) {
			theCriteria = new InCriteria(idoQueryTable(), COLUMN_GROUP_TYPE, groupTypes, !returnTypes);
		}

		Criteria inCr = new InCriteria(idoQueryTable(), COLUMN_GROUP_ID, findGroupRelationsSQL);

		if (theCriteria == null) {
			theCriteria = inCr;
		}
		else {
			theCriteria = new AND(theCriteria, inCr);
		}

		query.addCriteria(theCriteria);
		query.addOrder(idoQueryTable(), GroupBMPBean.COLUMN_NAME, true);

		return idoFindPKsByQueryUsingLoadBalance(query, PREFETCH_SIZE);
		// return idoFindPKsBySQL(query.toString());
	}

	/**
	 * Gets the children of the containingGroup
	 * 
	 * @param containingGroup
	 * @param groupTypeProxy
	 *          group type to return
	 * @param returnTypes
	 * @return
	 * @throws FinderException
	 */
	public Collection ejbFindGroupsContained(Group containingGroup, Group groupTypeProxy) throws FinderException {

		Table groupTable = new Table(ENTITY_NAME, "g");
		Table groupRelTable = new Table(GroupRelationBMPBean.TABLE_NAME, "gr");
		SelectQuery query = new SelectQuery(groupTable);
		query.addColumn(new WildCardColumn(groupTable));
		query.addJoin(groupTable, COLUMN_GROUP_ID, groupRelTable, GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN);

		query.addCriteria(new MatchCriteria(groupTable, COLUMN_GROUP_TYPE, MatchCriteria.EQUALS, groupTypeProxy.getGroupTypeKey()));
		query.addCriteria(new MatchCriteria(groupRelTable, GroupRelationBMPBean.GROUP_ID_COLUMN, MatchCriteria.EQUALS, containingGroup.getPrimaryKey()));
		query.addCriteria(new MatchCriteria(groupRelTable, GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN, MatchCriteria.EQUALS, RELATION_TYPE_GROUP_PARENT));
		query.addCriteria(new OR(new MatchCriteria(groupRelTable, GroupRelationBMPBean.STATUS_COLUMN, MatchCriteria.EQUALS, GroupRelationBMPBean.STATUS_ACTIVE), new MatchCriteria(groupRelTable, GroupRelationBMPBean.STATUS_COLUMN, MatchCriteria.EQUALS, GroupRelationBMPBean.STATUS_PASSIVE_PENDING)));

		query.addOrder(groupTable, COLUMN_NAME, true);

		return idoFindPKsByQueryIgnoringCacheAndUsingLoadBalance(query, (GenericEntity) groupTypeProxy, groupTypeProxy.getSelectQueryConstraints(), PREFETCH_SIZE);
		// return idoFindPKsBySQL(query.toString());
	}

	public Collection ejbFindGroupsContainedOld(Group containingGroup, Group groupTypeProxy) throws FinderException {

		String findGroupRelationsSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer) containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);

		SelectQuery query = idoSelectQuery();
		query.addCriteria(new MatchCriteria(idoQueryTable(), COLUMN_GROUP_TYPE, MatchCriteria.LIKE, groupTypeProxy.getGroupTypeKey()));
		query.addCriteria(new InCriteria(idoQueryTable(), COLUMN_GROUP_ID, findGroupRelationsSQL));

		query.addOrder(idoQueryTable(), COLUMN_NAME, true);

		return idoFindPKsByQueryIgnoringCacheAndUsingLoadBalance(query, (GenericEntity) groupTypeProxy, groupTypeProxy.getSelectQueryConstraints(), PREFETCH_SIZE);
		// return idoFindPKsBySQL(query.toString());
	}

	public int ejbHomeGetNumberOfGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes) throws FinderException, IDOException {
		String relatedSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer) containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);

		if (groupTypes != null && !groupTypes.isEmpty()) {
			IDOQuery query = idoQuery();
			query.appendSelectCountIDFrom(this.getEntityName(), getIDColumnName());
			query.appendWhere(GroupBMPBean.COLUMN_GROUP_TYPE);
			IDOQuery subQuery = idoQuery();
			subQuery.appendCommaDelimitedWithinSingleQuotes(groupTypes);
			if (returnTypes) {
				query.appendIn(subQuery);
			}
			else {
				query.appendNotIn(subQuery);
			}
			query.appendAnd();
			query.append(GroupBMPBean.COLUMN_GROUP_ID);
			query.appendIn(relatedSQL);
			return this.idoGetNumberOfRecords(query.toString());
		}
		else {
			System.err.println("ejbHomeGetNumberOfGroupsContained :NO GROUP TYPES SUPPLIED!");
			return 0;
		}
	}

	public int ejbHomeGetNumberOfVisibleGroupsContained(Group containingGroup) throws FinderException, IDOException {
		//String relatedSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer) containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);
		//String visibleGroupTypes = getGroupTypeHome().getVisibleGroupTypesSQLString();

		/*IDOQuery query = idoQuery();
		query.appendSelectCountIDFrom(this.getEntityName(), getIDColumnName());
		
		query.appendWhere(GroupBMPBean.COLUMN_GROUP_TYPE);
		query.appendIn(visibleGroupTypes);
		query.appendAnd();
		query.append(GroupBMPBean.COLUMN_GROUP_ID);
		query.appendIn(relatedSQL);*/
		StringBuffer query = new StringBuffer("select count(g.");
		query.append(this.getIDColumnName());
		query.append(") from ");
		query.append(this.getEntityName());
		query.append(" g, ic_group_type t, ic_group_relation r where g.group_type = t.group_type and t.is_visible != 'N' and g.ic_group_id = r.related_ic_group_id and r.ic_group_id =");
		query.append(((Integer) containingGroup.getPrimaryKey()).intValue());
		query.append(" and r.relationship_type = 'GROUP_PARENT' and (r.group_relation_status = 'ST_ACTIVE' or r.group_relation_status = 'PASS_PENDING')");
		
		//System.out.println("sql = " + query.toString());
		
		return this.idoGetNumberOfRecords(query.toString());

	}

	public Collection ejbFindTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes) throws FinderException {
		String relationsSQL = this.getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer) containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());

		if (groupTypes != null && !groupTypes.isEmpty()) {
			IDOQuery query = idoQuery();
			query.appendSelectAllFrom(this.getEntityName());
			query.appendWhere(GroupBMPBean.COLUMN_GROUP_TYPE);
			IDOQuery subQuery = idoQuery();
			subQuery.appendCommaDelimitedWithinSingleQuotes(groupTypes);
			if (returnTypes) {
				query.appendIn(subQuery);
			}
			else {
				query.appendNotIn(subQuery);
			}
			query.appendAnd();
			query.append(GroupBMPBean.COLUMN_GROUP_ID);
			query.appendIn(relationsSQL);
			query.appendOrderBy(GroupBMPBean.COLUMN_NAME);
			// System.out.println("[GroupBMPBean](ejbFindGroupsContained):
			// "+query.toString());
			return this.idoFindPKsBySQL(query.toString());
		}
		else {
			return ListUtil.getEmptyList();
		}
	}

	public int ejbHomeGetNumberOfTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes) throws FinderException, IDOException {
		String relationsSQL = getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer) containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());

		if (groupTypes != null && !groupTypes.isEmpty()) {
			IDOQuery query = idoQuery();
			query.appendSelectCountIDFrom(this.getEntityName(), getIDColumnName());
			query.appendWhere(GroupBMPBean.COLUMN_GROUP_TYPE);
			IDOQuery subQuery = idoQuery();
			subQuery.appendCommaDelimitedWithinSingleQuotes(groupTypes);
			if (returnTypes) {
				query.appendIn(subQuery);
			}
			else {
				query.appendNotIn(subQuery);
			}
			query.appendAnd();
			query.append(GroupBMPBean.COLUMN_GROUP_ID);
			query.appendIn(relationsSQL);
			// System.out.println("[GroupBMPBean](ejbHomeGetNumberOfGroupsContained):
			// "+query.toString());
			return this.idoGetNumberOfRecords(query.toString());
		}
		else {
			System.err.println("ejbHomeGetNumberOfTopNodeGroupsContained :NO GROUP TYPES SUPPLIED!");
			return 0;
		}
	}

	public int ejbHomeGetNumberOfTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException, IDOException {
		String relatedSQL = getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer) containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());
		String visibleGroupTypes = getGroupTypeHome().getVisibleGroupTypesSQLString();

		IDOQuery query = idoQuery();
		query.appendSelectCountIDFrom(this.getEntityName(), getIDColumnName());
		query.appendWhere(GroupBMPBean.COLUMN_GROUP_TYPE);
		query.appendIn(visibleGroupTypes);
		query.appendAnd();
		query.append(GroupBMPBean.COLUMN_GROUP_ID);
		query.appendIn(relatedSQL);
		return this.idoGetNumberOfRecords(query.toString());

	}

	public Collection ejbFindTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException {
		String relationsSQL = this.getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer) containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());
		String visibleGroupTypes = getGroupTypeHome().getVisibleGroupTypesSQLString();

		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this.getEntityName());
		query.appendWhere(GroupBMPBean.COLUMN_GROUP_TYPE);
		query.appendIn(visibleGroupTypes);
		query.appendAnd();
		query.append(GroupBMPBean.COLUMN_GROUP_ID);
		query.appendIn(relationsSQL);
		query.appendOrderBy(GroupBMPBean.COLUMN_NAME);

		return this.idoFindPKsBySQL(query.toString());
	}

	/**
	 * @todo change name to getGroupsContained();
	 */
	// private List getListOfAllGroupsContainedLegacy()throws EJBException{
	// String tableToSelectFrom = "IC_GROUP_TREE";
	// StringBuffer buffer=new StringBuffer();
	// buffer.append("select CHILD_IC_GROUP_ID from ");
	// buffer.append(tableToSelectFrom);
	// buffer.append(" where ");
	// buffer.append("IC_GROUP_ID");
	// buffer.append("=");
	// buffer.append(this.getID());
	// String SQLString=buffer.toString();
	// Connection conn= null;
	// Statement Stmt= null;
	// Vector vector = new Vector();
	// try
	// {
	// conn = getConnection(getDatasource());
	// Stmt = conn.createStatement();
	// ResultSet RS = Stmt.executeQuery(SQLString);
	// while (RS.next()){
	//
	// IDOLegacyEntity tempobj=null;
	// try{
	// tempobj =
	// (IDOLegacyEntity)Class.forName(this.getClass().getName()).newInstance();
	// tempobj.findByPrimaryKey(RS.getInt("CHILD_IC_GROUP_ID"));
	// }
	// catch(Exception ex){
	// System.err.println("There was an error in " + this.getClass().getName()
	// +".getAllGroupsContainingThis(): "+ex.getMessage());
	//
	// }
	//
	// vector.addElement(tempobj);
	//
	// }
	// RS.close();
	//
	// }
	// catch(Exception e){
	// throw new EJBException(e.getMessage());
	// }
	// finally{
	// if(Stmt != null){
	// try{
	// Stmt.close();
	// }
	// catch(SQLException sqle){}
	// }
	// if (conn != null){
	// freeConnection(getDatasource(),conn);
	// }
	// }
	//
	// if (vector != null){
	// vector.trimToSize();
	// return vector;
	// //return (Group[])
	// vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
	// }
	// else{
	// return null;
	// }
	//
	//
	// //return (Group[])this.findReverseRelated(this);
	//
	// }
	/**
	 * 
	 * @todo change implementation: let the database handle the filtering
	 * 
	 */
	public List getChildGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		List theReturn = new ArrayList();

		List types = null;

		if (groupTypes != null && groupTypes.length > 0) {
			types = ListUtil.convertStringArrayToList(groupTypes);
		}

		try {
			return ListUtil.convertCollectionToList(getGroupHome().findGroupsContained(this, types, returnSpecifiedGroupTypes));
		}
		catch (FinderException e) {
			e.printStackTrace();
			return theReturn;
		}
	}

	public List getChildGroupsIDs(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		List theReturn = new ArrayList();

		List types = null;

		if (groupTypes != null && groupTypes.length > 0) {
			types = ListUtil.convertStringArrayToList(groupTypes);
		}

		try {
			return ListUtil.convertCollectionToList(getGroupHome().findGroupsContainedIDs(this, types, returnSpecifiedGroupTypes));
		}
		catch (FinderException e) {
			e.printStackTrace();
			return theReturn;
		}
	}

	/**
	 * Returns collection of Childs that match the type of 'groupTypeProxy' and according to groupTypeProxy.getSelectQueryConstrains(). The objects in
	 * the collection will be of the same class as 'groupTypeProxy' i.e. if groupTypeProxy implements User then it will be collection of User elements
	 */
	public Collection getChildGroups(Group groupTypeProxy) throws EJBException {

		try {
			return getGroupHome().findGroupsContained(this, groupTypeProxy);
		}
		catch (FinderException e) {
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
	}

	public Collection getAllGroupsContainingUser(User user) throws EJBException {
		return this.getListOfAllGroupsContaining(user.getGroupID());
	}

	/**
	 * Adds the group by id groupToAdd under this group
	 * 
	 * @see com.idega.user.data.Group#addGroup(Group)
	 */
	public void addGroup(Group groupToAdd) throws EJBException {
		this.addGroup(this.getGroupIDFromGroup(groupToAdd));
	}

	/**
	 * Adds the user by id under this group and changes his deleted status to false if needed. Also sets his primary group to this group if it is not
	 * set
	 * 
	 * @see com.idega.user.data.Group#addGroup(User)
	 */
	public void addGroup(User userToAdd) throws EJBException {

		this.addGroup(this.getGroupIDFromGroup(userToAdd));
		boolean needsToStore = false;
		if (userToAdd.getDeleted()) {
			needsToStore = true;
			userToAdd.setDeleted(false);
		}

		if (userToAdd.getPrimaryGroupID() < 0) {
			needsToStore = true;
			userToAdd.setPrimaryGroup(this);
		}

		if (needsToStore) {
			userToAdd.store();
		}
	}

	/**
	 * Adds the group by id groupToAdd under this group
	 * 
	 * @see com.idega.user.data.Group#addGroup(Group)
	 */
	public void addGroup(Group groupToAdd, Timestamp time) throws EJBException {
		this.addGroup(this.getGroupIDFromGroup(groupToAdd), time);
	}

	/**
	 * Adds the group by id groupId under this group
	 * 
	 * @see com.idega.core.data.GenericGroup#addGroup(int,time)
	 */
	public void addGroup(int groupId, Timestamp time) throws EJBException {
		try {
			// GroupRelation rel = this.getGroupRelationHome().create();
			// rel.setGroup(this);
			// rel.setRelatedGroup(groupId);
			// rel.store();
			if (time != null) {
				addUniqueRelation(groupId, RELATION_TYPE_GROUP_PARENT, time);
			}
			else {
				addUniqueRelation(groupId, RELATION_TYPE_GROUP_PARENT);
			}
		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}

	/**
	 * Adds the group by id groupId under this group
	 * 
	 * @see com.idega.core.data.GenericGroup#addGroup(int)
	 */
	public void addGroup(int groupId) throws EJBException {
		addGroup(groupId, null);
	}

	public void addRelation(Group groupToAdd, String relationType) throws CreateException {
		this.addRelation(this.getGroupIDFromGroup(groupToAdd), relationType);
	}

	public void addRelation(Group groupToAdd, GroupRelationType relationType) throws CreateException {
		this.addRelation(this.getGroupIDFromGroup(groupToAdd), relationType);
	}

	public void addRelation(int relatedGroupId, GroupRelationType relationType) throws CreateException {
		this.addRelation(relatedGroupId, relationType.getType());
	}

	public void addRelation(int relatedGroupId, String relationType) throws CreateException {
		// try{
		GroupRelation rel = this.getGroupRelationHome().create();
		rel.setGroup(this);
		rel.setRelatedGroup(relatedGroupId);
		rel.setRelationshipType(relationType);
		rel.setRelatedGroupType(rel.getRelatedGroup().getGroupType());
		rel.store();
		// }
		// catch(Exception e){
		// throw new EJBException(e.getMessage());
		// }
	}

	/**
	 * Only adds a relation if one does not exist already
	 * 
	 * @param relatedGroupId
	 * @param relationType
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public void addUniqueRelation(int relatedGroupId, String relationType, Timestamp time) throws CreateException {
		// try{
		if (!hasRelationTo(relatedGroupId, relationType)) {
			// System.out.println("hasRelationTo("+relatedGroupId+","+relationType+")
			// IS FALSE");
			GroupRelation rel = this.getGroupRelationHome().create();
			rel.setGroup(this);
			rel.setRelatedGroup(relatedGroupId);
			rel.setRelationshipType(relationType);
			if (time == null) {
				time = IWTimestamp.getTimestampRightNow();
			}

			rel.setInitiationDate(time);
			rel.setRelatedGroupType(rel.getRelatedGroup().getGroupType());
			rel.store();
		}
		// }
		// catch(Exception e){
		// throw new EJBException(e.getMessage());
		// }
	}

	public void addUniqueRelation(int relatedGroupId, String relationType) throws CreateException {
		addUniqueRelation(relatedGroupId, relationType, null);
	}

	/**
	 * Only adds a relation if one does not exist already
	 * 
	 * @param relatedGroup
	 * @param relationType
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public void addUniqueRelation(Group relatedGroup, String relationType) throws CreateException {
		addUniqueRelation(((Integer) (relatedGroup.getPrimaryKey())).intValue(), relationType);
	}

	/**
	 * @deprecated use removeRelation(int relatedGroupId, String relationType, User performer)
	 */
	public void removeRelation(Group relatedGroup, String relationType) throws RemoveException {
		int groupId = this.getGroupIDFromGroup(relatedGroup);
		this.removeRelation(groupId, relationType);
	}

	/**
	 * @deprecated use removeRelation(int relatedGroupId, String relationType, User performer)
	 */
	public void removeRelation(int relatedGroupId, String relationType) throws RemoveException {
		removeRelation(relatedGroupId, relationType, null);
	}

	/**
	 * @deprecated use removeRelation(int relatedGroupId, String relationType, User performer)
	 */
	public void removeRelation(Group relatedGroup, String relationType, User performer) throws RemoveException {
		int groupId = this.getGroupIDFromGroup(relatedGroup);
		this.removeRelation(groupId, relationType, performer);
	}

	public void removeRelation(int relatedGroupId, String relationType, User performer) throws RemoveException {
		GroupRelation rel = null;
		try {
			// Group group = this.getGroupHome().findByPrimaryKey(relatedGroupId);
			Collection rels;
			rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relatedGroupId, relationType);
			Iterator iter = rels.iterator();
			while (iter.hasNext()) {
				rel = (GroupRelation) iter.next();
				if (performer == null) {
					rel.remove();
				}
				else {
					rel.removeBy(performer);
				}
			}
		}
		catch (FinderException e) {
			throw new RemoveException(e.getMessage());
		}
	}

	/**
	 * Returns a collection of Group objects that are related by the relation type relationType with this Group
	 */
	public Collection getRelatedBy(GroupRelationType relationType) throws FinderException {
		return getRelatedBy(relationType.getType());
	}

	/**
	 * Returns a collection of Group objects that are related by the relation type relationType with this Group
	 */
	public Collection getRelatedBy(String relationType) throws FinderException {
		GroupRelation rel = null;
		Collection theReturn = new ArrayList();
		Collection rels = null;
		rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relationType);
		Iterator iter = rels.iterator();
		while (iter.hasNext()) {
			rel = (GroupRelation) iter.next();
			Group g = rel.getRelatedGroup();
			theReturn.add(g);
		}
		return theReturn;
	}

	/**
	 * Returns a collection of Group objects that are reverse related by the relation type relationType with this Group
	 */
	public Collection getReverseRelatedBy(String relationType) throws FinderException {
		GroupRelation rel = null;
		Collection theReturn = new ArrayList();
		Collection rels = null;
		rels = this.getGroupRelationHome().findGroupsRelationshipsByRelatedGroup(this.getID(), relationType);
		Iterator iter = rels.iterator();
		while (iter.hasNext()) {
			rel = (GroupRelation) iter.next();
			Group g = rel.getGroup();
			theReturn.add(g);
		}
		return theReturn;
	}

	public void removeUser(User user, User currentUser) throws RemoveException {
		// former: user.getGroupId() but this method is deprecated therefore:
		// user.getId()
		try {
			this.removeGroup(user.getID(), currentUser, false);
		}
		catch (EJBException e) {
			throw new RemoveException(e.getMessage());
		}
	}

	public void removeUser(User user, User currentUser, Timestamp time) throws RemoveException {
		// former: user.getGroupId() but this method is deprecated therefore:
		// user.getId()
		try {
			this.removeGroup(user.getID(), currentUser, false, time);

		}
		catch (EJBException e) {
			throw new RemoveException(e.getMessage());
		}

	}

	/**
	 * This finder returns a collection of all groups of the grouptype(s) that are defined in the groupTypes parameter It also returns the groups that
	 * are defined as topnodes in the ic_domain_group_relation table It excludes groups that have been deleted and don't have any active relations to
	 * parent groups If returnSpecifiedGroupTypes is set as false then it excludes the grouptype(s) defined in the groupTypes paremeter excluding user
	 * representative groups
	 * 
	 * @return all groups of certain type(s) that have not been deleted
	 * @throws FinderException
	 */
	public Collection ejbFindAllGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws FinderException {
		Table groupTable = new Table(this, "g");
		Column idCol = new Column(groupTable, getColumnNameGroupID());

		Table groupRelSubTable = new Table(GroupRelationBMPBean.TABLE_NAME, "gr");
		Column relatedGroupIDSubCol = new Column(groupRelSubTable, GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN);
		// Column relationshipTypeSubCol = new Column(groupRelSubTable,
		// GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN);
		Column groupRelationstatusSubCol = new Column(groupRelSubTable, GroupRelationBMPBean.STATUS_COLUMN);

		SelectQuery firstSubQuery = new SelectQuery(groupRelSubTable);
		firstSubQuery.addColumn(relatedGroupIDSubCol);
		// subQuery.addCriteria(new MatchCriteria(relationshipTypeSubCol,
		// MatchCriteria.EQUALS, RELATION_TYPE_GROUP_PARENT));
		firstSubQuery.addCriteria(new InCriteria(groupRelationstatusSubCol, new String[] { GroupRelation.STATUS_ACTIVE, GroupRelation.STATUS_PASSIVE_PENDING }));

		Table groupDomainRelSubTable = new Table(GroupDomainRelationBMPBean.TABLE_NAME, "gdr");
		Column gdr_RelatedGroupIDSubCol = new Column(groupDomainRelSubTable, GroupDomainRelationBMPBean.RELATED_GROUP_ID_COLUMN);
		Column gdr_relationshipTypeSubCol = new Column(groupDomainRelSubTable, GroupDomainRelationBMPBean.RELATIONSHIP_TYPE_COLUMN);
		Column gdr_groupRelationstatusSubCol = new Column(groupDomainRelSubTable, GroupDomainRelationBMPBean.STATUS_COLUMN);

		SelectQuery secondSubQuery = new SelectQuery(groupDomainRelSubTable);
		secondSubQuery.addColumn(gdr_RelatedGroupIDSubCol);
		secondSubQuery.addCriteria(new MatchCriteria(gdr_relationshipTypeSubCol, MatchCriteria.EQUALS, GroupDomainRelationTypeBMPBean.RELATION_TYPE_TOP_NODE));
		secondSubQuery.addCriteria(new MatchCriteria(gdr_groupRelationstatusSubCol, MatchCriteria.IS, MatchCriteria.NULL));

		InCriteria firstInCriteria = new InCriteria(idCol, firstSubQuery);
		InCriteria secondInCriteria = new InCriteria(idCol, secondSubQuery);

		SelectQuery query = new SelectQuery(groupTable);
		query.addColumn(new WildCardColumn(groupTable));
		if (groupTypes != null && groupTypes.length != 0) {
			Column typeCol = new Column(groupTable, getGroupTypeColumnName());
			if (groupTypes.length == 1) {
				query.addCriteria(new MatchCriteria(typeCol, returnSpecifiedGroupTypes ? MatchCriteria.EQUALS : MatchCriteria.NOTEQUALS, groupTypes[0]));
			}
			else {
				query.addCriteria(new InCriteria(typeCol, groupTypes, !returnSpecifiedGroupTypes));
			}
		}
		query.addCriteria(new OR(firstInCriteria, secondInCriteria));
		query.addOrder(groupTable, getNameColumnName(), true);
		return this.idoFindPKsByQuery(query);
	}

	// public Collection ejbFindAllGroups(String[] groupTypes, boolean
	// returnSepcifiedGroupTypes) throws FinderException {
	// if (groupTypes != null && groupTypes.length > 0) {
	// String typeList =
	// IDOUtil.getInstance().convertArrayToCommaseparatedString(groupTypes, true);
	// return super.idoFindIDsBySQL("select * from " + getEntityName() + " where "
	// + getGroupTypeColumnName() + ((returnSepcifiedGroupTypes) ? " in (" : " not
	// in (") + typeList + ") order by " + getNameColumnName());
	// }
	// return super.idoFindAllIDsOrderedBySQL(getNameColumnName());
	// }
	/**
	 * 
	 * @return all groups excluding user representative groups
	 * @throws FinderException
	 */
	public Collection ejbFindAll() throws FinderException {
		// String[] types = {this.getGroupTypeValue()};
		// return ejbFindAllGroups(types,true);
		String theUserType = "ic_user_representative";

		SelectQuery query = idoSelectQuery();
		query.addCriteria(new MatchCriteria(idoQueryTable(), getGroupTypeColumnName(), MatchCriteria.NOTEQUALS, theUserType, true));
		int prefetchSize = 10000;
		return super.idoFindPKsByQueryUsingLoadBalance(query, prefetchSize);
	}

	/**
	 * @deprecated replaced with ejbFindAllGroups
	 */
	public static List getAllGroupsOld(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException {
		/*
		 * String typeList = ""; if (groupTypes != null && groupTypes.length > 0){ for(int g = 0; g < groupTypes.length; g++){ if(g>0){ typeList += ", "; }
		 * typeList += "'"+groupTypes[g]+"'"; } Group gr = (Group)com.idega.user.data.GroupBMPBean.getStaticInstance(); return
		 * EntityFinder.findAll(gr,"select * from "+gr.getEntityName()+" where
		 * "+com.idega.user.data.GroupBMPBean.getGroupTypeColumnName()+((returnSepcifiedGroupTypes)?" in (":" not in (")+typeList+") order by
		 * "+com.idega.user.data.GroupBMPBean.getNameColumnName()); } return
		 * EntityFinder.findAllOrdered(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getNameColumnName());
		 */
		return null;
	}

	protected boolean identicalGroupExistsInDatabase() throws Exception {
		// return SimpleQuerier.executeStringQuery("select * from
		// "+this.getEntityName()+" where "+this.getGroupTypeColumnName()+" =
		// '"+this.getGroupType()+"' and "+this.getNameColumnName()+" =
		// '"+this.getName()+"'",this.getDatasource()).length > 0;
		return false;
	}

	public void insert() throws SQLException {
		try {
			// if(!this.getName().equals("")){
			if (identicalGroupExistsInDatabase()) {
				throw new SQLException("group with same name and type already in database");
			}
			// }
			super.insert();
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				throw (SQLException) ex;
			}
			else {
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				throw new SQLException(ex.getMessage());
			}
		}
	}

	// public boolean equals(IDOLegacyEntity entity){
	// if(entity != null){
	// if(entity instanceof Group){
	// return this.equals((Group)entity);
	// } else {
	// return super.equals(entity);
	// }
	// }
	// return false;
	// }
	protected boolean equals(Group group) {
		if (group != null) {
			try {
				if (group.getPrimaryKey().equals(this.getPrimaryKey())) {
					return true;
				}
			}
			catch (Exception e) {
				return false;
			}
			return false;
		}
		return false;
	}

	private GroupHome getGroupHome() {
		return ((GroupHome) this.getEJBLocalHome());
	}

	private GroupDomainRelationHome getGroupDomainRelationHome() {
		try {
			return ((GroupDomainRelationHome) IDOLookup.getHome(GroupDomainRelation.class));
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	private GroupDomainRelationTypeHome getGroupDomainRelationTypeHome() {
		try {
			return ((GroupDomainRelationTypeHome) IDOLookup.getHome(GroupDomainRelationType.class));
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String ejbHomeGetGroupType() {
		return this.getGroupTypeValue();
	}

	public String ejbHomeGetRelationTypeGroupParent() {
		return RELATION_TYPE_GROUP_PARENT;
	}

	public Collection ejbFindGroups(String[] groupIDs) throws FinderException {
		Collection toReturn = new ArrayList(0);
		String sGroupList = "";
		/*
		 * if (groupIDs != null && groupIDs.length > 0){ for(int g = 0; g < groupIDs.length; g++){ if(g>0){ sGroupList += ", "; } sGroupList +=
		 * groupIDs[g]; } }
		 */
		sGroupList = IDOUtil.getInstance().convertArrayToCommaseparatedString(groupIDs);
		if (!sGroupList.equals("")) {
			String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIDColumnName() + " in (" + sGroupList + ")";
			toReturn = super.idoFindPKsBySQL(sql);
		}
		return toReturn;
	}

	public Collection ejbFindGroupsByType(String type) throws FinderException {
		StringBuffer sql = new StringBuffer("select ").append(getIDColumnName()).append(" from ");
		sql.append(getEntityName());
		sql.append(" where ");
		sql.append(COLUMN_GROUP_TYPE);
		sql.append(" = '");
		sql.append(type);
		sql.append("'");

		return super.idoFindPKsBySQL(sql.toString());
	}

	public Collection ejbFindGroupsByMetaData(String key, String value) throws FinderException {
		return super.idoFindPKsByMetaData(key, value);
	}

	public Integer ejbFindSystemUsersGroup() throws FinderException {
		return new Integer(GroupBMPBean.GROUP_ID_USERS);
	}

	private GroupTypeHome getGroupTypeHome() {
		try {
			return ((GroupTypeHome) IDOLookup.getHome(GroupType.class));
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection ejbFindGroupsRelationshipsByRelatedGroup(int groupID, String relationType, String orRelationType) throws FinderException {
		String firstRelationTypeClause = GroupRelationBMPBean.getRelationTypeWhereClause(relationType);
		String secondRelationTypeClause = GroupRelationBMPBean.getRelationTypeWhereClause(orRelationType);
		String sql = "select * from " + GroupRelationBMPBean.TABLE_NAME + " where " + GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN + "=" + groupID + " and (" + firstRelationTypeClause + " OR " + secondRelationTypeClause + ") and ( " + GroupRelationBMPBean.STATUS_COLUMN + "='" + GroupRelation.STATUS_ACTIVE + "' OR " + GroupRelationBMPBean.STATUS_COLUMN + "='" + GroupRelation.STATUS_PASSIVE_PENDING + "' ) ";
		return this.idoFindPKsBySQL(sql);
	}

	public Collection ejbFindParentGroups(int groupID) throws FinderException {
		String sql = "select " + getIDColumnName() + " from " + GroupRelationBMPBean.TABLE_NAME + " where " + GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN + "=" + groupID + " and (" + GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN + "='GROUP_PARENT' OR " + GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN + " is null) and ( " + GroupRelationBMPBean.STATUS_COLUMN + "='" + GroupRelation.STATUS_ACTIVE + "' OR " + GroupRelationBMPBean.STATUS_COLUMN + "='" + GroupRelation.STATUS_PASSIVE_PENDING + "' ) ";
		return this.idoFindPKsBySQL(sql);
	}

	private UserHome getUserHome() {

		try {
			return (UserHome) IDOLookup.getHome(User.class);
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List getUserGroupTypeList() {
		if (userGroupTypeSingletonList == null) {
			userGroupTypeSingletonList = new ArrayList();
			userGroupTypeSingletonList.add(getUserHome().getGroupType());
		}

		return userGroupTypeSingletonList;
	}

	/**
	 * Method hasRelationTo.
	 * 
	 * @param group
	 * @return boolean
	 * @throws RemoteException
	 */
	public boolean hasRelationTo(Group group) {

		return hasRelationTo(((Integer) group.getPrimaryKey()).intValue());

	}

	/**
	 * This is bidirectional
	 */
	public boolean hasRelationTo(int groupId) {
		int myId = ((Integer) this.getPrimaryKey()).intValue();
		Collection relations = new ArrayList();
		try {
			relations = this.getGroupRelationHome().findGroupsRelationshipsContainingBiDirectional(myId, groupId);
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		return !relations.isEmpty();
	}

	/**
	 * This is bidirectional
	 */
	public boolean hasRelationTo(int groupId, String relationType) {
		int myId = ((Integer) this.getPrimaryKey()).intValue();
		Collection relations = new ArrayList();
		try {
			relations = this.getGroupRelationHome().findGroupsRelationshipsContainingBiDirectional(myId, groupId, relationType);
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		return !relations.isEmpty();
	}

	public Iterator getChildrenIterator() {
		Iterator it = null;
		Collection children = getChildren();
		if (children != null) {
			it = children.iterator();
		}
		return it;
	}

	public Collection getChildren() {
		/**
		 * @todo: Change implementation this first part may not be needed. (Eiki,gummi)
		 * 
		 */
		if (this.getID() == GroupBMPBean.GROUP_ID_USERS) {
			// String[] groupTypes = {"ic_user_representative"};
			try {
				String[] groupTypes = new String[1];
				groupTypes[0] = ((GroupHome) IDOLookup.getHome(User.class)).getGroupType();
				return this.getGroupHome().findGroups(groupTypes);
			}
			catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		else {
			return getChildGroups(); // only returns groups not users
		}
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public ICTreeNode getChildAtIndex(int childIndex) {
		try {
			return ((GroupHome) this.getEJBLocalHome()).findByPrimaryKey(new Integer(childIndex));
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public int getChildCount() {
		if (this.getID() == GroupBMPBean.GROUP_ID_USERS) {
			try {
				String[] groupTypes = new String[1];
				groupTypes[0] = ((GroupHome) IDOLookup.getHome(User.class)).getGroupType();
				return this.getGroupHome().findGroups(groupTypes).size();
			}
			catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		else {
			try {
				// Collection types =
				// this.getGroupTypeHome().findVisibleGroupTypes();//TODO optimize or
				// cache
				return this.getGroupHome().getNumberOfVisibleGroupsContained(this);
			}
			catch (FinderException e) {
				throw new EJBException(e);
			}
			catch (IDOException idoex) {
				throw new EJBException(idoex);
			}
		}
	}

	public int getIndex(ICTreeNode node) {
		return node.getNodeID();
	}

	/**
	 * @todo reimplement
	 */
	public ICTreeNode getParentNode() {
		ICTreeNode parent = null;
		try {
			parent = (ICTreeNode) this.getParentGroups().iterator().next();
		}
		catch (Exception e) {
		}
		return parent;
	}

	public boolean isLeaf() {
		/**
		 * @todo reimplement
		 */
		return getChildCount() > 0;
	}

	public String getNodeName() {
		return this.getName();
	}

	public String getNodeName(Locale locale) {
		return this.getNodeName();
	}

	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

	public int getNodeID() {
		return ((Integer) this.getPrimaryKey()).intValue();
	}

	public int getSiblingCount() {
		ICTreeNode parent = getParentNode();
		if (parent != null) {
			return parent.getChildCount();
		}
		else {
			return 0;
		}
	}

	/**
	 * @see com.idega.core.ICTreeNode#getNodeType()
	 */
	public int getNodeType() {
		return -1;
	}

	public void store() {
		super.store();
	}

	/**
	 * Gets if the group is of type "UserGroupRepresentative"
	 */
	public boolean isUser() {
		return UserBMPBean.USER_GROUP_TYPE.equals(this.getGroupType());
	}

	public void addAddress(Address address) throws IDOAddRelationshipException {
		this.idoAddTo(address);
	}

	public Collection getAddresses(AddressType addressType) throws IDOLookupException, IDOCompositePrimaryKeyException, IDORelationshipException {
		String addressTypePrimaryKeyColumn = addressType.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();

		IDOEntityDefinition addressDefinition = IDOLookup.getEntityDefinitionForClass(Address.class);
		String addressTableName = addressDefinition.getSQLTableName();
		String addressPrimaryKeyColumn = addressDefinition.getPrimaryKeyDefinition().getField().getSQLFieldName();
		String groupAddressMiddleTableName = addressDefinition.getMiddleTableNameForRelation(getEntityName());

		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(addressPrimaryKeyColumn).appendFrom().append(addressTableName).append(" a, ");
		query.append(groupAddressMiddleTableName).append(" iua ").appendWhere();

		query.append("a.").append(addressPrimaryKeyColumn).appendEqualSign();
		query.append("iua.").append(addressPrimaryKeyColumn);

		query.appendAnd().append("a.");
		query.append(addressTypePrimaryKeyColumn).appendEqualSign();
		query.append(addressType.getPrimaryKey());

		query.appendAnd().append("iua.");
		query.append(COLUMN_GROUP_ID).appendEqualSign().append(getPrimaryKey());

		return idoGetRelatedEntitiesBySQL(Address.class, query.toString());
	}

	public Collection getPhones() {
		try {
			return super.idoGetRelatedEntities(Phone.class);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getPhones() : " + e.getMessage());
		}
	}

	public Collection getPhones(String phoneTypeID) {
		try {
			return super.idoGetRelatedEntities(Phone.class, PhoneBMPBean.getColumnNamePhoneTypeId(), phoneTypeID);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getPhones() : " + e.getMessage());
		}
	}

	public Collection getEmails() {
		try {
			return super.idoGetRelatedEntities(Email.class);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getEmails() : " + e.getMessage());
		}
	}

	public void addEmail(Email email) throws IDOAddRelationshipException {
		this.idoAddTo(email);
	}

	public void addPhone(Phone phone) throws IDOAddRelationshipException {
		this.idoAddTo(phone);
	}

	public void removeGroup(Group entityToRemoveFrom, User currentUser) throws EJBException {
		int groupId = this.getGroupIDFromGroup(entityToRemoveFrom);
		if ((groupId == -1) || (groupId == 0)) {
			this.removeGroup(groupId, currentUser, true);
		}
		else {
			// just removing this particular one
			this.removeGroup(groupId, currentUser, false);
		}
	}

	protected int getGroupIDFromGroup(Group group) {
		Integer groupID = ((Integer) group.getPrimaryKey());
		if (groupID != null) {
			return groupID.intValue();
		}
		else {
			return -1;
		}
	}

	public void removeGroup(User currentUser) throws EJBException {
		this.removeGroup(-1, currentUser, true);
	}

	public void removeGroup(int relatedGroupId, User currentUser, boolean AllEntries, Timestamp time) throws EJBException {
		try {
			Collection rels = null;
			if (AllEntries) {
				rels = this.getGroupRelationHome().findGroupsRelationshipsUnder(this);
			}
			else {
				rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relatedGroupId);
			}
			Iterator iter = rels.iterator();
			while (iter.hasNext()) {
				GroupRelation item = (GroupRelation) iter.next();
				item.removeBy(currentUser, time);
			}
		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}

	public void removeGroup(int relatedGroupId, User currentUser, boolean AllEntries) throws EJBException {
		removeGroup(relatedGroupId, currentUser, AllEntries, IWTimestamp.getTimestampRightNow());
	}

	protected GroupRelationHome getGroupRelationHome() {
		try {
			return ((GroupRelationHome) IDOLookup.getHome(GroupRelation.class));
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object ejbFindByHomePageID(int pageID) throws FinderException {
		return idoFindOnePKByQuery(idoQueryGetSelect().appendWhereEquals(getColumnNameHomePageID(), pageID));
	}

	public Integer ejbFindGroupByUniqueId(String uniqueIdString) throws FinderException {
		return (Integer) idoFindOnePKByUniqueId(uniqueIdString);
	}

	public Integer ejbFindBoardGroupByClubIDAndLeagueID(Integer clubID, Integer leagueID) throws FinderException {
		String sql = "select m.metadata_value as ic_group_id from ic_group_relation rel, ic_group div, ic_group_ic_metadata mg, ic_metadata m, ic_group_ic_metadata mg2, ic_metadata m2 " + "where rel.IC_GROUP_ID = " + clubID + "and  rel.RELATIONSHIP_TYPE='GROUP_PARENT' " + "and ( rel.GROUP_RELATION_STATUS='ST_ACTIVE' or rel.GROUP_RELATION_STATUS='PASS_PEND' ) " + "and  div.IC_GROUP_ID=rel.related_IC_GROUP_ID " + "and div.group_type='iwme_club_division' " + "and div.ic_group_id=mg.ic_group_id " + "and mg.ic_metadata_id=m.ic_metadata_id " + "and m.metadata_name='CLUBDIV_BOARD' " + "and div.ic_group_id=mg2.ic_group_id " + "and mg2.ic_metadata_id=m2.ic_metadata_id " + "and m2.metadata_name='CLUBDIV_CONN' " + "and m2.metadata_value in ('" + leagueID + "')";
		return (Integer) this.idoFindOnePKBySQL(sql);
	}

	public SelectQuery getSelectQueryConstraints() {
		return null;
	}

	public String getId() {
		return getPrimaryKey().toString();
	}

} // Class Group
