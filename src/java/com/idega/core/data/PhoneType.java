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

  public static final String UNIQUE_NAME_HOME_PHONE = "home_phone";
  public static final String UNIQUE_NAME_WORK_PHONE = "work_phone";
  public static final String UNIQUE_NAME_FAX_NUMBER = "fax_number";
  public static final String UNIQUE_NAME_MOBILE_PHONE = "mobile_phone";

  public PhoneType() {
    super();
  }

  public PhoneType(int id)throws SQLException{
    super(id);
  }

  public String getEntityName() {
    return "IC_PHONE_TYPE";
  }

  public void insertStartData() {
    try {
      PhoneType pt = new PhoneType();
        pt.setName("home");
        pt.setUniqueName(PhoneType.UNIQUE_NAME_HOME_PHONE);
      pt.insert();
      PhoneType pt1 = new PhoneType();
        pt1.setName("work");
        pt1.setUniqueName(PhoneType.UNIQUE_NAME_WORK_PHONE);
      pt1.insert();
      PhoneType pt2 = new PhoneType();
        pt2.setName("mobile");
        pt2.setUniqueName(PhoneType.UNIQUE_NAME_MOBILE_PHONE);
      pt2.insert();
      PhoneType pt3 = new PhoneType();
        pt3.setName("fax");
        pt3.setUniqueName(PhoneType.UNIQUE_NAME_FAX_NUMBER);
      pt3.insert();
    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

}