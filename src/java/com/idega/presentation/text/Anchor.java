package com.idega.presentation.text;

import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import java.io.IOException;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Hafthor Hilmarsson
 * @version 1.0
 *
 */

public class Anchor extends Link {

  public Anchor(String anchorName) {
    super("");
    setAttribute("name",anchorName);
  }

  public Anchor(Text text, String anchorName) {
    super(text);
    setAttribute("name",anchorName);
  }
}