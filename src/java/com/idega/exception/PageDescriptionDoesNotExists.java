/*
 * $Id: PageDescriptionDoesNotExists.java,v 1.1 2001/05/14 14:27:27 palli Exp $
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
 *
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
 public class PageDescriptionDoesNotExists extends Exception {

  private static String key = "PageDescriptionDoesNotExists";
  private static ExceptionHelper e = new ExceptionHelper();

  public PageDescriptionDoesNotExists() {
    super(e.getExceptionText(key));
  }

  public PageDescriptionDoesNotExists(String userDefinedKey) {
    super(e.getExceptionText(userDefinedKey));
  }
}