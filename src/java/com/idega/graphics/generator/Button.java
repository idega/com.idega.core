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
import java.io.File;
import java.awt.geom.AffineTransform;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.Font;
import com.idega.util.FileUtil;
import com.idega.util.IWColor;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class Button {

  private static Color defaultBorderColor = new Color(85,87,92);
  private static int defaultBorderSize = 1;
  private static Color defaultUnderColor = new Color(148,151,156);
  private static Color defaultOverColor = Color.white;
  private static Color defaultFillColor = new Color(201,203,206);
  private static Color defaultFontColor = Color.black;
  private static Color defaultHightlightColor = new Color(221,223,226);
  public static String BUTTON_UP = "_BUTTON_UP";
  public static String BUTTON_OVER = "_BUTTON_OVER";
  public static String BUTTON_DOWN = "_BUTTON_DOWN";

  protected Color underColor = defaultUnderColor;
  protected Color fillColor = defaultFillColor;
  protected Color overColor = defaultOverColor;
  protected Color borderColor = defaultBorderColor;
  protected Color fontColor = defaultFontColor;
  protected Color highlightColor = defaultHightlightColor;

  protected int borderSize = defaultBorderSize;

  protected boolean drawBorder = true;
  protected int width = 54;
  protected int height = 15;
  protected int doubleBorder = (2*borderSize);
  protected int textXPos = 5;
  protected int textYPos = 10;
  protected int verticalPadding = 5;

  public String buttonUpName;
  public String buttonDownName;
  public String buttonOverName;

  private String text;
  private Font font;
  private BufferedImage image;
  private Graphics2D g;
  private boolean resize = false;

  public Button() {
    this("");
  }

  public Button(String text) {
    this.text = text;
  }

  public Button(String text, Font font) {
    this(text);
    this.font = font;
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


  /**
   * This method allows direct generating of buttons through a command line or cgi script<br/>
   * the allowed parameters in correct order are<br/>
   * 1:textonbutton 2:fillcolor 3:highlightcolor 4:width 5:height 6:pathtofont
   */
  public static void main(String[] args) {
    Button button = new Button();
    button.text = args[0];
    button.fillColor = IWColor.getAWTColorFromHex(args[1]);
    button.highlightColor = IWColor.getAWTColorFromHex(args[2]);
    button.width = Integer.parseInt(args[3]);
    button.height = Integer.parseInt(args[4]);

    try{
      File file = new File(args[5]);
      FileInputStream fis = new FileInputStream(file);
      Font font = Font.createFont(Font.TRUETYPE_FONT, fis);
      button.setFont(font);
    }
    catch(Exception e){
      e.printStackTrace(System.err);
    }

    button.generate();
    System.exit(0);
  }

  public void setFontColor(Color color){
    fontColor = color;
  }

  public void setFont(Font theFont){
    font = theFont;
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

  public String getUpName(){
    return buttonUpName;
  }

  public String getOverName(){
    return buttonOverName;
  }

  public String getDownName(){
    return buttonDownName;
  }

  public String getStaticButtonDownString(){
    return BUTTON_DOWN;
  }

  public String getStaticButtonUpString(){
    return BUTTON_UP;
  }

  public String getStaticButtonOverString(){
    return BUTTON_OVER;
  }

  public int getWidth(){
    return width;
  }

  public int getHeight(){
    return height;
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

    image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

    g = image.createGraphics();

    if( font!= null ) g.setFont(font);
    fitText(g);

    if( resize ){
      image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
      g = image.createGraphics();
      if( font!= null ) g.setFont(font);
    }


    makeButton(g,text,image,folderPath,getStaticButtonUpString());

    makeButton(g,text,image,folderPath,getStaticButtonOverString());

    makeButton(g,text,image,folderPath,getStaticButtonDownString());

  }

  public void fitText(Graphics2D g){

    FontMetrics fm = g.getFontMetrics(g.getFont());
    //System.out.println("Leading : "+fm.getLeading());
    /*System.out.println("string width : "+fm.stringWidth(text));
    System.out.println("string height : "+fm.getHeight());
    System.out.println("string ascend : "+fm.getAscent());*/


    int tWidth = fm.stringWidth(text);
    int tHeight = fm.getAscent();

    if( tWidth >= width ){
      width = tWidth+(2*verticalPadding);
      resize = true;
    }

    textXPos = (width-tWidth)/2;
    textYPos = ((height+tHeight)/2)-1;
  }

  public void makeButton(Graphics2D g, String text, Image image, String filename, String effect){
   // g.setStroke(new BasicStroke(0.5f));

   if( drawBorder){
    g.setColor(borderColor);
    g.fillRect(0,0,width,height);
   }

    if(effect==BUTTON_DOWN) g.setColor(underColor);
    else g.setColor(overColor);

    g.fillRect(borderSize,borderSize,width-borderSize-2,height-doubleBorder-1);

    if(effect==BUTTON_OVER) g.setColor(highlightColor);
    else g.setColor(fillColor);
    g.fillRect(doubleBorder,doubleBorder,width-doubleBorder-2,height-doubleBorder-2);

    if(effect==BUTTON_DOWN) g.setColor(overColor);
    else g.setColor(underColor);

    //g.setStroke(new BasicStroke(1f));
    g.drawLine(borderSize,height-doubleBorder,width-borderSize-1,height-doubleBorder);
    g.drawLine(width-borderSize-1,height-doubleBorder,width-borderSize-1,doubleBorder-1);

    //g.setStroke(new BasicStroke(2f));
    //g.drawLine(0,height,width,0);

    if( effect==BUTTON_DOWN ) textYPos++;
    g.setColor(fontColor);
    g.drawString(text,textXPos,textYPos);

    encode(image,filename,effect);

  }

  public void encode(Image image, String path, String effect){
   try {
      GIFEncoder encode = new GIFEncoder(image);

      StringBuffer name = new StringBuffer();
      name.append(text);
      name.append(width);
      name.append("x");
      name.append(height);
      name.append(effect);
      name.append(".gif");

      String sName = name.toString();

      path+=name.toString();

      if( effect == getStaticButtonUpString() ){
        buttonUpName = sName;
      }
      else if( effect == getStaticButtonDownString() ){
        buttonDownName = sName;
      }
      else{
        buttonOverName = sName;
      }

      OutputStream output = new BufferedOutputStream(new FileOutputStream(path));

      encode.Write(output);
      output.close();
    }
    catch (Exception e) {
      e.printStackTrace(System.out);
    }

  }
}