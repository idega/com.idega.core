package com.idega.core.contact.data;

import java.sql.SQLException;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class EmailTypeBMPBean extends com.idega.core.data.GenericTypeBMPBean implements com.idega.core.contact.data.EmailType {

  public EmailTypeBMPBean(){
    super();
  }

  public EmailTypeBMPBean(int id)throws SQLException{
    super(id);
  }

  public String getEntityName() {
    return "ic_email_type";
  }

}
