package com.idega.business;

import javax.ejb.*;
import java.util.List;
import java.rmi.RemoteException;

/**
 * Title:        idega Business Objects
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */

public interface IBOHome extends EJBHome {
  public IBOService createIBO() throws CreateException, RemoteException;
}
