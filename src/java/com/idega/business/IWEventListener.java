//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.business;

import java.util.EventListener;
import com.idega.presentation.*;
import com.idega.idegaweb.IWException;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.8
*Interface that defines classes that handle events in IdegaWeb
*/

public interface IWEventListener extends EventListener {
  /**
   *
   * @param iwc
   * @return returns true if changes have been made or else false;
   * @throws IWException
   */
  public boolean actionPerformed(IWContext iwc)throws IWException;
}

