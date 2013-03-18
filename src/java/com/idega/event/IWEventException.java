package com.idega.event;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWEventException extends RuntimeException {

  public IWEventException() {
    this("Unspecified");
  }

  public IWEventException(String explanation) {
    super("IWEventException: " + explanation);
  }

}
