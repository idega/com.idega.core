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
public class Parameter extends InterfaceObject {

	public Parameter() {
		this("untitled");
	}

	public Parameter(String name) {
		this(name, "unspecified");
	}

	public Parameter(String name, String value) {
		super();
		setName(name);
		setContent(value);
	}

	public void print(IWContext iwc) throws IOException {
		if (getLanguage().equals("HTML")) {
			print("<input type=\"hidden\" name=\"" + getName() + "\" " + getAttributeString() + " >");
		}
	}

	public synchronized Object clone() {
		Parameter obj = null;
		try {
			obj = (Parameter) super.clone();
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

}