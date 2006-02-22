package com.idega.presentation.app;

import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;

/**
 * <p>
 * Class that presents a "application component" or a part of a application on screen.<br>
 * This is used e.g. by the Builder but is mostly irrelevant in new versions - can now be accomplished by setting the style
 * class "iw_applicationcomponent".
 * </p>
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWApplicationComponent extends Page {


  public IWApplicationComponent(){
    //setBackgroundColor(IWConstants.DEFAULT_LIGHT_INTERFACE_COLOR);
    //super.add(getIWACTable());
    //setAllMargins(0);
  	setStyleClass("iw_applicationcomponent");
  }

  public void add(PresentationObject obj){
    //iwacTable.add(obj,2,2);
  	super.add(obj);
  }

  public void _main(IWContext iwc) throws Exception {
    super._main(iwc);
    setImages(iwc);
  }

  /**
 * @param iwc
 */
public void setImages(IWContext iwc){
/*    if(!imagesSet){

//        topleft = iwb.getImage("iwapplication/component_topleft.gif");
//        topright =iwb.getImage("iwapplication/component_topright.gif");
//        bottomleft =iwb.getImage("iwapplication/component_bottomleft.gif");
//        bottomright =iwb.getImage("iwapplication/component_bottomright.gif");
//        top=iwb.getImage("iwapplication/component_toptiler.gif");
//        bottom=iwb.getImage("iwapplication/component_bottomtiler.gif");
//        left=iwb.getImage("iwapplication/component_lefttiler.gif");
//        right=iwb.getImage("iwapplication/component_righttiler.gif");

      imagesSet=true;
    }
        Image emptyCell = Table.getTransparentCell(iwc);
          emptyCell.setWidth(1);
          emptyCell.setHeight(1);
        Image tilerCell = (Image) emptyCell.clone();
          tilerCell.setHeight("100%");

//        iwacTable.setBackgroundImage(1,1,topleft);
//        iwacTable.setBackgroundImage(3,1,topright);
//        iwacTable.setBackgroundImage(1,3,bottomleft);
//        iwacTable.setBackgroundImage(3,3,bottomright);
//        iwacTable.setBackgroundImage(2,1,top);
//        iwacTable.setBackgroundImage(2,3,bottom);
//        iwacTable.setBackgroundImage(1,2,left);
//        iwacTable.setBackgroundImage(3,2,right);

        iwacTable.setColor(1,1,_lightColor);
        iwacTable.setColor(3,1,_lightColor);
        iwacTable.setColor(1,3,_lightColor);
        iwacTable.setColor(3,3,_darkColor);
        iwacTable.setColor(2,1,_lightColor);
        iwacTable.setColor(2,3,_darkColor);
        iwacTable.setColor(1,2,_lightColor);
        iwacTable.setColor(3,2,_darkColor);

        iwacTable.add(emptyCell,1,1);
        iwacTable.add(emptyCell,2,1);
        iwacTable.add(emptyCell,3,1);
        iwacTable.add(emptyCell,1,3);
        iwacTable.add(emptyCell,2,3);
        iwacTable.add(emptyCell,3,3);
        iwacTable.add(tilerCell,1,2);
        iwacTable.add(tilerCell,3,2);*/
  }

  public void setAlignment(String alignment) {
    //iwacTable.setAlignment(2,2,alignment);
  }

  public void setVerticalAlignment(String alignment) {
    //iwacTable.setVerticalAlignment(2,2,alignment);
  }

  public void setLightShadowColor(String color) {
  }

  public void setDarkShadowColor(String color) {
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
