package com.idega.core.data;

import java.sql.*;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PhoneType extends GenericType {

  public PhoneType() {
    super();
  }

  public PhoneType(int id)throws SQLException{
    super(id);
  }

  public String getEntityName() {
    return "IC_PHONE_TYPE";
  }

}