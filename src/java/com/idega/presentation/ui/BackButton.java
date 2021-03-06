/*
 * $Id: BackButton.java,v 1.18 2006/04/09 12:13:16 laddi Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
 * <p>
 * This component presents a button that can be clicked and the user is sent to the previous page in the browser history.
 * </p>
 *  Last modified: $Date: 2006/04/09 12:13:16 $ by $Author: laddi $
 *  
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.18 $
 */
public class BackButton extends GenericButton {

	//constants:
	private static final String BACK_KEY = "backbutton.back";
	private static final String BACK_KEY_DEFAULT_VALUE = "Back";
	//Instance variables:
	private String howFarBackOrForward = "-1";
	private boolean isImage=false;
	private boolean defaultContent = false;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[4];
		values[0] = super.saveState(ctx);
		values[1] = this.howFarBackOrForward;
		values[2] = Boolean.valueOf(this.isImage);
		values[3] = Boolean.valueOf(this.defaultContent);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.howFarBackOrForward = (String)values[1];
		this.isImage = ((Boolean) values[2]).booleanValue();
		this.defaultContent = ((Boolean)values[3]).booleanValue();
	}
	
	/**
	 * Constructs a new <code>BackButton</code> with the the default display label.
	 */
	public BackButton() {
		this("Back");
		this.defaultContent=true;
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
		this.isImage=true;
		defaultImage.setId(getId()+"_im");
		setButtonImage(defaultImage);
		setOnClick("history.go(" + this.howFarBackOrForward + ")");
	}
	
	/**
	 * Sets the steps to move back or forward upon button press.
	 * @param steps	The amount of steps to take, forward or backwards.
	 */
	public void setHistoryMove(int steps) {
		this.howFarBackOrForward = String.valueOf(steps);
	}
	

	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		IWBundle iwb = this.getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		if(!this.isImage && this.defaultContent){
			super.setValue(iwrb.getLocalizedString(BACK_KEY,BACK_KEY_DEFAULT_VALUE));	
		}
	}
	
	/**
	 * Sets the Button display string on the button.
	 * @param value	The value to set.
	 */
	public void setValue(String value) {
		setMarkupAttribute("value", value);
		this.defaultContent=false;
	}
	
	
	public void printWML(IWContext main) {	
		//print("<do type=\"prev\" label=\""+getContent()+"\"><prev/></do>");
		print("<anchor>"+getContent()+"<prev/></anchor>");
	}

}