//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class BooleanInput extends DropdownMenu{

  private static final String NO_KEY = "booleaninput.no";
  private static final String YES_KEY = "booleaninput.yes";

  public BooleanInput(){
      this("booleaninput");
  }

  public BooleanInput(String name){
    super(name);
    addMenuElement("N");
    addMenuElement("Y");
  }

  public void main(IWContext iwc)throws Exception{
    super.main(iwc);
    IWBundle iwb = this.getBundle(iwc);
    IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
    setMenuElementDisplayString("N",iwrb.getLocalizedString(NO_KEY));
    setMenuElementDisplayString("Y",iwrb.getLocalizedString(YES_KEY));
  }



public void setSelected(boolean selected){
  if(selected){
    this.setSelectedElement("Y");
  }
  else{
    this.setSelectedElement("N");
  }
}

}
