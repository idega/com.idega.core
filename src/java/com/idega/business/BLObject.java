//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.business;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import com.idega.idegaweb.*;
import java.util.Hashtable;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5
*/
public abstract class BLObject{


    private static Hashtable controlParameters;


      public BLObject(){
      }

      public  abstract String getBundleName();

      public IWBundle getBundle(IWMainApplication application){
        return IWBundle.getBundle(getBundleName(),application);
      }

}
