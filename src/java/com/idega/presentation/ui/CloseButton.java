//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import com.idega.presentation.ClickableDiv;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class CloseButton extends GenericButton {

	private void generalClose(){
		setOnClick("top.window.close()");
	}
	private void generalClose(String displayString){
		setName(displayString);
		setValue(displayString);
		setOnClick("top.window.close()");
	}
	private void defaultClose(){
		setAsImageButton(true);
		generalClose("close");
	}
	
	private void stringClose(String displayString){
		generalClose(displayString);
		setInputType(INPUT_TYPE_BUTTON);
	}
	private void imageClose(Image image){
		setAsImageButton(true);
		generalClose("close");
		if(image.getId().startsWith("id")){
			image.setId(getId()+"_imagebutton");
		}
		setButtonImage(image);
	}
	
	/**
	 * Constructs a new <code>CloseButton</code> with the the default display label.
	 */
	public CloseButton() {
		defaultClose();
	}

	/**
	 * Constructs a new <code>CloseButton</code> with the display string specified
	 * @param displayString	The string to display on the button.
	 */
	public CloseButton(String displayString) {
		stringClose(displayString);
	}

	/**
	 * Constructs a new <code>CloseButton</code> with the image specified.
	 * @param defaultImage	The image to use as the close button.
	 */
	public CloseButton(Image image) {
		imageClose(image);
	}
	
	public CloseButton(ClickableDiv div) {
		stringClose(div.getText());
	}
	
	public void printWML(IWContext main) {

	}


}
