package com.idega.event;

import java.util.EventListener;

/**
 * Title:        idegaWeb project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author
 * @version 1.0
 */

public interface IWActionListener extends EventListener{
  public void actionPerformed(IWActionEvent e);
}