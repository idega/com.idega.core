package com.idega.core.accesscontrol.business;

import com.idega.user.data.User;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2002 - idega - <a href="mailto:gimmi@idega.is">Grímur Jónsson</a>
 * @version 1.0
 */

public class LoginCreateException extends javax.ejb.CreateException {


  public LoginCreateException(){
    super("");
  }

  public LoginCreateException(String s){
    super("LoginException: "+ s);
  }

  public LoginCreateException(User user){
    super("Exception creating login for user: "+ user.getName()+" with id:"+user.toString());
  }
  
  public LoginCreateException(int userId){
    super("Exception creating login for user with id:"+userId);
  }
    
  
}
