package com.idega.idegaweb.presentation;

import java.util.AbstractList;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.TextInput;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class StatusBar extends Block {

  private String name = "iwstatus";
  private static String input = "input";
  private String inputname = this.name+input;
  private String message = "";
  private int length = 40;
  private String style = "";
  AbstractList interfaceObjects;

  public StatusBar(String name) {
    this.name = name;
    this.inputname = this.name+input;
  }

  public void setName(String name){
    this.name = name;
    this.inputname = this.name+input;
  }

  public void setMessage(String msg){
    this.message = msg;
  }

  public void setSize(int length){
    this.length = length;
  }

  public void setStyle(String style){
    this.style = style;
  }

  public void main(IWContext iwc){
    TextInput input = new TextInput(this.inputname,this.message);
    input.setSize(this.length);
    input.setDisabled(true);
    input.setMarkupAttribute("style",this.style);
    Form statusForm = new Form();
    statusForm.setName(this.name);
    statusForm.add(input);

    add(statusForm);
  }

  public void setMessageCaller(InterfaceObject obj,String msg){
    obj.setOnClick(getMessageScript(msg));
  }

  public void setMessageCaller(Link obj,String msg){
    obj.setOnMouseOver(getMessageScript(msg));
    obj.setOnMouseOut(getMessageScript(""));
  }

  public String getMessageScript(String message){
    return "javascript:document."+this.name+"."+this.inputname+".value ='"+message+"'";
  }

}
