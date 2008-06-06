package com.idega.user.data;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailBMPBean;
import com.idega.core.contact.data.Phone;
import com.idega.core.contact.data.PhoneBMPBean;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.localisation.data.ICLanguage;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressBMPBean;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Commune;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOEntityField;
import com.idega.data.IDOException;
import com.idega.data.IDOFinderException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDORuntimeException;
import com.idega.data.IDOUtil;
import com.idega.data.query.AND;
import com.idega.data.query.Column;
import com.idega.data.query.CountColumn;
import com.idega.data.query.Criteria;
import com.idega.data.query.InCriteria;
import com.idega.data.query.JoinCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.user.business.UserStatusBusinessBean;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.text.Name;
import com.idega.util.text.TextSoap;


/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.5
 */

public class UserBMPBean extends AbstractGroupBMPBean implements User, Group, com.idega.core.user.data.User {

	private static String sClassName = User.class.getName();
	static final String USER_GROUP_TYPE=User.USER_GROUP_TYPE;
	private static final String RELATION_TYPE_GROUP_PARENT = "GROUP_PARENT";
	private static final int PREFETCH_SIZE = 100;
	private static final int SUBLIST_SIZE = 1000;
	
	public final static String SQL_TABLE_NAME= "IC_USER";
	public final static String SQL_RELATION_EMAIL = "IC_USER_EMAIL";
	public final static String SQL_RELATION_ADDRESS = "IC_USER_ADDRESS";
	public final static String SQL_RELATION_PHONE = "IC_USER_PHONE";
	public final static String TABLE_NAME = SQL_TABLE_NAME;
 	public static final String COLUMN_NAME_USER_PROPERTIES_FILE_ID = "USER_PROPERTIES_FILE_ID";
  
	static final String META_DATA_HOME_PAGE = "homepage";
  

	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		//      addAttribute(getIDColumnName());
		super.addGeneralGroupRelation();
		addAttribute(getColumnNameFirstName(), "First name", true, true, java.lang.String.class, 45);
		addAttribute(getColumnNameMiddleName(), "Middle name", true, true, java.lang.String.class, 90);
		addAttribute(getColumnNameLastName(), "Last name", true, true, java.lang.String.class, 45);
		addAttribute(getColumnNameDisplayName(), "Display name", true, true, java.lang.String.class, 180);
		addAttribute(getColumnNameDescription(), "Description", true, true, java.lang.String.class);
		addAttribute(getColumnNameDateOfBirth(), "Birth date", true, true, java.sql.Date.class);
		addAttribute(getColumnNamePersonalID(), "Personal ID", true, true, String.class, 20);
	    addAttribute(getColumnNameDeleted(),"Deleted",true,true,Boolean.class);
	    addAttribute(getColumnNameDeletedBy(), "Deleted by", true, true, Integer.class, "many-to-one", User.class);
	    addAttribute(getColumnNameDeletedWhen(), "Deleted when", true, true, Timestamp.class);
    	addAttribute(getColumnNameFamilyID(), "Family ID", true, true, String.class, 20);
    	addAttribute(getColumnNamePreferredLocale(), "Preferred locale", true, true, String.class, 20);
		addOneToOneRelationship(COLUMN_NAME_USER_PROPERTIES_FILE_ID, ICFile.class);
		this.setNullable(COLUMN_NAME_USER_PROPERTIES_FILE_ID, true);

	//adds a unique id string column to this entity that is set when the entity is first stored.
	addUniqueIDColumn();
	
		addManyToOneRelationship(getColumnNameGender(), "Gender", com.idega.user.data.Gender.class);
		addOneToOneRelationship(getColumnNameSystemImage(), "Image", com.idega.core.file.data.ICFile.class);
		/**
		 * For legacy compatabuility
		 */
		addOneToOneRelationship(_COLUMNNAME_USER_GROUP_ID, "User", Group.class);
		addOneToOneRelationship(_COLUMNNAME_PRIMARY_GROUP_ID, "Primary group", Group.class);
		this.addManyToManyRelationShip(Address.class, SQL_RELATION_ADDRESS);
		this.addManyToManyRelationShip(Phone.class, SQL_RELATION_PHONE);
		this.addManyToManyRelationShip(Email.class, SQL_RELATION_EMAIL);
		this.setNullable(getColumnNameSystemImage(), true);
    addMetaDataRelationship();
		//      this.setNullable(_COLUMNNAME_PRIMARY_GROUP_ID,true);
		// this.setUnique(getColumnNamePersonalID(),true);
    
    //Added by Laddi 17.10.2003
    addManyToOneRelationship(getColumnNameNativeLanguage(), ICLanguage.class);

    addIndex("IDX_IC_USER_1", new String[]{getColumnNameLastName(), getColumnNameFirstName(), getColumnNameMiddleName()});
    addIndex("IDX_IC_USER_2", new String[]{getColumnNameFirstName(), getColumnNameLastName(), getColumnNameMiddleName()});
    addIndex("IDX_IC_USER_3", getColumnNameFirstName());
    addIndex("IDX_IC_USER_4", getColumnNamePersonalID());
    addIndex("IDX_IC_USER_5", _COLUMNNAME_USER_GROUP_ID);
    addIndex("IDX_IC_USER_6", getUniqueIdColumnName());
    
    	getEntityDefinition().setBeanCachingActiveByDefault(true);

	}

    public void setDefaultValues(){
    		super.setDefaultValues();
    		initializeColumnValue(getColumnNameDeleted(),Boolean.FALSE);
    }
	//
	//    public void insertStartData(){
	//
	//    }

	public String getIDColumnName() {
		return getColumnNameUserID();
	}

	public static UserBMPBean getStaticInstance() {
		return (UserBMPBean) GenericEntity.getStaticInstance(sClassName);
	}

	public static String getAdminDefaultName() {
		return "Administrator";
	}

	public String getGroupTypeDescription() {
		return "";
	}

	public String getGroupTypeKey() {
		return USER_GROUP_TYPE;
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
  
  public static String getColumnNameDeleted() {
    return "DELETED";
  }
  
  public static String getColumnNameDeletedBy() {
    return "DELETED_BY";
  }
  
  public static String getColumnNameDeletedWhen() {
    return "DELETED_WHEN";
  }
  
	public static String getColumnNameNativeLanguage() {
		return "IC_LANGUAGE_ID";
	}
	
	public static String getColumnNameFamilyID() {
		return "FAMILY_ID";
	}
	
	public static String getColumnNamePreferredLocale(){
		return "PREFERRED_LOCALE";
	}

	/**
	 * @depricated
	 */
	public static final String _COLUMNNAME_USER_GROUP_ID = "USER_REPRESENTATIVE";


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

	public Gender getGender() {
		return (Gender) getColumnValue(getColumnNameGender());
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

	public ICLanguage getNativeLanguage() {
		return (ICLanguage) getColumnValue(getColumnNameNativeLanguage());
	}

	public void setNativeLanguage(int ICLanguageID) {
		setColumn(getColumnNameNativeLanguage(), ICLanguageID);
	}

	public void setNativeLanguage(ICLanguage language) {
		setColumn(getColumnNameNativeLanguage(), language);
	}

	public void setFamilyID(String familyID) {
		setColumn(getColumnNameFamilyID(), familyID);
	}
	
	public String getFamilyID() {
		return getStringColumnValue(getColumnNameFamilyID());
	}
	
	public void setPreferredLocale(String preferredLocale) {
		setColumn(getColumnNamePreferredLocale(), preferredLocale);
	}
	
	public String getPreferredLocale() {
		return getStringColumnValue(getColumnNamePreferredLocale());
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
		else if (!middleName.equals("")) {
			middleName = " " + middleName;
		}

		if (lastName == null) {
			lastName = "";
		}
		else if (!lastName.equals("")){
			lastName = " " + lastName;
		}
		return firstName + middleName + lastName;
	}

	/*public String getNameLastFirst() {
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
	}*/

	public int getHomePageID() {
			return getGeneralGroup().getHomePageID();
	}

	public ICPage getHomePage() {
		return getGeneralGroup().getHomePage();
	}
	
	public int getHomeFolderID() {
		return getGeneralGroup().getHomeFolderID();
	}

	public ICFile getHomeFolder() {
		return getGeneralGroup().getHomeFolder();
	}
	
	public Timestamp getCreated() {
		return getGeneralGroup().getCreated();
	}
  
  public boolean getDeleted() {
    return getBooleanColumnValue(getColumnNameDeleted());
  }
  
  public int getDeletedBy() {
    return getIntColumnValue(getColumnNameDeletedBy());
  }
  
  public Timestamp getDeletedWhen() {
    return ((Timestamp) getColumnValue(getColumnNameDeletedWhen()));
  }
  
  public String getHomePageURL()  {
    return getMetaData(META_DATA_HOME_PAGE);
  }

  public Address getUsersMainAddress() throws EJBException, RemoteException {
	try {
	    AddressHome addressHome = (AddressHome) IDOLookup.getHome(Address.class);  
	    int id =  Integer.parseInt(getId());
		return addressHome.findPrimaryUserAddress(id);
	}
	catch (FinderException fe) {
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
		if( fName == null ){
			this.removeFromColumn(getColumnNameFirstName());
		}
		else{
			String temp = TextSoap.findAndCut(fName," ");
			if( temp.equals("")){
				this.removeFromColumn(getColumnNameFirstName());
			}
			else {
				setColumn(getColumnNameFirstName(), fName);
			}
		}	
		//      }
	}

	public void setMiddleName(String mName) {
		if( mName == null ){
			this.removeFromColumn(getColumnNameMiddleName());
		}
		else{
			String temp = TextSoap.findAndCut(mName," ");
			if( temp.equals("")){
				this.removeFromColumn(getColumnNameMiddleName());
			}
			else {
				setColumn(getColumnNameMiddleName(), mName);
			}
		}	
	}

	public void setLastName(String lName) {
		if( lName == null ){
			this.removeFromColumn(getColumnNameLastName());
		}
		else{
			String temp = TextSoap.findAndCut(lName," ");
			if( temp.equals("")){
				this.removeFromColumn(getColumnNameLastName());
			}
			else {
				setColumn(getColumnNameLastName(), lName);
			}
		}	
	}
	
	/**
	 * Divides the name string into first(1),middle(1-*) and lastname(1). <br>
	 * and uses setFirstName(),setMiddleName() and setLastName().
	 */
	public void setFullName(String fullName) {
		if ((fullName != null) && (fullName.length() > 0)) {
		    Name name = new Name(fullName).capitalize();
		    setFirstName(name.getFirstName());
		    setMiddleName(name.getMiddleName());
		    setLastName(name.getLastName());
		    /*
			StringTokenizer token = new StringTokenizer(name);
			int countWithoutFirstAndLast = token.countTokens() - 2;

			if (countWithoutFirstAndLast > -2) {
				setFirstName(((String) token.nextElement()));
			}
			
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
			*/
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
	
//	public void setFamilyID(String familyId) {
//		setColumn(getColumnNameFamilyID(), familyId);
//	}

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

	public void setHomePage(ICPage page)  {
		try{
			Group group = getGeneralGroup();
			group.setHomePage(page);
			group.store();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}
	
	
	public void setHomeFolderID(int fileID)  {
		try{
			Group group = getGeneralGroup();
			group.setHomeFolderID(fileID);
			group.store();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}

	public void setHomeFolderID(Integer fileID) {
		try{
			Group group = getGeneralGroup();
			group.setHomeFolderID(fileID);
			group.store();
		}
		catch(Exception e){
			throw new IDORuntimeException(e,this);	
		}
	}

	public void setHomeFolder(ICFile file)  {
		try{
			Group group = getGeneralGroup();
			group.setHomeFolder(file);
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
  
  public void setDeleted(boolean isDeleted) {
  	if(!isDeleted){
  		removeFromColumn(getColumnNameDeletedBy());
  		removeFromColumn(getColumnNameDeletedWhen());
  	}else{
  		removeFromColumn(_COLUMNNAME_PRIMARY_GROUP_ID);
  	}
    setColumn(getColumnNameDeleted(), isDeleted);
  }
  
  public void setDeletedBy(int userId)  {
    setColumn(getColumnNameDeletedBy(), userId);
  }
  
  public void setDeletedWhen(Timestamp timestamp) {
    setColumn(getColumnNameDeletedWhen(), timestamp);
  }
  
  /**
   * Do not use these with the UserBMPBean. Only here because UserBMPBean implements Group
   */
  public void setAliasID(int id) {
  }
  
	/**
	 * Do not use these with the UserBMPBean. Only here because UserBMPBean implements Group
	 */
  public void setAlias(Group alias) {
  }
  
	/**
	 * Do not use these with the UserBMPBean. Only here because UserBMPBean implements Group
	 */
  public int getAliasID() {
  	return -1;
  }
  
	/**
	 * Do not use these with the UserBMPBean. Only here because UserBMPBean implements Group
	 */
	public Group getAlias() {
		return null;
	}
  
  public void setHomePageURL(String homePageURL)  {
    setMetaData(META_DATA_HOME_PAGE, homePageURL);
  }
  
	/*  Setters end   */

	/*  Business methods begin   */

	public void removeAllAddresses() throws IDORemoveRelationshipException {
		super.idoRemoveFrom(Address.class);
	}

	public void removeAddress(Address address) throws IDORemoveRelationshipException {
		super.idoRemoveFrom(address);	
	}

	public void removeAllEmails() throws IDORemoveRelationshipException {
		super.idoRemoveFrom(Email.class);
	}
	
	public void removeEmail(Email email) throws IDORemoveRelationshipException {
		super.idoRemoveFrom(email);	
	}

	public void removeAllPhones() throws IDORemoveRelationshipException {
		super.idoRemoveFrom(Phone.class);
	}

	public void removePhone(Phone phone) throws IDORemoveRelationshipException {
		super.idoRemoveFrom(phone);	
	}

	public Collection getAddresses() {
		try {
		    Collection<Address> addresses = super.idoGetRelatedEntities(Address.class);
		    
		   return addresses;
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

	public Collection getPhones(String phoneTypeID) {
		try {
			return super.idoGetRelatedEntities(Phone.class, PhoneBMPBean.getColumnNamePhoneTypeId(), phoneTypeID);
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
  
  /**
   *
   */
  public void delete() throws SQLException {
    throw new SQLException("Use delete(int userId) instead");
  }

  /**
   * Delete this instance, store timestamp and theid of the user that causes the
   * the erasure
   * 
   * @param userId id of the user that is responsible for the deletion
   */
  public void delete(int userId) throws SQLException {
    setDeleted(true);
    
    setDeletedWhen(IWTimestamp.getTimestampRightNow());
    setDeletedBy(userId);

    super.update();
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

		SelectQuery query = idoSelectQuery();

		if(groupList!=null && !groupList.isEmpty()){
			InCriteria inCriteria = new InCriteria(idoQueryTable(),getIDColumnName(),sGroupList);
    	
			query.addCriteria(new AND(inCriteria,getNotDeletedCriteria()));
			addOrderByName(query,false,true);

			int loadBalancePrefetchSize = 1000;
			return this.idoFindPKsByQueryUsingLoadBalance(query, loadBalancePrefetchSize);
		}
		else{
			System.err.println("UserBMPBean: ejbFindUsersForUserRepresentativeGroups : groupList is NULL or empty!!");
			return null;
		}
		//      return this.idoFindIDsBySQL("select * from "+getEntityName()+" where "+this.getIDColumnName()+" in ("+sGroupList+")");
	}
	
	public SelectQuery getSelectQueryConstraints(){
		SelectQuery query = idoSelectQuery();
    	
		query.addCriteria(getNotDeletedCriteria());
		addOrderByName(query,false,true);
		return query;
	}

	public void addOrderByName(SelectQuery query, boolean lastnameFirst, boolean ascending){
		query.addOrder(idoQueryTable(),((lastnameFirst)?getColumnNameLastName():getColumnNameFirstName()),ascending);
		query.addOrder(idoQueryTable(),((!lastnameFirst)?getColumnNameLastName():getColumnNameFirstName()),ascending);
		query.addOrder(idoQueryTable(),getColumnNameMiddleName(),ascending);
	}
	
	/**
	 * Returns the User that is the instance of the User representing the group userRepGroup	 * @param userRepGroup a Group of type "UserRepresentative"	 * @return Integer the primary key of the User representing the UserGroup	 * @throws FinderException If an error occurs	 */
	public Integer ejbFindUserForUserRepresentativeGroup(Group userRepGroup) throws FinderException {
		//try{
			String sGroupPK = userRepGroup.getPrimaryKey().toString();
			IDOQuery query = idoQueryGetSelect();
			query
        .appendWhereEqualsQuoted(this.getIDColumnName(),sGroupPK)
        .appendAnd();
      appendIsNotDeleted(query);
			return (Integer)this.idoFindOnePKByQuery(query);
		//}
		//catch(RemoteException rme){
		//	throw new IDOFinderException(rme);
		//}
	}

  /** Gets all users that are not marked as deleted 
   * 
   * @return Collection
   * @throws FinderException
   */
	public Collection ejbFindAllUsers() throws FinderException {
    // use where not because DELETED = NULL means also not deleted
    // select * from ic_user where deleted != 'Y'
		SelectQuery query = idoSelectQuery();
		MatchCriteria c1 = new MatchCriteria(idoQueryTable(),getColumnNameDeleted(),MatchCriteria.EQUALS,GenericEntity.COLUMN_VALUE_FALSE,true);
		MatchCriteria c2 = new MatchCriteria(new Column(idoQueryTable(),getColumnNameDeleted()), false);
		query.addCriteria(new OR(c1, c2));
		
		return idoFindPKsByQueryUsingLoadBalance(query,10000);
	}
	
  /** Gets newest users that are not marked as deleted
   * 
   * @return Collection
   * @throws FinderException
   */
	public Collection ejbFindNewestUsers(int returningNumberOfRecords, int startingRecord) throws FinderException {
    // use where not because DELETED = NULL means also not deleted
    // select * from ic_user where deleted != 'Y'
	    IDOQuery query = idoQueryGetSelect();
	    query.appendWhere();
	    appendIsNotDeleted(query);
	    query.appendOrderByDescending(getIDColumnName());
	    return idoFindPKsBySQL(query.toString(), returningNumberOfRecords, startingRecord);
   
	}
	
	
	public Collection ejbFindUsersByMetaData(String key, String value) throws FinderException {
		return super.idoFindPKsByMetaData(key,value);
	}

  /** Gets all users that are not marked as deleted and
   *  that are members of the specified group
   * 
   * @param group
   * @return Collection
   * @throws FinderException
   */
	public Collection ejbFindUsersInPrimaryGroup(Group group) throws FinderException {
    IDOQuery query = idoQueryGetSelect();
    query
    	.appendWhere()
      .appendEqualsQuoted(_COLUMNNAME_PRIMARY_GROUP_ID, group.getPrimaryKey().toString())
      .appendAnd();
    appendIsNotDeleted(query);
    return idoFindIDsBySQL(query.toString());
  }
  
  /** Gets all users that are not marked as deleted ordered by first name.
   * This query uses a LoadTracker if necessary.
   * 
   * @return Collection
   * @throws FinderException
   */

	public Collection ejbFindAllUsersOrderedByFirstName() throws FinderException {
		SelectQuery query = idoSelectQuery();
		query.addCriteria(getNotDeletedCriteria());
		addOrderByName(query,false,true);
		
		int loadBalancePrefetchSize = 1000;
		return super.idoFindPKsByQueryUsingLoadBalance(query,loadBalancePrefetchSize);

	}
	
	public Object ejbFindByDateOfBirthAndName(Date dateOfBirth, String fullName) throws FinderException {
    Name name = new Name(fullName).capitalize();

    SelectQuery query = idoSelectQuery();
		query.addCriteria(new MatchCriteria(idoQueryTable(), FIELD_DATE_OF_BIRTH, MatchCriteria.EQUALS, dateOfBirth));
		if (name.getFirstName() != null && name.getFirstName().length() > 0) {
			query.addCriteria(new MatchCriteria(idoQueryTable(), FIELD_FIRST_NAME, MatchCriteria.EQUALS, name.getFirstName()));
		}
		if (name.getMiddleName() != null && name.getMiddleName().length() > 0) {
			query.addCriteria(new MatchCriteria(idoQueryTable(), FIELD_MIDDLE_NAME, MatchCriteria.EQUALS, name.getMiddleName()));
		}
		if (name.getLastName() != null && name.getLastName().length() > 0) {
			query.addCriteria(new MatchCriteria(idoQueryTable(), FIELD_LAST_NAME, MatchCriteria.EQUALS, name.getLastName()));
		}
		
		return idoFindOnePKByQuery(query);
	}

	public void removeGroup(int p0, boolean p1) throws javax.ejb.EJBException {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method removeGroup() not supported.");
	}
	public void removeUser(User p0) {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method removeUser() not supported.");
	}
	public void setGroupType(String p0){
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method setGroupType() not yet implemented.");
	}
	public String getGroupTypeValue() {
		return USER_GROUP_TYPE;
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method getGroupTypeValue() not yet implemented.");
	}
	public void setExtraInfo(String p0) {
		/**@todo: Implement this com.idega.user.data.Group method*/
		throw new java.lang.UnsupportedOperationException("Method setExtraInfo() not yet implemented.");
	}
	public void removeGroup() throws javax.ejb.EJBException {
		throw new java.lang.UnsupportedOperationException("Method removeGroup() not supported.");
	}
	/* public boolean equals(Group p0) throws java.rmi.RemoteException {
	   //throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
	   //return equals((Object)this);
	   return this.getGeneralGroup().equals(p0);
	 }*/
	public void addGroup(Group p0,java.sql.Timestamp p1) throws EJBException{
		throw new java.lang.UnsupportedOperationException("Method addGroup() not supported.");
	}
	public void addGroup(Group p0) throws EJBException{
		throw new java.lang.UnsupportedOperationException("Method addGroup() not supported.");
	}
	
	public void addGroup(User p0) throws EJBException{
		throw new java.lang.UnsupportedOperationException("Method addGroup() not supported.");
	}
	
	public List getChildGroups(String[] p0, boolean p1) throws javax.ejb.EJBException {
		throw new java.lang.UnsupportedOperationException("Method getGroupsContained() not supported");
	}
	public List getChildGroupsIDs(String[] p0, boolean p1) throws javax.ejb.EJBException {
		throw new java.lang.UnsupportedOperationException("Method getGroupsContained() not supported");
	}
	public List getListOfAllGroupsContaining(int p0) throws javax.ejb.EJBException {
		throw new java.lang.UnsupportedOperationException("Method getListOfAllGroupsContaining() not yet implemented.");
	}
	public void addGroup(int p0) throws javax.ejb.EJBException {
		throw new java.lang.UnsupportedOperationException("Method addGroup() not supported.");
	}
	public List getChildGroups() throws javax.ejb.EJBException {
		throw new java.lang.UnsupportedOperationException("Method getListOfAllGroupsContained() not supported");
	}
	public Collection getAllGroupsContainingUser(User p0) throws EJBException{
		throw new java.lang.UnsupportedOperationException("Method getAllGroupsContainingUser() not supported.");
	}
	public void removeGroup(Group p0) throws EJBException {
		throw new java.lang.UnsupportedOperationException("Method removeGroup() not yet implemented.");
	}
	public String getGroupType() {
		//throw new java.lang.UnsupportedOperationException("Method getGroupType() not yet implemented.");
		return "user_group_representative";
	}
	/**
	 * Gets a list of all the groups that this "group" is directly member of.	 * @see com.idega.user.data.Group#getListOfAllGroupsContainingThis()	 */
	public List getParentGroups() throws EJBException {
		return getParentGroups(null, null);
	}
	
	/**
	 * Optimized version of getParentGroups() by Sigtryggur 22.06.2004
	 * Database access is minimized by passing a Map of cached groupParents and Map of cached groups to the method
	 */
	public List getParentGroups(Map cachedParents, Map cachedGroups)  {
		/**@todo: Implement this com.idega.user.data.Group method*/
		//throw new java.lang.UnsupportedOperationException("Method getListOfAllGroupsContainingThis() not yet implemented.");
		try{
			List l = getGeneralGroup().getParentGroups(cachedParents, cachedGroups);
    		//The next 4 lines perform the same actions as have already been performed in GroupBMPBean.getParentGroups() and are therefore unnecessary.
			//Group primaryGroup = this.getPrimaryGroup();
			//if(primaryGroup!=null && !l.contains(primaryGroup)){
			//	l.add(primaryGroup);
			//}
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

	public boolean isUser(){
		return true;
	}
	
	public boolean isAlias() {
	    return false;
	}
	
	public Iterator getChildrenIterator() {
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
	public String getNodeName(Locale locale) {
		return this.getNodeName();
	}
	public int getNodeID() {
		return this.getID();
	}

	public Integer ejbFindUserByUniqueId(String uniqueIdString) throws FinderException {
		return (Integer) idoFindOnePKByUniqueId(uniqueIdString);
	}
	
	public Integer ejbFindUserFromEmail(String emailAddress) throws FinderException {
 		String sql = getUsersByEmailSqlQuery(emailAddress);
 		return (Integer) super.idoFindOnePKBySQL(sql);
	}
	
	private String getUsersByEmailSqlQuery(String emailAddress) {
		
		StringBuffer sql = new StringBuffer("select iu.* ");
		sql.append(" from ").append(this.getTableName()).append(" iu ,");
		sql.append(EmailBMPBean.SQL_TABLE_NAME).append(" ie ,");
		sql.append(SQL_RELATION_EMAIL).append(" iue ");
		sql.append(" where ie.").append(EmailBMPBean.SQL_TABLE_NAME).append("_ID = ");
		sql.append("iue.").append(EmailBMPBean.SQL_TABLE_NAME).append("_ID  and ");
		sql.append("iue.").append(getIDColumnName()).append(" = iu.").append(getIDColumnName());
		sql.append(" and ie.").append(EmailBMPBean.SQL_COLUMN_EMAIL).append(" = '");
		sql.append(emailAddress).append("'");
	    // append is not deleted
	    sql.append(" and ");//iu.");
	    appendIsNotDeleted(sql);
	    
	    return sql.toString();
	}
	
	public Collection ejbFindUsersByEmail(String emailAddress) throws FinderException {
		
		String sql = getUsersByEmailSqlQuery(emailAddress);
 		return idoFindPKsBySQL(sql);
	}

	public Collection ejbFindByNames(String first, String middle, String last) throws FinderException {
	    SelectQuery query = idoSelectQuery();
		if (first != null || middle != null || last != null) {
			if (first != null && !"".equals(first)) {
			    query.addCriteria(new MatchCriteria(idoQueryTable(), getColumnNameFirstName(),MatchCriteria.LIKE,"%"+first+"%",true));
			}
			if (middle != null && !"".equals(middle)) {
			    query.addCriteria(new MatchCriteria(idoQueryTable(), getColumnNameFirstName(),MatchCriteria.LIKE,"%"+middle+"%",true));
			}
			if (last != null && !"".equals(last)) {
			    query.addCriteria(new MatchCriteria(idoQueryTable(), getColumnNameFirstName(),MatchCriteria.LIKE,"%"+last+"%",true));
			}
			// append is deleted filter 
			query.addCriteria(getNotDeletedCriteria());
			return super.idoFindPKsByQuery(query);
		}
		throw new FinderException("No legal names provided");
	}

	public Integer ejbFindByPersonalID(String personalId) throws FinderException {
	    Table table = new Table(this);
	    SelectQuery query = new SelectQuery(table);
	    query.addColumn(new Column(getIDColumnName()));
	    query.addCriteria(new MatchCriteria(table,getColumnNamePersonalID(),MatchCriteria.EQUALS,personalId));
	    query.addCriteria(getNotDeletedCriteria());
	    return (Integer)idoFindOnePKByQuery(query);
	}

	public Integer ejbFindByFirstSixLettersOfPersonalIDAndFirstNameAndLastName(String personalId, String first_name, String last_name) throws FinderException {
    
    if (personalId.length() < 6) {
			throw new FinderException("PersonalID shorter than 6 letters");
		}
	IDOQuery query = idoQueryGetSelect();
		 query
			.appendWhere(getColumnNamePersonalID())
			.appendLike()
			.appendWithinSingleQuotes(personalId.substring(0,6)+"%")
		.appendAnd().append(getColumnNameFirstName())
		.appendEqualSign()
		.appendWithinSingleQuotes(first_name)
		.appendAnd().append(getColumnNameLastName())
		.appendEqualSign()
		.appendWithinSingleQuotes(last_name)
		.appendAnd();
		appendIsNotDeleted(query);
    
		 Collection users = idoFindPKsByQuery(query);


		if (users.size() > 1) {
			throw new FinderException("More than one user matched the criteria. Couldn't determine which user to choose");
		}
		else if (!users.isEmpty()) {
			return (Integer) users.iterator().next();
		}
		else {
			throw new FinderException("No user found");
		}
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
			IDOQuery query = idoQuery();
			query.appendSelect().append(getIDColumnName()).appendFrom().append(getEntityName())
        .appendWhereEquals(_COLUMNNAME_USER_GROUP_ID,userRepGroupID)
        .appendAnd();
      appendIsNotDeleted(query);
     
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
		//try{
			try{
				int groupID = ((Integer) userRepGroup.getPrimaryKey()).intValue();
				return this.ejbFindUserForUserGroup(groupID);
			}
			catch(FinderException e){
				if(userRepGroup.isUser()){
					//return this.getUserHome().findByPrimaryKey(userGroup.getPrimaryKey());
					return (Integer)userRepGroup.getPrimaryKey();
				}
				else{
					throw new IDOFinderException("UserBMPBean.ejbFindUserForUserGroup() : No User found for Group with id:"+userRepGroup.toString());
				}
			}

		//}
		//catch(RemoteException rme){
		//	throw new IDOFinderException(rme);
		//}
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
		return getGeneralGroup();
	}

	protected Group getGeneralGroup() {
		if (this._group == null) {
			try {
				Integer groupID;
				Integer userGroupID = this.getIntegerColumnValue(_COLUMNNAME_USER_GROUP_ID);
				if (userGroupID == null) {
					groupID = (Integer) this.getPrimaryKey();
				}
				else {
					groupID = userGroupID;
				}
				this._group = getGroupHome().findByPrimaryKey(groupID);
				//System.out.println("Getting userGroup "+_group.getName()+",id="+_group.getPrimaryKey()+" for user: "+this.getName()+",id="+this.getID());
			}
			catch (FinderException fe) {
				throw new EJBException(fe.getMessage());
			}
		}
		return this._group;
	}

	public void setGroupID(int icGroupId) {
		this.setID(icGroupId);
	}

	public Collection ejbFindUsersBySearchCondition(String condition, boolean orderLastFirst) throws FinderException {
		return ejbFindUsersBySearchCondition(condition, null, orderLastFirst);
	}
	
	public Collection ejbFindUsersBySearchCondition(String condition, boolean orderLastFirst, int endAge) throws FinderException {
			return ejbFindUsersBySearchCondition(condition, null, orderLastFirst, 0, endAge);
	}
	
	public Collection ejbFindUsersBySearchCondition(String condition, boolean orderLastFirst, int startAge, int endAge) throws FinderException {
			return ejbFindUsersBySearchCondition(condition, null, orderLastFirst, startAge, endAge);
		}
	
	/**
	 * 
	 * @param condition
	 * @param validUserPks if NULL, the function ignores it, else uses it. 
	 * @return Collection
	 * @throws FinderException
	 */
	public Collection ejbFindUsersBySearchCondition(String condition, String[] userIds, boolean orderLastFirst) throws FinderException {
		return ejbFindUsersBySearchCondition(condition, userIds, orderLastFirst, -1, -1);
	}
		
	public Collection ejbFindUsersBySearchCondition(String condition, String[] userIds, boolean orderLastFirst, int startAge, int endAge) throws FinderException {
		//strange stuff...examine
		if (userIds != null && userIds.length == 0) {
			return ejbFindUsersBySearchCondition(condition, new String[]{"-1"}, orderLastFirst);
		}else if (condition == null || condition.equals("")) {
			if (userIds == null) {
				return ejbFindAllUsers();
			}else {
				return ejbFindUsers(userIds);
			}
		}else {
			return ejbFindUsersByConditions(condition,condition,null,null,-1,-1,startAge,endAge,null,userIds,false, orderLastFirst);
		}
	}
	
	public Collection ejbFindUsersByConditions(String userName, String personalId, String streetName, String groupName, int gender, int statusId, int startAge, int endAge, String[] allowedGroups, String[] allowedUsers, boolean useAnd, boolean orderLastFirst) throws FinderException {
		
		if(userName!=null && userName.indexOf(" ")>-1){
			Name name = new Name(userName);
			useAnd = true;
			if(userName.equals(personalId)){
				personalId = null;
			}
			return ejbFindUsersByConditions(name.getFirstName(), name.getMiddleName(), name.getLastName(), personalId, streetName, groupName, gender, statusId, startAge, endAge, allowedGroups, allowedUsers, useAnd, orderLastFirst);
		}
		else {
			return ejbFindUsersByConditions(userName, userName, userName, personalId, streetName, groupName, gender, statusId, startAge, endAge, allowedGroups, allowedUsers, useAnd, orderLastFirst);	
		}
	
	}
	
	public Collection ejbFindUsersByConditions(String firstName, String middleName, String lastName, String personalId, String streetName, String groupName, int gender, int statusId, int startAge, int endAge, String[] allowedGroups, String[] allowedUsers, boolean useAnd, boolean orderLastFirst) throws FinderException {
		
		SelectQuery query = idoSelectQuery();
		
		Criteria theCriteria = null;
		
		//name	
		if( (firstName!=null && !"".equals(firstName)) || (middleName!=null && !"".equals(middleName)) || (lastName!=null && !"".equals(lastName)) ){
			theCriteria = getUserNameSearchCriteria(firstName,middleName,lastName,useAnd);
		}
				
		if( (startAge >= 0) && (endAge >=startAge) ){
			Criteria ageCriteria = getUserDateOfBirthCriteria(startAge, endAge);
			if(theCriteria==null) {
				theCriteria = ageCriteria;
			}
			else {
				theCriteria = new AND(theCriteria,ageCriteria);
			}
		}		
		
		//personalId
		if(personalId!=null && !personalId.equals("") ){
			Criteria personalIdCriteria = new MatchCriteria(idoQueryTable(),getColumnNamePersonalID(),MatchCriteria.LIKE,"%"+personalId+"%");
			if(theCriteria==null){
				theCriteria = personalIdCriteria;
			} else {
				if(useAnd) {
					theCriteria = new AND(theCriteria,personalIdCriteria);
				}
				else {
					theCriteria = new OR(theCriteria,personalIdCriteria);
				}
			}	
		}
			
		//address
		if(streetName!=null && !streetName.equals("")  ){
			try {
				Criteria streetNameCriteria = new InCriteria(idoQueryTable(),getIDColumnName(),getUserAddressSelectQuery(streetName));
				if(theCriteria==null){
					theCriteria = streetNameCriteria;
				} else {
					if(useAnd) {
						theCriteria = new AND(theCriteria,streetNameCriteria);
					}
					else {
						theCriteria = new OR(theCriteria,streetNameCriteria);
					}
				}
			}
			catch (RemoteException re) {
				re.printStackTrace();
			}
		}
				
		//gender
		if(gender>0){
			Criteria genderCriteria = new MatchCriteria(idoQueryTable(),getColumnNameGender(),MatchCriteria.EQUALS,gender);
			if(theCriteria==null){
				theCriteria = genderCriteria;
			} else {
				if(useAnd) {
					theCriteria = new AND(theCriteria,genderCriteria);
				}
				else {
					theCriteria = new OR(theCriteria,genderCriteria);
				}
			}
		}
			
		//status
		//TODO Eiki add sql for only allowed groups and create and use method getUserStatusCriteria
		if( statusId>0 ){
			Criteria StatusIdCriteria = new InCriteria(idoQueryTable(),getIDColumnName(),getUserStatusSearchString(statusId,IWTimestamp.RightNow()));
			if(theCriteria==null){
				theCriteria = StatusIdCriteria;
			} else {
				if(useAnd) {
					theCriteria = new AND(theCriteria,StatusIdCriteria);
				}
				else {
					theCriteria = new OR(theCriteria,StatusIdCriteria);
				}
			}
		}
		
		//group search
		//TODO Eiki filter out only allowed and create and use method getUserInAllowedGroupsCriteria
		if(groupName!=null || (allowedGroups!=null && allowedGroups.length>0 )){
			Criteria groupNameCriteria = new InCriteria(idoQueryTable(),getIDColumnName(),getUserInAllowedGroupsSearchString(groupName,allowedGroups));
			if(theCriteria==null){
				theCriteria = groupNameCriteria;
			} else {
				if(useAnd) {
					theCriteria = new AND(theCriteria,groupNameCriteria);
				}
				else {
					theCriteria = new OR(theCriteria,groupNameCriteria);
				}
			}
		}	
			
		//filter by users
		if ( allowedUsers != null && allowedUsers.length>0 ) {
			Criteria allowedUsersCriteria = new InCriteria(idoQueryTable(),getIDColumnName(),IDOUtil.getInstance().convertArrayToCommaseparatedString(allowedUsers));
			if(theCriteria==null){
				theCriteria = allowedUsersCriteria;
			} else {
				theCriteria = new AND(theCriteria,allowedUsersCriteria);
			}
		}
		
		if(theCriteria==null) {
			theCriteria = getNotDeletedCriteria();
		}
		else {
			theCriteria = new AND(theCriteria,getNotDeletedCriteria());
		}
		
		query.addCriteria(theCriteria);
		addOrderByName(query,orderLastFirst,true);

		//return this.idoFindIDsBySQL(query.toString());
		//to benefit from the IDOEntityList features
		
		int loadBalancePrefetchSize = 100;
		if(this.isDebugActive()){
			System.out.println("[ejbFindUsersByConditions-sql]: "+query.toString());
		}
		return idoFindPKsByQueryUsingLoadBalance(query, loadBalancePrefetchSize);

	}

	
//	public Collection ejbFindUsersByConditions(String firstName, String middleName, String lastName, String personalId, String streetName, String groupName, int gender, int statusId, int startAge, int endAge, String[] allowedGroups, String[] allowedUsers, boolean useAnd, boolean orderLastFirst) throws FinderException {
//		IDOQuery query = idoQuery();
//		boolean firstOperatorAdded = false;
//		
//		final String operator = useAnd ? " AND " : " OR ";
//		
//		query.appendSelectAllFrom(this).appendWhere();
//		
//		query.append(" ( ");
//		//name	
//		if( (firstName!=null && !"".equals(firstName)) || (middleName!=null && !"".equals(middleName)) || (lastName!=null && !"".equals(lastName)) ){
//			query.append(" ( ")
//			.append(getUserNameSearchString(firstName,middleName,lastName,operator))
//			.append(" ) ");
//			
//			firstOperatorAdded = true;
//		}
//				
//		if( (startAge >= 0) && (endAge >=startAge) ){
//			if(firstOperatorAdded ) query.appendAnd();
//			
//			query.append(" ( ")
//			.append(getUserDateOfBirthSearchString(startAge, endAge))
//			.append(" ) ");
//			
//			firstOperatorAdded = true;
//		}
//		
//		//not deleted
//		if(firstOperatorAdded) query.appendAnd();
//		appendIsNotDeleted(query);
//		
//		
//		query.append(" ) ");
//		
//		//personalId
//		if(personalId!=null && !personalId.equals("") ){
//			query.append(operator)
//			.append(" ( ")
//			.append(getColumnNamePersonalID()).append(" like '%").append(personalId).append("%' ")
//			.append(" ) ");
//		}
//			
//		//address
//		if(streetName!=null && !streetName.equals("")  ){
//			query.append(operator)
//			.append(getIDColumnName()).appendIn(getUserAddressSearchString(streetName));
//		}
//				
//		//gender
//		if(gender>0){
//			query.appendAnd()
//			.append(" ( ")
//			.append(getColumnNameGender()).appendEqualSign().append(gender)
//			.append(" ) ");
//		}
//			
//		//status
//		//TODO Eiki add sql for only allowed groups
//		if( statusId>0 ){
//			query.append(operator)
//			.append(getIDColumnName()).appendIn(getUserStatusSearchString(statusId,IWTimestamp.RightNow()));
//		}
//		
//		//group search
//		//TODO Eiki filter out only allowed
//		if(groupName!=null || (allowedGroups!=null && allowedGroups.length>0 )){
//			query.append(operator)
//			.append(getIDColumnName()).appendIn(getUserInAllowedGroupsSearchString(groupName,allowedGroups));
//		}	
//			
//		//filter by users
//		if ( allowedUsers != null && allowedUsers.length>0 ) {
//			query.appendAnd().append(getIDColumnName()).appendIn(IDOUtil.getInstance().convertArrayToCommaseparatedString(allowedUsers));	
//		}
//      
//		if (orderLastFirst)
//			query.appendOrderBy(this.getColumnNameLastName()+","+this.getColumnNameFirstName()+","+this.getColumnNameMiddleName());
//		else
//			query.appendOrderBy(this.getColumnNameFirstName()+","+this.getColumnNameLastName()+","+this.getColumnNameMiddleName());
//		
//		
//		//return this.idoFindIDsBySQL(query.toString());
//		//to benefit from the IDOEntityList features
//		
//		int loadBalancePrefetchSize = 100;
//		if(this.isDebugActive()){
//			System.out.println("[ejbFindUsersByConditions-sql]: "+query);
//		}
//		return idoFindPKsByQueryUsingLoadBalance(query, loadBalancePrefetchSize);
//
//	}

	private Criteria getUserDateOfBirthCriteria(int startAge, int endAge) {				
		IWTimestamp youngerAgeStamp = IWTimestamp.RightNow();
		IWTimestamp olderAgeStamp = IWTimestamp.RightNow();
		
		youngerAgeStamp.addYears(-startAge);
		youngerAgeStamp.setMonth(12);
		youngerAgeStamp.setDay(31);
		
		olderAgeStamp.addYears(-endAge);
		olderAgeStamp.setMonth(1);
		olderAgeStamp.setDay(1);
		
		Criteria c1 = new MatchCriteria(idoQueryTable(),getColumnNameDateOfBirth(),MatchCriteria.GREATEREQUAL,olderAgeStamp.getTimestamp());
		Criteria c2 = new MatchCriteria(idoQueryTable(),getColumnNameDateOfBirth(),MatchCriteria.LESSEQUAL,youngerAgeStamp.getTimestamp());
		
		return new AND(c1,c2);
	}

	
	private Criteria getUserNameSearchCriteria(String firstName, String middleName, String lastName, boolean andCriteria) {
		int count = 0;
		Criteria firstNameCriteria = null;
		if(firstName!=null && !firstName.equals("")){
			String conditionString = StringHandler.firstCharacterToUpperCase(firstName);
			firstNameCriteria = new MatchCriteria(idoQueryTable(),getColumnNameFirstName(),MatchCriteria.LIKE,conditionString+"%");
			count++;
		}
		
		Criteria middleNameCriteria = null;
		if(middleName!=null && !middleName.equals("") ){
			String conditionString = StringHandler.firstCharacterToUpperCase(middleName);
			middleNameCriteria = new MatchCriteria(idoQueryTable(),getColumnNameMiddleName(),MatchCriteria.LIKE,conditionString+"%");
			count++;
		}
		
		Criteria lastNameCriteria = null;
		if(lastName!=null  && !lastName.equals("") ){
			String conditionString = StringHandler.firstCharacterToUpperCase(lastName);
			lastNameCriteria = new MatchCriteria(idoQueryTable(),getColumnNameLastName(),MatchCriteria.LIKE,conditionString+"%");
			count++;
		}
		
		switch (count) {
		case 1:
			if(firstNameCriteria!=null) {
				return firstNameCriteria;
			}
			if(middleNameCriteria!=null) {
				return middleNameCriteria;
			}
			if(lastNameCriteria!=null) {
				return lastNameCriteria;
			}
			else {
				return null;
			}
		case 2:
			if(middleNameCriteria==null){
				if(andCriteria) {
					return new AND(firstNameCriteria,lastNameCriteria);
				}
				else {
					return new OR(firstNameCriteria,lastNameCriteria);
				}
			}
			if(lastNameCriteria==null){
				if(andCriteria) {
					return new AND(firstNameCriteria,middleNameCriteria);
				}
				else {
					return new OR(firstNameCriteria,middleNameCriteria);
				}
			}
			if(firstNameCriteria==null){
				if(andCriteria) {
					return new AND(middleNameCriteria,lastNameCriteria);
				}
				else {
					return new OR(middleNameCriteria,lastNameCriteria);
				}
			}
			else {
				return null;
			}
		case 3:
			if(andCriteria){
				AND cr1 = new AND(firstNameCriteria,middleNameCriteria);
				AND cr2 = new AND(cr1,lastNameCriteria);
				return cr2;
			}else {
				OR cr1 = new OR(firstNameCriteria,middleNameCriteria);
				OR cr2 = new OR(cr1,lastNameCriteria);
				return cr2;
			}
		default:
			return null;
		}
	}
	
	
//	/**
//	 * @param condition
//	 * @return
//	 */
//	private String getUserNameSearchString(String firstName, String middleName, String lastName, String ANDOrOR) {
//		boolean firstNameAdded = false;
//		boolean middleNameAdded = false;
//
//		StringBuffer sql = new StringBuffer();
//		if(firstName!=null && !firstName.equals("")){
//			sql.append(getColumnNameFirstName()).append(" like '").append(firstName).append("%' ");
//			firstNameAdded = true;
//		}
//		
//		if(middleName!=null && !middleName.equals("") ){
//			if(firstNameAdded){
//				sql.append(ANDOrOR).append(" ");
//			}
//			
//			sql.append(getColumnNameMiddleName()).append(" like '").append(middleName).append("%' ");
//			middleNameAdded = true;
//		}
//		
//		if(lastName!=null  && !lastName.equals("") ){
//			if(middleNameAdded || firstNameAdded){
//				sql.append(ANDOrOR).append(" ");
//			}
//			sql.append(getColumnNameLastName()).append(" like '").append(lastName).append("%'");
//		}
//		
//		
//		return sql.toString();
//	}

	/**
	 * @param condition
	 */
	private String getUserStatusSearchString(int statusId, IWTimestamp currentTime) {
		IDOQuery sql = idoQuery();
		
		sql.append("select distinct(ic_user_id) from ic_usergroup_status where status_id = ")
		.append(statusId);
		sql.appendAnd().append("date_from").appendLessThanOrEqualsSign().append(currentTime)
		.appendAnd().appendLeftParenthesis().append("date_to").appendIsNull().appendOr().append("date_to").appendLessThanSign().append(currentTime).appendRightParenthesis();
		
		return sql.toString();
	}

	private String getUserInAllowedGroupsSearchString(String condition, String[] allowedGroups) {
		StringBuffer sql = new StringBuffer();
		sql.append("select gr.related_ic_group_id from ic_group g, ic_group_relation gr where gr.ic_group_id = g.ic_group_id and (gr.GROUP_RELATION_STATUS='ST_ACTIVE' or gr.GROUP_RELATION_STATUS='PASS_PEND' ) ");
		if(condition!=null){
			sql.append(" and g.name like '%")
			.append(condition)
			.append("%' ");
		}
		
		if(allowedGroups!=null && allowedGroups.length>0){
			sql.append(" and gr.ic_group_id in ( ")
			.append(IDOUtil.getInstance().convertArrayToCommaseparatedString(allowedGroups))
			.append(" ) ");
		}
		
		//TODO use only allowed groups and g.ic_group_id in (view permission groups)
			
		return sql.toString();
	}

	/**
	 * 
	 * 
	 * @param streetName
	 * @return returns SelectQuery that returns the userIDs matching this streetName
	 */
	private SelectQuery getUserAddressSelectQuery(String streetName) throws IDOLookupException {
		Table addressTable = new Table(Address.class);
		Table userAddressTable = new Table(SQL_RELATION_ADDRESS);
		SelectQuery query = new SelectQuery(userAddressTable);	
		

		query.addColumn(userAddressTable,getIDColumnName(),true);
		
		IDOEntityDefinition addressDef = IDOLookup.getEntityDefinitionForClass(Address.class);
		IDOEntityField pkAddressField = null;
		try {
			pkAddressField = addressDef.getPrimaryKeyDefinition().getField();
		} catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
		}
		
		Criteria join = new JoinCriteria(new Column(addressTable,((pkAddressField==null)?"ic_address_id":pkAddressField.getSQLFieldName())),new Column(userAddressTable,((pkAddressField==null)?"ic_address_id":pkAddressField.getSQLFieldName())));
		if (streetName.indexOf(" ") == -1) {
		IDOEntityField addressField = addressDef.findFieldByUniqueName(Address.FIELD_STREET_NAME);
		Criteria match = new MatchCriteria(addressTable,addressField.getSQLFieldName(),MatchCriteria.LIKE,"%"+streetName.toUpperCase()+"%");

		query.addCriteria(new AND(join,match));
		} else {
			String streetNumber = streetName.substring(streetName.lastIndexOf(" ") + 1);
			streetName = streetName.substring(0, streetName.lastIndexOf(" "));
		
			IDOEntityField streetNameField = addressDef.findFieldByUniqueName(Address.FIELD_STREET_NAME);
			Criteria matchStreetName = new MatchCriteria(addressTable,streetNameField.getSQLFieldName(),MatchCriteria.LIKE,"%"+streetName.toUpperCase()+"%");
			query.addCriteria(new AND(join,matchStreetName));
			
			IDOEntityField streetNumberField = addressDef.findFieldByUniqueName(Address.FIELD_STREET_NUMBER);
			Criteria matchStreetNumber = new MatchCriteria(addressTable,streetNumberField.getSQLFieldName(),MatchCriteria.LIKE,streetNumber.toUpperCase()+"%");
			query.addCriteria(new AND(join,matchStreetNumber));
		}
		
//		sql.append("select ua.ic_user_id from ic_address a,ic_user_address ua where a.ic_address_id=ua.ic_address_id").append(" and a.street_name like '%").append(condition.toUpperCase()).append("%' ");
	
		return query;
		
	}
	


	public int ejbHomeGetUserCount() throws IDOException {
		//    String sqlQuery = "select count(*) from "+ this.getEntityName();
    IDOQuery query = idoQueryGetSelectCount();
    query.appendWhere();
    appendIsNotDeleted(query);
    return idoGetNumberOfRecords(query.toString());
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

  public Collection ejbFindUsers(String[] userIDs) throws FinderException {
  	IDOQuery query = idoQuery();
  	query
      .appendSelectAllFrom(this)
      .appendWhere()
      .append(getColumnNameUserID())
      .appendInArray(userIDs)
      .appendAnd();
    appendIsNotDeleted(query);
  	
  	return super.idoFindPKsBySQL(query.toString());
  }
  
  public Collection ejbFindUsersInQuery(IDOQuery query) throws FinderException {
  	IDOQuery sqlQuery = idoQuery();
		sqlQuery
      .appendSelectAllFrom(this)
      .appendWhere(getColumnNameUserID())
      .appendIn(query)
      .appendAnd();
    appendIsNotDeleted(sqlQuery);
		return super.idoFindPKsByQuery(sqlQuery);
  }
  
  /**
	 * Returns users within a given birth year range
	 *	 * @param minYear , the year on the format 'yyyy' 
	 * @param maxYear , the year on the format 'yyyy'  
	 * @return Collection
	 * @throws FinderException
	 */
   public Collection ejbFindUsersByYearOfBirth (int minYear, int maxYear)  throws FinderException {
        final IDOQuery  sql = idoQuery ();
        IWTimestamp minStamp = new IWTimestamp(1,1,minYear);
        IWTimestamp maxStamp = new IWTimestamp(31,12,maxYear);
        sql.appendSelectAllFrom(getTableName());
        sql.appendWhere(getColumnNameDateOfBirth());
        sql.append(" >= ");
        sql.appendWithinSingleQuotes(minStamp.toSQLDateString());
		sql.appendAnd();
		sql.append(getColumnNameDateOfBirth());
		sql.append(" <= ");
		sql.appendWithinSingleQuotes(maxStamp.toSQLDateString());
    sql.appendAnd();
    appendIsNotDeleted(sql);
        return idoFindIDsBySQL (sql.toString ());
    }
  
  public int ejbHomeGetCountByBirthYearAndCommune(int fromYear, int toYear, Commune commune) throws IDOException {
		IWTimestamp minStamp = new IWTimestamp(1, 1, fromYear);
		IWTimestamp maxStamp = new IWTimestamp(31, 12, toYear);

		Table user = new Table(User.class, "u");
		Table address = new Table(Address.class, "a");

		SelectQuery query = new SelectQuery(user);
		query.addColumn(new CountColumn(user, this.getIDColumnName()));
		query.addCriteria(new MatchCriteria(user.getColumn(getColumnNameDateOfBirth()), MatchCriteria.GREATEREQUAL, minStamp.toSQLDateString()));
		query.addCriteria(new MatchCriteria(user.getColumn(getColumnNameDateOfBirth()), MatchCriteria.LESSEQUAL, maxStamp.toSQLDateString()));
		if (commune != null) {
			try {
				query.addManyToManyJoin(user, address, "ua");
			}
			catch (IDORelationshipException ile) {
				throw new IDOException("Tables " + user.getName() + " and " + address.getName() + " don't have a relation.");
			}
			query.addCriteria(new MatchCriteria(address, AddressBMPBean.getColumnNameAddressTypeId(), MatchCriteria.EQUALS, 1));
			query.addCriteria(new MatchCriteria(address, "ic_commune_id", MatchCriteria.EQUALS, commune));
		}

		return idoGetNumberOfRecords(query);
	}
  
  public Collection ejbFindUsersByCreationTime(IWTimestamp firstCreationTime, IWTimestamp lastCreationTime) throws FinderException, IDOLookupException{
		try {
			IDOQuery query = idoQueryJointGroupQuery();					
			query.appendAnd();
			query.append(SQL_JOINT_VARIABLE_GROUP);
			query.append(".");
			query.append(GENERAL_GROUP_COLUMN_CREATED);
			query.appendGreaterThanOrEqualsSign();
			query.appendWithinSingleQuotes(firstCreationTime.toSQLString());
			
			query.appendAnd();
			query.append(SQL_JOINT_VARIABLE_GROUP);
			query.append(".");
			query.append(GENERAL_GROUP_COLUMN_CREATED);
			query.appendLessThanOrEqualsSign();
			query.appendWithinSingleQuotes(lastCreationTime.toSQLString());

			System.out.println("SQL -> "+this.getClass()+":"+query);
			// AND created > '18.11.2002 16:31:06'
			//select gr.created,usr.* from IC_USER usr, IC_GROUP gr WHERE usr.IC_USER_ID=gr.IC_GROUP_ID AND created > '18.11.2002 16:31:06' ORDER BY gr.CREATED  desc
					return idoFindPKsByQuery(query); 
		} catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
  }
  
  
  public Collection ejbFindByDateOfBirthAndGroupRelationInitiationTimeAndStatus(Date firstBirthDateInPeriode, Date lastBirthDateInPeriode, Group relatedGroup, Timestamp firstInitiationDateInPeriode, Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException{
  	//select usr.* from ic_user usr, ic_group_relation gr_rel where usr.date_of_birth >= '23.12.1898' and usr.date_of_birth <= '23.12.1920' and gr_rel.ic_group_id = 3 and gr_rel.related_ic_group_id=usr.ic_user_id and gr_rel.initiation_date >= '11.7.2002 15:17:39' and gr_rel.initiation_date <= '11.7.2002 15:17:40' and gr_rel.group_relation_status in ('ST_ACTIVE')
	try {
	  	//preparing
	  	
	  	IDOEntityDefinition grRelDef = IDOLookup.getEntityDefinitionForClass(GroupRelation.class);
		IDOEntityDefinition thisDef = this.getEntityDefinition();
	  	
	  	String[] tables = new String[2];
	  	String[] variables = new String[2];
	  	//table name
	  	tables[0] = thisDef.getSQLTableName();
		//	as variable
		variables[0] = "usr";
		//table name
	  	tables[1] = grRelDef.getSQLTableName();
		//	as variable
		variables[1] = "gr_rel";
	  	
	  	//constructing query
		IDOQuery query = idoQuery();
		//select
		query.appendSelect();
		query.append(variables[0]);
		query.append(".* ");
		//from
		query.appendFrom(tables,variables);
		//where
		query.appendWhere();
		query.append(variables[0]);
		query.append(".");
		query.append(getColumnNameDateOfBirth());
		query.appendGreaterThanOrEqualsSign();
		query.append(firstBirthDateInPeriode);
		//and
		query.appendAnd();
		query.append(variables[0]);
		query.append(".");
		query.append(getColumnNameDateOfBirth());
		query.appendLessThanOrEqualsSign();
		query.append(lastBirthDateInPeriode);
		//and
		query.appendAnd();
		query.append(variables[1]);
		query.append(".");
		query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_GROUP).getSQLFieldName());
		query.appendEqualSign();
		query.append(relatedGroup.getPrimaryKey());
		//and
		query.appendAnd();
		query.append(variables[1]);
		query.append(".");
		query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_RELATED_GROUP).getSQLFieldName());
		query.appendEqualSign();
		query.append(variables[0]);
		query.append(".");
		query.append(thisDef.getPrimaryKeyDefinition().getField().getSQLFieldName());
		//and
		String groupRelationColumnInitiationDate = grRelDef.findFieldByUniqueName(GroupRelation.FIELD_INITIATION_DATE).getSQLFieldName();
		query.appendAnd();
		query.append(variables[1]);
		query.append(".");
		query.append(groupRelationColumnInitiationDate);
		query.appendGreaterThanOrEqualsSign();
		query.append(firstInitiationDateInPeriode);
		//and
		query.appendAnd();
		query.append(variables[1]);
		query.append(".");
		query.append(groupRelationColumnInitiationDate);
		query.appendLessThanOrEqualsSign();
		query.append(lastInitiationDateInPeriode);
		
		//and if relationstatus
		if(relationStatus!= null){
			//and
			query.appendAnd();
			query.append(variables[1]);
			query.append(".");
			query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_STATUS).getSQLFieldName());
			query.appendInArrayWithSingleQuotes(relationStatus);		
		}

/*		
		//order by usr.last_name, usr.first_name, usr.middle_name
		String[] order = new String[3];
		order[0] = variables[0]+"."+thisDef.findFieldByUniqueName(User.FIELD_LAST_NAME).getSQLFieldName();
		order[1] = variables[0]+"."+thisDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
		order[2] = variables[0]+"."+thisDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
		query.appendOrderBy(order); 
*/	  	
		//orderby personal_id	
		query.appendOrderBy(variables[0]+"."+thisDef.findFieldByUniqueName(User.FIELD_PERSONAL_ID).getSQLFieldName()); 

		System.out.println("SQL -> "+this.getClass()+":"+query);
		return idoFindPKsByQuery(query); 
  	
	} catch (IDOCompositePrimaryKeyException e) {
		e.printStackTrace();
		return ListUtil.getEmptyList();
	}
  }
  
  
  
	public Collection ejbFindByGroupRelationInitiationTimeAndStatus(Group relatedGroup, Timestamp firstInitiationDateInPeriode, Timestamp lastInitiationDateInPeriode, String[] relationStatus) throws IDOLookupException, FinderException{
		//select usr.* from ic_user usr, ic_group_relation gr_rel where gr_rel.ic_group_id = 3 and gr_rel.related_ic_group_id=usr.ic_user_id and gr_rel.initiation_date >= '11.7.2002 15:17:39' and gr_rel.initiation_date <= '11.7.2002 15:17:40' and gr_rel.group_relation_status in ('ST_ACTIVE')
		try {
				//preparing
		  	
				IDOEntityDefinition grRelDef = IDOLookup.getEntityDefinitionForClass(GroupRelation.class);
			IDOEntityDefinition thisDef = this.getEntityDefinition();
		  	
				String[] tables = new String[2];
				String[] variables = new String[2];
				//table name
				tables[0] = thisDef.getSQLTableName();
			//	as variable
			variables[0] = "usr";
			//table name
				tables[1] = grRelDef.getSQLTableName();
			//	as variable
			variables[1] = "gr_rel";
		  	
				//constructing query
			IDOQuery query = idoQuery();
			//select
			query.appendSelect();
			query.append(variables[0]);
			query.append(".* ");
			//from
			query.appendFrom(tables,variables);
			//where
			query.appendWhere();
			query.append(variables[1]);
			query.append(".");
			query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_GROUP).getSQLFieldName());
			query.appendEqualSign();
			query.append(relatedGroup.getPrimaryKey());
			//and
			query.appendAnd();
			query.append(variables[1]);
			query.append(".");
			query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_RELATED_GROUP).getSQLFieldName());
			query.appendEqualSign();
			query.append(variables[0]);
			query.append(".");
			query.append(thisDef.getPrimaryKeyDefinition().getField().getSQLFieldName());
			//and
			String groupRelationColumnInitiationDate = grRelDef.findFieldByUniqueName(GroupRelation.FIELD_INITIATION_DATE).getSQLFieldName();
			query.appendAnd();
			query.append(variables[1]);
			query.append(".");
			query.append(groupRelationColumnInitiationDate);
			query.appendGreaterThanOrEqualsSign();
			query.append(firstInitiationDateInPeriode);
			//and
			query.appendAnd();
			query.append(variables[1]);
			query.append(".");
			query.append(groupRelationColumnInitiationDate);
			query.appendLessThanOrEqualsSign();
			query.append(lastInitiationDateInPeriode);
			
			//and if relationstatus
			if(relationStatus!= null){
				//and
				query.appendAnd();
				query.append(variables[1]);
				query.append(".");
				query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_STATUS).getSQLFieldName());
				query.appendInArrayWithSingleQuotes(relationStatus);		
			}
			
			//order by usr.last_name, usr.first_name, usr.middle_name
			String[] order = new String[3];
			order[0] = variables[0]+"."+thisDef.findFieldByUniqueName(User.FIELD_LAST_NAME).getSQLFieldName();
			order[1] = variables[0]+"."+thisDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
			order[2] = variables[0]+"."+thisDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
			query.appendOrderBy(order); 
		  	
			System.out.println("SQL -> "+this.getClass()+":"+query);
			return idoFindPKsByQuery(query); 
	  	
		} catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
	}
  
  public Collection ejbFindUsersBySpecificGroupsUserstatusDateOfBirthAndGender(Collection groups, Collection userStatuses, Integer yearOfBirthFrom, Integer yearOfBirthTo, String gender) throws FinderException {
      Table userTable = new Table(TABLE_NAME, "u");
	  Table userStatusTable = new Table(UserStatusBMPBean.ENTITY_NAME, "us");
	  Table statusTable = new Table(StatusBMPBean.ENTITY_NAME, "s");
	  
	  Table groupRelationSubTable = new Table(GroupRelationBMPBean.TABLE_NAME, "gr");
	  SelectQuery subQuery = new SelectQuery(groupRelationSubTable);
	  subQuery.addColumn(new Column(groupRelationSubTable, GroupRelationBMPBean.RELATED_GROUP_ID_COLUMN));
	  subQuery.addCriteria(new MatchCriteria(groupRelationSubTable, GroupRelationBMPBean.STATUS_COLUMN, MatchCriteria.EQUALS, GroupRelationBMPBean.STATUS_ACTIVE));
	  subQuery.addCriteria(new MatchCriteria(groupRelationSubTable, GroupRelationBMPBean.RELATIONSHIP_TYPE_COLUMN, MatchCriteria.EQUALS, RELATION_TYPE_GROUP_PARENT));

	  if (groups.size() < SUBLIST_SIZE) {
	  	subQuery.addCriteria(new InCriteria(groupRelationSubTable, GroupRelationBMPBean.GROUP_ID_COLUMN, groups));
	  } else {
		  int numberOfRounds = groups.size()/SUBLIST_SIZE;
		  List groupsList = (List)groups;
		  InCriteria firstInCriteria = new InCriteria(groupRelationSubTable, GroupRelationBMPBean.GROUP_ID_COLUMN, groupsList.subList(0,SUBLIST_SIZE));
		  InCriteria loopInCriteria = null;
		  OR orCriteria = null;
		  for (int i=0;i<numberOfRounds;i++){
		  	if (i==0) {
		  		loopInCriteria = new InCriteria(groupRelationSubTable, GroupRelationBMPBean.GROUP_ID_COLUMN, groupsList.subList(i*SUBLIST_SIZE,i*SUBLIST_SIZE+SUBLIST_SIZE));
		  		orCriteria = new OR(firstInCriteria,loopInCriteria);
		  	} else if (i>0) {
		  		loopInCriteria = new InCriteria(groupRelationSubTable, GroupRelationBMPBean.GROUP_ID_COLUMN, groupsList.subList(i*SUBLIST_SIZE,i*SUBLIST_SIZE+SUBLIST_SIZE));
		  		orCriteria = new OR(orCriteria,loopInCriteria);
		  	}
		  }
		  InCriteria lastInCriteria = new InCriteria(groupRelationSubTable, GroupRelationBMPBean.GROUP_ID_COLUMN, groupsList.subList(groupsList.size()-groupsList.size()%SUBLIST_SIZE,groupsList.size()));
		  orCriteria = new OR(orCriteria,lastInCriteria);
		  subQuery.addCriteria(orCriteria);
	  }
	  
	  SelectQuery query = new SelectQuery(userTable);
	  query.addColumn(new WildCardColumn(userTable));
	  query.addCriteria(new InCriteria(userTable, getColumnNameUserID(), subQuery));
	  SelectQuery statusSubQueryDeceased = null;
	  SelectQuery statusSubQuery = null;
	  if (userStatuses != null && !userStatuses.isEmpty()) {
		  if (userStatuses.contains(UserStatusBusinessBean.STATUS_DECEASED)) {
			  Table userStatusTableDeceased = new Table(UserStatusBMPBean.ENTITY_NAME, "us1");
			  Table statusTableDeceased = new Table(StatusBMPBean.ENTITY_NAME, "s1");
			  statusSubQueryDeceased = new SelectQuery(userStatusTableDeceased);
			  statusSubQueryDeceased.addColumn(userStatusTableDeceased,UserStatusBMPBean.IC_USER);
			  statusSubQueryDeceased.addJoin(userStatusTableDeceased, UserStatusBMPBean.STATUS_ID, statusTableDeceased, StatusBMPBean.ENTITY_NAME+"_id");
			  statusSubQueryDeceased.addCriteria(new MatchCriteria(userStatusTableDeceased,UserStatusBMPBean.DATE_TO,MatchCriteria.IS,MatchCriteria.NULL));
			  statusSubQueryDeceased.addCriteria(new MatchCriteria(statusTableDeceased, StatusBMPBean.STATUS_LOC_KEY, MatchCriteria.EQUALS, UserStatusBusinessBean.STATUS_DECEASED));
		  }
		  statusSubQuery = new SelectQuery(userStatusTable);
		  statusSubQuery.addColumn(userStatusTable,UserStatusBMPBean.IC_USER);
		  statusSubQuery.addJoin(userStatusTable, UserStatusBMPBean.STATUS_ID, statusTable, StatusBMPBean.ENTITY_NAME+"_id");
		  statusSubQuery.addCriteria(new MatchCriteria(userStatusTable,UserStatusBMPBean.DATE_TO,MatchCriteria.IS,MatchCriteria.NULL));
		  statusSubQuery.addCriteria(new InCriteria(userStatusTable, UserStatusBMPBean.IC_GROUP, groups));
		  if (userStatuses.size() == 1){
			  statusSubQuery.addCriteria(new MatchCriteria(statusTable, StatusBMPBean.STATUS_LOC_KEY, MatchCriteria.EQUALS, userStatuses.iterator().next().toString()));
	      } else {
	    	  statusSubQuery.addCriteria(new InCriteria(statusTable,StatusBMPBean.STATUS_LOC_KEY, userStatuses));
	      }  
	  }
	  if (statusSubQueryDeceased != null) {
		  InCriteria statusIn = new InCriteria(userTable, getColumnNameUserID(), statusSubQuery);
		  InCriteria statusInDeceased = new InCriteria(userTable, getColumnNameUserID(), statusSubQueryDeceased);
		  OR statusOR = new OR(statusIn,statusInDeceased);
		  query.addCriteria(statusOR);
	  } else  if (statusSubQuery != null) {
	  	query.addCriteria(new InCriteria(userTable, getColumnNameUserID(), statusSubQuery));
	  }
	  if (yearOfBirthFrom != null) {
	      IWTimestamp yearOfBirthFromStamp = new IWTimestamp(1,1,yearOfBirthFrom.intValue());
	      query.addCriteria(new MatchCriteria(userTable, getColumnNameDateOfBirth(),MatchCriteria.GREATEREQUAL, yearOfBirthFromStamp.getTimestamp()));
	  }
	  if (yearOfBirthTo != null) {
	      IWTimestamp yearOfBirthToStamp = new IWTimestamp(31,12,yearOfBirthTo.intValue());
	      query.addCriteria(new MatchCriteria(userTable, getColumnNameDateOfBirth(),MatchCriteria.LESSEQUAL, yearOfBirthToStamp.getTimestamp()));
	  }
	  if (gender != null) {
	      int genderNumber = -1;
	      if (gender.equals("m")) {
	          genderNumber = 1;
	      } else if (gender.equals("f")) {
	          genderNumber = 2;
	      }
	      query.addCriteria(new MatchCriteria(userTable, getColumnNameGender(),MatchCriteria.EQUALS, genderNumber));
	  }
//	  query.addOrder(groupTable, getColumnNameUserID(), true);
	  return idoFindPKsByQueryUsingLoadBalance(query, PREFETCH_SIZE);
//	  return idoFindPKsBySQL(query.toString());
  }
  
  
  /**
   * add condition if column deleted equals 'N' or is null
   * 
   * @param query
   */  
  private void appendIsNotDeleted(IDOQuery query) {
    query      
//			.append(getColumnNameDeleted()).appendNOTEqual().appendQuoted(GenericEntity.COLUMN_VALUE_TRUE);
      .appendLeftParenthesis()
      .appendEqualsQuoted(getColumnNameDeleted(), GenericEntity.COLUMN_VALUE_FALSE)
      .appendOr()
      .append(getColumnNameDeleted())
      .append(" IS NULL ")
      .appendRightParenthesis();
  }
  
  
  /**
   * add condition if column deleted equals 'N' or is null
   * 
   * @param query
   */  
  private Criteria getNotDeletedCriteria() {
  	MatchCriteria c1 = new MatchCriteria(idoQueryTable(),getColumnNameDeleted(),MatchCriteria.EQUALS,false);
  	MatchCriteria c2 = new MatchCriteria(idoQueryTable(),getColumnNameDeleted(),MatchCriteria.IS,MatchCriteria.NULL);
    return new OR(c1,c2);
  }
  
  
  /**
   * add condition if column deleted equals 'N' or is null
   * 
   * @param query
  */    
  private void appendIsNotDeleted(StringBuffer buffer)  {
    buffer
      .append('(')
      .append(getColumnNameDeleted())
      .append(" = '")
      .append(GenericEntity.COLUMN_VALUE_FALSE)
      .append("' OR ")
      .append(getColumnNameDeleted())
      .append(" IS NULL )");
  }
      
  /**
   * Unsupported.
   * 
   */    
  public void removeGroup(Group group, User currentUser)  {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported.
   * 
   */      
  public void removeGroup(int userId, User currentUser, boolean allEntries) {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Unsupported.
   * 
   */      
  public void removeGroup(User currentUser)  {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Unsupported.
   * 
   */      
  public void removeUser(User user, User currentUser) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported.
   * 
   */ 
  public void removeUser(User user, User currentUse, Timestamp time) {
	throw new UnsupportedOperationException();
  }

  /**
   * Unsupported.
   * 
   */ 
    public Collection getChildren() {
        throw new UnsupportedOperationException("Method getChildren() not implemented");
    }

	public Collection getAddresses(AddressType addressType) throws IDOLookupException, IDOCompositePrimaryKeyException, IDORelationshipException {
		String addressTypePrimaryKeyColumn = addressType.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName();
		
		IDOEntityDefinition addressDefinition = IDOLookup.getEntityDefinitionForClass(Address.class);
		String addressTableName = addressDefinition.getSQLTableName();
		String addressPrimaryKeyColumn = addressDefinition.getPrimaryKeyDefinition().getField().getSQLFieldName();
		String userAddressMiddleTableName = addressDefinition.getMiddleTableNameForRelation(getEntityName());
		
		IDOQuery query = idoQuery(); 
		query.appendSelect().append("a.").append(addressPrimaryKeyColumn).appendFrom().append(addressTableName).append(" a, ");
		query.append(userAddressMiddleTableName).append(" iua ").appendWhere();
		
		query.append("a.").append(addressPrimaryKeyColumn).appendEqualSign();
		query.append("iua.").append(addressPrimaryKeyColumn);
		
		query.appendAnd().append("a.");
		query.append(addressTypePrimaryKeyColumn).appendEqualSign();
		query.append(addressType.getPrimaryKey());
		
		query.appendAnd().append("iua.");
		query.append(getColumnNameUserID()).appendEqualSign().append(getPrimaryKey());

		return idoGetRelatedEntitiesBySQL(Address.class, query.toString());
	}

	
	/**
	 * Do not use this method, it is just here because User implements Group
	 */
	public void addGroup(int groupId, Timestamp time) throws EJBException {
	    throw new UnsupportedOperationException();
	}

	/**
	 * Do not use this method, it is just here because User implements Group
	 */
	public void addUniqueRelation(int relatedGroupId, String relationType, Timestamp time) throws CreateException {
	    throw new UnsupportedOperationException();
	}


	/**
	 * Do not use this method, it is just here because User implements Group
	 */
	public void removeGroup(int relatedGroupId, User currentUser, boolean AllEntries, Timestamp time) throws EJBException {
	    throw new UnsupportedOperationException();	
	}

	/**
  	* Finds all users that have duplicated emails
	*/
	public Collection ejbFindAllUsersWithDuplicatedEmails()throws FinderException{
// TODO Implement "HAVING" machanism in the SelectQuery structure 
//	    Table table = new Table(SQL_RELATION_EMAIL, "e") ;
//	    SelectQuery query = new SelectQuery(table);
//	    query.addColumn(new Column(getIDColumnName()));
//	    query.addGroupByColumn(getIDColumnName());
//	    query.addHavingColumn()
	    return this.idoFindPKsBySQL("select ic_user_id from ic_user_email group by ic_user_id having count(ic_user_id)>1");
	}

	/**
  	* Finds all users that have duplicated phones
	*/
	public Collection ejbFindAllUsersWithDuplicatedPhones(String phoneType)throws FinderException{
    	return this.idoFindPKsBySQL("select up.ic_user_id from ic_user_phone up, ic_phone p where up.ic_phone_id = p.ic_phone_id and p.ic_phone_type_id = " + phoneType + " group by ic_user_id having count(up.ic_user_id)>1");
	}
	
	public String getId(){
		return getPrimaryKey().toString();
	}
	
	
//	public boolean isDeceased() {
//		boolean isDeceased = false;
//		try {
//			UserStatus userStatus = getUserStatusBusiness().getDeceasedUserStatus((Integer)getPrimaryKey());
//			isDeceased = userStatus != null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//   	 	return isDeceased;
//	}

	public boolean isDeceased()  {
		Status deceasedStatus = null;
		try {
			deceasedStatus = getStatusHome().findByStatusKey(UserStatusBusinessBean.STATUS_DECEASED);
		}
		catch (FinderException e) {
			try {
				deceasedStatus = getStatusHome().create();
				deceasedStatus.setStatusKey(UserStatusBusinessBean.STATUS_DECEASED);
				deceasedStatus.store();
			}
			catch (CreateException e1) {
				e1.printStackTrace();
			}
		}
		Collection coll = null;
		try {
			coll = getUserStatusHome().findAllByUserIDAndStatusID((Integer)getPrimaryKey(),(Integer) deceasedStatus.getPrimaryKey());
		} 
		catch (FinderException e) {
			e.printStackTrace();
		}
		if (coll != null && !coll.isEmpty()) {
			System.out.println("User with SSN = " + getPersonalID() + " is deceased"); 
		} 
		return coll != null && !coll.isEmpty();
	}
	
		public void setUserProperties(ICFile file) {
		setColumn(COLUMN_NAME_USER_PROPERTIES_FILE_ID, file);
	}
	
	public ICFile getUserProperties() {
		return (ICFile) getColumnValue(COLUMN_NAME_USER_PROPERTIES_FILE_ID);
	}
	

	protected UserStatusHome getUserStatusHome(){
		UserStatusHome home = null;
		try {
		 home = (UserStatusHome) com.idega.data.IDOLookup.getHome(UserStatus.class);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		return home;
	}

	protected StatusHome getStatusHome(){
		StatusHome home = null;
		try {
		 home = (StatusHome) com.idega.data.IDOLookup.getHome(Status.class);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		return home;
	}
	
}
