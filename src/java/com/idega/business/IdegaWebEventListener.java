//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.business;

import com.idega.presentation.*;
import com.idega.idegaweb.IdegaWebException;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@deprecated Replaced with IWEventListener
*@version 0.8
*Interface that defines classes that handle events in IdegaWeb
*/
public interface IdegaWebEventListener{

      public void actionPerformed(IWContext iwc)throws IdegaWebException;

}
