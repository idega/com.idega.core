package com.idega.user.data;

import com.idega.builder.data.IBPage;
import com.idega.core.ICTreeNode;
import com.idega.core.data.Address;
import com.idega.core.data.Email;
import com.idega.core.data.Phone;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOException;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDORuntimeException;
import com.idega.data.IDOUtil;
import com.idega.util.ListUtil;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserBMPBean extends AbstractGroupBMPBean implements User, Group, com.idega.core.user.data.User {

	private static String sClassName = User.class.getName();

	//    public UserBMPBean(){
	//      super();
	//    }

	//    public UserBMPBean(int id)throws SQLException{
	//      super(id);
	//    }

	public String getEntityName() {
		return "ic_user";
	}

	public void initializeAttributes() {
		//      addAttribute(getIDColumnName());
		super.addGeneralGroupRelation();
		addAttribute(getColumnNameFirstName(), "First name", true, true, java.lang.String.class, 30);
		addAttribute(getColumnNameMiddleName(), "Middle name", true, true, java.lang.String.class, 40);
		addAttribute(getColumnNameLastName(), "Last name", true, true, java.lang.String.class, 30);
		addAttribute(getColumnNameDisplayName(), "Display name", true, true, java.lang.String.class, 30);
		addAttribute(getColumnNameDescription(), "Description", true, true, java.lang.String.class);
		addAttribute(getColumnNameDateOfBirth(), "Birth date", true, true, java.sql.Date.class);
		addAttribute(getColumnNamePersonalID(), "Personal ID", true, true, String.class, 20);
		addManyToOneRelationship(getColumnNameGender(), "Gender", com.idega.user.data.Gender.class);
		addOneToOneRelationship(getColumnNameSystemImage(), "Image", com.idega.core.data.ICFile.class);
		/**
		 * For legacy compatabuility
		 */
		addOneToOneRelationship(_COLUMNNAME_USER_GROUP_ID, "User", Group.class);
		addOneToOneRelationship(_COLUMNNAME_PRIMARY_GROUP_ID, "Primary group", Group.class);
		this.addManyToManyRelationShip(Address.class, "ic_user_address");
		this.addManyToManyRelationShip(Phone.class, "ic_user_phone");
		this.addManyToManyRelationShip(Email.class, "ic_user_email");
		this.setNullable(getColumnNameSystemImage(), true);
		//      this.setNullable(_COLUMNNAME_PRIMARY_GROUP_ID,true);
		// this.setUnique(getColumnNamePersonalID(),true);

	}

	//    public void setDefaultValues(){
	//    }
	//
	//    public void insertStartData(){
	//
	//    }

	public String getIDColumnName() {
		return getColumnNameUserID();
	}

	public static UserBMPBean getStaticInstance() {
		return (UserBMPBean) com.idega.user.data.UserBMPBean.getStaticInstance(sClassName);
	}

	public static String getAdminDefaultName() {
		return "Administrator";
	}

	public String getGroupTypeDescription() {
		return "";
	}

	public String getGroupTypeKey() {
		return "ic_user_representative";
	}
	
	public String ejbHomeGetGroupType(){
		return super.ejbHomeGetGroupType();
	}

	public boolean getGroupTypeVisibility() {
		return false;
	}

	/*  ColumNames begin   */

	public static String getColumnNameUserID() {
		return "IC_USER_ID";
	}
	public static String getColumnNameFirstName() {
		return "FIRST_NAME";
	}
	public static String getColumnNameMiddleName() {
		return "MIDDLE_NAME";
	}
	public static String getColumnNameLastName() {
		return "LAST_NAME";
	}
	public static String getColumnNameDisplayName() {
		return "DISPLAY_NAME";
	}
	public static String getColumnNameDescription() {
		return "DESCRIPTION";
	}
	public static String getColumnNameDateOfBirth() {
		return "DATE_OF_BIRTH";
	}
	public static String getColumnNameGender() {
		return "IC_GENDER_ID";
	}
	public static String getColumnNameSystemImage() {
		return "SYSTEM_IMAGE_ID";
	}
	//    public static final String _COLUMNNAME_USER_GROUP_ID = "USER_REPRESENTATIVE";
	public static final String _COLUMNNAME_PRIMARY_GROUP_ID = "PRIMARY_GROUP";
	public static String getColumnNamePersonalID() {
		return "PERSONAL_ID";
	}

	//added by Laddi
	public static String getColumnNameHomePageID() {
		return "HOME_PAGE_ID";
	}

	/**
	 * @depricated
	 */
	public static final String _COLUMNNAME_USER_GROUP_ID = "user_representative";

	/*  ColumNames end   */

	/*  Getters begin   */

	public String getPersonalID() {
		return getStringColumnValue(getColumnNamePersonalID());
	}

	public String getFirstName() {
		return (String) getColumnValue(getColumnNameFirstName());
	}

	public String getMiddleName() {
		return (String) getColumnValue(getColumnNameMiddleName());
	}

	public String getLastName() {
		return (String) getColumnValue(getColumnNameLastName());
	}

	public String getDisplayName() {
		return (String) getColumnValue(getColumnNameDisplayName());
	}

	public String getDescription() {
		return (String) getColumnValue(getColumnNameDescription());
	}

	public Date getDateOfBirth() {
		return (Date) getColumnValue(getColumnNameDateOfBirth());
	}

	public int getGenderID() {
		return getIntColumnValue(getColumnNameGender());
	}

	public int getSystemImageID() {
		return getIntColumnValue(getColumnNameSystemImage());
	}

	public int getPrimaryGroupID() {
		return getIntColumnValue(_COLUMNNAME_PRIMARY_GROUP_ID);
	}

	public Group getPrimaryGroup() {
		return (Group) getColumnValue(_COLUMNNAME_PRIMARY_GROUP_ID);
	}

	public String getName() {
		String firstName = this.getFirstName();
		String middleName = this.getMiddleName();
		String lastName = this.getLastName();

		if (firstName == null) {
			firstName = "";
		}

		if (middleName == null) {
			middleName = "";
		}
		else {
			middleName = " " + middleName;
		}

		if (lastName == null) {
			lastName = "";
		}
		else {
			lastName = " " + lastName;
		}
		return firstName + middleName + lastName;
	}

	public String getNameLastFirst() {
		return getNameLastFirst(false);
	}

	public String getNameLastFirst(boolean commaSeperated) {
		String firstName = this.getFirstName();
		String middleName = this.getMiddleName();
		String lastName = this.getLastName();

		if (lastName == null) {
			lastName = "";
		}
		else {
			if (commaSeperated)
				lastName += ",";
		}

		if (firstName == null) {
			firstName = "";
		}
		else {
			firstName = " " + firstName;
		}

		if (middleName == null) {
			middleName = "";
		}
		else {
			middleName = " " + middleName;
		}

		return lastName + firstName + middleName;
	}

	public int getHomePageID() {
		try {
			return getGeneralGroup().getHomePageID();
		}
		catch (RemoteException e) {
			return -1;
		}
	}

	public IBPage getHomePage() {
		try {
			return getGeneralGroup().getHomePage();
		}
		catch (RemoteException e) {
			return null;
		}
	}
	
	public Timestamp getCreated() {
		try {
			return getGeneralGroup().getCreated();
		}
		catch (RemoteException e) {
			return null;
		}
	}

	/*  Getters end   */

	/*  Setters begin   */

	public void setPersonalID(String personalId) {
		setColumn(getColumnNamePersonalID(), personalId);
	}

	public void setFirstName(String fName) {
		//      if(!com.idega.core.accesscontrol.business.AccessControl.isValidUsersFirstName(fName)){
		//	fName = "Invalid firstname";
		//      }
		//      if(com.idega.core.accesscontrol.business.AccessControl.isValidUsersFirstName(this.getFirstName())){ // if not Administrator
		setColumn(getColumnNameFirstName(), fName);
		//      }
	}

	public void setMiddleName(String mName) {
		setColumn(getColumnNameMiddleName(), mName);
	}

	public void setLastName(String lName) {
		setColumn(getColumnNameLastName(), lName);
	}

	/**
	 * Divides the name string into first(1),middle(1-*) and lastname(1). <br>
	 * and uses setFirstName(),setMiddleName() and setLastName().
	 */
	public void setFullName(String name) {
		if ((name != null) && (name.length() > 0)) {
			StringTokenizer token = new StringTokenizer(name);
			int countWithoutFirstAndLast = token.countTokens() - 2;

			setFirstName(((String) token.nextElement()));

			if (countWithoutFirstAndLast >= 1) {
				StringBuffer middleName = new StringBuffer();

				for (int i = 0; i < countWithoutFirstAndLast; i++) {
					middleName.append((String) token.nextElement());

					if (i != (countWithoutFirstAndLast - 1))
						middleName.append(" ");

				}

				setMiddleName(middleName.toString());
			}
			else { //set middle name == null
				this.removeFromColumn(this.getColumnNameMiddleName());
			}

			if (countWithoutFirstAndLast >= 0) {
				setLastName((String) token.nextElement());
			}
			else { //remove last name
				this.removeFromColumn(this.getColumnNameLastName());
			}
		}
	}

	public void setDisplayName(String dName) {
		setColumn(getColumnNameDisplayName(), dName);
	}

	public void setDescription(String description) {
		setColumn(getColumnNameDescription(), description);
	}

	public void setDateOfBirth(Date dateOfBirth) {
		setColumn(getColumnNameDateOfBirth(), dateOfBirth);
	}

	public void setGender(Integer gender) {
		setColumn(getColumnNameGender(), gender);
	}

	public void setGender(int gender) {
		setColumn(getColumnNameGender(), gender);
	}

	public void setSystemImageID(Integer fileID) {
		setColumn(getColumnNameSystemImage(), fileID);
	}

	public void setSystemImageID(int fileID) {
		setColumn(getColumnNameSystemImage(), fileID);
	}

	public void setPrimaryGroupID(int icGroupId) {
		setColumn(_COLUMNNAME_PRIMARY_GROUP_ID, icGroupId);
	}
	
	public void setPrimaryGroup(Group group)
	{
		try{
			int groupID = 	((Integer)group.getPrimaryKey()).intValue();
			setPrimaryGroupID(groupID);
		}
		catch(Exception e){
			e.printStackTrace();	
		}
	}
	public void setPrimaryGroupID(Integer icGroupId) {
		setColumn(_COLUMNNAME_PRIMARY_GROUP_ID, icGroupId);
	}

	public void setHomePageID(int pageID)  {
		try{
			Group group = getGeneralGroup();
			group.setHomePageID(pageID);
			group.store();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}

	public void setHomePageID(Integer pageID) {
		try{
			Group group = getGeneralGroup();
			group.setHomePageID(pageID);
			group.store();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}

	public void setHomePage(IBPage page)  {
		try{
			Group group = getGeneralGroup();
			group.setHomePage(page);
			group.store();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	public void setCreated(Timestamp stamp) {
		try{
			Group group = getGeneralGroup();
			group.setCreated(stamp);
			group.store();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}

	/*  Setters end   */

	/*  Business methods begin   */

	public void removeAllAddresses() throws IDORemoveRelationshipException {
		super.idoRemoveFrom(Address.class);
	}

	public void removeAllEmails() throws IDORemoveRelationshipException {
		super.idoRemoveFrom(Email.class);
	}

	public void removeAllPhones() throws IDORemoveRelationshipException {
		super.idoRemoveFrom(Phone.class);
	}

	public Collection getAddresses() {
		try {
			return super.idoGetRelatedEntities(Address.class);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getAddresses() : " + e.getMessage());
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

	public Collection getPhones() {
		try {
			return super.idoGetRelatedEntities(Phone.class);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in getPhones() : " + e.getMessage());
		}
	}

	public void addAddress(Address address) throws IDOAddRelationshipException {
		this.idoAddTo(address);
	}

	public void addEmail(Email email) throws IDOAddRelationshipException {
		this.idoAddTo(email);
	}

	public void addPhone(Phone phone) throws IDOAddRelationshipException {
		this.idoAddTo(phone);
	}

	/*  Business methods end   */

	/*  Finders begin   */



	/**
	 * Returns the Users that is the instance of the User representing the Groups in the Collection groupList
	 * @param groupList a Group of type "UserRepresentative"
	 * @return Collection of primary keys of the Users representing the UserGroups
	 * @throws FinderException If an error occurs
	 */
	public Collection ejbFindUsersForUserRepresentativeGroups(Collection groupList) throws FinderException {
		//      System.out.println("[UserBMPBean]: groupList = "+groupList);
		String sGroupList = IDOUtil.getInstance().convertListToCommaseparatedString(groupList);
		//      System.out.println("[UserBMPBean]: sGroupList = "+sGroupList);
		//      System.out.println("[UserBMPBean]: "+"select * from "+getEntityName()+" where "+_COLUMNNAME_USER_GROUP_ID+" in ("+sGroupList+")");

		IDOQuery query = new IDOQuery();
		query.appendSelectAllFrom(getEntityName());
		query.appendWhere(this.getIDColumnName());
		//		  IDOQuery subQuery = new IDOQuery();
		//		  try{
		//            subQuery.appendCommaDelimited(groupList);
		//          }
		//          catch(RemoteException rme){
		//            throw new EJBException(rme);
		//          }

		query.appendIn(sGroupList);
		query.appendOrderBy(this.getColumnNameFirstName());

		IDOQuery countQuery = new IDOQuery();
		countQuery.appendSelectCountFrom(getEntityName());
		countQuery.appendWhere(this.getIDColumnName());
		countQuery.appendIn(sGroupList);
		//	  return this.idoFindPKsBySQL(query.toString());
		try {
			return this.idoFindPKsBySQL(query.toString(), countQuery.toString());
		}
		catch (IDOException ex) {
			throw new EJBException(ex);
		}
		//      return this.idoFindIDsBySQL("select * from "+getEntityName()+" where "+this.getIDColumnName()+" in ("+sGroupList+")");
	}

	/**
	 * Returns the User that is the instance of the User representing the group userRepGroup	 * @param userRepGroup a Group of type "UserRepresentative"	 * @return Integer the primary key of the User representing the UserGroup	 * @throws FinderException If an error occurs	 */
	public Integer ejbFindUserForUserRepresentativeGroup(Group userRepGroup) throws FinderException {
		try{
			String sGroupPK = userRepGroup.getPrimaryKey().toString();
			IDOQuery query = idoQueryGetSelect();
			query.appendWhereEqualsQuoted(this.getIDColumnName(),sGroupPK);
			return (Integer)this.idoFindOnePKByQuery(query);
		}
		catch(RemoteException rme){
			throw new IDOFinderException(rme);
		}
	}

	public Collection ejbFindAllUsers() throws FinderException {
		return super.idoFindAllIDsBySQL();
	}

	public Collection ejbFindUsersInPrimaryGroup(Group group) throws FinderException, RemoteException {
		return super.idoFindAllIDsByColumnBySQL(_COLUMNNAME_PRIMARY_GROUP_ID, group.getPrimaryKey().toString());
	}

	public Collection ejbFindAllUsersOrderedByFirstName() throws FinderException {
		IDOQuery query = new IDOQuery();
		query.appendSelectAllFrom(this.getEntityName());
		query.appendOrderBy(this.getColumnNameFirstName() + "," + this.getColumnNameLastName() + "," + this.getColumnNameMiddleName());

		IDOQuery countQuery = new IDOQuery();
		countQuery.appendSelectCountFrom(this.getEntityName());

		//	  return super.idoFindPKsBySQL(query.toString());
		try {
			return super.idoFindPKsBySQL(query.toString(), countQuery.toString());
		}
		catch (IDOException ex) {
			throw new EJBException(ex);
		}

		//      return super.idoFindAllIDsOrderedBySQL(this.getColumnNameFirstName());
	}

	public void removeGroup(int p0, boolean p1) throws javax.ejb.EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method removeGroup() not yet implemented.");
	}
	public void removeUser(User p0) {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method removeUser() not yet implemented.");
	}
	public void setGroupType(String p0){
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method setGroupType() not yet implemented.");
	}
	public String getGroupTypeValue() {
		return "user_group_representative";
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method getGroupTypeValue() not yet implemented.");
	}
	public void setExtraInfo(String p0) {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method setExtraInfo() not yet implemented.");
	}
	public void removeGroup() throws javax.ejb.EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method removeGroup() not yet implemented.");
	}
	/* public boolean equals(Group p0) throws java.rmi.RemoteException {
	   //throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
	   //return equals((Object)this);
	   return this.getGeneralGroup().equals(p0);
	 }*/
	public void addGroup(Group p0) throws EJBException{
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method addGroup() not yet implemented.");
	}
	public List getChildGroups(String[] p0, boolean p1) throws javax.ejb.EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method getGroupsContained() not yet implemented.");
	}
	public List getListOfAllGroupsContaining(int p0) throws javax.ejb.EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method getListOfAllGroupsContaining() not yet implemented.");
	}
	public void addGroup(int p0) throws javax.ejb.EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method addGroup() not yet implemented.");
	}
	public List getChildGroups() throws javax.ejb.EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method getListOfAllGroupsContained() not yet implemented.");
	}
	public Collection getAllGroupsContainingUser(User p0) throws EJBException{
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method getAllGroupsContainingUser() not yet implemented.");
	}
	public void removeGroup(Group p0) throws EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method removeGroup() not yet implemented.");
	}
	public String getGroupType() {
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method getGroupType() not yet implemented.");
		return "user_group_representative";
	}
	/**
	 * Gets a list of all the groups that this "group" is directly member of.	 * @see com.idega.user.data.Group#getListOfAllGroupsContainingThis()	 */
	public List getParentGroups()  {
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method getListOfAllGroupsContainingThis() not yet implemented.");
		try{
			List l = getGeneralGroup().getParentGroups();
			Group primaryGroup = this.getPrimaryGroup();
			if(!l.contains(primaryGroup)){
				l.add(primaryGroup);
			}
			return l;
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	public String getExtraInfo()  {
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method getExtraInfo() not yet implemented.");
		try{
			return this.getGeneralGroup().getExtraInfo();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	public void addUser(User p0){
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method addUser() not yet implemented.");
	}

	public Iterator getChildren() {
		return ListUtil.getEmptyList().iterator();
	}
	public boolean getAllowsChildren() {
		return false;
	}
	public ICTreeNode getChildAtIndex(int childIndex) {
		/**@todo: Implement this com.idega.core.ICTreeNode method*/
		throw new java.lang.UnsupportedOperationException("Method getChildAtIndex() not supported.");
	}
	public int getChildCount() {
		return 0;
	}
	public int getIndex(ICTreeNode node) {
		throw new java.lang.UnsupportedOperationException("Method getIndex() not supported.");
	}
	public ICTreeNode getParentNode() {
		/**@todo: Implement this com.idega.core.ICTreeNode method*/
		throw new java.lang.UnsupportedOperationException("Method getParentNode() not yet implemented.");
	}
	public boolean isLeaf() {
		return true;
	}
	public String getNodeName() {
		return this.getName();
	}
	public int getNodeID() {
		return this.getID();
	}

	public Integer ejbFindUserFromEmail(String emailAddress) throws FinderException, RemoteException {
		StringBuffer sql = new StringBuffer("select iu.* ");
		sql.append("from ic_email ie,ic_user_email iue,ic_user iu ");
		sql.append("where ie.ic_email_id = iue.ic_email_address ");
		sql.append("and iue.ic_user_id = iu.ic_user_id ");
		sql.append(" and ie.address = ");
		sql.append(emailAddress);
		return (Integer) super.idoFindOnePKBySQL(sql.toString());
		/*
		Collection coll =  super.idoFindIDsBySQL(sql.toString());
		if(!coll.isEmpty())
		return (Integer)coll.iterator().next();
		else
		throw new FinderException("No user found");
		*/
	}

	public Collection ejbFindByNames(String first, String middle, String last) throws FinderException {
		StringBuffer sql = new StringBuffer("select * from ");
		sql.append("ic_user u ");
		boolean isfirst = true;
		if (first != null || middle != null || last != null) {
			sql.append(" where ");
			if (first != null && !"".equals(first)) {
				if (!isfirst)
					sql.append(" and ");
				sql.append(" u.").append(getColumnNameFirstName()).append(" like '%");
				sql.append(first);
				sql.append("%' ");
				isfirst = false;
			}
			if (middle != null && !"".equals(middle)) {
				if (!isfirst)
					sql.append(" and ");
				sql.append(" and u.").append(getColumnNameMiddleName()).append(" like '%");
				sql.append(middle);
				sql.append("%' ");
				isfirst = false;
			}
			if (last != null && !"".equals(last)) {
				if (!isfirst)
					sql.append(" and ");
				sql.append(" u.").append(getColumnNameLastName()).append(" like '%");
				sql.append(last);
				sql.append("%' ");
				isfirst = false;
			}
			System.err.println(sql.toString());
			return super.idoFindPKsBySQL(sql.toString());
		}
		throw new FinderException("No legal names provided");
	}

	public Integer ejbFindByPersonalID(String personalId) throws FinderException {
		Collection users = super.idoFindAllIDsByColumnBySQL(getColumnNamePersonalID(), personalId);
		if (!users.isEmpty())
			return (Integer) users.iterator().next();
		else
			throw new FinderException("No user found");
	}
	
	/**
	 * Returns the User that is the instance of the User representing the group userRepGroup
	 * @param userRepGroupID a primary key of a Group of type "UserRepresentative"
	 * @return Integer the primary key of the User representing the UserGroup
	 * @throws FinderException If an error occurs or no user is found for the Group
	 */
	public Integer ejbFindUserForUserGroup(int userRepGroupID) throws FinderException {
		/** @todo Remove backwards compatability  */
		Integer pk;
		//try {
			IDOQuery query = idoQueryGetSelect();
			query.appendWhereEquals(_COLUMNNAME_USER_GROUP_ID,userRepGroupID);
			pk = (Integer)this.idoFindOnePKByQuery(query);
		
			//pk = (Integer) super.idoFindOnePKBySQL("select * from " + getEntityName() + " where " + _COLUMNNAME_USER_GROUP_ID + "='" + userRepGroupID + "'");
		//}
		//catch (FinderException ex) {
			//pk = new Integer(userRepGroupID);
		//}
		//catch (Exception ex) {
			//throw new IDOFinderException(ex);
		//}
		return pk;
	}
	
	/**
	 * Returns the User that is the instance of the User representing the group userRepGroup
	 * @param userRepGroup a Group of type "UserRepresentative"
	 * @return Integer the primary key of the User representing the UserGroup
	 * @throws FinderException If an error occurs or no user is found for the Group
	 */
	public Integer ejbFindUserForUserGroup(Group userRepGroup) throws FinderException {
		try{
			int groupID = ((Integer) userRepGroup.getPrimaryKey()).intValue();
			return this.ejbFindUserForUserGroup(groupID);
		}
		catch(RemoteException rme){
			throw new IDOFinderException(rme);
		}
	}

	/*  Finders end   */

	/**
	 * @deprecated
	 */
	public Group getGroup() {
		return this;
	}

	/**
	 * @deprecated
	 */
	public int getGroupID() {
		return this.getID();
	}

	/**
	 * @deprecated
	 */
	public Group getUserGroup() {
		try {
			return getGeneralGroup();
		}
		catch (RemoteException ex) {
			throw new EJBException(ex);
		}
	}

	protected Group getGeneralGroup() throws RemoteException {
		if (_group == null) {
			try {
				Integer groupID;
				Integer userGroupID = this.getIntegerColumnValue(_COLUMNNAME_USER_GROUP_ID);
				if (userGroupID == null) {
					groupID = (Integer) this.getPrimaryKey();
				}
				else {
					groupID = userGroupID;
				}
				_group = getGroupHome().findByPrimaryKey(groupID);
				//System.out.println("Getting userGroup "+_group.getName()+",id="+_group.getPrimaryKey()+" for user: "+this.getName()+",id="+this.getID());
			}
			catch (FinderException fe) {
				throw new EJBException(fe.getMessage());
			}
		}
		return _group;
	}

	public void setGroupID(int icGroupId) {
		this.setID(icGroupId);
	}

	public int ejbHomeGetUserCount() throws IDOException {
		//    String sqlQuery = "select count(*) from "+ this.getEntityName();
		return super.idoGetNumberOfRecords();
	}

	//      public boolean equals(Object obj){
	//        System.out.println("UserBMPBean: "+this+".equals(Object "+obj+")");
	//        if(obj instanceof UserBMPBean){
	//  //        try {
	//            return ((UserBMPBean)obj).getPrimaryKey().equals(this.getPrimaryKey());
	//  //        }
	//  //        catch (RemoteException ex) {
	//  //          throw new EJBException(ex);
	//  //        }
	//        } else {
	//          return super.equals(obj);
	//        }
	//      }

}
