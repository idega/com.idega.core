package com.idega.core.user.data;

import java.sql.Date;
import java.sql.SQLException;

import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.data.GenericGroup;
import com.idega.core.location.data.Address;
import com.idega.core.file.data.ICFile;
import com.idega.data.GenericEntity;

/**
 * This bean is an implementation which is backwards compatible with the old User system and structure.
 * This bean is used as an implemention of User when the application attribute IW_USER_SYSTEM is set to OLD.
 * <br><br>Copyright (c) 2001-2003
 * Company:      idega.is
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class OldUserBMPBean extends com.idega.data.GenericEntity implements com.idega.core.user.data.User {

	private static String sClassName = User.class.getName();

	public OldUserBMPBean() {
		super();
	}

	public OldUserBMPBean(int id) throws SQLException {
		super(id);
	}

	public String getEntityName() {
		return "ic_user";
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());

		addAttribute(getColumnNameFirstName(), "First name", true, true, java.lang.String.class);
		addAttribute(getColumnNameMiddleName(), "Middle name", true, true, java.lang.String.class);
		addAttribute(getColumnNameLastName(), "Last name", true, true, java.lang.String.class);
		addAttribute(getColumnNameDisplayName(), "Display name", true, true, java.lang.String.class);
		addAttribute(getColumnNameDescription(), "Description", true, true, java.lang.String.class);
		addAttribute(getColumnNameDateOfBirth(), "Birth date", true, true, java.sql.Date.class);
		addManyToOneRelationship(getColumnNameGender(), "Gender", com.idega.core.user.data.Gender.class);
		addOneToOneRelationship(getColumnNameSystemImage(), "Image", ICFile.class);
		addOneToOneRelationship(_COLUMNNAME_USER_GROUP_ID, "User", GenericGroup.class);
		addOneToOneRelationship(_COLUMNNAME_PRIMARY_GROUP_ID, "Primary group", GenericGroup.class);
		this.addManyToManyRelationShip(Address.class, "ic_user_address");
		this.addManyToManyRelationShip(Phone.class, "ic_user_phone");
		this.addManyToManyRelationShip(Email.class, "ic_user_email");
		this.setNullable(getColumnNameSystemImage(), true);
		this.setNullable(_COLUMNNAME_PRIMARY_GROUP_ID, true);
		//temp
		this.addManyToManyRelationShip(GenericGroup.class, "ic_group_user");
		addAttribute(getColumnNamePersonalID(), "Personal ID", true, true, String.class, 20);

	}

	public void setDefaultValues() {
	}

	public void insertStartData() throws SQLException {

	}

	public String getIDColumnName() {
		return getColumnNameUserID();
	}

	public static User getStaticInstance() {
		return (User) GenericEntity.getStaticInstance(sClassName);
	}

	public static String getAdminDefaultName() {
		return "Administrator";
	}

	/*  ColumNames begin   */

	public static String getColumnNameUserID() {
		return "ic_user_id";
	}
	public static String getColumnNameFirstName() {
		return "first_name";
	}
	public static String getColumnNameMiddleName() {
		return "middle_name";
	}
	public static String getColumnNameLastName() {
		return "last_name";
	}
	public static String getColumnNameDisplayName() {
		return "display_name";
	}
	public static String getColumnNameDescription() {
		return "description";
	}
	public static String getColumnNameDateOfBirth() {
		return "date_of_birth";
	}
	public static String getColumnNameGender() {
		return "ic_gender_id";
	}
	public static String getColumnNameSystemImage() {
		return "system_image_id";
	}
	public static final String _COLUMNNAME_USER_GROUP_ID = "user_representative";
	public static final String _COLUMNNAME_PRIMARY_GROUP_ID = "primary_group";
	/*  ColumNames end   */

	/*  Getters begin   */

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

	public int getGroupID() {
		return getIntColumnValue(_COLUMNNAME_USER_GROUP_ID);
	}

	public int getPrimaryGroupID() {
		return getIntColumnValue(_COLUMNNAME_PRIMARY_GROUP_ID);
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

	/*  Getters end   */

	/*  Setters begin   */

	public void setFirstName(String fName) {
		if (!com.idega.core.accesscontrol.business.AccessControl.isValidUsersFirstName(fName)) {
			fName = "Invalid firstname";
		}
		if (com.idega.core.accesscontrol.business.AccessControl.isValidUsersFirstName(this.getFirstName())) { // if not Administrator
			setColumn(getColumnNameFirstName(), fName);
		}

	}

	public void setMiddleName(String mName) {
		setColumn(getColumnNameMiddleName(), mName);
	}

	public void setLastName(String lName) {
		setColumn(getColumnNameLastName(), lName);
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

	public void setGroupID(int icGroupId) {
		setColumn(_COLUMNNAME_USER_GROUP_ID, icGroupId);
	}

	public void setPrimaryGroupID(int icGroupId) {
		setColumn(_COLUMNNAME_PRIMARY_GROUP_ID, icGroupId);
	}

	public void setPrimaryGroupID(Integer icGroupId) {
		setColumn(_COLUMNNAME_PRIMARY_GROUP_ID, icGroupId);
	}

	public static String getColumnNamePersonalID() {
		return "PERSONAL_ID";
	}
	
	public String getPersonalID() {
		return getStringColumnValue(getColumnNamePersonalID());
	}
	
	public void setPersonalID(String personalId) {
		setColumn(getColumnNamePersonalID(), personalId);
	}

	public Integer ejbFindUserFromEmail(String emailAddress) throws javax.ejb.FinderException {
		StringBuffer sql = new StringBuffer("select iu.* ");
		sql.append("from ic_email ie,ic_user_email iue,ic_user iu ");
		sql.append("where ie.ic_email_id = iue.ic_email_address ");
		sql.append("and iue.ic_user_id = iu.ic_user_id ");
		sql.append(" and ie.address = '");
		sql.append(emailAddress);
		sql.append("'");
		java.util.Collection coll = super.idoFindIDsBySQL(sql.toString());
		if (!coll.isEmpty())
			return (Integer) coll.iterator().next();
		else
			throw new javax.ejb.FinderException("No user found");
	}
	
	public Integer ejbFindByPersonalID(String personalId) throws javax.ejb.FinderException {
		StringBuffer sql = new StringBuffer("select * from ic_user where ");
		sql.append(getColumnNamePersonalID());
		sql.append(" = '");
		sql.append(personalId);
		sql.append("'");
		java.util.Collection coll = super.idoFindIDsBySQL(sql.toString());
		if (!coll.isEmpty())
			return (Integer) coll.iterator().next();
		else
			throw new javax.ejb.FinderException("No user found");
	}
	
	
	public boolean equals(Object o ){
		if(o instanceof com.idega.core.user.data.User){
			com.idega.core.user.data.User user = (com.idega.core.user.data.User)o;
			Object thisPK = this.getPrimaryKey();
			if(thisPK!=null){
				return thisPK.equals(user.getPrimaryKey());
			}
		}
		else if(o instanceof com.idega.user.data.User){
			com.idega.user.data.User user = (com.idega.user.data.User)o;
			Object thisPK = this.getPrimaryKey();
			if(thisPK!=null){
				return thisPK.equals(user.getPrimaryKey());
			}
		}
		return false;
	}
}