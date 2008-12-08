/*
 * $Id: Heading6.java,v 1.3 2008/12/08 06:51:44 valdas Exp $
 * Created on Jul 11, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;


/**
 * Last modified: $Date: 2008/12/08 06:51:44 $ by $Author: valdas $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class Heading6 extends Heading {

	public Heading6() {
		super();
	}

	public Heading6(String text) {
		super(text);
	}

	@Override
	protected String getTag() {
		return "h6";
	}

	@Override
	protected boolean showTag() {
		return true;
	}
}