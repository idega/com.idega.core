/*
 * $Id: CSSSpacer.java,v 1.1 2005/01/25 17:43:24 eiki Exp $
 * Created on Jan 25, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;


/**
 * 
 *  Last modified: $Date: 2005/01/25 17:43:24 $ by $Author: eiki $
 * 
 * Creates a layer with the style class "spacer"<br>
 * that you insert before and after floating elements in a container so they take up "space".<br>
 * e.g. if you set a border on the container all the elements in the container will be within the border.
 * The style is defined in iw_core.css like this: <br>
 * .spacer {
 * clear: both;
 * }
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class CSSSpacer extends Layer {

	public static final String SPACER_STYLE_CLASS = "spacer";
	/**
	 * 
	 */
	public CSSSpacer() {
		super();
		setStyleClass(SPACER_STYLE_CLASS);
	}

	/**
	 * @param layerType
	 */
	public CSSSpacer(String layerType) {
		super(layerType);
		setStyleClass(SPACER_STYLE_CLASS);
	}
}
