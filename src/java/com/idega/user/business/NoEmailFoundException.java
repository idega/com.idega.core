package com.idega.user.business;

import com.idega.user.data.User;

/**
 * @author tryggvil
 * Exception thrown if no email is found for User
 */
public class NoEmailFoundException extends Exception
{
	public NoEmailFoundException(String userString){
			super("No email found for user "+userString);
	}
}
