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

public class CheckBox extends InterfaceObject{



private String checkedString;



public CheckBox(){

	this("untitled");

}



public CheckBox(String name){

	this(name,"unspecified");

}



public CheckBox(String name,String value){

	super();

	setName(name);

	setContent(value);

	setChecked(false);

}



public void setChecked(boolean ifChecked){

	if (ifChecked){

		checkedString="checked";

	}

	else{

		checkedString="";

	}

}



public String getCheckedString(){

	return this.checkedString;

}



public void handleKeepStatus(IWContext iwc){

	if (statusKeptOnAction()){

		if(iwc.getParameter(this.getName()) != null){

			if(iwc.getParameter(this.getName()).equals(this.getValue())){

				setChecked(true);

			}

		}

	}

}





public void print(IWContext iwc)throws IOException{

	initVariables(iwc);

	handleKeepStatus(iwc);

	//if ( doPrint(iwc) ){

		if (getLanguage().equals("HTML")){

			//if (getInterfaceStyle().equals("something")){

			//

			//}

			//else{

				println("<input type=\"checkbox\" name=\""+getName()+"\" "+getCheckedString()+" "+getAttributeString()+" >");

				//println("</input>");

			//}

		}

	//}



}



  public synchronized Object clone() {

    CheckBox obj = null;

    try {

      obj = (CheckBox)super.clone();

      obj.checkedString = this.checkedString;

    }

    catch(Exception ex) {

      ex.printStackTrace(System.err);

    }

    return obj;

  }



  public void setStyle(String style) {

    setAttribute("class",style);

  }



}



