/*
 * $Id: TestGraphics2.java,v 1.2 2001/05/17 10:26:38 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
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

/**
 *
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
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
