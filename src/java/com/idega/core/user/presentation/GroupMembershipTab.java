package com.idega.core.user.presentation;

import com.idega.jmodule.object.ModuleInfo;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GroupMembershipTab extends UserGroupTab {

  public GroupMembershipTab() {
    super();
    this.setName("Member of");
  }
  public void initFieldContents() {
    /**@todo: implement this com.idega.core.user.presentation.UserGroupTab abstract method*/
  }
  public void updateFieldsDisplayStatus() {
    /**@todo: implement this com.idega.core.user.presentation.UserGroupTab abstract method*/
  }
  public void initializeFields() {
    /**@todo: implement this com.idega.core.user.presentation.UserGroupTab abstract method*/
  }
  public void initializeTexts() {
    /**@todo: implement this com.idega.core.user.presentation.UserGroupTab abstract method*/
  }
  public boolean store(ModuleInfo modinfo) {
    return true;
  }
  public void lineUpFields() {
    /**@todo: implement this com.idega.core.user.presentation.UserGroupTab abstract method*/
  }
  public boolean collect(ModuleInfo modinfo) {
    return true;
  }
  public void initializeFieldNames() {
    /**@todo: implement this com.idega.core.user.presentation.UserGroupTab abstract method*/
  }
  public void initializeFieldValues() {
    /**@todo: implement this com.idega.core.user.presentation.UserGroupTab abstract method*/
  }
}