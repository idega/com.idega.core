package com.idega.user.business;

import java.sql.SQLException;
import com.idega.user.data.*;
import com.idega.user.business.UserGroupBusiness;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.data.*;
import com.idega.util.idegaTimestamp;
import java.util.Collection;
import com.idega.core.data.Email;
import com.idega.user.data.Group;
import com.idega.data.EntityFinder;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import com.idega.data.IDOLegacyEntity;
import com.idega.presentation.IWContext;
import com.idega.block.staff.business.StaffBusiness;

import com.idega.data.*;

import javax.ejb.*;
import javax.transaction.*;
import com.idega.business.*;

import java.rmi.RemoteException;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2000 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author
 * @version 1.0
 */

public class UserBusinessBean extends com.idega.business.IBOServiceBean implements UserBusiness{

  private GroupHome groupHome;
  private UserHome userHome;
  private UserGroupRepresentativeHome userRepHome;

  private EmailHome emailHome;
  private AddressHome addressHome;
  private PhoneHome phoneHome;

  public UserBusinessBean() {
  }

  public UserHome getUserHome(){
    if(userHome==null){
      try{
        userHome = (UserHome)IDOLookup.getHome(User.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return userHome;
  }

  public UserGroupRepresentativeHome getUserGroupRepresentativeHome(){
    if(userRepHome==null){
      try{
        userRepHome = (UserGroupRepresentativeHome)IDOLookup.getHome(UserGroupRepresentative.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return userRepHome;
  }

  public GroupHome getGroupHome(){
    if(groupHome==null){
      try{
        groupHome = (GroupHome)IDOLookup.getHome(Group.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return groupHome;
  }



  public EmailHome getEmailHome(){
    if(emailHome==null){
      try{
        emailHome = (EmailHome)IDOLookup.getHome(Email.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return emailHome;
  }


  public AddressHome getAddressHome(){
    if(addressHome==null){
      try{
        addressHome = (AddressHome)IDOLookup.getHome(Address.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return addressHome;
  }


  public PhoneHome getPhoneHome(){
    if(phoneHome==null){
      try{
        phoneHome = (PhoneHome)IDOLookup.getHome(Phone.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return phoneHome;
  }

  public User insertUser(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, idegaTimestamp date_of_birth, Integer primary_group) throws CreateException,RemoteException{
    User userToAdd = getUserHome().create();

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

    userToAdd.store();


    UserGroupRepresentative group = this.getUserGroupRepresentativeHome().create();
    group.setName(userToAdd.getName());
    group.setDescription("User representative in table ic_group");
    group.store();

    userToAdd.setGroup(group);

    userToAdd.store();

    if(primary_group != null){
      Group prgr = userToAdd.getPrimaryGroup();
      prgr.addUser(userToAdd);
    }

    return userToAdd;

  }

  public User createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, idegaTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, idegaTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType) throws CreateException{
      UserTransaction transaction = this.getSessionContext().getUserTransaction();
      try{
        transaction.begin();
        User newUser;
        newUser = insertUser(firstname,middlename, lastname,null,null,null,null,primary_group);

        LoginDBHandler.createLogin(newUser,userLogin,password,accountEnabled,modified,
                                 daysOfValidity,passwordExpires,userAllowedToChangePassw,changeNextTime,encryptionType);
        transaction.commit();
        return newUser;
      }
      catch(Exception e){
        throw new CreateException(e.getMessage());
      }
      finally{
        try{
          transaction.rollback();
        }
        catch(SystemException se){}
      }
  }

/*
  public User getUser(int userGroupRepresentativeID) throws SQLException {
    List l = EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(User.class),com.idega.user.data.UserBMPBean._COLUMNNAME_USER_GROUP_ID,userGroupRepresentativeID);
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

  public  void deleteUser(int userId) throws RemoveException {
      try{
      User delUser = getUser(userId);

      /**
       * @todo: Reenable
       */
      //StaffBusiness.delete(userId);

      //delUser.removeFrom(com.idega.user.data.GroupBMPBean.getStaticInstance());
      int groupId =delUser.getGroupID();
      /*try {
        delUser.removeFrom((Address)com.idega.core.data.AddressBMPBean.getStaticInstance(Address.class));
      }
      catch (SQLException e) {
        e.printStackTrace();
      }
      try {
        delUser.removeFrom((Email)com.idega.core.data.EmailBMPBean.getStaticInstance(Email.class));
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
      try {
        delUser.removeFrom((Phone)com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
      }
      catch (SQLException exc) {
        exc.printStackTrace();
      }*/
      delUser.removeAllAddresses();
      delUser.removeAllEmails();
      delUser.removeAllPhones();

      LoginDBHandler.deleteUserLogin(userId);
      delUser.remove();

      this.getUserGroupBusiness().deleteGroup(groupId);
    }
    catch(Exception e){
      throw new RemoveException(e.getMessage());
    }
  }

  public  void setPermissionGroup(User user, Integer primaryGroupId) throws IDOStoreException,RemoteException {
    if(primaryGroupId != null){
      user.setPrimaryGroupID(primaryGroupId);
      user.store();
    }
  }

  /**
   * Male: M, male, 0
   * Female: F, female, 1
   */
  public  Integer getGenderId(String gender) throws Exception{
      String genderName = null;
      if(gender == "M" || gender == "male" || gender == "0" ){
        genderName = com.idega.user.data.GenderBMPBean.NAME_MALE;
      } else if(gender == "F" || gender == "female" || gender == "1" ){
        genderName = com.idega.user.data.GenderBMPBean.NAME_FEMALE;
      } else{
        //throw new RuntimeException("String gender must be: M, male, 0, F, female or 1 ");
        return null;
      }
      Gender g = (Gender)com.idega.user.data.GenderBMPBean.getStaticInstance(Gender.class);
      String[] result = com.idega.data.SimpleQuerier.executeStringQuery("Select "+g.getIDColumnName()+" from "+g.getEntityName()+"where "+com.idega.user.data.GenderBMPBean.getNameColumnName()+" = '"+genderName+"'");

      if(result != null && result.length > 0){
        return new Integer(result[0]);
      } else {
        return null;
        //throw new RuntimeException("no result");
      }
  }

  public  Phone[] getUserPhones(int userId)throws RemoteException{
    try {
      Collection phones = this.getUser(userId).getPhones();
      return (Phone[])phones.toArray();
      //return (Phone[]) ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
    }
    catch (EJBException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Phone getUserPhone(int userId, int phoneTypeId)throws RemoteException{
    try {
      Phone[] result = this.getUserPhones(userId);
      //IDOLegacyEntity[] result = ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
      if(result != null){
        for (int i = 0; i < result.length; i++) {
          if(((Phone)result[i]).getPhoneTypeId() == phoneTypeId){
            return (Phone)result[i];
          }
        }
      }
      return null;
    }
    catch (EJBException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Email getUserMail(int userId) {
    return getUserMail(this.getUser(userId));
  }

  public  Email getUserMail(User user) {
    try {
      Collection L = user.getEmails();
      if(L != null){
        if ( ! L.isEmpty() )
          return (Email)L.iterator().next();
      }
      return null;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber) throws EJBException {
    try{
    Phone phone = getUserPhone(userId,phoneTypeId);
    boolean insert = false;
    if ( phone == null ) {
      phone = this.getPhoneHome().create();
      phone.setPhoneTypeId(phoneTypeId);
      insert = true;
    }

    if ( phoneNumber != null ) {
      phone.setNumber(phoneNumber);
    }

    phone.store();
    if(insert){
      //((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(phone);
      this.getUser(userId).addPhone(phone);
    }

    }
    catch(Exception e){
      e.printStackTrace();
      throw new EJBException(e.getMessage());
    }


  }

  public void updateUserMail(int userId, String email) throws CreateException,RemoteException {
    Email mail = getUserMail(userId);
    boolean insert = false;
    if ( mail == null ) {
      mail = this.getEmailHome().create();
      insert = true;
    }

    if ( email != null ) {
      mail.setEmailAddress(email);
    }
    mail.store();
    if(insert){
      //((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(mail);
      try{
       this.getUser(userId).addEmail(mail);
      }
      catch(Exception e){
        throw new RemoteException(e.getMessage());
      }
    }

  }

  public Address getUserAddress1(int userId) throws EJBException,RemoteException{
    AddressType addressType1 = this.getAddressHome().getAddressType1();
    //IDOLegacyEntity[] result = ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.AddressBMPBean.getStaticInstance(Address.class));
    Collection addresses = this.getUser(userId).getAddresses();
    if(addresses != null){
      /*int addrTypeId = com.idega.core.data.AddressTypeBMPBean.getId(com.idega.core.data.AddressTypeBMPBean.ADDRESS_1);
      for (int i = 0; i < result.length; i++) {
        if(((Address)result[i]).getAddressTypeID() == addrTypeId){
          return (Address)result[i];
        }
      }*/
      Iterator iter = addresses.iterator();
      while (iter.hasNext()) {
        Address item = (Address)iter.next();
        if(item.getAddressType().equals(addressType1))
          return item;
      }
    }
    return null;
  }


  public void updateUserAddress1(int userId, String streetName, String streetNumber, String city, Integer postalCodeId, String providence, Integer countryId, String pobox ) throws CreateException,RemoteException {
    Address addr = this.getUserAddress1(userId);
    boolean insert = false;
    if(addr == null){
      //addr = ((com.idega.core.data.AddressHome)com.idega.data.IDOLookup.getHomeLegacy(Address.class)).createLegacy();
      addr = getAddressHome().create();
      addr.setAddressType(getAddressHome().getAddressType1());
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


    addr.store();
    if(insert){
      //(((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId)).addTo(addr);
      try{
        this.getUser(userId).addAddress(addr);
      }
      catch(Exception e){
        throw new RemoteException(e.getMessage());
      }
    }
  }

  public void updateUser(int user_id, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, idegaTimestamp date_of_birth, Integer primary_group ) throws EJBException,RemoteException {
    User userToUpdate = this.getUser(user_id);
    this.updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, date_of_birth, primary_group);
  }

  public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, idegaTimestamp date_of_birth, Integer primary_group ) throws EJBException,RemoteException {

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

    userToUpdate.store();

  }

  /**
   * Returns null if no emails found for user
   */
  public Collection listOfUserEmails(int iUserId){
    try {
      return this.getEmailHome().findEmailsForUser(iUserId);
    }
    catch (Exception ex) {

    }
    return null;
  }

  public void addNewUserEmail(int iUserId,String sNewEmailAddress){
    try {
      Email eEmail = lookupEmail(sNewEmailAddress);
      if(eEmail==null){
        eEmail = this.getEmailHome().create();
        eEmail.setEmailAddress(sNewEmailAddress);
        eEmail.store();
      }
      User U = getUser(iUserId);
      U.addEmail(eEmail);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public  Email lookupEmail(String EmailAddress){
    try {
      //EntityFinder.debug = true;
      //java.util.Collection c = EntityFinder.getInstance().findAllByColumn(Email.class,com.idega.core.data.EmailBMPBean.getColumnNameAddress(),EmailAddress);
      Email email = getEmailHome().findEmailByAddress(EmailAddress);
      return email;
    }
    catch (Exception ex) {

    }
    return null;
  }

  /**
   * @deprecated use  getUserGroupsDirectlyRelated(int iUserId)
   */
  public  Collection listOfUserGroups(int iUserId){
      return  getUserGroupsDirectlyRelated(iUserId);
  }


  public Collection getUserGroups(int iUserId)throws EJBException{
    try {
      return getUserGroups(this.getUser(iUserId));
      //return getUserGroups(((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId).getGroupID());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUsersInGroup(int iGroupId) {
    try {
      return getUserGroupBusiness().getUsersContained(iGroupId);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUsersInGroup(Group group) {
    try {
      return this.getUserGroupBusiness().getUsersContained(group);  //EntityFinder.findRelated(group,com.idega.user.data.UserBMPBean.getStaticInstance());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


  public  Collection getUsers() throws FinderException,RemoteException{
    //Collection l = EntityFinder.findAll(com.idega.user.data.UserBMPBean.getStaticInstance());
    Collection l = this.getUserHome().findAllUsers();
    return l;
  }



  /**
   *  Returns User from userid, null if not found
   */
  public  User getUser(int iUserId){
    try {
      return getUserHome().findByPrimaryKey(new Integer(iUserId));
    }
    catch (Exception ex) {

    }
    return null;
  }



  public  Collection getUsersInNoGroup() throws SQLException  {
    //return EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean.getStaticInstance());
    //Collection nonrelated = EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getStaticInstance());
    //return UserGroupBusiness.getUsersForUserRepresentativeGroups(nonrelated);
    throw new java.lang.UnsupportedOperationException("method getUsersInNoGroup() not implemented");
  }

  public  Collection getUserGroupsDirectlyRelated(int iUserId){
    try {
      return getUserGroupsDirectlyRelated(this.getUser(iUserId));
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUsersInPrimaryGroup(Group group){
    try {
      //return EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean._COLUMNNAME_PRIMARY_GROUP_ID,group.getID());
      return this.getUserHome().findUsersInPrimaryGroup(group);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUserGroupsDirectlyRelated(User user){
    try {
      return getUserGroupBusiness().getGroupsContainingDirectlyRelated(user.getGroupID()); //  EntityFinder.findRelated(user,com.idega.user.data.GroupBMPBean.getStaticInstance());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUserGroupsNotDirectlyRelated(int iUserId){
    try {
      User user = this.getUser(iUserId);
      /*Collection isDirectlyRelated = getUserGroupsDirectlyRelated(user);
      Collection AllRelatedGroups = getUserGroups(user);

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
      return this.getUserGroupBusiness().getGroupsContainingNotDirectlyRelated(user.getGroupID());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getAllGroupsNotDirectlyRelated(int iUserId,IWContext iwc){
    try {

      User user = getUser(iUserId);
      /*Collection isDirectlyRelated = getUserGroupsDirectlyRelated(user);
      Collection AllGroups = UserGroupBusiness.getAllGroups(); //EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getStaticInstance());

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
      return getUserGroupBusiness().getAllGroupsNotDirectlyRelated(user.getGroupID(),iwc);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


  public  Collection getUserGroups(User user) throws RemoteException{
    //String[] groupTypesToReturn = new String[2];

    //groupTypesToReturn[0] = com.idega.user.data.GroupBMPBean.getStaticInstance().getGroupTypeValue();
    //groupTypesToReturn[1] = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
    return getUserGroups(user,null,false);
  }

  /**
   * @todo replace ((com.idega.user.data.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).findByPrimaryKeyLegacy(user.getGroupID()) by user.getGroupID()
   */
  public  Collection getUserGroups(User user, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws RemoteException{
    return getUserGroupBusiness().getGroupsContaining(user.getGroup(),groupTypes, returnSepcifiedGroupTypes);
  }


  public UserGroupBusiness getUserGroupBusiness()throws RemoteException{
    return (UserGroupBusiness) IBOLookup.getServiceInstance(null,UserGroupBusiness.class);
  }

  public Collection getAllUsersOrderedByFirstName()throws FinderException,RemoteException{
    return this.getUserHome().findAllUsersOrderedByFirstName();
  }



} // Class UserBusiness
