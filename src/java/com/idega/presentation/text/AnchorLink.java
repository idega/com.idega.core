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

  public AnchorLink() {
    super("");
  }

  public AnchorLink(String text, String anchorname) {
    this(new Text(text),anchorname);
  }

   public AnchorLink(int text, int anchorname) {
    this(Integer.toString(text),Integer.toString(anchorname));
  }

  public AnchorLink(Text text, String anchorname) {
    super(text,anchorname);
  }

  public void setURL(String url){
	setAttribute("href","#"+url);
  }

  public void print(IWContext iwc)throws Exception{
    initVariables(iwc);
    obj = super.getObject();
    if (getLanguage().equals("HTML")){
      print("<a "+getAttributeString()+" >");
      if ( obj!=null) obj.print(iwc);
      print("</a>");
    }



}
}
