//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.business;

import java.util.Hashtable;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWException;
import com.idega.util.InheritableStaticPropertyHandler;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5
*/
public class BasicLogicUnit implements IWEventListener{


    private static Hashtable controlParameters;


      public BasicLogicUnit(){
        setControlParameter();
      }


      public void actionPerformed(IWContext iwc)throws IWException{

      }


      public static String getControlParameter(){
        return null;
      }


      public static void internalSetState(IWContext iwc,String state){
        iwc.setSessionAttribute(getControlParameter(),state);
      }

      public static String internalGetState(IWContext iwc){
        return (String) iwc.getSessionAttribute(getControlParameter());
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
