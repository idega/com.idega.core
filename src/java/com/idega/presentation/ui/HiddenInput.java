//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.presentation.ui;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class HiddenInput extends Parameter {

	/**
	 * Constructs a new <code>HiddenInput</code> with the name "untitled".
	 */
	public HiddenInput() {
		this("untitled");
	}

	/**
	 * Constructs a new <code>HiddenInput</code> with the given name and the value "unspecified".
	 */
	public HiddenInput(String name) {
		this(name, "unspecified");
	}

	/**
	 * Constructs a new <code>HiddenInput</code> with the given name and sets the given
	 * value.
	 */
	public HiddenInput(String name, String value) {
		super(name, value);
	}
}
