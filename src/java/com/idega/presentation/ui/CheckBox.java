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
public class CheckBox extends GenericInput {
	
	/**
	 * Constructs a new <code>CheckBox</code> with name set as "untitled" and value as
	 * "unspecified".
	 */
	public CheckBox() {
		this("untitled");
	}
	
	/**
	 * Constructs a new <code>CheckBox</code> with the given name and value as "unspecified".
	 */
	public CheckBox(String name) {
		this(name, "unspecified");
	}
	
	/**
	 * Constructs a new <code>CheckBox</code> with the given name and value.
	 */
	public CheckBox(String name, String value) {
		super();
		setName(name);
		setContent(value);
		setChecked(false);
		setInputType(INPUT_TYPE_CHECKBOX);
	}
	
	/**
	 * Sets whether the checkbox is checked or not.
	 * @param ifChecked
	 */
	public void setChecked(boolean ifChecked) {
		if (ifChecked) {
			setAttribute("checked");
		}
		else {
			removeAttribute("checked");
		}
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.getParameter(this.getName()) != null) {
			if (iwc.getParameter(this.getName()).equals(this.getValue())) {
				setChecked(true);
			}
		}
	}
	
	public synchronized Object clone() {
		CheckBox obj = null;
		try {
			obj = (CheckBox) super.clone();
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
}