package com.idega.user.data;
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
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressType;
import com.idega.core.net.data.ICNetwork;
import com.idega.core.net.data.ICProtocol;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORuntimeException;
import com.idega.data.IDOUtil;
import com.idega.data.MetaDataCapable;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001-2003 idega software
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>,
 * @version 1.0
 */
public class GroupBMPBean extends com.idega.core.data.GenericGroupBMPBean implements Group, ICTreeNode, MetaDataCapable {
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

	private List userRepresentativeGroupTypeList;
	
	public final void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getNameColumnName(), "Group name", true, true, "java.lang.String");
		addAttribute(getGroupTypeColumnName(), "Group type", true, true, String.class, 30, "one-to-many", GroupType.class);
		addAttribute(getGroupDescriptionColumnName(), "Description", true, true, "java.lang.String");
		addAttribute(getExtraInfoColumnName(), "Extra information", true, true, "java.lang.String");
		addAttribute(COLUMN_CREATED, "Created when", Timestamp.class);
		addAttribute(getColumnNameHomePageID(), "Home page ID", true, true, Integer.class, "many-to-one", ICPage.class);
		setNullable(getColumnNameHomePageID(), true);
		addAttribute(COLUMN_SHORT_NAME, "Short name", true, true, String.class);
		addAttribute(COLUMN_ABBREVATION, "Abbrevation", true, true, String.class);

		//adds a unique id string column to this entity that is set when the entity is first stored.
		addUniqueIDColumn();
		
		this.addManyToManyRelationShip(ICNetwork.class, "ic_group_network");
		this.addManyToManyRelationShip(ICProtocol.class, "ic_group_protocol");
		this.addManyToManyRelationShip(Phone.class, SQL_RELATION_PHONE);
		this.addManyToManyRelationShip(Email.class, SQL_RELATION_EMAIL);
		this.addManyToManyRelationShip(Address.class, SQL_RELATION_ADDRESS);
		addMetaDataRelationship();
		//can have extra info in the ic_metadata table
		
//		id of the group that has the permissions for this group. If this is not null then this group has inherited permissions.
		addManyToOneRelationship(COLUMN_PERMISSION_CONTROLLING_GROUP, Group.class);
		addAttribute(COLUMN_IS_PERMISSION_CONTROLLING_GROUP, "Do children of this group get same permissions", true, true, Boolean.class);
		
		addManyToOneRelationship(COLUMN_ALIAS_TO_GROUP, Group.class);
		setNullable(COLUMN_ALIAS_TO_GROUP, true);
		
		addManyToOneRelationship(COLUMN_HOME_FOLDER_ID,ICFile.class);

		//indexes
		addIndex("IDX_IC_GROUP_1", new String[]{ COLUMN_GROUP_TYPE, COLUMN_GROUP_ID});
		addIndex("IDX_IC_GROUP_2", COLUMN_NAME);
		addIndex("IDX_IC_GROUP_3", COLUMN_GROUP_ID);
		addIndex("IDX_IC_GROUP_4", COLUMN_GROUP_TYPE);
		addIndex("IDX_IC_GROUP_5", new String[]{ COLUMN_GROUP_ID, COLUMN_GROUP_TYPE});
	}

	public final String getEntityName() {
		return ENTITY_NAME;
	}
	//        public String getNameOfMiddleTable(IDOLegacyEntity entity1,IDOLegacyEntity entity2){
	//            return "ic_group_user";
	//        }
	public void setDefaultValues() {
		setGroupType(getGroupTypeValue());
		setCreated(IWTimestamp.getTimestampRightNow());
	}
	//  public void insertStartData(){
	//    try {
	//      GroupTypeHome tghome = (GroupTypeHome)IDOLookup.getHome(GroupType.class);
	//      try{
	//        GroupType type = tghome.findByPrimaryKey(getGroupTypeKey());
	//      } catch(FinderException e){
	//        e.printStackTrace();
	//        try {
	//          GroupType type = tghome.create();
	//          type.setType(getGroupTypeKey());
	//          type.setDescription(getGroupTypeDescription());
	//          type.store();
	//        }
	//        catch (Exception ex) {
	//          ex.printStackTrace();
	//        }
	//      }
	//    }
	//    catch (RemoteException rmi){
	//      throw new EJBException(rmi);
	//    }
	//  }
	//  protected boolean doInsertInCreate(){
	//    return true;
	//  }
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
	/*  ColumNames begin   */
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
	/*  ColumNames end   */
	/*  functions begin   */
	public String getName() {
		return (String)getColumnValue(getNameColumnName());
	}
	public void setName(String name) {
		setColumn(getNameColumnName(), name);
	}
	public String getGroupType() {
		//try {
		//was going to the database everytime!! I kill you tryggvi/gummi (eiki)

		//return (String) ((GroupType) getColumnValue(getGroupTypeColumnName())).getPrimaryKey();
		return getStringColumnValue(getGroupTypeColumnName());

		//} catch (RemoteException ex) {
		//	throw new EJBException(ex);
		//}
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
		return (Group)getColumnValue(COLUMN_ALIAS_TO_GROUP);
	}
	
	/**
	 * This only returns true if the group is of the type alias and the id is bigger than 0
	 */
	public boolean isAlias() {
	    return ("alias".equals(getGroupType()) &&  ( getAliasID()>0 ));
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
		return (Group)getColumnValue(COLUMN_PERMISSION_CONTROLLING_GROUP);
	}

	public void setShortName(String shortName) {
		setColumn(COLUMN_SHORT_NAME, shortName);
	}

	public void setAbbrevation(String abbr) {
		setColumn(COLUMN_ABBREVATION, abbr);
	}

	public String getDescription() {
		return (String)getColumnValue(getGroupDescriptionColumnName());
	}
	public void setDescription(String description) {
		setColumn(getGroupDescriptionColumnName(), description);
	}
	public String getExtraInfo() {
		return (String)getColumnValue(getExtraInfoColumnName());
	}
	public void setExtraInfo(String extraInfo) {
		setColumn(getExtraInfoColumnName(), extraInfo);
	}
	public Timestamp getCreated() {
		return ((Timestamp)getColumnValue(COLUMN_CREATED));
	}
	public void setCreated(Timestamp created) {
		setColumn(this.COLUMN_CREATED, created);
	}
	public int getHomePageID() {
		return getIntColumnValue(getColumnNameHomePageID());
	}
	public ICPage getHomePage() {
		return (ICPage)getColumnValue(getColumnNameHomePageID());
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
		return (ICFile)getColumnValue(COLUMN_HOME_FOLDER_ID);
	}
	public void setHomeFolderID(int fileID) {
		setColumn(COLUMN_HOME_FOLDER_ID, fileID);
	}
	public void setHomeFolderID(Integer fileID) {
		setColumn(COLUMN_HOME_FOLDER_ID, fileID);
	}
	public void setHomeFolder(ICFile file) {
		setHomeFolderID((Integer)file.getPrimaryKey());
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
  
  public void setHomePageURL(String homePage)  {
    setMetaData(META_DATA_HOME_PAGE, homePage );
  }
  
  public void setIsPermissionControllingGroup(boolean isControlling){
  	setColumn(COLUMN_IS_PERMISSION_CONTROLLING_GROUP,isControlling);
  }
  
  public boolean isPermissionControllingGroup(){
  	return getBooleanColumnValue(COLUMN_IS_PERMISSION_CONTROLLING_GROUP,false);
  }

	/**
	 * Gets a list of all the groups that this "group" is directly member of.
	 * @see com.idega.user.data.Group#getListOfAllGroupsContainingThis()
	 */
	public List getParentGroups() throws EJBException {
  		return getParentGroups(null, null);
	}

	/**
	 * Optimized version of getParentGroups() by Sigtryggur 22.06.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	public List getParentGroups(Map cachedParents, Map cachedGroups) throws EJBException {
		List theReturn = new ArrayList();
		try {
			Collection relations = null;
			if (cachedParents!=null) {
				if (cachedParents.containsKey(this.getPrimaryKey()))
					relations = (Collection)cachedParents.get(this.getPrimaryKey());
				else
				{	
					relations = getChildGroupRelationships();
					cachedParents.put(this.getPrimaryKey(), relations);
				}
			}
			else 
				relations = getChildGroupRelationships();
			Iterator iter = relations.iterator();
			while (iter.hasNext()) {
				GroupRelation item = (GroupRelation)iter.next();
				//Group parent = item.getRelatedGroup();
				
				Group parent = null;
				if (cachedGroups!=null) {
					if (cachedGroups.containsKey(new Integer(item.getGroupID())))
						parent = (Group) cachedGroups.get(new Integer(item.getGroupID()));
					else {	
						parent = item.getGroup();
						cachedGroups.put(new Integer(item.getGroupID()), parent);
					}
				}
				else 
					parent = item.getGroup();
				
				if (!theReturn.contains(parent)) {
					theReturn.add(parent);
				}
			}
			if (isUser()) {
				User user = getUserForGroup();
				Group parent = user.getPrimaryGroup();
				if (!theReturn.contains(parent)) {
					theReturn.add(parent);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
		return theReturn;
	}
	/**
	 * Returns the User instance representing the Group if the Group is of type UserGroupRepresentative
	 **/
	private User getUserForGroup() {
		try {
			UserHome uHome = (UserHome)IDOLookup.getHome(User.class);
			return uHome.findUserForUserGroup(this);
		}
		catch (Exception e) {
			throw new IDORuntimeException(e, this);
		}
	}

	/**
	 * Finds all the GroupRelations that point to groups that "this" group is a direct parent for
	 * @return Collection of GroupRelations
	 */
	protected Collection getParentalGroupRelationships() throws FinderException {
		//null type is included as relation type for backwards compatability.
		return this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), RELATION_TYPE_GROUP_PARENT, null);
	}

	/**
	 * Finds all the GroupRelations that point to groups that "this" group is a direct child of
	 * @return Collection of GroupRelation
	 */
	protected Collection getChildGroupRelationships() throws FinderException {
		//null type is included as relation type for backwards compatability.
		return this.getGroupRelationHome().findGroupsRelationshipsByRelatedGroup(this.getID(), RELATION_TYPE_GROUP_PARENT, null);
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
		return (Integer)this.ejbCreate();
	}
	public void ejbPostCreateGroup() throws CreateException {
	}
	public Object ejbFindByName(String name) throws FinderException {
		return this.idoFindOnePKBySQL("select * from " + this.getEntityName() + " where " + this.getNameColumnName() + " = '" + name + "'");
	}
	public Integer ejbFindGroupByPrimaryKey(Object pk) throws FinderException {
		return (Integer)this.ejbFindByPrimaryKey(pk);
	}
	//        private List getListOfAllGroupsContainingLegacy(int group_id)throws EJBException{
	//          String tableToSelectFrom = "IC_GROUP_TREE";
	//          StringBuffer buffer=new StringBuffer();
	//          buffer.append("select * from ");
	//          buffer.append(tableToSelectFrom);
	//          buffer.append(" where ");
	//          buffer.append("CHILD_IC_GROUP_ID");
	//          buffer.append("=");
	//          buffer.append(group_id);
	//          String SQLString=buffer.toString();
	//          Connection conn= null;
	//          Statement Stmt= null;
	//          Vector vector = new Vector();
	//          try
	//          {
	//            conn = getConnection(getDatasource());
	//            Stmt = conn.createStatement();
	//            ResultSet RS = Stmt.executeQuery(SQLString);
	//            while (RS.next()){
	//              IDOLegacyEntity tempobj=null;
	//              try{
	//                tempobj = (IDOLegacyEntity)Class.forName(this.getClass().getName()).newInstance();
	//                tempobj.findByPrimaryKey(RS.getInt(this.getIDColumnName()));
	//              }
	//              catch(Exception ex){
	//                System.err.println("There was an error in " + this.getClass().getName() +".getAllGroupsContainingThis(): "+ex.getMessage());
	//              }
	//              vector.addElement(tempobj);
	//            }
	//
	//            RS.close();
	//
	//          }
	//          catch(Exception e){
	//            throw new EJBException(e.getMessage());
	//          }
	//          finally{
	//            if(Stmt != null){
	//              try{
	//                Stmt.close();
	//              }
	//              catch(SQLException e){}
	//            }
	//            if (conn != null){
	//                freeConnection(getDatasource(),conn);
	//            }
	//          }
	//
	//          if (vector != null){
	//            vector.trimToSize();
	//            return vector;
	//            //return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
	//          }
	//          else{
	//            return null;
	//          }
	//        }
	//
	//??
	//        public Group[] getAllGroupsContained()throws SQLException{
	//
	//          List vector = this.getListOfAllGroupsContained();
	//
	//          if(vector != null){
	//
	//            return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
	//
	//          }else{
	//
	//            return new Group[0];
	//
	//          }
	//
	//        }

	/**
	 * @return A list of groups (not users) under this group
	 */
	public List getChildGroups() throws EJBException {
		List theReturn = new ArrayList();

		try {
			return ListUtil.convertCollectionToList(getGroupHome().findGroupsContained(this, getUserGroupTypeList(), false));
		}
		catch (FinderException e) {
			e.printStackTrace();
			return theReturn;
		}

	}

	/**
	 * Gets the children of the containingGroup
	 * @param containingGroup
	 * @param groupTypes
	 * @param returnTypes
	 * @return
	 * @throws FinderException
	 */
	public Collection ejbFindGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes) throws FinderException {

		String findGroupRelationsSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer)containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);

		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(getEntityName());
		if (groupTypes != null && !groupTypes.isEmpty()) {
			query.appendWhere(COLUMN_GROUP_TYPE);

			IDOQuery subQuery = idoQuery();
			subQuery.appendCommaDelimitedWithinSingleQuotes(groupTypes);
			if (returnTypes) {
				query.appendIn(subQuery);
			}
			else {
				query.appendNotIn(subQuery);
			}
			query.appendAnd();
		}
		else {
			query.appendWhere();
		}
		query.append(this.COLUMN_GROUP_ID);
		query.appendIn(findGroupRelationsSQL);
		query.appendOrderBy(this.COLUMN_NAME);


		
		return idoFindPKsByQueryUsingLoadBalance(query, PREFETCH_SIZE);
		//return idoFindPKsBySQL(query.toString());
	}

	public int ejbHomeGetNumberOfGroupsContained(Group containingGroup, Collection groupTypes, boolean returnTypes) throws FinderException, IDOException {
		String relatedSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer)containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);

		if (groupTypes != null && !groupTypes.isEmpty()) {
			IDOQuery query = idoQuery();
			query.appendSelectCountFrom(this.getEntityName());
			query.appendWhere(this.COLUMN_GROUP_TYPE);
			IDOQuery subQuery = idoQuery();
			subQuery.appendCommaDelimitedWithinSingleQuotes(groupTypes);
			if (returnTypes) {
				query.appendIn(subQuery);
			}
			else {
				query.appendNotIn(subQuery);
			}
			query.appendAnd();
			query.append(this.COLUMN_GROUP_ID);
			query.appendIn(relatedSQL);
			return this.idoGetNumberOfRecords(query.toString());
		}
		else {
			System.err.println("ejbHomeGetNumberOfGroupsContained :NO GROUP TYPES SUPPLIED!");
			return 0;
		}
	}

	public int ejbHomeGetNumberOfVisibleGroupsContained(Group containingGroup) throws FinderException, IDOException {
		String relatedSQL = getGroupRelationHome().getFindRelatedGroupIdsInGroupRelationshipsContainingSQL(((Integer)containingGroup.getPrimaryKey()).intValue(), RELATION_TYPE_GROUP_PARENT);
		String visibleGroupTypes = getGroupTypeHome().getVisibleGroupTypesSQLString();

		IDOQuery query = idoQuery();
		query.appendSelectCountFrom(this.getEntityName());
		query.appendWhere(this.COLUMN_GROUP_TYPE);
		query.appendIn(visibleGroupTypes);
		query.appendAnd();
		query.append(this.COLUMN_GROUP_ID);
		query.appendIn(relatedSQL);
		return this.idoGetNumberOfRecords(query.toString());

	}

	public Collection ejbFindTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes) throws FinderException {
		String relationsSQL = this.getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer)containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());

		if (groupTypes != null && !groupTypes.isEmpty()) {
			IDOQuery query = idoQuery();
			query.appendSelectAllFrom(this.getEntityName());
			query.appendWhere(this.COLUMN_GROUP_TYPE);
			IDOQuery subQuery = idoQuery();
			subQuery.appendCommaDelimitedWithinSingleQuotes(groupTypes);
			if (returnTypes) {
				query.appendIn(subQuery);
			}
			else {
				query.appendNotIn(subQuery);
			}
			query.appendAnd();
			query.append(this.COLUMN_GROUP_ID);
			query.appendIn(relationsSQL);
			query.appendOrderBy(this.COLUMN_NAME);
			//      System.out.println("[GroupBMPBean](ejbFindGroupsContained): "+query.toString());
			return this.idoFindPKsBySQL(query.toString());
		}
		else {
			return ListUtil.getEmptyList();
		}
	}
	public int ejbHomeGetNumberOfTopNodeGroupsContained(ICDomain containingDomain, Collection groupTypes, boolean returnTypes) throws FinderException, IDOException {
		String relationsSQL = getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer)containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());

		if (groupTypes != null && !groupTypes.isEmpty()) {
			IDOQuery query = idoQuery();
			query.appendSelectCountFrom(this.getEntityName());
			query.appendWhere(this.COLUMN_GROUP_TYPE);
			IDOQuery subQuery = idoQuery();
			subQuery.appendCommaDelimitedWithinSingleQuotes(groupTypes);
			if (returnTypes) {
				query.appendIn(subQuery);
			}
			else {
				query.appendNotIn(subQuery);
			}
			query.appendAnd();
			query.append(this.COLUMN_GROUP_ID);
			query.appendIn(relationsSQL);
			//      System.out.println("[GroupBMPBean](ejbHomeGetNumberOfGroupsContained): "+query.toString());
			return this.idoGetNumberOfRecords(query.toString());
		}
		else {
			System.err.println("ejbHomeGetNumberOfTopNodeGroupsContained :NO GROUP TYPES SUPPLIED!");
			return 0;
		}
	}

	public int ejbHomeGetNumberOfTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException, IDOException {
		String relatedSQL = getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer)containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());
		String visibleGroupTypes = getGroupTypeHome().getVisibleGroupTypesSQLString();

		IDOQuery query = idoQuery();
		query.appendSelectCountFrom(this.getEntityName());
		query.appendWhere(this.COLUMN_GROUP_TYPE);
		query.appendIn(visibleGroupTypes);
		query.appendAnd();
		query.append(this.COLUMN_GROUP_ID);
		query.appendIn(relatedSQL);
		return this.idoGetNumberOfRecords(query.toString());

	}

	public Collection ejbFindTopNodeVisibleGroupsContained(ICDomain containingDomain) throws FinderException {
		String relationsSQL = this.getGroupDomainRelationHome().getFindRelatedGroupIdsInGroupDomainRelationshipsContainingSQL(((Integer)containingDomain.getPrimaryKey()).intValue(), getGroupDomainRelationTypeHome().getTopNodeRelationTypeString());
		String visibleGroupTypes = getGroupTypeHome().getVisibleGroupTypesSQLString();

		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this.getEntityName());
		query.appendWhere(this.COLUMN_GROUP_TYPE);
		query.appendIn(visibleGroupTypes);
		query.appendAnd();
		query.append(this.COLUMN_GROUP_ID);
		query.appendIn(relationsSQL);
		query.appendOrderBy(this.COLUMN_NAME);

		return this.idoFindPKsBySQL(query.toString());
	}
	/**
	 * @todo change name to getGroupsContained();
	 */
	//        private List getListOfAllGroupsContainedLegacy()throws EJBException{
	//          String tableToSelectFrom = "IC_GROUP_TREE";
	//          StringBuffer buffer=new StringBuffer();
	//          buffer.append("select CHILD_IC_GROUP_ID from ");
	//          buffer.append(tableToSelectFrom);
	//          buffer.append(" where ");
	//          buffer.append("IC_GROUP_ID");
	//          buffer.append("=");
	//          buffer.append(this.getID());
	//          String SQLString=buffer.toString();
	//		Connection conn= null;
	//		Statement Stmt= null;
	//		Vector vector = new Vector();
	//		try
	//		{
	//			conn = getConnection(getDatasource());
	//			Stmt = conn.createStatement();
	//			ResultSet RS = Stmt.executeQuery(SQLString);
	//			while (RS.next()){
	//
	//				IDOLegacyEntity tempobj=null;
	//				try{
	//					tempobj = (IDOLegacyEntity)Class.forName(this.getClass().getName()).newInstance();
	//					tempobj.findByPrimaryKey(RS.getInt("CHILD_IC_GROUP_ID"));
	//				}
	//				catch(Exception ex){
	//					System.err.println("There was an error in " + this.getClass().getName() +".getAllGroupsContainingThis(): "+ex.getMessage());
	//
	//				}
	//
	//				vector.addElement(tempobj);
	//
	//			}
	//			RS.close();
	//
	//		}
	//                catch(Exception e){
	//                  throw new EJBException(e.getMessage());
	//                }
	//		finally{
	//			if(Stmt != null){
	//                          try{
	//				Stmt.close();
	//			  }
	//                          catch(SQLException sqle){}
	//                        }
	//			if (conn != null){
	//				freeConnection(getDatasource(),conn);
	//			}
	//		}
	//
	//		if (vector != null){
	//			vector.trimToSize();
	//                        return vector;
	//			//return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
	//		}
	//		else{
	//			return null;
	//		}
	//
	//
	//          //return (Group[])this.findReverseRelated(this);
	//
	//        }
	/**
	
	 * @todo change implementation: let the database handle the filtering
	
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
	public Collection getAllGroupsContainingUser(User user) throws EJBException {
		return this.getListOfAllGroupsContaining(user.getGroupID());
	}
	/**
	 * Adds the group by id groupToAdd under this group 
	 * @see com.idega.user.data.Group#addGroup(Group)
	 */
	public void addGroup(Group groupToAdd) throws EJBException {
		this.addGroup(this.getGroupIDFromGroup(groupToAdd));
	}
	
	/**
	 * Adds the group by id groupToAdd under this group 
	 * @see com.idega.user.data.Group#addGroup(Group)
	 */
	public void addGroup(Group groupToAdd, Timestamp time) throws EJBException {
		this.addGroup(this.getGroupIDFromGroup(groupToAdd), time);
	}
	
	/**
	 * Adds the group by id groupId under this group 
	 * @see com.idega.core.data.GenericGroup#addGroup(int,time)
	 */
	public void addGroup(int groupId,Timestamp time) throws EJBException {
		try {
			//GroupRelation rel = this.getGroupRelationHome().create();
			//rel.setGroup(this);
			//rel.setRelatedGroup(groupId);
			//rel.store();
			addUniqueRelation(groupId, RELATION_TYPE_GROUP_PARENT,time);

		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}
	
	/**
	 * Adds the group by id groupId under this group 
	 * @see com.idega.core.data.GenericGroup#addGroup(int)
	 */
	public void addGroup(int groupId) throws EJBException {
		try {
			//GroupRelation rel = this.getGroupRelationHome().create();
			//rel.setGroup(this);
			//rel.setRelatedGroup(groupId);
			//rel.store();
			addUniqueRelation(groupId, RELATION_TYPE_GROUP_PARENT);

		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
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
		//try{
		GroupRelation rel = this.getGroupRelationHome().create();
		rel.setGroup(this);
		rel.setRelatedGroup(relatedGroupId);
		rel.setRelationshipType(relationType);
		rel.store();
		//}
		//catch(Exception e){
		//  throw new EJBException(e.getMessage());
		//}
	}
	/**
	 * Only adds a relation if one does not exist already
	 * @param relatedGroupId
	 * @param relationType
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public void addUniqueRelation(int relatedGroupId, String relationType, Timestamp time) throws CreateException {
		//try{
		if (!hasRelationTo(relatedGroupId, relationType)) {
			//System.out.println("hasRelationTo("+relatedGroupId+","+relationType+") IS FALSE");
			GroupRelation rel = this.getGroupRelationHome().create();
			rel.setGroup(this);
			rel.setRelatedGroup(relatedGroupId);
			rel.setRelationshipType(relationType);
			if(time != null){
				rel.setInitiationDate(time);			
			}
			rel.store();
		}
		//}
		//catch(Exception e){
		//  throw new EJBException(e.getMessage());
		//}
	}

	public void addUniqueRelation(int relatedGroupId, String relationType) throws CreateException {
		addUniqueRelation(relatedGroupId, relationType, null);
	}


	/**
		 * Only adds a relation if one does not exist already
		 * @param relatedGroup
		 * @param relationType
		 * @throws CreateException
		 * @throws RemoteException
		 */
	public void addUniqueRelation(Group relatedGroup, String relationType) throws CreateException {
		addUniqueRelation(((Integer) (relatedGroup.getPrimaryKey())).intValue(), relationType);
	}

	public void removeRelation(Group relatedGroup, String relationType) throws RemoveException {
		int groupId = this.getGroupIDFromGroup(relatedGroup);
		this.removeRelation(groupId, relationType);
	}
	public void removeRelation(int relatedGroupId, String relationType) throws RemoveException {
		GroupRelation rel = null;
		try {
			//Group group = this.getGroupHome().findByPrimaryKey(relatedGroupId);
			Collection rels;
			rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(), relatedGroupId, relationType);
			Iterator iter = rels.iterator();
			while (iter.hasNext()) {
				rel = (GroupRelation)iter.next();
				rel.remove();
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
			rel = (GroupRelation)iter.next();
			Group g = rel.getRelatedGroup();
			theReturn.add(g);
		}
		return theReturn;
	}

	/**
	 * Returns a collection of Group objects that are reverse related by the
	 * relation type relationType with this Group
	 */
	public Collection getReverseRelatedBy(String relationType) throws FinderException {
		GroupRelation rel = null;
		Collection theReturn = new ArrayList();
		Collection rels = null;
		rels = this.getGroupRelationHome().findGroupsRelationshipsByRelatedGroup(this.getID(), relationType);
		Iterator iter = rels.iterator();
		while (iter.hasNext()) {
			rel = (GroupRelation)iter.next();
			Group g = rel.getGroup();
			theReturn.add(g);
		}
		return theReturn;
	}

	//        private void addGroupLegacy(int groupId)throws EJBException{
	//          Connection conn= null;
	//          Statement Stmt= null;
	//          try{
	//            conn = getConnection(getDatasource());
	//            Stmt = conn.createStatement();
	//            String sql = "insert into IC_GROUP_TREE ("+getIDColumnName()+", CHILD_IC_GROUP_ID) values("+getID()+","+groupId+")";
	//            //System.err.println(sql);
	//            int i = Stmt.executeUpdate(sql);
	//            //System.err.println(sql);
	//          }catch (Exception ex) {
	//            ex.printStackTrace(System.out);
	//          }finally{
	//            if(Stmt != null){
	//              try{
	//                Stmt.close();
	//              }
	//              catch(SQLException sqle){}
	//            }
	//            if (conn != null){
	//              freeConnection(getDatasource(),conn);
	//            }
	//          }
	//        }

	//        private void removeGroupLegacy(int groupId, boolean AllEntries)throws EJBException{
	//          Connection conn= null;
	//          Statement Stmt= null;
	//          try{
	//            conn = getConnection(getDatasource());
	//            Stmt = conn.createStatement();
	//            String qry;
	//            if(AllEntries)//removing all in middle table
	//              qry = "delete from IC_GROUP_TREE where "+this.getIDColumnName()+"='"+this.getID()+"' OR CHILD_IC_GROUP_ID ='"+this.getID()+"'";
	//            else// just removing this particular one
	//              qry = "delete from IC_GROUP_TREE where "+this.getIDColumnName()+"='"+this.getID()+"' AND CHILD_IC_GROUP_ID ='"+groupId+"'";
	//            int i = Stmt.executeUpdate(qry);
	//          }catch (Exception ex) {
	//            ex.printStackTrace(System.out);
	//          }finally{
	//            if(Stmt != null){
	//              try{
	//                Stmt.close();
	//              }
	//              catch(SQLException sqle){}
	//            }
	//            if (conn != null){
	//              freeConnection(getDatasource(),conn);
	//            }
	//          }
	//        }
	//
	//        /**
	//         * @deprecated moved to UserGroupBusiness
	//         */
	//        public static void addUserOld(int groupId, User user){
	//          //((com.idega.user.data.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).findByPrimaryKeyLegacy(groupId).addGroup(user.getGroupID());
	//          throw new java.lang.UnsupportedOperationException("Method adduser moved to UserBusiness");
	//        }
	//  public void addUser(User user)throws RemoteException{
	//    this.addGroup(user.getGroupID());
	//  }
	public void removeUser(User user, User currentUser) {
		// former: user.getGroupId() but this method is deprecated therefore: user.getId()
		this.removeGroup(user.getID(), currentUser, false);
	}
	
	public void removeUser(User user, User currentUser, Timestamp time) {
		// former: user.getGroupId() but this method is deprecated therefore: user.getId()
		this.removeGroup(user.getID(), currentUser, false, time);
	}

	//        public Group findGroup(String groupName) throws SQLException{
	//
	//          List group = EntityFinder.findAllByColumn(com.idega.data.GenericEntity.getStaticInstance(this.getClass().getName()),getNameColumnName(),groupName,getGroupTypeColumnName(),this.getGroupTypeValue());
	//
	//          if(group != null){
	//
	//            return (Group)group.get(0);
	//
	//          }else{
	//
	//            return null;
	//
	//          }
	//
	//        }
	public Collection ejbFindAllGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws FinderException {
		if (groupTypes != null && groupTypes.length > 0) {
			String typeList = IDOUtil.getInstance().convertArrayToCommaseparatedString(groupTypes, true);
			return super.idoFindIDsBySQL("select * from " + getEntityName() + " where " + getGroupTypeColumnName() + ((returnSepcifiedGroupTypes) ? " in (" : " not in (") + typeList + ") order by " + getNameColumnName());
		}
		return super.idoFindAllIDsOrderedBySQL(getNameColumnName());
	}
	/**
	 *
	 * @return all groups where getGroupType() is same as this.getGroupTypeValue()
	 * @throws FinderException
	 */
	public Collection ejbFindAll() throws FinderException {
		//          String[] types = {this.getGroupTypeValue()};
		//          return ejbFindAllGroups(types,true);
		return super.idoFindIDsBySQL("select * from " + getEntityName() + " where " + getGroupTypeColumnName() + " like '" + this.getGroupTypeValue() + "' order by " + getNameColumnName());
	}
	/**
	 * @deprecated replaced with ejbFindAllGroups
	 */
	public static List getAllGroupsOld(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException {
		/*String typeList = "";
		if (groupTypes != null && groupTypes.length > 0){
		  for(int g = 0; g < groupTypes.length; g++){
		    if(g>0){ typeList += ", "; }
		    typeList += "'"+groupTypes[g]+"'";
		  }
		  Group gr = (Group)com.idega.user.data.GroupBMPBean.getStaticInstance();
		  return EntityFinder.findAll(gr,"select * from "+gr.getEntityName()+" where "+com.idega.user.data.GroupBMPBean.getGroupTypeColumnName()+((returnSepcifiedGroupTypes)?" in (":" not in (")+typeList+") order by "+com.idega.user.data.GroupBMPBean.getNameColumnName());
		}
		return EntityFinder.findAllOrdered(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getNameColumnName());
		*/
		return null;
	}
	protected boolean identicalGroupExistsInDatabase() throws Exception {
		//    return SimpleQuerier.executeStringQuery("select * from "+this.getEntityName()+" where "+this.getGroupTypeColumnName()+" = '"+this.getGroupType()+"' and "+this.getNameColumnName()+" = '"+this.getName()+"'",this.getDatasource()).length > 0;
		return false;
	}
	public void insert() throws SQLException {
		try {
			//            if(!this.getName().equals("")){
			if (identicalGroupExistsInDatabase()) {
				throw new SQLException("group with same name and type already in database");
			}
			//            }
			super.insert();
		}
		catch (Exception ex) {
			if (ex instanceof SQLException) {
				throw (SQLException)ex;
			}
			else {
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				throw new SQLException(ex.getMessage());
			}
		}
	}
	//        public boolean equals(IDOLegacyEntity entity){
	//          if(entity != null){
	//            if(entity instanceof Group){
	//              return this.equals((Group)entity);
	//            } else {
	//              return super.equals(entity);
	//            }
	//          }
	//          return false;
	//        }
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
		return ((GroupHome)this.getEJBLocalHome());
	}

	private GroupDomainRelationHome getGroupDomainRelationHome() {
		try {
			return ((GroupDomainRelationHome)IDOLookup.getHome(GroupDomainRelation.class));
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	private GroupDomainRelationTypeHome getGroupDomainRelationTypeHome() {
		try {
			return ((GroupDomainRelationTypeHome)IDOLookup.getHome(GroupDomainRelationType.class));
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
		/*    if (groupIDs != null && groupIDs.length > 0){
		      for(int g = 0; g < groupIDs.length; g++){
		        if(g>0){ sGroupList += ", "; }
		        sGroupList += groupIDs[g];
		      }
		    }*/
		sGroupList = IDOUtil.getInstance().convertArrayToCommaseparatedString(groupIDs);
		if (!sGroupList.equals("")) {
			String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIDColumnName() + " in (" + sGroupList + ")";
			toReturn = super.idoFindPKsBySQL(sql);
		}
		return toReturn;
	}

	public Collection ejbFindGroupsByType(String type) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
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
		return new Integer(this.GROUP_ID_USERS);
	}

	private GroupTypeHome getGroupTypeHome() {
		try {
			return ((GroupTypeHome)IDOLookup.getHome(GroupType.class));
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}

	private UserHome getUserHome() {

		try {
			return (UserHome)IDOLookup.getHome(User.class);
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
	 * @param group
	 * @return boolean
	 * @throws RemoteException
	 */
	public boolean hasRelationTo(Group group) {

		return hasRelationTo(((Integer)group.getPrimaryKey()).intValue());

	}
	/**
	 * This is bidirectional
	 */
	public boolean hasRelationTo(int groupId) {
		int myId = ((Integer)this.getPrimaryKey()).intValue();
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
		int myId = ((Integer)this.getPrimaryKey()).intValue();
		Collection relations = new ArrayList();
		try {
			relations = this.getGroupRelationHome().findGroupsRelationshipsContainingBiDirectional(myId, groupId, relationType);
		}
		catch (FinderException ex) {
			ex.printStackTrace();
		}
		return !relations.isEmpty();
	}

	public Iterator getChildren() {
		/**
		 * @todo: Change implementation this first part may not be needed. (Eiki,gummi)
		 *
		 */
		if (this.getID() == this.GROUP_ID_USERS) {
			//      String[] groupTypes = {"ic_user_representative"};
			try {
				String[] groupTypes = new String[1];
				groupTypes[0] = ((GroupHome)IDOLookup.getHome(User.class)).getGroupType();
				return this.getGroupHome().findGroups(groupTypes).iterator();
			}
			catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		else {
			return getChildGroups().iterator(); //only returns groups not users
		}
	}
	public boolean getAllowsChildren() {
		return true;
	}
	public ICTreeNode getChildAtIndex(int childIndex) {
		try {
			return ((GroupHome)this.getEJBLocalHome()).findByPrimaryKey(new Integer(childIndex));
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	public int getChildCount() {
		if (this.getID() == this.GROUP_ID_USERS) {
			try {
				String[] groupTypes = new String[1];
				groupTypes[0] = ((GroupHome)IDOLookup.getHome(User.class)).getGroupType();
				return this.getGroupHome().findGroups(groupTypes).size();
			}
			catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		else {
			try {
				//				Collection types = this.getGroupTypeHome().findVisibleGroupTypes();//TODO optimize or cache
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
			parent = (ICTreeNode)this.getParentGroups().iterator().next();
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
	public String getNodeName(Locale locale, IWApplicationContext iwac){
		return getNodeName(locale);
	}
	public int getNodeID() {
		return ((Integer)this.getPrimaryKey()).intValue();
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
	public int getNodeType(){
		return -1;
	}
	
	public void store() {
		super.store();
	}

	/**
	 * Gets if the group is of type "UserGroupRepresentative"
	 **/
	public boolean isUser() {
		return UserBMPBean.USER_GROUP_TYPE.equals(this.getGroupType());
	}

	public void addAddress(Address address) throws IDOAddRelationshipException {
		this.idoAddTo(address);
	}

	public Collection getAddresses(AddressType addressType) throws IDOLookupException, IDOCompositePrimaryKeyException, IDORelationshipException {
		String addressTypeTableName = addressType.getEntityName();
		String addressTypePrimaryKeyColumn = addressType.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
		
		IDOEntityDefinition addressDefinition = IDOLookup.getEntityDefinitionForClass(Address.class);
		String addressTableName = addressDefinition.getSQLTableName();
		String addressPrimaryKeyColumn = addressDefinition.getPrimaryKeyDefinition().getField().getSQLFieldName();
		String groupAddressMiddleTableName = addressDefinition.getMiddleTableNameForRelation(getEntityName());
		
		IDOQuery query = idoQuery(); 
		query.appendSelect().appendDistinct().append("a.*").appendFrom().append(addressTableName).append(" a, ");
		query.append(groupAddressMiddleTableName).append(" iua, ");
		query.append(addressTypeTableName).append(" iat ").appendWhere();
		
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
		if ((groupId == -1) || (groupId == 0)) //removing all in middle table
			this.removeGroup(groupId, currentUser, true);
		else // just removing this particular one
			this.removeGroup(groupId, currentUser, false);
	}
	protected int getGroupIDFromGroup(Group group) {
		Integer groupID = ((Integer)group.getPrimaryKey());
		if (groupID != null)
			return groupID.intValue();
		else
			return -1;
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
				GroupRelation item = (GroupRelation)iter.next();
				item.removeBy(currentUser,time);
			}
		}
		catch (Exception e) {
			throw new EJBException(e.getMessage());
		}
	}
	
	public void removeGroup(int relatedGroupId, User currentUser, boolean AllEntries) throws EJBException {
		removeGroup(relatedGroupId,currentUser,AllEntries,IWTimestamp.getTimestampRightNow());
	}
	
	protected GroupRelationHome getGroupRelationHome() {
		try {
			return ((GroupRelationHome)IDOLookup.getHome(GroupRelation.class));
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object ejbFindByHomePageID(int pageID) throws FinderException {
		return idoFindOnePKByQuery(idoQueryGetSelect().appendWhereEquals(getColumnNameHomePageID(),pageID));
	}

	public Integer ejbFindGroupByUniqueId(String uniqueIdString) throws FinderException {
		return (Integer) idoFindOnePKByUniqueId(uniqueIdString);
	}
	
} // Class Group
