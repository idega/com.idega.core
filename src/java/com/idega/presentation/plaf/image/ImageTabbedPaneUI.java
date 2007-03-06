package com.idega.presentation.plaf.image;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.plaf.GenericTabbedPaneUI;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.util.IWColor;



/**
 * Title:        IW Objects
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class ImageTabbedPaneUI extends GenericTabbedPaneUI {

  public ImageTabbedPaneUI() {
  }
  public void initTab(){
    setTab(new ImageTabPresentation());
  }

  public void initTabPage(){
    setTabPage(new ImageTabPagePresentation(getMainColor()));
  }


  public class ImageTabPresentation extends GenericTabPresentation {

    public ImageTabPresentation(){
      super();
//      this.setBorder(1);
    }

    public ImageTabPresentation( IWColor color ){
      this();
      this.setCellpadding(0);
      this.setCellspacing(0);
      this.setColor(color);
    }


    public Link getTabLink(PresentationObject obj){
      Link tempLink = null;

      if(obj instanceof ImageTab){
        tempLink = new Link(((ImageTab)obj).getTabNotSelected());
      }else{
        tempLink = new Link(obj.getName());
      }

      if(getForm() != null){
        tempLink.setToFormSubmit(getForm(),true);
      }

      return tempLink;
    }


    public PresentationObject getTab(int index,boolean selected){
      Link tempObj = (Link)this.getAddedTabs().elementAt(index);

      PresentationObject obj = tempObj.getObject();

      if(obj instanceof ImageTab){
        tempObj.setObject( selected ? ((ImageTab)obj).getTabSelected() : ((ImageTab)obj).getTabNotSelected());
      }else{
        tempObj.setText(obj.getName());
      }


      return tempObj;
    }

    public void setSelectedIndex(int index){
      super.setSelectedIndex(index);
      lineUpTabs();
    }

    public void lineUpTabs(){
      this.resize(this.getAddedTabs().size()+1, 1);
      this.empty();

      if(this.getSelectedIndex() == -1 && this.getAddedTabs().size() != 0){
        this.setSelectedIndex(0);
      }

      for (int i = 0; i < this.getAddedTabs().size(); i++) {
        PresentationObject tempObj = this.getTab(i,(this.getSelectedIndex()==i));
        this.add(tempObj,i+1,1);
      }

      this.setWidth("100%");

    }

//  public void empty(PresentationObject obj){}

    /**
     * unimplemented
     */
    public void setWidth(String width){

    }
    /**
     * unimplemented
     */
    public void SetHeight(String height){

    }


    public void main(IWContext iwc) throws Exception {
      this.lineUpTabs();
    }


  } // InnerClass BasicTabPresentation



  public class ImageTabPagePresentation extends GenericTabPagePresentation {

    public ImageTabPagePresentation(){
      super();
    }

    public ImageTabPagePresentation( IWColor color ){
      this();
      this.setColor(color);
      this.setCellpadding(0);
      this.setCellspacing(0);
      this.setWidth("100%");
      initilizePage();
    }


    public void initilizePage(){
      this.resize(1,1);
      this.setColor(getColor().getHexColorString());
      this.setAlignment(1,1,"center");
    }

    public void add(PresentationObject obj){
      this.add(Text.getBreak(),1,1);
      this.add(obj,1,1);
      this.setVerticalAlignment(1,1,"top");
    }
//  public void empty(){}

    public void setWidth(String width){
      super.setWidth(width);
    }

    public void setHeight(String height){
      super.setHeight(height);
    }

    public void empty(){
      super.empty();
    }

    public void fireContentChange(){}

  } // InnerClass GenericTabPagePresentation



} // Class ImageTabbedPaneUI
