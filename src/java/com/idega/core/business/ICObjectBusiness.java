package com.idega.core.business;

import com.idega.core.data.*;
import com.idega.presentation.PresentationObject;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWBundle;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */


public class ICObjectBusiness {


  private static ICObjectBusiness instance;

  private  Map icoInstanceMap;
  private  Map icObjectMap;

  private ICObjectBusiness(){
  }

  private  Map getIcoInstanceMap(){
    if(icoInstanceMap==null){
      icoInstanceMap = new HashMap();
    }
    return icoInstanceMap;
  }

  private  Map getIcObjectMap(){
    if(icObjectMap==null){
      icObjectMap = new HashMap();
    }
    return icObjectMap;
  }


  public static ICObjectBusiness getInstance(){
    if(instance==null){
      instance = new ICObjectBusiness();
    }
    return instance;
  }


  public Class getICObjectClassForInstance(int ICObjectInstanceId) {
    ICObjectInstance instance = this.getICObjectInstance(ICObjectInstanceId);
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

  public Class getICObjectClass(int ICObjectId) {
    ICObject obj = this.getICObject(ICObjectId);
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

  public PresentationObject getNewObjectInstance(Class icObjectClass){
      PresentationObject inst = null;
      try{
        inst = (PresentationObject)icObjectClass.newInstance();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }

  public PresentationObject getNewObjectInstance(String icObjectClassName){
      PresentationObject inst = null;
      try{
        inst = getNewObjectInstance(Class.forName(icObjectClassName));
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }

  public  PresentationObject getNewObjectInstance(int icObjectInstanceID){
      PresentationObject inst = null;
      try{
        ICObjectInstance ico = this.getICObjectInstance(icObjectInstanceID);
        inst = ico.getNewInstance();
        inst.setICObjectInstance(ico);
      }
      catch(Exception e){
        e.printStackTrace();
      }
      return inst;
  }


  public  IWBundle getBundleForInstance(String icObjectInstanceID,IWMainApplication iwma){
    return getBundleForInstance(Integer.parseInt(icObjectInstanceID),iwma);
  }

  public  IWBundle getBundleForInstance(int icObjectInstanceID,IWMainApplication iwma){
    try{
      if(icObjectInstanceID==-1){
        return iwma.getBundle(com.idega.presentation.Page.IW_BUNDLE_IDENTIFIER);
      }
      else{
        ICObjectInstance instance = getICObjectInstance(icObjectInstanceID);
        return instance.getObject().getBundle(iwma);
      }
    }
    catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }


  public Class getClassForInstance(String icObjectInstanceID)throws ClassNotFoundException{
    return getClassForInstance(Integer.parseInt(icObjectInstanceID));
  }


  public Class getClassForInstance(int icObjectInstanceID)throws ClassNotFoundException{
    if (icObjectInstanceID == -1)
      return(com.idega.presentation.Page.class);
    else
      return getICObjectInstance(icObjectInstanceID).getObject().getObjectClass();
  }


  public ICObjectInstance getICObjectInstance(String icObjectInstanceID) {
    return getICObjectInstance(Integer.parseInt(icObjectInstanceID));
  }


  public ICObject getICObject(int icObjectID){
    try{
      Integer key = new Integer(icObjectID);
      ICObject theReturn = (ICObject)getIcObjectMap().get(key);
      if(theReturn == null){
        theReturn =  new ICObject(icObjectID);
        getIcObjectMap().put(key,theReturn);
      }
      return theReturn;
    }
    catch(Exception e){
      throw new RuntimeException("Error getting ICObject for id="+icObjectID+" - message: "+e.getMessage());
    }
  }


  public  ICObjectInstance getICObjectInstance(int icObjectInstanceID){
    try{
      Integer key = new Integer(icObjectInstanceID);
      ICObjectInstance theReturn = (ICObjectInstance)getIcoInstanceMap().get(key);
      if(theReturn == null){
        theReturn =  new ICObjectInstance(icObjectInstanceID);
        getIcoInstanceMap().put(key,theReturn);
      }
      return theReturn;
    }
    catch(Exception e){
      throw new RuntimeException("Error getting ICObjectInstance for id="+icObjectInstanceID+" - message: "+e.getMessage());
    }
  }


} // Class