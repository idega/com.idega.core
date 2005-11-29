/*
 * $Id: PieChart.java,v 1.6 2005/11/29 15:30:03 laddi Exp $
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
import java.awt.geom.Arc2D;
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
public class PieChart extends Chart{

   public PieChart() {
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
   public String create() throws ChartException {
    if (data_ == null)
      throw new ChartException("No data specified");

    //The width of the picture in pixels
    int width = 450;
    //The height of the picture in pixels
    int height = 300;
    //Specifies the x=0 point for the chart
    int xBarStart = 80;
    //Specifies the y=0 point for the chart
    int yBarStart = height - 30;
    //Specifies the y=maxPlotValue for the chart
    int yBarEnd = 30;
    //Specifies the length of the x-axis on the bar chart. Dependent upon the lengend sizes.
    int xBarLength = yBarStart - yBarEnd;

    //Sum of data entered
    if (data_.length == 0)
      throw new ChartException("No data specified");

    double sumPlotValues = data_[0].doubleValue();
    for (int i = 1; i < data_.length; i++) {
      sumPlotValues += data_[i].doubleValue();
    }

    //How long is the longest legend in pixel
    int max = 0;
    //How long is the current legend in pixel
    int curr = 0;

    BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    //Reverse axes
    AffineTransform trans = new AffineTransform((double)1,(double)0,(double)0,(double)-1,(double)0,(double)height);
    //I matrix
    AffineTransform I = new AffineTransform((double)1,(double)0,(double)0,(double)1,(double)0,(double)0);
    //Matrix for the chart
    AffineTransform chart = new AffineTransform((double)1,(double)0,(double)0,(double)1,(double)xBarStart,(double)yBarEnd);
    Graphics2D g = null;
    FontMetrics fm = null;
    Arc2D arc = null;

    g = image.createGraphics();
    g.setTransform(I);

    g.setColor(backGround_);
    g.fillRect(0,0,width,height);

    if (legend_.length != 0) {
      fm = g.getFontMetrics(g.getFont());
      for (int i = 0; i < legend_.length; i++) {
        curr = fm.stringWidth(legend_[i]);
        if (curr > max)
          max = curr;
      }

      g.setColor(Color.black);
      for (int i = 0; i < legend_.length; i++) {
        g.drawString(legend_[i],width-max-10,40+i*20);
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

    g.setTransform(chart);
    double startAngle = 0;
    double angle = 0;
    for (int i = 0; i < data_.length; i++) {
      if (colours_.length > i)
        g.setColor(colours_[i]);
      else
        g.setColor(Color.yellow);
      angle = (data_[i].doubleValue() / sumPlotValues * 360);
      arc = new Arc2D.Double(0,0,xBarLength,xBarLength,startAngle,angle,Arc2D.PIE);
      g.fill(arc);
      g.setColor(Color.black);
      g.draw(arc);

      DecimalFormat format = new DecimalFormat();
      if (numberOfDigits_ >= 0) {
        format.setMaximumFractionDigits(numberOfDigits_);
        format.setMinimumFractionDigits(numberOfDigits_);
      }

      double an = (startAngle+angle/2)*(2*java.lang.Math.PI/360);
      float x = (float)((xBarLength/2) + (xBarLength/2+10)*java.lang.Math.cos(an));
      float y = (float)((xBarLength/2) - (xBarLength/2+10)*java.lang.Math.sin(an));
      double currAngle = startAngle+angle/2;
      String over = format.format(data_[i].doubleValue()).toString();
      if (addToBarLabel_ != null)
        over = over.concat(addToBarLabel_);
      fm = g.getFontMetrics(g.getFont());
      int stringWidth = fm.stringWidth(over);
      int stringHeight = fm.getHeight();
      if ((currAngle == 0) || (currAngle == 360)){
        g.drawString(over,x,y+(stringHeight/2));
      }
      else if ((currAngle > 0) && (currAngle < 90)) {
        g.drawString(over,x,y);
      }
      else if (currAngle == 90) {
        g.drawString(over,x-(stringWidth/2),y);
      }
      else if ((currAngle > 90) && (currAngle < 180)) {
        g.drawString(over,x-stringWidth,y);
      }
      else if (currAngle == 180) {
        g.drawString(over,x-stringWidth,y+(stringHeight/2));
      }
      else if ((currAngle > 180) && (currAngle < 270)) {
        g.drawString(over,x-stringWidth,y+(stringHeight/2));
      }
      else if (currAngle == 270) {
        g.drawString(over,x-(stringWidth/2),y+(stringHeight/2));
      }
      else if ((currAngle > 270) && (currAngle < 360)) {
        g.drawString(over,x,y+(stringHeight/2));
      }

      startAngle += angle;
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
}
