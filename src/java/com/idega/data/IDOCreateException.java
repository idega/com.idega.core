package com.idega.data;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOCreateException extends javax.ejb.CreateException {

  public IDOCreateException() {
  }

  public IDOCreateException(String message){
    super(message);
  }

  public IDOCreateException(Exception forwardException) {
    this(forwardException.getMessage());
  }
}
