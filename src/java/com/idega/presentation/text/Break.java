package com.idega.presentation.text;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;

public class Break extends Block {

  private int _numberOfBreaks = 1;

  public Break() { }

  public Break(int numberOfBreaks) {
    _numberOfBreaks = numberOfBreaks;
  }

  public void main(IWContext iwc) {
    for (int a = 1; a <= _numberOfBreaks; a++) {
      super.add(Text.getBreak());
    }
  }

  /**
   *  Sets how many breaks should be displayed
   *
   *@param  numberOfBreaks  The new numberOfBreaks value
   */
  public void setNumberOfBreaks(int numberOfBreaks) {
    _numberOfBreaks = numberOfBreaks;
  }

  /**
   *  Sets how many breaks should be displayed
   *
   *@param  numberOfBreaks  The new numberOfBreaks value
   */
  public void setNumberOfBreaks(String numberOfBreaks) {
    _numberOfBreaks = Integer.parseInt(numberOfBreaks);
  }
}
