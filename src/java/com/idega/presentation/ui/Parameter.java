//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;

import com.idega.presentation.IWContext;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Parameter extends GenericInput {

	/**
	 * Constructs a new <code>Parameter</code> with the name "untitled".
	 */
	public Parameter() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>Parameter</code> with the given name and the value "unspecified".
	 */
	public Parameter(String name) {
		this(name, "unspecified");
	}

	/**
	 * Constructs a new <code>Parameter</code> with the given name and sets the given
	 * value.
	 */
	public Parameter(String name, String value) {
		super();
		setName(name);
		setContent(value);
		setInputType(INPUT_TYPE_HIDDEN);
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
		if (iwc.getParameter(getName()) != null) {
			setContent(iwc.getParameter(getName()));
		}
	}
}