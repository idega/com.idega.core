//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.business;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.Hashtable;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.idegaweb.IdegaWebException;
import com.idega.util.InheritableStaticPropertyHandler;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5
*/
public class BasicLogicUnit implements IdegaWebEventListener{


    private static Hashtable controlParameters;


      public BasicLogicUnit(){
        setControlParameter();
      }


      public void actionPerformed(ModuleInfo modinfo)throws IdegaWebException{

      }


      public static String getControlParameter(){
        return null;
      }


      public static void internalSetState(ModuleInfo modinfo,String state){
        modinfo.setSessionAttribute(getControlParameter(),state);
      }

      public static String internalGetState(ModuleInfo modinfo){
        return (String) modinfo.getSessionAttribute(getControlParameter());
      }


      private void setControlParameter(){
          InheritableStaticPropertyHandler.setProperty();
      }

/*
      public static Map getMap(){
        return controlParameters;
      }
*/

}
