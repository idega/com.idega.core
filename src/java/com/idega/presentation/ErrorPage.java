package com.idega.presentation;

import com.idega.presentation.text.Text;

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

  private Text getErrorText(){
    _errorText.setBold();
    _errorText.setFontSize(Text.FONT_SIZE_14_HTML_4);
    return _errorText;
  }
}