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

  private boolean infoTitles = true;
  private boolean titlesVertical =true;
  private boolean use_top = true;
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
    this.contentTable = new Table();
  }
  public void main(IWContext iwc){
    drawTables(iwc);
  }

  private void drawTables(IWContext iwc){
    com.idega.presentation.Image image = Table.getTransparentCell(iwc);
    image.setHeight(this.bottomHeight);

    this.contentTable.setWidth(this.sWidth);
    this.contentTable.setHeight(this.sHeight);
    this.contentTable.setCellpadding(3);
    this.contentTable.setCellspacing(1);
    if ( this._columns != -1 ) {
			this.contentTable.setColumns(this._columns);
		}
    if ( this._rows != -1 ) {
			this.contentTable.setRows(this._rows);
		}
    this.contentTable.setHorizontalZebraColored(this.ZEBRA_LIGHT_COLOR,this.ZEBRA_DARK_COLOR);

    if(this.infoTitles){
      if(this.titlesVertical) {
				this.contentTable.setColumnColor(1,this.HEADING_COLOR);
			}
			else {
				this.contentTable.setRowColor(this.use_top?2:1,this.HEADING_COLOR);
			}
    }

    int lastrow = this.contentTable.getRows();
    int lastcol = this.contentTable.getColumns();
    if(this.use_top){
      this.contentTable.mergeCells(1,1,this.contentTable.getColumns(),1);
      this.contentTable.setColor(1,1,this.TOP_COLOR);
    }

    if ( this._addBottom ) {
      lastrow++;
      this.contentTable.mergeCells(1,lastrow,lastcol,lastrow);
      this.contentTable.add(image,1,lastrow);
      this.contentTable.setColor(1,lastrow,this.BOTTOM_COLOR);
    }

    if(this.buttons!=null){
      lastrow++;
      this.contentTable.mergeCells(1,lastrow,lastcol,lastrow);
      this.contentTable.setAlignment(1,lastrow,this.buttonAlign);
      while(this.buttons.size()>0){
        this.contentTable.add((PresentationObject)this.buttons.remove(0),1,lastrow);
        if (this.buttons.size()>0) {
					this.contentTable.add(Text.NON_BREAKING_SPACE,1,lastrow);
				}
      }
    }

    add(this.contentTable);
  }

  public void setUseTitles(boolean titles){
    this.infoTitles = true;
    this.infoTitles = titles;
  }

  public void setTitlesVertical(boolean vertical){
    this.titlesVertical = vertical;
  }
  public void setTitlesHorizontal(boolean horizontal){
    this.infoTitles = true;
    this.titlesVertical = !horizontal;
  }
  public void addTitle(UIComponent title){
    this.contentTable.add(title,1,1);
  }
  public void addTitle(String title){
    Text T = new Text(title,true,false,false);
    T.setFontColor(this.TITLE_FONT_COLOR);
    addTitle(T);
    this.use_top = true;
  }
  public void add(UIComponent objectToAdd,int col,int row){
    this.contentTable.add(objectToAdd,col,this.use_top?++row:row);
  }
  public void addButton(UIComponent objectToAdd){
    if(this.buttons ==null) {
			this.buttons = new Vector();
		}
    this.buttons.add(objectToAdd);
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
    this._rows = rows;
  }
  public void setTableColumns(int columns){
    this._columns = columns;
  }
  public void setHeaderColor(String color){
    this.TOP_COLOR=color;
  }
  public void setTitleColor(String color){
    this.HEADING_COLOR=color;
  }
  public void setZebraColors(String color1,String color2){
    this.ZEBRA_LIGHT_COLOR=color1;
    this.ZEBRA_DARK_COLOR=color2;
  }
  public void setBottomColor(String color){
    this.BOTTOM_COLOR=color;
  }

  /**
   * @deprecated  Replaced by {@link #setUseBottom(boolean)}
   */
  public void addBottom(boolean addBottom){
    this._addBottom = addBottom;
  }

  public void setUseBottom(boolean useBottom){
    this._addBottom = useBottom;
  }

  public void setUseTop(boolean useTop){
    this.use_top = useTop;
  }

  public Table getContentTable(){
    return this.contentTable;
  }
}