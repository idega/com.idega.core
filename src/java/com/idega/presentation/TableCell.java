/*
 *
 * Copyright (C) 2001-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import com.idega.idegaweb.IWUserContext;


/**
 * Used by the table class to represent each cell.
 * tags for each cell.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class TableCell extends PresentationObjectContainer {

	
	//TODO: A lot of the rendering logic should be moved to this class from the Table class
	
	protected TableCell() {
		super();
		this.setTransient(false);
	}

	
	/**
	 * Override this method to bypass the permission logic for TableCells
	 * (because they dont have a direct instance id)
	 */
	public Object clonePermissionChecked(IWUserContext iwc,
			boolean askForPermission) {
		return clone(iwc, askForPermission);
	}
}
