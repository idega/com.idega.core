package com.idega.idegaweb.template;


import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;


/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2000 idega.is All Rights Reserved
 * Company:      idega margmidlun
 * @author
 * @version 1.0
 */


public class AccessControlTestTemplate extends TemplatePage {


  public AccessControlTestTemplate() throws Exception{

  }

  public void main(IWContext iwc) throws Exception {
    /*
    if(iwc.hasEditPermission(this)){
      this.add(myForums);
    }else
    */{

      this.add(new Text("ekki leyfi!"));
    }
     // this.add(new Login());
  }


}
