/*
 * $Id$
 * Created on Apr 25, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import javax.faces.component.UIComponent;


public class Span extends Layer {

	public Span() {
		super(SPAN);
	}
	
	public Span(UIComponent addedComponent) {
		this();
		add(addedComponent);
	}
}