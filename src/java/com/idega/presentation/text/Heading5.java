/*
 * $Id: Heading5.java,v 1.2 2005/10/02 13:46:18 laddi Exp $
 * Created on Jul 11, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;


/**
 * Last modified: $Date: 2005/10/02 13:46:18 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class Heading5 extends Text {

	public Heading5() {
		super();
	}

	public Heading5(String text) {
		super(text);
	}

	protected String getTag() {
		return "h5";
	}

	protected boolean showTag() {
		return true;
	}
}