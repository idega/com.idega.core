package com.idega.exception;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IWBundleDoesNotExist extends RuntimeException {

  public IWBundleDoesNotExist(){
    this("Unspecified");
  }

  public IWBundleDoesNotExist(String bundleIdentifier){
    super("IWBundle: "+bundleIdentifier+" is not installed or does not exist");
  }


}
