package com.idega.data;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOFinderException extends javax.ejb.FinderException {

  public IDOFinderException() {
  }

  public IDOFinderException(String message){
    super(message);
  }

  public IDOFinderException(Exception forwardException) {
    this(forwardException.getMessage());
  }


}
