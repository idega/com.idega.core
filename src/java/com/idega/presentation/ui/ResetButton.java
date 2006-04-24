//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import com.idega.presentation.Image;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class ResetButton extends GenericButton {

	/**
	 * Constructs a new <code>ResetButton</code> with the the default display label.
	 */
	public ResetButton() {
		this("Reset");
	}

	/**
	 * Constructs a new <code>ResetButton</code> with the display string specified
	 * @param displayString	The string to display on the button.
	 */
	public ResetButton(String displayString) {
		super("reset", displayString);
		setInputType(INPUT_TYPE_RESET);
	}

	/**
	 * Constructs a new <code>ResetButton</code> with the image specified.
	 * @param defaultImage	The image to use as the reset button.
	 */
	public ResetButton(Image defaultImage) {
		super();
		defaultImage.setId(getId()+"_img");
		setButtonImage(defaultImage);
		setOnClick("this.form.reset();");
	}

	/**
	 * @see com.idega.presentation.ui.GenericButton#setAsImageButton(boolean)
	 */
	public void setAsImageButton(boolean asImageButton) {
		super.setAsImageButton(asImageButton);
		setOnClick("this.form.reset();");
	}
}
