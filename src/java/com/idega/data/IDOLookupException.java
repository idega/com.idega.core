package com.idega.data;

import java.rmi.RemoteException;

/**
 * An Exception thrown when the lookup of a data object fails
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IDOLookupException extends RemoteException{
	
	public IDOLookupException()
	{
		super();
	}
	public IDOLookupException(String message)
	{
		super(message);
	}
	public IDOLookupException(Exception exeption)
	{
		this("IDOLookupException - Originally: " + exeption.getClass().getName() + " : " + exeption.getMessage());
	}
	public IDOLookupException(Exception exeption, IDOEntity thrower)
	{
		this(
			"IDOLookupException - Originally: "
				+ exeption.getClass().getName()
				+ " : "
				+ exeption.getMessage()
				+ " : from : "
				+ thrower.getClass().getName());
	}
}