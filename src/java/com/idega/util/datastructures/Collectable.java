package com.idega.util.datastructures;

import com.idega.jmodule.object.ModuleInfo;

/**
 * Title:        IWTabbedPane
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface Collectable {

  public boolean collect(ModuleInfo modinfo);
  public boolean store(ModuleInfo modinfo);

}