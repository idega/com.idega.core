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
import com.idega.jmodule.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.idegaweb.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class ObjectInstanciator extends DynamicTemplateServlet
{
      //TEMPORARY IMPLEMENTATION - See DynamicTemplateServlet
      public void main(ModuleInfo modinfo)throws Exception{
        String className = IWMainApplication.decryptClassName(modinfo.getParameter(IWMainApplication.classToInstanciateParameter));
        ModuleObject obj = (ModuleObject)Class.forName(className).newInstance();
        add(obj);
      }
}

//-------------
//- End of file
//-------------
