package com.idega.idegaweb.presentation;

import com.idega.presentation.Block;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.InterfaceObject;
import java.util.AbstractList;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class BusyBar extends Block {

  String name = "busy";
  String url = "";
  AbstractList interfaceObjects;

  public BusyBar(String name) {
    this.name = name;
  }

  public void setName(String name){
    this.name = name;
  }

  private void addInterfaceObject(PresentationObject obj){
    if(interfaceObjects==null)
      interfaceObjects = new java.util.Vector();
    interfaceObjects.add(obj);
  }

  public void setInterfaceObject(InterfaceObject obj){
    addInterfaceObject(obj);
  }

   public void setLinkObject(Link obj){
    addInterfaceObject(obj);
  }

  public void main(IWContext iwc){
    url = iwc.getApplication().getCoreBundle().getImage("busy.gif").getURL();
    getParentPage().setOnLoad(Image.getPreloadScript(url));
    Image busy = iwc.getApplication().getCoreBundle().getImage("transparentcell.gif");
    busy.setName(name);
    setScripts();
    add(busy);
  }

  private void setScripts(){
    java.util.Iterator iter = interfaceObjects.iterator();
    while(iter.hasNext()){
      Object o = (Object) iter.next();
      if(o instanceof InterfaceObject ){
        InterfaceObject obj = (InterfaceObject)o;
        obj.setOnClick(getCallingScript());
        obj.setOnClick(getDisabledScript());
      }
      else if(o instanceof Link){
        Link obj = (Link) o;
        obj.setOnClick(getCallingScript());
        obj.setOnClick(getDisabledScript());
      }

    }
  }

  private String getDisabledScript(){
    return "this.disabled = true";

  }

  private String getCallingScript(){
    return "javascript:document.images['"+name+"'].src='"+url+"'";
  }



}