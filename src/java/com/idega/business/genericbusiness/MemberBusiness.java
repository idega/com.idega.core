package com.idega.business.genericbusiness;

import java.sql.SQLException;
import com.idega.data.genericentity.Member;
import com.idega.util.idegaTimestamp;

/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2000 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author
 * @version 1.0
 */

public class MemberBusiness {

  public MemberBusiness() {
  }


  public int insertMember(String firstname, String middlename, String lastname, String displayname, String description, String gender, idegaTimestamp date_of_birth) throws SQLException{
    Member memberToAdd = new Member();

    if(firstname != null){
      memberToAdd.setFirstName(firstname);
    }
    if(middlename != null){
      memberToAdd.setMiddleName(middlename);
    }
    if(lastname != null){
      memberToAdd.setLastName(lastname);
    }
    if(displayname != null){
//      memberToAdd.setDisplayName(displayname);
    }
    if(description != null){
//      memberToAdd.setDescription(description);
    }
    if(gender != null){
      memberToAdd.setGender(gender);
    }
    if(date_of_birth != null){
      memberToAdd.setDateOfBirth(date_of_birth.getSQLDate());
    }


    memberToAdd.insert();

    return memberToAdd.getID();

  }


  public void updateMember(int member_id, String firstname, String middlename, String lastname, String displayname, String description, String gender, idegaTimestamp date_of_birth) throws SQLException {
    Member memberToUpdate = new Member(member_id);

    if(firstname != null){
      memberToUpdate.setFirstName(firstname);
    }
    if(middlename != null){
      memberToUpdate.setMiddleName(middlename);
    }
    if(lastname != null){
      memberToUpdate.setLastName(lastname);
    }
    if(displayname != null){
//      memberToUpdate.setDisplayName(displayname);
    }
    if(description != null){
//      memberToUpdate.setDescription(description);
    }
    if(gender != null){
      memberToUpdate.setGender(gender);
    }
    if(date_of_birth != null){
      memberToUpdate.setDateOfBirth(date_of_birth.getSQLDate());
    }

    memberToUpdate.update();

  }



} // Class MemberBusiness