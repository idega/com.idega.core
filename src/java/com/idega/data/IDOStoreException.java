package com.idega.data;

import javax.ejb.EJBException;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOStoreException extends EJBException {

  public IDOStoreException() {
  }

  public IDOStoreException(String message) {
    super(message);
  }
}
