/*

 * $Id: ExceptionWrapper.java,v 1.2 2002/04/06 19:07:45 tryggvil Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.presentation;



/**

 *

 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

 * @version 1.2

 */

public class ExceptionWrapper extends PresentationObjectContainer {

  private Exception ex;

  private PresentationObject obj;



  public ExceptionWrapper() {

  }



  public ExceptionWrapper(Exception ex, PresentationObject thrower) {

    setException(ex);

    setThrower(thrower);

    add("Exception: in " + thrower.getClass().getName() + " <br> " + ex.getClass().getName() + "  " + ex.getMessage());

    ex.printStackTrace(System.err);

  }



  public void setException(Exception ex) {

    this.ex = ex;

  }



  public void setThrower(PresentationObject obj) {

    this.obj = obj;

  }

}

