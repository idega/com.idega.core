/*
 * $Id: CreateChart.java,v 1.6 2004/06/24 20:12:24 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.graphics;

import java.awt.Color;

/**
 *
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
public class CreateChart {

  /*
   * The data to be plotted
   */
  private Double data_[] = null;
  /*
   * The legend for the data
   */
  private String legend_[] = null;

	/**
	 * 
	 * @uml.property name="chart_"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	/*
	 * The chart itself
	 */
	private Chart chart_ = null;

  /*
   * The background colour for the picture
   */
  private Color backGround_ = null;
  /*
   * The colours for the chart elements
   */
  private Color colours_[] = null;
  /*
   * The pre- and post-fix for the file names
   */
  private String prefix_ = null;
  private String postfix_ = null;
  /*
   * The prefix for the chart url
   */
  private String webPrefix_ = null;

  private int numberOfDigits_ = -1;
  private String addToBarLabel_ = null;


  /*
   * The types of charts supported
   */
  public static int PIECHART = 0;
  public static int BARCHART = 1;

  public CreateChart() {
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   */
  public boolean setLegend(String legend[]) {
    if (legend.length == 0)
      return(false);

    legend_ = new String[legend.length];
    for (int i = 0; i < legend.length; i++)
      legend_[i] = new String(legend[i]);

    return(true);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   */
  public boolean setData(Double data[]) {
    if (data.length == 0)
      return(false);

    data_ = new Double[data.length];
    for (int i = 0; i < data.length; i++)
      data_[i] = new Double(data[i].doubleValue());

    return(true);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   */
  public void setBackgroundColour(Color c) {
    backGround_ = c;
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   */
  public boolean setChartColours(Color colours[]) {
    if (colours.length == 0)
      return(false);

    colours_ = new Color[colours.length];
    for (int i = 0; i < colours.length; i++)
      colours_[i] = new Color(colours[i].getRGB());

    return(true);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setWebPrefix(String prefix) {
    webPrefix_ = prefix;
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public String createChart(int type) throws ChartException {
    String url;

    if (type == PIECHART) {
      url = createPieChart();
    }
    else if (type == BARCHART) {
      url = createBarChart();
    }
    else
      throw new ChartException("Chart type not supported");

    return(url);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setFilePrefix(String prefix) {
    prefix_ = prefix;
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setFilePostfix(String postfix) {
    postfix_ = postfix;
  }

  private String createPieChart() throws ChartException {
    String url = null;

    if (data_ == null)
      throw new ChartException("No data set for chart");

    chart_ = new PieChart();
    try {
      if (data_.length != 0)
        chart_.setData(data_);
      if ((legend_ != null) && (legend_.length != 0))
        chart_.setLegend(legend_);
      if (backGround_ != null)
        chart_.setBackgroundColour(backGround_);
      if ((colours_ != null) && (colours_.length != 0))
        chart_.setChartColours(colours_);
      chart_.setFilePrefix(prefix_);
      chart_.setFilePostfix(postfix_);
      chart_.setWebPrefix(webPrefix_);
      if (numberOfDigits_ >= 0)
        chart_.setNumberOfBarLabelDigits(numberOfDigits_);
      if (addToBarLabel_ != null)
        chart_.addToBarLabel(addToBarLabel_);

      url = chart_.create();
    }
    catch (ChartException e) {
      throw(e);
    }

    return(url);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  private String createBarChart() throws ChartException {
    String url = null;

    if (data_ == null)
      throw new ChartException("No data set for chart");

    chart_ = new BarChart();
    try {
      if (data_.length != 0)
        chart_.setData(data_);
      if ((legend_ != null) && (legend_.length != 0))
        chart_.setLegend(legend_);
      if (backGround_ != null)
        chart_.setBackgroundColour(backGround_);
      if ((colours_ != null) && (colours_.length != 0))
        chart_.setChartColours(colours_);
      chart_.setFilePrefix(prefix_);
      chart_.setFilePostfix(postfix_);
      chart_.setWebPrefix(webPrefix_);
      if (numberOfDigits_ >= 0)
        chart_.setNumberOfBarLabelDigits(numberOfDigits_);
      if (addToBarLabel_ != null)
        chart_.addToBarLabel(addToBarLabel_);

      url = chart_.create();
    }
    catch (ChartException e) {
      throw(e);
    }

    return(url);
  }

  public void setNumberOfBarLabelDigits(int number) {
    numberOfDigits_ = number;
  }

  public void addToBarLabel(String stringToAdd) {
    addToBarLabel_ = stringToAdd;
  }

}