package com.idega.core.user.data;

import java.sql.SQLException;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PrimaryUserGroupBMPBean extends com.idega.core.data.GenericGroupBMPBean implements com.idega.core.user.data.PrimaryUserGroup {


  public PrimaryUserGroupBMPBean(){
    super();
  }

  public PrimaryUserGroupBMPBean(int id) throws SQLException {
    super(id);
  }


  public String getGroupTypeValue(){
    return "ic_primary_usergroup";
  }

}
