package com.idega.core.data;

import com.idega.data.*;
import java.sql.SQLException;
import com.idega.core.user.data.User;


/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class Email extends GenericEntity {

  public Email(){
    super();
  }

  public Email(int id)throws SQLException{
    super(id);
  }


  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameAddress(),"Email address",true,true,String.class,255);
    addManyToOneRelationship(getColumnNameEmailTypeId(),"Type",EmailType.class);
    this.addManyToManyRelationShip(User.class,"ic_user_email");
  }

  public String getEntityName() {
    return "ic_email";
  }

  public static String getColumnNameAddress(){return "ADDRESS";}
  public static String getColumnNameEmailTypeId(){return "ic_email_type_id";}

  public void setEmailAddress(String address){
    setColumn(getColumnNameAddress(),address);
  }
  public String getEmailAddress(){
    return getStringColumnValue(getColumnNameAddress());
  }
  public void setEmailTypeId(int id){
    setColumn(getColumnNameEmailTypeId(),id);
  }
  public int getEmailTypeId(){
    return getIntColumnValue(getColumnNameEmailTypeId());
  }

}