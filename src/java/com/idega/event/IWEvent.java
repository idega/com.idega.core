package com.idega.event;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.ModuleInfo;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWEvent extends AWTEvent implements ActiveEvent, IWModuleEvent{

  public static final int IWEVENT_RESERVED_ID_MAX = AWTEvent.RESERVED_ID_MAX + 1000;

  public ModuleInfo myModinfo = null;


  public IWEvent(Object source, int id) {
    super(source, id);
/*
    switch(id)
    {
      case 1:
        consumed = true;
        break;
      default:

    }
*/
  }

  public void setModuleInfo(ModuleInfo modinfo){
    myModinfo = modinfo;
  }

  public ModuleInfo getModuleInfo(){
    return myModinfo;
  }

  public void dispatch(){
    Object obj = getSource();
    if(obj instanceof ModuleObject){
      ((ModuleObject)obj).dispatchEvent(this);
    }else{
      System.err.println("unable to dispatch event: " + this);
    }
  }

} // Class IWEvent