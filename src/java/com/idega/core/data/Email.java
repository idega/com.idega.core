package com.idega.core.data;

import com.idega.data.*;
import java.sql.SQLException;


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
    this.addAttribute(getColumnNameAddress(),"Netfang",true,true,String.class,255);
    this.addAttribute(getColumnNameEmailTypeId(),"Gerð",true,true,Integer.class,"many-to-one",EmailType.class);
  }

  public String getEntityName() {
    return "ic_email";
  }

  public static String getColumnNameAddress(){return "email_address";}
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