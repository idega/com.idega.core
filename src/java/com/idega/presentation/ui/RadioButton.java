/*
 * $Id: RadioButton.java,v 1.7 2003/02/06 17:08:08 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class RadioButton extends GenericInput {

	/**
	 * Constructs a new <code>RadioButton</code> with the name "untitled" and the value
	 * "unspecified".
	 */
	public RadioButton() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>RadioButton</code> with the given name and the value
	 * "unspecified".
	 */
	public RadioButton(String name) {
		this(name, "unspecified");
	}

	/**
	 * Constructs a new <code>RadioButton</code> with the given name and value.
	 */
	public RadioButton(String name, String value) {
		super();
		setName(name);
		setContent(value);
		setInputType(INPUT_TYPE_RADIO);
	}

	/**
	 * Sets the radio button as selected.
	 */
	public void setSelected() {
		setSelected(true);
	}

	/**
	 * Sets the radio button as selected.
	 */
	public void setSelected(boolean selected) {
		if (selected)
			setAttribute("checked");
		else
			removeAttribute("checked");
	}

	/**
	 * Returns whether the radion button is selected.
	 * @return boolean	True if selected, false otherwise.
	 */
	public boolean getSelected() {
		if (isAttributeSet("checked"))
			return true;
		return false;
	}

	public void handleKeepStatus(IWContext iwc) {
		String[] parameters = iwc.getParameterValues(getName());
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i].equals(getValue())) {
					setSelected();
				}
			}
		}
	}
}