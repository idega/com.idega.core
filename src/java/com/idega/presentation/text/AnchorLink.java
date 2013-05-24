package com.idega.presentation.text;

import com.idega.presentation.PresentationObject;
import com.idega.util.CoreConstants;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Hafthor Hilmarsson
 * @version 1.0
 */

public class AnchorLink extends Link {
  private String anchorName;
  private boolean addCurrentURLToLink = false;

  public AnchorLink() {
    super();
  }

  public AnchorLink(Text text, String anchorname) {
    super(text);
    this.anchorName = anchorname;
  }

  public AnchorLink(PresentationObject object, String anchorname) {
    super(object);
    this.anchorName = anchorname;
  }

  public void addCurrentURLToLink(boolean addCurrentURLToLink) {
  	this.addCurrentURLToLink = addCurrentURLToLink;
  }

  public void setAnchorName(String anchorName){
   this.anchorName = anchorName;
  }

  @Override
protected void setFinalUrl(String url) {
    if (this.anchorName != null) {
    	if (this.addCurrentURLToLink) {
    		super.setFinalUrl(url+CoreConstants.HASH+this.anchorName);
    	} else {
        super.setFinalUrl(CoreConstants.HASH+this.anchorName);
    	}
    }else {
      super.setFinalUrl(url);
    }
  }

}
