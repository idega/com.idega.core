package com.idega.core.accesscontrol.data;

import com.idega.data.IDOException;

/**
 * Title:        idegaWeb User Subsystem
 * Description:  idegaWeb User Subsystem is the base system for Users and Group management
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class PasswordNotKnown extends IDOException {

  public PasswordNotKnown(String userName) {
    super("Password not known (encrypted) for username: "+userName);
  }
}