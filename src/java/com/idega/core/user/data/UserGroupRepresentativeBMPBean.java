package com.idega.core.user.data;

import java.sql.SQLException;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class UserGroupRepresentativeBMPBean extends com.idega.core.data.GenericGroupBMPBean implements com.idega.core.user.data.UserGroupRepresentative {

  public UserGroupRepresentativeBMPBean() {
    super();
  }

  public UserGroupRepresentativeBMPBean(int id) throws SQLException  {
    super(id);
  }


  public String getGroupTypeValue(){
    return "ic_user_representative";
  }


  public static String getClassName(){
    return UserGroupRepresentative.class.getName();
  }

  protected boolean identicalGroupExistsInDatabase() throws Exception {
    return false;
  }

}
