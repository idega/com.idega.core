package com.idega.core.user.business;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.data.GenericGroup;
import com.idega.core.location.data.Address;
import com.idega.core.user.data.Gender;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.data.EntityFinder;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLegacyEntity;
import com.idega.presentation.IWContext;
import com.idega.user.data.UserBMPBean;
import com.idega.util.IWTimestamp;
import com.idega.util.reflect.MethodInvoker;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2000 idega.is All Rights Reserved
 * Company:      idega margmi�lun
 * @author
 * @version 1.0
 */

public class UserBusiness {

  public UserBusiness() {
  }

  public static UserBusiness getInstance(){
    return new UserBusiness();
  }

  public User insertUser(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group) throws SQLException{
  	return insertUser(firstname,middlename,lastname,displayname,description,gender,date_of_birth,primary_group, null);
  }
  public User insertUser(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group,String personalID) throws SQLException{
    User userToAdd = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).createLegacy();

    if(firstname != null){
      userToAdd.setFirstName(firstname);
    }
    if(middlename != null){
      userToAdd.setMiddleName(middlename);
    }
    if(lastname != null){
      userToAdd.setLastName(lastname);
    }
    if(displayname != null){
      userToAdd.setDisplayName(displayname);
    }
    if(description != null){
      userToAdd.setDescription(description);
    }
    if(gender != null){
      userToAdd.setGender(gender);
    }

    if(date_of_birth != null){
      userToAdd.setDateOfBirth(date_of_birth.getSQLDate());
    }

    if(primary_group != null){
      userToAdd.setPrimaryGroupID(primary_group);
    }
    
    if(personalID!=null){
    	userToAdd.setPersonalID(personalID);
    }

    userToAdd.insert();


    UserGroupRepresentative group = ((com.idega.core.user.data.UserGroupRepresentativeHome)com.idega.data.IDOLookup.getHomeLegacy(UserGroupRepresentative.class)).createLegacy();
    group.setName(userToAdd.getName());
    group.setDescription("User representative in table ic_group");
    group.insert();

    userToAdd.setGroupID(group.getID());

    userToAdd.update();

    if(primary_group != null){
      GenericGroup prgr = ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(userToAdd.getPrimaryGroupID());
      prgr.addUser(userToAdd);
    }

    return userToAdd;

  }

/*
  public User getUser(int userGroupRepresentativeID) throws SQLException {
    List l = EntityFinder.findAllByColumn(com.idega.core.user.data.UserBMPBean.getStaticInstance(User.class),com.idega.core.user.data.UserBMPBean._COLUMNNAME_USER_GROUP_ID,userGroupRepresentativeID);
    if(l != null && l.size() > 0){
      return ((User)l.get(0));
    }
    return null;
  }

  public int getUserID(int userGroupRepresentativeID) throws SQLException {
    User user = getUser(userGroupRepresentativeID);
    if(user != null){
      return user.getID();
    }
    return -1;
  }
*/

  public static void deleteUser(int userId) throws SQLException {
    User delUser = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId);
    
    //Reflection workaround:
    try
	{
		MethodInvoker.getInstance().invokeStaticMethodWithOneIntParameter("com.idega.block.staff.business.StaffBusiness","delete",userId);
		//StaffBusiness.delete(userId);
	}
	catch (IllegalAccessException e1)
	{
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	catch (InvocationTargetException e1)
	{
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	catch (NoSuchMethodException e1)
	{
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	catch (ClassNotFoundException e1)
	{
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}


    //delUser.removeFrom(com.idega.core.data.GenericGroupBMPBean.getStaticInstance());
    int groupId =delUser.getGroupID();
    try {
      delUser.removeFrom(GenericEntity.getStaticInstance(Address.class));
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      delUser.removeFrom(GenericEntity.getStaticInstance(Email.class));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }
    try {
      delUser.removeFrom(GenericEntity.getStaticInstance(Phone.class));
    }
    catch (SQLException exc) {
      exc.printStackTrace();
    }

    LoginDBHandler.deleteUserLogin(userId);
    delUser.delete();

    UserGroupBusiness.deleteGroup(groupId);
  }

  public static void setPermissionGroup(User user, Integer primaryGroupId) throws SQLException {
    if(primaryGroupId != null){
      user.setPrimaryGroupID(primaryGroupId);
      user.update();
    }
  }

  /**
   * Male: M, male, 0
   * Female: F, female, 1
   */
  public static Integer getGenderId(String gender) throws Exception{
      String genderName = null;
      if(gender == "M" || gender == "male" || gender == "0" ){
        genderName = com.idega.core.user.data.GenderBMPBean.NAME_MALE;
      } else if(gender == "F" || gender == "female" || gender == "1" ){
        genderName = com.idega.core.user.data.GenderBMPBean.NAME_FEMALE;
      } else{
        //throw new RuntimeException("String gender must be: M, male, 0, F, female or 1 ");
        return null;
      }
      Gender g = (Gender) GenericEntity.getStaticInstance(Gender.class);
      String[] result = com.idega.data.SimpleQuerier.executeStringQuery("Select "+g.getIDColumnName()+" from "+g.getEntityName()+"where "+com.idega.core.user.data.GenderBMPBean.getNameColumnName()+" = '"+genderName+"'");

      if(result != null && result.length > 0){
        return new Integer(result[0]);
      } else {
        return null;
        //throw new RuntimeException("no result");
      }
  }

  public static Phone[] getUserPhones(int userId) {
    try {
      return (Phone[]) ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(GenericEntity.getStaticInstance(Phone.class));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static Phone getUserPhone(int userId, int phoneTypeId) {
    try {
      IDOLegacyEntity[] result = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(GenericEntity.getStaticInstance(Phone.class));
      if(result != null){
        for (int i = 0; i < result.length; i++) {
          if(((Phone)result[i]).getPhoneTypeId() == phoneTypeId){
            return (Phone)result[i];
          }
        }
      }
      return null;
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static Email getUserMail(int userId) {
    try {
      return getUserMail(((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static Email getUserMail(User user) {
    try {
      List L = EntityFinder.getInstance().findRelated(user,Email.class);
      if(L != null){
        if ( L.size() > 0 )
          return (Email)L.get(0);
      }
      return null;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber) throws SQLException {
    Phone phone = getUserPhone(userId,phoneTypeId);
    boolean insert = false;
    if ( phone == null ) {
      phone = ((com.idega.core.contact.data.PhoneHome)com.idega.data.IDOLookup.getHomeLegacy(Phone.class)).createLegacy();
      phone.setPhoneTypeId(phoneTypeId);
      insert = true;
    }

    if ( phoneNumber != null ) {
      phone.setNumber(phoneNumber);
    }

    if(insert){
      phone.insert();
      ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(phone);
    }
    else{
      phone.update();
    }
  }

  public void updateUserMail(int userId, String email) throws SQLException {
    Email mail = getUserMail(userId);
    boolean insert = false;
    if ( mail == null ) {
      mail = ((com.idega.core.contact.data.EmailHome)com.idega.data.IDOLookup.getHomeLegacy(Email.class)).createLegacy();
      insert = true;
    }

    if ( email != null ) {
      mail.setEmailAddress(email);
    }

    if(insert){
      mail.insert();
      ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(mail);
    }
    else{
      mail.update();
    }
  }

  public Address getUserAddress1(int userId) throws SQLException {
    IDOLegacyEntity[] result = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(GenericEntity.getStaticInstance(Address.class));
    if(result != null){
      int addrTypeId = com.idega.core.location.data.AddressTypeBMPBean.getId(com.idega.core.location.data.AddressTypeBMPBean.ADDRESS_1);
      for (int i = 0; i < result.length; i++) {
        if(((Address)result[i]).getAddressTypeID() == addrTypeId){
          return (Address)result[i];
        }
      }
    }
    return null;
  }

/**
 * @deprecated to be replaced or use AddressBusiness. eiki@idega.is
 */
  public void updateUserAddress1(int userId, String streetName, String streetNumber, String city, Integer postalCodeId, String province, Integer countryId, String pobox ) throws SQLException {
    Address addr = this.getUserAddress1(userId);
    boolean insert = false;
    if(addr == null){
      addr = ((com.idega.core.location.data.AddressHome)com.idega.data.IDOLookup.getHomeLegacy(Address.class)).createLegacy();
      addr.setAddressTypeID(com.idega.core.location.data.AddressTypeBMPBean.getId(com.idega.core.location.data.AddressTypeBMPBean.ADDRESS_1));
      insert = true;
    }


    if( city != null){
      addr.setCity(city);
    }

    if( countryId != null){
      addr.setCountryId(countryId.intValue());
    }

    if( pobox != null){
      addr.setPOBox(pobox);
    }

    if( postalCodeId != null){
      addr.setPostalCodeID(postalCodeId.intValue());
    }

    if( province != null){
      addr.setProvince(province);
    }

    if( streetName != null){
      addr.setStreetName(streetName);
    }

    if( streetNumber != null){
      addr.setStreetNumber(streetNumber);
    }


    if(insert){
      addr.insert();
      (((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId)).addTo(addr);
    }else{
      addr.update();
    }

  }

  public void updateUser(int user_id, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group ) throws SQLException {
    User userToUpdate = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(user_id);

    this.updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, date_of_birth, primary_group,null);

  }

	public void updateUser(int user_id, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String personalID ) throws SQLException {
		User userToUpdate = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(user_id);

		this.updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, date_of_birth, primary_group,personalID);

	}


  public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group ) throws SQLException {
		this.updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, date_of_birth, primary_group, null);

  }

	public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String personalID ) throws SQLException {

		if(firstname != null){
			userToUpdate.setFirstName(firstname);
		}
		if(middlename != null){
			userToUpdate.setMiddleName(middlename);
		}
		if(lastname != null){
			userToUpdate.setLastName(lastname);
		}
		if(displayname != null){
			userToUpdate.setDisplayName(displayname);
		}
		if(description != null){
			userToUpdate.setDescription(description);
		}
		if(gender != null){
			userToUpdate.setGender(gender);
		}
		if(date_of_birth != null){
			userToUpdate.setDateOfBirth(date_of_birth.getSQLDate());
		}

		if(primary_group != null){
			userToUpdate.setPrimaryGroupID(primary_group);
		}
		
		if (personalID != null)
			userToUpdate.setPersonalID(personalID);

		userToUpdate.update();

	}


  public static List listOfUserEmails(int iUserId){
    StringBuffer sql = new StringBuffer("select ie.* ");
    sql.append("from ic_email ie,ic_user_email iue ");
    sql.append("where ie.ic_email_id = iue.ic_email_address ");
    sql.append(" and iue.ic_user_id = ");
    sql.append(iUserId);
    Email eEmail = ((com.idega.core.contact.data.EmailHome)com.idega.data.IDOLookup.getHomeLegacy(Email.class)).createLegacy();
    try {
      return com.idega.data.EntityFinder.findAll(eEmail,sql.toString());
    }
    catch (SQLException ex) {

    }
    return null;
  }
  public static void addNewUserEmail(int iUserId,String sNewEmailAddress){
    try {
      Email eEmail = lookupEmail(sNewEmailAddress);
      if(eEmail==null){
        eEmail = ((com.idega.core.contact.data.EmailHome)com.idega.data.IDOLookup.getHomeLegacy(Email.class)).createLegacy();
        eEmail.setEmailAddress(sNewEmailAddress);
        eEmail.insert();
      }

      eEmail.addTo(User.class,iUserId);

    }
    catch (SQLException ex) {

    }
  }

  public static Email lookupEmail(String EmailAddress){
    try {
      //EntityFinder.debug = true;
      java.util.List c = EntityFinder.getInstance().findAllByColumn(Email.class,com.idega.core.contact.data.EmailBMPBean.getColumnNameAddress(),EmailAddress);
      //EntityFinder.debug = false;
      if(c!=null && c.size() > 0)
        return (Email) c.get(0);
    }
    catch (Exception ex) {

    }
    return null;
  }

  /**
   * @deprecated use  getUserGroupsDirectlyRelated(int iUserId)
   */
  public static List listOfUserGroups(int iUserId){
      return  getUserGroupsDirectlyRelated(iUserId);
  }


  public static List getUserGroups(int iUserId)throws SQLException{
    try {
      return getUserGroups(((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId).getGroupID());
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUsersInGroup(int iGroupId) {
    try {
      return UserGroupBusiness.getUsersContained(iGroupId);
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUsersInGroup(GenericGroup group) {
    try {
      return UserGroupBusiness.getUsersContained(group);  //EntityFinder.findRelated(group,com.idega.core.user.data.UserBMPBean.getStaticInstance());
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }


  public static List getUsers() throws SQLException  {
    List l = EntityFinder.findAll(GenericEntity.getStaticInstance(User.class));
    return l;
  }



  /**
   *  Returns User from userid, null if not found
   */
  public static User getUser(int iUserId){
    try {
      return ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId);
    }
    catch (Exception ex) {

    }
    return null;
  }

  	/**
	 *  Returns User from personal id returns null if not found
	 */
	public  User getUser(String personalID) {
		try {
			return ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPersonalID(personalID);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

  public static List getUsersInNoGroup() throws SQLException  {
    //return EntityFinder.findNonRelated(com.idega.core.data.GenericGroupBMPBean.getStaticInstance(),com.idega.core.user.data.UserBMPBean.getStaticInstance());

    List nonrelated = EntityFinder.findNonRelated(com.idega.core.data.GenericGroupBMPBean.getStaticInstance(),com.idega.core.data.GenericGroupBMPBean.getStaticInstance());

    return UserGroupBusiness.getUsersForUserRepresentativeGroups(nonrelated);
  }

  public static List getUserGroupsDirectlyRelated(int iUserId){
    try {
      return getUserGroupsDirectlyRelated(((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUsersInPrimaryGroup(GenericGroup group){
    try {
      return EntityFinder.findAllByColumn(GenericEntity.getStaticInstance(User.class),UserBMPBean._COLUMNNAME_PRIMARY_GROUP_ID,group.getID());
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUserGroupsDirectlyRelated(User user){
    //try {
      return UserGroupBusiness.getGroupsContainingDirectlyRelated(user.getGroupID()); //  EntityFinder.findRelated(user,com.idega.core.data.GenericGroupBMPBean.getStaticInstance());
    /*}
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }*/
  }

  public static List getUserGroupsNotDirectlyRelated(int iUserId){
    try {
      User user = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId);
      /*List isDirectlyRelated = getUserGroupsDirectlyRelated(user);
      List AllRelatedGroups = getUserGroups(user);

      if(AllRelatedGroups != null){
        if(isDirectlyRelated != null){
          Iterator iter = isDirectlyRelated.iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            AllRelatedGroups.remove(item);
            //while(AllRelatedGroups.remove(item)){}
          }
        }
        return AllRelatedGroups;
      }else {
        return null;
      }
      */
      return UserGroupBusiness.getGroupsContainingNotDirectlyRelated(user.getGroupID());
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getAllGroupsNotDirectlyRelated(int iUserId,IWContext iwc){
    try {

      User user = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId);
      /*List isDirectlyRelated = getUserGroupsDirectlyRelated(user);
      List AllGroups = UserGroupBusiness.getAllGroups(); //EntityFinder.findAll(com.idega.core.data.GenericGroupBMPBean.getStaticInstance());

      if(AllGroups != null){
        if(isDirectlyRelated != null){
          Iterator iter = isDirectlyRelated.iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            AllGroups.remove(item);
            //while(AllGroups.remove(item)){}
          }
        }
        return AllGroups;
      }else{
        return null;
      }
      */
      return UserGroupBusiness.getAllGroupsNotDirectlyRelated(user.getGroupID(),iwc);
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }


  public static List getUserGroups(User user) throws SQLException{
    //String[] groupTypesToReturn = new String[2];

    //groupTypesToReturn[0] = com.idega.core.data.GenericGroupBMPBean.getStaticInstance().getGroupTypeValue();
    //groupTypesToReturn[1] = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
    return UserBusiness.getUserGroups(user,null,false);
  }

  /**
   * @todo replace ((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(user.getGroupID()) by user.getGroupID()
   */
  public static List getUserGroups(User user, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException{
    return UserGroupBusiness.getGroupsContaining(((com.idega.core.data.GenericGroupHome)com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(user.getGroupID()),groupTypes, returnSepcifiedGroupTypes);
  }




} // Class UserBusiness
