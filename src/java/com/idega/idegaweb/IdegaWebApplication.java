//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import java.net.*;
import com.idega.util.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*@deprecated replaced with IWMainApplication
*Class to serve as a base center for an IdegaWeb application
*/
public class IdegaWebApplication extends IWMainApplication{//implements ServletContext{


    public IdegaWebApplication(ServletContext application){
      super(application);
    }

}
