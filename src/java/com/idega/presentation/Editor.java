package com.idega.presentation;



import com.idega.presentation.text.Text;
import com.idega.presentation.ui.InterfaceObject;



/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega multimedia

 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>

 * @version 1.0

 */



public abstract class Editor extends com.idega.presentation.PresentationObjectContainer {



  protected final static int ACT1 = 1,ACT2 = 2, ACT3 = 3,ACT4  = 4,ACT5 = 5,ACT6=6;

  protected boolean isAdmin = false;

  protected String MiddleColor,LightColor,DarkColor,WhiteColor,TextFontColor,HeaderFontColor,IndexFontColor;

  protected Table Frame,MainFrame,HeaderFrame;

  protected int BORDER = 0;

  protected String sHeader = null;

  protected int fontSize = 2;

  protected boolean fontBold = false;

  protected String styleAttribute = "font-size: 8pt";

  private int iBorder = 2;



  public Editor(String sHeader){

    this.LightColor = "#D7DADF";

    this.MiddleColor = "#9fA9B3";

    this.DarkColor = "#27334B";

    this.WhiteColor = "#FFFFFF";

    this.TextFontColor = "#000000";

    this.HeaderFontColor = this.DarkColor;

    this.IndexFontColor = "#000000";

    this.sHeader = sHeader;

  }



  public Editor(){

    this.LightColor = "#D7DADF";

    this.MiddleColor = "#9fA9B3";

    this.DarkColor = "#27334B";

    this.WhiteColor = "#FFFFFF";

    this.TextFontColor = "#000000";

    this.HeaderFontColor = this.DarkColor;

    this.IndexFontColor = "#000000";

    this.sHeader = null;

  }



  protected abstract void control(IWContext iwc);



  public void setColors(String LightColor,String MainColor,String DarkColor){

    if(LightColor.startsWith("#")) {
		this.LightColor = LightColor;
	}

    if(MainColor.startsWith("#")) {
		this.MiddleColor = MainColor;
	}

    if(DarkColor.startsWith("#")) {
		this.DarkColor = DarkColor;
	}

  }

  public void setBorder(int border){

    this.iBorder = border;

  }

  public void setHeaderText(String sHeader){

    this.sHeader = sHeader;

  }

  public void setTextFontColor(String color){

    this.TextFontColor = color;

  }

  public void setHeaderFontColor(String color){

    this.HeaderFontColor = color;

  }

  public void setIndexFontColor(String color){

    this.IndexFontColor = color;

  }

  public void setTextFontSize(int size){

    this.fontSize = size;

  }

  public void setTextFontBold(boolean bold){

    this.fontBold = bold;

  }

  public void setStyleAttribute(String style){

    this.styleAttribute = style;

  }

  protected void makeView(){

    this.makeMainFrame();

    this.makeFrame();

    this.makeHeader();

  }

  protected void makeMainFrame(){

    this.MainFrame = new Table(1,2);

    this.MainFrame.setWidth("100%");

    this.MainFrame.setCellspacing(0);

    this.MainFrame.setCellpadding(0);

    this.MainFrame.setBorder(this.BORDER);

    add(this.MainFrame);

  }

  protected void makeFrame(){

    this.Frame = new Table(1,2);

    this.Frame.setCellspacing(0);

    this.Frame.setCellpadding(0);

    this.Frame.setWidth("100%");

    this.Frame.setBorder(this.BORDER);

    this.addFrame();

  }

  protected void makeHeader(){

    this.HeaderFrame = new Table();

    if(this.sHeader != null){

      this.HeaderFrame = new Table(2,1);

      this.HeaderFrame.setColumnAlignment(2,"right");

      Text T = new Text(this.sHeader);

      T.setBold();

      T.setFontColor(this.DarkColor);

      this.HeaderFrame.add(T,1,1);

    }

    this.HeaderFrame.setBorder(this.BORDER);

    this.addHeader(this.HeaderFrame);

  }

  protected void addFrame(){

    Table BorderTable = new Table(1,1);

    BorderTable.setBorder(this.BORDER);

    BorderTable.setCellpadding(this.iBorder);

    BorderTable.setCellspacing(0);

    BorderTable.setColor(this.DarkColor);

    BorderTable.setWidth("100%");

    Table whiteTable = new Table(1,1);

    whiteTable.setBorder(this.BORDER);

    whiteTable.setColor(this.WhiteColor);

    whiteTable.setCellpadding(2);

    whiteTable.setCellspacing(0);

    whiteTable.setWidth("100%");

    whiteTable.add(this.Frame);

    BorderTable.add(whiteTable);

    this.MainFrame.add(BorderTable,1,2);

  }

  protected void addMain(PresentationObject T){

    this.Frame.add(T,1,2);

  }



  protected void addLinks(PresentationObject T){

    this.MainFrame.add(T,1,1);

  }

  protected void addHeader(PresentationObject T){

    this.Frame.add(T,1,1);

  }

  protected void addToHeader(PresentationObject T){

    this.HeaderFrame.add(T);

  }

  protected void addToRightHeader(PresentationObject T){

    if(this.sHeader != null) {
		this.HeaderFrame.add(T,2,1);
	}

  }

  protected void addMsg(PresentationObject T){



  }

  public Text formatText(String s){

    Text T= new Text();

    if(s!=null){

      T= new Text(s);

      if(this.fontBold) {
		T.setBold();
	}

      T.setFontColor(this.TextFontColor);

      T.setFontSize(this.fontSize);

    }

    return T;

  }

  public Text formatText(int i){

    return formatText(String.valueOf(i));

  }

  protected void setStyle(InterfaceObject O){

    O.setMarkupAttribute("style",this.styleAttribute);

  }

  public void main(IWContext iwc){



    this.isAdmin = iwc.hasEditPermission(this);

    control(iwc);

  }

}// class TariffKeyEditor
