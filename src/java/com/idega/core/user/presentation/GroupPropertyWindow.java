package com.idega.core.user.presentation;

import com.idega.jmodule.object.*;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GroupPropertyWindow extends TabbedPropertyWindow {

  public static final String PARAMETERSTRING_GROUP_ID = "ic_group_id";


  public GroupPropertyWindow(){
    super();
    this.setBackgroundColor("#d4d0c8");
  }

  public String getSessionAddressString(){
    return "ic_group_property_window";
  }

  public void initializePanel( ModuleInfo modinfo, TabbedPropertyPanel panel){
    panel.addTab(new GeneralGroupInfoTab(),0,modinfo);
    panel.addTab(new GroupMembershipTab(),1,modinfo);
    //panel.addTab(new ExtendedGroupMembershipTab(),2,modinfo);
  }

  public void main(ModuleInfo modinfo) throws Exception {
    String id = modinfo.getParameter(GroupPropertyWindow.PARAMETERSTRING_GROUP_ID);
    if(id != null){
      int newId = Integer.parseInt(id);
      ModuleObject[] obj = this.getAddedTabs();
      for (int i = 0; i < obj.length; i++) {
        ModuleObject mo = obj[i];
        if( mo instanceof UserGroupTab && ((UserGroupTab)mo).getGroupId() != newId){
          ((UserGroupTab)mo).setGroupId(newId);
        }
      }
    }
  }





}