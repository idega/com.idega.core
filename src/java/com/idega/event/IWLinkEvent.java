package com.idega.event;

import java.awt.AWTEvent;
import com.idega.presentation.text.Link;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWLinkEvent extends IWEvent {

  public final static int LINK_ACTION_PERFORMED = AWTEvent.RESERVED_ID_MAX +1;

  public IWLinkEvent(Link source, int id) {
    super(source,id);
  }

}
