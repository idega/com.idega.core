// idega 2000 - laddi
package com.idega.idegaweb.service;

import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.*;
import com.idega.idegaweb.presentation.IWAdminWindow;

public class HelpWindow extends IWAdminWindow {

public HelpWindow() {
  setWidth(300);
  setHeight(300);
  setMerged();
}

	public void main(ModuleInfo modinfo) {
    boolean hasImage = false;
    String headline = modinfo.getParameter(HelpButton.PARAMETERSTRING_HEADLINE);
    String text = modinfo.getParameter(HelpButton.PARAMETERSTRING_TEXT);
    String url = modinfo.getParameter(HelpButton.PARAMETERSTRING_URL);
      if ( url.length() > 0 ) {
        hasImage = true;
      }

    try {
      super.main(modinfo);
      if ( headline.length() > 0 )
        setTitle(headline);
      else
        setTitle("HelpWindow");

      Table helpTable = new Table(1,3);
        helpTable.setCellpadding(8);
        helpTable.setWidth("100%");
        helpTable.setHeight("100%");
        helpTable.setHeight(3,"100%");
        helpTable.setVerticalAlignment(1,3,"bottom");
        helpTable.setAlignment(1,3,"right");

      Image image = new Image();
        if ( hasImage ) {
          image = new Image(url);
        }
        image.setAlignment("right");
        image.setHorizontalSpacing(4);

      if ( headline.length() > 0 ) {
        helpTable.add(formatHeadline(headline),1,1);
        if ( hasImage ) {
          helpTable.add(image,1,2);
        }
        helpTable.add(formatText(text,false),1,2);
      }
      else {
        if ( hasImage ) {
          helpTable.add(image,1,1);
        }
        helpTable.add(formatText(text,false),1,1);
      }

      helpTable.add(new CloseButton(iwbCore.getImage("close")),1,3);

      addBottom(helpTable);
    }
    catch (Exception e) {
      add("No help available");
    }
	}

}