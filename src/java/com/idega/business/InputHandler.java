package com.idega.business;

import java.util.Collection;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


/**
 * Title:		InputHandler
 * Description: An interface for a form object that is used in th Report Generator Block or in method invocation reports.
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 
 * <br>
 * <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>,
 * <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * <a herf="mailto:thomas@idega.is">Thomas Hilbig</a>
 * <br>
 * 
 * @version		1.2
 */
public interface InputHandler {

	/**
	 * This method is called to get the corresponding UI widget. The specified parameter value sets the
	 * selection or content of the returned widget. 
	 * If the parameter value is null a default selection or default content is shown.
	 * The value is of the same kind of values that are returned by the request, that is it is one of possible request values.
	 * @param name
	 * @param value 
	 * @param iwc
	 * @return Returns an instance of the GUI Widget that handles the input and is responsible for creating the request
	 * parameters that are used for the method getResultingObject after the form is sent.
	 *
	 */
	PresentationObject getHandlerObject(String name,String value,IWContext iwc);
	
		/**
	 * This method is called to get the corresponding UI widget. The specified parameter values (collection of strings) set the
	 * selections or content of the returned widget. If the parameter value is null a default selection or default content is
	 * shown.
	 * The values are of the same kind of values that are returned by the request, that is they are a subset of the possible request values.
	 * If the returned UI widget allows only the selection of one value the standard implementation might be<br>
	 * <code>
	 * String value = (String) Collections.min(values);<br>
	 * return getHandlerObject(name, value, iwc);
			</code>
	 * @param name
	 * @param values - collections of string
	 * @param iwc 
	 * @return Returns an instance of the GUI Widget that handles the input and is responsible for creating the request
	 * parameters that are used for the method getResultingObject after the form is sent.
	 *
	 */
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

	/** This method is supplied with a single resulting object that is if the resulting object is a collection only one of the elements should
	 * be supplied. It converts the resulting object to an object type that corresponds to the specified className.
	 * E.g. if the resulting object is an integer (representing the age of someone) this method should return the date of birth when the
	 * specified className is of the type SQLDate. 
	 * In most implementations the returned value is equal to the specified value, that is the method does nothing than just returning the 
	 * parameter value.
	 * The returned value is used for SQL statements after involving toString() of that object. 
	 * The standard implementation might be:<br>
	 * <code>return value;</code>
	 * @param value
	 * @param className
	 * @return an object that is able to return a useable string representation for SQL-statements after calling toString()
	 */
	Object convertSingleResultingObjectToType(Object value, String className);
	
}
