package com.idega.presentation.app;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.core.data.ICObject;

import java.util.List;
import java.util.Iterator;

public class IWControlCenter extends PresentationObjectContainer {

  int windowWidth=300;
  int windowHeight=200;
  int headerHeight=25;
  int border = 3;
  String windowBorder = "gray";
  String backgroundColor = "#D4D0C8";
  String headerColor = backgroundColor;
  String darkerColor = "gray";
  String bodyColor = "white";

  public IWControlCenter() {

  }

  public void main(IWContext iwc){

    /*Table outerWindow = new Table(1,2);
    outerWindow.setWidth(windowWidth);
    outerWindow.setHeight(windowHeight);
    outerWindow.setAlignment("center");
    outerWindow.setVerticalAlignment("middle");
    add(outerWindow);

    outerWindow.setCellspacing(border);
    outerWindow.setColor(windowBorder);
    outerWindow.setAlignment(1,2,"center");
    outerWindow.setVerticalAlignment(1,2,"middle");

    Table header = new Table();
    header.setWidth("100%");
    header.setHeight(headerHeight);

    outerWindow.add(header,1,1);
    header.setColor(headerColor);
    Text headerText = new Text("idegaWeb ApplicationSuite");
    headerText.setFontSize(1);
    headerText.setFontColor("black");
    header.add(headerText);*/

    Table body = new Table();
    //outerWindow.add(body,1,2);
    add(body);
    body.setWidth("100%");
    //body.setHeight(windowHeight-headerHeight);
    //body.setHeight("100%");
    body.setCellpadding(4);
    //body.setColor(bodyColor);


    List icoList = IWApplication.getApplictionICObjects();
    if(icoList!=null){
      int x =1;
      int y =1;
      Iterator iter = icoList.iterator();
      while (iter.hasNext()) {
        ICObject item = (ICObject)iter.next();
        Class c = null;
        try{
          c = item.getObjectClass();
        }
        catch(Exception e){
        }

        PresentationObject icon = IWApplication.getIWApplicationIcon(c,iwc);
        body.setAlignment(x,y,"center");
        body.setVerticalAlignment(x,y,"middle");
        body.add(icon,x,y);
        if(x==1){
          x=2;
        }
/* Uncomment for three line application suite
        else if(x==2){
          x=3;
        }
*/
        else{
          x=1;
          y++;
        }
      }
    }

  }
}