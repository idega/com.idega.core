/*
 * Created on 17.8.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.business;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


/**
 * Title:		InputHandler
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public interface InputHandler {

	/**
	 * @return Returns an instance of the GUI Widget that handles the input
	 */
	public PresentationObject getHandlerObject(String name,String stringValue,IWContext iwc);

	/**
	 * @return Creates object of the expected datatype from the string value and returns it as an Object
	 */
	public Object getResultingObject(String[] value, IWContext iwc) throws Exception;
	
	public String getDisplayNameOfValue(Object value, IWContext iwc);
}
