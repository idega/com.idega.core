package com.idega.graphics.generator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

import java.util.Vector;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import com.idega.graphics.GIFEncoder;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.awt.geom.AffineTransform;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.Font;
import com.idega.util.FileUtil;

public class Tab extends Button {
  public static String BUTTON_UP = "_TAB_UP";
  public static  String BUTTON_OVER = "_TAB_OVER";
  public static  String BUTTON_DOWN = "_TAB_DOWN";
  protected boolean drawBorder = false;
  protected int width = 54;
  protected int height = 20;

  public Tab() {
    super();
  }

  public Tab(String text) {
    super(text);
    super.height = this.height;
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

  public void makeButton(Graphics2D g, String text, Image image, String filename, String effect){


    g.setColor(overColor);// delete this when transparencies are supported
    g.fillRect(0,0,width,height);

    if(effect==BUTTON_OVER) {
      g.setColor(highlightColor);
    }
    else if( effect==BUTTON_DOWN ){
      g.setColor(highlightColor);
    }
    else g.setColor(fillColor);
    g.fillRect(1,0,width-doubleBorder,height-doubleBorder);

    g.setColor(overColor);
    g.drawLine(0,0,0,height-doubleBorder-2);
    g.drawLine(1,height-doubleBorder-1,2,height-doubleBorder-1);

    g.setColor(underColor);
    g.drawLine(2,height-doubleBorder-1,width-doubleBorder-1,height-doubleBorder-1);
    g.drawLine(width-2,height-doubleBorder-2,width-2,0);

    g.setColor(borderColor);
    g.drawLine(2,height-doubleBorder,width-doubleBorder-1,height-doubleBorder);
    g.drawLine(width-1,height-doubleBorder-2,width-1,0);
    g.drawLine(width-2,height-doubleBorder-1,width-2,height-doubleBorder-1);

    if( effect==BUTTON_DOWN ){
      //textYPos++;
      g.setColor(fontColor);
    }
    else if( effect==BUTTON_OVER ){
      g.setColor(Color.blue);
    }
    else  g.setColor(fontColor);

    g.drawString(text,textXPos,textYPos-3);


   // g.fillRect(borderSize,borderSize,width-borderSize-2,height-doubleBorder-1);
   //g.fillRect(0,0,width,height);

/*

    if(effect==BUTTON_DOWN) g.setColor(overColor);
    else g.setColor(underColor);

    g.drawLine(borderSize,height-doubleBorder,width-borderSize-1,height-doubleBorder);
    g.drawLine(width-borderSize-1,height-doubleBorder,width-borderSize-1,doubleBorder-1);

    if( effect==BUTTON_DOWN ) textYPos++;
    g.setColor(fontColor);
    g.drawString(text,textXPos,textYPos);*/

    encode(image,filename,effect);

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

}