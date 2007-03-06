package com.idega.core.contact.data;

import java.sql.SQLException;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class PhoneTypeBMPBean extends com.idega.core.data.GenericTypeBMPBean implements com.idega.core.contact.data.PhoneType {

  public static final String UNIQUE_NAME_HOME_PHONE = "home_phone";
  public static final String UNIQUE_NAME_WORK_PHONE = "work_phone";
  public static final String UNIQUE_NAME_MOBILE_PHONE = "mobile_phone";
  public static final String UNIQUE_NAME_FAX_NUMBER = "fax_number";
  public static final int HOME_PHONE_ID = 1;
  public static final int WORK_PHONE_ID = 2;
  public static final int MOBILE_PHONE_ID = 3;
  public static final int FAX_NUMBER_ID = 4;

  public PhoneTypeBMPBean() {
    super();
  }

  public PhoneTypeBMPBean(int id)throws SQLException{
    super(id);
  }

  public String getEntityName() {
    return "IC_PHONE_TYPE";
  }

  public void insertStartData() {
    try {
      PhoneType pt = ((com.idega.core.contact.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy();
        pt.setID(com.idega.core.contact.data.PhoneTypeBMPBean.HOME_PHONE_ID);
        pt.setName("home");
        pt.setUniqueName(com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_HOME_PHONE);
      pt.insert();
      PhoneType pt1 = ((com.idega.core.contact.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy();
        pt1.setID(com.idega.core.contact.data.PhoneTypeBMPBean.WORK_PHONE_ID);
        pt1.setName("work");
        pt1.setUniqueName(com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_WORK_PHONE);
      pt1.insert();
      PhoneType pt2 = ((com.idega.core.contact.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy();
        pt2.setID(com.idega.core.contact.data.PhoneTypeBMPBean.MOBILE_PHONE_ID);
        pt2.setName("mobile");
        pt2.setUniqueName(com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_MOBILE_PHONE);
      pt2.insert();
      PhoneType pt3 = ((com.idega.core.contact.data.PhoneTypeHome)com.idega.data.IDOLookup.getHomeLegacy(PhoneType.class)).createLegacy();
        pt3.setID(com.idega.core.contact.data.PhoneTypeBMPBean.FAX_NUMBER_ID);
        pt3.setName("fax");
        pt3.setUniqueName(com.idega.core.contact.data.PhoneTypeBMPBean.UNIQUE_NAME_FAX_NUMBER);
      pt3.insert();
    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

}
