package com.idega.core.user.presentation;

import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.interfaceobject.IFrame;
import com.idega.jmodule.object.TabbedPropertyPanel;


/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserPropertyWindow extends Window{

  //public TabbedPropertyPanel panel;

  public UserPropertyWindow(){
    super(440,500);
    this.setBackgroundColor("#d4d0c8");
  }

  public void main(ModuleInfo modinfo) throws Exception {
    this.empty();
    TabbedPropertyPanel panel = TabbedPropertyPanel.getInstance("ic_user_property_window", modinfo );
    if(panel.justConstructed()){
      initializePanel(modinfo, panel);
    }

    if(panel.clickedCancel() || panel.clickedOk()){
      this.setParentToReload();
      this.close();
      panel.dispose(modinfo);
    }else{
      this.add(panel);
    }

  }



  public void initializePanel( ModuleInfo modinfo, TabbedPropertyPanel panel){
    GeneralUserInfoTab genTab = new GeneralUserInfoTab();
    panel.addTab(genTab, 0, modinfo);
    panel.addTab(new AddressInfoTab(), 1, modinfo);
  }



}