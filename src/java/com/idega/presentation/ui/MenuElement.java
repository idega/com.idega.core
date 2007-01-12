/*
 * $Id: MenuElement.java,v 1.10.2.1 2007/01/12 19:32:04 idegaweb Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;
import java.io.IOException;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class MenuElement extends InterfaceObject {
	private boolean isDisabled;
	private boolean isSelected;

	private static String emptyString = "";

	public MenuElement() {
		this("untitled");
	}

	public MenuElement(String ElementName) {
		this(ElementName, ElementName);
	}

	public MenuElement(String ElementName, String ElementValue) {
		super();
		setName(ElementName);
		setValue(ElementValue);
		setSelected(false);
		setDisabled(false);
	}

	public void setElementName(String ElementName) {
		setName(ElementName);
	}

	public void setElementValue(String ElementValue) {
		setValue(ElementValue);
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setSelected(boolean ifSelected) {
		this.isSelected = ifSelected;
	}

	public void setDisabled(boolean ifDisabled) {
		this.isDisabled = ifDisabled;
	}

	public String getElementValue() {
		return getValueAsString();
	}

	public String getElementName() {
		return getName();
	}

	public void print(IWContext iwc) throws IOException {
		if (getMarkupLanguage().equals("HTML")) {
			String disabledString = emptyString;
			String selectedString = emptyString;
			if (isSelected()) {
				setMarkupAttribute("selected", "selected");
			}
			if (isDisabled()) {
				setMarkupAttribute("disabled", "disabled");
			}
			print("<option name=\"" + getName() + "\" " + getMarkupAttributesString() + " " + disabledString + " " + selectedString + " >");
			print(getName());
			print("</option>");
		}
		else if (getMarkupLanguage().equals("WML")) {
			print("<option name=\"" + getName() + "\" value=\"" + getValueAsString() + "\" >");
			print(getName());
			print("</option>");
		}
	}

	public synchronized Object clone() {
		MenuElement obj = null;
		obj = (MenuElement) super.clone();
		obj.isDisabled = this.isDisabled;
		obj.isSelected = this.isSelected;

		return obj;
	}
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}