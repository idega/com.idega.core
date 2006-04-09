package com.idega.presentation;

import com.idega.presentation.Block;
import com.idega.presentation.text.Link;
import com.idega.presentation.Table;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.text.Text;

/**
 * Title: FrameList
 * Description: An addable list that extends Block, can be inserted into pages or frames.
 * Copyright:    Copyright (c) 2001
 * Company: idega
 * @author Laddi
 * @version 1.0
 */

public class FrameList extends Block {

private Table listTable;
private String style = "";

  public FrameList() {
    initializeTable();
  }

  public void _main(IWContext iwc) throws Exception {
    getParentPage().setAllMargins(0);
    initializeTable();

    add(this.listTable);
    super._main(iwc);
  }

  public void main(IWContext iwc) {
  }

  private void initializeTable() {
    this.listTable = new Table();
      this.listTable.setCellpadding(3);
      this.listTable.setCellspacing(0);
      this.listTable.setWidth("100%");
      //listTable.setHeight("100%");
  }

  public void addToList(PresentationObject obj, Image displayImage) {
    int rows = this.listTable.getRows();
      if ( !this.listTable.isEmpty(1,rows) ) {
	rows++;
      }

    if ( displayImage != null ) {
      this.listTable.add(displayImage,1,rows);
      this.listTable.add(obj,2,rows);
    }
    else {
      this.listTable.add(obj,1,rows);
    }
  }

  public void addToList(PresentationObject obj) {
    addToList(obj,null);
  }

  public void addToList(String displayString) {
    addToList(new Text(displayString),null);
  }

  public void addToList(Class classToAdd, Image displayImage, String displayString, String target) {
    Text text = new Text(displayString);
      text.setFontStyle(this.style);

    Link link = new Link(text,classToAdd);
      link.setTarget(target);

    int rows = this.listTable.getRows();
      if ( !this.listTable.isEmpty(1,rows) ) {
	rows++;
      }

    if ( displayImage != null ) {
      this.listTable.add(displayImage,1,rows);
      this.listTable.add(link,2,rows);
    }
    else {
      this.listTable.add(link,1,rows);
    }
  }

  public void addToList(Class classToAdd, Image displayImage, String target) {
    addToList(classToAdd,displayImage,classToAdd.getName(),target);
  }

  public void addToList(Class classToAdd, Image displayImage) {
    addToList(classToAdd,displayImage,classToAdd.getName(),"_self");
  }

  public void addToList(Class classToAdd, String displayString, String target) {
    addToList(classToAdd,null,displayString,target);
  }

  public void addToList(Class classToAdd, String displayString) {
    addToList(classToAdd,displayString,"_self");
  }

  public void addToList(Class classToAdd) {
    addToList(classToAdd,classToAdd.getName().substring(classToAdd.getName().lastIndexOf(".")+1),"_self");
  }

  public void setLinkStyle(String style) {
    this.style = style;
  }

  public void setListWidth(String width) {
    this.listTable.setWidth(width);
  }

  public void setListWidth(int width) {
    setListWidth(Integer.toString(width));
  }

  public void setListHeight(String height) {
    this.listTable.setHeight(height);
  }

  public void setListHeight(int height) {
    setListHeight(Integer.toString(height));
  }

  public void setZebraColors(String color1, String color2) {
    this.listTable.setHorizontalZebraColored(color1,color2);
  }

  public void setListpadding(int padding) {
    this.listTable.setCellpadding(padding);
  }

  public void setListSpacing(int spacing) {
    this.listTable.setCellspacing(spacing);
  }
}
