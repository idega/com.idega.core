package com.idega.presentation.ui;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class DataTable extends PresentationObjectContainer {

  private Table contentTable;
  private Table borderTable;
  private int iBorder = 1;
  private boolean infoTitles = true;
  private boolean titlesVertical =true;
  private String sHeight = "";
  private String sWidth = "";
  private String sBorderColor = "#FFFFFF";
  private String sContentColor = "#FFFFFF";
  private String vAlign = "middle";
  private String hAlign = "center";

  private String DARKBLUE = "#27334B";
  private String DARKGREY = "#D7DADF";
  private String LIGHTGREY = "#F4F4F4";
  private String DARKRED =  "#932A2D";
  private String WHITE ="#FFFFFF";

  public DataTable(){
    borderTable = new Table(1,3);
    contentTable = new Table();
    super.add(borderTable);
  }
  public void main(IWContext iwc){
    drawTables(iwc);
  }
  private void drawTables(IWContext iwc){
    com.idega.presentation.Image image = Table.getTransparentCell(iwc);
    image.setHeight(6);
    borderTable.setHeight(this.sHeight);
    borderTable.setWidth(this.sWidth);
    borderTable.setCellpadding(0);
    borderTable.setCellspacing(0);

    contentTable.setWidth("100%");
    contentTable.setHeight("100%");
    contentTable.setHorizontalZebraColored(WHITE,LIGHTGREY);
    contentTable.setColor(this.sContentColor);
    borderTable.add(contentTable,1,2);

    if(infoTitles){
      if(titlesVertical)
        contentTable.setColumnColor(1,DARKGREY);
      else
        contentTable.setRowColor(1,DARKGREY);
    }

    borderTable.add(image,1,3);
    borderTable.setColor(1,3,DARKRED);
    borderTable.setColor(1,1,DARKBLUE);

  }

  public void setUseTitles(boolean titles){
    infoTitles = true;
    infoTitles = titles;
  }

  public void setTitlesVertical(boolean vertical){
    titlesVertical = vertical;
  }

  public void setTitlesHorisontal(boolean horizontal){
    infoTitles = true;
    titlesVertical = !horizontal;
  }

  public void addTitle(PresentationObject title){
    borderTable.add(title,1,1);
  }
  public void addTitle(String title){
    Text T = new Text(title,true,false,false);
    T.setFontColor(WHITE);
    borderTable.add(T,1,1);
  }
  public void add(PresentationObject objectToAdd,int col,int row){
    contentTable.add(objectToAdd,col,row);
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
  public Table getContentTable(){
    return contentTable;
  }
}