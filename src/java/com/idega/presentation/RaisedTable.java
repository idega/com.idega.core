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

  private String _borderColor = "#CCCCCC";
  private String _shadowColor = "#7A7A7A";

  private Table iwacTable;

  public RaisedTable(){
    super.add(getTable());
  }

  public void add(PresentationObject obj){
    iwacTable.add(obj,2,2);
  }

  private Table getTable(){
    if(iwacTable == null){
      iwacTable = new Table(4,4);
      iwacTable.setCellpadding(0);
      iwacTable.setCellspacing(0);
      iwacTable.setHeight(1,"1");
      iwacTable.setHeight(3,"1");
      iwacTable.setHeight(4,"1");
      iwacTable.setWidth(1,"1");
      iwacTable.setWidth(3,"1");
      iwacTable.setWidth(4,"1");

      iwacTable.setColor(1,1,_borderColor);
      iwacTable.setColor(2,1,_borderColor);
      iwacTable.setColor(3,1,_borderColor);
      iwacTable.setColor(1,2,_borderColor);
      iwacTable.setColor(3,2,_borderColor);
      iwacTable.setColor(1,3,_borderColor);
      iwacTable.setColor(2,3,_borderColor);
      iwacTable.setColor(3,3,_borderColor);

      iwacTable.setColor(4,2,_shadowColor);
      iwacTable.setColor(4,3,_shadowColor);
      iwacTable.setColor(4,4,_shadowColor);
      iwacTable.setColor(2,4,_shadowColor);
      iwacTable.setColor(3,4,_shadowColor);
    }
    return iwacTable;
  }

  public void setHorizontalAlignment(String alignment) {
    iwacTable.setAlignment(2,2,alignment);
  }

  public void setVerticalAlignment(String alignment) {
    iwacTable.setVerticalAlignment(2,2,alignment);
  }

  public void setBorderColor(String color) {
    _borderColor = color;
  }

  public void setShadowColor(String color) {
    _shadowColor = color;
  }

  public synchronized Object clone() {
    RaisedTable obj = null;
    try {
      obj = (RaisedTable) super.clone();
      if ( this.iwacTable != null )
        obj.iwacTable= (Table)this.iwacTable.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }
}
