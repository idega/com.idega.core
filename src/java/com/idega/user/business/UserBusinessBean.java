package com.idega.user.business;

import com.idega.builder.data.IBDomain;
import java.sql.SQLException;
import com.idega.user.data.*;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.data.*;
import com.idega.util.IWTimestamp;
import com.idega.util.text.Name;

import java.util.Collection;
import com.idega.core.data.Email;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.presentation.DataEmailer;

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
import com.idega.core.accesscontrol.data.LoginTable;

import com.idega.data.*;

import javax.ejb.*;
import javax.transaction.*;
import com.idega.business.*;

import java.rmi.RemoteException;

 /**
  * <p>Title: idegaWeb</p>
  * <p>Description: </p>
  * <p>Copyright: Copyright (c) 2002</p>
  * <p>Company: idega Software</p>
  * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
  * @version 1.0
  */

public class UserBusinessBean extends com.idega.business.IBOServiceBean implements UserBusiness{

  private GroupHome groupHome;
  private UserHome userHome;
  private UserGroupRepresentativeHome userRepHome;

  private EmailHome emailHome;
  private AddressHome addressHome;
  private PhoneHome phoneHome;

  private Gender male,female;

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

//  public UserGroupRepresentativeHome getUserGroupRepresentativeHome(){
//    if(userRepHome==null){
//      try{
//        userRepHome = (UserGroupRepresentativeHome)IDOLookup.getHome(UserGroupRepresentative.class);
//      }
//      catch(RemoteException rme){
//        throw new RuntimeException(rme.getMessage());
//      }
//    }
//    return userRepHome;
//  }

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

  /**
   * @deprecated replaced with createUser
   */
  public User insertUser(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,displayname,null,description,gender,date_of_birth,primary_group);
  }
  
  /**
 * Method createUserByPersonalIDIfDoesNotExist does what is says.
 * @param fullName
 * @param personalID
 * @param gender
 * @param dateOfBirth
 * @return User
 * @throws CreateException
 * @throws RemoteException
 */
  public User createUserByPersonalIDIfDoesNotExist(String fullName,String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException,RemoteException{
    User user;
    try{
      user = getUserHome().findByPersonalID(personalID);
      user.setFullName(fullName);
      
      user.setGender( (Integer)gender.getPrimaryKey() );
      user.setDateOfBirth(dateOfBirth.getDate());
      user.store();
    }
    catch(FinderException ex){
      Name name = new Name(fullName);
    		
      user = createUser(name.getFirstName(), name.getMiddleName() , name.getLastName() , personalID, gender, dateOfBirth);
    }

    return user;
  }
  
/**
 * Method createUserByPersonalIDIfDoesNotExist does what is says.
 * @param firstName
 * @param middleName
 * @param lastName
 * @param personalID
 * @param gender
 * @param dateOfBirth
 * @return User
 * @throws CreateException
 * @throws RemoteException
 */
  public User createUserByPersonalIDIfDoesNotExist(String firstName, String middleName, String lastName,String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException,RemoteException{
    User user;
    StringBuffer fullName = new StringBuffer();

	  firstName = (firstName==null) ? "" : firstName;
	  middleName = (middleName==null) ? "" : middleName;
	  lastName = (lastName==null) ? "" : lastName;
	
	  fullName.append(firstName).append(" ").append(middleName).append(" ").append(lastName);

      user = createUserByPersonalIDIfDoesNotExist(fullName.toString(),personalID,gender,dateOfBirth);

    return user;
  }
  


  public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group) throws CreateException,RemoteException{
    try{
      User userToAdd = getUserHome().create();

       /**
       * @todo Using setFullName in stead
       * is this ok and should it be done also in updateUser (I don´t think so)?
       * @modified by Eirikur Hrafnsson
       */

      StringBuffer fullName = new StringBuffer();

      firstName = (firstName==null) ? "" : firstName;
      middleName = (middleName==null) ? "" : middleName;
      lastName = (lastName==null) ? "" : lastName;

      fullName.append(firstName).append(" ").append(middleName).append(" ").append(lastName);

      userToAdd.setFullName(fullName.toString());

      /*if(firstname != null){
        userToAdd.setFirstName(firstname);
      }
      if(middlename != null){
        userToAdd.setMiddleName(middlename);
      }
      if(lastname != null){
        userToAdd.setLastName(lastname);
      }*/


      if(displayname != null){
        userToAdd.setDisplayName(displayname);
      }
      if(description != null){
        userToAdd.setDescription(description);
      }
      if(personalID!=null){
        userToAdd.setPersonalID(personalID);
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
      setUserUnderDomain(this.getIWApplicationContext().getDomain(), userToAdd, (GroupDomainRelationType)null);
  //    UserGroupRepresentative group = (UserGroupRepresentative)this.getUserGroupRepresentativeHome().create();
  //    group.setName(userToAdd.getName());
  //    group.setDescription("User representative in table ic_group");
  //    group.store();
  //    userToAdd.setGroup(group);
  //    userToAdd.store();

      if(primary_group != null){
        Group prgr = userToAdd.getPrimaryGroup();
        prgr.addGroup(userToAdd);
      }
      return userToAdd;
    }
    catch(Exception e){
      throw new IDOCreateException(e);
    }

  }


  public void setUserUnderDomain(IBDomain domain, User user, GroupDomainRelationType type) throws CreateException,RemoteException{
    GroupDomainRelation relation = (GroupDomainRelation)IDOLookup.create(GroupDomainRelation.class);
    relation.setDomain(domain);
    relation.setRelatedUser(user);

    if(type != null){
      relation.setRelationship(type);
    }

    relation.store();
  }

  /**
   * Generates a login for a user with a random password and a login derived from the users name (or random login if all possible logins are taken)
   */
  public LoginTable generateUserLogin(int userID)throws Exception{
    //return this.generateUserLogin(userID);
    return LoginDBHandler.generateUserLogin(userID);
  }

  /**
   * Generates a login for a user with a random password and a login derived from the users name (or random login if all possible logins are taken)
   */
  public LoginTable generateUserLogin(User user)throws Exception{
    //return LoginDBHandler.generateUserLogin(user);
    int userID = ((Integer)user.getPrimaryKey()).intValue();
    return this.generateUserLogin(userID);
  }

  /**
   * Creates a user with a firstname,middlename, lastname, where middlename can be null
   */
  public User createUser(String firstname, String middlename, String lastname) throws CreateException,RemoteException{
    return createUser(firstname,middlename,lastname,(String)null);
  }

  /**
   * Creates a new user with a firstname,middlename, lastname and personalID where middlename and personalID can be null
   */
  public User createUser(String firstname, String middlename, String lastname,String personalID) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,null,personalID,null,null,null,null);
  }
  /**
   * Creates a new user with a firstname,middlename, lastname and primaryGroupID where middlename can be null
   */
   public User createUser(String firstName, String middleName, String lastName, int primary_groupID) throws CreateException,RemoteException{
 		return createUser(firstName,middleName,lastName,null,null,null,null,null,new Integer(primary_groupID));
  }
  
  /**
   * Creates a new user with a firstname,middlename, lastname and primaryGroupID where middlename can be null but primary_group can not be noull
   */
  public User createUser(String firstName, String middleName, String lastName, Group primary_group) throws CreateException,RemoteException{
 		return createUser(firstName,middleName,lastName,null,null,null,null,null,(Integer)primary_group.getPrimaryKey());
  }

  /**
   * Creates a new user with a firstname,middlename, lastname ,personalID and gender where middlename and personalID can be null
   */
  public User createUser(String firstname, String middlename, String lastname,String personalID, Gender gender) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,null,personalID,null,(Integer)gender.getPrimaryKey(),null,null);
  }

  /**
   * Creates a new user with a firstname,middlename, lastname ,personalID, gender and date of birth where middlename,personalID,gender,dateofbirth can be null
   */
  public User createUser(String firstname, String middlename, String lastname,String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,null,personalID,null,(Integer)gender.getPrimaryKey(),dateOfBirth,null);
  }


  public User createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType) throws CreateException{
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
        try{
          transaction.rollback();
        }
        catch(SystemException se){}
        throw new CreateException(e.getMessage());
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

      this.getGroupBusiness().deleteGroup(groupId);
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
  public Integer getGenderId(String gender) throws Exception{
    try{
      GenderHome home = (GenderHome) this.getIDOHome(Gender.class);

      if(gender == "M" || gender == "male" || gender == "0" ){
        if(male == null){
          male = home.getMaleGender();
        }
        return (Integer) male.getPrimaryKey();
      }
      else if(gender == "F" || gender == "female" || gender == "1" ){
        if(female == null){
          female = home.getFemaleGender();
        }
        return (Integer) female.getPrimaryKey();
      }
      else{
        //throw new RuntimeException("String gender must be: M, male, 0, F, female or 1 ");
        return null;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }

  }

  public  Phone[] getUserPhones(int userId)throws RemoteException{
    try {
      Collection phones = this.getUser(userId).getPhones();
//	  if(phones != null){
        return (Phone[])phones.toArray(new Phone[phones.size()]);
//	  }
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

  /**
   * @depricated Use getUsersMainAddress(user) instead.
   */
  public Address getUserAddress1(int userId) throws EJBException,RemoteException{
    AddressType addressType1 = this.getAddressHome().getAddressType1();
    User user = this.getUser(userId);
    if( user!=null ){
      return this.getUsersMainAddress(user);
    }
   else return null;
  }

  /**
   * Gets the users main address and returns it.
   * @returns the address if found or null if not.
   */
  public Address getUsersMainAddress(User user) throws RemoteException{
    AddressType addressType1 = this.getAddressHome().getAddressType1();
    Collection addresses = user.getAddresses();
    if(addresses != null){
      Iterator iter = addresses.iterator();
      while (iter.hasNext()) {
        Address item = (Address)iter.next();
        if(item.getAddressType().equals(addressType1))
          return item;
      }
    }
    return null;
  }



  public void updateUserAddress1(int userId, String streetName, String streetNumber, String city, Integer postalCodeId, String province, Integer countryId, String pobox ) throws CreateException,RemoteException {
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

    if( province != null){
      addr.setProvince(province);
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

  public void updateUser(int user_id, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group ) throws EJBException,RemoteException {
    User userToUpdate = this.getUser(user_id);
    this.updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID, date_of_birth, primary_group);
  }

  public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group ) throws EJBException,RemoteException {

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
    if (personalID != null){
    	userToUpdate.setPersonalID(personalID);
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
      return getGroupBusiness().getUsersContained(iGroupId);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUsersInGroup(Group group) {
    try {
      return this.getGroupBusiness().getUsersContained(group);  //EntityFinder.findRelated(group,com.idega.user.data.UserBMPBean.getStaticInstance());
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
   *  Returns User from userid, throws EJBException if not found
   */
  public  User getUser(int iUserId){
    try {
      return getUserHome().findByPrimaryKey(new Integer(iUserId));
    }
    catch (Exception ex) {
      throw new EJBException("Error getting user for id: "+iUserId+" Message: "+ex.getMessage());
    }
    //return null;
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
      return getGroupBusiness().getGroupsContainingDirectlyRelated(user.getGroupID()); //  EntityFinder.findRelated(user,com.idega.user.data.GroupBMPBean.getStaticInstance());
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
      return this.getGroupBusiness().getGroupsContainingNotDirectlyRelated(user.getGroupID());
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
      return getGroupBusiness().getAllGroupsNotDirectlyRelated(user.getGroupID(),iwc);
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
    return getGroupBusiness().getGroupsContaining(user.getGroup(),groupTypes, returnSepcifiedGroupTypes);
  }


  public GroupBusiness getGroupBusiness()throws RemoteException{
    return (GroupBusiness) IBOLookup.getServiceInstance(this.getIWApplicationContext(),GroupBusiness.class);
  }

  public Collection getAllUsersOrderedByFirstName()throws FinderException,RemoteException{
    return this.getUserHome().findAllUsersOrderedByFirstName(); 
  }
  
  
  
	public Email getUsersMainEmail(User user)throws NoEmailFoundException{
		String userString = null;
		try{
			userString = user.getName();
			Collection collection = user.getEmails();
			for (Iterator iterator = collection.iterator(); iterator.hasNext();)
			{
				Email element = (Email) iterator.next();
				return element;
			}
		}
		catch(Exception e){
		}
		throw new NoEmailFoundException(userString);
	}

	public Phone getUsersHomePhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersHomePhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersWorkPhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersWorkPhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersMobilePhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersMobilePhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersFaxPhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersFaxPhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}


/**
 * @return Correct name of the group or user or null if there was an error getting the name.
 * Gets the name of the group and explicitely checks if the "groupOrUser" and if it is a user it 
 * returns the correct name of the user. Else it regularely returns the name of the group.
 **/
  public String getNameOfGroupOrUser(Group groupOrUser){
  	try{
  		String userGroupType=getUserHome().getGroupType();
  		if(groupOrUser.getGroupType().equals(userGroupType)){
  			int userID = ((Integer)groupOrUser.getPrimaryKey()).intValue();
  			return getUser(userID).getName();
  		}
  		else{
  			return groupOrUser.getName();	
  		}
  	}
  	catch(Exception e){
  		return "";
  	}
  }


} // Class UserBusiness
