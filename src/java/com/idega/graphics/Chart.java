/*
 * $Id: Chart.java,v 1.7 2006/04/09 12:13:12 laddi Exp $
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
public abstract class Chart {

  /*
   * The data to be plotted
   */
  protected Double data_[] = null;
  /*
   * The legend for the data
   */
  protected String legend_[] = null;

	/**
	 * 
	 * @uml.property name="uRL_"
	 */
	/*
	 * The URL for the chart created
	 */
	protected String URL_ = null;

  /*
   * The background colour for the chart
   */
  protected Color backGround_ = null;
  /*
   * The colours for the chart elements
   */
  protected Color colours_[] = null;
  /*
   * The prefix for the chart filename
   */
  protected String prefix_ = null;
  /*
   * The prefix for the chart url
   */
  protected String webPrefix_ = null;
  /*
   * The postfix for the chart filename
   */
  protected String postfix_ = null;

  protected int numberOfDigits_ = -1;
  protected String addToBarLabel_ = null;


  /**
   * Sets the names of the chart elements
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setLegend(String legend[]) throws ChartException {
    if (this.data_ != null) {
      if (this.data_.length != legend.length) {
        throw new ChartException("The number of data elements do not match the number of legend elements");
      }
    }

    if (legend == null) {
			throw new ChartException("Legend is null");
		}

    if (legend.length == 0) {
			throw new ChartException("Legend is empty");
		}

    this.legend_ = new String[legend.length];
    for (int i = 0; i < legend.length; i++) {
			this.legend_[i] = new String(legend[i]);
		}
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setData(Double data[]) throws ChartException {
    if (this.legend_ != null) {
      if (this.legend_.length != data.length) {
        throw new ChartException("The number of data elements do not match the number of legend elements");
      }
    }

    if (data == null) {
			throw new ChartException("Data is null");
		}

    if (data.length == 0) {
			throw new ChartException("Empty data");
		}

    this.data_ = new Double[data.length];
    for (int i = 0; i < data.length; i++) {
			this.data_[i] = new Double(data[i].doubleValue());
		}
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setBackgroundColour(Color c) {
    this.backGround_ = c;
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public void setChartColours(Color colours[]) throws ChartException {
    if (colours == null) {
			throw new ChartException("Colours is null");
		}

    if (colours.length == 0) {
			throw new ChartException("Colours is empty");
		}

    this.colours_ = new Color[colours.length];
    for (int i = 0; i < colours.length; i++) {
			this.colours_[i] = new Color(colours[i].getRGB());
		}
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
  public void setFilePostfix(String postfix) {
    this.postfix_ = postfix;
  }

  public void setNumberOfBarLabelDigits(int number) {
    this.numberOfDigits_ = number;
  }

  public void addToBarLabel(String stringToAdd) {
    this.addToBarLabel_ = stringToAdd;
  }

  /**
   * Lýsing á falli
   *
   * @param parameter-name description  Adds a parameter to the "Parameters" section. The description may be continued on the next line.
   * @return description                Adds a "Returns" section with the description text. This text should describe the return type and permissible range of values.
   * @throws class-name description
   */
  public abstract String create() throws ChartException;
}
