//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/


package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class CloseButton extends GenericButton{

private Image defaultImage;

public CloseButton(){
	this("Close");
}

public CloseButton(String displayString){
	super();
	setName("");
	setValue(displayString);

	setAttribute("OnClick","top.window.close()");
}

public CloseButton(Image defaultImage){
	super();
	this.defaultImage = defaultImage;
	setAttribute("OnClick","top.window.close()");
}

public synchronized Object clone(){
  CloseButton obj = (CloseButton)super.clone();
  if(this.defaultImage != null){
    obj.defaultImage = (Image)this.defaultImage.clone();
  }
  return obj;
}

public void print(IWContext iwc) throws IOException{
	initVariables(iwc);
        StringBuffer printString = new StringBuffer();
         if( defaultImage!= null ) {
          setAttribute("src",defaultImage.getMediaURL(iwc));
        }
	//if ( doPrint(iwc) ){
		if (getLanguage().equals("HTML")){
			if (getInterfaceStyle().equals("default")){
				if (defaultImage == null){
                                  printString.append("<input type=\"button\" name=\"");
                                  printString.append(getName());
                                  printString.append("\" ");
                                  printString.append(getAttributeString());
                                  printString.append(" >");
                                  print(printString.toString());
				}
				else{
                                  setAttribute("border","0");
                                  printString.append("<input type=\"image\" name=\"");
                                  printString.append(getName());
                                  printString.append("\" ");
                                  printString.append(getAttributeString());
                                  printString.append(" >");

                                  print(printString.toString());
				}
/*
				if (defaultImage == null){

					println("<input type=\"button\" name=\""+getName()+"\" "+getAttributeString()+" >");
					//println("</input>");
				}
				else{
					println("<img "+getAttributeString()+" >");
					//println("</img>");
				}
*/
			}
		}
		else if (getLanguage().equals("WML")){

			if (getInterfaceStyle().equals("default")){
				println("<input type=\"button\" name=\""+getName()+"\" "+getAttributeString()+" >");
				println("</input>");
			}
		}
	//}
}

}

