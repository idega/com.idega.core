/*
 * $Id: LibraryDoesNotExist.java,v 1.2 2002/04/06 19:07:44 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.exception;

import com.idega.util.ExceptionHelper;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class LibraryDoesNotExist extends Exception {
  private static String _key = "LibraryDoesNotExist";
  private static ExceptionHelper _e = new ExceptionHelper();

  public LibraryDoesNotExist() {
    super(_e.getExceptionText(_key));
  }

  public LibraryDoesNotExist(String userDefinedKey) {
    super(_e.getExceptionText(userDefinedKey));
  }

}
