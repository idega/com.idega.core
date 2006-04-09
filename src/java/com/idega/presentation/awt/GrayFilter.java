package com.idega.presentation.awt;

import java.awt.image.RGBImageFilter;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

//======================================================

/** Builds an image filter that can be used to gray-out
 *  the image.
 * @see ImageButton
 */

class GrayFilter extends RGBImageFilter {

  //----------------------------------------------------

  private int darkness = 0xffafafaf;

  //----------------------------------------------------

  public GrayFilter() {
    this.canFilterIndexColorModel = true;
  }

  public GrayFilter(int darkness) {
    this();
    this.darkness = darkness;
  }

  //----------------------------------------------------

  public int filterRGB(int x, int y, int rgb) {
    return(rgb & this.darkness);
  }

  //----------------------------------------------------
}

