package com.idega.core.usermodule.presentation;

import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.TabbedPropertyPanel;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.jmodule.object.ModuleInfo;

/**
 * Title:        UserModule
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserPropertyWindow extends Window implements IWSubmitListener {

  public TabbedPropertyPanel panel;
  private boolean justConstructed = true;
  private String attributeString;
  public static String UserPropertyWindowAttributeString = "-UserPropertyWindow";

  public UserPropertyWindow(String key, ModuleInfo modinfo) {
    super(420,480);
    panel = TabbedPropertyPanel.getInstance(key, modinfo );
    initializePanel(modinfo);
    this.add(panel);
    this.setBackgroundColor("#d4d0c8");
  }


  public static UserPropertyWindow getInstance(String key, ModuleInfo modinfo){
    Object  obj = modinfo.getSessionAttribute(key+UserPropertyWindowAttributeString);
    if(obj != null && obj instanceof UserPropertyWindow){
      UserPropertyWindow UserPropertyWindowObj = (UserPropertyWindow)obj;
      UserPropertyWindowObj.justConstructed(false);
      return UserPropertyWindowObj;
    }else{
      UserPropertyWindow tempWindow = new UserPropertyWindow(key,modinfo);
      modinfo.setSessionAttribute(key+UserPropertyWindowAttributeString, tempWindow);
      tempWindow.setAttributeString(key+UserPropertyWindowAttributeString);
      return tempWindow;
    }
  }

  public boolean justConstructed(){
    return justConstructed;
  }

  public void justConstructed(boolean justConstructed){
    this.justConstructed = justConstructed;
    this.panel.justConstructed(justConstructed);
  }

  public void setAttributeString(String attributeString){
    this.attributeString = attributeString;
  }

  public void dispose(ModuleInfo modinfo){
    modinfo.getSession().removeAttribute(attributeString);
    panel.dispose(modinfo);
  }


  public void initializePanel( ModuleInfo modinfo ){

    panel.addTab(new GeneralUserInfoTab(), 0, modinfo);
    panel.addTab(new AddressInfoTab(), 1, modinfo);
    panel.getOkButton().addIWSubmitListener(this,modinfo);
    panel.getCancelButton().addIWSubmitListener(this,panel,modinfo);

  }


  public void actionPerformed(IWSubmitEvent e){
    if (e.getSource() == panel.getOkButton() || e.getSource() == panel.getCancelButton()){
      this.dispose(this.getEventModuleInfo());
      this.close();
    }
  }


/*
  public void main(ModuleInfo modinfo) throws Exception {

  }
*/




}