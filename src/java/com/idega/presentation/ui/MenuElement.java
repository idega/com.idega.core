//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class MenuElement extends InterfaceObject{

  private boolean isDisabled;
  private boolean isSelected;

  private static String emptyString = "";

  public MenuElement(){
          this("untitled");
  }

  public MenuElement(String ElementName){
          this(ElementName,ElementName);
  }

  public MenuElement(String ElementName,String ElementValue){
          super();
          setName(ElementName);
          setValue(ElementValue);
          setSelected(false);
          setDisabled(false);
  }

  public void setElementName(String ElementName){
          setName(ElementName);
  }

  public void setElementValue(String ElementValue){
          setValue(ElementValue);
  }

  public boolean isSelected(){
          return isSelected;
  }

  public boolean isDisabled(){
          return isDisabled;
  }

  public void setSelected(boolean ifSelected){
          isSelected=ifSelected;
  }

  public void setDisabled(boolean ifDisabled){
          isDisabled = ifDisabled;
  }

  public String getElementValue(){
          return getValue();
  }

  public String getElementName(){
          return getName();
  }

  public void print(IWContext iwc)throws IOException{
          initVariables(iwc);
          //if ( doPrint(iwc) ){
            if (getLanguage().equals("HTML")){
                    String disabledString = emptyString;
                    String selectedString = emptyString;
                    if (isSelected()){
                            selectedString = "selected";
                    }
                    if (isDisabled()){
                            disabledString = "disabled";
                    }
                            //if (getInterfaceStyle().equals("default"))
                                    print("<option name=\""+getName()+"\" "+getAttributeString()+" "+disabledString+" "+selectedString+" >");
                                    print(getName());
                                    print("</option>");
                            // }
                    }

            else if (getLanguage().equals("WML")){
                    print("<option name=\""+getName()+"\" value=\""+getValue()+"\" >");
                    print(getName());
                    print("</option>");
            }
          //}
  }

}
