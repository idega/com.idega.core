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
public class BackButton extends GenericButton{

private Image defaultImage;
private String howFarBackOrForward = "-1";

public BackButton(){
	this("<=");
}

public BackButton(String displayString){
	super();
	setName("");
	setValue(displayString);
	setAttribute("OnClick","history.go("+this.howFarBackOrForward+")");
}

public BackButton(Image defaultImage){
	super();
	setAttribute("OnClick","history.go("+this.howFarBackOrForward+")");
	this.defaultImage= defaultImage;
}

public void setHistoryMove(String howFarBackOrForward){

	this.howFarBackOrForward = howFarBackOrForward;

}

public void setHistoryMove(int howFarBackOrForward){

	this.howFarBackOrForward = "" + howFarBackOrForward;

}

public void print(IWContext iwc) throws IOException{
	initVariables(iwc);
        StringBuffer printString = new StringBuffer();

         if( defaultImage!= null ) {
          setAttribute("src",defaultImage.getMediaURL(iwc));
        }
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
			}
		}
		else if (getLanguage().equals("WML")){

			if (getInterfaceStyle().equals("default")){
                          printString.append("<input type=\"button\" name=\"");
                          printString.append(getName());
                          printString.append("\" ");
                          printString.append(getAttributeString());
                          printString.append(" >");
                          println(printString.toString());
                          println("</input>");
			}
		}
	//}
}

}

