package com.idega.presentation.texteditor;

import com.idega.util.text.TextSoap;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.*;
import com.idega.presentation.ui.TextArea;

public class TextEditor extends PresentationObject {
  public static final String DEFAULT_HIDDEN_TEXTEDITOR_INPUT_NAME = "myTextEditor";
  private String width = "300";
  private String height = "300";
  private String color = "white";
  private boolean menues = true;
  private String text = "";
  private String inputName = DEFAULT_HIDDEN_TEXTEDITOR_INPUT_NAME;

  public TextEditor() { }

  public TextEditor(String text) {
    this();
    this.text = text;
  }

  public TextEditor(String inputName, String text) {
    this(text);
    this.inputName = inputName;
  }

  public TextEditor(String text,String width, String height) {
    this(text);
    this.width = width;
    this.height = height;
  }

  public TextEditor(String inputName, String text, String width, String height) {
    this(text,width,height);
    this.inputName = inputName;
  }

  public void main(IWContext iwc) {
    if( iwc.isIE() && (!iwc.isMacOS()) ){
      Page parent = getParentPage();
      parent.addScriptSource(this.getBundle(iwc).getResourcesVirtualPath()+"/texteditor/dhtmledit/dhtmleditor.js");
      parent.addScriptSource(this.getBundle(iwc).getResourcesVirtualPath()+"/texteditor/js/windows.js");
      //parent.addScriptSource("webEdition/dhtmledit/dhtmleditor.js");
      //parent.addScriptSource("webEdition/js/windows.js");
    }
  }


  public void setHeight(String height) {
    this.height = height;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public void setBackgroundColor(String color) {
    this.color = color;
  }

  public void setShowMenues(boolean menues) {
    this.menues = menues;
  }

  public void setInputName(String name) {
    this.inputName = name;
  }

  public void setContent(String text) {
    this.text = text;
  }

  public void print(IWContext iwc)throws Exception{
    initVariables(iwc);

    if (getLanguage().equals("HTML")){

      if( iwc.isIE() && (!iwc.isMacOS()) ){//and is windows

        /*
        var WE_DHTMLEDIT_PATH = "webEdition/dhtmledit/";

        HiddenInput editor = new HiddenInput("iw_editor","off");  Dont know what enabling this does
        editor.print(iwc);
        println("");
        HiddenInput editor2 = new HiddenInput("iw_editor[Text#autobr]","off");
        editor2.print(iwc);
        //HiddenInput html = new HiddenInput("iw_editor2[Text]",""); this is the original name for the hidden html field
        */


        Layer source = new Layer();
        source.setZIndex(1);
        source.setVisibility("hidden");
        source.setLeftPosition(-500);
        source.setTopPosition(-500);

        TextArea sourceView = new TextArea(inputName,text,65,18);
        source.add(sourceView);

        String menu = "1";
        if(!menues ){
          menu = "0";
        }


        StringBuffer buf = new StringBuffer();
        buf.append("<script language=\"JavaScript1.2\">");
        buf.append("new DHTMLEdit(\"").append(inputName).append("\",").append(width).append(",").append(height)
            .append(",\"\",").append(menu).append(",\"").append(color).append("\");");
        buf.append("</script>");

        source.print(iwc);
        println(buf.toString());

      }
      else{
       TextArea area = new TextArea(inputName,65,18);
       area.setContent(text);
       area.print(iwc);
      }
    }
  }
}