package com.idega.presentation.text;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Break extends Block {

private int _numberOfBreaks = 1;

  public Break() {
  }

  public Break(int numberOfBreaks) {
    _numberOfBreaks = numberOfBreaks;
  }

  public void main(IWContext iwc) {
    for ( int a = 1; a <= _numberOfBreaks; a++ ) {
      super.add(Text.getBreak());
    }
  }

  public void setNumberOfBreaks(int numberOfBreaks) {
    _numberOfBreaks = numberOfBreaks;
  }
}