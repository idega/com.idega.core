package com.idega.presentation;

import com.idega.presentation.ui.Window;

/**
 * Title:        IW Objects
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class BlockWindow extends Window {

  public BlockWindow(){
    super();
  }

  public BlockWindow(String name){
    super(name);
  }

  public BlockWindow(int width, int heigth) {
    super(width,heigth);
  }

  public BlockWindow(String name,int width,int height){
    super(name,width,height);
  }

  public BlockWindow(String name,String url){
    super(name,url);
  }

  public BlockWindow(String name, int width, int height, String url){
    super(name,width,height,url);
  }

  public BlockWindow(String name,String classToInstanciate,String template){
    super(name,classToInstanciate,template);
  }

  public BlockWindow(String name,Class classToInstanciate,Class template){
    super(name,classToInstanciate,template);
  }

  public BlockWindow(String name,Class classToInstanciate){
    super(name,classToInstanciate);
  }

}