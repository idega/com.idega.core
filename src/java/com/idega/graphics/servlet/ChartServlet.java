package com.idega.graphics.servlet;

/**
 * Title: ChartServlet
 * Description: A servlet to handle chart drawing and displaying.
 * Copyright: Idega software Copyright (c) 2001
 * Company: idega
 * @author <a href = "mailto:laddi@idega.is">Þórhallur Helgason</a>
 * @version 1.0
 *
 */

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.servlet.IWCoreServlet;

import java.awt.Color;
import java.io.OutputStream;

import com.jrefinery.chart.*;
import com.jrefinery.chart.demo.*;
import com.jrefinery.data.*;

public class ChartServlet extends IWCoreServlet{

  protected JFreeChart createChart() {
    JFreeChart chart;

    try {
      XYDataset xyData1 = DemoDatasetFactory.createTimeSeriesCollection2();
      chart = ChartFactory.createTimeSeriesChart("Þróun forgjafar","DAGSETNING","FORGJÖF",xyData1,true);
      chart.setChartBackgroundPaint(new Color(206,223,208));
      chart.setDataset(xyData1);
      return chart;
    }
    catch (Exception e) {
        return null;
    }
  }

  public void doGet( HttpServletRequest _req, HttpServletResponse _res) throws IOException{
    doPost(_req,_res);
  }

  public void doPost( HttpServletRequest request, HttpServletResponse response) throws IOException{
    response.setContentType("image/png");
    String contentType = request.getParameter("content_type");
    int contType = 2;
    if ( contentType != null ) {
      if ( contentType.equalsIgnoreCase("jpeg") )
        contType = 1;
      if ( contentType.equalsIgnoreCase("png") )
        contType = 2;
      if ( contentType.equalsIgnoreCase("gif") )
        contType = 3;
      response.setContentType("image/"+contentType);
    }

    JFreeChart chart = createChart();

    int width = 640;
    int height = 480;
    try {
        width = Integer.parseInt( request.getParameter( "width" ) );
        height = Integer.parseInt( request.getParameter( "height" ) );
    }
    catch (Exception e) {
    }

    OutputStream out = response.getOutputStream();
    switch (contType) {
      case 1:
        ChartUtilities.writeChartAsJPEG(out, chart, width, height);
        break;
      case 2:
        ChartUtilities.writeChartAsPNG(out, chart, width, height);
        break;
      case 3:
        ChartUtilities.writeChartAsGIF(out, chart, width, height);
        break;
    }

    out.close();
  }
}


