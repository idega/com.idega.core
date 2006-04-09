/*
 * $Id: ExceptionHelper.java,v 1.5 2006/04/09 12:13:13 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util;

import java.util.ResourceBundle;

/**
 * A class to get the localized version of the exception text.
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */
public class ExceptionHelper {

  private ResourceBundle bundle = null;

  public ExceptionHelper() {
    //bundle = ResourceBundle.getBundle("com.idega.exception.ExceptionText",Locale.getDefault());
  }

  public String getExceptionText(String key) {
    if (this.bundle == null) {
			return(new String("Unable to get exception resource bundle"));
		}

    String text = this.bundle.getString(key);

    if (text == null) {
			return(new String("Undefined exception key " + key));
		}

    return(text);
  }
}
