package com.idega.core.accesscontrol.data;








/**

 * Title:        AccessControl

 * Description:

 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved

 * Company:      idega margmiðlun

 * @author

 * @version 1.0

 */



public class PermissionGroupBMPBean extends com.idega.core.data.GenericGroupBMPBean implements com.idega.core.accesscontrol.data.PermissionGroup {
//public class PermissionGroupBMPBean extends com.idega.user.data.GroupBMPBean implements com.idega.core.accesscontrol.data.PermissionGroup {




  /*
  public PermissionGroupBMPBean() {
    super();
  }
  public PermissionGroupBMPBean(int id) throws SQLException{
    super(id);
  }*/



  public String getGroupTypeValue(){

    return "permission";

  }



  public static String getClassName(){

    return "com.idega.core.accesscontrol.data.PermissionGroup";

  }



   public static PermissionGroup getStaticPermissionGroupInstance(){

    return (PermissionGroup)getStaticInstance(getClassName());

  }





} // Class PermissionGroup
