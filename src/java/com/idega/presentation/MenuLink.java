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
     this.container = new Table(4,1);
     this.menuLink2 = new Link();
     this.menuLink1 = new Link();
  }

  public void setLocalizedText(String localeString,String text){
    this.menuLink2.setLocalizedText(localeString,text);
  }

  public void setMenuImage(Image image){
    this.menuImage = image;
  }

  public void setOnMouseOverImage(Image image){
    this.overImage = image;
  }

  public void setStateImage(Image image){
    this.stateImage = image;
  }

  public void setPage(ICPage page){
    this.pageId = page.getID();
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
    this.container.setWidth(this.width);
    this.container.setHeight(this.height);
    this.container.setWidth(1,1,String.valueOf(this.spaceBefore));
    this.container.setWidth(2,1,String.valueOf(this.imageWidth));
    this.container.setWidth(3,1,String.valueOf(this.spaceBetween));
    this.container.setCellpadding(0);
    this.container.setCellspacing(0);
  }

  public void main(IWContext iwc)throws Exception{
    initContainer();
	BuilderService bs = getBuilderService(iwc);
    int sessId=bs.getCurrentPageId(iwc);
    if(this.stateImage!=null && this.pageId == sessId){
      this.menuLink1.setImage(this.stateImage);
    }
    else if(this.menuImage !=null){
      this.menuLink1.setImage(this.menuImage);
      if(this.overImage !=null){
        this.menuLink1.setOnMouseOverImage(this.overImage);
        this.menuLink2.setOnMouseOverImage(this.menuImage,this.overImage);
      }
    }
    if(this.pageId > 0){
      this.menuLink1.setPage(this.pageId);
      this.menuLink2.setPage(this.pageId);
    }

    if(this.style != null) {
			this.menuLink2.setFontStyle(this.style);
		}
    this.container.add(this.menuLink1,2,1);
    this.container.add(this.menuLink2,4,1);
    add(this.container);
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
