package com.idega.exception;

/**
 * Title:        IW Accesscontrol
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author       <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class ICObjectNotInstalledException extends RuntimeException {

  public ICObjectNotInstalledException(){
    this("Unspecified");
  }

  public ICObjectNotInstalledException(String className){
    super("ICObject:"+className+" is not installed");
  }


}
