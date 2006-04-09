package com.idega.presentation.text;

import com.idega.idegaweb.IWConstants;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;

/**
 * @author laddi A <code>PresentationObject</code> that displays a break on
 *         the page it is added on.
 */
public class Break extends PresentationObject {

	private int _numberOfBreaks = 1;

	/**
	 * Constructs a new <code>Break</code> object with a single break.
	 */
	public Break() {
	}

	/**
	 * Constructs a new <code>Break</code> object with the number of breaks
	 * specified.
	 * 
	 * @param numberOfBreaks
	 */
	public Break(int numberOfBreaks) {
		this._numberOfBreaks = numberOfBreaks;
	}

	public void print(IWContext iwc) {
		for (int a = 1; a <= this._numberOfBreaks; a++) {
			if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
				String markup = iwc.getApplicationSettings().getDefaultMarkupLanguage();
				print("<br " + (!markup.equals(Page.HTML) ? "/" : "") + ">");
			}
			else if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_WML)) {
				if(!((this.getParent() instanceof Page) || (this.getParent() instanceof Block))){
					print("<br />");
				}
			}
		}
	}

	/**
	 * Sets how many breaks should be displayed
	 * 
	 * @param numberOfBreaks
	 *            The number of breaks to add
	 */
	public void setNumberOfBreaks(int numberOfBreaks) {
		this._numberOfBreaks = numberOfBreaks;
	}

	/**
	 * Sets how many breaks should be displayed
	 * 
	 * @param numberOfBreaks
	 *            The number of breaks to add
	 */
	public void setNumberOfBreaks(String numberOfBreaks) {
		setNumberOfBreaks(Integer.parseInt(numberOfBreaks));
	}
}