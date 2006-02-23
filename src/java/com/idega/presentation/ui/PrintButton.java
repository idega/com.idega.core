//idega 2000 - Grímur Jónsson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import com.idega.presentation.Image;

/**
*@author <a href="mailto:gimmi@idega.is">Grímur Jónsson</a>
*@version 1.0
*/
public class PrintButton extends GenericButton {

	/**
	 * Constructs a new <code>PrintButton</code> with the the default display label.
	 */
	public PrintButton() {
		this("Print");
	}

	/**
	 * Constructs a new <code>PrintButton</code> with the display string specified
	 * @param displayString	The string to display on the button.
	 */
	public PrintButton(String displayString) {
		super("Print",displayString);
		setOnClick("javascript:window.print();");
	}

	/**
	 * Constructs a new <code>PrintButton</code> with the image specified.
	 * @param buttonImage	The image to use as the print button.
	 */
	public PrintButton(Image buttonImage) {
		super();
		buttonImage.setId(getId()+"_im");
		setOnClick("javascript:window.print();");
		setButtonImage(buttonImage);
	}
}
