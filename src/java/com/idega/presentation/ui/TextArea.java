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

public class TextArea extends InterfaceObject{





protected String content;

protected boolean editable;

protected boolean keepContent;

protected boolean wrap = true;





public TextArea(){

	this("untitled");

}



public TextArea(String name){

	this(name,"");

}



public TextArea(String name,String content){

	super();

	setName(name);

	this.content=content;

	editable=true;

	keepContent = false;

}



public TextArea(String name,int width,int height){

	this(name,"");

	setWidth(width);

	setHeight(height);

}



public TextArea(String name,String content,int width,int height){

	this(name,content);

	setWidth(width);

	setHeight(height);

}





public void setWidth(int width){

	setAttribute("cols",Integer.toString(width));

}



public void setHeight(int height){

	setAttribute("rows",Integer.toString(height));

}





public void setAsEditable(){

	editable=true;

}



public void setAsNotEditable(){

	editable=false;

}



public void setValue(String value){

  setContent(value);

}



public void setValue(int value){

  setValue(Integer.toString(value));

}



public void setContent(String s){

	content=s;

}



public String getValue(){

  return getContent();

}



public void setWrap(boolean wrapping){

  this.wrap=wrapping;

}



//Enables the possibility of maintaining the content of the area between requests.

public void keepContent(){

	keepContent = true;

}



public void keepStatusOnAction(){

	keepContent();

}



public String getContent(){

	if (getRequest().getParameter(getName()) == null){

		return content;

	}

	else{

		if (keepContent == true ){

			return getRequest().getParameter(getName());

		}

		else{

			return content;

		}

	}

}



public void print(IWContext iwc)throws IOException{


	//if ( doPrint(iwc) ){

		if (getLanguage().equals("HTML")){

			String EditableString = "";

			if (!editable){EditableString = "READONLY";}

                       //eiki,idega iceland

                        if ( !wrap ) { EditableString += " wrap=\"OFF\"";}



			//if (getInterfaceStyle().equals("default"))

				print("<textarea name=\""+getName()+"\""+getAttributeString()+EditableString+" >");

				print(getContent() );

				print("</textarea>");

			// }

		}

		else if (getLanguage().equals("WML")){

			if ( content != null){

				setValue(content);

			}

			print("<input type=\"text\" name=\""+getName()+"\" value=\""+getContent()+"\" >");

			print("</input>");

		}

	//}

}



  public void setStyle(String style) {

    setAttribute("class",style);

  }



}



