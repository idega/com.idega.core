package com.idega.idegaweb.template;

import com.idega.jmodule.forum.presentation.GrayForums;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.jmodule.login.*;


/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2000 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author
 * @version 1.0
 */


public class AccessControlTestTemplate extends TemplatePage {
  GrayForums myForums;

  public AccessControlTestTemplate() throws Exception{
    myForums = new GrayForums();
  }

  public void main(IWContext iwc) throws Exception {
    if(iwc.getAccessController().hasPermission(AccessControl.getViewPermissionString(),myForums,iwc)){
      this.add(myForums);
    }else{
      this.add(new Text("ekki leyfi!"));
    }
      this.add(new Login());
  }


}
