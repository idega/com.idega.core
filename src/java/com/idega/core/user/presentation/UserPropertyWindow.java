package com.idega.core.user.presentation;

import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.interfaceobject.IFrame;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserPropertyWindow extends Window{

  public UserPropertyWindow(){
    super(440,500);
    this.setBackgroundColor("#d4d0c8");
  }

  public void main(ModuleInfo modinfo) throws Exception {
    this.empty();
    UserPropertyPanel upw = UserPropertyPanel.getInstance("ic_user_property_window",modinfo);
    if(upw.doClose()){
      this.setParentToReload();
      this.close();
      upw.dispose(modinfo);
    }else{
      this.add(upw);
    }
  }





}