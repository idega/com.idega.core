/*
 * $Id: BarChart.java,v 1.6 2005/11/29 15:30:03 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
public class BarChart extends Chart {

  public BarChart() {
    colours_ = new Color[5];
    colours_[0] = new Color(194,180,166);
    colours_[1] = new Color(140,159,208);
    colours_[2] = new Color(125,161,158);
    colours_[3] = new Color(255,239,222);
    colours_[4] = new Color(225,180,143);

    backGround_ = new Color(224,215,194);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public String create() throws ChartException{
    //The width of the picture in pixels
    int width = 450;
    //The height of the picture in pixels
    int height = 200;
    //Specifies the x=0 point for the chart
    int xBarStart = 30;
    //Specifies the y=0 point for the chart
    int yBarStart = height - 30;
    //Specifies the y=maxPlotValue for the chart
    int yBarEnd = 30;
    //Specifies the length of the x-axis on the bar chart. Dependent upon the lengend sizes.
    int xBarLength = width - xBarStart - 10;
    //The highest value to be plotted.
    if (data_.length == 0)
      throw new ChartException("No data specified");

    double maxPlotValue = data_[0].doubleValue();
    for (int i = 1; i < data_.length; i++) {
      if (data_[i].doubleValue() > maxPlotValue)
        maxPlotValue = data_[i].doubleValue();
    }

    maxPlotValue = getRoundedValue(maxPlotValue);

    //How long is the longest legend in pixel
    int max = 0;
    //How long is the current legend in pixel
    int curr = 0;

/*    if (legend_.length != 0) {
      fm = g.getFontMetrics(g.getFont());
      for (int i = 0; i < legend_.length; i++) {
        curr = fm.stringWidth(legend_[i]);
        if (curr > max)
          max = curr;
      }
    }*/
    int extraHeight = legend_.length * 20;

/*    if (max > 150) {
      extraHeight = legend_.length * 20;
      max = -1;
    }*/

    height += extraHeight;

    BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    //Reverse axes
    AffineTransform trans = new AffineTransform((double)1,(double)0,(double)0,(double)-1,(double)0,(double)height);
    //I matrix
    AffineTransform I = new AffineTransform((double)1,(double)0,(double)0,(double)1,(double)0,(double)0);
    //Matrix for the chart
    AffineTransform chart = new AffineTransform((double)1,(double)0,(double)0,(double)-1,(double)xBarStart,(double)yBarStart);
    AffineTransform chartText = null;
    Graphics2D g = null;
    FontMetrics fm = null;

    yBarEnd += extraHeight;
    yBarStart += extraHeight;

    g = image.createGraphics();
    g.setTransform(I);

    g.setColor(backGround_);
    g.fillRect(0,0,width,height);

    if (legend_.length != 0) {
/*      if (max > -1) {
        g.setColor(Color.black);
        for (int i = 0; i < legend_.length; i++) {
          g.drawString((String)legend_[i],width-max-10,40+i*20);
        }

        g.setTransform(trans);

        for (int i = 0; i < legend_.length; i++) {
          if (colours_.length > i)
            g.setColor(colours_[i]);
          else
            g.setColor(Color.yellow);
          g.fillRect(width-max-30,height-40-i*20,10,10);
          g.setColor(Color.black);
          g.drawRect(width-max-30,height-40-i*20,10,10);
        }
      }
      else {*/
        int start = height - legend_.length * 20;
        g.setColor(Color.black);
        for (int i = 0; i < legend_.length; i++) {
          g.drawString(legend_[i],xBarStart,start+i*20);
        }

        g.setTransform(trans);

        for (int i = 0; i < legend_.length; i++) {
          if (colours_.length > i)
            g.setColor(colours_[i]);
          else
            g.setColor(Color.yellow);
          g.fillRect(xBarStart-20,height-start-i*20,10,10);
          g.setColor(Color.black);
          g.drawRect(xBarStart-20,height-start-i*20,10,10);
//        }
      }
    }

    g.setTransform(trans);
    xBarLength = width - max - xBarStart - 40;
    g.setColor(Color.black);
    g.drawLine(xBarStart,yBarEnd,xBarLength+xBarStart,yBarEnd);
    g.drawLine(xBarStart,yBarEnd,xBarStart,yBarStart);
    int step = (yBarStart - yBarEnd) / 5;
    for (int i = 0; i < 6; i++)
      g.drawLine(xBarStart,yBarEnd+i*step,xBarStart-5,yBarEnd+i*step);

    int barwidth = (int)(xBarLength / (double)data_.length);

    g.setTransform(chart);
    for (int i = 0; i < data_.length; i++) {
      if (colours_.length > i)
        g.setColor(colours_[i]);
      else
        g.setColor(Color.yellow);
      g.fillRect(5 + i * barwidth,0,barwidth-10,(int)(data_[i].doubleValue()/maxPlotValue*(yBarStart-yBarEnd)));
      g.setColor(Color.black);
      g.drawRect(5 + i * barwidth,0,barwidth-10,(int)(data_[i].doubleValue()/maxPlotValue*(yBarStart-yBarEnd)));

      DecimalFormat format = new DecimalFormat();
      if (numberOfDigits_ >= 0) {
        format.setMaximumFractionDigits(numberOfDigits_);
        format.setMinimumFractionDigits(numberOfDigits_);
      }

      String over = format.format(data_[i].doubleValue()).toString();
      if (addToBarLabel_ != null)
        over = over.concat(addToBarLabel_);
      fm = g.getFontMetrics(g.getFont());
      curr = fm.stringWidth(over);
      float center = 0;
      if (curr < barwidth)
        center = (barwidth - curr)/(float)2.0;
      chartText = new AffineTransform(1,0,0,1,xBarStart,yBarStart - (data_[i].doubleValue()/maxPlotValue*(yBarStart-yBarEnd))-5 - extraHeight);
      g.setTransform(chartText);
      g.drawString(over,(i * barwidth+center),(float)0.0);
      g.setTransform(chart);
    }

    try {
      GIFEncoder encode = new GIFEncoder(image);
      Date date = Calendar.getInstance().getTime();
      String filename = Long.toString(date.getTime());
      URL_ = filename;
      if (prefix_ != null)
        filename = new String(prefix_ + filename);
      if (postfix_ != null)
        filename = filename.concat(postfix_);
      filename = filename.concat(".gif");

      OutputStream output = new BufferedOutputStream(new FileOutputStream(filename));

System.out.println("Chart filename = " + filename);

      encode.Write(output);
      output.close();
    }
    catch (Exception e) {
      System.out.println("Error : " + e);
    }

    if (webPrefix_ != null)
      URL_ = new String(webPrefix_ + URL_);
    if (postfix_ != null)
      URL_ = URL_.concat(postfix_);
    URL_ = URL_.concat(".gif");

    return(URL_);
  }

  private double getRoundedValue(double inValue) {
    int intValue = (int)inValue;

    //Þarf að búa til almennilega rútínu hérna. Á þetta til í Berghita forritinu, og set það inn seinna.

    if (intValue == 0)
      return(1);
    else if (intValue <= 5)
      return(5);
    else if (intValue <= 10)
      return(10);
    else if (intValue <= 50)
      return(50);
    else if (intValue <= 100)
      return(100);
    else if (intValue <= 200)
      return(200);
    else if (intValue <= 300)
      return(300);
    else if (intValue <= 400)
      return(400);
    else if (intValue <= 500)
      return(500);
    else if (intValue <= 600)
      return(600);
    else if (intValue <= 700)
      return(700);
    else if (intValue <= 800)
      return(800);
    else if (intValue <= 900)
      return(900);
    else if (intValue <= 1000)
      return(1000);
    else
      return(2001);

  }
}
