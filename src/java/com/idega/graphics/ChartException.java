/*
 * $Id: ChartException.java,v 1.1 2001/05/16 20:26:33 eiki Exp $
 *
 * Copyright (C) 2000 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.graphics;

/**
 * An exception class for the chart creation classes
 *
 * @version $Id: ChartException.java,v 1.1 2001/05/16 20:26:33 eiki Exp $
 * @author Pall Helgason
 */

public class ChartException extends Exception {

  public ChartException() {
    super();
  }

  public ChartException(String s) {
    super(s);
  }
}