//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import com.idega.jmodule.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.idegaweb.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class PageInstanciator extends JSPModule
{

  public void initializePage(){
    try{
      //String servletName = this.getServletConfig().getServletName();
      //System.out.println("Inside initializePage for "+servletName);
      setPage(getThisPage(getModuleInfo()));
    }
    catch(Exception ex){
      ex.printStackTrace(System.err);
    }
  }

  private Page getThisPage(ModuleInfo modinfo){
    String className = IWMainApplication.decryptClassName(modinfo.getParameter(IWMainApplication.classToInstanciateParameter));
    try{
      return (Page)Class.forName(className).newInstance();
    }
    catch(Exception ex){
      ex.printStackTrace(System.err);
      return null;
    }
  }


}

//-------------
//- End of file
//-------------
