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
  private static final String SELECT_KEY = "booleaninput.select";

  private boolean _showSelectOption=true;

  public BooleanInput(){
      this("booleaninput");
  }

  public BooleanInput(String name){
    super(name);

  }

  public void main(IWContext iwc)throws Exception{
    super.main(iwc);

    IWBundle iwb = this.getBundle(iwc);
    IWResourceBundle iwrb = iwb.getResourceBundle(iwc);

    if(_showSelectOption){
      addMenuElement("",iwrb.getLocalizedString(SELECT_KEY,"Select:"));
    }

    addMenuElement("N");
    addMenuElement("Y");

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

  public void displayOnlyBooleanOptions(){
    _showSelectOption=false;
  }

  public void displaySelectOption(){
    this._showSelectOption=true;
  }

}
