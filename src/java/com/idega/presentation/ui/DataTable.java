package com.idega.presentation.ui;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import java.util.Vector;

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

  private int iBorder = 1;
  private boolean infoTitles = true;
  private boolean titlesVertical =true;
  private String sHeight = "";
  private String sWidth = "";
  private String buttonAlign  = "right";
  private Vector buttons = null;
  private int bottomHeight = 3;

  private String DARKBLUE = "#27334B";
  private String DARKGREY = "#D7DADF";
  private String LIGHTGREY = "#F4F4F4";
  private String DARKRED =  "#932A2D";
  private String WHITE ="#FFFFFF";

  public DataTable(){
    contentTable = new Table();
  }
  public void main(IWContext iwc){
    drawTables(iwc);
  }
  private void drawTables(IWContext iwc){
    com.idega.presentation.Image image = Table.getTransparentCell(iwc);
    image.setHeight(bottomHeight);

    contentTable.setWidth(this.sWidth);
    contentTable.setHeight(this.sHeight);
    contentTable.setCellpadding(3);
    contentTable.setCellspacing(1);
    contentTable.setHorizontalZebraColored(WHITE,LIGHTGREY);

    if(infoTitles){
      if(titlesVertical)
        contentTable.setColumnColor(1,DARKGREY);
      else
        contentTable.setRowColor(2,DARKGREY);
    }

    int lastrow = contentTable.getRows()+1;
    int lastcol = contentTable.getColumns();
    contentTable.mergeCells(1,1,contentTable.getColumns(),1);
    contentTable.setColor(1,1,DARKBLUE);

    contentTable.mergeCells(1,lastrow,lastcol,lastrow);
    contentTable.add(image,1,lastrow);
    contentTable.setColor(1,lastrow,DARKRED);
    if(buttons!=null){
      lastrow++;
      contentTable.mergeCells(1,lastrow,lastcol,lastrow);
      contentTable.setAlignment(1,lastrow,buttonAlign);
      while(buttons.size()>0){
        contentTable.add((PresentationObject)buttons.remove(0),1,lastrow);
      }
    }

    add(contentTable);
  }

  public void setUseTitles(boolean titles){
    infoTitles = true;
    infoTitles = titles;
  }

  public void setTitlesVertical(boolean vertical){
    titlesVertical = vertical;
  }
  public void setTitlesHorizontal(boolean horizontal){
    infoTitles = true;
    titlesVertical = !horizontal;
  }
  public void addTitle(PresentationObject title){
    contentTable.add(title,1,1);
  }
  public void addTitle(String title){
    Text T = new Text(title,true,false,false);
    T.setFontColor(WHITE);
    addTitle(T);
  }
  public void add(PresentationObject objectToAdd,int col,int row){
    contentTable.add(objectToAdd,col,++row);
  }
  public void addButton(PresentationObject objectToAdd){
    if(buttons ==null)
      buttons = new Vector();
    buttons.add(objectToAdd);
  }
   public void setBottomHeight(int height){
    this.bottomHeight=height;
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