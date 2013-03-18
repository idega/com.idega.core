/*
 * $Id: CreateChart.java,v 1.7 2006/04/09 12:13:12 laddi Exp $
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
    if (legend.length == 0) {
			return(false);
		}

    this.legend_ = new String[legend.length];
    for (int i = 0; i < legend.length; i++) {
			this.legend_[i] = new String(legend[i]);
		}

    return(true);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   */
  public boolean setData(Double data[]) {
    if (data.length == 0) {
			return(false);
		}

    this.data_ = new Double[data.length];
    for (int i = 0; i < data.length; i++) {
			this.data_[i] = new Double(data[i].doubleValue());
		}

    return(true);
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   */
  public void setBackgroundColour(Color c) {
    this.backGround_ = c;
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   */
  public boolean setChartColours(Color colours[]) {
    if (colours.length == 0) {
			return(false);
		}

    this.colours_ = new Color[colours.length];
    for (int i = 0; i < colours.length; i++) {
			this.colours_[i] = new Color(colours[i].getRGB());
		}

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
    this.webPrefix_ = prefix;
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
		else {
			throw new ChartException("Chart type not supported");
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
  public void setFilePrefix(String prefix) {
    this.prefix_ = prefix;
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setFilePostfix(String postfix) {
    this.postfix_ = postfix;
  }

  private String createPieChart() throws ChartException {
    String url = null;

    if (this.data_ == null) {
			throw new ChartException("No data set for chart");
		}

    this.chart_ = new PieChart();
    try {
      if (this.data_.length != 0) {
				this.chart_.setData(this.data_);
			}
      if ((this.legend_ != null) && (this.legend_.length != 0)) {
				this.chart_.setLegend(this.legend_);
			}
      if (this.backGround_ != null) {
				this.chart_.setBackgroundColour(this.backGround_);
			}
      if ((this.colours_ != null) && (this.colours_.length != 0)) {
				this.chart_.setChartColours(this.colours_);
			}
      this.chart_.setFilePrefix(this.prefix_);
      this.chart_.setFilePostfix(this.postfix_);
      this.chart_.setWebPrefix(this.webPrefix_);
      if (this.numberOfDigits_ >= 0) {
				this.chart_.setNumberOfBarLabelDigits(this.numberOfDigits_);
			}
      if (this.addToBarLabel_ != null) {
				this.chart_.addToBarLabel(this.addToBarLabel_);
			}

      url = this.chart_.create();
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

    if (this.data_ == null) {
			throw new ChartException("No data set for chart");
		}

    this.chart_ = new BarChart();
    try {
      if (this.data_.length != 0) {
				this.chart_.setData(this.data_);
			}
      if ((this.legend_ != null) && (this.legend_.length != 0)) {
				this.chart_.setLegend(this.legend_);
			}
      if (this.backGround_ != null) {
				this.chart_.setBackgroundColour(this.backGround_);
			}
      if ((this.colours_ != null) && (this.colours_.length != 0)) {
				this.chart_.setChartColours(this.colours_);
			}
      this.chart_.setFilePrefix(this.prefix_);
      this.chart_.setFilePostfix(this.postfix_);
      this.chart_.setWebPrefix(this.webPrefix_);
      if (this.numberOfDigits_ >= 0) {
				this.chart_.setNumberOfBarLabelDigits(this.numberOfDigits_);
			}
      if (this.addToBarLabel_ != null) {
				this.chart_.addToBarLabel(this.addToBarLabel_);
			}

      url = this.chart_.create();
    }
    catch (ChartException e) {
      throw(e);
    }

    return(url);
  }

  public void setNumberOfBarLabelDigits(int number) {
    this.numberOfDigits_ = number;
  }

  public void addToBarLabel(String stringToAdd) {
    this.addToBarLabel_ = stringToAdd;
  }

}