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
    borderTable = new Table(1,1);
    contentTable = new Table(1,1);
    super.add(borderTable);
  }
  public void main(IWContext iwc){
    drawTables();
  }
  private void drawTables(){

    borderTable.setHeight(this.sHeight);
    borderTable.setWidth(this.sWidth);
    borderTable.setCellpadding(this.iBorder);
    borderTable.setColor(this.sBorderColor);
    contentTable.setWidth("100%");
    contentTable.setHeight("100%");
    contentTable.setRowVerticalAlignment(1,vAlign);
    contentTable.setRowAlignment(1,hAlign);
    contentTable.setColor(this.sContentColor);
    borderTable.add(contentTable);

  }
  public void add(PresentationObject objectToAdd){
    contentTable.add(objectToAdd,1,1);
  }
  public void add(String stringToAdd){
    contentTable.add(stringToAdd);
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