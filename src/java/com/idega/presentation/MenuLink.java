package com.idega.presentation;

import com.idega.presentation.text.Link;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class MenuLink extends Block {

  Link menuLink1,menuLink2;
  Image menuImage;
  Image overImage;
  Image stateImage;
  String style;
  int pageId = -1;
  int spaceBefore = 4;
  int spaceBetween = 4;
  int imageWidth;
  int width;
  int height;
  int imageHeight;
  Table container;

  public MenuLink() {
     container = new Table(4,1);
     menuLink2 = new Link();
     menuLink1 = new Link();
  }

  public void setLocalizedText(String localeString,String text){
    menuLink2.setLocalizedText(localeString,text);
  }

  public void setMenuImage(Image image){
    menuImage = image;
  }

  public void setOnMouseOverImage(Image image){
    overImage = image;
  }

  public void setStateImage(Image image){
    stateImage = image;
  }

  public void setPage(ICPage page){
    pageId = page.getID();
  }

  public void setStyle(String style){
    this.style = style;
  }

  public void setImageWidth(int width){
    this.imageWidth = width;
  }
  public void setHeight(int height){
    this.height = height;
  }
  public void setWidth(int width){
    this.width = width;
  }
  public void setSpaceBefore(int space){
    this.spaceBefore = space;
  }
  public void setSpaceBetween(int space){
    this.spaceBetween = space;
  }


  public void initContainer(){
    container.setWidth(width);
    container.setHeight(height);
    container.setWidth(1,1,String.valueOf(spaceBefore));
    container.setWidth(2,1,String.valueOf(imageWidth));
    container.setWidth(3,1,String.valueOf(spaceBetween));
    container.setCellpadding(0);
    container.setCellspacing(0);
  }

  public void main(IWContext iwc)throws Exception{
    initContainer();
	BuilderService bs = getBuilderService(iwc);
    int sessId=bs.getCurrentPageId(iwc);
    if(stateImage!=null && pageId == sessId){
      menuLink1.setImage(stateImage);
    }
    else if(menuImage !=null){
      menuLink1.setImage(menuImage);
      if(overImage !=null){
        menuLink1.setOnMouseOverImage(overImage);
        menuLink2.setOnMouseOverImage(menuImage,overImage);
      }
    }
    if(pageId > 0){
      menuLink1.setPage(pageId);
      menuLink2.setPage(pageId);
    }

    if(style != null)
     menuLink2.setFontStyle(style);
    container.add(menuLink1,2,1);
    container.add(menuLink2,4,1);
    add(container);
  }

  public Object clone() {
    MenuLink obj = null;
    try {
      obj = (MenuLink)super.clone();
      obj.container = (Table)this.container.clone();
      obj.menuLink1 = (Link) this.menuLink1.clone();
      obj.menuLink2 = (Link) this.menuLink2.clone();

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}
