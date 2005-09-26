/*
 * $Id: ICPageWriter.java,v 1.1 2005/09/26 17:29:43 thomas Exp $
 * Created on Sep 23, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.io.serialization;

import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;


public class ICPageWriter extends ICFileWriter {
	
	boolean isMarkedAsDeleted = false;
	String name = null;
	
	public ICPageWriter(IWContext iwc) {
		super(iwc);
	}
	
	public ICPageWriter(ICPage page, IWContext iwc) {
		super(page.getFile(), iwc);
		isMarkedAsDeleted = page.getDeleted();
		name = page.getName();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isMarkedAsDeleted() {
		return isMarkedAsDeleted;
	}

}
