package com.idega.presentation.ui;

import java.util.Collection;
import java.util.Collections;

import com.idega.business.InputHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on May 11, 2004
 */
public abstract class DropDownMenuInputHandler extends DropdownMenu 	implements	InputHandler {
	
	public DropDownMenuInputHandler() {
		super();
	}
	
	public DropDownMenuInputHandler(String name) {
		super(name);
	}
	
	abstract public String getDisplayForResultingObject(Object value, IWContext iwc);	
	
	public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
		this.setName(name);
		if (value != null) {
			this.setSelectedElement(value);
		}
		return this;
	}
	
	public PresentationObject getHandlerObject(String name, Collection values, IWContext iwc) {
		String value = (String) Collections.min(values);
		return getHandlerObject(name, value, iwc);
	}
	
	public Object getResultingObject(String[] values, IWContext iwc) throws Exception {
		if (values != null && values.length > 0) {
			return values[0];
		}
		else
			return null;
	}

	public Object convertSingleResultingObjectToType(Object value, String className) {
		return value;
	}
}
