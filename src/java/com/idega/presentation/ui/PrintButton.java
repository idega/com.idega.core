//idega 2000 - Grímur Jónsson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.presentation.ui;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;

/**
*@author <a href="mailto:gimmi@idega.is">Grímur Jónsson</a>
*@version 1.0
*/
public class PrintButton extends GenericButton{

private Image defaultImage;

public PrintButton(){
	this("Print");
}

public PrintButton(String displayString){
	super();
	setName("Print");
	setValue(displayString);
	setAttribute("OnClick","javascript:window.print();");
}

public PrintButton(Image defaultImage){
	super();
	setAttribute("OnClick","javascript:window.print();");
	this.defaultImage= defaultImage;
	setAttribute("src",defaultImage.getURL());
}


public void print(IWContext iwc) throws IOException{
	initVariables(iwc);
        StringBuffer printString = new StringBuffer();
	//if ( doPrint(iwc) ){
		if (getLanguage().equals("HTML")){
//eiki jan 2001 StringBuffer wizard
			if (getInterfaceStyle().equals("default")){
				if (defaultImage == null){
                                  printString.append("<input type=\"button\" name=\"");
                                  printString.append(getName());
                                  printString.append("\" ");
                                  printString.append(getAttributeString());
                                  printString.append(" >");
                                  println(printString.toString());
				}
				else{
                                  setAttribute("border","0");
                                  printString.append("<input type=\"image\" name=\"");
                                  printString.append(getName());
                                  printString.append("\" ");
                                  printString.append(getAttributeString());
                                  printString.append(" >");

                                  println(printString.toString());
				}
			}
		}
		else if (getLanguage().equals("WML")){
/*
			if (getInterfaceStyle().equals("default")){
                          printString.append("<input type=\"button\" name=\"");
                          printString.append(getName());
                          printString.append("\" ");
                          printString.append(getAttributeString());
                          printString.append(" >");
                          println(printString.toString());
                          println("</input>");
			}
*/
		}
	//}
}

}

