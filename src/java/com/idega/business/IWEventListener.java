//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.business;

import com.idega.jmodule.object.*;
import com.idega.idegaweb.IWException;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.8
*Interface that defines classes that handle events in IdegaWeb
*/
public interface IWEventListener{

      public void actionPerformed(ModuleInfo modinfo)throws IWException;

}
