package com.idega.user.data;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORuntimeException;
import com.idega.data.IDOStoreException;
import com.idega.data.query.SelectQuery;
import com.idega.idegaweb.IWApplicationContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>,<a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class AbstractGroupBMPBean extends GenericEntity implements Group {

	private static final long serialVersionUID = -4190055194376271683L;

	protected Group _group;

	protected static final String GENERAL_GROUP_COLUMN_GROUP_ID = GroupBMPBean.COLUMN_GROUP_ID;
	protected static final String GENERAL_GROUP_COLUMN_NAME = GroupBMPBean.COLUMN_NAME;
	protected static final String GENERAL_GROUP_COLUMN_GROUP_TYPE = GroupBMPBean.COLUMN_GROUP_TYPE;
	protected static final String GENERAL_GROUP_COLUMN_DESCRIPTION = GroupBMPBean.COLUMN_DESCRIPTION;
	protected static final String GENERAL_GROUP_COLUMN_EXTRA_INFO = GroupBMPBean.COLUMN_EXTRA_INFO;
	protected static final String GENERAL_GROUP_COLUMN_CREATED = GroupBMPBean.COLUMN_CREATED;
	protected static final String GENERAL_GROUP_COLUMN_HOME_PAGE_ID = GroupBMPBean.COLUMN_HOME_PAGE_ID;
	protected static final String GENERAL_GROUP_COLUMN_ALIAS_TO_GROUP = GroupBMPBean.COLUMN_ALIAS_TO_GROUP;
	protected static final String GENERAL_GROUP_COLUMN_SHORT_NAME = GroupBMPBean.COLUMN_SHORT_NAME;
	protected static final String GENERAL_GROUP_COLUMN_ABBREVATION = GroupBMPBean.COLUMN_ABBREVATION;

	protected static final String SQL_JOINT_VARIABLE_GROUP = "t1";
	protected static final String SQL_JOINT_VARIABLE_RELATED_ABSTRACTGROUP = "t2";

	/**
	 * Returns a unique Key to identify this GroupType
	 */
	@Override
	public abstract String getGroupTypeKey();
	//  public String getGroupTypeKey(){return null;}
	/**
	 * Returns a description for the GroupType
	 */
	@Override
	public abstract String getGroupTypeDescription();
	//  public String getGroupTypeDescription(){return null;}

	/**
	 * Returns the visibility of the GroupType
	 */
	public boolean getGroupTypeVisibility() {
		return true;
	}

	//  public void initializeAttributes() {
	//    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
	//  }
	//  public String getEntityName() {
	//    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
	//    return null;
	//  }

	public void addGeneralGroupRelation() {
		this.addManyToOneRelationship(getIDColumnName(), "Group ID", Group.class);
		this.getAttribute(getIDColumnName()).setAsPrimaryKey(true);
	}

	@Override
	public Object ejbCreate() throws CreateException {
			this._group = this.getGroupHome().create();
			this._group.setGroupType(this.getGroupTypeKey());
			this._group.setName(this.getName() + "");
			this._group.store();
			this.setPrimaryKey(this._group.getPrimaryKey());
			this.setUniqueId(this._group.getUniqueId());

			return super.ejbCreate();
	}

	public String ejbHomeGetGroupType() {
		return this.getGroupTypeKey();
	}

	@Override
	public void setDefaultValues() {
			setGroupType(getGroupTypeKey());
	}

	@Override
	public void insertStartData() {
		try {
			GroupTypeHome tghome = (GroupTypeHome) IDOLookup.getHome(GroupType.class);

			GroupType type = tghome.create();
			type.setType(getGroupTypeKey());
			type.setDescription(getGroupTypeDescription());
			type.setVisibility(getGroupTypeVisibility());
			type.store();

		}
		catch (Exception e) {
			System.err.println(
				"[Error : "
					+ getClass().getName()
					+ "] Registering grouptype '"
					+ getGroupTypeKey()
					+ "'. Errormessage was: "
					+ e.getMessage());
			//e.printStackTrace();
		}
	}

	@Override
	protected boolean doInsertInCreate() {
		return true;
	}
	/*
	  public Object ejbFindByPrimaryKey(Object key)throws FinderException{
	    try{
	      _group = this.getGroupHome().findByPrimaryKey(key);
	      return super.ejbFindByPrimaryKey(key);
	    }
	    catch(RemoteException rme){
	      throw new EJBException(rme.getMessage());
	    }
	  }
	*/
	@Override
	public void store() throws IDOStoreException {
			if (this.getGroupType() == null) { // || getGroupHome().getGroupType().equals(this.getGroupType())
				this.setGroupType(this.getGroupTypeKey());
			}
			getGeneralGroup().store();
			//      System.out.println("User before/st primaryKey = " + this.getPrimaryKey());
			super.store();
			//      System.out.println("User after/st primaryKey = " + this.getPrimaryKey());
	}

	@Override
	public void remove() throws RemoveException{
			super.remove();
			getGeneralGroup().remove();
	}

	protected GroupHome getGroupHome(){
		GroupHome home = null;
		try {
		 home = (GroupHome) com.idega.data.IDOLookup.getHome(Group.class);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}

		return home;
	}

	protected Group getGeneralGroup(){
		if (this._group == null) {
			try {
				this._group = getGroupHome().findByPrimaryKey(this.getPrimaryKey());
			}
			catch (FinderException fe) {
				throw new EJBException(fe.getMessage());
			}
		}
		return this._group;
	}

	//
	//
	//

	@Override
	public String getName() {
		try {
			return getGeneralGroup().getName();
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	@Override
	public void setName(String name) {
		try {
			getGeneralGroup().setName(name);
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	@Override
	public String getShortName() {
		try {
			return getGeneralGroup().getShortName();
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	@Override
	public void setShortName(String shortName) {
		try {
			getGeneralGroup().setShortName(shortName);
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	@Override
	public String getAbbrevation() {
		try {
			return getGeneralGroup().getAbbrevation();
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	@Override
	public void setAbbrevation(String abbr) {
		try {
			getGeneralGroup().setAbbrevation(abbr);
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	@Override
	public String getGroupType(){
		return getGeneralGroup().getGroupType();
	}

	@Override
	public void setGroupType(String type){
		getGeneralGroup().setGroupType(type);
	}

	@Override
	public void setGroupType(GroupType type){
		getGeneralGroup().setGroupType(type);
	}

	@Override
	public String getDescription(){
		return getGeneralGroup().getDescription();
	}

	@Override
	public void setDescription(String description){
		getGeneralGroup().setDescription(description);
	}

	@Override
	public String getExtraInfo(){
		return getGeneralGroup().getExtraInfo();
	}

	@Override
	public void setExtraInfo(String extraInfo){
		getGeneralGroup().setExtraInfo(extraInfo);
	}

	@Override
	public Timestamp getCreated(){
		return getGeneralGroup().getCreated();
	}

	@Override
	public void setCreated(Timestamp created){
		getGeneralGroup().setCreated(created);
	}

	/**
	 * Returns a collection of Group objects that are related by the relation type relationType with this Group
	 */
	@Override
	public Collection getRelatedBy(GroupRelationType relationType) throws FinderException{
		return this.getGeneralGroup().getRelatedBy(relationType);
	}

	/**
	 * Returns a collection of Group objects that are related by the relation type relationType with this Group
	 */
	@Override
	public Collection getRelatedBy(String relationType) throws FinderException{
		return this.getGeneralGroup().getRelatedBy(relationType);
	}

	@Override
	public void addRelation(Group groupToAdd, String relationType) throws CreateException{
		this.getGeneralGroup().addRelation(groupToAdd, relationType);
	}

	@Override
	public Collection getReverseRelatedBy(String relationType) throws FinderException{
		return this.getGeneralGroup().getReverseRelatedBy(relationType);
	}

	@Override
	public void addRelation(Group groupToAdd, GroupRelationType relationType) throws CreateException{
		this.getGeneralGroup().addRelation(groupToAdd, relationType);
	}

	@Override
	public void addRelation(int relatedGroupId, GroupRelationType relationType) throws CreateException{
		this.getGeneralGroup().addRelation(relatedGroupId, relationType);
	}

	@Override
	public void addUniqueRelation(int relatedGroupId, String relationType) throws CreateException{
		this.getGeneralGroup().addUniqueRelation(relatedGroupId, relationType);
	}

	@Override
	public void addUniqueRelation(Group relatedGroup, String relationType) throws CreateException{
		this.getGeneralGroup().addUniqueRelation(relatedGroup, relationType);
	}

	@Override
	public void addRelation(int relatedGroupId, String relationType) throws CreateException{
		this.getGeneralGroup().addRelation(relatedGroupId, relationType);
	}

	@Override
	public void removeRelation(Group relatedGroup, String relationType) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroup, relationType);
	}

	@Override
	public void removeRelation(Group relatedGroup, String relationType, User performer) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroup, relationType, performer);
	}

	@Override
	public void removeRelation(int relatedGroupId, String relationType) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroupId, relationType);
	}

	@Override
	public void removeRelation(int relatedGroupId, String relationType, User performer) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroupId, relationType, performer);
	}

	@Override
	public java.util.List getParentGroups() throws javax.ejb.EJBException{
		return this.getGeneralGroup().getParentGroups();
	}

	@Override
	public void addGroup(com.idega.user.data.Group p0) throws javax.ejb.EJBException{
		this.getGeneralGroup().addGroup(p0);
	}

	@Override
	public void addGroup(com.idega.user.data.User p0) throws javax.ejb.EJBException{
		this.getGeneralGroup().addGroup(p0);
	}

	@Override
	public java.util.List getChildGroups(java.lang.String[] p0, boolean p1) throws javax.ejb.EJBException{
		return this.getGeneralGroup().getChildGroups(p0, p1);
	}

	@Override
	public void addGroup(int p0) throws javax.ejb.EJBException{
		this.getGeneralGroup().addGroup(p0);
	}

	@Override
	public java.util.List getChildGroups() throws javax.ejb.EJBException{
		return this.getGeneralGroup().getChildGroups();
	}

	@Override
	public java.util.Collection getChildGroups(Group returnProxy) throws javax.ejb.EJBException{
		return this.getGeneralGroup().getChildGroups(returnProxy);
	}

	@Override
	public java.util.Collection getAllGroupsContainingUser(com.idega.user.data.User p0)
		throws javax.ejb.EJBException{
		return this.getGeneralGroup().getAllGroupsContainingUser(p0);
	}

	@Override
	public java.lang.String getGroupTypeValue(){
		return this.getGroupTypeKey();
	}

	@Override
	public boolean hasRelationTo(Group group){
		return this.getGeneralGroup().hasRelationTo(group);
	}

	@Override
	public boolean hasRelationTo(int id){
		return this.getGeneralGroup().hasRelationTo(id);
	}

	@Override
	public boolean hasRelationTo(int groupId, String relationType) {
		return this.getGeneralGroup().hasRelationTo(groupId, relationType);
	}

	protected boolean equals(com.idega.user.data.Group group) {
		//System.out.println("AbstractPratyBMPBean in equals(com.idega.user.data.Group p0)");
		if (group instanceof AbstractGroupBMPBean) {
			return this.getGeneralGroup().equals(((AbstractGroupBMPBean) group).getGeneralGroup());
		}
		else {
			try {
				return this.getGeneralGroup().equals(group);
			}
			catch (Exception e) {
				//throw new IDORuntimeException(e);
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		//    System.out.println("AbstractPratyBMPBean in equals(Object obj)");
		if (obj instanceof AbstractGroupBMPBean) {
			return this.equals(((AbstractGroupBMPBean) obj).getGeneralGroup());
		}
		else if (obj instanceof Group) {
			//return super.equals((Group)obj);
			return equals((Group) obj);
		}
		else {
			return super.equals(obj);
		}
	}

	@Override
	public boolean equals(IDOEntity obj) {
		if (obj instanceof AbstractGroupBMPBean) {
			return this.equals(((AbstractGroupBMPBean) obj).getGeneralGroup());
		}
		else if (obj instanceof Group) {
			return super.equals(obj);
		}
		else {
			return super.equals(obj);
		}
	}


	@Override
	public boolean isUser(){
		try{
			return getGeneralGroup().isUser();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);
		}
	}


	@Override
	public void setPermissionControllingGroupID(int id) {
		try{
			getGeneralGroup().setPermissionControllingGroupID(id);
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);
		}
	}

	@Override
	public void setPermissionControllingGroup(Group controllingGroup) {
		try{
			getGeneralGroup().setPermissionControllingGroup(controllingGroup);
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);
		}
	}

	@Override
	public int getPermissionControllingGroupID() {
		try{
			return getGeneralGroup().getPermissionControllingGroupID();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);
		}
	}

	@Override
	public Group getPermissionControllingGroup() {
		try{
			return getGeneralGroup().getPermissionControllingGroup();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);
		}
	}

	@Override
	public void setIsPermissionControllingGroup(boolean isControlling){
		try{
			getGeneralGroup().setIsPermissionControllingGroup(isControlling);
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);
		}
	}

	@Override
	public boolean isPermissionControllingGroup(){
		try{
			return getGeneralGroup().isPermissionControllingGroup();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);
		}
	}


	//
	//
	//

	/**
	 * ICTreeNode implementation begins
	 */

	@Override
	public Iterator getChildrenIterator() {
		return this.getGeneralGroup().getChildrenIterator();
	}

	@Override
	public boolean getAllowsChildren() {
		return this.getGeneralGroup().getAllowsChildren();
	}
	@Override
	public Group getChildAtIndex(int childIndex) {
		return this.getGeneralGroup().getChildAtIndex(childIndex);
	}
	@Override
	public int getChildCount() {
		return this.getGeneralGroup().getChildCount();
	}
	@Override
	public int getIndex(Group node) {
		return this.getGeneralGroup().getIndex(node);
	}
	@Override
	public Group getParentNode() {
		return this.getGeneralGroup().getParentNode();
	}
	@Override
	public boolean isLeaf() {
		return this.getGeneralGroup().isLeaf();
	}
	@Override
	public String getNodeName() {
		return this.getGeneralGroup().getNodeName();
	}

	@Override
	public String getNodeName(Locale locale) {
		return this.getGeneralGroup().getNodeName(locale);
	}
	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return this.getGeneralGroup().getNodeName(locale,iwac);
	}
	@Override
	public int getNodeID() {
		return this.getGeneralGroup().getNodeID();
	}
	@Override
	public int getSiblingCount() {
		return this.getGeneralGroup().getSiblingCount();
	}

	/**
	 * @see com.idega.core.ICTreeNode#getNodeType()
	 */
	@Override
	public int getNodeType(){
		return -1;
	}


	/**
	 * ICTreeNode implementation ends
	 */


	/**
	 * Creates the begining of a query: "select t2.* from #Group.tablename# t1, #this.tablename# t2 WHERE t1.#primarykey#=gr.#primaryKey#"
	 * Grouptable is t1 and this table is t2
	 * To add conditions to this query you should append AND first and then the conditions
	 * @return Returns IDOQuery that contains begining of a joint-query between Group and this entiy
	 * @throws IDOLookupException
	 * @throws IDOCompositPrimaryKeyException
	 */
	protected IDOQuery idoQueryJointGroupQuery() throws IDOLookupException, IDOCompositePrimaryKeyException{
		IDOQuery query = idoQuery();
		query.appendSelect();
		query.append(SQL_JOINT_VARIABLE_RELATED_ABSTRACTGROUP+".*");
		query.appendFrom();

		IDOEntityDefinition groupDefinition = IDOLookup.getEntityDefinitionForClass(Group.class);
		query.append(groupDefinition.getSQLTableName());
		query.append(" "+SQL_JOINT_VARIABLE_GROUP+", ");

		query.append(this.getEntityName());
		query.append(" "+SQL_JOINT_VARIABLE_RELATED_ABSTRACTGROUP+" ");


		query.appendWhere(" "+SQL_JOINT_VARIABLE_GROUP+".");
		query.append(groupDefinition.getPrimaryKeyDefinition().getField().getSQLFieldName());
		query.appendEqualSign();
		query.append(SQL_JOINT_VARIABLE_RELATED_ABSTRACTGROUP+".");
		query.append(this.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName());

		return query;
	}

	@Override
	public SelectQuery getSelectQueryConstraints(){
		return null;
	}

	@Override
	public void addGroup(int groupId, Timestamp time) throws EJBException {
	    getGeneralGroup().addGroup(groupId,time);
	}

	@Override
	public void addUniqueRelation(int relatedGroupId, String relationType, Timestamp time) throws CreateException {
		getGeneralGroup().addUniqueRelation(relatedGroupId, relationType, time);
	}

	@Override
	public void removeGroup(int relatedGroupId, User currentUser, boolean AllEntries, Timestamp time) throws EJBException {
		getGeneralGroup().removeGroup(relatedGroupId,currentUser, AllEntries, time);
	}

	public String getType() {
		return getGroupType();
	}
}