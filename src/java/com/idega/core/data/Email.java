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
    this.addManyToManyRelationShip(EmailType.class, "ic_email_email_type");
  }

  public String getEntityName() {
    return "ic_email";
  }

  public static String getColumnNameAddress(){return "email_address";}

}