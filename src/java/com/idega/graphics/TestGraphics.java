package com.idega.graphics;

public class TestGraphics {
  public TestGraphics() {
  }

  public static void main(String[] args) {
    TestGraphics testGraphics = new TestGraphics();
    testGraphics.invokedStandalone = true;

    testGraphics.test();
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
    String legend[] = new String[2];
    legend[0] = "Test 1";
    legend[1] = "Test 2";
    Double data[] = new Double[2];
    data[0] = new Double(46.15);
    data[1] = new Double(53.85);

    try {
      CreateChart chart =new CreateChart();
      chart.setData(data);
      chart.setLegend(legend);
      //Hérna á að nota Constants.getChartFileRootDir() í staðinn
      chart.setFilePrefix("c:\\");
      chart.setFilePostfix("temp");
      chart.setWebPrefix("/tio/no/survey/results/");
      chart.addToBarLabel("%");
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