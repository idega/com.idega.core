package com.idega.data;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDONoDatastoreError extends Error {

  public IDONoDatastoreError() {
    super("Unable to communicate with datastore");
  }

  public IDONoDatastoreError(String message) {
    super("Unable to communicate with datastore - Message was: "+message);
  }



}
