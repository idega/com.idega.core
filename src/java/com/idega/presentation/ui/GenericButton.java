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
public class GenericButton extends InterfaceObject {

	public GenericButton() {
		super();
		setName("untitled");
		setValue("");
	}

	public GenericButton(String name, String value) {
		super();
		setName(name);
		setValue(value);
	}

	public void setOnMouseOver(Image mouseOverImage) {
		setAttribute("onmouseover", "this.src=" + mouseOverImage.getURL());
	}

	public void setOnClick(Image onClickImage) {
		setOnClick("this.src=" + onClickImage.getURL());
	}

	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			if (getInterfaceStyle().equals("default")) {
				println("<input type=\"button\" name=\"" + getName() + "\" " + getAttributeString() + " >");
			}
		}
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}
}