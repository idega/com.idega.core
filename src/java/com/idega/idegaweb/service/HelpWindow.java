// idega 2000 - laddi

package com.idega.idegaweb.service;



import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.HelpButton;
import com.idega.util.text.TextSoap;



public class HelpWindow extends IWAdminWindow {



private final static String IW_BUNDLE_IDENTIFIER="com.idega.core";

private IWResourceBundle iwrb;



public HelpWindow() {

  setWidth(300);

  setHeight(300);

}



	public void main(IWContext iwc) {

    this.iwrb = getResourceBundle(iwc);



    boolean hasImage = false;

    String headline = iwc.getParameter(HelpButton.PARAMETERSTRING_HEADLINE);

    String text = iwc.getParameter(HelpButton.PARAMETERSTRING_TEXT);

      if ( text != null && text.length() > 0 ) {

        text = TextSoap.findAndReplace(text,"\r\n\r\n","<br><br>");

      }

    String url = iwc.getParameter(HelpButton.PARAMETERSTRING_URL);

      if ( url != null ) {

        if ( url.length() > 0 ) {

          hasImage = true;

        }

      }



    try {

      super.main(iwc);

      if ( headline.length() > 0 ) {
				setTitle(headline);
			}
			else {
				setTitle("HelpWindow");
			}



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



      helpTable.add(new CloseButton(this.iwrb.getImage("close.gif")),1,3);



      add(helpTable);

    }

    catch (Exception e) {

      add("No help available");

    }

	}



  public String getBundleIdentifier(){

    return IW_BUNDLE_IDENTIFIER;

  }

}
