package com.idega.event;

import java.awt.AWTEvent;
import com.idega.presentation.ui.SubmitButton;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWSubmitEvent extends IWEvent {

  public static final int SUBMIT_PERFORMED = AWTEvent.RESERVED_ID_MAX + 11;

  public IWSubmitEvent(SubmitButton source, int id) {
    super(source,id);
  }


}
