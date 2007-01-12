/*
 * $Id: TestGraphics.java,v 1.6.6.1 2007/01/12 19:32:25 idegaweb Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.graphics;

/**
 *
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
public class TestGraphics {
  public TestGraphics() {
  }

  public static void main(String[] args) {
    TestGraphics testGraphics = new TestGraphics();
    testGraphics.invokedStandalone = true;

    TestGraphics.test();
  }
  private boolean invokedStandalone = false;

  public static void test() {
//    System.setProperty ("awt.toolkit", "com.eteks.awt.PJAToolkit");
//    System.setProperty ("java.awt.graphicsenv", "com.eteks.java2d.PJAGraphicsEnvironment");
//    System.setProperty ("awt.toolkit", "com.eteks.awt.PJAToolkit");
//    System.setProperty ("awt.toolkit", "com.eteks.awt.PJAToolkit");
//    System.out.println("toolkit = " + System.getProperty("awt.toolkit"));
//    System.out.println("user.home = " + System.getProperty("user.home"));
//    System.out.println("fontplatform = " + System.getProperty("java2d.font.usePlatformFont"));
//    System.out.println("graphenv = " + System.getProperty("java.awt.graphicsenv"));
    String legend[] = new String[5];
    legend[0] = "Ernir";
    legend[1] = "Fuglar";
    legend[2] = "P�r";
    legend[3] = "Skollar";
    legend[4] = "Skrambar+";
    Double data[] = new Double[5];
    data[0] = new Double(3);
    data[1] = new Double(2);
    data[2] = new Double(86);
    data[3] = new Double(47);
    data[4] = new Double(690);

    try {
      CreateChart chart =new CreateChart();
      chart.setData(data);
      chart.setLegend(legend);
      //H�rna � a� nota Constants.getChartFileRootDir() � sta�inn
      chart.setFilePrefix("c:\\");
      chart.setFilePostfix("temp");
      chart.setWebPrefix("/tio/no/survey/results/");
      chart.addToBarLabel("");
      chart.setNumberOfBarLabelDigits(2);
//      String url = chart.createChart(CreateChart.BARCHART);
      String url = chart.createChart(CreateChart.PIECHART);
      System.out.println("url = " + url);
    }
    catch (ChartException e) {
      System.out.println("Error : " + e);
    }
  }
}