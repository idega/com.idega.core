package com.idega.core.user.business;

import java.sql.SQLException;
import com.idega.core.user.data.User;
import com.idega.util.idegaTimestamp;

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



} // Class UserBusiness