package com.idega.user.business;


/**
 * @author tryggvil
 * Exception thrown if no email is found for User
 */
public class NoPhoneFoundException extends Exception
{
	public NoPhoneFoundException(String userString){
			super("No phone found for user "+userString);
	}
}
