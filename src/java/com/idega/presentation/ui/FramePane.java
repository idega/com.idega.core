//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/



package com.idega.presentation.ui;



import java.io.*;

import java.util.*;

import com.idega.presentation.*;

import com.idega.presentation.text.*;

/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/



public class FramePane extends InterfaceObjectContainer{



private Table table;

//private static String imagePrefix = "/common/pics/interfaceobject/FramePane/";

private static String imagePrefix = "framePane/";

private static boolean sourcesSet=false;

private Table InnerTable;



  private static Image topRight;

  private static Image topLeft;

  private static Image bottomLeft;

  private static Image bottomRight;

  private static Image Right;

  private static Image Left;

  private static Image Bottom;

  private static Image Top;



public FramePane(){

  this("");

}



public FramePane(String headerText){

  table = new Table(3,3);

  //table.setWidth("200");

  //table.setHeight("200");

  table.setCellpadding(0);

  table.setCellspacing(0);

  table.setAlignment(2,2,"center");

  table.setVerticalAlignment(2,2,"middle");

  super.add(table);

  table.setHeight(1,"24");

  table.setHeight(3,"10");

  table.setWidth(1,"10");

  table.setWidth(3,"10");

  //Image topRight=new Image(imagePrefix+"topright.gif");

  //table.add(topRight,3,1);

  //table.setBackgroundImage(3,1,topRight);

  //Image topLeft=new Image(imagePrefix+"topleft.gif");

  //table.add(topLeft,1,1);

  //table.setBackgroundImage(1,1,topLeft);

  //Image bottomLeft=new Image(imagePrefix+"bottomleft.gif");

  //table.add(bottomLeft,1,3);

  table.add(Text.emptyString(),1,3);

  //table.setBackgroundImage(1,3,bottomLeft);

  //Image bottomRight=new Image(imagePrefix+"bottomright.gif");

  //table.add(bottomRight,3,3);

  //table.setBackgroundImage(3,3,bottomRight);

  table.add(Text.emptyString(),3,3);

  //Image Right=new Image(imagePrefix+"righttiler.gif");

  //table.setBackgroundImage(3,2,Right);

  table.add(Text.emptyString(),3,2);

  //Image Left=new Image(imagePrefix+"lefttiler.gif");

  //table.setBackgroundImage(1,2,Left);

  table.add(Text.emptyString(),1,2);

  //Image Bottom=new Image(imagePrefix+"bottomtiler.gif");

  //table.setBackgroundImage(2,3,Bottom);

  table.add(Text.emptyString(),2,3);

  InnerTable = new Table(2,1);

  InnerTable.setHeight("24");

  InnerTable.setWidth(2,1,"100%");

  InnerTable.setCellpadding(0);

  InnerTable.setCellspacing(0);

  InnerTable.add(headerText,1,1);

  //Image Top=new Image(imagePrefix+"toptiler.gif");

  //InnerTable.setBackgroundImage(2,1,Top);

  table.add(InnerTable,2,1);





}



public void main(IWContext iwc){

  com.idega.idegaweb.IWBundle bundle = this.getBundle(iwc);

  if(!sourcesSet){

    topRight = bundle.getImage(imagePrefix+"topright.gif");

    topLeft = bundle.getImage(imagePrefix+"topleft.gif");

    bottomLeft = bundle.getImage(imagePrefix+"bottomleft.gif");

    bottomRight = bundle.getImage(imagePrefix+"bottomright.gif");

    Right = bundle.getImage(imagePrefix+"righttiler.gif");

    Left = bundle.getImage(imagePrefix+"lefttiler.gif");

    Bottom = bundle.getImage(imagePrefix+"bottomtiler.gif");

    Top = bundle.getImage(imagePrefix+"toptiler.gif");

    sourcesSet=true;

  }

  table.add(topRight,3,1);

  table.add(topLeft,1,1);

  table.setBackgroundImage(1,3,bottomLeft);

  table.setBackgroundImage(3,3,bottomRight);

  table.setBackgroundImage(3,2,Right);

  table.setBackgroundImage(1,2,Left);

  table.setBackgroundImage(2,3,Bottom);

  InnerTable.setBackgroundImage(2,1,Top);

}



public void add(PresentationObject obj){

  table.add(obj,2,2);

}





public void setWidth(int width){

  table.setWidth(width);

}



public void setHeight(int height){

  table.setHeight(height);

}





  public synchronized Object clone() {

    FramePane obj = null;

    try {

      obj = (FramePane)super.clone();

      if(this.table!=null){

        obj.table=(Table)this.table.clone();

      }

      if(this.InnerTable!=null){

        obj.InnerTable=(Table)this.InnerTable.clone();

      }

    }

    catch(Exception ex) {

      ex.printStackTrace(System.err);

    }



    return obj;

  }



}
