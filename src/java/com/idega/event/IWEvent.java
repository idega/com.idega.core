package com.idega.event;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class IWEvent extends AWTEvent implements ActiveEvent, IWModuleEvent{

  public static final int IWEVENT_RESERVED_ID_MAX = AWTEvent.RESERVED_ID_MAX + 1000;

	/**
	 * 
	 * @uml.property name="myModinfo"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	public IWContext myModinfo = null;

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

  public void setIWContext(IWContext iwc){
    this.myModinfo = iwc;
  }

  public IWContext getIWContext(){
    return this.myModinfo;
  }

  public void dispatch(){
    Object obj = getSource();
    if(obj instanceof PresentationObject){
      ((PresentationObject)obj).dispatchEvent(this);
    }else{
      System.err.println("unable to dispatch event: " + this);
    }
  }

} // Class IWEvent
