//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class BackButton extends GenericButton {

	private String howFarBackOrForward = "-1";
	private boolean isImage=false;

	private static final String BACK_KEY = "backbutton.back";
	private static final String BACK_KEY_DEFAULT_VALUE = "Back";
	private boolean defaultContent = false;


	/**
	 * Constructs a new <code>BackButton</code> with the the default display label.
	 */
	public BackButton() {
		this("Back");
		defaultContent=true;
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
		isImage=true;
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
	

	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		IWBundle iwb = this.getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		if(!isImage && defaultContent){
			super.setValue(iwrb.getLocalizedString(BACK_KEY,BACK_KEY_DEFAULT_VALUE));	
		}
	}
	
	
	
	public void printWML(IWContext main) {	
		//print("<do type=\"prev\" label=\""+getContent()+"\"><prev/></do>");
		print("<anchor>"+getContent()+"<prev/></anchor>");
	}

}