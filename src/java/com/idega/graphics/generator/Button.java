package com.idega.graphics.generator;

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
import java.text.DecimalFormat;
import java.awt.Font;
import java.util.*;
import com.idega.util.FileUtil;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class Button {

  private static final Color defaultBorderColor = new Color(85,87,92);
  private static final int defaultBorderSize = 1;
  private static final Color defaultUnderColor = new Color(148,151,156);
  private static final Color defaultOverColor = Color.white;
  private static final Color defaultFillColor = new Color(201,203,206);
  private static final Color defaultFontColor = Color.black;
  private static final Color defaultHightlightColor = new Color(221,223,226);
  public static final String BUTTON_UP = "_BUTTON_UP";
  public static final String BUTTON_OVER = "_BUTTON_OVER";
  public static final String BUTTON_DOWN = "_BUTTON_DOWN";

  private Color underColor = defaultUnderColor;
  private Color fillColor = defaultFillColor;
  private Color overColor = defaultOverColor;
  private Color borderColor = defaultBorderColor;
  private Color fontColor = defaultFontColor;
  private Color highlightColor = defaultHightlightColor;

  private int borderSize = defaultBorderSize;

  private boolean drawBorder = true;
  private int width = 100;
  private int height = 15;
  private int doubleBorder = (2*borderSize);
  private int textXPos = 5;
  private int textYPos = 10;

  private String buttonUpName;
  private String buttonDownName;
  private String buttonOverName;

  private String text;


  public Button() {
  }

  public Button(String text) {
    this.text = text;
  }

  public Button(String text, int width, int height) {
    this(text);
    this.width = width;
    this.height = height;
  }

  public Button(String text, int width, int height, Color fillColor) {
    this(text,width,height);
    this.fillColor = fillColor;
    highlightColor = fillColor.brighter();
  }

  public Button(String text, Color fillColor) {
    this(text);
    this.fillColor = fillColor;
    highlightColor = fillColor.brighter();
  }

  public static void main(String[] args) {
    Button test = new Button("Tester",Color.orange);
    //Button test = new Button("Tester",30,40,Color.orange);

    //test.setHighlightColor(Color.blue.brighter());
    test.generate();
    System.exit(0);
  }

  public void setFontColor(Color color){
    fontColor = color;
  }

  public void setFillColor(Color color){
    fillColor = color;
  }

  public void setHighlightColor(Color color){
    highlightColor = color;
  }

  public void setBorderColor(Color color){
    borderColor = color;
  }

  public void setOverColor(Color color){
    overColor = color;
  }

  public void setUnderColor(Color color){
    underColor = color;
  }

  public String getButtonUpName(){
    return buttonUpName;
  }

  public String getButtonOverName(){
    return buttonOverName;
  }

  public String getButtonDownName(){
    return buttonDownName;
  }

/*
  public Color getHighlightColor(){
        highlightColor = color;
    new Color(fillColor.getRed()+20,fillColor.getGreen()+20,fillColor.getBlue()+20);

  }*/

  public void generate() {
    generate("");
  }
  public void generate(String folderPath) {

    BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    //AffineTransform trans = new AffineTransform((double)1,(double)0,(double)0,(double)-1,(double)0,(double)height);
    Graphics2D g = null;
    g = image.createGraphics();

    g.setBackground(borderColor);

    try {
      Font font = Font.createFont(Font.TRUETYPE_FONT,new FileInputStream(folderPath+"fonts"+FileUtil.getFileSeparator()+"Spliffy.ttf"));

      Font font2 = font.deriveFont(Font.PLAIN,10.f);

      g.setFont(font2);
      centerText(font2,g);

    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }



    makeButton(g,text,image,folderPath,BUTTON_UP);

    makeButton(g,text,image,folderPath,BUTTON_OVER);

    makeButton(g,text,image,folderPath,BUTTON_DOWN);

  }

  public void centerText(Font font, Graphics2D g){

    FontMetrics fm = g.getFontMetrics(font);
    //System.out.println("Leading : "+fm.getLeading());
    System.out.println("string width : "+fm.stringWidth(text));
    System.out.println("string height : "+fm.getHeight());
    System.out.println("string ascend : "+fm.getAscent());

    int tWidth = fm.stringWidth(text);
    int tHeight = fm.getAscent();

    textXPos = (width-tWidth)/2;
    textYPos = (height+tHeight)/2;
  }

  public void makeButton(Graphics2D g, String text, Image image, String filename, String effect){
   // g.setStroke(new BasicStroke(0.5f));

    if(effect==BUTTON_DOWN) g.setColor(underColor);
    else g.setColor(overColor);

    g.fillRect(borderSize,borderSize,width-borderSize-2,height-doubleBorder-1);

    if(effect==BUTTON_OVER) g.setColor(this.highlightColor);
    else g.setColor(fillColor);
    g.fillRect(doubleBorder,doubleBorder,width-doubleBorder-2,height-doubleBorder-2);

    if(effect==BUTTON_DOWN) g.setColor(overColor);
    else g.setColor(underColor);

    //g.setStroke(new BasicStroke(1f));
    g.drawLine(borderSize,height-doubleBorder,width-borderSize-1,height-doubleBorder);
    g.drawLine(width-borderSize-1,height-doubleBorder,width-borderSize-1,doubleBorder-1);

    //g.setStroke(new BasicStroke(2f));
    //g.drawLine(0,height,width,0);

    g.setColor(fontColor);
    g.drawString(text,textXPos,textYPos);

    try {
      GIFEncoder encode = new GIFEncoder(image);
      Date date = Calendar.getInstance().getTime();

      StringBuffer name = new StringBuffer();
      name.append("test");
      name.append(width);
      name.append("x");
      name.append(height);
      name.append(effect);
      name.append(".gif");

      String sName = name.toString();

      filename+=name.toString();

      if( effect == BUTTON_UP ){
        buttonUpName = sName;
      }
      else if( effect == BUTTON_DOWN ){
        buttonDownName = sName;
      }
      else{
        buttonOverName = sName;
      }

      OutputStream output = new BufferedOutputStream(new FileOutputStream(filename));

      System.out.println("filename = " + filename);

      encode.Write(output);
      output.close();
    }
    catch (Exception e) {
      System.out.println("Error : " + e);
    }

  }
}