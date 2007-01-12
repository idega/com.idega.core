package com.idega.presentation;

import com.idega.presentation.text.Text;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ErrorPage extends Page {

  private Text _errorText=new Text("Error:");

  public ErrorPage(){
    super.setBackgroundColor("#FFFFFF");
    add(getErrorText());
  }

  public void setErrorMessage(String message){
    this._errorText.setText(message);
  }

  private void setException(Exception e){
    this.setErrorMessage(e.getMessage());
    addBreak();
    add("<pre>"+this.getStackTraceAsString(e)+"</pre>");
  }

  private Text getErrorText(){
    this._errorText.setBold();
    this._errorText.setFontSize(Text.FONT_SIZE_14_HTML_4);
    return this._errorText;
  }

  private String getStackTraceAsString(Exception e){
    StringWriter writer = new StringWriter();
    PrintWriter print = new PrintWriter(writer);
    e.printStackTrace(print);
    return writer.toString();
  }

}
