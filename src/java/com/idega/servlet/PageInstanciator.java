//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import com.idega.jmodule.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
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
      setPage(getThisPage(getIWContext()));
    }
    catch(Exception ex){
      ex.printStackTrace(System.err);
    }
  }

  private Page getThisPage(IWContext iwc){
    String className = IWMainApplication.decryptClassName(iwc.getParameter(IWMainApplication.classToInstanciateParameter));
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
