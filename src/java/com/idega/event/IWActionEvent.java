package com.idega.event;

import com.idega.util.IWEventObject;
import java.awt.event.ActionEvent;

/**
 * Title:        idegaWeb project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author
 * @version 1.0
 */

public class IWActionEvent extends ActionEvent {

  public final static int LINK_EVENT = ActionEvent.RESERVED_ID_MAX + 1;
  public final static int FORM_EVENT = LINK_EVENT + 1;

  public IWActionEvent(Object source) {
    super(source, ActionEvent.ACTION_PERFORMED,"OK");
  }

  public IWActionEvent(Object source, int id) {
    super(source, id,"OK");
  }


} // Class IWActionEvent