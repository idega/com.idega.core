package com.idega.presentation.event;

import com.idega.presentation.IWContext;
import com.idega.event.*;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class ResetPresentationEvent extends IWPresentationEvent {

  private static final String PRM_DO_RESET = "ic_rspt";
  private boolean _doReset = false;

  public ResetPresentationEvent() {
    this.addParameter(PRM_DO_RESET,"1");
  }

  public boolean doReset(){
    return this._doReset;
  }

  public boolean initializeEvent(IWContext iwc) {
    this._doReset = ("1".equals(iwc.getParameter(PRM_DO_RESET)));

    if(this._doReset){
      return true;
    }else {
      return false;
    }

  }
}