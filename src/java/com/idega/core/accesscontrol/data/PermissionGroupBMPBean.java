package com.idega.core.accesscontrol.data;


/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmidlun
 * @author
 * @version 1.1
 * @deprecated All hardcoded group "type" classes should be avoided. Just use a regular com.idega.user.data.Group and set its grouptype.
 */

public class PermissionGroupBMPBean extends com.idega.user.data.GroupBMPBean implements com.idega.core.accesscontrol.data.PermissionGroup {

  public String getGroupTypeValue(){

    return "permission";

  }

  public static String getClassName(){

    return "com.idega.core.accesscontrol.data.PermissionGroup";

  }

  /**
   * ONLY FOR BACKWARD COMPATABILTY ISSUES WITH ACCESSCONTROL
   */
  public void setID(int id) {
	setColumn(getIDColumnName(), new Integer(id));
  }

  /**
   * ONLY FOR BACKWARD COMPATABILTY ISSUES WITH ACCESSCONTROL
   */
  public int getID() {
	return getIntColumnValue(getIDColumnName());
  }


   public static PermissionGroup getStaticPermissionGroupInstance(){
    return (PermissionGroup)getStaticInstance(getClassName());
  }

} // Class PermissionGroup
