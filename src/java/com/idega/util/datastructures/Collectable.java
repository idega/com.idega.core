package com.idega.util.datastructures;

import com.idega.presentation.IWContext;

/**
 * Title:        IWTabbedPane
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public interface Collectable {

  public boolean collect(IWContext iwc);
  public boolean store(IWContext iwc);

}
