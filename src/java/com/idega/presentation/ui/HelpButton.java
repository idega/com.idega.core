// idega 2000 - laddi
package com.idega.presentation.ui;

import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.presentation.*;
import com.idega.idegaweb.service.HelpWindow;
import com.idega.idegaweb.IWResourceBundle;

public class HelpButton extends Link {

private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";
public static final String PARAMETERSTRING_HEADLINE = "headline";
public static final String PARAMETERSTRING_TEXT = "text";
public static final String PARAMETERSTRING_URL = "url";
private String text = "";
private String headline = "";
private String url = "";

protected IWResourceBundle iwrb;

  public HelpButton(String text) {
    super();
    this.text=text;
  }

  public HelpButton(String headline, String text) {
    super();
    this.headline=headline;
    this.text=text;
  }

  public HelpButton(String headline, String text, String imageURL) {
    super();
    this.headline=headline;
    this.text=text;
    this.url=imageURL;
  }

  public void main(IWContext iwc) {
    iwrb = getResourceBundle(iwc);
		if(iwrb != null)
    this.setPresentationObject(iwrb.getImage("/help/help.gif",text));
    this.addParameter(PARAMETERSTRING_HEADLINE,headline);
    this.addParameter(PARAMETERSTRING_TEXT,text);
    this.addParameter(PARAMETERSTRING_URL,url);
    this.setWindowToOpen(HelpWindow.class);
  }

 // public String getBundleIdentifier(){
 //   return IW_BUNDLE_IDENTIFIER;
 // }

}