package com.idega.presentation.ui;

import java.util.Vector;
import javax.faces.component.UIComponent;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
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

  private int iBorder = 1;
  private boolean infoTitles = true;
  private boolean titlesVertical =true;
  private boolean use_top = true;
  private boolean use_bottom = true;
  private String sHeight = "";
  private String sWidth = "";
  private String buttonAlign  = "right";
  private Vector buttons = null;
  private int bottomHeight = 3;
  private int _rows = -1;
  private int _columns = -1;
  private boolean _addBottom = true;

  private String TOP_COLOR = "#27334B";
  private String HEADING_COLOR = "#D7DADF";
  private String ZEBRA_DARK_COLOR = "#F4F4F4";
  private String BOTTOM_COLOR =  "#932A2D";
  private String ZEBRA_LIGHT_COLOR ="#FFFFFF";
  private String TITLE_FONT_COLOR = "#FFFFFF";

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
    if ( _columns != -1 )
      contentTable.setColumns(_columns);
    if ( _rows != -1 )
      contentTable.setRows(_rows);
    contentTable.setHorizontalZebraColored(ZEBRA_LIGHT_COLOR,ZEBRA_DARK_COLOR);

    if(infoTitles){
      if(titlesVertical)
        contentTable.setColumnColor(1,HEADING_COLOR);
      else
        contentTable.setRowColor(use_top?2:1,HEADING_COLOR);
    }

    int lastrow = contentTable.getRows();
    int lastcol = contentTable.getColumns();
    if(use_top){
      contentTable.mergeCells(1,1,contentTable.getColumns(),1);
      contentTable.setColor(1,1,TOP_COLOR);
    }

    if ( _addBottom ) {
      lastrow++;
      contentTable.mergeCells(1,lastrow,lastcol,lastrow);
      contentTable.add(image,1,lastrow);
      contentTable.setColor(1,lastrow,BOTTOM_COLOR);
    }

    if(buttons!=null){
      lastrow++;
      contentTable.mergeCells(1,lastrow,lastcol,lastrow);
      contentTable.setAlignment(1,lastrow,buttonAlign);
      while(buttons.size()>0){
        contentTable.add((PresentationObject)buttons.remove(0),1,lastrow);
        if (buttons.size()>0)
        	contentTable.add(Text.NON_BREAKING_SPACE,1,lastrow);
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
  public void addTitle(UIComponent title){
    contentTable.add(title,1,1);
  }
  public void addTitle(String title){
    Text T = new Text(title,true,false,false);
    T.setFontColor(TITLE_FONT_COLOR);
    addTitle(T);
    use_top = true;
  }
  public void add(UIComponent objectToAdd,int col,int row){
    contentTable.add(objectToAdd,col,use_top?++row:row);
  }
  public void addButton(UIComponent objectToAdd){
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
  public void setTableRows(int rows){
    _rows = rows;
  }
  public void setTableColumns(int columns){
    _columns = columns;
  }
  public void setHeaderColor(String color){
    TOP_COLOR=color;
  }
  public void setTitleColor(String color){
    HEADING_COLOR=color;
  }
  public void setZebraColors(String color1,String color2){
    ZEBRA_LIGHT_COLOR=color1;
    ZEBRA_DARK_COLOR=color2;
  }
  public void setBottomColor(String color){
    BOTTOM_COLOR=color;
  }

  /**
   * @deprecated  Replaced by {@link #setUseBottom(boolean)}
   */
  public void addBottom(boolean addBottom){
    _addBottom = addBottom;
    use_bottom = addBottom;
  }

  public void setUseBottom(boolean useBottom){
    _addBottom = useBottom;
    use_bottom = useBottom;
  }

  public void setUseTop(boolean useTop){
    use_top = useTop;
  }

  public Table getContentTable(){
    return contentTable;
  }
}