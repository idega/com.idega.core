package com.idega.core.user.presentation;

import com.idega.presentation.TabbedPropertyWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.TabbedPropertyPanel;
import com.idega.presentation.PresentationObject;

/**
 * Title:        User
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AdministratorPropertyWindow extends UserPropertyWindow{

  public AdministratorPropertyWindow(){
    super();
  }

  public String getSessionAddressString(){
    return "ic_admin_property_window";
  }

  public void initializePanel( IWContext iwc, TabbedPropertyPanel panel){
    UserLoginTab ult = new UserLoginTab();
    ult.doNotDisplayLoginInfoSettings();
    panel.addTab(ult,0,iwc);


  }

}
