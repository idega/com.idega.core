package com.idega.user.data;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class UserGroupRepresentativeBMPBean extends com.idega.user.data.GroupBMPBean implements com.idega.user.data.UserGroupRepresentative {



  public String getGroupTypeValue(){
    return "ic_user_representative";
  }


  public static String getClassName(){
    return UserGroupRepresentative.class.getName();
  }

  protected boolean identicalGroupExistsInDatabase() throws Exception {
    return false;
  }


  public String ejbHomeGetGroupType(){
   return this.getGroupTypeValue();
  }
}
