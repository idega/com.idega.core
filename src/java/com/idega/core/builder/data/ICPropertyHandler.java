/*
 * $Id: ICPropertyHandler.java,v 1.1 2004/06/28 11:12:49 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.core.builder.data;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public interface ICPropertyHandler {
  /**
   * Returns a list of Class Objects this Handler will default handle
   * Can return null if none apply
   */
  public List getDefaultHandlerTypes();

  /**
   * Returns an instance of the GUI Widget that handles the setting
   */
  public PresentationObject getHandlerObject(String name,String stringValue,IWContext iwc);

  /**
   * A function that is executed after the user presses OK/Apply on the property window.
   */
  public void onUpdate(String values[], IWContext iwc);
}
