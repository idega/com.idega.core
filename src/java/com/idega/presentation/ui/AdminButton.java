//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class AdminButton extends GenericButton{

//private Image defaultImage;
//private Image onMouseOverImage;
//private Image onClickImage;
private String adminWindowPage=IdegaWebApplication.windowOpenerURL;
private String sessionStorageName=IdegaWebApplication.windowOpenerParameter;

private GenericButton button;
private Link link;
private JModuleAdminWindow windowStored;
/*public AdminButton(){
}*/

public AdminButton(String displayText,JModuleAdminWindow adminWindow){
        windowStored=adminWindow;
        adminWindow.setURL(adminWindowPage);
        button = new GenericButton("",displayText);
        link = new Link(button,adminWindow);
        link.setParentObject(this);
}

public AdminButton(Image defaultImage,JModuleAdminWindow adminWindow){
        windowStored=adminWindow;
        adminWindow.setURL(adminWindowPage);
        link = new Link(defaultImage,adminWindow);
        link.setParentObject(this);
}


public AdminButton(String adminClassName){
  try{
        JModuleAdminWindow adminWindow = (JModuleAdminWindow)Class.forName(adminClassName).newInstance();
        windowStored=adminWindow;
        adminWindow.setURL(adminWindowPage);
        String displayText = adminWindow.getName()+"stjóri";
        button = new GenericButton("",displayText);
        link = new Link(button,adminWindow);
        link.setParentObject(this);
  }
  catch(ClassNotFoundException ex){
    ex.printStackTrace(System.err);
  }
  catch(IllegalAccessException ex){
    ex.printStackTrace(System.err);
  }
  catch(InstantiationException ex){
    ex.printStackTrace(System.err);
  }
}

public AdminButton(String displayText,String adminClassName){
  try{
        JModuleAdminWindow adminWindow = (JModuleAdminWindow)Class.forName(adminClassName).newInstance();
        windowStored=adminWindow;
        //adminWindow.setURL(adminWindowPage);
        button = new GenericButton("",displayText);
        link = new Link(button,adminWindow);
        link.setParentObject(this);
  }
  catch(ClassNotFoundException ex){
    ex.printStackTrace(System.err);
  }
  catch(IllegalAccessException ex){
    ex.printStackTrace(System.err);
  }
  catch(InstantiationException ex){
    ex.printStackTrace(System.err);
  }

}


public void main(IWContext iwc)throws Exception{
  if(windowStored!=null){

    String sessionParameterName=com.idega.servlet.WindowOpener.storeWindow(iwc,windowStored);
    addParameter(sessionStorageName,sessionParameterName);

    //String sessionParameterName=this.getID();
    //addParameter(sessionStorageName,sessionParameterName);
    //iwc.setSessionAttribute(sessionParameterName,windowStored);
  }
  /*if(link!=null){
    link.main(iwc);
  }*/
}

public void addParameter(Parameter parameter){
  addParameter(parameter.getName(),parameter.getValue());
}

public void addParameter(String parameterName,String parameterValue){
  if(link!=null){
    link.addParameter(parameterName,parameterValue);
  }
}



public void print(IWContext iwc) throws Exception{
  if (link!=null){
    if(button==null){
      link.print(iwc);
    }
    else{
      windowStored.setURL(link.getURL());
      //windowStored.setURL(windowStored.getURL(iwc)+link.getParameterString(iwc,windowStored.getURL(iwc)));
      button.setOnClick(windowStored.getCallingScriptString(iwc));
      button.print(iwc);
    }

  }
}


}

