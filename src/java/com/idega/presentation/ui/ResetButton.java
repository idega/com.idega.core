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

public class ResetButton extends GenericButton{



public ResetButton(){

	super("reset","Reset");

}



public ResetButton(String name){

	super("reset",name);

}



public void print(IWContext iwc) throws IOException{


	//if ( doPrint(iwc) ){

		if (getLanguage().equals("HTML")){



			if (getInterfaceStyle().equals("default")){

				println("<input type=\"reset\" name=\""+getName()+"\" "+getAttributeString()+" >");

				//println("</input>");

			}

		}

	//}

}



}



