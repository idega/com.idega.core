package com.idega.presentation;

import com.idega.presentation.*;
import com.idega.idegaweb.*;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class RaisedTable extends PresentationObjectContainer {

  private String _lightColor = "#FFFFFF";
  private String _darkColor = "#999999";
  private String _color = "#CCCCCC";

  private Table iwacTable;

  public RaisedTable(){
    super.add(getIWACTable());
  }

  public void add(PresentationObject obj){
    iwacTable.add(obj,2,2);
  }

  private Table getIWACTable(){
    if(iwacTable == null){
      iwacTable = new Table(3,3);
      iwacTable.setHeight(150);
      iwacTable.setWidth(150);
      iwacTable.setCellpadding(0);
      iwacTable.setCellspacing(0);
      iwacTable.setHeight(1,"1");
      iwacTable.setHeight(3,"1");
      iwacTable.setWidth(2,"100%");
      iwacTable.setWidth(1,"1");
      iwacTable.setWidth(3,"1");
    }
    return iwacTable;
  }

  public void _main(IWContext iwc) throws Exception {
    super._main(iwc);
    setImages(iwc);
  }

  public void setImages(IWContext iwc){
    IWBundle iwb = this.getBundle(iwc);

    Image emptyCell = Table.getTransparentCell(iwc);
      emptyCell.setWidth(1);
      emptyCell.setHeight(1);
    Image tilerCell = (Image) emptyCell.clone();
      tilerCell.setHeight("100%");


    iwacTable.setColor(1,1,_lightColor);
    iwacTable.setColor(3,1,_lightColor);
    iwacTable.setColor(1,3,_lightColor);
    iwacTable.setColor(3,3,_darkColor);
    iwacTable.setColor(2,1,_lightColor);
    iwacTable.setColor(2,3,_darkColor);
    iwacTable.setColor(1,2,_lightColor);
    iwacTable.setColor(3,2,_darkColor);
    iwacTable.setColor(2,2,_color);

    iwacTable.add(emptyCell,1,1);
    iwacTable.add(emptyCell,2,1);
    iwacTable.add(emptyCell,3,1);
    iwacTable.add(emptyCell,1,3);
    iwacTable.add(emptyCell,2,3);
    iwacTable.add(emptyCell,3,3);
    iwacTable.add(tilerCell,1,2);
    iwacTable.add(tilerCell,3,2);
  }

  public void setAlignment(String alignment) {
    iwacTable.setAlignment(2,2,alignment);
  }

  public void setVerticalAlignment(String alignment) {
    iwacTable.setVerticalAlignment(2,2,alignment);
  }

  public void setWidth(String width) {
    iwacTable.setWidth(width);
  }

  public void setWidth(int width) {
    setWidth(Integer.toString(width));
  }

  public void setHeight(String height) {
    iwacTable.setHeight(height);
  }

  public void setHeight(int height) {
    setHeight(Integer.toString(height));
  }

  public void setLightShadowColor(String color) {
    _lightColor = color;
  }

  public void setDarkShadowColor(String color) {
    _darkColor = color;
  }

  public synchronized Object clone() {
    RaisedTable obj = null;
    try {
      obj = (RaisedTable) super.clone();
      obj.iwacTable= (Table)this.iwacTable.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }
}