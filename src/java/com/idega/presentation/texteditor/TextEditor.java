package com.idega.presentation.texteditor;

import com.idega.presentation.ui.*;
import com.idega.util.text.TextSoap;
import com.idega.presentation.*;

public class TextEditor extends PresentationObject {
  public static final String DEFAULT_HIDDEN_TEXTEDITOR_INPUT_NAME = "myTextEditor";
  private String width = "300";
  private String height = "300";
  private String color = "white";
  private int cols = 50;
  private int rows = 15;
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
      parent.addJavascriptURL(this.getBundle(iwc).getResourcesVirtualPath()+"/texteditor/dhtmledit/dhtmleditor.js");
      parent.addJavascriptURL(this.getBundle(iwc).getResourcesVirtualPath()+"/texteditor/js/windows.js");
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

  public String getMarkupLanguage(IWContext iwc){
     if( iwc.isIE() && (!iwc.isMacOS()) && (!iwc.isOpera()) ){//IE5.5,windows and not Opera (faking as IE)
      return "HTML";
     }
     else return null;
  }

  public void print(IWContext iwc)throws Exception{

    if (getMarkupLanguage().equals("HTML")){

      if( iwc.isIE() && (!iwc.isMacOS()) && (!iwc.isOpera()) ){//IE5.5,windows and not Opera (faking as IE)

        Layer source = new Layer();
        source.setZIndex(1);
        source.setVisibility("hidden");
        source.setLeftPosition(-500);
        source.setTopPosition(-500);

        TextArea sourceView = new TextArea(inputName,TextSoap.findAndReplace(text, "<br/>","\r\n"),65,18);
        source.add(sourceView);

        String menu = "1";
        if(!menues ){
          menu = "0";
        }


        StringBuffer buf = new StringBuffer();
        buf.append("<script language=\"JavaScript1.2\">");
        buf.append("new DHTMLEdit(\"").append(inputName).append("\",").append(width).append(",").append(height)
            .append(",\"\",").append(menu).append(",\"").append(color).append("\"")
            .append(",\"").append(Window.getWindowURLWithParameter(Class.forName("com.idega.builder.presentation.IBColorChooserWindow"),iwc,"from_editor","true")).append("\"")
            .append(",\"").append(Window.getWindowURLWithParameter(Class.forName("com.idega.builder.presentation.IBPageChooserWindow"),iwc,"from_editor","true")).append("\");");

        buf.append("</script>");

        renderChild(iwc,source);
        println(buf.toString());
        /*
        var WE_DHTMLEDIT_PATH = "webEdition/dhtmledit/";

        HiddenInput editor = new HiddenInput("iw_editor","off");  Dont know what enabling this does if anything
        editor._print(iwc);
        println("");
        HiddenInput editor2 = new HiddenInput("iw_editor[Text#autobr]","off");
        editor2._print(iwc);
        //HiddenInput html = new HiddenInput("iw_editor2[Text]",""); this is the original name for the editor
        */

      }
      else{
       TextArea area = new TextArea(inputName,cols,rows);
       text = TextSoap.findAndReplace(text, "<br/>","\r\n");
       area.setContent(text);
       renderChild(iwc,area);
      }
    }
  }
/**
 * @return Returns the number rows of rows for textarea shown if client does not handle editor.
 */
public int getRows() {
	return rows;
}

/**
 * @param rows The number of rows of textarea to show if client does not handle editor.
 */
public void setRows(int rows) {
	this.rows = rows;
}

/**
 * @return Returns the number of columns for textarea to show if client does not handle editor.
 */
public int getColumns() {
	return cols;
}

/**
 * @param cols The number of  textarea columns to show if client does not handle editor .
 */
public void setColumns(int cols) {
	this.cols = cols;
}

}