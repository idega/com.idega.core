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
import java.awt.geom.AffineTransform;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.util.Date;
import java.util.Calendar;
import java.text.DecimalFormat;
import java.awt.Font;

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
  private static final int BUTTON_UP = 1;
  private static final int BUTTON_HIGHLIGHT = 2;
  private static final int BUTTON_DOWN = 3;


  private Color underColor = defaultUnderColor;
  private Color fillColor = defaultFillColor;
  private Color overColor = defaultOverColor;
  private Color borderColor = defaultBorderColor;
  private Color fontColor = defaultFontColor;
  private Color highlightColor = defaultHightlightColor;

  private int borderSize = defaultBorderSize;

  private boolean drawBorder = true;
  int width = 100;
  int height = 15;
  int doubleBorder = (2*borderSize);



  public Button() {
  }

  public static void main(String[] args) {
    Button test = new Button();
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

  public void generate() {

    BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    //AffineTransform trans = new AffineTransform((double)1,(double)0,(double)0,(double)-1,(double)0,(double)height);
    Graphics2D g = null;
   // FontMetrics fm = null;

    g = image.createGraphics();

    g.setBackground(borderColor);

    String buttonText = "Þetta er flott";

    makeButton(g,buttonText,image,"buttontestup",BUTTON_UP);

    makeButton(g,buttonText,image,"buttontesthighlight",BUTTON_HIGHLIGHT);

    makeButton(g,buttonText,image,"buttontestdown",BUTTON_DOWN);

  }

  public void makeButton(Graphics2D g, String text, Image image, String filename, int effect){
    g.setStroke(new BasicStroke(0.5f));

    if(effect==BUTTON_DOWN) g.setColor(underColor);
    else g.setColor(overColor);

    g.fillRect(borderSize,borderSize,width-borderSize-2,height-doubleBorder-1);

    if(effect==BUTTON_HIGHLIGHT) g.setColor(this.highlightColor);
    else g.setColor(fillColor);
    g.fillRect(doubleBorder,doubleBorder,width-doubleBorder-2,height-doubleBorder-2);

    if(effect==BUTTON_DOWN) g.setColor(overColor);
    else g.setColor(underColor);

    //g.setStroke(new BasicStroke(1f));
    g.drawLine(borderSize,height-doubleBorder,width-borderSize-1,height-doubleBorder);
    g.drawLine(width-borderSize-1,height-doubleBorder,width-borderSize-1,doubleBorder-1);

    //g.setStroke(new BasicStroke(2f));
    //g.drawLine(0,height,width,0);

    String fontpath = System.getProperty("java.awt.fonts");
    System.out.println("Fontpath="+fontpath);
   // System.setProperty("java.awt.fonts","c:\temp\fonts\");

    g.setColor(fontColor);
    Font font = new Font("Impact",Font.PLAIN,10);
    g.setFont(font);
    g.drawString(text,5,10);

    try {
      GIFEncoder encode = new GIFEncoder(image);
      Date date = Calendar.getInstance().getTime();
      filename = filename.concat(".gif");

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