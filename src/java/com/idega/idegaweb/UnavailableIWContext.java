package com.idega.idegaweb;

/**
 * Title:        idegaWeb Presentation Objects
 * Description:  idegaWeb Presentation Objects is a framework for presenting user interface via centralised (web)applications
 * Copyright:    Copyright (c) 2000-2001
 * Company:      idega
 * @author
 * @version 1.0
 */

public class UnavailableIWContext extends RuntimeException {

  public UnavailableIWContext() {
    super("IWContext is not set for the current thread");
  }

  public UnavailableIWContext(String message) {
    super(message);
  }
  
  public UnavailableIWContext(Exception cause) {
	    super(cause);
	  }
}