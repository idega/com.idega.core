package com.idega.graphics.generator;

import java.util.Vector;
import java.awt.Color;
import java.awt.image.BufferedImage;
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

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class Button {

  protected static final Color defaultBorderColor = new Color(85,87,92);
  protected static final int defaultBorderSize = 1;
  private int borderSize = defaultBorderSize;
  private Color darkSideColor = new Color(148,151,156);
  private Color lightSideColor = Color.white;
  private Color fillColor = new Color(201,203,206);
  private Color borderColor = defaultBorderColor;
  private Color fontColor = Color.black;
  private boolean drawBorder = true;


  public Button() {
  }

  public static void main(String[] args) {
    Button test = new Button();
    test.generate();
    System.exit(0);
  }

  public void generate() {

    int width = 50+borderSize+1;
    int height = 18+borderSize+1;
    int doubleBorder = (2*borderSize);

    BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    //AffineTransform trans = new AffineTransform((double)1,(double)0,(double)0,(double)-1,(double)0,(double)height);
    Graphics2D g = null;
   // FontMetrics fm = null;

    g = image.createGraphics();

    g.setBackground(borderColor);
    g.setStroke(new BasicStroke(0.5f));
    //g.setTransform(trans);

    //g.setColor(borderColor);
    //g.fillRect(0,0,width,height);

    g.setColor(lightSideColor);
    g.fillRect(borderSize,borderSize,width-borderSize-2,height-doubleBorder-2);

    g.setColor(fillColor);
    g.fillRect(doubleBorder,doubleBorder,width-doubleBorder-2,height-doubleBorder-3);

        g.setColor(darkSideColor);
    g.setStroke(new BasicStroke(1f));
    g.drawLine(borderSize,height-doubleBorder-1,width-borderSize,height-borderSize);
    g.drawLine(width-borderSize-1,height-borderSize,width-borderSize,doubleBorder);

        g.setStroke(new BasicStroke(2f));
g.drawLine(0,height,width,0);
/*
    g.setColor(fontColor);
    g.drawString("test",5,height-5);*/

/*
    g.setColor(Color.black);
    g.drawLine(5,178,50,165);
    g.drawLine(50,165,100,114);
    g.drawLine(100,114,150,150);*/

    try {
      GIFEncoder encode = new GIFEncoder(image);
      Date date = Calendar.getInstance().getTime();
      String filename = "buttontest";//+Long.toString(date.getTime());
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