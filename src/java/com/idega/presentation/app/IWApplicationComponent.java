package com.idega.presentation.app;

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

public class IWApplicationComponent extends Page {


  private static boolean imagesSet=false;

  private static Image topleft;
  private static Image topright;
  private static Image bottomleft;
  private static Image bottomright;
  private static Image top;
  private static Image bottom;
  private static Image left;
  private static Image right;

  private Table iwacTable;

  public IWApplicationComponent(){
    setBackgroundColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
    super.add(getIWACTable());
    setAllMargins(0);
  }

  public void add(PresentationObject obj){
    iwacTable.add(obj,2,2);
  }

  private Table getIWACTable(){
    if(iwacTable == null){
      iwacTable = new Table(3,3);
      iwacTable.setHeight("100%");
      iwacTable.setWidth("100%");
      iwacTable.setCellpadding(0);
      iwacTable.setCellspacing(0);
      iwacTable.setHeight(1,"2");
      iwacTable.setHeight(3,"2");
      iwacTable.setWidth(1,"2");
      iwacTable.setWidth(3,"2");
    }
    return iwacTable;
  }

  public void _main(IWContext iwc) throws Exception {
    super._main(iwc);
    setImages(iwc);
  }

  public void setImages(IWContext iwc){
    if(!imagesSet){
      IWBundle iwb = this.getBundle(iwc);

        topleft = iwb.getImage("iwapplication/component_topleft.gif");
        topright =iwb.getImage("iwapplication/component_topright.gif");
        bottomleft =iwb.getImage("iwapplication/component_bottomleft.gif");
        bottomright =iwb.getImage("iwapplication/component_bottomright.gif");
        top=iwb.getImage("iwapplication/component_toptiler.gif");
        bottom=iwb.getImage("iwapplication/component_bottomtiler.gif");
        left=iwb.getImage("iwapplication/component_lefttiler.gif");
        right=iwb.getImage("iwapplication/component_righttiler.gif");

      imagesSet=true;
    }
        Image emptyCell = Table.getTransparentCell(iwc);
          emptyCell.setWidth(2);
          emptyCell.setHeight(2);
        Image tilerCell = (Image) emptyCell.clone();
          tilerCell.setHeight("100%");

        iwacTable.setBackgroundImage(1,1,topleft);
        iwacTable.setBackgroundImage(3,1,topright);
        iwacTable.setBackgroundImage(1,3,bottomleft);
        iwacTable.setBackgroundImage(3,3,bottomright);
        iwacTable.setBackgroundImage(2,1,top);
        iwacTable.setBackgroundImage(2,3,bottom);
        iwacTable.setBackgroundImage(1,2,left);
        iwacTable.setBackgroundImage(3,2,right);

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

  static IWApplicationComponent getAppCompInstance(Class componentClass,IWContext iwc){
    try{
      return (IWApplicationComponent)componentClass.newInstance();
    }
    catch(Exception e){
      return null;
    }
  }



}