package com.idega.business;

import java.util.Collection;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


/**
 * Title:		InputHandler
 * Description: An interface for a form object that is used in th Report Generator Block or in method invocation reports.
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,<a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a><br>
 * @version		1.2
 */
public interface InputHandler {

	/**
	 * @return Returns an instance of the GUI Widget that handles the input and is responsible for creating the request
	 * parameters that are used for the method getResultingObject after the form is sent.
	 */
	PresentationObject getHandlerObject(String name,String value,IWContext iwc);
	
	PresentationObject getHandlerObject(String name, Collection values, IWContext iwc);
	
	/**
	 * This method is called when the request is made and is given the String[] values from the request that your
	 * handlerobject should have created for the form. Valid return objects are Strings,Integers,IDOEntities
	 * and collection of those types.
	 * @return Creates object of the expected datatype from the string value and returns it as an Object
	 */
	Object getResultingObject(String[] value, IWContext iwc) throws Exception;

	/**
	 * This method is supplied with the resulting object from the method getResultingObject and should create a
	 * string representation of that data. This string is then displayed to the user.
	 * @param value
	 * @param iwc
	 * @return a string representation for the resulting object
	 */
	String getDisplayForResultingObject(Object value, IWContext iwc);

	Object convertSingleResultingObjectToType(Object value, String className);
	
}
