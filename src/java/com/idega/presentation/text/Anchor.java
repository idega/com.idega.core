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

  public Anchor() {
    super("");
  }
  public Anchor(String anchorName) {
    this();
    setAnchorName(anchorName);
  }

  public Anchor(Text text, String anchorName) {
    super(text);
    setAnchorName(anchorName);
  }

  public void setAnchorName(String anchorName){
    setAttribute("name",anchorName);
  }

  public String getAnchorName(){
    return getAttribute("name");
  }

   public void main(IWContext iwc)throws Exception {
    if(iwc.isInEditMode()){
      if( getAnchorName() != null ) super.setText(getAnchorName());
    }
    super.main(iwc);

   }

}
