//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.presentation.text;

import java.io.*;
import java.util.*;
import com.idega.presentation.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Paragraph extends PresentationObjectContainer{


public Paragraph(){
	super();
}

public Paragraph(String align){
	super();
	setAlign(align);
}

public Paragraph(String align,String ID){
	super();
	setAlign(align);
	setID(ID);
}

public Paragraph(String align,String ID,String Class){
	super();
	setAlign(align);
	setID(ID);
	setClass(Class);
}


public Paragraph(String align,String ID,String Class,String style){
	super();
	setAlign(align);
	setID(ID);
	setClass(Class);
	setStyle(style);
}

public void setAlign(String s){
	setAttribute("align",s);
}

public void setClass(String s){
	setAttribute("class",s);
}

public void setStyle(String s){
	setAttribute("style",s);
}

public void print(IWContext iwc)throws Exception{
	initVariables(iwc);
	//if ( doPrint(iwc) ){
		if (getLanguage().equals("HTML")){
			//if (getInterfaceStyle().equals("something")){
			//}
			//else{

			println("<p "+getAttributeString()+" >");
			super.print(iwc);
			println("</p>");

			// }
		}
		else if (getLanguage().equals("WML")){
			println("<p>");
			super.print(iwc);
			println("</p>");		}
	        }

        //}


}

