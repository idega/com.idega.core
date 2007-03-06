package com.idega.core.accesscontrol.business;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega - <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class UserHasLoginException extends LoginCreateException {


  public UserHasLoginException(){
    super("");
  }

  public UserHasLoginException(String s){
    super("UserHasLoginException: "+ s);
  }
}
