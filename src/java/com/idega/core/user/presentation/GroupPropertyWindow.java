package com.idega.core.user.presentation;

import com.idega.presentation.*;


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

  public static final String SESSION_ADDRESS = "ic_group_property_window";

  public GroupPropertyWindow(){
    super();
    this.setBackgroundColor("#d4d0c8");
  }

  public String getSessionAddressString(){
    return SESSION_ADDRESS; 
  }

  public void initializePanel( IWContext iwc, TabbedPropertyPanel panel){
    panel.addTab(new GeneralGroupInfoTab(),0,iwc);
    panel.addTab(new GroupMembershipTab(),1,iwc);
    //panel.addTab(new ExtendedGroupMembershipTab(),2,iwc);
  }

  public void main(IWContext iwc) throws Exception {
    String id = iwc.getParameter(GroupPropertyWindow.PARAMETERSTRING_GROUP_ID);
    if(id != null){
      int newId = Integer.parseInt(id);
      PresentationObject[] obj = this.getAddedTabs();
      for (int i = 0; i < obj.length; i++) {
        PresentationObject mo = obj[i];
        if( mo instanceof UserGroupTab && ((UserGroupTab)mo).getGroupId() != newId){
          ((UserGroupTab)mo).setGroupId(newId);
        }
      }
    }
  }





}
