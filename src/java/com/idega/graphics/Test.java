package com.idega.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Test {

  public Test() {
  }

  public static void main(String[] args) {
    Test test = new Test();
    test.draw();
  }

  public void draw() {
    int width = 450;
    int height = 200;

    BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    AffineTransform trans = new AffineTransform((double)1,(double)0,(double)0,(double)-1,(double)0,(double)height);
    Graphics2D g = null;

    g = image.createGraphics();
    g.setTransform(trans);

    g.setColor(new Color(255,255,255));
    g.fillRect(0,0,width,height);

    g.setColor(new Color(0,0,0));
    g.drawLine(5,5,5,195);
    g.drawLine(5,5,445,5);

    g.setColor(new Color(0,0,255));
    g.drawLine(5,178,50,165);
    g.drawLine(50,165,100,114);
    g.drawLine(100,114,150,150);

    try {
      GIFEncoder encode = new GIFEncoder(image);
      Date date = Calendar.getInstance().getTime();
      String filename = "c:\\"+Long.toString(date.getTime());
      filename = filename.concat(".gif");

      OutputStream output = new BufferedOutputStream(new FileOutputStream(filename));

      System.out.println("Chart filename = " + filename);

      encode.Write(output);
      output.close();
    }
    catch (Exception e) {
      System.out.println("Error : " + e);
    }
  }
}