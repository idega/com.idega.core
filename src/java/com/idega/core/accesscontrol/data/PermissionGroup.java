package com.idega.core.accesscontrol.data;

import com.idega.core.data.GeneralGroup;
import java.sql.*;


/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author
 * @version 1.0
 */

public class PermissionGroup extends GeneralGroup {


  public PermissionGroup() {
    super();
  }

  public PermissionGroup(int id) throws SQLException{
    super(id);
  }

  public String getGroupTypeValue(){
    return "permission";
  }

  public static String getClassName(){
    return "com.idega.builder.accesscontrol.data.PermissionGroup";
  }

   public static PermissionGroup getStaticPermissionGroupInstance(){
    return (PermissionGroup)getStaticInstance(getClassName());
  }


} // Class PermissionGroup