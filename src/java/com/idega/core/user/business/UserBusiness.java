package com.idega.core.user.business;

import java.sql.SQLException;
import com.idega.core.user.data.User;
import com.idega.core.user.data.Gender;
import com.idega.util.idegaTimestamp;
import java.util.List;
import com.idega.core.data.Email;
import com.idega.core.data.GenericGroup;
import com.idega.data.EntityFinder;
import java.sql.Connection;
import java.sql.Statement;

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

    return userToAdd;

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

  public static List listOfUserGroups(int iUserId){
    try {
      return new User(iUserId).getAllGroups();
    }
    catch (SQLException ex) {
      return null;
    }
  }

} // Class UserBusiness