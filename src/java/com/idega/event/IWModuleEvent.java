package com.idega.event;

import com.idega.jmodule.object.ModuleInfo;

/**
 * Title:        IW Event
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface IWModuleEvent {
  public void setModuleInfo(ModuleInfo modinfo);
  public ModuleInfo getModuleInfo();
}