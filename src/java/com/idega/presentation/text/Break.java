package com.idega.presentation.text;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Break extends Block {

  public Break() {
    super.add(Text.getBreak());
  }

}