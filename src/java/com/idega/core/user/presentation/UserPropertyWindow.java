package com.idega.core.user.presentation;

import com.idega.jmodule.object.TabbedPropertyWindow;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.TabbedPropertyPanel;
import com.idega.jmodule.object.ModuleObject;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserPropertyWindow extends TabbedPropertyWindow{

  public static final String PARAMETERSTRING_USER_ID = "ic_user_id";

  public UserPropertyWindow(){
    super();
    this.setBackgroundColor("#d4d0c8");
  }

  public String getSessionAddressString(){
    return "ic_user_property_window";
  }

  public void initializePanel( ModuleInfo modinfo, TabbedPropertyPanel panel){
    GeneralUserInfoTab genTab = new GeneralUserInfoTab();

    panel.addTab(genTab, 0, modinfo);
    panel.addTab(new AddressInfoTab(), 1, modinfo);
    panel.addTab(new UserGroupList(),2,modinfo);
    panel.addTab(new UserLoginTab(),3,modinfo);


  }

  public void main(ModuleInfo modinfo) throws Exception {
    String id = modinfo.getParameter(UserPropertyWindow.PARAMETERSTRING_USER_ID);
    if(id != null){
      int newId = Integer.parseInt(id);
      ModuleObject[] obj = this.getAddedTabs();
      for (int i = 0; i < obj.length; i++) {
        ModuleObject mo = obj[i];
        if( mo instanceof UserTab && ((UserTab)mo).getUserId() != newId){
          ((UserTab)mo).setUserID(newId);
        }
      }
    }
  }

}