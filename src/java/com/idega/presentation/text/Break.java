package com.idega.presentation.text;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author laddi
 *
 * A <code>PresentationObject</code> that displays a break on the page it is added on.
 */
public class Break extends PresentationObject {

  private int _numberOfBreaks = 1;

	/**
	 * Constructs a new <code>Break</code> object with a single break.
	 */
  public Break() {
  }

	/**
	 * Constructs a new <code>Break</code> object with the number of breaks specified.
	 * @param numberOfBreaks
	 */
  public Break(int numberOfBreaks) {
    _numberOfBreaks = numberOfBreaks;
  }

  public void print(IWContext iwc) {
    for (int a = 1; a <= _numberOfBreaks; a++) {
	    if (getLanguage().equals("HTML")) {
	      print("<br />");
	    }
	    else if (getLanguage().equals("WML")) {
	      print("<br />");
	    }
    }
  }

  /**
   * Sets how many breaks should be displayed
   * @param  numberOfBreaks  The number of breaks to add
   */
  public void setNumberOfBreaks(int numberOfBreaks) {
    _numberOfBreaks = numberOfBreaks;
  }

  /**
   * Sets how many breaks should be displayed
   * @param  numberOfBreaks  The number of breaks to add
   */
  public void setNumberOfBreaks(String numberOfBreaks) {
    setNumberOfBreaks(Integer.parseInt(numberOfBreaks));
  }
}