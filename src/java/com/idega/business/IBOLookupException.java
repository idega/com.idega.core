package com.idega.business;

import java.rmi.RemoteException;

/**
 * An Exception thrown when the lookup of a data object fails
 * Title:        idega Business Objects
 * Copyright:    Copyright (c) 2003
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBOLookupException extends RemoteException{
	
	public IBOLookupException()
	{
		super();
	}
	public IBOLookupException(String message)
	{
		super(message);
	}
	public IBOLookupException(Exception exeption)
	{
		this("IDOLookupException - Originally: " + exeption.getClass().getName() + " : " + exeption.getMessage());
	}
	public IBOLookupException(Exception exeption, IBOService thrower)
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