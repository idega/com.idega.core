package com.idega.core.business;

import com.idega.core.data.*;
import com.idega.presentation.PresentationObject;
import java.sql.SQLException;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */


public class ICObjectBusiness {


  public static Class getICObjectClassForInstance(int ICObjectInstanceId) throws SQLException{
    ICObjectInstance instance = new ICObjectInstance(ICObjectInstanceId);
    ICObject obj = instance.getObject();
    if(obj != null){
      try {
        return obj.getObjectClass();
      }
      catch (ClassNotFoundException ex) {
        ex.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }

  public static Class getICObjectClass(int ICObjectId) throws SQLException{
    ICObject obj = new ICObject(ICObjectId);
    if(obj != null){
      try {
        return obj.getObjectClass();
      }
      catch (ClassNotFoundException ex) {
        ex.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }

  public static PresentationObject getNewObjectInstance(Class icObjectClass){
      PresentationObject inst = null;
      try{
        inst = (PresentationObject)icObjectClass.newInstance();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }

  public static PresentationObject getNewObjectInstance(String icObjectClassName){
      PresentationObject inst = null;
      try{
        inst = getNewObjectInstance(Class.forName(icObjectClassName));
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }

  public static PresentationObject getNewObjectInstance(int icObjectInstanceID){
      PresentationObject inst = null;
      try{
        ICObjectInstance ico = new ICObjectInstance(icObjectInstanceID);
        inst = ico.getNewInstance();
        inst.setICObjectInstance(ico);
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }



} // Class