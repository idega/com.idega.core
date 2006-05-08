package com.idega.graphics.generator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;

public class Tab extends Button {
  private static String BUTTON_UP = "_TAB_UP";
  private static  String BUTTON_OVER = "_TAB_OVER";
  private static  String BUTTON_DOWN = "_TAB_DOWN";
  protected boolean firstRun = true;

  private boolean flip = false;

  public Tab() {
    super();
    this.drawBorder = false;
    this.height = 15;
  }

  public Tab(String text) {
    super(text);
  }

  public Tab(String text, Font font) {
    super(text,font);
  }

  public Tab(String text, int width, int height) {
    super(text,width,height);
  }

  public Tab(String text, int width, int height, Color fillColor) {
    super(text,width,height,fillColor);
  }

  public Tab(String text, Color fillColor) {
    super(text,fillColor);
  }

  public static void main(String[] args) {
    Tab test = new Tab("Tester");
    //Button test = new Button("Tester",30,40,Color.orange);

    //test.setHighlightColor(Color.blue.brighter());
    test.generate();
    System.exit(0);
  }

  public void generate(String filePath){
    super.height = this.height;
    super.generate(filePath);
  }

  public void makeButton(Graphics2D g, String text, Image image, String filename, String effect){

    /*if( flip ){
      g.transform(flipTransform);
      if( flip ) textYPos = height-2;
    }*/
    if(!this.flip){

       this.textYPos = this.height-1;

      //defaultOverColor==white
      //defaultHightlightColor = light light gray
      //defaultFillColor==light gray
      //defaultUnderColor= gray
      //defaultBorderColor= dark gray

      g.setColor(this.backgroundColor);// delete this when transparencies are supported
      g.fillRect(0,0,this.width,this.height);

      if(effect==BUTTON_OVER) {
        g.setColor(this.highlightColor);
      }
      else if( effect==BUTTON_DOWN ){
        g.setColor(this.highlightColor);
      }
			else {
				g.setColor(this.fillColor);
			}

      g.drawRect(2,1,this.width-4,this.height);
      g.fillRect(2,1,this.width-4,this.height);
      g.fillRect(1,2,1,this.height-2);

      g.setColor(this.overColor);
      g.fillRect(0,2,1,this.height-2);
      g.fillRect(1,1,1,1);
      g.fillRect(2,0,this.width-4,1);

      g.setColor(this.underColor);
      g.fillRect(this.width-2,2,1,this.height-2);

      g.setColor(this.borderColor);
      g.fillRect(this.width-2,1,1,1);
      g.fillRect(this.width-1,2,1,this.height-2);
    }
    else{
      g.setColor(this.overColor);// delete this when transparencies are supported
      g.fillRect(0,0,this.width,this.height);

      if(effect==BUTTON_OVER) {
        g.setColor(this.highlightColor);
      }
      else if( effect==BUTTON_DOWN ){
        g.setColor(this.highlightColor);
      }
			else {
				g.setColor(this.fillColor);
			}

      g.fillRect(1,0,this.width-this.doubleBorder,this.height-this.doubleBorder);

      g.setColor(this.overColor);
      g.drawLine(0,0,0,this.height-this.doubleBorder-2);
      g.drawLine(1,this.height-this.doubleBorder-1,2,this.height-this.doubleBorder-1);

      g.setColor(this.underColor);
      g.drawLine(2,this.height-this.doubleBorder-1,this.width-this.doubleBorder-1,this.height-this.doubleBorder-1);
      g.drawLine(this.width-2,this.height-this.doubleBorder-2,this.width-2,0);

      g.setColor(this.borderColor);
      g.drawLine(2,this.height-this.doubleBorder,this.width-this.doubleBorder-1,this.height-this.doubleBorder);
      g.drawLine(this.width-1,this.height-this.doubleBorder-2,this.width-1,0);
      g.drawLine(this.width-2,this.height-this.doubleBorder-1,this.width-2,this.height-this.doubleBorder-1);
    }
    if( effect==BUTTON_DOWN ){
      //textYPos++;
      g.setColor(this.fontColor);
    }
    else if( effect==BUTTON_OVER ){
      g.setColor(Color.blue);
    }
		else {
			g.setColor(this.fontColor);
		}

    //if( flip ) g.transform(flipTransform);

    g.drawString(text,this.textXPos,this.textYPos-3);

    encode(image,filename+this.flip,effect);

  }

  public String getStaticButtonDownString(){
    return Tab.BUTTON_DOWN;
  }

  public String getStaticButtonUpString(){
    return Tab.BUTTON_UP;
  }

  public String getStaticButtonOverString(){
    return Tab.BUTTON_OVER;
  }

  public void flip(boolean flip){
   this.flip = flip;
  }

  public void setBackgroundColor(Color color){
    this.backgroundColor=color;
  }

}