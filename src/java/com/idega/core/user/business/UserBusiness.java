package com.idega.core.user.business;

import java.sql.SQLException;
import com.idega.core.user.data.*;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.data.*;
import com.idega.util.idegaTimestamp;
import java.util.List;
import com.idega.core.data.Email;
import com.idega.core.data.GenericGroup;
import com.idega.data.EntityFinder;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import com.idega.data.GenericEntity;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2000 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author
 * @version 1.0
 */

public class UserBusiness {

  public UserBusiness() {
  }




  public User insertUser(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, idegaTimestamp date_of_birth) throws SQLException{
    User userToAdd = new User();

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



    userToAdd.insert();

    /*
    UserGroupRepresentative group = new UserGroupRepresentative();
    group.setName(userToAdd.getName());
    group.setDescription("User representive in table ic_group");
    group.insert();

    userToAdd.setGroupID(group.getID());

    userToAdd.update();
*/
    return userToAdd;

  }


  public static void deleteUser(int userId) throws SQLException {
    User delUser = new User(userId);

    delUser.removeFrom(GenericGroup.getStaticInstance());
    delUser.removeFrom((Address)Address.getStaticInstance(Address.class));
    delUser.removeFrom((Email)Email.getStaticInstance(Email.class));
    delUser.removeFrom((Phone)Phone.getStaticInstance(Phone.class));

    LoginDBHandler.deleteUserLogin(userId);

    delUser.delete();

  }



  /**
   * Male: M, male, 0
   * Female: F, female, 1
   */
  public static Integer getGenderId(String gender) throws Exception{
      String genderName = null;
      if(gender == "M" || gender == "male" || gender == "0" ){
        genderName = Gender.NAME_MALE;
      } else if(gender == "F" || gender == "female" || gender == "1" ){
        genderName = Gender.NAME_FEMALE;
      } else{
        //throw new RuntimeException("String gender must be: M, male, 0, F, female or 1 ");
        return null;
      }
      Gender g = (Gender)Gender.getStaticInstance(Gender.class);
      String[] result = com.idega.data.SimpleQuerier.executeStringQuery("Select "+g.getIDColumnName()+" from "+g.getEntityName()+"where "+Gender.getNameColumnName()+" = '"+genderName+"'");

      if(result != null && result.length > 0){
        return new Integer(result[0]);
      } else {
        return null;
        //throw new RuntimeException("no result");
      }
  }

  public Phone[] getUserPhones(int userId) {
    try {
      return (Phone[]) new User(userId).findRelated(Phone.getStaticInstance(Phone.class));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public Phone getUserPhone(int userId, int phoneTypeId) {
    try {
      GenericEntity[] result = new User(userId).findRelated(Phone.getStaticInstance(Phone.class));
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

  public Email getUserMail(int userId) {
    try {
      GenericEntity[] result = new User(userId).findRelated(Email.getStaticInstance(Email.class));
      if(result != null){
        if ( result.length > 0 )
          return (Email)result[0];
      }
      return null;
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber) throws SQLException {
    Phone phone = getUserPhone(userId,phoneTypeId);
    boolean insert = false;
    if ( phone == null ) {
      phone = new Phone();
      phone.setPhoneTypeId(phoneTypeId);
      insert = true;
    }

    if ( phoneNumber != null ) {
      phone.setNumber(phoneNumber);
    }

    if(insert){
      phone.insert();
      new User(userId).addTo(phone);
    }
    else{
      phone.update();
    }
  }

  public void updateUserMail(int userId, String email) throws SQLException {
    Email mail = getUserMail(userId);
    boolean insert = false;
    if ( mail == null ) {
      mail = new Email();
      insert = true;
    }

    if ( email != null ) {
      mail.setEmailAddress(email);
    }

    if(insert){
      mail.insert();
      new User(userId).addTo(mail);
    }
    else{
      mail.update();
    }
  }

  public Address getUserAddress1(int userId) throws SQLException {
    GenericEntity[] result = new User(userId).findRelated(Address.getStaticInstance(Address.class));
    if(result != null){
      int addrTypeId = AddressType.getId(AddressType.ADDRESS_1);
      for (int i = 0; i < result.length; i++) {
        if(((Address)result[i]).getAddressTypeID() == addrTypeId){
          return (Address)result[i];
        }
      }
    }
    return null;
  }


  public void updateUserAddress1(int userId, String streetName, String streetNumber, String city, Integer postalCodeId, String providence, Integer countryId, String pobox ) throws SQLException {
    Address addr = this.getUserAddress1(userId);
    boolean insert = false;
    if(addr == null){
      addr = new Address();
      addr.setAddressTypeID(AddressType.getId(AddressType.ADDRESS_1));
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

    if( providence != null){
      addr.setProvidence(providence);
    }

    if( streetName != null){
      addr.setStreetName(streetName);
    }

    if( streetNumber != null){
      addr.setStreetNumber(streetNumber);
    }


    if(insert){
      addr.insert();
      (new User(userId)).addTo(addr);
    }else{
      addr.update();
    }

  }


  public void updateUser(int user_id, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, idegaTimestamp date_of_birth) throws SQLException {
    User userToUpdate = new User(user_id);

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

    userToUpdate.update();

  }

  public static List listOfUserEmails(int iUserId){
    StringBuffer sql = new StringBuffer("select ie.* ");
    sql.append("from ic_email ie,ic_user_email iue ");
    sql.append("where ie.ic_email_id = iue.ic_email_address ");
    sql.append(" and iue.ic_user_id = ");
    sql.append(iUserId);
    Email eEmail = new Email();
    try {
      return com.idega.data.EntityFinder.findAll(eEmail,sql.toString());
    }
    catch (SQLException ex) {
      return null;
    }
  }
  public static void addNewUserEmail(int iUserId,String sNewEmailAddress){
    Connection conn= null;
    Statement Stmt= null;
    try {
      Email eEmail = new Email();
      eEmail.setEmailAddress(sNewEmailAddress);
      eEmail.insert();
      eEmail.getID();

      conn = com.idega.util.database.ConnectionBroker.getConnection();
      Stmt = conn.createStatement();
      Stmt.executeUpdate("insert into IC_USER_EMAIL (IC_USER_ID, IC_EMAIL_ADDRESS) values("+String.valueOf(iUserId)+","+String.valueOf(eEmail.getID())+")");

    }
    catch (SQLException ex) {
      ex.printStackTrace();
    }
    finally{
      if(Stmt != null){
        try{
        Stmt.close();
        }
        catch(SQLException ex){}
      }
      if (conn != null){
        com.idega.util.database.ConnectionBroker.freeConnection(conn);
      }
    }
  }

  public static List listOfGroups(){
    try {
      return EntityFinder.findAll(new GenericGroup());
    }
    catch (SQLException ex) {
      return null;
    }
  }


  /**
   * @deprecated use  getUserGroupsDirectlyRelated(int iUserId)
   */
  public static List listOfUserGroups(int iUserId){
      return  getUserGroupsDirectlyRelated(iUserId);
  }


  public static List getUserGroups(int iUserId)throws SQLException{
    try {
      return getUserGroups(new User(iUserId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUsersInGroup(int iGroupId) {
    try {
      return getUsersInGroup(new GenericGroup(iGroupId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUsersInGroup(GenericGroup group) {
    try {
      return EntityFinder.findRelated(group,User.getStaticInstance());
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUsersInNoGroup() {
    return EntityFinder.findNonRelated(GenericGroup.getStaticInstance(),User.getStaticInstance());
  }

  public static List getUserGroupsDirectlyRelated(int iUserId){
    try {
      return getUserGroupsDirectlyRelated(new User(iUserId));
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUserGroupsDirectlyRelated(User user){
    try {
      return EntityFinder.findRelated(user,GenericGroup.getStaticInstance());
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getUserGroupsNotDirectlyRelated(int iUserId){
    try {
      User user = new User(iUserId);
      List isDirectlyRelated = getUserGroupsDirectlyRelated(user);
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
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List getAllGroupsNotDirectlyRelated(int iUserId){
    try {
      User user = new User(iUserId);
      List isDirectlyRelated = getUserGroupsDirectlyRelated(user);
      List AllGroups = EntityFinder.findAll(GenericGroup.getStaticInstance());

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
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }



  /**
   * @todo change implementation. Use EntityFinder not GenericEntity
   */
  public static List getUserGroups(User user) throws SQLException{
      GenericGroup[] groups = GenericGroup.getStaticInstance().getAllGroupsContainingUser(user);

      if (groups != null && groups.length > 0){
        Hashtable GroupsContained = new Hashtable();

        String key = "";
        for (int i = 0; i < groups.length; i++) {
          key = Integer.toString(groups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,groups[i]);
            putGroupsContaining( (GenericGroup)groups[i], GroupsContained );
          }
        }


        Vector  groupVector = new Vector();
        Enumeration e;
        int i = 0;
        for ( e = (Enumeration)GroupsContained.elements(); e.hasMoreElements();){
          GenericGroup tempObj = (GenericGroup)e.nextElement();
          if (!tempObj.getGroupType().equals(((UserGroupRepresentative)UserGroupRepresentative.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue()))
            groupVector.add(i++, tempObj);
        }

        //return (PermissionGroup[])groupVector.toArray((Object[])new PermissionGroup[0]);
        return groupVector;
      }else{
        return null;
      }
    }

  /**
   * @todo change implementation. Use EntityFinder not GenericEntity
   */
    private static void putGroupsContaining(GenericGroup group, Hashtable GroupsContained ) throws SQLException{
      GenericGroup[] pGroups = group.getAllGroupsContainingThis();
      if (pGroups != null){
        String key = "";
        for (int i = 0; i < pGroups.length; i++) {
          key = Integer.toString(pGroups[i].getID());
          if(!GroupsContained.containsKey(key)){
            GroupsContained.put(key,pGroups[i]);
            putGroupsContaining((GenericGroup)pGroups[i], GroupsContained);
          }
        }
      }
    }



} // Class UserBusiness