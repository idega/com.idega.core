package com.idega.util;

/**
 * This exception is thrown when a date couldnt be parsed
 * @author <a href="mailto:joakim@idega.is">Joakim Johnson</a>
 * @version 1.0
 */
public class DateFormatException extends Exception {

  public DateFormatException() {
	super();
  }

  public DateFormatException(String s) {
	super(s);
  }
}	