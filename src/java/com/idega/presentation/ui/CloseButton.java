//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class CloseButton extends GenericButton {

	/**
	 * Constructs a new <code>CloseButton</code> with the the default display label.
	 */
	public CloseButton() {
		setAsImageButton(true);
		setOnClick("top.window.close()");
		setValue("close");
	}

	/**
	 * Constructs a new <code>CloseButton</code> with the display string specified
	 * @param displayString	The string to display on the button.
	 */
	public CloseButton(String displayString) {
		super();
		setName("");
		setValue(displayString);
		setInputType(INPUT_TYPE_BUTTON);
		setOnClick("top.window.close()");
	}

	/**
	 * Constructs a new <code>CloseButton</code> with the image specified.
	 * @param defaultImage	The image to use as the close button.
	 */
	public CloseButton(Image image) {
		super();
		setButtonImage(image);
		setOnClick("top.window.close()");
	}
	
	public void printWML(IWContext main) {

	}


}
