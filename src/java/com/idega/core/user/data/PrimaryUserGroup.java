package com.idega.core.user.data;

import com.idega.core.data.GenericGroup;
import java.sql.SQLException;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class PrimaryUserGroup extends GenericGroup {


  public PrimaryUserGroup(){
    super();
  }

  public PrimaryUserGroup(int id) throws SQLException {
    super(id);
  }


  public String getGroupTypeValue(){
    return "ic_primary_usergroup";
  }

}