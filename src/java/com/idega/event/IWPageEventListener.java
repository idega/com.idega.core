//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.event;

import java.util.EventListener;

import com.idega.idegaweb.IWException;
import com.idega.presentation.IWContext;

/**
 * Implementors of this class can be used as event listeners on "page level".
 * This means that when a GET or POST operation is performed next the class which
 * implements this class and is added as an event listener will be instanciated and
 * the method actionPerformed(iwc) is called.
 * This all happens before the page is reloaded again (instanciated and main(iwc) and print(iwc) are called.
 * <br>
 * <br>
 * <b>This class was formerly com.idega.business.IWEventListener.</b>
 * 
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/

public interface IWPageEventListener extends EventListener {
  /**
   *
   * @param iwc
   * @return returns true if changes have been made or else false;
   * @throws IWException
   */
  public boolean actionPerformed(IWContext iwc)throws IWException;
}

