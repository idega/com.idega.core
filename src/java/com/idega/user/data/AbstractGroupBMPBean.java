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

import com.idega.core.data.ICTreeNode;
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
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>,<a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class AbstractGroupBMPBean extends GenericEntity implements Group {

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
	public abstract String getGroupTypeKey();
	//  public String getGroupTypeKey(){return null;}
	/**
	 * Returns a description for the GroupType
	 */
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

	public Object ejbCreate() throws CreateException {
			_group = this.getGroupHome().create();
			_group.setGroupType(this.getGroupTypeKey());
			_group.setName(this.getName() + "");
			_group.store();
			this.setPrimaryKey(_group.getPrimaryKey());

			return super.ejbCreate();
	}

	public String ejbHomeGetGroupType() {
		return this.getGroupTypeKey();
	}

	public void setDefaultValues() {
			setGroupType(getGroupTypeKey());
	}

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
	public void store() throws IDOStoreException {
			if (this.getGroupType() == null) { // || getGroupHome().getGroupType().equals(this.getGroupType())
				this.setGroupType(this.getGroupTypeKey());
			}
			getGeneralGroup().store();
			//      System.out.println("User before/st primaryKey = " + this.getPrimaryKey());
			super.store();
			//      System.out.println("User after/st primaryKey = " + this.getPrimaryKey());
	}

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
		if (_group == null) {
			try {
				_group = getGroupHome().findByPrimaryKey(this.getPrimaryKey());
			}
			catch (FinderException fe) {
				throw new EJBException(fe.getMessage());
			}
		}
		return _group;
	}

	//
	//
	//

	public String getName() {
		try {
			return getGeneralGroup().getName();
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	public void setName(String name) {
		try {
			getGeneralGroup().setName(name);
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	public String getShortName() {
		try {
			return getGeneralGroup().getShortName();
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	public void setShortName(String shortName) {
		try {
			getGeneralGroup().setShortName(shortName);
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	public String getAbbrevation() {
		try {
			return getGeneralGroup().getAbbrevation();
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	public void setAbbrevation(String abbr) {
		try {
			getGeneralGroup().setAbbrevation(abbr);
		}
		catch (Exception ex) {
			throw new EJBException(ex);
		}
	}

	public String getGroupType(){
		return getGeneralGroup().getGroupType();
	}

	public void setGroupType(String type){
		getGeneralGroup().setGroupType(type);
	}
	
	public void setGroupType(GroupType type){
		getGeneralGroup().setGroupType(type);
	}

	public String getDescription(){
		return getGeneralGroup().getDescription();
	}

	public void setDescription(String description){
		getGeneralGroup().setDescription(description);
	}

	public String getExtraInfo(){
		return getGeneralGroup().getExtraInfo();
	}

	public void setExtraInfo(String extraInfo){
		getGeneralGroup().setExtraInfo(extraInfo);
	}

	public Timestamp getCreated(){
		return getGeneralGroup().getCreated();
	}

	public void setCreated(Timestamp created){
		getGeneralGroup().setCreated(created);
	}

	//
	//
	//

	//
	//
	//

	/**
	 * Returns a collection of Group objects that are related by the relation type relationType with this Group
	 */
	public Collection getRelatedBy(GroupRelationType relationType) throws FinderException{
		return this.getGeneralGroup().getRelatedBy(relationType);
	}

	/**
	 * Returns a collection of Group objects that are related by the relation type relationType with this Group
	 */
	public Collection getRelatedBy(String relationType) throws FinderException{
		return this.getGeneralGroup().getRelatedBy(relationType);
	}

	public void addRelation(Group groupToAdd, String relationType) throws CreateException{
		this.getGeneralGroup().addRelation(groupToAdd, relationType);
	}
	
	public Collection getReverseRelatedBy(String relationType) throws FinderException{
		return this.getGeneralGroup().getReverseRelatedBy(relationType);
	}

	public void addRelation(Group groupToAdd, GroupRelationType relationType) throws CreateException{
		this.getGeneralGroup().addRelation(groupToAdd, relationType);
	}

	public void addRelation(int relatedGroupId, GroupRelationType relationType) throws CreateException{
		this.getGeneralGroup().addRelation(relatedGroupId, relationType);
	}
	
	public void addUniqueRelation(int relatedGroupId, String relationType) throws CreateException{
		this.getGeneralGroup().addUniqueRelation(relatedGroupId, relationType);
	}
	
	public void addUniqueRelation(Group relatedGroup, String relationType) throws CreateException{
		this.getGeneralGroup().addUniqueRelation(relatedGroup, relationType);
	}

	public void addRelation(int relatedGroupId, String relationType) throws CreateException{
		this.getGeneralGroup().addRelation(relatedGroupId, relationType);
	}

	public void removeRelation(Group relatedGroup, String relationType) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroup, relationType);
	}

	public void removeRelation(Group relatedGroup, String relationType, User performer) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroup, relationType, performer);
	}

	public void removeRelation(int relatedGroupId, String relationType) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroupId, relationType);
	}

	public void removeRelation(int relatedGroupId, String relationType, User performer) throws RemoveException{
		this.getGeneralGroup().removeRelation(relatedGroupId, relationType, performer);
	}

	public java.util.List getParentGroups() throws javax.ejb.EJBException{
		return this.getGeneralGroup().getParentGroups();
	}

	public void addGroup(com.idega.user.data.Group p0) throws javax.ejb.EJBException{
		this.getGeneralGroup().addGroup(p0);
	}

	public java.util.List getChildGroups(java.lang.String[] p0, boolean p1) throws javax.ejb.EJBException{
		return this.getGeneralGroup().getChildGroups(p0, p1);
	}

	public void addGroup(int p0) throws javax.ejb.EJBException{
		this.getGeneralGroup().addGroup(p0);
	}

	public java.util.List getChildGroups() throws javax.ejb.EJBException{
		return this.getGeneralGroup().getChildGroups();
	}
	
	public java.util.Collection getChildGroups(Group returnProxy) throws javax.ejb.EJBException{
		return this.getGeneralGroup().getChildGroups(returnProxy);
	}

	public java.util.Collection getAllGroupsContainingUser(com.idega.user.data.User p0)
		throws javax.ejb.EJBException{
		return this.getGeneralGroup().getAllGroupsContainingUser(p0);
	}

	public java.lang.String getGroupTypeValue(){
		return this.getGroupTypeKey();
	}

	public boolean hasRelationTo(Group group){
		return this.getGeneralGroup().hasRelationTo(group);
	}
	
	public boolean hasRelationTo(int id){
		return this.getGeneralGroup().hasRelationTo(id);
	}
	
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

	public boolean equals(IDOEntity obj) {
		if (obj instanceof AbstractGroupBMPBean) {
			return this.equals(((AbstractGroupBMPBean) obj).getGeneralGroup());
		}
		else if (obj instanceof Group) {
			return super.equals((Group) obj);
		}
		else {
			return super.equals(obj);
		}
	}


	public boolean isUser(){
		try{
			return getGeneralGroup().isUser();	
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	
	public void setPermissionControllingGroupID(int id) {
		try{
			getGeneralGroup().setPermissionControllingGroupID(id);	
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	public void setPermissionControllingGroup(Group controllingGroup) {
		try{
			getGeneralGroup().setPermissionControllingGroup(controllingGroup);	
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	public int getPermissionControllingGroupID() {
		try{
			return getGeneralGroup().getPermissionControllingGroupID();	
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	public Group getPermissionControllingGroup() {
		try{
			return getGeneralGroup().getPermissionControllingGroup();	
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	public void setIsPermissionControllingGroup(boolean isControlling){
		try{
			getGeneralGroup().setIsPermissionControllingGroup(isControlling);	
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
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

	public Iterator getChildrenIterator() {
		return this.getGeneralGroup().getChildrenIterator();
	}

	public boolean getAllowsChildren() {
		return this.getGeneralGroup().getAllowsChildren();
	}
	public ICTreeNode getChildAtIndex(int childIndex) {
		return this.getGeneralGroup().getChildAtIndex(childIndex);
	}
	public int getChildCount() {
		return this.getGeneralGroup().getChildCount();
	}
	public int getIndex(ICTreeNode node) {
		return this.getGeneralGroup().getIndex(node);
	}
	public ICTreeNode getParentNode() {
		return this.getGeneralGroup().getParentNode();
	}
	public boolean isLeaf() {
		return this.getGeneralGroup().isLeaf();
	}
	public String getNodeName() {
		return this.getGeneralGroup().getNodeName();
	}

	public String getNodeName(Locale locale) {
		return this.getGeneralGroup().getNodeName(locale);
	}
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return this.getGeneralGroup().getNodeName(locale,iwac);
	}
	public int getNodeID() {
		return this.getGeneralGroup().getNodeID();
	}
	public int getSiblingCount() {
		return this.getGeneralGroup().getSiblingCount();
	}
	
	/**
	 * @see com.idega.core.ICTreeNode#getNodeType()
	 */
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
	
	public SelectQuery getSelectQueryConstraints(){
		return null;
	}
	
	

}