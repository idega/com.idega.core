package com.idega.user.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneBMPBean;
import com.idega.core.data.GenericGroupBMPBean;
import com.idega.core.file.data.ICFile;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressType;
import com.idega.core.net.data.ICNetwork;
import com.idega.core.net.data.ICProtocol;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDOUtil;
import com.idega.data.MetaDataCapable;
import com.idega.data.UniqueIDCapable;
import com.idega.data.query.Column;
import com.idega.data.query.InCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.event.GroupCreatedEvent;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.events.GroupUserRemovedEvent;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * Title: IW Core Description: Copyright: Copyright (c) 2001-2003 idega software
 * Company: idega.is
 *
 * @author <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,
 * @version 1.0
 */
public class GroupBMPBean extends GenericGroupBMPBean implements Group, MetaDataCapable, UniqueIDCapable, GroupNode<Group> {

	private static final long serialVersionUID = -5276962419455614341L;

	private static final int PREFETCH_SIZE = 100;

	public static final String RELATION_TYPE_GROUP_PARENT = "GROUP_PARENT";
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
	static final String COLUMN_GROUP_MODERATOR_ID = "GROUP_MODERATOR_ID";
	public static final String COLUMN_SYSTEM_IMAGE_ID = "SYSTEM_IMAGE_ID";
//	static final String COLUMN_GROUP_PERSONAL_ID = "GROUP_PERSONAL_ID";

	static final String META_DATA_HOME_PAGE = "homepage";

	private static List<String> userGroupTypeSingletonList;

	@Override
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
//		addAttribute(COLUMN_GROUP_PERSONAL_ID, "Personal id", true, true, String.class);

		//addAttribute(COLUMN_GROUP_MODERATOR_ID,"Moderator Id", true,true, Integer.class);

		addManyToOneRelationship(COLUMN_GROUP_MODERATOR_ID, User.class);
		// adds a unique id string column to this entity that is set when the entity
		// is first stored.
		addUniqueIDColumn();

		this.addManyToManyRelationShip(ICNetwork.class, "ic_group_network");
		this.addManyToManyRelationShip(ICProtocol.class, "ic_group_protocol");
		this.addManyToManyRelationShip(Phone.class, SQL_RELATION_PHONE);
		this.addManyToManyRelationShip(Email.class, SQL_RELATION_EMAIL);
		this.addManyToManyRelationShip(Address.class, SQL_RELATION_ADDRESS);
		addMetaDataRelationship();
		// can have extra info in the ic_metadata table

		addOneToOneRelationship(COLUMN_SYSTEM_IMAGE_ID, "Image", com.idega.core.file.data.ICFile.class);
		this.setNullable(COLUMN_SYSTEM_IMAGE_ID, true);


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
	}

	@Override
	public final String getEntityName() {
		return ENTITY_NAME;
	}

	// public String getNameOfMiddleTable(IDOLegacyEntity entity1,IDOLegacyEntity
	// entity2){
	// return "ic_group_user";
	// }
	@Override
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
	@Override
	public String getGroupTypeValue() {
		return getGroupTypeHome().getGeneralGroupTypeString();
	}

	@Override
	public String getGroupTypeKey() {
		return getGroupTypeValue();
	}

	@Override
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
	@Override
	public String getName() {
		return (String) getColumnValue(getNameColumnName());
	}

	@Override
	public void setName(String name) {
		setColumn(getNameColumnName(), name);
	}

	@Override
	public String getType() {
		return getGroupType();
	}
	@Override
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

	@Override
	public GroupType getGroupTypeEntity() {
		return (GroupType) getColumnValue(getGroupTypeColumnName());
	}

	@Override
	public void setGroupType(String groupType) {
		setColumn(getGroupTypeColumnName(), groupType);
	}

	@Override
	public void setGroupType(GroupType groupType) {
		setColumn(getGroupTypeColumnName(), groupType);
	}

	@Override
	public void setAliasID(int id) {
		setColumn(COLUMN_ALIAS_TO_GROUP, id);
	}

	@Override
	public void setAlias(Group alias) {
		setColumn(COLUMN_ALIAS_TO_GROUP, alias);
	}

	@Override
	public int getAliasID() {
		return getIntColumnValue(COLUMN_ALIAS_TO_GROUP);
	}

	@Override
	public Group getAlias() {
		return (Group) getColumnValue(COLUMN_ALIAS_TO_GROUP);
	}

	/**
	 * This only returns true if the group is of the type alias and the id is
	 * bigger than 0
	 */
	@Override
	public boolean isAlias() {
		return ("alias".equals(getGroupType()) && (getAliasID() > 0));
	}

	@Override
	public void setPermissionControllingGroupID(int id) {
		setColumn(COLUMN_PERMISSION_CONTROLLING_GROUP, id);
	}

	@Override
	public void setPermissionControllingGroup(Group controllingGroup) {
		setColumn(COLUMN_PERMISSION_CONTROLLING_GROUP, controllingGroup);
	}

	@Override
	public int getPermissionControllingGroupID() {
		return getIntColumnValue(COLUMN_PERMISSION_CONTROLLING_GROUP);
	}

	@Override
	public Group getPermissionControllingGroup() {
		return (Group) getColumnValue(COLUMN_PERMISSION_CONTROLLING_GROUP);
	}

	@Override
	public void setShortName(String shortName) {
		setColumn(COLUMN_SHORT_NAME, shortName);
	}

	@Override
	public void setAbbrevation(String abbr) {
		setColumn(COLUMN_ABBREVATION, abbr);
	}


//	@Override
//	public void setGroupPersonalId(String groupPersonalId) {
//		setColumn(COLUMN_GROUP_PERSONAL_ID, groupPersonalId);
//	}
//
//	@Override
//	public String getGroupPersonalId() {
//		return (String) getColumnValue(COLUMN_GROUP_PERSONAL_ID);
//	}
//
	@Override
	public String getDescription() {
		return (String) getColumnValue(getGroupDescriptionColumnName());
	}

	@Override
	public void setDescription(String description) {
		setColumn(getGroupDescriptionColumnName(), description);
	}

	@Override
	public String getExtraInfo() {
		return (String) getColumnValue(getExtraInfoColumnName());
	}

	@Override
	public void setExtraInfo(String extraInfo) {
		setColumn(getExtraInfoColumnName(), extraInfo);
	}

	@Override
	public Timestamp getCreated() {
		return ((Timestamp) getColumnValue(COLUMN_CREATED));
	}

	@Override
	public void setCreated(Timestamp created) {
		setColumn(GroupBMPBean.COLUMN_CREATED, created);
	}

	@Override
	public int getHomePageID() {
		return getIntColumnValue(getColumnNameHomePageID());
	}

	@Override
	public ICPage getHomePage() {
		return (ICPage) getColumnValue(getColumnNameHomePageID());
	}

	@Override
	public void setHomePageID(int pageID) {
		setColumn(getColumnNameHomePageID(), pageID);
	}

	@Override
	public void setHomePageID(Integer pageID) {
		setColumn(getColumnNameHomePageID(), pageID);
	}

	@Override
	public void setHomePage(ICPage page) {
		setColumn(getColumnNameHomePageID(), page);
	}

	@Override
	public int getHomeFolderID() {
		return getIntColumnValue(COLUMN_HOME_FOLDER_ID);
	}

	@Override
	public ICFile getHomeFolder() {
		return (ICFile) getColumnValue(COLUMN_HOME_FOLDER_ID);
	}

	@Override
	public void setHomeFolderID(int fileID) {
		setColumn(COLUMN_HOME_FOLDER_ID, fileID);
	}

	@Override
	public void setHomeFolderID(Integer fileID) {
		setColumn(COLUMN_HOME_FOLDER_ID, fileID);
	}

	@Override
	public void setHomeFolder(ICFile file) {
		setHomeFolderID((Integer) file.getPrimaryKey());
	}

	@Override
	public String getShortName() {
		return getStringColumnValue(COLUMN_SHORT_NAME);
	}

	@Override
	public String getAbbrevation() {
		return getStringColumnValue(COLUMN_ABBREVATION);
	}

	@Override
	public String getHomePageURL() {
		return getMetaData(META_DATA_HOME_PAGE);
	}

	@Override
	public void setHomePageURL(String homePage) {
		setMetaData(META_DATA_HOME_PAGE, homePage);
	}

	@Override
	public void setIsPermissionControllingGroup(boolean isControlling) {
		setColumn(COLUMN_IS_PERMISSION_CONTROLLING_GROUP, isControlling);
	}

	@Override
	public boolean isPermissionControllingGroup() {
		return getBooleanColumnValue(COLUMN_IS_PERMISSION_CONTROLLING_GROUP, false);
	}

	@Override
	public int getSystemImageID() {
		return getIntColumnValue(COLUMN_SYSTEM_IMAGE_ID);
	}

	@Override
	public void setSystemImageID(Integer fileID) {
		setColumn(COLUMN_SYSTEM_IMAGE_ID, fileID);
	}

	@Override
	public void setSystemImageID(int fileID) {
		setColumn(COLUMN_SYSTEM_IMAGE_ID, fileID);
	}


	/**
	 * Gets a list of all the groups that this "group" is directly member of.
	 *
	 * @see com.idega.user.data.Group#getListOfAllGroupsContainingThis()
	 */
	@Override
	public List<Group> getParentGroups() throws EJBException {
		return getParentGroups(null, null);
	}

	protected Collection getChildGroupRelationships() throws FinderException {
		// null type is included as relation type for backwards compatability.
		return this.getGroupRelationHome().findGroupsRelationshipsByRelatedGroup(this.getID(), RELATION_TYPE_GROUP_PARENT, null);
	}

	/**
	 * Returns the User instance representing the Group if the Group is of type
	 * UserGroupRepresentative
	 *
	 * @throws IDOLookupException
	 * @throws FinderException
	 */
	private User getUserForGroup() throws IDOLookupException, FinderException {
		UserHome uHome = (UserHome) IDOLookup.getHome(User.class);
		return uHome.findUserForUserGroup(this);
	}

	/**
	 * Finds all the GroupRelations that point to groups that "this" group is a
	 * direct parent for
	 *
	 * @return Collection of GroupRelations
	 */
	protected Collection<GroupRelation> getParentalGroupRelationships() throws FinderException {
		// null type is included as relation type for backwards compatability.
		return this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), RELATION_TYPE_GROUP_PARENT, null);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	protected List<Group> getListOfAllGroupsContaining(int group_id) throws EJBException {
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
	@Override
	public List<Group> getChildGroups() throws EJBException {
		try {
			return ListUtil.convertCollectionToList(getGroupHome().findGroupsContained(this, getUserGroupTypeList(), false));
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
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
	public Collection ejbFindGroupsContainedTemp(Group containingGroup, Collection groupTypes, boolean returnTypes) throws FinderException {
		Table groupTable = new Table(ENTITY_NAME, "g");
		Table groupRelTable = new Table(GroupRelationBMPBean.TABLE_NAME, "gr");
		SelectQuery query = new SelectQuery(groupTable);
		query.addColumn(new WildCardColumn(groupTable));
		query.addJoin(groupTable, COLUMN_GROUP_ID, groupRelTable, GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN);
		if (groupTypes != null && !groupTypes.isEmpty()) {
			if (groupTypes.size() == 1) {
				query.addCriteria(new MatchCriteria(groupTable, COLUMN_GROUP_TYPE, MatchCriteria.NOTEQUALS, groupTypes.iterator().next().toString()));
			}
			else {
				query.addCriteria(new InCriteria(groupTable, COLUMN_GROUP_TYPE, groupTypes, !returnTypes));
			}
		}
		query.addCriteria(new MatchCriteria(groupRelTable, GroupRelationBMPBean.GROUP_ID_COLUMN, MatchCriteria.EQUALS, containingGroup.getPrimaryKey()));
		query.addCriteria(new MatchCriteria(groupRelTable, GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN, MatchCriteria.EQUALS, RELATION_TYPE_GROUP_PARENT));
		String[] statuses = { GroupRelationBMPBean.STATUS_ACTIVE, GroupRelationBMPBean.STATUS_PASSIVE_PENDING };
		query.addCriteria(new InCriteria(groupRelTable, GroupRelationBMPBean.STATUS_COLUMN, statuses));
		query.addOrder(groupTable, COLUMN_NAME, true);
		return idoFindPKsByQueryUsingLoadBalance(query, PREFETCH_SIZE);
	}

	public Collection<Integer> ejbFindGroupsContained(Group containingGroup, Collection<String> groupTypes, boolean returnTypes) throws FinderException {
		String query = "SELECT distinct g." + COLUMN_GROUP_ID + " FROM " + ENTITY_NAME + " g inner join " + GroupRelationBMPBean.TABLE_NAME + " gr on g." + COLUMN_GROUP_ID + " = gr." +
				GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN + " WHERE gr." + COLUMN_GROUP_ID + " = " + containingGroup.getPrimaryKey() + " and gr." + GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN + " = '" +
				RELATION_TYPE_GROUP_PARENT + "' and ( gr." + GroupRelationBMPBean.STATUS_COLUMN + " = '" + GroupRelationBMPBean.STATUS_ACTIVE + "' or gr." + GroupRelationBMPBean.STATUS_COLUMN + " = '" +
				GroupRelationBMPBean.STATUS_PASSIVE_PENDING + "' )";
		if (!ListUtil.isEmpty(groupTypes)) {
			query += " and g.GROUP_TYPE " + (!returnTypes ? "NOT" : CoreConstants.EMPTY) + " IN (";
			for (Iterator<String> typesIter = groupTypes.iterator(); typesIter.hasNext();) {
				query += "'" + typesIter.next() + "'";
				if (typesIter.hasNext()) {
					query+= ", ";
				}
			}
			query += ")";
		}
		return idoFindPKsBySQL(query, PREFETCH_SIZE);
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
	public Collection<Integer> ejbFindGroupsContained(Group containingGroup, Group groupTypeProxy, Class<? extends IDOEntity> theClass) throws FinderException {
		String idColumn = getIDColumnName();
		String select = "g." + COLUMN_GROUP_ID;
		String join = CoreConstants.EMPTY;
		String from = ENTITY_NAME + " g ";
		String order = null;
		if (groupTypeProxy != null && (groupTypeProxy instanceof User || groupTypeProxy.getType().equals(User.USER_GROUP_TYPE))) {
			select = "u." + User.FIELD_USER_ID;
			from = UserBMPBean.SQL_TABLE_NAME + " u ";
			join = " inner join " + ENTITY_NAME + " g on " + select + " = g." + COLUMN_GROUP_ID;
			order = " order by u." + User.FIELD_DISPLAY_NAME + ", u." + User.FIELD_FIRST_NAME;
			theClass = UserBMPBean.class;
			idColumn = User.FIELD_USER_ID;
		}

		String query = "SELECT distinct " + select + " FROM " + from + join + " inner join " + GroupRelationBMPBean.TABLE_NAME + " gr on g." + COLUMN_GROUP_ID + " = gr." +
				GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN + " WHERE gr." + COLUMN_GROUP_ID + " = " + containingGroup.getPrimaryKey() + " and gr." + GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN + " = '" +
				RELATION_TYPE_GROUP_PARENT + "' and ( gr." + GroupRelationBMPBean.STATUS_COLUMN + " = '" + GroupRelationBMPBean.STATUS_ACTIVE + "' or gr." + GroupRelationBMPBean.STATUS_COLUMN + " = '" +
				GroupRelationBMPBean.STATUS_PASSIVE_PENDING + "' ) and g." + COLUMN_GROUP_TYPE + " like '" + groupTypeProxy.getGroupTypeKey() + "' ";
		if (order != null) {
			query += order;
		}

		return idoFindPKsBySQLIgnoringCache(query, -1, -1, null, theClass, idColumn);
	}

	public int ejbHomeGetNumberOfGroupsContained(Group containingGroup, Collection<String> groupTypes, boolean returnTypes) throws FinderException, IDOException {
		try {
			throw new RuntimeException("Containing group: " + containingGroup + ", group type: " + groupTypes + ", return types: " + returnTypes);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Testing stack trace", e);
		}

		IDOQuery query = idoQuery();

		long start = System.currentTimeMillis();
		try {
		String relatedSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer) containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);

		if (groupTypes != null && !groupTypes.isEmpty()) {
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
		} finally {
			getLogger().info("Took time: " + (System.currentTimeMillis() - start) + " ms. Query: " + query);
		}
	}

	public int ejbHomeGetNumberOfVisibleGroupsContained(Group containingGroup) throws FinderException, IDOException {
		String query = "SELECT COUNT(g." + COLUMN_GROUP_ID + ") FROM " + ENTITY_NAME + " g inner join " + GroupRelationBMPBean.TABLE_NAME + " gr on g." + COLUMN_GROUP_ID + " = gr." +
				GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN + " inner join " + GroupTypeBMPBean.TABLE_NAME + " gt on g." + COLUMN_GROUP_TYPE + " = gt." + GroupTypeBMPBean.TYPE_COLUMN +
				" WHERE gr." + COLUMN_GROUP_ID + "=" + containingGroup.getPrimaryKey() + " and gr." + GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN + "='" +
				RELATION_TYPE_GROUP_PARENT + "' and ( gr." + GroupRelationBMPBean.STATUS_COLUMN + "='" + GroupRelationBMPBean.STATUS_ACTIVE + "' or gr." + GroupRelationBMPBean.STATUS_COLUMN + "='" +
				GroupRelationBMPBean.STATUS_PASSIVE_PENDING + "' ) and gt." + GroupTypeBMPBean.COLUMN_IS_VISIBLE + " !='N'";
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
	 *
	 * @todo change implementation: let the database handle the filtering
	 *
	 */
	@Override
	public List<Group> getChildGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		List<Group> theReturn = new ArrayList<Group>();
		List<String> types = null;

		if (groupTypes != null && groupTypes.length > 0) {
			types = ListUtil.convertStringArrayToList(groupTypes);
		}

		try {
			theReturn = ListUtil.convertCollectionToList(getGroupHome().findGroupsContained(this, types, returnSpecifiedGroupTypes));
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return theReturn;
	}

	@Override
	public List<Integer> getChildGroupsIDs(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws EJBException {
		List<Integer> theReturn = new ArrayList<Integer>();

		List<String> types = null;

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
	 * Returns collection of Childs that match the type of 'groupTypeProxy' and
	 * according to groupTypeProxy.getSelectQueryConstrains(). The objects in the
	 * collection will be of the same class as 'groupTypeProxy' i.e. if
	 * groupTypeProxy implements User then it will be collection of User elements
	 */
	@Override
	public Collection<? extends Group> getChildGroups(Group groupTypeProxy) throws EJBException {
		try {
			return getGroupHome().findGroupsContained(this, groupTypeProxy);
		} catch (FinderException e) {
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
	}

	@Override
	public Collection<Group> getAllGroupsContainingUser(User user) throws EJBException {
		return this.getListOfAllGroupsContaining(user.getGroupID());
	}

	/**
	 * Adds the group by id groupToAdd under this group
	 *
	 * @see com.idega.user.data.Group#addGroup(Group)
	 */
	@Override
	public void addGroup(Group groupToAdd) throws EJBException {
		this.addGroup(this.getGroupIDFromGroup(groupToAdd));
	}

	/**
	 * Adds the user by id under this group and changes his deleted status to
	 * false if needed. Also sets his primary group to this group if it is not set
	 *
	 * @see com.idega.user.data.Group#addGroup(User)
	 */
	@Override
	public void addGroup(User userToAdd) throws EJBException {
		addGroup(userToAdd, IWTimestamp.getTimestampRightNow());
	}

	@Override
	public void addGroup(User userToAdd, Timestamp time) throws EJBException {
		addUser(userToAdd, time);
	}

	public Integer addUser(User userToAdd, Timestamp time) throws EJBException {
		Integer userId = Integer.valueOf(this.getGroupIDFromGroup(userToAdd));
		Integer relationId = addGroup(userId, time);

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

		return relationId;
	}

	/**
	 * Adds the group by id groupToAdd under this group
	 *
	 * @see com.idega.user.data.Group#addGroup(Group)
	 */
	@Override
	public void addGroup(Group groupToAdd, Timestamp time) throws EJBException {
		this.addGroup(this.getGroupIDFromGroup(groupToAdd), time);
	}

	/**
	 * Adds the group by id groupId under this group
	 *
	 * @see com.idega.core.data.GenericGroup#addGroup(int,time)
	 */
	@Override
	public void addGroup(int groupId, Timestamp time) throws EJBException {
		addGroup(Integer.valueOf(groupId), time);
	}

	private Integer addGroup(Integer groupId, Timestamp time) throws EJBException {
		try {
			if (time != null) {
				return addUniqueRelation(groupId, RELATION_TYPE_GROUP_PARENT, time);
			}
			else {
				return addUniqueRelation(groupId, RELATION_TYPE_GROUP_PARENT, null);
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
	@Override
	public void addGroup(int groupId) throws EJBException {
		addGroup(groupId, null);
	}

	@Override
	public void addRelation(Group groupToAdd, String relationType) throws CreateException {
		this.addRelation(this.getGroupIDFromGroup(groupToAdd), relationType);
	}

	@Override
	public void addRelation(Group groupToAdd, GroupRelationType relationType) throws CreateException {
		this.addRelation(this.getGroupIDFromGroup(groupToAdd), relationType);
	}

	@Override
	public void addRelation(int relatedGroupId, GroupRelationType relationType) throws CreateException {
		this.addRelation(relatedGroupId, relationType.getType());
	}

	@Override
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
	@Override
	public void addUniqueRelation(int relatedGroupId, String relationType, Timestamp time) throws CreateException {
		addUniqueRelation(Integer.valueOf(relatedGroupId), relationType, time);
	}

	private Integer addUniqueRelation(Integer relatedGroupId, String relationType, Timestamp time) throws CreateException {
		Collection<GroupRelation> relations = getRelations(relatedGroupId, relationType);
		if (!ListUtil.isEmpty(relations)) {
			return (Integer) relations.iterator().next().getPrimaryKey();
		}

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
		return (Integer) rel.getPrimaryKey();
	}

	@Override
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
	@Override
	public void addUniqueRelation(Group relatedGroup, String relationType) throws CreateException {
		addUniqueRelation(((Integer) (relatedGroup.getPrimaryKey())).intValue(), relationType);
	}

	/**
	 * @deprecated use removeRelation(int relatedGroupId, String relationType,
	 *             User performer)
	 */
	@Override
	@Deprecated
	public void removeRelation(Group relatedGroup, String relationType) throws RemoveException {
		int groupId = this.getGroupIDFromGroup(relatedGroup);
		this.removeRelation(groupId, relationType);
	}

	/**
	 * @deprecated use removeRelation(int relatedGroupId, String relationType,
	 *             User performer)
	 */
	@Deprecated
	@Override
	public void removeRelation(int relatedGroupId, String relationType) throws RemoveException {
		removeRelation(relatedGroupId, relationType, null);
	}

	/**
	 * @deprecated use removeRelation(int relatedGroupId, String relationType,
	 *             User performer)
	 */
	@Override
	@Deprecated
	public void removeRelation(Group relatedGroup, String relationType, User performer) throws RemoveException {
		int groupId = this.getGroupIDFromGroup(relatedGroup);
		this.removeRelation(groupId, relationType, performer);
	}

	@Override
	public void removeRelation(int relatedGroupId, String relationType, User performer) throws RemoveException {
		GroupRelation rel = null;
		try {
			// Group group = this.getGroupHome().findByPrimaryKey(relatedGroupId);
			Collection<GroupRelation> rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relatedGroupId, relationType);
			Iterator<GroupRelation> iter = rels.iterator();
			while (iter.hasNext()) {
				rel = iter.next();
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
	 * Returns a collection of Group objects that are related by the relation type
	 * relationType with this Group
	 */
	@Override
	public Collection<Group> getRelatedBy(GroupRelationType relationType) throws FinderException {
		return getRelatedBy(relationType.getType());
	}

	/**
	 * Returns a collection of Group objects that are related by the relation type
	 * relationType with this Group
	 */
	@Override
	public Collection<Group> getRelatedBy(String relationType) throws FinderException {
		GroupRelation rel = null;
		Collection<Group> theReturn = new ArrayList<Group>();
		Collection<GroupRelation> rels = null;
		rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relationType);
		Iterator<GroupRelation> iter = rels.iterator();
		while (iter.hasNext()) {
			rel = iter.next();
			Group g = rel.getRelatedGroup();
			theReturn.add(g);
		}
		return theReturn;
	}
	@Override
	public Collection<Group> getRelated(Collection<String> relationTypes){
		GroupRelation rel = null;
		Collection<Group> theReturn = new ArrayList<Group>();
		Collection<GroupRelation> rels = null;
		rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relationTypes);
		Iterator<GroupRelation> iter = rels.iterator();
		while (iter.hasNext()) {
			rel = iter.next();
			Group g = rel.getRelatedGroup();
			theReturn.add(g);
		}
		return theReturn;
	}

	/**
	 * Returns a collection of Group objects that are reverse related by the
	 * relation type relationType with this Group
	 */
	@Override
	public Collection<Group> getReverseRelatedBy(String relationType) throws FinderException {
		return getReverseRelatedBy(Integer.valueOf(getId()), relationType);
	}

	protected Collection<Group> getReverseRelatedBy(Integer groupId, String relationType) throws FinderException {
		GroupRelation rel = null;
		Collection<Group> theReturn = new ArrayList<Group>();
		Collection<GroupRelation> rels = null;
		rels = this.getGroupRelationHome().findGroupsRelationshipsByRelatedGroup(groupId, relationType);
		Iterator<GroupRelation> iter = rels.iterator();
		while (iter.hasNext()) {
			rel = iter.next();
			Group g = rel.getGroup();
			theReturn.add(g);
		}
		return theReturn;
	}

	@Override
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

	@Override
	public void removeUser(User user, User currentUser, Timestamp time) throws RemoveException {
		try {
			this.removeGroup(user.getID(), currentUser, false, time == null ? IWTimestamp.getTimestampRightNow() : time);

		}
		catch (EJBException e) {
			throw new RemoveException(e.getMessage());
		}

	}

	/**
	 * This finder returns a collection of all groups of the grouptype(s) that are
	 * defined in the groupTypes parameter It also returns the groups that are
	 * defined as topnodes in the ic_domain_group_relation table It excludes
	 * groups that have been deleted and don't have any active relations to parent
	 * groups If returnSpecifiedGroupTypes is set as false then it excludes the
	 * grouptype(s) defined in the groupTypes paremeter excluding user
	 * representative groups
	 *
	 * @return all groups of certain type(s) that have not been deleted
	 * @throws FinderException
	 */
	public Collection<Group> ejbFindAllGroups(String[] groupTypes, boolean returnSpecifiedGroupTypes) throws FinderException {
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
	@Deprecated
	public static List getAllGroupsOld(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException {
		/*
		 * String typeList = ""; if (groupTypes != null && groupTypes.length > 0){
		 * for(int g = 0; g < groupTypes.length; g++){ if(g>0){ typeList += ", "; }
		 * typeList += "'"+groupTypes[g]+"'"; } Group gr =
		 * (Group)com.idega.user.data.GroupBMPBean.getStaticInstance(); return
		 * EntityFinder.findAll(gr,"select * from "+gr.getEntityName()+" where
		 * "+com.idega.user.data.GroupBMPBean.getGroupTypeColumnName()+((returnSepcifiedGroupTypes)?"
		 * in (":" not in (")+typeList+") order by
		 * "+com.idega.user.data.GroupBMPBean.getNameColumnName()); } return
		 * EntityFinder.findAllOrdered(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getNameColumnName());
		 */
		return null;
	}

	@Override
	protected boolean identicalGroupExistsInDatabase() throws Exception {
		// return SimpleQuerier.executeStringQuery("select * from
		// "+this.getEntityName()+" where "+this.getGroupTypeColumnName()+" =
		// '"+this.getGroupType()+"' and "+this.getNameColumnName()+" =
		// '"+this.getName()+"'",this.getDatasource()).length > 0;
		return false;
	}

	@Override
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
		 * if (groupIDs != null && groupIDs.length > 0){ for(int g = 0; g <
		 * groupIDs.length; g++){ if(g>0){ sGroupList += ", "; } sGroupList +=
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
		AccessController instance = IWMainApplication.getDefaultIWMainApplication().getAccessController();
		return new Integer(instance.getUsersGroupID());
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

	/**
	 *
	 * @param groups is {@link Group#getPrimaryKey()}, not <code>null</code>;
	 * @return {@link Collection} of {@link Group#getPrimaryKey()} of parent {@link Group}s
	 * or {@link Collections#emptyList()} on failure;
	 */
	public Collection<Integer> ejbFindParentGroups(Collection<Integer> groups) {
		if (!ListUtil.isEmpty(groups)) {
			String primaryKeys = IDOUtil.getInstance()
					.convertCollectionOfIntegersToCommaseparatedString(groups);

			StringBuilder query = new StringBuilder();
			query.append("SELECT IC_GROUP_ID FROM IC_GROUP_RELATION ");
			query.append("WHERE RELATED_IC_GROUP_ID IN (").append(primaryKeys).append(") ");
			query.append("AND (");
				query.append("RELATIONSHIP_TYPE = 'GROUP_PARENT' ");
				query.append("OR ");
				query.append("RELATIONSHIP_TYPE IS NULL");
			query.append(") AND (");
				query.append("GROUP_RELATION_STATUS = 'ST_ACTIVE' ");
				query.append("OR ");
				query.append("GROUP_RELATION_STATUS = 'PASS_PEND' ");
			query.append(")");

			try {
				return this.idoFindPKsBySQL(query.toString());
			} catch (FinderException e) {
				getLogger().log(Level.WARNING,
						"Failed to get primary keys by query: '" + query.toString() + "'");
			}
		}

		return Collections.emptyList();
	}

	/**
	 *
	 * <p>Searches whole {@link Group} tree to find it all</p>
	 * @param groups is {@link Group#getPrimaryKey()}, not <code>null</code>;
	 * @return {@link Collection} of {@link Group#getPrimaryKey()} of parent {@link Group}s
	 * or {@link Collections#emptyList()} on failure;
	 */
	public Collection<Integer> ejbFindParentGroupsRecursively(Collection<Integer> groups) {
		Set<Integer> parentGroupsTree = new TreeSet<Integer>();

		while (!ListUtil.isEmpty(groups)) {
			parentGroupsTree.addAll(groups);
			groups = ejbFindParentGroups(groups);
		}

		return parentGroupsTree;
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

	private List<String> getUserGroupTypeList() {
		if (userGroupTypeSingletonList == null) {
			userGroupTypeSingletonList = new ArrayList<String>();
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
	@Override
	public boolean hasRelationTo(Group group) {

		return hasRelationTo(((Integer) group.getPrimaryKey()).intValue());

	}

	/**
	 * This is bidirectional
	 */
	@Override
	public boolean hasRelationTo(int groupId) {
		int myId = ((Integer) this.getPrimaryKey()).intValue();
		Collection<GroupRelation> relations = new ArrayList<>();
		try {
			relations = this.getGroupRelationHome().findGroupsRelationshipsContainingBiDirectional(myId, groupId);
		} catch (FinderException ex) {}
		return !ListUtil.isEmpty(relations);
	}

	/**
	 * This is bidirectional
	 */
	@Override
	public boolean hasRelationTo(int groupId, String relationType) {
		Collection<GroupRelation> relations = getRelations(groupId, relationType);
		return !ListUtil.isEmpty(relations);
	}

	private Collection<GroupRelation> getRelations(int groupId, String relationType) {
		int myId = ((Integer) this.getPrimaryKey()).intValue();
		try {
			return this.getGroupRelationHome().findGroupsRelationshipsContainingBiDirectional(myId, groupId, relationType);
		} catch (FinderException ex) {}
		return Collections.emptyList();
	}

	@Override
	public Iterator<Group> getChildrenIterator() {
		Iterator<Group> it = null;
		Collection<Group> children = getChildren();
		if (children != null) {
			it = children.iterator();
		}
		return it;
	}

	@Override
	public Collection<Group> getChildren() {
		AccessController instance = IWMainApplication.getDefaultIWMainApplication().getAccessController();
		if (this.getID() == instance.getUsersGroupID()) {
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

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public Group getChildAtIndex(int childIndex) {
		try {
			return ((GroupHome) this.getEJBLocalHome()).findByPrimaryKey(new Integer(childIndex));
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public int getChildCount() {
		AccessController instance = IWMainApplication.getDefaultIWMainApplication().getAccessController();
		if (this.getID() == instance.getUsersGroupID()) {
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

	@Override
	public int getIndex(Group node) {
		return node.getNodeID();
	}

	/**
	 * @todo reimplement
	 */
	@Override
	public Group getParentNode() {
		Group parent = null;
		try {
			parent = this.getParentGroups().iterator().next();
		}
		catch (Exception e) {
		}
		return parent;
	}

	@Override
	public boolean isLeaf() {
		/**
		 * @todo reimplement
		 */
		return getChildCount() > 0;
	}

	@Override
	public String getNodeName() {
		return this.getName();
	}

	@Override
	public String getNodeName(Locale locale) {
		return this.getNodeName();
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

	@Override
	public int getNodeID() {
		return ((Integer) this.getPrimaryKey()).intValue();
	}

	@Override
	public int getSiblingCount() {
		Group parent = getParentNode();
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
	@Override
	public int getNodeType() {
		return -1;
	}

	@Override
	public void store() {
		String id = getId();
		super.store();
		if (StringUtil.isEmpty(id)) {
			GroupCreatedEvent groupCreatedEvent = new GroupCreatedEvent(this);
			ELUtil.getInstance().publishEvent(groupCreatedEvent);
		}
	}

	/**
	 * Gets if the group is of type "UserGroupRepresentative"
	 */
	@Override
	public boolean isUser() {
		return UserBMPBean.USER_GROUP_TYPE.equals(this.getGroupType());
	}

	@Override
	public void addAddress(Address address) throws IDOAddRelationshipException {
		this.idoAddTo(address);
	}

	@Override
	public Collection getAddresses(AddressType addressType) throws IDOLookupException, IDOCompositePrimaryKeyException, IDORelationshipException {
		IDOEntityDefinition addressDefinition = IDOLookup.getEntityDefinitionForClass(Address.class);
		String addressTableName = addressDefinition.getSQLTableName();
		String addressPrimaryKeyColumn = addressDefinition.getPrimaryKeyDefinition().getField().getSQLFieldName();
		String groupAddressMiddleTableName = addressDefinition.getMiddleTableNameForRelation(getEntityName());

		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(addressPrimaryKeyColumn).appendFrom().append(addressTableName).append(" a, ");
		query.append(groupAddressMiddleTableName).append(" iua ").appendWhere();

		query.append("a.").append(addressPrimaryKeyColumn).appendEqualSign();
		query.append("iua.").append(addressPrimaryKeyColumn);

		if (addressType != null) {
			String addressTypePrimaryKeyColumn = addressType.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
			query.appendAnd().append("a.");
			query.append(addressTypePrimaryKeyColumn).appendEqualSign();
			query.append(addressType.getPrimaryKey());
		}

		query.appendAnd().append("iua.");
		query.append(COLUMN_GROUP_ID).appendEqualSign().append(getPrimaryKey());

		return idoGetRelatedEntitiesBySQL(Address.class, query.toString());
	}

	@Override
	public Collection getPhones() {
		try {
			return super.idoGetRelatedEntities(Phone.class);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getPhones() : " + e.getMessage());
		}
	}

	@Override
	public Collection getPhones(String phoneTypeID) {
		try {
			return super.idoGetRelatedEntities(Phone.class, PhoneBMPBean.getColumnNamePhoneTypeId(), phoneTypeID);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getPhones() : " + e.getMessage());
		}
	}

	@Override
	public Collection getEmails() {
		try {
			return super.idoGetRelatedEntities(Email.class);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getEmails() : " + e.getMessage());
		}
	}

	@Override
	public void addEmail(Email email) throws IDOAddRelationshipException {
		this.idoAddTo(email);
	}

	@Override
	public void addPhone(Phone phone) throws IDOAddRelationshipException {
		this.idoAddTo(phone);
	}

	@Override
	public void removeGroup(Group entityToRemoveFrom, User currentUser) throws EJBException {
		int groupId = this.getGroupIDFromGroup(entityToRemoveFrom);
		removeGroup(groupId, currentUser);
	}

	@Override
	public void removeGroup(Integer groupIdToRemove, User currentUser) throws EJBException {
		int groupId = groupIdToRemove == null ? -1 : groupIdToRemove;
		if (groupId == -1 || groupId == 0) {
			this.removeGroup(groupId, currentUser, true);
		} else {
			this.removeGroup(groupId, currentUser, false);
		}
	}

	protected int getGroupIDFromGroup(Group group) {
		if (group == null || group.getPrimaryKey() == null) {
			return -1;
		}

		Integer groupID = null;
		String primaryKey = group.getPrimaryKey().toString();
		try {
			groupID = Integer.valueOf(primaryKey);
		} catch (NumberFormatException e) {
			getLogger().warning(
					"Failed to convert " + primaryKey +
					" to " + Integer.class.getSimpleName());
		}

		if (groupID != null) {
			return groupID.intValue();
		} else {
			return -1;
		}
	}

	@Override
	public void removeGroup(User currentUser) throws EJBException {
		this.removeGroup(-1, currentUser, true);
	}

	@Override
	public void removeGroup(int relatedGroupId, User currentUser, boolean allEntries, Timestamp time) throws EJBException {
		try {
			Collection<GroupRelation> rels = null;
			if (allEntries) {
				rels = getGroupRelationHome().findGroupsRelationshipsUnder(this);
			} else {
				rels = getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relatedGroupId);
			}

			for (Iterator<GroupRelation> iter = rels.iterator(); iter.hasNext();) {
				iter.next().removeBy(currentUser, time);
			}

			ELUtil.getInstance().publishEvent(
					new GroupUserRemovedEvent(
							(Integer) currentUser.getPrimaryKey(),
							relatedGroupId
					)
			);
		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}

	@Override
	public void removeGroup(int relatedGroupId, User currentUser, boolean allEntries) throws EJBException {
		removeGroup(relatedGroupId, currentUser, allEntries, IWTimestamp.getTimestampRightNow());
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

	@Override
	public SelectQuery getSelectQueryConstraints() {
		return null;
	}

	@Override
	public String getId(){
		Object primaryKey = getPrimaryKey();
		return primaryKey == null ? null : primaryKey.toString();
	}

	public Collection ejbFindAllByNamePhrase(String phrase, Locale locale) throws FinderException {
		IDOQuery query = idoQuery("select ").append(getIDColumnName()).append(" from ").append(getEntityName()).appendWhere().append("lower(");
		query.append(getNameColumnName()).append(")").appendLike().appendSingleQuote().append(CoreConstants.PERCENT).append(phrase.toLowerCase(locale));
		query.append(CoreConstants.PERCENT).appendSingleQuote().appendGroupBy(getIDColumnName());
		return idoFindPKsByQuery(query);
	}

	@Override
	public void setModerator(User moderator){
		setColumn(COLUMN_GROUP_MODERATOR_ID, moderator);
	}

	@Override
	public User getModerator(){
		return (User) getColumnValue(COLUMN_GROUP_MODERATOR_ID);
	}


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
	public Collection<Integer> getMostPopularGroups(Collection<String> types,int amount) throws FinderException{
//		something like this:
//			SELECT r.IC_GROUP_ID, count(*) num FROM ic_group g, IC_GROUP_RELATION r WHERE (g.IC_GROUP_ID = r.IC_GROUP_ID)
//			AND (r.RELATIONSHIP_TYPE = 'GROUP_PARENT') AND (g.GROUP_TYPE IN ('social', 'ic_user_representative')) GROUP BY
//			r.IC_GROUP_ID ORDER BY num DESC

		IDOQuery query = idoQuery("SELECT r.").append(GroupRelationBMPBean.GROUP_ID_COLUMN).append(", count(*) num FROM ")
				.append(ENTITY_NAME).append(" g, ").append(GroupRelationBMPBean.TABLE_NAME).append(" r WHERE (g.")
				.append(COLUMN_GROUP_ID).append(" = ").append("r.").append(GroupRelationBMPBean.GROUP_ID_COLUMN)
				.append(") AND (r.").append(GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN).append(" = 'GROUP_PARENT')");

		if(!ListUtil.isEmpty(types)){
			StringBuilder typeStrings = new StringBuilder("'");
			Iterator <String> iter = types.iterator();
			for(;true;){
				String type = iter.next();
				typeStrings.append(type);
				if(iter.hasNext()){
					typeStrings.append(CoreConstants.JS_STR_PARAM_SEPARATOR);
				}else{
					typeStrings.append("', 'ic_user_representative'");
					break;
				}
			}
			query.append(" AND (g.").append(COLUMN_GROUP_TYPE).append(" IN (").append(typeStrings).append("))");
		}

		query.append(" GROUP BY r.").append(GroupRelationBMPBean.GROUP_ID_COLUMN).append(" ORDER BY num DESC");

		if(amount > 0){
			return this.idoFindPKsByQuery(query, amount);
		}

		return this.idoFindPKsByQuery(query);
	}

	/**
	 * Gets groups and returns them ordered by modification date descendant
	 * @param amount the maximum number of groups that will be returned if less than or equals 0 returns all maches.
	 * @param types group types that will be returned if empty groups of all types will be returned.
	 * @return
	 */
	public Collection<Integer> getGroups(Collection<String> types,int amount) throws FinderException{

		IDOQuery query = idoQuery("SELECT g.").append(COLUMN_GROUP_ID).append(" FROM ").append(GroupBMPBean.ENTITY_NAME).append(" g ");

		if(!ListUtil.isEmpty(types)){
			StringBuilder typeStrings = new StringBuilder("'");
			Iterator <String> iter = types.iterator();
			for(;true;){
				String type = iter.next();
				typeStrings.append(type);
				if(iter.hasNext()){
					typeStrings.append(CoreConstants.JS_STR_PARAM_SEPARATOR);
				}else{
					typeStrings.append(CoreConstants.QOUTE_SINGLE_MARK);
					break;
				}
			}
			query.append(" WHERE (g.").append(COLUMN_GROUP_TYPE).append(" IN (").append(typeStrings).append("))");
		}

		query.append(" ORDER BY g.").append(COLUMN_CREATED).append("  DESC");

		if(amount > 0){
			return this.idoFindPKsByQuery(query, amount);
		}

		return this.idoFindPKsByQuery(query);
	}

	/**Searches by:
	 * 		name,
	 * 		description
	 * @param request the request by which result will be searched
	 * @param amount the maximum number of groups that will be returned if less than or equals 0 returns all maches.
	 * @param types group types that will be returned if empty groups of all types will be returned.
	 * @return
	 */
	public Collection<Group> getGroupsBySearchRequest(String request,
			Collection<String> types, int amount) throws FinderException {

		if(!request.startsWith(CoreConstants.PERCENT)){
			request = CoreConstants.PERCENT + request;
		}
		if(!request.endsWith(CoreConstants.PERCENT)){
			request = request + CoreConstants.PERCENT;
		}

		IDOQuery query = idoQuery("SELECT g.").append(COLUMN_GROUP_ID).append(" FROM ").append(GroupBMPBean.ENTITY_NAME);

		query.append(" g WHERE ").append("((g.").append(COLUMN_NAME).append(" LIKE '").append(request).append("') OR (g.").append(COLUMN_DESCRIPTION)
				.append(" LIKE '").append(request).append("'))");
		if(!ListUtil.isEmpty(types)){
			StringBuilder typeStrings = new StringBuilder("'");
			Iterator <String> iter = types.iterator();
			for(;true;){
				String type = iter.next();
				typeStrings.append(type);
				if(iter.hasNext()){
					typeStrings.append(CoreConstants.JS_STR_PARAM_SEPARATOR);
				}else{
					typeStrings.append(CoreConstants.QOUTE_SINGLE_MARK);
					break;
				}
			}
			query.append(" AND (g.").append(COLUMN_GROUP_TYPE).append(" IN (").append(typeStrings).append("))");
		}
		query.append(" AND (g.").append(COLUMN_GROUP_TYPE).append(" != 'ic_user_representative')");
		query.append(" ORDER BY g.").append(COLUMN_CREATED).append("  DESC");

		if(amount > 0){
			return this.idoFindPKsByQuery(query, amount);
		}

		return this.idoFindPKsByQuery(query);

	}

	/**
	 * Optimized version of getParentGroups() by Sigtryggur 22.06.2004 Database
	 * access is minimized by passing a Map of cached groupParents and Map of
	 * cached groups to the method
	 */
	@Override
	public List<Group> getParentGroups(
			Map<String, Collection<Integer>> cachedParents,
			Map<String, Group> cachedGroups) throws EJBException {
		Collection<Group> groups = getGroupHome().findParentGroups((Integer) getPrimaryKey());
		if (!ListUtil.isEmpty(groups)) {
			return new ArrayList<Group>(groups);
		}

		return Collections.emptyList();
	}

	/**
	 *
	 * @param primaryKeys {@link Collection} of {@link Group#getPrimaryKey()},
	 * not <code>null</code>;
	 * @return {@link Collection} of {@link Group#getPermissionControllingGroupID()}
	 * or {@link Collections#emptyList()} on failure;
	 */
	public Collection<Integer> ejbFindPermissionGroups(
			Collection<Integer> primaryKeys) {
		if (!ListUtil.isEmpty(primaryKeys)) {
			String criteria = IDOUtil.getInstance()
					.convertCollectionOfIntegersToCommaseparatedString(primaryKeys);

			StringBuilder query = new StringBuilder();
			query.append("SELECT icg.PERM_GROUP_ID ");
			query.append("FROM ic_group icg ");
			query.append("WHERE icg.IC_GROUP_ID IN (").append(criteria).append(") ");
			query.append("AND icg.PERM_GROUP_ID IS NOT NULL ");

			try {
				return idoFindPKsBySQL(query.toString());
			} catch (FinderException e) {
				getLogger().log(Level.WARNING,
						"Failed to get primary keys by query: '" + query.toString() + "'");
			}
		}

		return Collections.emptyList();
	}

}