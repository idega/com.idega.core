package com.idega.idegaweb;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWNotPropertyListException extends RuntimeException {

  public IWNotPropertyListException(String key){
    super("IWPropertyKey '"+key+"' is not a PropertyList");
  }
}
