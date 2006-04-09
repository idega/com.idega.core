package com.idega.presentation;


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
    this.iwacTable.add(obj,2,2);
  }

  private Table getTable(){
    if(this.iwacTable == null){
      this.iwacTable = new Table(4,4);
      this.iwacTable.setCellpadding(0);
      this.iwacTable.setCellspacing(0);
      this.iwacTable.setHeight(1,"1");
      this.iwacTable.setHeight(3,"1");
      this.iwacTable.setHeight(4,"1");
      this.iwacTable.setWidth(1,"1");
      this.iwacTable.setWidth(3,"1");
      this.iwacTable.setWidth(4,"1");

      this.iwacTable.setColor(1,1,this._borderColor);
      this.iwacTable.setColor(2,1,this._borderColor);
      this.iwacTable.setColor(3,1,this._borderColor);
      this.iwacTable.setColor(1,2,this._borderColor);
      this.iwacTable.setColor(3,2,this._borderColor);
      this.iwacTable.setColor(1,3,this._borderColor);
      this.iwacTable.setColor(2,3,this._borderColor);
      this.iwacTable.setColor(3,3,this._borderColor);

      this.iwacTable.setColor(4,2,this._shadowColor);
      this.iwacTable.setColor(4,3,this._shadowColor);
      this.iwacTable.setColor(4,4,this._shadowColor);
      this.iwacTable.setColor(2,4,this._shadowColor);
      this.iwacTable.setColor(3,4,this._shadowColor);
    }
    return this.iwacTable;
  }

  public void setHorizontalAlignment(String alignment) {
    this.iwacTable.setAlignment(2,2,alignment);
  }

  public void setVerticalAlignment(String alignment) {
    this.iwacTable.setVerticalAlignment(2,2,alignment);
  }

  public void setBorderColor(String color) {
    this._borderColor = color;
  }

  public void setShadowColor(String color) {
    this._shadowColor = color;
  }

  public synchronized Object clone() {
    RaisedTable obj = null;
    try {
      obj = (RaisedTable) super.clone();
      if ( this.iwacTable != null ) {
				obj.iwacTable= (Table)this.iwacTable.clone();
			}
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }
}
