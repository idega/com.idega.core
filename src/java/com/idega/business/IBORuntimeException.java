package com.idega.business;
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
public class IBORuntimeException extends EJBException
{
	public IBORuntimeException()
	{
		super();
	}
	public IBORuntimeException(String message)
	{
		super(message);
	}
	public IBORuntimeException(Exception exeption)
	{
		this("IBORuntimeException - Originally: " + exeption.getClass().getName() + " : " + exeption.getMessage());
	}
	public IBORuntimeException(Exception exeption, IBOServiceBean thrower)
	{
		this(
			"IBORuntimeException - Originally: "
				+ exeption.getClass().getName()
				+ " : "
				+ exeption.getMessage()
				+ " : from : "
				+ thrower.getClass().getName());
	}
}