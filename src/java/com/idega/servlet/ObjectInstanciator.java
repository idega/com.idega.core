//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import com.idega.util.database.*;
import javax.sql.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.idegaweb.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class ObjectInstanciator extends DynamicTemplateServlet
{
      //TEMPORARY IMPLEMENTATION - See DynamicTemplateServlet
      public void main(IWContext iwc)throws Exception{
        String className = IWMainApplication.decryptClassName(iwc.getParameter(IWMainApplication.classToInstanciateParameter));
        PresentationObject obj = (PresentationObject)Class.forName(className).newInstance();
        if(obj instanceof Page){
          this.setPage((Page)obj);
        }else{
          add(obj);
        }
      }
}

//-------------
//- End of file
//-------------
