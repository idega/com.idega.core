package com.idega.presentation;

/**
 * Title:        idegaclasses
 * Description:  This exception is thrown when there is an exception when the page is loading
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWPageInitializationException extends Exception {

  public IWPageInitializationException() {
  }

  public IWPageInitializationException(String message){
    super(message);
  }
}
