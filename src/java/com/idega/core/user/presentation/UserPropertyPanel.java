package com.idega.core.user.presentation;

import com.idega.jmodule.object.TabbedPropertyPanel;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.core.user.presentation.BasicUserOverview;
import com.idega.jmodule.object.ModuleObjectContainer;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class UserPropertyPanel extends ModuleObjectContainer implements IWSubmitListener  {


    public TabbedPropertyPanel panel;
    private boolean justConstructed = true;
    private boolean defaultConst = false;
    private String sessionAddressString;
    private final static String UserPropertyPanelAttributeString = "-UserPropertyWindow";
    private boolean close = false;

    public UserPropertyPanel(String key, ModuleInfo modinfo) {
      init(key, modinfo);
    }

    private void init(String key, ModuleInfo modinfo){
      panel = TabbedPropertyPanel.getInstance(key, modinfo );
      if(panel.justConstructed()){
        initializePanel(modinfo);
      }
      this.add(panel);
    }

    public static UserPropertyPanel getInstance(String key, ModuleInfo modinfo){
      Object  obj = modinfo.getSessionAttribute(key+UserPropertyPanelAttributeString);
      if(obj != null && obj instanceof UserPropertyPanel){
        UserPropertyPanel UserPropertyPanelObj = (UserPropertyPanel)obj;
        UserPropertyPanelObj.justConstructed(false);
        return UserPropertyPanelObj;
      }else{
        UserPropertyPanel tempPanel = new UserPropertyPanel(key,modinfo);
        modinfo.setSessionAttribute(key+UserPropertyPanelAttributeString, tempPanel);
        tempPanel.setSessionAddressString(key+UserPropertyPanelAttributeString);
        return tempPanel;
      }
    }

    public boolean justConstructed(){
      return justConstructed;
    }

    public void justConstructed(boolean justConstructed){
      this.justConstructed = justConstructed;
      this.panel.justConstructed(justConstructed);
    }

    public void setSessionAddressString(String sessionAddressString){
      this.sessionAddressString = sessionAddressString;
    }

    public String getSessionAddressString(){
      return this.sessionAddressString;
    }

    public void dispose(ModuleInfo modinfo){
      modinfo.getSession().removeAttribute(sessionAddressString);
      panel.dispose(modinfo);
    }


    public void initializePanel( ModuleInfo modinfo ){
      GeneralUserInfoTab genTab = new GeneralUserInfoTab();
      panel.addTab(genTab, 0, modinfo);
      panel.addTab(new AddressInfoTab(), 1, modinfo);
      panel.getOkButton().addIWSubmitListener(this,modinfo);
      panel.getCancelButton().addIWSubmitListener(this,panel,modinfo);

    }


    public void actionPerformed(IWSubmitEvent e){
      if (e.getSource() == panel.getOkButton() || e.getSource() == panel.getCancelButton()){
        this.close = true;
      }
    }

    public boolean doClose(){
      return close;
    }
/*
    public void main(ModuleInfo modinfo) throws Exception {
      this.close = false;
    }
*/
  } // Class
