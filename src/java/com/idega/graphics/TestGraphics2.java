package com.idega.graphics;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import com.idega.graphics.GIFEncoder;
import java.awt.Color;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.FontMetrics;

public class TestGraphics2 {


  public TestGraphics2() {
  }

  public static void main(String[] args) {
    TestGraphics2 testGraphics = new TestGraphics2();
    testGraphics.invokedStandalone = true;

    testGraphics.test();
  }
  private boolean invokedStandalone = false;

  public static void test() {
    CreateChart create = new CreateChart();

    java.util.Date date = java.util.Calendar.getInstance().getTime();
    System.out.println("time = " + date.getTime());

    try {
      FileInputStream f = new FileInputStream("c:\\test2.gif");
    }
    catch (Exception e) {
      System.out.println("Exception : " + e);
    }

  }
}
