package com.idega.data;
import javax.ejb.EJBException;
/**
 * An Unchecked Exception thrown when an error occurs on runtime in IDO.
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IDORuntimeException extends EJBException
{
	public IDORuntimeException()
	{
		super();
	}
	public IDORuntimeException(String message)
	{
		super(message);
	}
	public IDORuntimeException(Exception exeption)
	{
		this("IDORuntimeException - Originally: " + exeption.getClass().getName() + " : " + exeption.getMessage());
	}
	public IDORuntimeException(Exception exeption, IDOEntity thrower)
	{
		this(
			"IDORuntimeException - Originally: "
				+ exeption.getClass().getName()
				+ " : "
				+ exeption.getMessage()
				+ " : from : "
				+ thrower.getClass().getName());
	}
}