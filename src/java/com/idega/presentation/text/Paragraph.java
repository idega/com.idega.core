//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.presentation.text;



import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

public class Paragraph extends PresentationObjectContainer{



  public static final String HORIZONTAL_ALIGN_LEFT = "left";

  public static final String HORIZONTAL_ALIGN_RIGHT = "right";

  public static final String HORIZONTAL_ALIGN_CENTER = "center";

  public static final String HORIZONTAL_ALIGN_JUSTIFY = "justify";



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

	setMarkupAttribute("align",s);

}



public void setClass(String s){

	setMarkupAttribute("class",s);

}



public void setStyle(String s){

	setMarkupAttribute("style",s);

}



public void print(IWContext iwc)throws Exception{


	//if ( doPrint(iwc) ){

		if (getMarkupLanguage().equals("HTML")){

			//if (getInterfaceStyle().equals("something")){

			//}

			//else{



			println("<p "+getMarkupAttributesString()+" >");

			super.print(iwc);

			println("</p>");



			// }

		}

		else if (getMarkupLanguage().equals("WML")){

			println("<p>");

			super.print(iwc);

			println("</p>");		}

	        }



        //}





}



