package com.idega.event;

import javax.swing.event.ChangeEvent;
import com.idega.jmodule.object.ModuleObject;

/**
 * Title:        idegaWeb project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author
 * @version 1.0
 */

public class TabbedPaneEvent extends ChangeEvent {

  public final static int OK = 0;
  public final static int Apply = 1;
  public final static int Cancel = 2;

  private int id;

  public TabbedPaneEvent(ModuleObject source, int id) {
    super((Object)source);
    this.id = id;
  }


  public int getID(){
    return id;
  }




}