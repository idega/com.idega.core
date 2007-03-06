package com.idega.idegaweb;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class IWServiceNotStartedException extends Exception {

  public IWServiceNotStartedException(IWService service) {
    super("IWServiceNotStarted: "+service.getServiceName());
  }

}
