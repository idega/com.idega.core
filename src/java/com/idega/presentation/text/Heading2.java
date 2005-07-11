/*
 * $Id: Heading2.java,v 1.1 2005/07/11 14:32:58 laddi Exp $
 * Created on Jul 11, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;


/**
 * Last modified: $Date: 2005/07/11 14:32:58 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class Heading2 extends Text {

	protected String getTag() {
		return "h2";
	}

	protected boolean showTag() {
		return true;
	}
}