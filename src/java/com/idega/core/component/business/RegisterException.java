/*
 * Created on Jan 11, 2004
 *
 */
package com.idega.core.component.business;

/**
 * RegisterException
 * @author aron 
 * @version 1.0
 */
public class RegisterException extends Exception {
	/**
	 * 
	 */
	public RegisterException() {
		super();
	}

	/**
	 * @param message
	 */
	public RegisterException(String message) {
		super(message);
		
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RegisterException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * @param cause
	 */
	public RegisterException(Throwable cause) {
		super(cause);
	}

}
