/*
 * $Id: InterfaceObjectContainer.java,v 1.2 2002/01/23 15:01:54 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation.ui;

import com.idega.presentation.PresentationObjectContainer;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class InterfaceObjectContainer extends PresentationObjectContainer{
  /**
   *
   */
  public InterfaceObjectContainer() {
	  setID();
  }

  /**
   *
   */
  public synchronized Object clone() {
    InterfaceObjectContainer obj = null;
    try {
      obj = (InterfaceObjectContainer)super.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return(obj);
  }
}