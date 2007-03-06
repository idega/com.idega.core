package com.idega.core.accesscontrol.business;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class NotLoggedOnException extends RuntimeException {


  public NotLoggedOnException(){
    super("");
  }

  public NotLoggedOnException(String s){
    super("NotLoggedOnException: "+ s);
  }
}
