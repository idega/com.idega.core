package com.idega.event;

import com.idega.presentation.IWContext;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */


public interface IWModuleEvent {

	/**
	 * 
	 * @uml.property name="iWContext"
	 */
	public void setIWContext(IWContext iwc);

	/**
	 * 
	 * @uml.property name="iWContext"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	public IWContext getIWContext();

}
