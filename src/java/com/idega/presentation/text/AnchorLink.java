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
 */

public class AnchorLink extends Link {
  private PresentationObject obj;
  private String anchorName;

  public AnchorLink() {
    super();
  }

  public AnchorLink(Text text, String anchorname) {
    super(text);
    this.anchorName = anchorname;
  }

  public void setAnchorName(String anchorName){
   this.anchorName = anchorName;
  }

  protected void setFinalUrl(String url) {
    if (anchorName != null) {
      super.setFinalUrl(url+"#"+anchorName);
    }else {
      super.setFinalUrl(url);
    }
  }

}
