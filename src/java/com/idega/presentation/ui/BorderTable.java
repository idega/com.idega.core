package com.idega.presentation.ui;



import com.idega.presentation.PresentationObjectContainer;

import com.idega.presentation.PresentationObject;

import com.idega.presentation.IWContext;

import com.idega.presentation.Table;



/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega multimedia

 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>

 * @version 1.0

 */



public class BorderTable extends PresentationObjectContainer {



  private Table contentTable;

  private Table borderTable;

  private int iBorder = 1;

  private String sHeight = "";

  private String sWidth = "";

  private String sBorderColor = "#FFFFFF";

  private String sContentColor = "#FFFFFF";

  private String vAlign = "middle";

  private String hAlign = "center";



  public BorderTable(){

    this.borderTable = new Table(1,1);

    this.contentTable = new Table(1,1);

    super.add(this.borderTable);

  }

  public void main(IWContext iwc){

    drawTables();

  }

  private void drawTables(){



    this.borderTable.setHeight(this.sHeight);

    this.borderTable.setWidth(this.sWidth);

    this.borderTable.setCellpadding(this.iBorder);

    this.borderTable.setColor(this.sBorderColor);

    this.contentTable.setWidth("100%");

    this.contentTable.setHeight("100%");

    this.contentTable.setRowVerticalAlignment(1,this.vAlign);

    this.contentTable.setRowAlignment(1,this.hAlign);

    this.contentTable.setColor(this.sContentColor);

    this.borderTable.add(this.contentTable);



  }

  public void add(PresentationObject objectToAdd){

    this.contentTable.add(objectToAdd,1,1);

  }

  public void add(String stringToAdd){

    this.contentTable.add(stringToAdd);

  }

  public void setBorder(int border){

    this.iBorder = border;

  }

  public void setVerticalAlignment(String align ){

    this.vAlign = align;

  }

  public void setHorizontalAlignment(String align){

    this.hAlign = align;

  }

  public void setWidth(int tableWidth){

    this.sWidth=String.valueOf(tableWidth);

  }

  public void setWidth(String tableWidth){

    this.sWidth=tableWidth;

  }

  public void setHeight(String tableHeight){

    this.sHeight = tableHeight;

  }

  public void setHeight(int tableHeight){

    this.sHeight = String.valueOf(tableHeight);

  }

  public void setColor(String tableColor){

    this.sContentColor=tableColor;

  }

  public void setBorderColor(String borderColor){

    this.sBorderColor=borderColor;

  }

}
