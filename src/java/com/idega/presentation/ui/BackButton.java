//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class BackButton extends GenericButton {

	private String howFarBackOrForward = "-1";

	/**
	 * Constructs a new <code>BackButton</code> with the the default display label.
	 */
	public BackButton() {
		this("Back");
	}

	/**
	 * Constructs a new <code>BackButton</code> with the display string specified
	 * @param displayString	The string to display on the button.
	 */
	public BackButton(String displayString) {
		super();
		setName("");
		setValue(displayString);
		setInputType(INPUT_TYPE_BUTTON);
		setOnClick("history.go(" + this.howFarBackOrForward + ")");
	}

	/**
	 * Constructs a new <code>BackButton</code> with the image specified.
	 * @param defaultImage	The image to use as the back button.
	 */
	public BackButton(Image defaultImage) {
		super();
		setButtonImage(defaultImage);
		setOnClick("history.go(" + this.howFarBackOrForward + ")");
	}
	
	/**
	 * Sets the steps to move back or forward upon button press.
	 * @param steps	The amount of steps to take, forward or backwards.
	 */
	public void setHistoryMove(int steps) {
		howFarBackOrForward = String.valueOf(steps);
	}
}